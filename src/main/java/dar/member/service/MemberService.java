package dar.member.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dar.member.controller.model.ChapterAssignmentResult;
import dar.member.controller.model.MemberData;
import dar.member.controller.model.MemberData.MemberChapter;
import dar.member.controller.model.MemberData.MemberPatriot;
import dar.member.controller.model.PatriotAssignmentResult;
import dar.member.dao.ChapterDao;
import dar.member.dao.MemberDao;
import dar.member.dao.PatriotDao;
import dar.member.entity.Chapter;
import dar.member.entity.Member;
import dar.member.entity.Patriot;

/**
 * Service layer for managing member operations. Provides methods for creating,
 * updating, retrieving, and deleting members.
 */
@Service // Marks this class as a Spring service component
public class MemberService {

	@Autowired // Injects the MemberDao dependency
	private MemberDao memberDao;

	@Autowired // Injects the MemberDao dependency
	private ChapterDao chapterDao;

	@Autowired // Injects the MemberDao dependency
	private PatriotDao patriotDao;

	/**
	 * Copies relevant fields from a DTO to a Member entity.
	 * 
	 * @param member     The entity to update.
	 * @param memberData The DTO containing updated field values.
	 */
	private void copyMemberFields(Member member, MemberData memberData) {
		member.setMemberId(memberData.getMemberId());
		member.setMemberFirstName(memberData.getMemberFirstName());
		member.setMemberLastName(memberData.getMemberLastName());
		member.setMemberAddress(memberData.getMemberAddress());
		member.setMemberCity(memberData.getMemberCity());
		member.setMemberState(memberData.getMemberState());
		member.setMemberPhone(memberData.getMemberPhone());
		member.setMemberZip(memberData.getMemberZip());
		member.setMemberEmail(memberData.getMemberEmail());
		member.setMemberBirthday(memberData.getMemberBirthday());
		member.setMemberJoinDate(memberData.getMemberJoinDate());
		member.setMemberOffice(memberData.getMemberOffice());
	}

	private void copyChapterFields(Chapter chapter, MemberChapter memberChapter) {
		chapter.setChapterId(memberChapter.getChapterId());
		chapter.setChapterName(memberChapter.getChapterName());
		chapter.setChapterNumber(memberChapter.getChapterNumber());
	}

	/**
	 * Updates a chapter that is associated with a specific member.
	 * 
	 * @param memberId
	 * @param chapterId
	 * @param memberChapter
	 * @return
	 */
	@Transactional
	public MemberChapter updateChapter(Long memberId, Long chapterId, MemberChapter memberChapter) {
		Chapter chapter = findChapterById(memberId, chapterId); // Ensures the chapter exists

		// Update fields
		chapter.setChapterName(memberChapter.getChapterName());
		chapter.setChapterNumber(memberChapter.getChapterNumber());

		Chapter updatedChapter = chapterDao.save(chapter);
		return new MemberChapter(updatedChapter);
	}

	/**
	 * Copies data from a MemberPatriot DTO into a Patriot entity. Used when
	 * creating or updating a Patriot from client input.
	 * 
	 * @param patriot
	 * @param memberPatriot
	 */
	@Transactional
	private void copyPatriotFields(Patriot patriot, MemberPatriot memberPatriot) {
		patriot.setPatriotId(memberPatriot.getPatriotId());
		patriot.setPatriotFirstName(memberPatriot.getPatriotFirstName()); // Stores the patriot's first name
		patriot.setPatriotLastName(memberPatriot.getPatriotLastName()); // Stores the patriot's last name
		patriot.setPatriotState(memberPatriot.getPatriotState()); // Stores the patriot's email address
		patriot.setPatriotRankService(memberPatriot.getPatriotRankService()); // Stores the patriot's email address
	}

