package leanersdts;

import java.util.ArrayList;
import java.util.List;
// We will need a JSON parsing library, e.g., org.json, Gson, or Jackson.
// For now, we'll placeholder the parsing logic.

public class QuizQuestionDatabase {

    // The server endpoint URL will be defined here once available.
    // private static final String QUIZ_API_URL = "http://localhost:YOUR_SERVER_PORT/api/v1/quiz/questions";

    public List<QuizQuestion> getRandomQuestions() {
        // Default number of questions to fetch for a quiz.
        // This could be made configurable if needed in the future.
        int numberOfQuestionsToFetch = 20;

        List<QuizQuestion> fetchedQuestions = ServerConnector.getRandomQuizQuestions(numberOfQuestionsToFetch);

        if (fetchedQuestions == null) {
            // ServerConnector.getRandomQuizQuestions logs errors internally.
            // Return an empty list to prevent NullPointerExceptions downstream.
            System.err.println("QuizQuestionDatabase: Failed to fetch questions from server. Returning empty list.");
            return new ArrayList<>(); // Return an empty list, not null
        }

        if (fetchedQuestions.isEmpty()) {
            System.err.println("QuizQuestionDatabase: No questions were returned from the server.");
        }
        
        // Server is expected to handle question variety and option shuffling.
        // Client-side shuffling (e.g., Collections.shuffle(fetchedQuestions);) can be added here if necessary,
        // but it's generally better if the server provides the final order.
        return fetchedQuestions;
    }
}


