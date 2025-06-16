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

    @Override
    public String toString() {
        return String.format(
            "ProgressTracking{learnerId='%s', moduleName='%s', quizScore=%d, " +
            "passed=%b, dateCompleted='%s', totalQuestions=%d, " +
            "correctAnswers='%s', wrongAnswers='%s', skippedAnswers='%s', " +
            "timeTaken='%s', totalCorrect=%d, totalWrong=%d, " +
            "totalSkipped=%d, totalTime=%d, totalAttempts=%d}",
            learnerId, moduleName, quizScore, passed, dateCompleted,
            totalQuestions, correctAnswers, wrongAnswers, skippedAnswers,
            timeTaken, totalCorrect, totalWrong, totalSkipped, totalTime,
            totalAttempts
        );
    }
} 