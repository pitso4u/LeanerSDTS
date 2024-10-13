/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package leanersdts;

/**
 *
 * @author Soetsang
 */
public interface ControlledScreen {
     public void setScreenParent(ScreenManager screenPage);
     public void runOnScreenChange();
     /**
     * Cleanup method to be called when the screen is unloaded.
     */
   public void cleanup();
}
