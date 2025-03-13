package dar.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import dar.member.entity.Patriot;

/**
 * Data Access Object (DAO) for Chapter entity. Extends JpaRepository to
 * provide CRUD operations for Chapter. JpaRepository provides built-in methods
 * like save, findById, findAll, deleteById, etc.
 */
public interface PatriotDao extends JpaRepository<Patriot, Long> {
}

