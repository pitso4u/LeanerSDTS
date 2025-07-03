package leanersdts;

import java.time.LocalDate;
import java.time.LocalTime;

public class ProgressTracking {
    private String learnerId;
    private String moduleName;
    private int quizScore;
    private boolean passed;
    private String dateCompleted;
    private int totalQuestions;
    private String correctAnswers;
    private String wrongAnswers;
    private String skippedAnswers;
    private String timeTaken;
    private int totalCorrect;
    private int totalWrong;
    private int totalSkipped;
    private int totalTime;
    private int totalAttempts;
    
    // Category-specific fields for enhanced results display
    private int signsCorrect;
    private int signsTotal;
    private int rulesCorrect;
    private int rulesTotal;
    private int controlsCorrect;
    private int controlsTotal;

    public ProgressTracking(String learnerId, String moduleName, int quizScore, boolean passed,
                          String dateCompleted, int totalQuestions, String correctAnswers,
                          String wrongAnswers, String skippedAnswers, String timeTaken,
                          int totalCorrect, int totalWrong, int totalSkipped, int totalTime,
                          int totalAttempts) {
        this.learnerId = learnerId;
        this.moduleName = moduleName;
        this.quizScore = quizScore;
        this.passed = passed;
        this.dateCompleted = dateCompleted;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.skippedAnswers = skippedAnswers;
        this.timeTaken = timeTaken;
        this.totalCorrect = totalCorrect;
        this.totalWrong = totalWrong;
        this.totalSkipped = totalSkipped;
        this.totalTime = totalTime;
        this.totalAttempts = totalAttempts;
        
        // Initialize category fields to zero by default
        this.signsCorrect = 0;
        this.signsTotal = 0;
        this.rulesCorrect = 0;
        this.rulesTotal = 0;
        this.controlsCorrect = 0;
        this.controlsTotal = 0;
    }
    
    // Additional constructor with category breakdown
    public ProgressTracking(String learnerId, String moduleName, int quizScore, boolean passed,
                          String dateCompleted, int totalQuestions, String correctAnswers,
                          String wrongAnswers, String skippedAnswers, String timeTaken,
                          int totalCorrect, int totalWrong, int totalSkipped, int totalTime,
                          int totalAttempts, int signsCorrect, int signsTotal, 
                          int rulesCorrect, int rulesTotal, int controlsCorrect, int controlsTotal) {
        this(learnerId, moduleName, quizScore, passed, dateCompleted, totalQuestions,
             correctAnswers, wrongAnswers, skippedAnswers, timeTaken,
             totalCorrect, totalWrong, totalSkipped, totalTime, totalAttempts);
             
        this.signsCorrect = signsCorrect;
        this.signsTotal = signsTotal;
        this.rulesCorrect = rulesCorrect;
        this.rulesTotal = rulesTotal;
        this.controlsCorrect = controlsCorrect;
        this.controlsTotal = controlsTotal;
    }
    
    // Default constructor for JSON deserialization
    public ProgressTracking() {
        // Default values
        this.learnerId = "";
        this.moduleName = "";
        this.dateCompleted = LocalDate.now().toString();
        this.correctAnswers = "";
        this.wrongAnswers = "";
        this.skippedAnswers = "";
        this.timeTaken = "";
    }

    // Getters
    public String getLearnerId() { return learnerId; }
    public String getModuleName() { return moduleName; }
    public int getQuizScore() { return quizScore; }
    public boolean isPassed() { return passed; }
    public String getDateCompleted() { return dateCompleted; }
    public int getTotalQuestions() { return totalQuestions; }
    public String getCorrectAnswers() { return correctAnswers; }
    public String getWrongAnswers() { return wrongAnswers; }
    public String getSkippedAnswers() { return skippedAnswers; }
    public String getTimeTaken() { return timeTaken; }
    public int getTotalCorrect() { return totalCorrect; }
    public int getTotalWrong() { return totalWrong; }
    public int getTotalSkipped() { return totalSkipped; }
    public int getTotalTime() { return totalTime; }
    public int getTotalAttempts() { return totalAttempts; }
    
    // Category-specific getters
    public int getSignsCorrect() { return signsCorrect; }
    public int getSignsTotal() { return signsTotal; }
    public int getRulesCorrect() { return rulesCorrect; }
    public int getRulesTotal() { return rulesTotal; }
    public int getControlsCorrect() { return controlsCorrect; }
    public int getControlsTotal() { return controlsTotal; }
    
    // Alias for dateCompleted to match method name used in ResultsScreenController
    public String getDate() { return dateCompleted; }

    // Setters
    public void setLearnerId(String learnerId) { this.learnerId = learnerId; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public void setQuizScore(int quizScore) { this.quizScore = quizScore; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public void setDateCompleted(String dateCompleted) { this.dateCompleted = dateCompleted; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public void setCorrectAnswers(String correctAnswers) { this.correctAnswers = correctAnswers; }
    public void setWrongAnswers(String wrongAnswers) { this.wrongAnswers = wrongAnswers; }
    public void setSkippedAnswers(String skippedAnswers) { this.skippedAnswers = skippedAnswers; }
    public void setTimeTaken(String timeTaken) { this.timeTaken = timeTaken; }
    public void setTotalCorrect(int totalCorrect) { this.totalCorrect = totalCorrect; }
    public void setTotalWrong(int totalWrong) { this.totalWrong = totalWrong; }
    public void setTotalSkipped(int totalSkipped) { this.totalSkipped = totalSkipped; }
    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }
    public void setTotalAttempts(int totalAttempts) { this.totalAttempts = totalAttempts; }
    
    // Category-specific setters
    public void setSignsCorrect(int signsCorrect) { this.signsCorrect = signsCorrect; }
    public void setSignsTotal(int signsTotal) { this.signsTotal = signsTotal; }
    public void setRulesCorrect(int rulesCorrect) { this.rulesCorrect = rulesCorrect; }
    public void setRulesTotal(int rulesTotal) { this.rulesTotal = rulesTotal; }
    public void setControlsCorrect(int controlsCorrect) { this.controlsCorrect = controlsCorrect; }
    public void setControlsTotal(int controlsTotal) { this.controlsTotal = controlsTotal; }
    
    // Alias for setDateCompleted to match method name used in ResultsScreenController
    public void setDate(String date) { this.dateCompleted = date; }

    @Override
    public String toString() {
        return String.format(
            "ProgressTracking{learnerId='%s', moduleName='%s', quizScore=%d, " +
            "passed=%b, dateCompleted='%s', totalQuestions=%d, " +
            "correctAnswers='%s', wrongAnswers='%s', skippedAnswers='%s', " +
            "timeTaken='%s', totalCorrect=%d, totalWrong=%d, " +
            "totalSkipped=%d, totalTime=%d, totalAttempts=%d, " +
            "signsCorrect=%d, signsTotal=%d, rulesCorrect=%d, rulesTotal=%d, " +
            "controlsCorrect=%d, controlsTotal=%d}",
            learnerId, moduleName, quizScore, passed, dateCompleted,
            totalQuestions, correctAnswers, wrongAnswers, skippedAnswers,
            timeTaken, totalCorrect, totalWrong, totalSkipped, totalTime,
            totalAttempts, signsCorrect, signsTotal, rulesCorrect, rulesTotal,
            controlsCorrect, controlsTotal
        );
    }
}