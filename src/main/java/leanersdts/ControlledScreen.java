/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

/**
 * Interface for screens that can be controlled by the ScreenManager.
 */
public interface ControlledScreen {
    /**
     * Sets the screen parent (ScreenManager) for this screen.
     * @param screenParent The ScreenManager instance
     */
    void setScreenParent(ScreenManager screenParent);

    /**
     * Method called when the screen is changed.
     */
    void runOnScreenChange();

    /**
     * Cleanup method to be called when the screen is unloaded.
     */
    void cleanup();
}
