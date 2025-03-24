package dar.member.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dar.member.entity.Chapter;

/**
 * Data Access Object (DAO) for Chapter entity. Extends JpaRepository to provide
 * CRUD operations for Chapter. JpaRepository provides built-in methods like
 * save, findById, findAll, deleteById, etc.
 */
public interface ChapterDao extends JpaRepository<Chapter, Long> {
	List<Chapter> findByChapterNameIgnoreCaseAndChapterNumberIgnoreCase(String chapterName, String chapterNumber);
}
