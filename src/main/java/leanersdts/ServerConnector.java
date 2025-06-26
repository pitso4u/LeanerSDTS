package leanersdts;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import javafx.scene.image.Image;
import org.json.JSONArray;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ServerConnector {
    private static final Logger LOGGER = Logger.getLogger(ServerConnector.class.getName());
    private static String baseUrl;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Random random = new Random();

    public static void setBaseUrl(String url) {
        LOGGER.info("[ServerConnector] Setting baseUrl to: " + url);
        baseUrl = url;
    }

    public static LoginData validateLogin(String username, String password) {
        LOGGER.info("[ServerConnector] validateLogin called. Current baseUrl: " + baseUrl);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            LOGGER.severe("[ServerConnector] Base URL is not set. Cannot validate login.");
            return null;
        }
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("username", username);
            requestBody.put("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "api/learners/authenticate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                JSONObject user = jsonResponse.getJSONObject("user");
                LoginData loginData = LoginData.getInstance();
                loginData.setLearnerId(user.getInt("learner_id"));
                loginData.setFullName(user.getString("full_name"));
                loginData.setEmail(user.getString("email"));
                return loginData;
            } else {
                LOGGER.log(Level.SEVERE, "Login failed with status code: {0}. Response: {1}", new Object[]{response.statusCode(), response.body()});
                return null;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
            return null;
        }
    }

    private static List<QuizQuestion> fetchQuestionsFromCategory(String category, int requestedCount) throws IOException, InterruptedException {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            LOGGER.severe("[ServerConnector] Base URL is not set. Cannot fetch questions for category: " + category);
            return Collections.emptyList();
        }
        // Server currently doesn't use 'count', so we fetch all and then client will select.
        // Pass 'requestedCount' in URL anyway for future server-side improvement.
        String apiUrl = baseUrl + "api/questions/" + category + "?count=" + requestedCount;
        LOGGER.info("[ServerConnector] Fetching questions from category: " + category + ". URL: " + apiUrl + " (Note: Server currently returns all questions from this category)");

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Accept", "application/json")
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<QuizQuestion> questions = new ArrayList<>();

        if (response.statusCode() == 200) {
            JSONArray jsonArray = new JSONArray(response.body());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonQuestion = jsonArray.getJSONObject(i);

                String questionText = jsonQuestion.optString("question_text", null);
                if (questionText == null || questionText.trim().isEmpty()) {
                     LOGGER.warning("[ServerConnector] Skipping question due to missing or empty question_text in category " + category + ": " + jsonQuestion.toString());
                    continue;
                }

                // Extract all 4 options and the 1-indexed correct option number
                // Robustly parse options, filtering out nulls
                List<String> allOptions = new ArrayList<>();
                String correctOptionText = null;
                int correctOptionNum = jsonQuestion.optInt("correct_option", -1);

                for (int j = 1; j <= 4; j++) {
                    String option = jsonQuestion.optString("option" + j, null);
                    if (option != null && !option.trim().isEmpty()) {
                        allOptions.add(option);
                        if (j == correctOptionNum) {
                            correctOptionText = option;
                        }
                    }
                }

                // Ensure we have a valid question structure
                if (allOptions.size() < 2 || correctOptionText == null) {
                    LOGGER.warning("[ServerConnector] Skipping question due to insufficient options or missing correct answer. Q: " + questionText);
                    continue;
                }

                // Shuffle and select up to 3 options, ensuring the correct one is included
                List<String> finalOptions = new ArrayList<>();
                finalOptions.add(correctOptionText);
                allOptions.remove(correctOptionText);
                Collections.shuffle(allOptions);

                for (int j = 0; j < allOptions.size() && finalOptions.size() < 3; j++) {
                    finalOptions.add(allOptions.get(j));
                }

                Collections.shuffle(finalOptions); // Shuffle the final list including the correct answer

                int newCorrectIndex = finalOptions.indexOf(correctOptionText);

                if (newCorrectIndex == -1) { // Should be impossible, but as a safeguard
                    LOGGER.severe("[ServerConnector] CRITICAL: Could not find correct answer in final options for Q: " + questionText);
                    continue;
                }

                QuizQuestion question;
                switch (category.toLowerCase()) {
                    case "controls": question = new ControlQuestion(); break;
                    case "signs": question = new SignQuestion(); break;
                    case "rules": question = new RuleQuestion(); break;
                    default:
                        LOGGER.warning("[ServerConnector] Unknown question category: " + category + ". Defaulting to generic QuizQuestion.");
                        question = new QuizQuestion() {
                            private String questionTextData;
                            private String[] optionsData;
                            private int correctAnswerIndexData;
                            private int userAnswerIndexData = -1; // Default to not answered
                            private boolean hasImageData;
                            private String imageUrlData;
                            private List<Image> imagesData = new ArrayList<>(); // Store images if loaded
                            private boolean correctData = false;
                            private boolean skippedData = false;

                            @Override
                            public String getQuestion() { return questionTextData; }
                            @Override
                            public void setQuestion(String q) { this.questionTextData = q; }
                            @Override
                            public String[] getOptions() { return optionsData; }
                            @Override
                            public void setOptions(String[] opts) { this.optionsData = opts; }
                            @Override
                            public int getCorrectAnswerIndex() { return correctAnswerIndexData; }
                            @Override
                            public void setCorrectAnswer(int index) { this.correctAnswerIndexData = index; }
                            @Override
                            public int getUserAnswerIndex() { return userAnswerIndexData; }
                            @Override
                            public void setUserAnswerIndex(int index) { this.userAnswerIndexData = index; }
                            @Override
                            public boolean isValid() {
                                boolean optionsValid = optionsData != null && optionsData.length > 0 &&
                                                       correctAnswerIndexData >= 0 && correctAnswerIndexData < optionsData.length;
                                return questionTextData != null && !questionTextData.isEmpty() && optionsValid;
                            }
                            @Override
                            public void setHasImage(boolean hi) { this.hasImageData = hi; }
                            @Override
                            public boolean hasImage() { return hasImageData; }
                            @Override
                            public String getImageUrl() { return imageUrlData; }
                            @Override
                            public void setImageUrl(String url) { this.imageUrlData = url; }
                            @Override
                            public List<Image> getImages() { return imagesData; }

                            @Override
                            public boolean isCorrect() { return correctData; }
                            @Override
                            public void setCorrect(boolean correct) { this.correctData = correct; }
                            @Override
                            public boolean isSkipped() { return skippedData; }
                            @Override
                            public void setSkipped(boolean skipped) { this.skippedData = skipped; }

                            @Override
                            public String getCategory() {
                                return category; // Return the 'category' variable from the enclosing scope
                            }
                        };
                        break;
                }
                question.setQuestion(questionText);
                question.setOptions(finalOptions.toArray(new String[0]));
                question.setCorrectAnswer(newCorrectIndex);

                boolean hasImage = jsonQuestion.optBoolean("has_image", false);
                question.setHasImage(hasImage);
                if (hasImage) {
                    String imageUrl = jsonQuestion.optString("image_url", null);
                     if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        question.setImageUrl(URI.create(baseUrl).resolve(imageUrl).toString());
                    } else {
                        LOGGER.warning("[ServerConnector] Question marked hasImage=true but imageUrl is missing/empty. Question: " + questionText);
                        question.setHasImage(false);
                    }
                }
                questions.add(question);
            }
        } else {
            LOGGER.log(Level.SEVERE, "[ServerConnector] Failed to get questions for category " + category + " with status code: {0}. Response: {1}", new Object[]{response.statusCode(), response.body()});
        }
        return questions;
    }

    public static List<QuizQuestion> getQuizQuestions() {
        LOGGER.info("[ServerConnector] getQuizQuestions called. Fetching structured quiz set. Current baseUrl: " + baseUrl);
        List<QuizQuestion> finalQuizQuestions = new ArrayList<>();

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            LOGGER.severe("[ServerConnector] Base URL is not set. Cannot fetch quiz questions.");
            return finalQuizQuestions;
        }

        final int signsCount = 28;
        final int rulesCount = 28;
        final int controlsCount = 8;

        try {
            // Fetch, shuffle, and select questions for each category
            List<QuizQuestion> signsQuestions = fetchQuestionsFromCategory("signs", signsCount);
            Collections.shuffle(signsQuestions, random);
            int signsToTake = Math.min(signsCount, signsQuestions.size());
            if (signsToTake > 0) {
                finalQuizQuestions.addAll(signsQuestions.subList(0, signsToTake));
            }

            List<QuizQuestion> rulesQuestions = fetchQuestionsFromCategory("rules", rulesCount);
            Collections.shuffle(rulesQuestions, random);
            int rulesToTake = Math.min(rulesCount, rulesQuestions.size());
            if (rulesToTake > 0) {
                finalQuizQuestions.addAll(rulesQuestions.subList(0, rulesToTake));
            }

            List<QuizQuestion> controlsQuestions = fetchQuestionsFromCategory("controls", controlsCount);
            Collections.shuffle(controlsQuestions, random);
            int controlsToTake = Math.min(controlsCount, controlsQuestions.size());
            if (controlsToTake > 0) {
                finalQuizQuestions.addAll(controlsQuestions.subList(0, controlsToTake));
            }

            // Shuffle the final combined list so the user gets a mixed quiz
            Collections.shuffle(finalQuizQuestions, random);

            LOGGER.info("[ServerConnector] Successfully created structured quiz. Total questions: " + finalQuizQuestions.size()
                + " (Signs: " + signsToTake
                + ", Rules: " + rulesToTake
                + ", Controls: " + controlsToTake + ")");

            return finalQuizQuestions;

        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "[ServerConnector] Error getting structured quiz questions", e);
            return Collections.emptyList();
        }
    }

    public static boolean saveQuizResults(java.util.List<QuizQuestion> questions, long timeTaken) {
        LOGGER.info("[ServerConnector] saveQuizResults called. Current baseUrl: " + baseUrl);
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            LOGGER.severe("[ServerConnector] Base URL is not set. Cannot save quiz results.");
            return false;
        }

        long score = questions.stream().filter(QuizQuestion::isCorrect).count();

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("learnerId", LoginData.getInstance().getLearnerId());
            requestBody.put("score", score);
            requestBody.put("totalQuestions", questions.size());
            requestBody.put("timeTaken", timeTaken);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "api/progress-tracking/quiz-attempt"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                LOGGER.info("[ServerConnector] Quiz results saved successfully. Status: " + response.statusCode());
                return true;
            }
            LOGGER.warning("[ServerConnector] Failed to save quiz results. Status: " + response.statusCode() + ", Body: " + response.body());
            return false;
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "[ServerConnector] Error saving quiz results", e);
            return false;
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}
 