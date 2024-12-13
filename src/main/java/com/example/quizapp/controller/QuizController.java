package com.example.quizapp.controller;

import com.example.quizapp.dto.AnswerSubmissionRequest;
import com.example.quizapp.dto.QuestionResponse;
import com.example.quizapp.entity.QuizSession;
import com.example.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/start")
    public QuizSession startNewQuiz() {
        return quizService.startNewQuizSession();
    }

    @GetMapping("/question")
    public QuestionResponse getRandomQuestion() {
        return quizService.getRandomQuestion();
    }

    @PostMapping("/submit")
    public QuizSession submitAnswer(@RequestBody AnswerSubmissionRequest request) {
        return quizService.submitAnswer(request);
    }
}