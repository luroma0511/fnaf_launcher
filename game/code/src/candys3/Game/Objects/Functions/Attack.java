package candys3.Game.Objects.Functions;

import candys3.Game.Objects.Character.Rat;
import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import util.SoundHandler;
import util.Time;

import java.util.Random;

public class Attack {
    private boolean attack;
    private float killTimer;
    private float reactionTimer;
    private int limit;
    private boolean moved;
    private float flashTime;
    private boolean skip;
    private boolean signal;
    private int moves;
    private byte teleports;
    private float moveFrame;
    private byte position;
    private byte prevPosition;
    private String audio;
    private float moveSpeed;

    public void reset(float flashTime, int moves, float killTimer, byte limit, byte teleports, byte position, float moveSpeed) {
        this.killTimer = killTimer;
        reactionTimer = 0.5f;
        this.flashTime = flashTime;
        this.moves = moves;
        this.limit = limit;
        this.teleports = teleports;
        this.position = position;
        prevPosition = position;
        this.moveSpeed = moveSpeed;
        skip = false;
        signal = false;
        moveFrame = 0;
        moved = true;
        audio = null;
        attack = false;
    }

    public boolean input(SoundHandler soundHandler, Player player, Room room, boolean imageHovered, boolean hovered, int audioID, boolean hell){
        boolean looking = room.getFrame() == 0 && room.getState() == 0;
        if (!attack && moves > 0 && looking && imageHovered){
            if (!player.isScared()) player.setScared();
            if (!player.isAttack()) player.setAttack();
            playAudio(soundHandler, audioID);
            if (hell) killTimer = 1.75f;
            else killTimer = 1;
            attack = true;
        }

        if (hovered && looking) {
            if (skip) {
                skip = false;
                return true;
            }
            if (attack) {
                if (reactionTimer <= 0.25f) reactionTimer = Time.increaseTimeValue(reactionTimer, 0.25f, 0.5f);
                killTimer = Time.increaseTimeValue(killTimer, 2, 0.75f);
                flashTime = Time.decreaseTimeValue(flashTime, 0, 1);
                player.addFlashPoints();
            }
            return flashTime == 0 && moves > 0;
        } else if ((flashTime > 0 || moves > 0) && (int) moveFrame == 0) {
            reactionTimer = Time.decreaseTimeValue(reactionTimer, 0, 1);
            if (reactionTimer <= 0.375f) killTimer = Time.decreaseTimeValue(killTimer, 0, 1);
        }
        return false;
    }

    public boolean update(SoundHandler soundHandler, Random random, byte id, boolean scared, float pitchSpeed){
        if (scared){
            float pitch = Time.increaseTimeValue(soundHandler.getSoundEffect(soundHandler.PITCH, audio), 2, pitchSpeed);
            soundHandler.setSoundEffect(soundHandler.PITCH, audio, pitch);
        }

        if (moves == 0 && moveFrame == 0){
            if (flashTime != 0) return false;
            attack = teleports != 0;
            return true;
        }

        if (moveFrame != 0){
            moveFrame = Time.decreaseTimeValue(moveFrame, 0, moveSpeed);
            if (moveFrame != 0) return false;
            prevPosition = position;
            moved = true;
        }
        else if (flashTime == 0 && !skip){
            moveFrame = 2.99f;
            moves--;
            reactionTimer = 0.35f;
            soundHandler.play("dodge");
            signal = true;
            movePosition(random, id);
        }
        return false;
    }

    public byte dodgeUpdate(SoundHandler soundHandler, Random random, Rat rat, Player player, byte side, int limit, boolean hell){
        if (skip) skip = false;
        this.limit = limit;
        setPosition((byte) 0);
        teleports--;
        reactionTimer = 1;
        if (hell) killTimer = 1.75f;
        else killTimer = 1;
        setMoved();
        player.setBlacknessTimes(1);
        if (side == 0 || side == 2) side = 1;
        else {
            if (rat == null || rat.getState() != 0 || rat.getDoor().getFrame() == 13 || rat.getSide() == 1) side = (byte) (random.nextInt(2) * 2);
            else if (rat.getSide() == 0) side = 0;
            else side = 2;
        }
        playDodge(soundHandler, player, side);
        return side;
    }

