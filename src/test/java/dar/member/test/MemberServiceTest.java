package dar.member.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dar.member.controller.model.ChapterAssignmentResult;
import dar.member.controller.model.MemberData.MemberChapter;
import dar.member.dao.ChapterDao;
import dar.member.dao.MemberDao;
import dar.member.entity.Chapter;
import dar.member.entity.Member;
import dar.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberDao memberDao;

    @Mock
    private ChapterDao chapterDao;

    @InjectMocks
    private MemberService memberService;

    @Test
    void saveChapter_shouldAssignExistingChapterToMember() {
        // Given
        Long memberId = 1L;
        Member member = new Member();
        member.setMemberId(memberId);
        MemberChapter inputChapter = new MemberChapter();
        inputChapter.setChapterName("Liberty");
        inputChapter.setChapterNumber("001");

        Chapter existingChapter = new Chapter();
        existingChapter.setChapterId(100L);
        existingChapter.setChapterName("Liberty");
        existingChapter.setChapterNumber("001");

        when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        when(chapterDao.findByChapterNameIgnoreCaseAndChapterNumberIgnoreCase("Liberty", "001"))
            .thenReturn(List.of(existingChapter));

        // When
        ChapterAssignmentResult result = memberService.saveChapter(memberId, inputChapter);

        // Then
        assertEquals("Existing chapter assigned to member.", result.message());
        assertEquals(existingChapter.getChapterId(), result.chapter().getChapterId());
        verify(memberDao).save(member);
        verify(chapterDao, never()).save(any());
    }

    @Test
    void saveChapter_shouldCreateNewChapterIfNoMatch() {
        // Given
        Long memberId = 1L;
        Member member = new Member();
        member.setMemberId(memberId);
        MemberChapter inputChapter = new MemberChapter();
        inputChapter.setChapterName("Justice");
        inputChapter.setChapterNumber("002");

        Chapter newChapter = new Chapter();
        newChapter.setChapterId(200L);
        newChapter.setChapterName("Justice");
        newChapter.setChapterNumber("002");

        when(memberDao.findById(memberId)).thenReturn(Optional.of(member));
        when(chapterDao.findByChapterNameIgnoreCaseAndChapterNumberIgnoreCase("Justice", "002"))
            .thenReturn(List.of());
        when(chapterDao.save(any())).thenReturn(newChapter);

        // When
        ChapterAssignmentResult result = memberService.saveChapter(memberId, inputChapter);

        // Then
        assertEquals("New chapter created and assigned to member.", result.message());
        assertEquals(200L, result.chapter().getChapterId());
        verify(memberDao).save(member);
        verify(chapterDao).save(any());
    }
}
