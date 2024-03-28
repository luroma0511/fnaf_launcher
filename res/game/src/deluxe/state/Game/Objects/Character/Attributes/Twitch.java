package deluxe.state.Game.Objects.Character.Attributes;

import util.Time;

public class Twitch {
    private boolean twitching;
    private float frame;

    public void update(boolean hovered){
        twitching = hovered;
        if (!twitching) frame = 0;
        else {
            frame = Time.increaseTimeValue(frame, 2, 26);
            if (frame == 2) frame = 0;
        }
    }

    public boolean isTwitching() {
        return twitching;
    }

    public float getFrame() {
        return frame;
    }
}