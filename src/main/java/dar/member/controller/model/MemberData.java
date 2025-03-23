package dar.member.controller.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import dar.member.entity.Chapter;
import dar.member.entity.Member;
import dar.member.entity.Patriot;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO (Data Transfer Object) for Member entity. Encapsulates member details
 * along with associated patriot and chapter.
 */
@Data
@NoArgsConstructor
public class MemberData {

	private Long memberId;
	private String memberFirstName;
	private String memberLastName;
	private String memberAddress;
	private String memberCity;
	private String memberState;
	private String memberZip;
	private String memberPhone;
	private String memberEmail;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // ✅ Ensures correct JSON format
	private LocalDate memberBirthday;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate memberJoinDate;

	private String memberOffice;

	@ToString.Exclude // ✅ Prevents infinite loops in logs/debugging
	private Set<MemberPatriot> patriot = new HashSet<>();

	@ToString.Exclude // ✅ Prevents infinite loops in logs/debugging
	private Set<MemberChapter> chapter = new HashSet<>();

	/**
	 * Constructs a MemberData object from a Member entity.
	 */
	public MemberData(Member member) {
		memberId = member.getMemberId();
		memberFirstName = member.getMemberFirstName();
		memberLastName = member.getMemberLastName();
		memberAddress = member.getMemberAddress();
		memberCity = member.getMemberCity();
		memberState = member.getMemberState();
		memberZip = member.getMemberZip();
		memberPhone = member.getMemberPhone();
		memberEmail = member.getMemberEmail();
		memberBirthday = member.getMemberBirthday();
		memberJoinDate = member.getMemberJoinDate();
		memberOffice = member.getMemberOffice();

		// Converts associated Patriot entities to DTO representations
		for (Patriot p : member.getPatriot()) {
			patriot.add(new MemberPatriot(p));
		}

		Chapter c = member.getChapter();
		if (c != null) {
			chapter.add(new MemberChapter(c));
		}
	}

	/**
	 * DTO for Patriot entity associated with a Member.
	 */
	@Data
	@NoArgsConstructor
	public static class MemberPatriot {
		private Long patriotId;
		private String patriotFirstName;
		private String patriotLastName;
		private String patriotState;
		private String patriotRankService;

		public MemberPatriot(Patriot patriot) {
			this.patriotId = patriot.getPatriotId();
			this.patriotFirstName = patriot.getPatriotFirstName();
			this.patriotLastName = patriot.getPatriotLastName();
			this.patriotState = patriot.getPatriotState();
			this.patriotRankService = patriot.getPatriotRankService();
		}
	}

	/**
	 * DTO for Chapter entity associated with a Member.
	 */
	@Data
	@NoArgsConstructor
	public static class MemberChapter {
		private Long chapterId;
		private String chapterName;
		private String chapterNumber;

		public MemberChapter(Chapter chapter) {
			this.chapterId = chapter.getChapterId();
			this.chapterName = chapter.getChapterName();
			this.chapterNumber = chapter.getChapterNumber();
		}
	}
}