	/**
	 * Updates a chapter that is not currently assigned to any member.
	 *
	 * @param chapterId     The ID of the unassigned chapter to update.
	 * @param memberChapter The updated chapter data from the request.
	 * @return The updated chapter wrapped in a MemberChapter DTO.
	 */
	@Transactional
	public MemberChapter updateUnassignedChapter(Long chapterId, MemberChapter memberChapter) {
		if (memberChapter == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter details cannot be null.");
		}

		// Retrieve the existing chapter by ID or throw 404 if not found
		Chapter chapter = chapterDao.findById(chapterId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Chapter with ID=" + chapterId + " not found."));

		// Validate required fields
		if (memberChapter.getChapterName() == null || memberChapter.getChapterName().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter first name cannot be empty.");
		}

		if (memberChapter.getChapterNumber() == null || memberChapter.getChapterNumber().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter last name cannot be empty.");
		}

		// Apply updates
		chapter.setChapterName(memberChapter.getChapterName());
		chapter.setChapterNumber(memberChapter.getChapterNumber());

		// Save and return the updated chapter
		Chapter updatedChapter = chapterDao.save(chapter);
		return new MemberChapter(updatedChapter);
	}

	/**
	 * Updates a patriot who is not currently assigned to any member.
	 *
	 * @param patriotId     The ID of the unassigned patriot to update.
	 * @param memberPatriot The updated patriot data from the request.
	 * @return The updated patriot wrapped in a MemberPatriot DTO.
	 */
	@Transactional
	public MemberPatriot updateUnassignedPatriot(Long patriotId, MemberPatriot memberPatriot) {
		if (memberPatriot == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Patriot data cannot be null.");
		}

		Patriot patriot = patriotDao.findById(patriotId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Patriot with ID=" + patriotId + " not found."));

		// Update fields
		patriot.setPatriotFirstName(memberPatriot.getPatriotFirstName());
		patriot.setPatriotLastName(memberPatriot.getPatriotLastName());
		patriot.setPatriotState(memberPatriot.getPatriotState());
		patriot.setPatriotRankService(memberPatriot.getPatriotRankService());

		Patriot updated = patriotDao.save(patriot);
		return new MemberPatriot(updated);
	}

	/**
	 * Updates a patriot who is assigned to a specific member.
	 *
	 * @param memberId      The ID of the member who owns the patriot.
	 * @param patriotId     The ID of the patriot to update.
	 * @param memberPatriot The updated patriot data from the request.
	 * @return The updated patriot wrapped in a MemberPatriot DTO.
	 */
	@Transactional
	public MemberPatriot updatePatriot(Long memberId, Long patriotId, MemberPatriot memberPatriot) {
		Patriot patriot = findPatriotById(memberId, patriotId); // Ensure the patriot exists

		// Update fields
		patriot.setPatriotFirstName(memberPatriot.getPatriotFirstName());
		patriot.setPatriotLastName(memberPatriot.getPatriotLastName());
		patriot.setPatriotState(memberPatriot.getPatriotState());
		patriot.setPatriotRankService(memberPatriot.getPatriotRankService());

		Patriot updatedPatriot = patriotDao.save(patriot);
		return new MemberPatriot(updatedPatriot);
	}

	/**
	 * Finds an existing member by ID or creates a new instance if ID is null.
	 * 
	 * @param memberId The ID of the member.
	 * @return An existing or new Member entity.
	 */
	private Member findOrCreateMember(Long memberId) {
		if (Objects.isNull(memberId)) {
			return new Member(); // Creates a new Member if ID is null
		} else {
			return findMemberById(memberId); // Retrieves existing Member by ID
		}
	}

	/**
	 * Finds an existing chapter by ID if present; otherwise, creates a new Chapter
	 * instance. Used during chapter assignment to determine whether to reuse or
	 * create a chapter.
	 *
	 * @param memberId  The ID of the member (used for validation).
	 * @param chapterId The ID of the chapter to find.
	 * @return An existing Chapter entity or a new blank Chapter instance.
	 */
	private Chapter findOrCreateChapter(Long memberId, Long chapterId) {
		if (Objects.isNull(chapterId)) {
			return new Chapter();
		}
		return findChapterById(memberId, chapterId);
	}

	/**
	 * Finds an existing member by ID if present.
	 * 
	 * @param memberId
	 * @return
	 */
	private Member findMemberById(Long memberId) {
		return memberDao.findById(memberId)
				.orElseThrow(() -> new NoSuchElementException("Member with ID=" + memberId + " was not found."));
	}

	/**
	 * Finds chapter for a member by chapterID
	 * 
	 * @param memberId
	 * @param chapterId
	 * @return
	 */
	private Chapter findChapterById(Long memberId, Long chapterId) {
		Chapter chapter = chapterDao.findById(chapterId)
				.orElseThrow(() -> new NoSuchElementException("Chapter with ID=" + chapterId + " was not found."));

		boolean found = chapter.getMembers().stream().anyMatch(member -> member.getMemberId().equals(memberId));

		if (!found) {
			throw new IllegalArgumentException(
					"The chapter with ID=" + chapterId + " is not associated with member ID=" + memberId + ".");
		}

		return chapter;
	}

	/**
	 * Finds patriot by ID. Throws message for patriot not found or is not assigned
	 * to that member.
	 * 
	 * @param memberId
	 * @param patriotId
	 * @return
	 */
	private Patriot findPatriotById(Long memberId, Long patriotId) {
		Patriot patriot = patriotDao.findById(patriotId)
				.orElseThrow(() -> new NoSuchElementException("Patriot with ID=" + patriotId + " was not found."));

		boolean found = false;

		for (Member member : patriot.getMember()) {
			if (member.getMemberId().equals(memberId)) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new IllegalArgumentException(
					"The patriot with ID=" + patriotId + " is not a patriot of the member with ID=" + memberId);
		}

		return patriot;
	}

	/**
	 * Retrieves all members as a list of DTOs.
	 * 
	 * @return A list of all member data.
	 */
	@Transactional(readOnly = true) // Optimized for read-only transactions
	public List<MemberData> retrieveAllmember() {
		List<Member> member = memberDao.findAll();
		List<MemberData> response = new ArrayList<>();

		for (Member m : member) {
			response.add(new MemberData(m)); // Converts each entity to a DTO
		}
		return response;
	}

	/**
	 * Retrieves a member by its ID as a DTO.
	 * 
	 * @param memberId The ID of the member to retrieve.
	 * @return The corresponding member data.
	 */
	@Transactional(readOnly = true)
	public MemberData retrieveMemberById(Long memberId) {
		Member member = findMemberById(memberId);
		return new MemberData(member);
	}

	/**
	 * Retrieves a chapter by its ID as a DTO.
	 * 
	 * @param memberId
	 * @param chapterId
	 * @return
	 */
	@Transactional(readOnly = true)
	public MemberChapter retrieveChapterById(Long memberId, Long chapterId) {
		Chapter chapter = findChapterById(memberId, chapterId);
		return new MemberChapter(chapter);
	}

	/**
	 * Saves a new or updated chapter.
	 * 
	 * @param memberId
	 * @param memberChapter
	 * @return
	 */
	@Transactional
	public ChapterAssignmentResult saveChapter(Long memberId, MemberChapter memberChapter) {
		Member member = findMemberById(memberId);

		if (member.getChapter() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This member is already assigned to a chapter.");
		}

		List<Chapter> matches = chapterDao.findByChapterNameIgnoreCaseAndChapterNumberIgnoreCase(
				memberChapter.getChapterName(), memberChapter.getChapterNumber());

		Chapter chapter;
		String message;

		if (!matches.isEmpty()) {
			chapter = matches.get(0);
			message = "Existing chapter assigned to member.";
		} else {
			chapter = new Chapter();
			copyChapterFields(chapter, memberChapter);
			chapter = chapterDao.save(chapter);
			message = "New chapter created and assigned to member.";
		}

		member.setChapter(chapter);
		memberDao.save(member);

		return new ChapterAssignmentResult(message, new MemberChapter(chapter));
	}

	/**
	 * Saves a new or updated member.
	 * 
	 * @param memberData
	 * @return
	 */
	@Transactional(readOnly = false) // Allows write operations within a transaction
	public MemberData saveMember(MemberData memberData) {
		if (memberData == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "member data cannot be null.");
		}

		// Validate required fields
		if (memberData.getMemberFirstName() == null || memberData.getMemberFirstName().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "member first name cannot be empty.");
		}

		if (memberData.getMemberLastName() == null || memberData.getMemberLastName().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "member last name cannot be empty.");
		}

