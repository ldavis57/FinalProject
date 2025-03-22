package dar.member.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import dar.member.controller.MemberController;
import dar.member.controller.model.MemberData;
import dar.member.controller.model.MemberData.MemberChapter;
import dar.member.controller.model.MemberData.MemberPatriot;
import dar.member.controller.model.PatriotAssignmentResult;
import dar.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(post("/dar_members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andExpect(jsonPath("$.memberFirstName").value("George"));
    }

    @Test
    void retrieveAllMembers_shouldReturnList() throws Exception {
        Mockito.when(memberService.retrieveAllmember()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/dar_members"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMember_shouldReturnConfirmationMessage() throws Exception {
        Mockito.when(memberService.deleteMember(1L)).thenReturn("Member deleted successfully");

        mockMvc.perform(delete("/dar_members/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Member deleted successfully"));
    }

    @Test
    void addChapterToMember_shouldReturnChapter() throws Exception {
        MemberChapter chapter = new MemberChapter();
        chapter.setChapterId(100L);
        Mockito.when(memberService.saveChapter(eq(1L), any(MemberChapter.class))).thenReturn(chapter);

        mockMvc.perform(post("/dar_members/1/chapter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chapter)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.chapterId").value(100L));
    }

    @Test
    void addPatriotToMember_shouldReturnResult() throws Exception {
        MemberPatriot patriot = new MemberPatriot();
        PatriotAssignmentResult result = new PatriotAssignmentResult("Assigned", patriot);

        Mockito.when(memberService.savePatriot(eq(1L), any(MemberPatriot.class))).thenReturn(result);

        mockMvc.perform(post("/dar_members/1/patriot")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patriot)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Assigned"));
    }
}
