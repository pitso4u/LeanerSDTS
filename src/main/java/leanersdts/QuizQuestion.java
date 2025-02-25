/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.leanersdts;

import javafx.scene.image.Image;
import java.util.List;

public interface QuizQuestion {
    String getQuestionText();

    String[] getOptions();

    int getCorrectOption();

    List<Image> getImages();
    
    int getUserAnswerIndex();

    void setUserAnswerIndex(int userAnswerIndex);

    boolean hasImage();
}



