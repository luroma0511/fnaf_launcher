package core.state.Game.Objects.Character.Attributes;

import util.Time;

public class Twitch {
    private boolean twitching;
    private float frame;

    public void update(boolean hovered){
        twitching = hovered;
        if (!twitching) frame = 0;
        else frame = Time.increaseTimeValue(frame, Integer.MAX_VALUE, 22);
    }

    public boolean isTwitching() {
        return twitching;
    }

    public int getFrame() {
        return (int) frame % 2;
    }
}