    public void playDodge(SoundHandler soundHandler, Player player, byte side){
        if (side < player.getSide()) soundHandler.play("dodgeLeft");
        else if (side > player.getSide()) soundHandler.play("dodgeRight");
        else soundHandler.play("dodge");
    }

    public boolean notLimitCheck(Random random, boolean cat, byte type){
        if (!signal) return true;
        if (limit <= 0) return false;
        if (random.nextInt(5) == 2) {
            limit--;
            return true;
        }
        if (!cat || type != 0 || random.nextInt(10) != 4 || killTimer <= 0.5f) return false;
        skip = !skip;
//        attackTimer -= 0.25f;
//        if (attackTimer < 0) attackTimer = 0;
        limit--;
        return true;
    }

    private void playAudio(SoundHandler soundHandler, int id){
        if (id == 0 || id == 1) soundHandler.play("attack_begin");
        if (id == 0) audio = "attack";
        if (audio == null) return;
        soundHandler.play(audio);
        soundHandler.setSoundEffect(soundHandler.LOOP, audio, 1);
    }

    public void stopAudio(SoundHandler soundHandler){
        soundHandler.stop(audio);
        audio = null;
    }

    public byte getTeleports() {
        return teleports;
    }

    public float getKillTimer() {
        return killTimer;
    }

    public void setKillTimer(float killTimer){
        this.killTimer = killTimer;
    }

    public void setReactionTimer(float reactionTimer) {
        this.reactionTimer = reactionTimer;
    }

    public boolean notMoved() {
        return !moved;
    }

    public void setMoved() {
        moved = !moved;
    }

    public byte getPosition() {
        return position;
    }

    public void setFlashTime(float flashTime) {
        this.flashTime = flashTime;
    }

    public boolean isSignal() {
        return signal;
    }

    public void setSignal() {
        signal = !signal;
    }

    public int getLimit(){
        return limit;
    }

    public void increaseLimit() {
        limit++;
    }

    public boolean isAttack() {
        return attack;
    }

    public void setPosition(byte position){
        this.position = position;
        prevPosition = position;
    }

    private void movePosition(Random random, byte id) {
        if (id == 0){
            if (position == 0) position = (byte) (random.nextInt(2) + 1);
            else if (position == 1) position = (byte) (random.nextInt(2) * 2);
            else position = (byte) (random.nextInt(2));
        } else {
            if (position == 0 || position == 3) position = (byte) (random.nextInt(2) + 1);
            else if (position == 1 || position == 2){
                if (random.nextInt(10) == 0) position = 3;
                else if (position == 1) position = (byte) (random.nextInt(2) * 2);
                else position = (byte) (random.nextInt(2));
            }
        }
    }

    public int getRegion(int twitch){
        if ((int) moveFrame == 0){
            int number = position * 4;
            if (position == 3) number = 14;
            number += twitch;
            return number;
        }
        int number = 0;
        int moveFrame = (int) this.moveFrame;
        if (prevPosition == 0 && position == 1) number = 4 - moveFrame;
        else if (prevPosition == 1 && position == 0) number = 1 + moveFrame;
        else if (prevPosition == 1 && position == 2) number = 8 - moveFrame;
        else if (prevPosition == 2 && position == 1) number = 5 + moveFrame;
        else if (prevPosition == 2 && position == 0) number = 12 - moveFrame;
        else if (prevPosition == 0 && position == 2) number = 9 + moveFrame;
        else if (prevPosition == 1 && position == 3) number = 14 - moveFrame;
        else if (prevPosition == 3 && position == 1) number = 11 + moveFrame;
        else if (prevPosition == 3 && position == 2) number = 18 - moveFrame;
        else if (prevPosition == 2 && position == 3) number = 15 + moveFrame;
        return number;
    }

    public float getReactionTimer() {
        return reactionTimer;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public void setAudioPitch(SoundHandler soundHandler, float value) {
        float pitch = soundHandler.getSoundEffect(soundHandler.PITCH, audio);
        if (pitch != 0.9f) soundHandler.setSoundEffect(soundHandler.PITCH, audio, value);
    }
}
