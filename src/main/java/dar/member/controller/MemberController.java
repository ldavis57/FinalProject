package dar.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import dar.member.controller.model.ChapterAssignmentResult;
import dar.member.controller.model.MemberData;
import dar.member.controller.model.MemberData.MemberChapter;
import dar.member.controller.model.MemberData.MemberPatriot;
import dar.member.controller.model.PatriotAssignmentResult;
import dar.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller to manage member operations. Provides endpoints to create,
 * update, and retrieve member data.
 */
@RestController
@RequestMapping("/dar_members") // specifies all calls start with dar_member
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
	 * Creates a new patriot that is not initially assigned to any member.
	 *
	 * @param patriotDto The details of the patriot to create, received in the
	 *                   request body.
	 * @return The created patriot data wrapped in a MemberPatriot DTO.
	 */
	@PostMapping("/patriot")
	@ResponseStatus(HttpStatus.CREATED)
	public MemberPatriot createUnassignedPatriot(@RequestBody MemberPatriot patriotDto) {
		log.info("Creating new unassigned patriot: {}", patriotDto);
		return memberService.createUnassignedPatriot(patriotDto);
	}

	/**
	 * Adds an chapter to a specific member.
	 * 
	 * @param memberId      The ID of the member.
	 * @param memberChapter The chapter details received in the request body.
	 * @return The added chapter data.
	 */
	@PostMapping("/{memberId}/chapter")
	@ResponseStatus(HttpStatus.CREATED)
	public ChapterAssignmentResult addChapterToMember(@PathVariable Long memberId,
			@RequestBody MemberChapter memberChapter) {
		log.info("Adding chapter {} to member with ID={}", memberChapter, memberId);
		return memberService.saveChapter(memberId, memberChapter); // ✅ Now matches return type
	}

	/**
	 * Adds a patriot to a specific member.
	 * 
	 * @param memberId      The ID of the member.
	 * @param memberPatriot The patriot details received in the request body.
	 * @return The added patriot data.
	 */
	@PostMapping("/{memberId}/patriot")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PatriotAssignmentResult addPatriotToMember(@PathVariable Long memberId,
			@RequestBody MemberPatriot memberPatriot) {

		log.info("Adding patriot {} to member with ID={}", memberPatriot, memberId);
		return memberService.savePatriot(memberId, memberPatriot);
	}

	/**
	 * Saves new or assigns existing patriot to member
	 * 
	 * @param memberId
	 * @param patriotDTO
	 * @return
	 */
	@PutMapping("/members/{memberId}/patriot")
	public ResponseEntity<PatriotAssignmentResult> savePatriot(@PathVariable Long memberId,
			@RequestBody MemberPatriot patriotDTO) {

		PatriotAssignmentResult result = memberService.savePatriot(memberId, patriotDTO);
		return ResponseEntity.ok(result);
	}

	/**
	 * Assigns existing patriot to a specific member.
	 * 
	 * @param memberId      The ID of the member.
	 * @param patriotId     The ID of the patriot to be updated.
	 * @param memberPatriot The updated patriot data.
	 * @return The updated patriot data.
	 */
	@PutMapping("/{memberId}/patriot/{patriotId}")
	public ResponseEntity<MemberPatriot> assignPatriot(@PathVariable Long memberId, @PathVariable Long patriotId) {
		MemberPatriot result = memberService.assignPatriotToMember(memberId, patriotId);
		return ResponseEntity.ok(result);
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
	 * @param memberId      The ID of the member.
	 * @param chapterId     The ID of the chapter to be updated.
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
	 * Updates the details of a chapter that is not currently assigned to any
	 * member.
	 *
	 * @param chapterId     The ID of the unassigned chapter to update (from the URL
	 *                      path).
	 * @param memberChapter The updated chapter data received in the request body.
	 * @return The updated chapter data wrapped in a MemberChapter DTO.
	 */
	@PutMapping("/chapter/{chapterId}")
	public MemberChapter updateUnassignedChapter(@PathVariable Long chapterId,
			@RequestBody MemberChapter memberChapter) { // allows updating an unassigned chapter
		log.info("Updating chapter with ID={}", chapterId);
		return memberService.updateUnassignedChapter(chapterId, memberChapter);
	}

	/**
	 * Updates the details of a patriot that is not currently assigned to any
	 * member.
	 *
	 * @param patriotId     The ID of the unassigned patriot to update (from the URL
	 *                      path).
	 * @param memberPatriot The updated patriot data received in the request body.
	 * @return A ResponseEntity containing the updated patriot data wrapped in a
	 *         MemberPatriot DTO.
	 */
	@PutMapping("/patriot/{patriotId}")
	public ResponseEntity<MemberPatriot> updateUnassignedPatriot(@PathVariable Long patriotId,
			@RequestBody MemberPatriot memberPatriot) {

		log.info("Updating Patriot with ID={}", patriotId);

		MemberPatriot updated = memberService.updateUnassignedPatriot(patriotId, memberPatriot);
		return ResponseEntity.ok(updated); // ✅ this now matches the return type
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
		try {
			return memberService.retrieveMemberById(memberId);
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member with ID=" + memberId + " not found.", e);
		}
	}

	/**
	 * Retrieves all chapter for a specific member.
	 * 
	 * @param memberId The ID of the member.
	 * @return A list of chapter working in the specified member.
	 */
	@GetMapping("/{memberId}/chapter")
	public MemberChapter getChapterForMember(@PathVariable Long memberId) {
		log.info("Retrieving chapter for member with ID={}", memberId);
		return memberService.getChapterByMemberId(memberId);
	}

	/**
	 * Retrieves a specific chapter by their ID from a member.
	 * 
	 * @param memberId  The ID of the member.
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
	public List<MemberData.MemberChapter> getAllChapters() {
		log.info("Retrieving all chapters across all members");
		return memberService.getAllChapters();
	}

	/**
	 * Retrieves all patriot across all members.
	 * 
	 * @return A list of all patriot in all members.
	 */
	@GetMapping("/patriots")
	public List<MemberPatriot> getAllPatriots() {
		log.info("Retrieving all patriots across all members");
		return memberService.getAllPatriots();
	}

	/**
	 * Retrieves all patriots for a specific member.
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
	 * Retrieves a specific patriot by their ID from a member.
	 * 
	 * @param memberId  The ID of the member.
	 * @param patriotId The ID of the patriot to retrieve.
	 * @return The patriot data if found.
	 */
	@GetMapping("/{memberId}/patriot/{patriotId}")
	public MemberPatriot getPatriotById(@PathVariable Long memberId, @PathVariable Long patriotId) {
		log.info("Retrieving patriot with ID={} for member with ID={}", patriotId, memberId);
		return memberService.getPatriotById(memberId, patriotId);
	}

	/**
	 * Retrieves all patriots that are not currently assigned to any member.
	 *
	 * @return A list of unassigned patriots, each wrapped in a MemberPatriot DTO.
	 */
	@GetMapping("/patriots/unassigned") // get all unassigned Patriots
	public List<MemberPatriot> getUnassignedPatriots() {
		log.info("Retrieving all unassigned patriots.");
		return memberService.getUnassignedPatriots();
	}

	/**
	 * Deletes a specific member by ID.
	 * 
	 * @param memberId The ID of the member to be deleted.
	 */
	@DeleteMapping("/{memberId}")
	public ResponseEntity<String> deleteMemberById(@PathVariable Long memberId) {
		log.info("Deleting member with ID={}", memberId);

		String resultMessage = memberService.deleteMember(memberId);
		return ResponseEntity.ok(resultMessage);
	}

	/**
	 * Deletes a patriot from a specific member.
	 * 
	 * @param memberId  The ID of the member.
	 * @param patriotId The ID of the patriot to be deleted.
	 */
	@DeleteMapping("/{memberId}/patriot/{patriotId}")
	public ResponseEntity<String> deletePatriot(@PathVariable Long memberId, @PathVariable Long patriotId) {
		log.info("Deleting patriot with ID={} from member with ID={}", patriotId, memberId);
		String result = memberService.deletePatriot(memberId, patriotId);
		return ResponseEntity.ok(result);
	}

	/**
	 * Deletes unassigned patriot by ID
	 * 
	 * @param patriotId
	 * @return
	 */
	@DeleteMapping("/patriot/{patriotId}")
	public ResponseEntity<String> deleteUnassignedPatriot(@PathVariable Long patriotId) {
		log.info("Deleting unassigned patriot with ID={}", patriotId);
		String result = memberService.deleteUnassignedPatriot(patriotId);
		return ResponseEntity.ok(result);
	}

	/**
	 * Deletes an chapter from a specific member.
	 * 
	 * @param memberId  The ID of the member.
	 * @param chapterId The ID of the chapter to be deleted.
	 */
	@DeleteMapping("/{memberId}/chapter/{chapterId}")
	@ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content on successful deletion
	public ResponseEntity<String> deleteChapter(@PathVariable Long memberId, @PathVariable Long chapterId) {
		log.info("Deleting chapter with ID={} from member with ID={}", chapterId, memberId);
		String result = memberService.deleteChapter(memberId, chapterId);
		return ResponseEntity.ok(result);
	}

	/**
	 * Deletes a chapter by its ID.
	 *
	 * @param chapterId The ID of the chapter to delete.
	 * @return A confirmation message in a JSON response.
	 */
	@DeleteMapping("/chapter/{chapterId}")
	public ResponseEntity<Map<String, String>> deleteChapterById(@PathVariable Long chapterId) {
		log.info("Deleting chapter with ID={}", chapterId); // Log the deletion
		memberService.deleteChapterById(chapterId); // Delegate to service layer

		// Create a response message
		Map<String, String> response = new HashMap<>();
		response.put("message", "Chapter has been successfully deleted.");

		return ResponseEntity.ok(response); // Return HTTP 200 with message
	}

	/**
	 * Prevents deletion of all chapters at once. Throws an exception if the
	 * endpoint is called.
	 */
	@DeleteMapping("/chapters")
	public void deleteAllChapters() {
		log.warn("Attempting to delete all chapters."); // Log the prohibited action
		throw new UnsupportedOperationException("Deleting all chapters is not allowed.");
	}

	/**
	 * Prevents deletion of all patriots at once. Throws an exception if the
	 * endpoint is called.
	 */
	@DeleteMapping("/patriots")
	public void deleteAllPatriots() {
		log.warn("Attempting to delete all Patriots."); // Log the prohibited action
		throw new UnsupportedOperationException("Deleting all Patriots is not allowed.");
	}
}