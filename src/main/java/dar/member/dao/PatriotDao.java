package dar.member.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dar.member.entity.Patriot;

public interface PatriotDao extends JpaRepository<Patriot, Long> {
    
    Optional<Patriot> findByPatriotFirstNameAndPatriotLastNameAndPatriotRankServiceAndPatriotState(
        String patriotFirstName,
        String patriotLastName,
        String patriotRankService,
        String patriotState
    );
    
    @Query("""
    	    SELECT p FROM Patriot p
    	    WHERE LOWER(p.patriotFirstName) = LOWER(:firstName)
    	      AND LOWER(p.patriotLastName) = LOWER(:lastName)
    	      AND LOWER(p.patriotRankService) = LOWER(:rank)
    	      AND LOWER(p.patriotState) = LOWER(:state)
    	""")
    	Optional<Patriot> findMatchingPatriotIgnoreCase(
    	    @Param("firstName") String firstName,
    	    @Param("lastName") String lastName,
    	    @Param("rank") String rank,
    	    @Param("state") String state
    	);
    
    @Query("""
    	    SELECT patriot FROM Patriot patriot
    	    WHERE patriot.member IS EMPTY
    	""")
    	List<Patriot> findAllUnassignedPatriots();
}
