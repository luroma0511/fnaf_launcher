package game.deluxe.state.Game.Objects.Character.Attributes;

import game.engine.util.Engine;

public class Door {
    private boolean doorSignal;
    private float doorCooldown;
    private float getInCooldown;
    private float doorFrame;
    private float fpsSpeed;

    private byte leftDoorTimes;
    private byte middleDoorTimes;
    private byte rightDoorTimes;

    private final float initialDoorCooldown;
    private final float initialGetInCooldown;
    private final float initialDoorFrame;

    public Door(float doorCooldown, float getInCooldown, float doorFrame, float fpsSpeed){
        initialDoorCooldown = doorCooldown;
        initialGetInCooldown = getInCooldown;
        initialDoorFrame = doorFrame;
        this.fpsSpeed = fpsSpeed;
        reset();
    }

    public void reset(){
        doorCooldown = initialDoorCooldown;
        getInCooldown = initialGetInCooldown;
        doorFrame = initialDoorFrame;
        leftDoorTimes = 0;
        middleDoorTimes = 0;
        rightDoorTimes = 0;
    }

    public void input(Hitbox hitbox, byte side, float mx, float my){
        if (!hitbox.isHovered(mx, my)) return;
        doorCooldown = initialDoorCooldown;
        if (side == 0){
            leftDoorTimes++;
            middleDoorTimes = 0;
            rightDoorTimes = 0;
        } else if (side == 1){
            middleDoorTimes++;
            leftDoorTimes = 0;
            rightDoorTimes = 0;
        } else if (side == 2){
            rightDoorTimes++;
            leftDoorTimes = 0;
            middleDoorTimes = 0;
        }
    }

    public boolean update(Engine engine, Hitbox hitbox){
        doorSignal = doorCooldown == 0;
        doorCooldown = engine.decreaseTimeValue(doorCooldown, 0, 1);
        if (doorCooldown == 0){
            if (doorFrame > 0){
                doorFrame = engine.decreaseTimeValue(doorFrame, 0, fpsSpeed);
            }

            getInCooldown = engine.decreaseTimeValue(getInCooldown, 0, 1);
            if (getInCooldown == 0) {
                return true;
            }
        } else {
            if (doorFrame < initialDoorFrame) {
                doorFrame = engine.increaseTimeValue(doorFrame, initialDoorFrame, fpsSpeed);
            }
        }
        return false;
    }

    public byte getLeftDoorTimes() {
        return leftDoorTimes;
    }

    public byte getMiddleDoorTimes() {
        return middleDoorTimes;
    }

    public byte getRightDoorTimes() {
        return rightDoorTimes;
    }

    public boolean isDoorSignal() {
        return doorSignal;
    }

    public float getDoorCooldown() {
        return doorCooldown;
    }
}
