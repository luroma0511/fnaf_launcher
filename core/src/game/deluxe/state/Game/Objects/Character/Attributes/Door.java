package game.deluxe.state.Game.Objects.Character.Attributes;

import java.util.Random;

import game.engine.util.Engine;

public class Door {
    private float cooldown;
    private float timer;
    private float frame;
    private byte sameSide;

    private final float intialCooldown;
    private final float initialTimer;

    public Door(float cooldown, float timer){
        intialCooldown = cooldown;
        initialTimer = timer;
        reset();
    }

    public void reset(){
        cooldown = intialCooldown;
        timer = initialTimer;
        frame = 13;
        sameSide = 0;
    }

    public void input(Hitbox hitbox, float mx, float my){
        if (frame > 0 || !hitbox.isHovered(mx, my)) return;
        cooldown = intialCooldown;
        hitbox.setCoord(0, 0);
    }

    public byte update(Engine engine, Hitbox hitbox, Random random, byte side){
        boolean signal = cooldown == 0;
        cooldown = engine.decreaseTimeValue(cooldown, 0, 1);
        if (cooldown != 0) {
            frame = engine.increaseTimeValue(frame, 13, 30);
            return side;
        }
        frame = engine.decreaseTimeValue(frame, 0, 30);
        timer = engine.decreaseTimeValue(timer, 0, 1);
        if (signal) return side;
        byte previousSide = side;
        if (sameSide == 3){
            sameSide = 0;
            int rand = random.nextInt(2);
            if (side == 0) side = (byte) (1 + rand);
            else if (side == 1) side = (byte) (2 * rand);
            else side = (byte) rand;
        } else {
            side = (byte) random.nextInt(3);
            if (previousSide == side) sameSide++;
        }
        if (side == 0) hitbox.setCoord(hitbox.getLeftDoorX(), hitbox.getLeftDoorY());
        else if (side == 1) hitbox.setCoord(hitbox.getMiddleDoorX(), hitbox.getMiddleDoorY());
        else hitbox.setCoord(hitbox.getRightDoorX(), hitbox.getRightDoorY());
        return side;
    }

    public float getFrame() {
        return frame;
    }

    public boolean isTimeUp() {
        return timer == 0;
    }
}