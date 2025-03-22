package dar.member.controller.model;

import dar.member.controller.model.MemberData.MemberPatriot;

/**
 * A response wrapper for the result of assigning a patriot to a member.
 * 
 * @param message A status or success message describing the outcome.
 * @param patriot The assigned patriot data (wrapped in a MemberPatriot DTO).
 */
public record PatriotAssignmentResult(
    String message,
    MemberPatriot patriot
) {}
