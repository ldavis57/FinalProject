package dar.member.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import dar.member.controller.MemberController;
import dar.member.controller.model.MemberData;
import dar.member.controller.model.MemberData.MemberPatriot;
import dar.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;
    private MemberData memberData;
    private MemberPatriot memberPatriot;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();

        memberData = new MemberData();
        memberData.setMemberId(1L);
        memberData.setMemberFirstName("John");
        memberData.setMemberLastName("Doe");

        memberPatriot = new MemberPatriot();
        memberPatriot.setPatriotId(1L);
        memberPatriot.setPatriotFirstName("George");
        memberPatriot.setPatriotLastName("Washington");
        memberPatriot.setPatriotState("Virginia");
    }

    @Test
    void createMember_ShouldReturnCreatedMember() throws Exception {
        when(memberService.saveMember(any(MemberData.class))).thenReturn(memberData);

        mockMvc.perform(post("/dar_member")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"memberFirstName\": \"John\", \"memberLastName\": \"Doe\" }"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberFirstName").value("John"));
    }

    @Test
    void retrieveMemberById_ShouldReturnMember() throws Exception {
        when(memberService.retrieveMemberById(1L)).thenReturn(memberData);

        mockMvc.perform(get("/dar_member/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberFirstName").value("John"));
    }

    @Test
    void retrieveMemberById_ShouldReturnNotFound() throws Exception {
        when(memberService.retrieveMemberById(1L)).thenThrow(new NoSuchElementException("Not found"));

        mockMvc.perform(get("/dar_member/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMemberById_ShouldReturnNoContent() throws Exception {
        doNothing().when(memberService).deleteMember(1L);

        mockMvc.perform(delete("/dar_member/1"))
                .andExpect(status().isNoContent());
    }
}
