package state.Game.Functions;

import java.util.Random;

import util.Time;

public class BedSide {
    private float frame;
    private int phase;
    private int targetPhase;
    private boolean retreat;
    private float delay;
    private float flashTime;
    private float moveTime;
    private boolean signal;
    private boolean soundLock;
    private float limit;

    private final float initialDelay;

    public BedSide(float delay){
        initialDelay = delay;
    }

    public void reset(){
        delay = initialDelay;
        retreat = false;
        phase = 0;
        targetPhase = 0;
        frame = 0;
        flashTime = 0;
        limit = 0;
        moveTime = 1;
    }

    public boolean input(boolean hovered, boolean cat){
        boolean sound = true;
        if (hovered){
            frame = (int) frame;
            limit = 8;
            flashTime = Time.increaseTimeValue(flashTime, 1.25f, 1);
            if (!cat && frame > 8) {
                delay = Time.increaseTimeValue(delay, 6, 2);
                float difference = moveTime - Time.convertValue(24);
                while (difference <= 0) {
                    difference += 1;
                    frame--;
                }
                moveTime = difference;
            }
        } else if (!retreat) {
            flashTime = Time.decreaseTimeValue(flashTime, 0, 1);
            sound = false;
        }
        return sound;
    }

    public int update(Random random, int side, boolean cat){
        if (cat) {
            signal = false;
            if (flashTime == 1.25f) retreat = true;
            else if (flashTime == 0) delay = Time.decreaseTimeValue(delay, 0, 1);
            if (delay == 0) {
                delay = 6;
                if (targetPhase == 0) side = random.nextInt(2) * 2;
                targetPhase++;
            }
            if (retreat) {
                frame = Time.decreaseTimeValue(frame, 0, 60);
                return side;
            }
            float limit = 0;
            if (targetPhase == 1) limit = 23;
            else if (targetPhase == 2) limit = 42;
            else if (targetPhase == 3) limit = 57;
            if (frame < limit) {
                frame = Time.increaseTimeValue(frame, limit, 30);
                if (frame == limit) signal = true;
            }
        } else {
            if (flashTime == 0) delay = Time.decreaseTimeValue(delay, 0, 1);
            else if (flashTime == 1.25f && frame == 8) retreat = true;
            if (retreat) {
                frame = Time.decreaseTimeValue(frame, 0, 60);
                return side;
            }
            if (delay == 0) {
                delay = 6;
                if (frame == 0) {
                    side = 2;
//                    side = random.nextInt(2) * 2;
                    limit = 8;
                } else limit = frame + 17;
            }
            frame = Time.increaseTimeValue(frame, limit, 30);
        }
        return side;
    }

    public void setPhase(int phase){
        this.phase = phase;
    }

    public boolean isSignal() {
        return signal;
    }

    public int getPhase() {
        return phase;
    }

    public boolean endState(){
        return retreat && frame == 0;
    }

    public int getFrame(){
        return (int) frame;
    }

    public boolean getSoundLock(){
        return soundLock;
    }

    public void setSoundLock(){
        soundLock = !soundLock;
    }
}
