package dar.member.controller.model;

import dar.member.controller.model.MemberData.MemberChapter;

/**
 * A response wrapper for the result of assigning or creating a chapter.
 *
 * @param message A status or success message describing the outcome.
 * @param chapter The assigned or created chapter.
 */
public record ChapterAssignmentResult(
    String message,
    MemberChapter chapter
) {}
