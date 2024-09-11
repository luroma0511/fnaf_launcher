package candys3.Game.Objects.Functions;

import util.Time;

public class Twitch {
    private float frame;

    public boolean update(boolean hovered){
        if (!hovered) frame = 0;
        else frame = Time.increaseTimeValue(frame, Integer.MAX_VALUE, 22);
        return hovered;
    }

    public int getFrame() {
        return (int) frame % 2;
    }
}