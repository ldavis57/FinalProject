package dar.member.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dar.member.controller.model.MemberData.MemberChapter;
import dar.member.dao.ChapterDao;
import dar.member.dao.MemberDao;
import dar.member.entity.Chapter;
import dar.member.entity.Member;
import dar.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberServiceChapterValidationTest {

    @Mock
    private MemberDao memberDao;

    @Mock
    private ChapterDao chapterDao;

    @InjectMocks
    private MemberService memberService;

    @Test
    void updateChapter_shouldThrowIfMemberHasNoChapter() {
        Long memberId = 1L;
        Long chapterId = 10L;

        Member member = new Member();
        member.setMemberId(memberId);
        member.setChapter(null); // No chapter assigned

        Chapter chapter = new Chapter();
        chapter.setChapterId(chapterId);

        when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        when(chapterDao.findById(chapterId)).thenReturn(Optional.of(chapter));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.updateChapter(memberId, chapterId, new MemberChapter())
        );

        assertTrue(ex.getMessage().contains("is not assigned to any chapter"));
    }

    @Test
    void updateChapter_shouldThrowIfChapterDoesNotMatchMember() {
        Long memberId = 1L;
        Long chapterId = 100L;

        Chapter otherChapter = new Chapter();
        otherChapter.setChapterId(200L);

        Member member = new Member();
        member.setMemberId(memberId);
        member.setChapter(otherChapter); // assigned to a different chapter

        Chapter requestedChapter = new Chapter();
        requestedChapter.setChapterId(chapterId);

        when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        when(chapterDao.findById(chapterId)).thenReturn(Optional.of(requestedChapter));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            memberService.updateChapter(memberId, chapterId, new MemberChapter())
        );

        assertTrue(ex.getMessage().contains("already assigned to a different chapter"));
    }

    @Test
    void updateChapter_shouldThrowIfMemberNotFound() {
        Long memberId = 1L;
        Long chapterId = 100L;

        when(memberDao.findById(memberId)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->
            memberService.updateChapter(memberId, chapterId, new MemberChapter())
        );

        assertTrue(ex.getMessage().contains("Member with ID="));
    }

    @Test
    void updateChapter_shouldThrowIfChapterNotFound() {
        Long memberId = 1L;
        Long chapterId = 100L;

        Member member = new Member();
        member.setMemberId(memberId);
        member.setChapter(new Chapter());

        when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        when(chapterDao.findById(chapterId)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class, () ->
            memberService.updateChapter(memberId, chapterId, new MemberChapter())
        );

        assertTrue(ex.getMessage().contains("Chapter with ID="));
    }
}
