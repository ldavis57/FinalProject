package dar.member.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dar.member.dao.MemberDao;
import dar.member.dao.PatriotDao;
import dar.member.entity.Member;
import dar.member.entity.Patriot;
import dar.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberServiceDeletePatriotTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberDao memberDao;

    @Mock
    private PatriotDao patriotDao;

    @Test
    void deletePatriot_shouldUnlinkFromMemberAndDeleteIfUnassigned() {
        Long memberId = 1L;
        Long patriotId = 10L;

        Member member = new Member();
        member.setMemberId(memberId);

        Patriot patriot = new Patriot();
        patriot.setPatriotId(patriotId);
        patriot.getMember().add(member);
        member.getPatriot().add(patriot);

        Mockito.when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        Mockito.when(patriotDao.findById(patriotId)).thenReturn(Optional.of(patriot));

        String result = memberService.deletePatriot(memberId, patriotId);

        assertEquals("Patriot ID=10 was successfully deleted.", result);
        assertFalse(member.getPatriot().contains(patriot));
        assertFalse(patriot.getMember().contains(member));

        Mockito.verify(memberDao).save(member);
        Mockito.verify(patriotDao).save(patriot);
        Mockito.verify(patriotDao).delete(patriot);
    }

    @Test
    void deletePatriot_shouldUnlinkFromMemberButNotDeleteIfStillAssigned() {
        Long memberId = 1L;
        Long patriotId = 10L;

        Member member = new Member();
        member.setMemberId(memberId);

        Member otherMember = new Member();
        otherMember.setMemberId(2L);

        Patriot patriot = new Patriot();
        patriot.setPatriotId(patriotId);
        patriot.getMember().add(member);
        patriot.getMember().add(otherMember);
        member.getPatriot().add(patriot);

        Mockito.when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        Mockito.when(patriotDao.findById(patriotId)).thenReturn(Optional.of(patriot));

        String result = memberService.deletePatriot(memberId, patriotId);

        assertEquals("Patriot was unlinked from member but still assigned elsewhere.", result);
        assertFalse(member.getPatriot().contains(patriot));
        assertFalse(patriot.getMember().contains(member));

        Mockito.verify(memberDao).save(member);
        Mockito.verify(patriotDao).save(patriot);
        Mockito.verify(patriotDao, Mockito.never()).delete(Mockito.any());
    }
}
