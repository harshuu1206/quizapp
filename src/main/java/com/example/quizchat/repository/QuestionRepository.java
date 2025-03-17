package com.example.quizchat.repository;

import com.example.quizchat.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubjectId(Long subjectId);

    @Query(value = "SELECT * FROM questions WHERE subject_id = :subjectId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsBySubjectId(@Param("subjectId") Long subjectId, @Param("limit") int limit);
}
