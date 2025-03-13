package dar.member.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a Member entity in the system. This class is mapped to a database
 * table using JPA annotations.
 */
@Entity
@Data // Lombok generates getter, setter, equals, hashCode, and toString()
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	private String memberFirstName;
	private String memberLastName;
	private String memberAddress;
	private String memberCity;
	private String memberState;
	private String memberZip;
	private String memberPhone;
	private String memberOffice;
	private String memberEmail;

	@JsonProperty("memberBirthday") // ✅ Ensures JSON mapping
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	private LocalDate memberBirthday;

	@JsonProperty("memberJoinDate") // ✅ Ensures JSON mapping
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	private LocalDate memberJoinDate;

	/**
	 * Defines a many-to-many relationship with the Patriot entity.
	 * CascadeType.PERSIST ensures that when a Member is persisted, its associated
	 * Patriots are also persisted.
	 */
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "member_patriot", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "patriot_id"))
	@EqualsAndHashCode.Exclude // ✅ Prevents infinite loops in bidirectional relationships
	@ToString.Exclude // ✅ Prevents infinite loops when printing objects
	private Set<Patriot> patriot = new HashSet<>();

	/**
	 * Defines a one-to-many relationship with the Chapter entity. CascadeType.ALL
	 * ensures that all operations (persist, merge, remove, refresh, detach) are
	 * cascaded to Chapter. orphanRemoval = true means that if a Chapter is removed
	 * from this set, it is deleted from the database.
	 */
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	@EqualsAndHashCode.Exclude // ✅ Prevents infinite loops in bidirectional relationships
	@ToString.Exclude // ✅ Prevents infinite loops when printing objects
	private Set<Chapter> chapter = new HashSet<>();
}
