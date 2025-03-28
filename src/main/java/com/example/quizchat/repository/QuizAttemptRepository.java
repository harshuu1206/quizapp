package com.example.quizchat.repository;

import com.example.quizchat.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserId(Long userId);
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.subject.id = :subjectId")
    List<QuizAttempt> findByUserIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Long subjectId);
}
