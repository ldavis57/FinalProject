package dar.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import dar.member.controller.model.MemberData;
import dar.member.controller.model.MemberData.MemberPatriot;
import dar.member.controller.model.MemberData.MemberChapter;
import dar.member.service.MemberService;

/**
 * REST Controller to manage member operations. Provides endpoints to create,
 * update, and retrieve member data.
 */
@RestController
@RequestMapping("/dar_member") // specifies all calls start with dar_member
@Slf4j // Enables logging using Lombok
public class MemberController {

	@Autowired // Injects the memberService instance automatically
	private MemberService memberService;

	/**
	 * Creates a new member.
	 * 
	 * @param memberData The member details received in the request body.
	 * @return The saved member data.
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MemberData createMember(@RequestBody MemberData memberData) {
		log.info("Creating new member: {}", memberData);

		if (memberData == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Member data cannot be null.");
		}

		return memberService.saveMember(memberData);
	}

	/**
	 * Adds an chapter to a specific member.
	 * 
	 * @param memberId       The ID of the member.
	 * @param memberChapter The chapter details received in the request body.
	 * @return The added chapter data.
	 */
	@PostMapping("/{memberId}/chapter")
	@ResponseStatus(code = HttpStatus.CREATED) // Returns HTTP 201 Created on success
	public MemberChapter addChapterToMember(@PathVariable Long memberId,
			@RequestBody MemberChapter memberChapter) { // @RequestBody: body is json
		log.info("Adding chapter {} to member with ID={}", memberChapter, memberId);
		return memberService.saveChapter(memberId, memberChapter);
	}

	/**
	 * Adds a patriot to a specific member.
	 * 
	 * @param memberId       The ID of the member.
	 * @param memberPatriot The patriot details received in the request body.
	 * @return The added patriot data.
	 */
	@PostMapping("/{memberId}/patriot")
	@ResponseStatus(code = HttpStatus.CREATED)
	public MemberPatriot addPatriotToMember(@PathVariable Long memberId,
			@RequestBody MemberPatriot memberPatriot) {
		log.info("Adding patriot {} to member with ID={}", memberPatriot, memberId);
		return memberService.savePatriot(memberId, memberPatriot);
	}

	/**
	 * Updates an existing member by ID.
	 * 
	 * @param memberId   The ID of the member.
	 * @param memberData The updated member data.
	 * @return The updated member data.
	 */
	@PutMapping("/{memberId}")
	public MemberData updateMember(@PathVariable Long memberId, @RequestBody MemberData memberData) {
		memberData.setMemberId(memberId); // Ensures the correct ID is set
		log.info("Updating member with ID: {}", memberData);
		return memberService.saveMember(memberData);
	}

	/**
	 * Updates an chapter in a specific member.
	 * 
	 * @param memberId       The ID of the member.
	 * @param chapterId       The ID of the chapter to be updated.
	 * @param memberChapter The updated chapter data.
	 * @return The updated chapter data.
	 */
	@PutMapping("/{memberId}/chapter/{chapterId}")
	public MemberChapter updateChapter(@PathVariable Long memberId, @PathVariable Long chapterId,
			@RequestBody MemberChapter memberChapter) {
		log.info("Updating chapter with ID={} for member with ID={}", chapterId, memberId);
		return memberService.updateChapter(memberId, chapterId, memberChapter);
	}

	/**
	 * Updates a patriot in a specific member.
	 * 
	 * @param memberId       The ID of the member.
	 * @param patriotId       The ID of the patriot to be updated.
	 * @param memberPatriot The updated patriot data.
	 * @return The updated patriot data.
	 */
	@PutMapping("/{memberId}/patriot/{patriotId}")
	public MemberPatriot updatePatriot(@PathVariable Long memberId, @PathVariable Long patriotId,
			@RequestBody MemberPatriot memberPatriot) {
		log.info("Updating patriot with ID={} for member with ID={}", patriotId, memberId);
		return memberService.updatePatriot(memberId, patriotId, memberPatriot);
	}

	@PutMapping("/chapter/{chapterId}")
	public MemberChapter updateUnassignedChapter(@PathVariable Long chapterId,
			@RequestBody MemberChapter memberChapter) {
		log.info("Updating chapter with ID={}", chapterId);
		return memberService.updateUnassignedChapter(chapterId, memberChapter);
	}

	@PutMapping("/{memberId}/chapter/{chapterId}/assign")
	public MemberChapter assignChapterToMember(@PathVariable Long memberId, @PathVariable Long chapterId) {
		log.info("Assigning chapter with ID={} to member with ID={}", chapterId, memberId);
		return memberService.assignChapterToMember(memberId, chapterId);
	}

