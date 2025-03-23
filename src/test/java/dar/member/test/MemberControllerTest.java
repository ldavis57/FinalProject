package dar.member.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import dar.member.controller.MemberController;
import dar.member.controller.model.ChapterAssignmentResult;
import dar.member.controller.model.MemberData;
import dar.member.controller.model.MemberData.MemberChapter;
import dar.member.service.MemberService;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@SuppressWarnings("removal")
	@MockBean
	private MemberService memberService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createMember_shouldReturnCreatedMember() throws Exception {
		MemberData memberData = new MemberData();
		memberData.setMemberId(1L);
		memberData.setMemberFirstName("George");

		Mockito.when(memberService.saveMember(any(MemberData.class))).thenReturn(memberData);

		mockMvc.perform(post("/dar_members").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(memberData))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.memberId").value(1L)).andExpect(jsonPath("$.memberFirstName").value("George"));
	}

	@Test
	void retrieveAllMembers_shouldReturnList() throws Exception {
		Mockito.when(memberService.retrieveAllmember()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/dar_members")).andExpect(status().isOk());
	}

	@Test
	void deleteMember_shouldReturnConfirmationMessage() throws Exception {
		Mockito.when(memberService.deleteMember(1L)).thenReturn("Member deleted successfully");

		mockMvc.perform(delete("/dar_members/1")).andExpect(status().isOk())
				.andExpect(content().string("Member deleted successfully"));
	}

	@Test
	void addChapterToMember_shouldReturnChapter() throws Exception {
		MemberChapter chapter = new MemberChapter();
		chapter.setChapterId(100L);
		chapter.setChapterName("Liberty Chapter");
		chapter.setChapterNumber("001");

		ChapterAssignmentResult result = new ChapterAssignmentResult("New chapter created", chapter);

		Mockito.when(memberService.saveChapter(eq(1L), any(MemberChapter.class))).thenReturn(result); // ✅ now returning
																										// ChapterAssignmentResult

		mockMvc.perform(post("/dar_members/1/chapter").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(chapter))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.chapter.chapterId").value(100L)) // ✅ nested inside `chapter`
				.andExpect(jsonPath("$.message").value("New chapter created"));
	}

}
