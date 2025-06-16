package leanersdts;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;

public class ControlQuestion implements QuizQuestion {
    private String question;
    private String[] options; // Should store 3 options
    private int correctAnswer; // Index for the options array
    private int userAnswerIndex = -1;
    private boolean hasImage;
    private String imageUrl;
    private List<Image> images; // For client-side JavaFX Image objects
    private boolean correct = false;
    private boolean skipped = false;

    public ControlQuestion() {
        this.options = new String[3]; // Initialize to hold 3 options
        this.images = new ArrayList<>();
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String[] getOptions() {
        return options;
    }

    @Override
    public void setOptions(String[] options) {
        if (options != null && options.length == 3) {
            this.options = options;
        } else {
            // Handle error: log or throw exception for incorrect number of options
            System.err.println("ControlQuestion:setOptions - Incorrect number of options provided. Expected 3, got " + (options != null ? options.length : "null"));
            // Initialize with empty strings to prevent NullPointerExceptions later, or throw an IllegalArgumentException
            this.options = new String[]{"-error-", "-error-", "-error-"}; 
        }
    }

    @Override
    public int getCorrectAnswerIndex() {
        return correctAnswer;
    }

    @Override
    public void setCorrectAnswer(int index) {
        this.correctAnswer = index;
    }

    @Override
    public int getUserAnswerIndex() {
        return userAnswerIndex;
    }

    @Override
    public void setUserAnswerIndex(int index) {
        this.userAnswerIndex = index;
    }

    @Override
    public boolean isValid() {
        if (question == null || question.trim().isEmpty()) return false;
        if (options == null || options.length != 3) return false;
        for (String option : options) {
            if (option == null || option.trim().isEmpty()) return false;
        }
        return correctAnswer >= 0 && correctAnswer < options.length;
    }

    @Override
    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    @Override
    public boolean hasImage() {
        return hasImage;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public List<Image> getImages() {
        // This might load images on demand based on imageUrl if needed by client UI
        // For now, it just returns the list. Server provides URL, client handles loading.
        return images;
    }

    @Override
    public boolean isCorrect() {
        return correct;
    }

    @Override
    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    @Override
    public boolean isSkipped() {
        return skipped;
    }

    @Override
    public void setSkipped(boolean skipped) {
        this.skipped = skipped;
    }

    @Override
    public String getCategory() {
        return "controls";
    }
}