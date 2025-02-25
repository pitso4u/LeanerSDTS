package main.java.leanersdts;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

public class ControlQuestion implements QuizQuestion {
    private String questionText;
    private String[] options;
    private int correctOption;
    private String imageUrl;
    private boolean hasImage;
    private int userAnswerIndex = -1; // Initialize with -1 to indicate no answer

    public ControlQuestion(String questionText, String[] options, int correctOption, String imageUrl, boolean hasImage) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.imageUrl = imageUrl;
        this.hasImage = hasImage;
    }

    @Override
    public String getQuestionText() {
        return questionText;
    }

    @Override
    public String[] getOptions() {
        return options;
    }

    @Override
    public int getCorrectOption() {
        return correctOption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean hasImage() {
        return hasImage;
    }

    public int getUserAnswerIndex() {
        return userAnswerIndex;
    }

    public void setUserAnswerIndex(int userAnswerIndex) {
        this.userAnswerIndex = userAnswerIndex;
    }

    @Override
    public List<Image> getImages() {
        List<Image> images = new ArrayList<>();
        String imageUrl = getImageUrl();

        if (imageUrl != null) {
            try {
                File imageFile = new File(imageUrl);
                String fileUrl = imageFile.toURI().toURL().toString();
                images.add(new Image(fileUrl));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return images;
    }
}