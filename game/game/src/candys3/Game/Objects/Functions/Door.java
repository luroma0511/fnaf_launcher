package candys3.Game.Objects.Functions;

import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import util.SoundHandler;
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
    private boolean firstToEnter;

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

    public boolean input(SoundHandler soundHandler, Room room, boolean hovered){
        if (frame > 0 || room.getFrame() != 0 || room.getState() != 0 || !hovered) return false;
        cooldown = initialCooldown;
        soundHandler.play("spotted");
        resetKnock();
        return true;
    }

    public void pause(){
        if (cooldown != 0) return;
        frame = 13;
        cooldown = 0.01f;
        timer = Time.increaseTimeValue(timer, 5, 1);
        signal = false;
        resetKnock();
    }

    public void check(){
        if (timer < 1.5f) timer = 1.5f;
    }

    public void update(SoundHandler soundHandler, Player player, byte side){
        signal = cooldown == 0;
        if (!player.isFreeze()) cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
        if (cooldown != 0) {
            frame = Time.increaseTimeValue(frame, 13, 30);
            return;
        }
        frame = Time.decreaseTimeValue(frame, 0, 30);
        if (!player.isFreeze()) timer = Time.decreaseTimeValue(timer, 0, 1);
        if (!signal) {
            knockHard = timer <= 3;
            knockTimes = 3;
            return;
        }
        knockDelay = Time.decreaseTimeValue(knockDelay, 0, 1);
        if (knockDelay == 0 && knockTimes > 0){
            if (side == 0) playKnock(soundHandler, "Left");
            else if (side == 1) playKnock(soundHandler, "");
            else playKnock(soundHandler, "Right");
            knockTimes--;
            knockDelay = 0.125f + timer * 0.05f;
        }
    }

    public void playKnock(SoundHandler soundHandler, String path){
        if (knockHard) soundHandler.play("hard_knock" + path);
        else soundHandler.play("knock" + path);
    }

    public float getCooldown() {
        return cooldown;
    }

    public float getInitialCooldown() {
        return initialCooldown;
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