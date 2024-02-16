package game.deluxe.state.Game.Objects;

import game.engine.Candys3Deluxe;
import game.engine.util.CameraManager;
import game.engine.util.Engine;
import game.engine.util.InputManager;

public class Player {
    private float x;
    private float y;
    private final int boundsX;
    private final int boundsY;
    private final int initX;
    private final int initY;
    private final float lookSpeed;

    public Player(float lookSpeed, int initX, int initY){
        this.lookSpeed = lookSpeed;
        this.initX = initX;
        this.initY = initY;
        boundsX = initX * 2;
        boundsY = initY * 2;
    }

    public void reset(){
        x = initX;
        y = initY;
    }

    public void update(Engine engine, InputManager inputManager){
        x += lookMechanic(engine, x, inputManager.getX(), (float) Candys3Deluxe.width / 5, 1, boundsX);
        y += lookMechanic(engine, y, inputManager.getY(), (float) Candys3Deluxe.height / 5, 2, boundsY);
    }

    public void adjustCamera(Flashlight flashlight, InputManager inputManager, CameraManager cameraManager){
        if (x != cameraManager.getX() || y != cameraManager.getY()) {
            cameraManager.translate(x - cameraManager.getX(), y - cameraManager.getY());
        }
        flashlight.setCoord(inputManager.getX() + x, inputManager.getY() + y);
    }

    private float lookMechanic(Engine engine, float position, float mouse, float offset, int multiplier, int bounds){
        float distance = 0;
        float limit = engine.convertValue(bounds * 0.75f * multiplier);
        if (mouse < offset * 2){
            distance = (mouse - offset * 2) * engine.convertValue(lookSpeed);
            if (position + distance < 0){
                distance = -position;
            } else if (distance < -limit){
                distance = -limit;
            }
        } else if (mouse > offset * 3){
            distance = (mouse - offset * 3) * engine.convertValue(lookSpeed);
            if (position + distance > bounds){
                distance = bounds - position;
            } else if (distance > limit){
                distance = limit;
            }
        }
        return distance;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
