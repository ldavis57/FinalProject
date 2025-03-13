package dar.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import dar.member.entity.Member;

/**
 * Data Access Object (DAO) for Member entity. Extends JpaRepository to
 * provide CRUD operations for Member. JpaRepository provides built-in methods
 * like save, findById, findAll, deleteById, etc.
 */
public interface MemberDao extends JpaRepository<Member, Long> {
}
