/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.leanersdts;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class SanFranciscoFireworks extends Pane {

    private final AnimationTimer timer;
    private final Canvas canvas;
//    private final ImageView background;
    private final List<Particle> particles = new ArrayList<>();
    private final Paint[] colors;
    private int countDownTillNextFirework = 10;

public SanFranciscoFireworks() {
    // create a color palette of 180 colors
    colors = new Paint[181];
    colors[0] = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
    new Stop(0, Color.rgb(255, 255, 255, 1)),  // Adjust the alpha (transparency) here
    new Stop(0.2, Color.hsb(59, 0.38, 1)),
    new Stop(0.6, Color.hsb(59, 0.38, 1, 0.1)),
    new Stop(1, Color.hsb(59, 0.38, 1, 0))
);

// Modify other colors in the loop similarly
for (int h = 0; h < 360; h += 2) {
    colors[1 + (h / 2)] = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, 
        new Stop(0, Color.rgb(255, 255, 255, 0.8)),
        new Stop(0.2, Color.hsb(h, 1, 1)),
        new Stop(0.6, Color.hsb(h, 1, 1, 0.1)),
        new Stop(1, Color.hsb(h, 1, 1, 0))
    );

    }
    // create canvas
    canvas = new Canvas(1024, 500);

    canvas.setBlendMode(BlendMode.ADD);
    canvas.setEffect(new Reflection(0,0.4,0.15,0));
    
    // Exclude the background ImageView from being added to children
    getChildren().add(canvas);
    
    // create animation timer that will be called every frame
    timer = new AnimationTimer() {

        @Override public void handle(long now) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            // clear area with transparent black
            gc.setFill(Color.rgb(0, 0, 0, 0.2));
            gc.fillRect(0, 0, 1024, 708);
            // draw fireworks
            drawFireworks(gc);
            // countdown to launching the next firework
            if (countDownTillNextFirework == 0) {
                countDownTillNextFirework = 10 + (int)(Math.random()*30);
                fireParticle();
            }
            countDownTillNextFirework --;
        }
    };
}


    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    /**
     * Make resizable and keep background image proportions and center.
     */
    @Override
    protected void layoutChildren() {
        // final double w = 480.0;
        // final double h = 360.0;
        final double w = getWidth();
        final double h = getHeight();
        final double scale = Math.min(w / 1024d, h / 708d);
        final int width = (int) (1024 * scale);
        final int height = (int) (708 * scale);
        final int x = (int) ((w - width) / 2);
        final int y = (int) ((h - height) / 2);
//        background.relocate(x, y);
//        background.setFitWidth(width);
//        background.setFitHeight(height);
        canvas.relocate(x, y);
        canvas.setWidth(width);
        canvas.setHeight(height * 0.706);
    }
private void drawFireworks(GraphicsContext gc) {
    // Draw a dark background
    gc.setFill(Color.rgb(0, 0, 0, 0.2)); // You can adjust the color and transparency
    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

    Iterator<Particle> iter = particles.iterator();
    List<Particle> newParticles = new ArrayList<>();
    while (iter.hasNext()) {
        Particle firework = iter.next();
        // if the update returns true then particle has expired
        if (firework.update()) {
            // remove particle from those drawn
            iter.remove();
            // check if it should be exploded
            if (firework.shouldExplodeChildren) {
                if (firework.size == 9) {
                    explodeCircle(firework, newParticles);
                } else if (firework.size == 8) {
                    explodeSmallCircle(firework, newParticles);
                }
            }
        }
        firework.draw(gc);
    }
    particles.addAll(newParticles);
}


    private void fireParticle() {
        particles.add(new Particle(
                canvas.getWidth() * 0.5, canvas.getHeight() + 10,
                Math.random() * 5 - 2.5, 0,
                0, 150 + Math.random() * 100,
                colors[0], 9,
                false, true, true));
    }

    private void explodeCircle(Particle firework, List<Particle> newParticles) {
        final int count = 20 + (int) (60 * Math.random());
        final boolean shouldExplodeChildren = Math.random() > 0.5;
        final double angle = (Math.PI * 2) / count;
        final int color = (int) (Math.random() * colors.length);
        for (int i = count; i > 0; i--) {
            double randomVelocity = 4 + Math.random() * 4;
            double particleAngle = i * angle;
            newParticles.add(
                    new Particle(
                            firework.posX, firework.posY,
                            Math.cos(particleAngle) * randomVelocity, Math.sin(particleAngle) * randomVelocity,
                            0, 0,
                            colors[color],
                            8,
                            true, shouldExplodeChildren, true));
        }
    }

    private void explodeSmallCircle(Particle firework, List<Particle> newParticles) {
        final double angle = (Math.PI * 2) / 12;
        for (int count = 12; count > 0; count--) {
            double randomVelocity = 2 + Math.random() * 2;
            double particleAngle = count * angle;
            newParticles.add(
                    new Particle(
                            firework.posX, firework.posY,
                            Math.cos(particleAngle) * randomVelocity, Math.sin(particleAngle) * randomVelocity,
                            0, 0,
                            firework.color,
                            4,
                            true, false, false));
        }
    }
}
