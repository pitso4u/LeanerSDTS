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

    // API endpoints
    private static final String API_LEARNER_PROFILE = "api/learners/profile/";
    private static final String API_LEARNER_SCHEDULE = "api/learners/schedule/";
    private static final String API_TEST_HISTORY = "api/progress-tracking/history/";

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

    /**
     * Fetches the learner profile information from the server
     * @return JSONObject containing learner profile data or null if failed
     */
    public static JSONObject getLearnerProfile() {
        LOGGER.info("[ServerConnector] Fetching learner profile");
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            LOGGER.severe("[ServerConnector] Base URL is not set. Cannot fetch learner profile.");
            return null;
        }
        
        LoginData loginData = LoginData.getInstance();
        if (loginData == null || loginData.getLearnerId() <= 0) {
            LOGGER.severe("[ServerConnector] No valid login data available. Cannot fetch learner profile.");
            return null;
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + API_LEARNER_PROFILE + loginData.getLearnerId()))
                .header("Accept", "application/json")
                .GET()
                .build();
                
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return new JSONObject(response.body());
            } else {
                LOGGER.log(Level.SEVERE, "Failed to fetch learner profile with status code: {0}. Response: {1}", 
                    new Object[]{response.statusCode(), response.body()});
                return null;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error fetching learner profile", e);
            return null;
        }
    }
    
    /**
     * Fetches the learner's next scheduled test date
     * @return String containing the next test date or null if failed
     */
    public static String getLearnerNextSchedule() {
        LOGGER.info("[ServerConnector] Fetching learner schedule");
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            LOGGER.severe("[ServerConnector] Base URL is not set. Cannot fetch learner schedule.");
            return null;
        }
        
        LoginData loginData = LoginData.getInstance();
        if (loginData == null || loginData.getLearnerId() <= 0) {
            LOGGER.severe("[ServerConnector] No valid login data available. Cannot fetch learner schedule.");
            return null;
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + API_LEARNER_SCHEDULE + loginData.getLearnerId()))
                .header("Accept", "application/json")
                .GET()
                .build();
                
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                if (jsonResponse.has("next_test_date")) {
                    return jsonResponse.getString("next_test_date");
                } else {
                    LOGGER.warning("[ServerConnector] No next_test_date field in schedule response");
                    return null;
                }
            } else {
                LOGGER.log(Level.SEVERE, "Failed to fetch learner schedule with status code: {0}. Response: {1}", 
                    new Object[]{response.statusCode(), response.body()});
                return null;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error fetching learner schedule", e);
            return null;
        }
    }
    
    /**
     * Fetches the learner's test history
     * @return List of ProgressTracking objects or empty list if failed
     */
    public static List<ProgressTracking> getTestHistory() {
        LOGGER.info("[ServerConnector] Fetching test history");
        List<ProgressTracking> history = new ArrayList<>();
        
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            LOGGER.severe("[ServerConnector] Base URL is not set. Cannot fetch test history.");
            return history;
        }
        
        LoginData loginData = LoginData.getInstance();
        if (loginData == null || loginData.getLearnerId() <= 0) {
            LOGGER.severe("[ServerConnector] No valid login data available. Cannot fetch test history.");
            return history;
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + API_TEST_HISTORY + loginData.getLearnerId()))
                .header("Accept", "application/json")
                .GET()
                .build();
                
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JSONArray jsonArray = new JSONArray(response.body());
                
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject entry = jsonArray.getJSONObject(i);
                    ProgressTracking progress = new ProgressTracking();
                    
                    // Parse the JSON data into ProgressTracking object
                    progress.setLearnerId(String.valueOf(entry.optInt("learner_id", 0)));
                    progress.setModuleName(entry.optString("module_name", "Unknown"));
                    progress.setQuizScore(entry.optInt("score", 0));
                    progress.setTotalQuestions(entry.optInt("total_questions", 0));
                    progress.setPassed(entry.optBoolean("passed", false));
                    progress.setDate(entry.optString("date", "Unknown"));
                    
                    // Parse category details if available
                    if (entry.has("category_details")) {
                        JSONObject details = entry.getJSONObject("category_details");
                        
                        if (details.has("signs")) {
                            JSONObject signs = details.getJSONObject("signs");
                            progress.setSignsCorrect(signs.optInt("correct", 0));
                            progress.setSignsTotal(signs.optInt("total", 0));
                        }
                        
                        if (details.has("rules")) {
                            JSONObject rules = details.getJSONObject("rules");
                            progress.setRulesCorrect(rules.optInt("correct", 0));
                            progress.setRulesTotal(rules.optInt("total", 0));
                        }
                        
                        if (details.has("controls")) {
                            JSONObject controls = details.getJSONObject("controls");
                            progress.setControlsCorrect(controls.optInt("correct", 0));
                            progress.setControlsTotal(controls.optInt("total", 0));
                        }
                    }
                    
                    history.add(progress);
                }
                
                LOGGER.info("[ServerConnector] Successfully fetched test history. Entries: " + history.size());
            } else {
                LOGGER.log(Level.SEVERE, "Failed to fetch test history with status code: {0}. Response: {1}", 
                    new Object[]{response.statusCode(), response.body()});
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error fetching test history", e);
        }
        
        return history;
    }
    
    /**
     * Saves detailed quiz results to the server including category breakdowns
     * @param questions List of answered quiz questions
     * @param timeTaken Time taken to complete the quiz in milliseconds
     * @return true if saved successfully, false otherwise
     */
    public static boolean saveDetailedQuizResults(List<QuizQuestion> questions, long timeTaken) {
        LOGGER.info("[ServerConnector] Saving detailed quiz results");
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            LOGGER.severe("[ServerConnector] Base URL is not set. Cannot save detailed quiz results.");
            return false;
        }
        
        LoginData loginData = LoginData.getInstance();
        if (loginData == null || loginData.getLearnerId() <= 0) {
            LOGGER.severe("[ServerConnector] No valid login data available. Cannot save detailed quiz results.");
            return false;
        }
        
        try {
            // Calculate overall score
            long score = questions.stream().filter(QuizQuestion::isCorrect).count();
            
            // Calculate category scores
            int signsCorrect = 0, signsTotal = 0;
            int rulesCorrect = 0, rulesTotal = 0;
            int controlsCorrect = 0, controlsTotal = 0;
            
            for (QuizQuestion question : questions) {
                String category = question.getCategory().toLowerCase();
                switch (category) {
                    case "signs":
                        signsTotal++;
                        if (question.isCorrect()) signsCorrect++;
                        break;
                    case "rules":
                        rulesTotal++;
                        if (question.isCorrect()) rulesCorrect++;
                        break;
                    case "controls":
                        controlsTotal++;
                        if (question.isCorrect()) controlsCorrect++;
                        break;
                }
            }
            
            // Create JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("learnerId", loginData.getLearnerId());
            requestBody.put("score", score);
            requestBody.put("totalQuestions", questions.size());
            requestBody.put("timeTaken", timeTaken);
            
            // Add category details
            JSONObject categoryDetails = new JSONObject();
            
            if (signsTotal > 0) {
                JSONObject signs = new JSONObject();
                signs.put("correct", signsCorrect);
                signs.put("total", signsTotal);
                categoryDetails.put("signs", signs);
            }
            
            if (rulesTotal > 0) {
                JSONObject rules = new JSONObject();
                rules.put("correct", rulesCorrect);
                rules.put("total", rulesTotal);
                categoryDetails.put("rules", rules);
            }
            
            if (controlsTotal > 0) {
                JSONObject controls = new JSONObject();
                controls.put("correct", controlsCorrect);
                controls.put("total", controlsTotal);
                categoryDetails.put("controls", controls);
            }
            
            requestBody.put("categoryDetails", categoryDetails);
            
            // Send the request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "api/progress-tracking/detailed-quiz-attempt"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();
                
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                LOGGER.info("[ServerConnector] Detailed quiz results saved successfully");
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Failed to save detailed quiz results. Status: {0}, Response: {1}", 
                    new Object[]{response.statusCode(), response.body()});
                return false;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error saving detailed quiz results", e);
            return false;
        }
    }
}