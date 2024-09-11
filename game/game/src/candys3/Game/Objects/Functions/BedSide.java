package candys3.Game.Objects.Functions;

import candys3.Game.Objects.Character.Rat;
import candys3.Game.Objects.Player;
import util.Time;

public class BedSide {
    private float frame;
    private int phase;
    private int targetPhase;
    private boolean retreat;
    private float delay;
    private float flashTime;
    private boolean signal;
    private boolean soundLock;
    private float limit;
    private float killTimer;
    private float hellCooldown;

    private final boolean hell;
    private final float initialKillTimer;
    private final float initialCooldown;
    private final float initialDelay;

    public BedSide(float delay, float cooldown, float killTimer, boolean hell){
        initialDelay = delay;
        initialCooldown = cooldown;
        initialKillTimer = killTimer;
        this.hell = hell;
    }

    public void reset(){
        delay = initialCooldown;
        retreat = false;
        phase = 0;
        targetPhase = 0;
        frame = 0;
        flashTime = 0;
        limit = 0;
        killTimer = 0;
    }

    public boolean input(Rat rat, boolean hovered, boolean cat){
        boolean sound = true;
        if (hovered){
            limit = 8;
            if ((int) frame == 8 || cat) flashTime = Time.increaseTimeValue(flashTime, 1.25f, 1);
            if (!cat && (int) frame >= 8) {
                if (rat != null && rat.getType() == 2 && rat.getHellCooldown() > 0){
                    rat.getAttack().setReactionTimer(0.85f);
                }
                if (killTimer == initialKillTimer) this.frame = Time.increaseTimeValue(this.frame, 44, 12);
                else if ((int) frame > 8) {
                    delay = Time.increaseTimeValue(delay, 6, 2);
                    this.frame = Time.decreaseTimeValue(this.frame, 8, 24);
                }
            }
        } else if (!retreat) {
            if (killTimer == initialKillTimer) frame = Time.increaseTimeValue(frame, 44, 24);
            flashTime = Time.decreaseTimeValue(flashTime, 0, 1);
            sound = false;
        }
        return sound;
    }

    public int update(Player player, boolean ratAttack, int side, boolean cat){
        if (cat) {
            signal = false;
            if (flashTime == 1.25f) retreat = true;
            else if (flashTime == 0) delay = Time.decreaseTimeValue(delay, 0, 1);
            if (delay == 0) {
                targetPhase++;
                if (targetPhase != 4) delay = initialDelay;
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
            if (hell){
                if (player.getSide() == side || (int) frame > 8) hellCooldown = 5;
                else if (ratAttack) hellCooldown = Time.decreaseTimeValue(hellCooldown, 0, 1);

                if (hellCooldown == 0){
                    hellCooldown = 5;
                    retreat = true;
                }
            }
            if (flashTime == 0 && ((hell && (player.getSide() == side || (int) frame > 8))
                    || (!hell && !player.isFreeze()))) {
                delay = Time.decreaseTimeValue(delay, 0, 1);
            } else if (flashTime == 1.25f && (int) frame == 8) {
                if (hell) killTimer = initialKillTimer;
                else retreat = true;
            }
            if (frame >= 8 && initialKillTimer != -1) killTimer = Time.increaseTimeValue(killTimer, initialKillTimer, 1);
            if (killTimer == initialKillTimer){
                delay = 0;
                limit = 0;
            } else if (delay == 0) {
                delay = initialDelay;
                if (frame == 0) limit = 8;
                else limit = frame + 17;
            }
            if (retreat) {
                frame = Time.decreaseTimeValue(frame, 0, 60);
                return side;
            }
            if (initialKillTimer == -1 || killTimer < initialKillTimer) frame = Time.increaseTimeValue(frame, limit, 30);
            if ((int) frame > 43) frame = 43;
        }
        return side;
    }

    public void setLimit(float limit) {
        this.limit = limit;
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

    public void setRetreat() {
        retreat = !retreat;
    }

    public float getDelay() {
        return delay;
    }

    public void setDelay(float delay) {
        this.delay = delay;
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
