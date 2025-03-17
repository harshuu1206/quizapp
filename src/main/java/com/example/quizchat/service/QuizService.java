package com.example.quizchat.service;

import com.example.quizchat.model.*;
import com.example.quizchat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class QuizService {

    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuizService(
            SubjectRepository subjectRepository,
            QuestionRepository questionRepository,
            OptionRepository optionRepository,
            QuizAttemptRepository quizAttemptRepository,
            UserAnswerRepository userAnswerRepository,
            UserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.userRepository = userRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Question> getQuestionsBySubjectId(Long subjectId) {
        return questionRepository.findBySubjectId(subjectId);
    }

    public List<Question> getRandomQuestionsBySubjectId(Long subjectId, int limit) {
        return questionRepository.findRandomQuestionsBySubjectId(subjectId, limit);
    }

    public List<Option> getOptionsByQuestionId(Long questionId) {
        return optionRepository.findByQuestionId(questionId);
    }

    @Transactional
    public QuizAttempt saveQuizAttempt(Long userId, Long subjectId, int score, int totalQuestions, int timeTaken, List<UserAnswer> answers) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setUser(user);
        quizAttempt.setSubject(subject);
        quizAttempt.setScore(score);
        quizAttempt.setTotalQuestions(totalQuestions);
        quizAttempt.setTimeTaken(timeTaken);

        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttempt);

        for (UserAnswer answer : answers) {
            answer.setQuizAttempt(savedAttempt);
            userAnswerRepository.save(answer);
        }

        return savedAttempt;
    }

    public List<QuizAttempt> getQuizAttemptsByUserId(Long userId) {
        return quizAttemptRepository.findByUserId(userId);
    }

    public List<QuizAttempt> getQuizAttemptsByUserIdAndSubjectId(Long userId, Long subjectId) {
        return quizAttemptRepository.findByUserIdAndSubjectId(userId, subjectId);
    }

    public Optional<QuizAttempt> getQuizAttemptById(Long id) {
        return quizAttemptRepository.findById(id);
    }

    public List<UserAnswer> getUserAnswersByQuizAttemptId(Long quizAttemptId) {
        return userAnswerRepository.findByQuizAttemptId(quizAttemptId);
    }
    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
}