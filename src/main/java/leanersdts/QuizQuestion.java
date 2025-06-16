/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;

public interface QuizQuestion {
    String getQuestion();
    void setQuestion(String question);
    String[] getOptions(); // Returns all options (expected to be 3)
    void setOptions(String[] options); // Sets all options
    int getCorrectAnswerIndex(); // Index of the correct answer in the array from getOptions()
    void setCorrectAnswer(int index);
    int getUserAnswerIndex();
    void setUserAnswerIndex(int index);
    boolean isValid(); // May need review based on how questions are constructed now
    void setHasImage(boolean hasImage);
    boolean hasImage();
    String getImageUrl();
    void setImageUrl(String imageUrl);
    List<Image> getImages(); // For client-side loaded JavaFX Image objects

    boolean isCorrect();
    void setCorrect(boolean correct);

    boolean isSkipped();
    void setSkipped(boolean skipped);
    String getCategory();
}



