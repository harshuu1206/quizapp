package com.example.quizchat.controller;

import com.example.quizchat.model.*;
import com.example.quizchat.repository.QuestionRepository;
import com.example.quizchat.repository.SubjectRepository;
import com.example.quizchat.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(quizService.getAllSubjects());
    }

    @GetMapping("/subjects/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return quizService.getSubjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/questions/subject/{subjectId}")
    public ResponseEntity<List<Question>> getQuestionsBySubjectId(@PathVariable Long subjectId) {
        return ResponseEntity.ok(quizService.getQuestionsBySubjectId(subjectId));
    }

    @GetMapping("/questions/random")
    public ResponseEntity<List<Question>> getRandomQuestions(
            @RequestParam Long subjectId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(quizService.getRandomQuestionsBySubjectId(subjectId, limit));
    }

    @GetMapping("/options/question/{questionId}")
    public ResponseEntity<List<Option>> getOptionsByQuestionId(@PathVariable Long questionId) {

        return ResponseEntity.ok(quizService.getOptionsByQuestionId(questionId));
    }

    @PostMapping("/attempts")
    public ResponseEntity<?> saveQuizAttempt(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long receivedSubjectId = Long.valueOf(request.get("subjectId").toString());

            // 🛠 Convert OpenTDB subject IDs to database subject IDs (Only if necessary)
            Map<Long, Long> subjectMapping = Map.of(
                    23L, 1L,  // OpenTDB "History" → DB "History" (ID: 1)
                    17L, 2L,  // OpenTDB "Math" → DB "Mathematics" (ID: 2)
                    19L, 3L   // OpenTDB "Science" → DB "English" (ID: 3)
            );

            Long subjectId = subjectMapping.getOrDefault(receivedSubjectId, receivedSubjectId); // Use mapped or same ID

            int score = Integer.parseInt(request.get("score").toString());
            int totalQuestions = Integer.parseInt(request.get("totalQuestions").toString());
            int timeTaken = Integer.parseInt(request.get("timeTaken").toString());

            List<Map<String, Object>> answersData = (List<Map<String, Object>>) request.get("answers");
            List<UserAnswer> answers = answersData.stream().map(answerData -> {
                UserAnswer answer = new UserAnswer();
                answer.setSelectedAnswer(answerData.get("selectedAnswer").toString());
                return answer;
            }).collect(Collectors.toList());

            QuizAttempt attempt = quizService.saveQuizAttempt(userId, subjectId, score, totalQuestions, timeTaken, answers);

            return ResponseEntity.ok(Map.of(
                    "message", "Quiz submitted successfully!",
                    "score", attempt.getScore(),
                    "totalQuestions", attempt.getTotalQuestions()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to save quiz attempt."));
        }
    }

    @GetMapping("/attempts/user/{userId}")
    public ResponseEntity<List<QuizAttempt>> getQuizAttemptsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(quizService.getQuizAttemptsByUserId(userId));
    }

    @GetMapping("/attempts/{id}")
    public ResponseEntity<QuizAttempt> getQuizAttemptById(@PathVariable Long id) {
        return quizService.getQuizAttemptById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/answers/attempt/{attemptId}")
    public ResponseEntity<List<UserAnswer>> getUserAnswersByQuizAttemptId(@PathVariable Long attemptId) {
        return ResponseEntity.ok(quizService.getUserAnswersByQuizAttemptId(attemptId));
    }

    @PostMapping("/subjects")
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        Subject savedSubject = quizService.createSubject(subject);
        return ResponseEntity.ok(savedSubject);
    }

    @PostMapping("/questions")
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question savedQuestion = quizService.createQuestion(question);
        return ResponseEntity.ok(savedQuestion);
    }

    @GetMapping("/results/{userId}")
    public ResponseEntity<?> getUserQuizResults(@PathVariable Long userId) {
        List<QuizAttempt> attempts = quizService.getQuizAttemptsByUserId(userId);
        if (attempts.isEmpty()) {
            return ResponseEntity.ok(Map.of("score", 0, "totalQuestions", 0, "username", "Unknown", "subject", "N/A"));
        }

        QuizAttempt latestAttempt = attempts.get(attempts.size() - 1); // ✅ Get latest attempt
        Optional<User> user = quizService.getUserById(userId); // Assuming user retrieval method exists
        Optional<Subject> subject = quizService.getSubjectById(latestAttempt.getSubjectId()); // Assuming subject retrieval

        return ResponseEntity.ok(Map.of(
                "username", user.map(User::getUsername).orElse("Unknown"),
                "score", latestAttempt.getScore(),
                "totalQuestions", latestAttempt.getTotalQuestions(),
                "subject", subject.map(Subject::getName).orElse("N/A") // ✅ Get subject name
        ));
    }
}
