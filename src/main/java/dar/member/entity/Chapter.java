package dar.member.entity; // Defines the package where this class belongs

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

/**
 * Represents an Chapter entity in member. This class is mapped
 * to a database table using JPA annotations.
 */
@Entity // Marks this class as a JPA entity, mapping it to a database table
@Data // Lombok annotation to automatically generate getter, setter, equals, hashCode,
		// and toString methods
public class Chapter {

	@Id // Marks this field as the primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// Specifies that the primary key will be auto-generated by the database using
	// an identity column
	private Long chapterId; // Unique identifier for an chapter
	private String chapterName; // First name of the chapter
	private String chapterNumber; // Last name of the chapter
	
	@OneToMany(mappedBy = "chapter")
	private List<Member> members = new ArrayList<>();

}
