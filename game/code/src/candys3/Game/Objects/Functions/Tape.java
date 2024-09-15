package candys3.Game.Objects.Functions;

import candys3.Game.Objects.Room;
import util.Time;

import java.util.Random;

public class Tape {
    private float frame;
    private float cooldown;
    private boolean leave;

    public boolean update(Room room){
        leave = room.getFrame() == 0 && room.getState() == 2;
        if (!leave && !room.isMusicPlaying() && room.notTapeWeasel()){
            if (frame != 0) {
                frame = 0;
                cooldown = -1;
            }
            return false;
        }
        if (!leave) cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
        else frame = Time.decreaseTimeValue(frame, 0, 27);
        if (cooldown == 0) {
            cooldown = -1;
            return true;
        }
        return false;
    }

    public void reset(Random random, int chance){
        leave = false;
        frame = 12.99f;
        cooldown = random.nextInt(chance) * 0.75f;
    }

    public int getFrame(){
        return (int) frame;
    }
}