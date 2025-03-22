package dar.member.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dar.member.entity.Patriot;

/**
 * Repository interface for accessing and managing Patriot entities. Extends
 * JpaRepository to provide basic CRUD operations.
 */
public interface PatriotDao extends JpaRepository<Patriot, Long> {

	/**
	 * Finds a single patriot by an exact match on all four identifying fields.
	 * Case-sensitive query using Spring Data JPA naming convention.
	 *
	 * @param patriotFirstName   The patriot's first name.
	 * @param patriotLastName    The patriot's last name.
	 * @param patriotRankService The patriot's rank or service.
	 * @param patriotState       The patriot's state.
	 * @return An Optional containing the matched patriot, or empty if not found.
	 */
	Optional<Patriot> findByPatriotFirstNameAndPatriotLastNameAndPatriotRankServiceAndPatriotState(
			String patriotFirstName, String patriotLastName, String patriotRankService, String patriotState);

	/**
	 * Finds all patriots matching the specified fields, ignoring case sensitivity.
	 * Useful for matching entries that may differ in capitalization.
	 *
	 * @param firstName The patriot's first name.
	 * @param lastName  The patriot's last name.
	 * @param rank      The patriot's rank or service.
	 * @param state     The patriot's state.
	 * @return A list of matching patriots (could contain multiple).
	 */
	@Query("""
			    SELECT p FROM Patriot p
			    WHERE LOWER(p.patriotFirstName) = LOWER(:firstName)
			      AND LOWER(p.patriotLastName) = LOWER(:lastName)
			      AND LOWER(p.patriotRankService) = LOWER(:rank)
			      AND LOWER(p.patriotState) = LOWER(:state)
			""")
	List<Patriot> findMatchingPatriotIgnoreCase(@Param("firstName") String firstName,
			@Param("lastName") String lastName, @Param("rank") String rank, @Param("state") String state);

	/**
	 * Retrieves all patriots that are not assigned to any member. A patriot is
	 * considered unassigned if its `member` collection is empty.
	 *
	 * @return A list of unassigned patriots.
	 */
	@Query("""
			    SELECT patriot FROM Patriot patriot
			    WHERE patriot.member IS EMPTY
			""")
	List<Patriot> findAllUnassignedPatriots();
}
