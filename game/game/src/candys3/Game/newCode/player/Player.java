package candys3.Game.newCode.player;

import core.Engine;
import util.InputManager;
import util.SoundHandler;
import util.Time;
import util.Window;

public class Player {
    public final Tape tape = new Tape();
    public float xPosition;
    public float yPosition;
    public float shakeXPosition;
    public float flashlightX;
    public float flashlightY;
    public boolean freeze;

    public int state;
    public float frame;
    public int frameTarget;
    public boolean framePositive;

    public void reset(InputManager inputManager){
        xPosition = 896;
        yPosition = 152;
        flashlightX = inputManager.getX() + xPosition;
        flashlightY = inputManager.getY() + yPosition;
        freeze = false;
        frame = 0;
        framePositive = false;
        state = 0;
    }

    public void update(Engine engine){
        var input = engine.appHandler.getInput();
        var soundHandler = engine.appHandler.soundHandler;
        var window = engine.appHandler.window;

        if (frame < 1 && !freeze){
            int boundsX = 1280;
            int boundsY = 720;
            if (state == 0){
                boundsX = 3072;
                boundsY = 1024;
            } else if (state == 1) boundsX = 2048;

            float distance = getDistance(input.getX(),
                    (float) window.width() / 4,
                    (float) (window.width() / 4) * 3,
                    boundsX, 4, 1250);
            xPosition += distance;

            distance = getDistance(input.getY(),
                    (float) window.height() / 3,
                    (float) (window.height() / 3) * 2,
                    boundsY, 0, 950);
            xPosition += distance;
        }

        float difference = Time.getDelta() * 25;
        if (framePositive){
            frame += difference;
            if (frame >= frameTarget){
                frame = frameTarget - 0.01f;
                framePositive = false;
            }
        } else {
            frame -= difference;
            if (frame <= 0) frame = 0;
        }
    }

    private float getDistance(float mouse, float leftBound, float rightBound, int length, int shakeLimit, float limit) {
        float distance = 0;
        float speed = Time.getDelta() * 5;
        limit *= Time.getDelta();
        if (mouse < leftBound){
            distance = speed * (mouse - leftBound);
            if (xPosition + distance < shakeLimit) distance = -xPosition + shakeLimit;
            else if (distance < -limit) distance = -limit;
        } else if (mouse > rightBound){
            distance = speed * (mouse - rightBound);
            if (xPosition + distance > length - shakeLimit) distance = length - xPosition - shakeLimit;
            else if (distance > limit) distance = limit;
        }
        return distance;
    }
}
