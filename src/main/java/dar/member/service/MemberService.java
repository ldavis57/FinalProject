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

import dar.member.controller.model.MemberData;
import dar.member.controller.model.MemberData.MemberPatriot;
import dar.member.controller.model.MemberData.MemberChapter;
import dar.member.dao.PatriotDao;
import dar.member.dao.ChapterDao;
import dar.member.dao.MemberDao;
import dar.member.entity.Patriot;
import dar.member.entity.Chapter;
import dar.member.entity.Member;

/**
 * Service layer for managing member operations. Provides methods for
 * creating, updating, retrieving, and deleting members.
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
	 * Saves or updates a member record. Used by both POST (create) and PUT
	 * (update) operations.
	 * 
	 * @param memberData The member data transfer object (DTO).
	 * @return The saved member data.
	 */
//	@Transactional(readOnly = false) // Allows write operations within a transaction
//	public MemberData saveMember(MemberData memberData) {
//		Long memberId = memberData.getMemberId();
//		Member member = findOrCreateMember(memberId);
//
//		copyMemberFields(member, memberData); // Copies DTO fields to entity
//		return new MemberData(memberDao.save(member)); // Saves entity and returns DTO
//	}
	
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
		member.setMemberOffice(memberData.getMemberOffice());	}

	private void copyChapterFields(Chapter chapter, MemberChapter memberChapter) {
		chapter.setChapterId(memberChapter.getChapterId());
		chapter.setChapterName(memberChapter.getChapterName());
		chapter.setChapterNumber(memberChapter.getChapterNumber());
	}

	private void copyPatriotFields(Patriot patriot, MemberPatriot memberPatriot) {
		patriot.setPatriotId(memberPatriot.getPatriotId());
		patriot.setPatriotFirstName(memberPatriot.getPatriotFirstName()); // Stores the patriot's first name
		patriot.setPatriotLastName(memberPatriot.getPatriotLastName()); // Stores the patriot's last name
		patriot.setPatriotState(memberPatriot.getPatriotState()); // Stores the patriot's email address
		patriot.setPatriotRankService(memberPatriot.getPatriotRankService()); // Stores the patriot's email address
	}
	
	@Transactional
	public MemberChapter updateChapter(Long memberId, Long chapterId, MemberChapter memberChapter) {
	    Chapter chapter = findChapterById(memberId, chapterId); // Ensures the chapter exists

	    // Update fields
	    chapter.setChapterName(memberChapter.getChapterName());
	    chapter.setChapterNumber(memberChapter.getChapterNumber());

	    Chapter updatedChapter = chapterDao.save(chapter);
	    return new MemberChapter(updatedChapter);
	}
	
	@Transactional
	public MemberChapter updateUnassignedChapter(Long chapterId, MemberChapter memberChapter) {
	    if (memberChapter == null) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter details cannot be null.");
	    }
	
	    Chapter chapter = chapterDao.findById(chapterId)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter with ID=" + chapterId + " not found."));
	
	    // Validate required fields before updating
	    if (memberChapter.getChapterName() == null || memberChapter.getChapterName().trim().isEmpty()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter first name cannot be empty.");
	    }
	
	    if (memberChapter.getChapterNumber() == null || memberChapter.getChapterNumber().trim().isEmpty()) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapter last name cannot be empty.");
	    }
	
	    // Update fields
	    chapter.setChapterName(memberChapter.getChapterName());
	    chapter.setChapterNumber(memberChapter.getChapterNumber());
	
	    Chapter updatedChapter = chapterDao.save(chapter);
	    return new MemberChapter(updatedChapter);
	}

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

	private Chapter findOrCreateChapter(Long memberId, Long chapterId) {
		if (Objects.isNull(chapterId)) {
			return new Chapter();
		}
		return findChapterById(memberId, chapterId);
	}

	private Patriot findOrCreatePatriot(Long memberId, Long patriotId) {
		if (Objects.isNull(patriotId)) {
			return new Patriot();
		}
		return findPatriotById(memberId, patriotId);
	}

	private Member findMemberById(Long memberId) {
		return memberDao.findById(memberId)
				.orElseThrow(() -> new NoSuchElementException("Member with ID=" + memberId + " was not found."));
	}

	private Chapter findChapterById(Long memberId, Long chapterId) {
		Chapter chapter = chapterDao.findById(chapterId)
				.orElseThrow(() -> new NoSuchElementException("Chapter with ID=" + chapterId + " was not found."));

		if (chapter.getMember().getMemberId() != memberId) {
			throw new IllegalArgumentException("The chapter with ID=" + chapterId
					+ " is not employed by the member with ID=" + memberId + ".");
		}
		return chapter;
	}

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
					"The patriot with ID=" + patriotId + " is not a member of the member with ID=" + memberId);
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
	
	@Transactional(readOnly = true)
	public MemberChapter retrieveChapterById(Long memberId, Long chapterId) {
	    Chapter chapter = findChapterById(memberId, chapterId);
	    return new MemberChapter(chapter);
	}


	@Transactional(readOnly = false)
	public MemberChapter saveChapter(Long memberId, MemberChapter memberChapter) {
	    try {
	        Member member = findMemberById(memberId);
	        Long chapterId = memberChapter.getChapterId();
	        Chapter chapter = findOrCreateChapter(memberId, chapterId);

	        copyChapterFields(chapter, memberChapter);
	        chapter.setMember(member);
	        member.getChapter().add(chapter);

	        Chapter dbChapter = chapterDao.save(chapter);
	        return new MemberChapter(dbChapter);
	    } catch (NoSuchElementException e) {
	        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "member not found with ID=" + memberId, e);
	    }
	}
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

	@Transactional
	public MemberPatriot savePatriot(Long memberId, MemberPatriot memberPatriot) {
		Member member = findMemberById(memberId);
		Long patriotId = memberPatriot.getPatriotId();
		Patriot patriot = findOrCreatePatriot(memberId, patriotId);

		copyPatriotFields(patriot, memberPatriot);

		patriot.getMember().add(member);
		member.getPatriot().add(patriot);

		Patriot dbPatriot = patriotDao.save(patriot);
		return new MemberPatriot(dbPatriot);
	}
	
	@Transactional(readOnly = true)
	public List<MemberPatriot> getPatriotsByMemberId(Long memberId) {
	    Member member = findMemberById(memberId); // Ensure member exists

	    List<MemberPatriot> patriotList = new LinkedList<>();
	    for (Patriot patriot : member.getPatriot()) {
	        patriotList.add(new MemberPatriot(patriot));
	    }

	    return patriotList;
	}
	
	@Transactional(readOnly = true)
	public MemberPatriot getPatriotById(Long memberId, Long patriotId) {
	    Patriot patriot = findPatriotById(memberId, patriotId); // Ensure the patriot exists
	    return new MemberPatriot(patriot);
	}
	
	@Transactional(readOnly = true)
	public List<MemberPatriot> getAllPatriots() {
	    List<Patriot> patriots = patriotDao.findAll(); // Fetch all patriot
	    List<MemberPatriot> patriotDTOs = new LinkedList<>();

	    for (Patriot patriot : patriots) {
	        patriotDTOs.add(new MemberPatriot(patriot));
	    }

	    return patriotDTOs;
	}

	@Transactional(readOnly = true)
	public MemberChapter getChapterById(Long memberId, Long chapterId) {
	    Chapter chapter = findChapterById(memberId, chapterId); // Ensure the chapter exists
	    return new MemberChapter(chapter);
	}
	
	@Transactional(readOnly = true)
	public List<MemberChapter> getChapterByMemberId(Long memberId) {
	    Member member = findMemberById(memberId); // Ensure member exists

	    List<MemberChapter> chapterList = new LinkedList<>();
	    for (Chapter chapter : member.getChapter()) {
	        chapterList.add(new MemberChapter(chapter));
	    }

	    return chapterList;
	}
	
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
	public void deleteMember(Long memberId) {
		memberDao.deleteById(memberId);
	}

	@Transactional
	public void deleteChapter(Long memberId, Long chapterId) {
	    Chapter chapter = findChapterById(memberId, chapterId); // Ensure the chapter exists
	
	    // Remove chapter from the member’s chapter list
	    Member member = chapter.getMember();
	    if (member != null) {
	        member.getChapter().remove(chapter);
	    }
	
	    // Delete the chapter from the database
	    chapterDao.delete(chapter);
	}

	@Transactional
	public void deletePatriot(Long memberId, Long patriotId) {
	    Patriot patriot = findPatriotById(memberId, patriotId); // Ensure the patriot exists
	    
	    // Remove the patriot from the member's patriot list
	    for (Member member : patriot.getMember()) {
	        if (member.getMemberId().equals(memberId)) {
	            member.getPatriot().remove(patriot);
	            break;
	        }
	    }
	
	    // Delete the patriot from the database
	    patriotDao.deleteById(patriotId);
	}
	
	@Transactional
	public MemberChapter assignChapterToMember(Long memberId, Long chapterId) {
	    Chapter chapter = chapterDao.findById(chapterId)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chapter with ID=" + chapterId + " not found."));

	    Member member = memberDao.findById(memberId)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "member with ID=" + memberId + " not found."));

	    // Check if the chapter is already assigned
	    if (chapter.getMember() != null && chapter.getMember().getMemberId().equals(memberId)) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
	                "Chapter is already assigned to this member.");
	    }

	    // Assign the member to the chapter
	    chapter.setMember(member);
	    member.getChapter().add(chapter);

	    Chapter updatedChapter = chapterDao.save(chapter);
	    return new MemberChapter(updatedChapter);
	}
}