		if (memberData.getMemberPhone() == null || memberData.getMemberPhone().trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "member phone number cannot be empty.");
		}

		// Create or update member
		Long memberId = memberData.getMemberId();
		Member member = findOrCreateMember(memberId);

		// Copy fields from DTO to entity
		copyMemberFields(member, memberData);

		// Save member in database
		Member savedMember = memberDao.save(member);
		return new MemberData(savedMember);
	}

	/**
	 * Saves a new or updated patriot.
	 * 
	 * @param memberId
	 * @param memberPatriot
	 * @return
	 */
	@Transactional
	public PatriotAssignmentResult savePatriot(Long memberId, MemberPatriot memberPatriot) {
		Member member = findMemberById(memberId);

		List<Patriot> matches = patriotDao.findMatchingPatriotIgnoreCase(memberPatriot.getPatriotFirstName(),
				memberPatriot.getPatriotLastName(), memberPatriot.getPatriotRankService(),
				memberPatriot.getPatriotState());

		Patriot patriot;
		String message;

		if (!matches.isEmpty()) {
			patriot = matches.get(0); // Just pick the first match (or add smarter logic)

			if (!member.getPatriot().contains(patriot)) {
				member.getPatriot().add(patriot);
				patriot.getMember().add(member);
				memberDao.save(member);
			}

			message = "Existing patriot assigned";
		} else {
			// Create new patriot
			patriot = new Patriot();
			copyPatriotFields(patriot, memberPatriot);

			patriot.getMember().add(member);
			member.getPatriot().add(patriot);

			patriotDao.save(patriot);

			message = "New patriot created";
		}

		return new PatriotAssignmentResult(message, new MemberPatriot(patriot));
	}

	/**
	 * Get patriot by ID
	 * 
	 * @param memberId
	 * @param patriotId
	 * @return
	 */
	@Transactional(readOnly = true)
	public MemberPatriot getPatriotById(Long memberId, Long patriotId) {
		Patriot patriot = findPatriotById(memberId, patriotId); // Ensure the patriot exists
		return new MemberPatriot(patriot);
	}

	/**
	 * Get patriots for a member
	 * 
	 * @param memberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<MemberPatriot> getPatriotsByMemberId(Long memberId) {
		Member member = findMemberById(memberId); // ensures member exists

		List<MemberPatriot> patriotList = new ArrayList<>();
		for (Patriot patriot : member.getPatriot()) {
			patriotList.add(new MemberPatriot(patriot));
		}

		return patriotList;
	}

	/**
	 * Create a patriot without assigning it to a member.
	 * 
	 * @param dto
	 * @return
	 */
	@Transactional
	public MemberPatriot createUnassignedPatriot(MemberPatriot dto) {
		Patriot patriot = new Patriot();
		copyPatriotFields(patriot, dto);
		patriotDao.save(patriot);
		return new MemberPatriot(patriot);
	}

	/**
	 * Get all patriots assigned or not.
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<MemberPatriot> getAllPatriots() {
		List<Patriot> patriots = patriotDao.findAll(); // Fetch all patriot
		List<MemberPatriot> patriotDTOs = new LinkedList<>();

		for (Patriot patriot : patriots) {
			patriotDTOs.add(new MemberPatriot(patriot));
		}

		return patriotDTOs;
	}

	/**
	 * Gets chapter by chapter ID
	 * 
	 * @param memberId
	 * @param chapterId
	 * @return
	 */
	@Transactional(readOnly = true)
	public MemberChapter getChapterById(Long memberId, Long chapterId) {
		Chapter chapter = findChapterById(memberId, chapterId); // Ensure the chapter exists
		return new MemberChapter(chapter);
	}

	/**
	 * Gets chapter by member ID
	 * 
	 * @param memberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public MemberChapter getChapterByMemberId(Long memberId) {
		Member member = findMemberById(memberId);
		Chapter chapter = member.getChapter();

		if (chapter == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No chapter assigned to member.");
		}

		return new MemberChapter(chapter);
	}

	/**
	 * Retrieves list of all chapters assigned or not.
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<MemberChapter> getAllChapters() {
		List<Chapter> chapters = chapterDao.findAll(); // Fetch all chapter
		List<MemberChapter> chapterDTOs = new LinkedList<>();

		for (Chapter chapter : chapters) {
			chapterDTOs.add(new MemberChapter(chapter));
		}

		return chapterDTOs;
	}

	/**
	 * Deletes a member by its ID.
	 * 
	 * @param memberId The ID of the member to delete.
	 */
	@Transactional
	public String deleteMember(Long memberId) {
		Member member = findMemberById(memberId); // Ensure member exists
		Chapter chapter = member.getChapter(); // Might be null

		String message;

		// Check if the chapter is assigned only to this member
		boolean isOnlyMemberInChapter = chapter != null && chapter.getMembers().size() == 1;

		// Unlink the member from the chapter before deletion
		member.setChapter(null);
		memberDao.delete(member);

		if (isOnlyMemberInChapter) {
			chapterDao.delete(chapter);
			message = String.format("Member with ID=%d was deleted. Chapter '%s' (ID=%d) was also deleted", memberId,
					chapter.getChapterName(), chapter.getChapterId());
		} else {
			message = String.format("Member with ID=%d was deleted. No chapter was assigned.", memberId);
		}

		return message;
	}

	/**
	 * Deletes a chapter from the system if it is only assigned to the specified
	 * member.
	 *
	 * @param memberId  The ID of the member requesting the deletion.
	 * @param chapterId The ID of the chapter to delete.
	 */
	@Transactional
	public String deleteChapter(Long memberId, Long chapterId) {
		// Look up the chapter by ID
		Chapter chapter = chapterDao.findById(chapterId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Chapter with ID=" + chapterId + " not found."));

		List<Member> members = chapter.getMembers();

		// ✅ Only allow deletion if the chapter is assigned to exactly one member — and
		// it's the caller
		if (members.size() == 1 && members.get(0).getMemberId().equals(memberId)) {
			// Remove the chapter from the member before deletion
			Member member = members.get(0);
			member.setChapter(null);

			chapterDao.delete(chapter); // Delete the chapter from the database

			return "Chapter ID=" + chapterId + " has been deleted.";
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Chapter is assigned to other members and cannot be deleted.");
		}
	}

	/**
	 * Deletes a patriot from a specific member's list and from the database.
	 *
	 * @param memberId  The ID of the member from whom the patriot is being removed.
	 * @param patriotId The ID of the patriot to delete.
	 */
	@Transactional
	public String deletePatriot(Long memberId, Long patriotId) {
		Member member = findMemberById(memberId);
		Patriot patriot = patriotDao.findById(patriotId)
				.orElseThrow(() -> new NoSuchElementException("Patriot with ID=" + patriotId + " not found"));

		// ✅ Remove patriot from member
		member.getPatriot().remove(patriot);

		// ✅ Remove member from patriot
		patriot.getMember().remove(member);

		memberDao.save(member); // persist unlinking
		patriotDao.save(patriot); // persist unlinking

		// ✅ Only delete patriot if it's no longer linked to anyone
		if (patriot.getMember().isEmpty()) {
			patriotDao.delete(patriot);
			return "Patriot ID=" + patriotId + " was successfully deleted.";
		} else {
			return "Patriot was unlinked from member but still assigned elsewhere.";
		}
	}

	/**
	 * Deletes a patriot who is not assigned to any member.
	 *
	 * @param patriotId The ID of the unassigned patriot to delete.
	 * @throws IllegalStateException if the patriot is assigned to any members.
	 */
	@Transactional
	public String deleteUnassignedPatriot(Long patriotId) {
		Patriot patriot = patriotDao.findById(patriotId)
				.orElseThrow(() -> new NoSuchElementException("Patriot with ID=" + patriotId + " not found"));

		if (!patriot.getMember().isEmpty()) {
			throw new IllegalStateException("Cannot delete patriot assigned to one or more members.");
		}

		patriotDao.delete(patriot);

		return "Patriot ID=" + patriotId + " was successfully deleted.";
	}

	/**
	 * Deletes a chapter by ID regardless of member assignments. Use with caution —
	 * suitable for unassigned chapters only.
	 *
	 * @param chapterId The ID of the chapter to delete.
	 */
	@Transactional
	public void deleteChapterById(Long chapterId) {
		Chapter chapter = chapterDao.findById(chapterId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Chapter with ID=" + chapterId + " not found."));

		chapterDao.delete(chapter);
	}

	/**
	 * Assigns a chapter to a member by their respective IDs.
	 *
	 * @param memberId  The ID of the member to assign the chapter to.
	 * @param chapterId The ID of the chapter to assign.
	 * @return A DTO representing the assigned chapter.
	 */
	@Transactional
	public MemberChapter assignChapterToMember(Long memberId, Long chapterId) {
		Member member = memberDao.findById(memberId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found: " + memberId));

		Chapter chapter = chapterDao.findById(chapterId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter not found: " + chapterId));

		// Prevent reassigning if the member is already linked to a different chapter
		if (member.getChapter() != null && !member.getChapter().getChapterId().equals(chapterId)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This member is already assigned to a chapter.");
		}

		member.setChapter(chapter); // Assign the chapter
		memberDao.save(member); // Persist the updated member

		return new MemberChapter(chapter); // Return the result as a DTO
	}

	/**
	 * Assigns an existing patriot to a member.
	 *
	 * @param memberId  The ID of the member.
	 * @param patriotId The ID of the patriot to assign.
	 * @return A DTO representing the assigned patriot.
	 */
	@Transactional
	public MemberPatriot assignPatriotToMember(Long memberId, Long patriotId) {
		Member member = memberDao.findById(memberId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found"));

		Patriot patriot = patriotDao.findById(patriotId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patriot not found"));

		// Prevent duplicate assignment
		if (!member.getPatriot().contains(patriot)) {
			member.getPatriot().add(patriot);
			patriot.getMember().add(member);
			memberDao.save(member);
		}

		return new MemberPatriot(patriot);
	}

	/**
	 * Retrieves a list of patriots who are not assigned to any member.
	 *
	 * @return A list of MemberPatriot DTOs representing unassigned patriots.
	 */
	@Transactional(readOnly = true)
	public List<MemberPatriot> getUnassignedPatriots() {
		List<Patriot> unassigned = patriotDao.findAllUnassignedPatriots();
		List<MemberPatriot> dtoList = new ArrayList<>();

		for (Patriot patriot : unassigned) {
			dtoList.add(new MemberPatriot(patriot));
		}

		return dtoList;
	}
}