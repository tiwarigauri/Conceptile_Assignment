package com.example.quizapp.service;

import com.example.quizapp.dto.AnswerSubmissionRequest;
import com.example.quizapp.dto.QuestionResponse;
import com.example.quizapp.entity.Question;
import com.example.quizapp.entity.QuizSession;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class QuizService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizSessionRepository quizSessionRepository;

    public QuizSession startNewQuizSession() {
        QuizSession quizSession = new QuizSession();
        quizSession.setTotalQuestions(0);
        quizSession.setCorrectAnswers(0);
        quizSession.setIncorrectAnswers(0);
        return quizSessionRepository.save(quizSession);
    }

    public QuestionResponse getRandomQuestion() {
        long count = questionRepository.count();
        if (count == 0) {
            return null;
        }
        Random random = new Random();
        long randomId = random.nextInt((int) count) + 1;
        Optional<Question> question = questionRepository.findById(randomId);
        if (question.isPresent()) {
            Question q = question.get();
            QuestionResponse response = new QuestionResponse();
            response.setQuestionId(q.getId());
            response.setQuestionText(q.getQuestionText());
            response.setOptionA(q.getOptionA());
            response.setOptionB(q.getOptionB());
            response.setOptionC(q.getOptionC());
            response.setOptionD(q.getOptionD());
            return response;
        }
        return null;
    }

    public QuizSession submitAnswer(AnswerSubmissionRequest request) {
        Optional<QuizSession> sessionOptional = quizSessionRepository.findById(request.getSessionId());
        Optional<Question> questionOptional = questionRepository.findById(request.getQuestionId());
        if (sessionOptional.isPresent() && questionOptional.isPresent()) {
            QuizSession session = sessionOptional.get();
            Question question = questionOptional.get();
            session.setTotalQuestions(session.getTotalQuestions() + 1);
            if (question.getCorrectAnswer().equalsIgnoreCase(request.getSelectedAnswer())) {
                session.setCorrectAnswers(session.getCorrectAnswers() + 1);
            } else {
                session.setIncorrectAnswers(session.getIncorrectAnswers() + 1);
            }
            return quizSessionRepository.save(session);
        }
        return null;
    }
}