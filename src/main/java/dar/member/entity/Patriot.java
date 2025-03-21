package dar.member.entity; // Defines the package for the entity class

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity // Marks this class as a JPA entity, meaning it will be mapped to a database table
@Data // Lombok annotation to generate boilerplate code like getters, setters, and toString()
@Table(name = "patriot", uniqueConstraints = @UniqueConstraint(
	    columnNames = {"patriot_first_name", "patriot_last_name", "patriot_rank_service", "patriot_state"}))
public class Patriot { 

    @Id // Specifies the primary key for the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the primary key using database identity column
    private Long patriotId; // Unique identifier for each patriot
    private String patriotFirstName; // Stores the patriot's first name
    private String patriotLastName;  // Stores the patriot's last name
    private String patriotState;     // Stores the patriot's email address
    private String patriotRankService;     // Stores the patriot's rank/service
   
    @EqualsAndHashCode.Exclude // Prevents infinite loops in bidirectional relationships
    @ToString.Exclude // Prevents infinite loops in toString()
    @ManyToMany(mappedBy = "patriot", cascade = CascadeType.PERSIST) 
    private Set<Member> member = new HashSet<>();}