	/**
	 * Retrieves all members.
	 * 
	 * @return A list of all stored members.
	 */
	@GetMapping
	public List<MemberData> retrieveAllMembers() {
		log.info("Retrieving all members");
		return memberService.retrieveAllmember();
	}

	/**
	 * Retrieves a specific member by ID.
	 * 
	 * @param memberId The ID of the member to retrieve.
	 * @return The member data if found.
	 */
	@GetMapping("/{memberId}")
	public MemberData retrieveMemberById(@PathVariable Long memberId) {
		log.info("Retrieving member with ID={}", memberId);
		return memberService.retrieveMemberById(memberId);
	}

	/**
	 * Retrieves all chapter for a specific member.
	 * 
	 * @param memberId The ID of the member.
	 * @return A list of chapter working in the specified member.
	 */
	@GetMapping("/{memberId}/chapter")
	public List<MemberChapter> getChapterForMember(@PathVariable Long memberId) {
		log.info("Retrieving chapter for member with ID={}", memberId);
		return memberService.getChapterByMemberId(memberId);
	}

	/**
	 * Retrieves a specific chapter by their ID from a member.
	 * 
	 * @param memberId The ID of the member.
	 * @param chapterId The ID of the chapter to retrieve.
	 * @return The chapter data if found.
	 */
	@GetMapping("/{memberId}/chapter/{chapterId}")
	public MemberChapter getChapterById(@PathVariable Long memberId, @PathVariable Long chapterId) {
		log.info("Retrieving chapter with ID={} from member with ID={}", chapterId, memberId);
		return memberService.retrieveChapterById(memberId, chapterId);
	}

	/**
	 * Retrieves all chapter across all members.
	 * 
	 * @return A list of all chapter in all members.
	 */
	@GetMapping("/chapters")
	public List<MemberChapter> getAllChapters() {
		log.info("Retrieving all chapters across all members");
		return memberService.getAllChapters();
	}

	/**
	 * Retrieves all patriot for a specific member.
	 * 
	 * @param memberId The ID of the member.
	 * @return A list of patriot associated with the specified member.
	 */
	@GetMapping("/{memberId}/patriots")
	public List<MemberPatriot> getAllPatriotsForMember(@PathVariable Long memberId) {
		log.info("Retrieving all patriots for member with ID={}", memberId);
		return memberService.getPatriotsByMemberId(memberId);
	}

	/**
	 * Retrieves all patriot across all members.
	 * 
	 * @return A list of all patriot in all members.
	 */
	@GetMapping("/patriot")
	public List<MemberPatriot> getAllPatriots() {
		log.info("Retrieving all patriots across all members");
		return memberService.getAllPatriots();
	}

	/**
	 * Retrieves a specific patriot by their ID from a member.
	 * 
	 * @param memberId The ID of the member.
	 * @param patriotId The ID of the patriot to retrieve.
	 * @return The patriot data if found.
	 */
	@GetMapping("/{memberId}/patriot/{patriotId}")
	public MemberPatriot getPatriotById(@PathVariable Long memberId, @PathVariable Long patriotId) {
		log.info("Retrieving patriot with ID={} for member with ID={}", patriotId, memberId);
		return memberService.getPatriotById(memberId, patriotId);
	}

	/**
	 * Deletes a specific member by ID.
	 * 
	 * @param memberId The ID of the member to be deleted.
	 */
	@DeleteMapping("/{memberId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMemberById(@PathVariable Long memberId) {
	    log.info("Deleting member with ID={}", memberId);
	    memberService.deleteMember(memberId);
	}	/**
	 * Deletes a patriot from a specific member.
	 * 
	 * @param memberId The ID of the member.
	 * @param patriotId The ID of the patriot to be deleted.
	 */
	@DeleteMapping("/{memberId}/patriot/{patriotId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content on successful deletion
	public void deletePatriot(@PathVariable Long memberId, @PathVariable Long patriotId) {
		log.info("Deleting patriot with ID={} from member with ID={}", patriotId, memberId);
		memberService.deletePatriot(memberId, patriotId);
	}

	/**
	 * Deletes an chapter from a specific member.
	 * 
	 * @param memberId The ID of the member.
	 * @param chapterId The ID of the chapter to be deleted.
	 */
	@DeleteMapping("/{memberId}/chapter/{chapterId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content on successful deletion
	public void deleteChapter(@PathVariable Long memberId, @PathVariable Long chapterId) {
		log.info("Deleting chapter with ID={} from member with ID={}", chapterId, memberId);
		memberService.deleteChapter(memberId, chapterId);
	}
}
