package core.state.Game.Objects.Character.Attributes;

import core.state.Game.Objects.Player;
import core.state.Game.Objects.Room;
import util.SoundManager;
import util.Time;

public class Door {
    private float cooldown;
    private float timer;
    private float frame;
    private byte sameSide;
    private boolean signal;
    private byte knockTimes;
    private float knockDelay;
    private boolean knockHard;

    private final float initialCooldown;

    public Door(float cooldown){
        initialCooldown = cooldown;
    }

    public void reset(float timer){
        reset(timer, -1);
    }

    public void reset(float timer, float cooldown){
        if (cooldown != -1) this.cooldown = cooldown;
        else this.cooldown = initialCooldown;
        this.timer = timer;
        frame = 13;
        sameSide = 0;
        resetKnock();
    }

    public void resetKnock(){
        knockTimes = 0;
        knockDelay = 0;
    }

    public boolean input(Room room, boolean hovered){
        if (frame > 0 || room.getFrame() != 0 || room.getState() != 0 || !hovered) return false;
        cooldown = initialCooldown;
        SoundManager.play("spotted");
        resetKnock();
        return true;
    }

    public void update(byte side, Player player){
        signal = cooldown == 0;
        if (!player.isFreeze()) cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
        if (cooldown != 0) {
            frame = Time.increaseTimeValue(frame, 13, 30);
            return;
        }
        frame = Time.decreaseTimeValue(frame, 0, 30);
        timer = Time.decreaseTimeValue(timer, 0, 1);
        if (!signal) {
            knockHard = timer <= 3;
            knockTimes = 3;
            return;
        }
        knockDelay = Time.decreaseTimeValue(knockDelay, 0, 1);
        if (knockDelay == 0 && knockTimes > 0){
            if (side == 0) playKnock("Left");
            else if (side == 1) playKnock("");
            else playKnock("Right");
            knockTimes--;
            knockDelay = 0.125f + timer * 0.05f;
        }
    }

    public void playKnock(String path){
        if (knockHard) SoundManager.play("hard_knock" + path);
        else SoundManager.play("knock" + path);
    }

    public void pause(){
        signal = !signal;
        resetKnock();
        frame = 13;
        timer = Time.increaseTimeValue(timer, 5, 1);
        cooldown = Time.increaseTimeValue(cooldown, initialCooldown, 1);
    }

    public byte getSameSide() {
        return sameSide;
    }

    public void setSameSide(byte sameSide) {
        this.sameSide = sameSide;
    }

    public float getFrame() {
        return frame;
    }

    public boolean isSignal() {
        return !signal && cooldown == 0;
    }

    public boolean isTimeUp() {
        return timer == 0;
    }
}