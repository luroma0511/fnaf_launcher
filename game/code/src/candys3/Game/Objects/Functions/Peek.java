package candys3.Game.Objects.Functions;

import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import candys3.GameData;
import util.SoundHandler;
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

    public boolean input(SoundHandler soundHandler, Player player, Room room, boolean hovered, float delayTime){
        if (flashTime == 0 || (cooldown == 0 && !GameData.noJumpscares) || room.getFrame() != 0 || room.getState() != 0) return false;
        if (!hovered) {
            if (!player.isAttack() && player.isScared()) player.setScared();
            cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
            return false;
        }
        if (!player.isScared()) player.setScared();
        flashTime = Time.decreaseTimeValue(flashTime, 0, 1);
        if (flashTime != 0) return false;
        soundHandler.play("thunder");
        player.setFreeze();
        player.setBlacknessDelay(delayTime);
        player.setBlacknessTimes(2);
        player.setBlacknessSpeed(6);
        return true;
    }

    public boolean update(){
        if (flashTime == 0) delay = Time.decreaseTimeValue(delay, 0, 1);
        return delay == 0;
    }

    public boolean notKillTime(){
        return cooldown != 0;
    }
}
