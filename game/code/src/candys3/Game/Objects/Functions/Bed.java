package candys3.Game.Objects.Functions;

import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import util.SoundHandler;
import util.Time;

public class Bed {
    private float cooldown;
    private float killTimer;

    public void reset(float cooldown, float killTimer){
        this.cooldown = cooldown;
        this.killTimer = killTimer;
    }

    public void input(SoundHandler soundHandler, Player player, Room room, boolean imageHovered){
        if (!imageHovered || room.getState() != 1 || room.getFrame() != 0 || player.isBedSpot()) return;
        player.setBedSpot();
        soundHandler.play("bed");
    }

    public boolean update(Player player, Room room){
        if (player.isBedSpot() && room.getFrame() == 0) killTimer = Time.decreaseTimeValue(killTimer,0, 1);
        cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
        return cooldown == 0;
    }

    public float getCooldown() {
        return cooldown;
    }

    public boolean killTime(){
        return killTimer == 0;
    }
}
