/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

/**
 *
 * @author pitso
 */
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class AdjustOptionsButtonFontSize {

    public static void adjustFontSize(List<Button> optionButtons, String[] options) {
        int maxCharacters = 150; // Adjust as needed

        for (int i = 0; i < optionButtons.size(); i++) {
            Button button = optionButtons.get(i);
            String optionText = options[i];

            // Calculate font size based on the length of the option text
            double fontSize = calculateFontSize(optionText, button.getFont(), maxCharacters);

            button.setFont(new Font(fontSize));
            button.setText(optionText);  // Set the option text on the button
        }
    }

    private static double calculateFontSize(String text, Font font, int maxCharacters) {
        Text tempText = new Text(text);
        tempText.setFont(font);

        double maxWidth = calculateMaxWidth(text, font, maxCharacters);
        double currentWidth = tempText.getBoundsInLocal().getWidth();
        double fontSize = font.getSize();

        while (currentWidth > maxWidth && text.length() > maxCharacters) {
            fontSize -= 1;
            tempText.setFont(new Font(fontSize));
            currentWidth = tempText.getBoundsInLocal().getWidth();
        }

        return fontSize;
    }

    private static double calculateMaxWidth(String text, Font font, int maxCharacters) {
        if (text == null || text.isEmpty()) {
            return 0.0; // or return a default value if appropriate
        }

        Text tempText = new Text(text);
        tempText.setFont(font);
        double averageCharWidth = tempText.getBoundsInLocal().getWidth() / text.length();
        return averageCharWidth * maxCharacters;
    }
}
