package dar.member.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import dar.member.controller.model.MemberData.MemberPatriot;
import dar.member.dao.MemberDao;
import dar.member.dao.PatriotDao;
import dar.member.entity.Member;
import dar.member.entity.Patriot;
import dar.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberServicePatriotValidationTest {

    @Mock
    private MemberDao memberDao;

    @Mock
    private PatriotDao patriotDao;

    @InjectMocks
    private MemberService memberService;

    @Test
    void updatePatriot_shouldThrowIfMemberNotFound() {
        Long memberId = 1L;
        Long patriotId = 10L;

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            memberService.updatePatriot(memberId, patriotId, new MemberPatriot())
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertFalse(ex.getReason().contains("Member with ID="));
    }

    @Test
    void updatePatriot_shouldThrowIfPatriotNotFound() {
        Long memberId = 1L;
        Long patriotId = 99L;

        Member member = new Member();
        member.setMemberId(memberId);

       // when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        when(patriotDao.findById(patriotId)).thenReturn(Optional.empty()); // if patriot now found

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            memberService.updatePatriot(memberId, patriotId, new MemberPatriot())
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Patriot with ID="));
    }

    @Test
    void updatePatriot_shouldThrowIfPatriotNotLinkedToMember() {
        Long memberId = 1L;
        Long patriotId = 10L;

        Member otherMember = new Member();
        otherMember.setMemberId(2L);

        Patriot patriot = new Patriot();
        patriot.setPatriotId(patriotId);
        patriot.getMember().add(otherMember); // linked to someone else

        Member member = new Member();
        member.setMemberId(memberId);

        // when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        when(patriotDao.findById(patriotId)).thenReturn(Optional.of(patriot));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
            memberService.updatePatriot(memberId, patriotId, new MemberPatriot())
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("not a patriot of the member with ID="));
    }
}
