/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

public class RuleQuestion implements QuizQuestion {
    private String questionText;
    private String[] options;
    private List<Integer> selectedOptions = new ArrayList<>();
    private int correctOption;
    private String imageUrl;
    private boolean hasImage;
private int userAnswerIndex = -1; // Initialize with -1 to indicate no answer

    public RuleQuestion(String questionText, String[] options, int correctOption, String imageUrl, boolean hasImage) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.imageUrl = imageUrl;
        this.hasImage = hasImage;
    }

    // Implement getters for the fields

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
    public void addSelectedOption(int optionIndex) {
        selectedOptions.add(optionIndex);
    }

    public void removeSelectedOption(int optionIndex) {
        selectedOptions.remove((Integer) optionIndex);
    }

    public List<Integer> getSelectedOptions() {
        return selectedOptions;
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


