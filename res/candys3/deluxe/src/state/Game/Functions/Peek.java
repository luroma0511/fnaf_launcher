package state.Game.Functions;

import state.Game.Objects.Player;
import state.Game.Objects.Room;
import util.SoundManager;
import util.Time;

public class Peek {
    private float cooldown;
    private float flashTime;
    private float delay;

    public void reset(float cooldown, float flashTime, float delay){
        this.cooldown = cooldown;
        this.flashTime = flashTime;
        this.delay = delay;
    }

    public void input(Player player, Room room, boolean hovered, float delayTime){
        if (flashTime == 0 || cooldown == 0 || room.getFrame() != 0 || room.getState() != 0) return;
        if (!hovered) {
            if (!player.isAttack() && player.isScared()) player.setScared();
            cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
            return;
        }
        if (!player.isScared()) player.setScared();
        flashTime = Time.decreaseTimeValue(flashTime, 0, 1);
        if (flashTime != 0) return;
        SoundManager.play("thunder");
        player.setFreeze();
        player.setBlacknessDelay(delayTime);
        player.setBlacknessTimes(2);
        player.setBlacknessSpeed(6);
    }

    public boolean update(){
        if (flashTime == 0) delay = Time.decreaseTimeValue(delay, 0, 1);
        return delay == 0;
    }

    public boolean isKillTime(){
        return cooldown == 0;
    }
}
