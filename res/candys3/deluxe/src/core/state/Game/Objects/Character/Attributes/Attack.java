package core.state.Game.Objects.Character.Attributes;

import java.util.Random;

import core.state.Game.Objects.Player;
import core.state.Game.Objects.Room;
import util.SoundManager;
import util.Time;

public class Attack {
    private boolean attack;
    private float killTimer;
    private int limit;
    private boolean moved;
    private float flashTime;
    private boolean skip;
    private float delayMovement;
    private float initialDelayMovement;
    private float attackTimer;
    private byte teleports;
    private float moveFrame;
    private byte position;
    private byte prevPosition;
    private String audio;
    private float moveSpeed;

    public void reset(float flashTime, float delayMovement, float attackTimer, byte teleports, byte position, float moveSpeed) {
        killTimer = 3;
        this.flashTime = flashTime;
        this.delayMovement = delayMovement;
        this.initialDelayMovement = delayMovement;
        this.attackTimer = attackTimer;
        this.teleports = teleports;
        this.position = position;
        prevPosition = position;
        this.moveSpeed = moveSpeed;
        moveFrame = 0;
        moved = true;
        audio = null;
        attack = false;
    }

    public boolean input(Player player, Room room, boolean imageHovered, boolean hovered, int audioID){
        boolean looking = room.getFrame() == 0 && room.getState() == 0;
        if (!attack && attackTimer > 0 && looking && imageHovered){
            if (!player.isScared()) player.setScared();
            player.setAttack();
            playAudio(audioID);
            killTimer = 1;
            attack = true;
        }

        if (hovered && looking) {
            if (skip) {
                flashTime = 0;
                skip = false;
                return true;
            }
            if (attack) {
                killTimer = Time.increaseTimeValue(killTimer, 2, 1);
                attackTimer = Time.decreaseTimeValue(attackTimer, 0, 1);
            }
            flashTime = Time.decreaseTimeValue(flashTime, 0, 1);
            return flashTime == 0 && attackTimer > 0;
        } else if (flashTime > 0 || attackTimer > 0) killTimer = Time.decreaseTimeValue(killTimer, 0, 1);
        return false;
    }

    public boolean update(Random random, byte id, boolean scared, float pitchSpeed){
        if (scared){
            float pitch = Time.increaseTimeValue(SoundManager.getPitch(audio), 2, pitchSpeed);
            SoundManager.setPitch(audio, pitch);
        }

        if (attackTimer == 0 && moveFrame == 0){
            if (flashTime != 0) return false;
            else if (teleports != 0) return true;
            stopAudio();
            attack = false;
            return true;
        }

        if (moveFrame != 0){
            moveFrame = Time.decreaseTimeValue(moveFrame, 0, moveSpeed);
            if (moveFrame != 0) return false;
            prevPosition = position;
            moved = true;
        } else if (delayMovement > 0) delayMovement = Time.decreaseTimeValue(delayMovement, 0, 1);
        else if (flashTime == 0){
            moveFrame = 2.99f;
            delayMovement = initialDelayMovement;
            SoundManager.stop("twitch");
            SoundManager.play("dodge");
            movePosition(random, id);
        }
        return false;
    }

    private void playAudio(int id){
        if (id == 0 || id == 1) SoundManager.play("attack_begin");
        if (id == 0) audio = "attack";

        if (audio == null) return;
        SoundManager.play(audio);
        SoundManager.setLoop(audio, true);
    }

    private void stopAudio(){
        SoundManager.stop(audio);
        audio = null;
    }

    public byte getTeleports() {
        return teleports;
    }

    public void decreaseTeleports() {
        teleports--;
    }

    public float getKillTimer() {
        return killTimer;
    }

    public void setKillTimer(float killTimer){
        this.killTimer = killTimer;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved() {
        moved = !moved;
    }

    public byte getPosition() {
        return position;
    }

    public float getFlashTime() {
        return flashTime;
    }

    public void setFlashTime(float flashTime) {
        this.flashTime = flashTime;
    }

    public void setSkip(){
        skip = !skip;
    }

    public int getLimit(){
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setPosition(byte position){
        this.position = position;
        prevPosition = position;
    }

    private void movePosition(Random random, byte id) {
        byte chance = (byte) random.nextInt(2);
        if (id == 0){
            if (position == 0) position = (byte) (chance + 1);
            else if (position == 1) position = (byte) (chance * 2);
            else position = chance;
        } else {
            if (position == 0 || position == 3) position = (byte) (chance + 1);
            else if (random.nextInt(2) == 1) position = 3;
            else if (position == 1) position = (byte) (chance * 2);
            else position = chance;
        }
    }

    public int getRegion(Twitch twitch){
        if ((int) moveFrame == 0){
            int number = position * 4;
            if (position == 3) number = 14;
            if (twitch.isTwitching()) number += twitch.getFrame();
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

    public void setAttackTimer(float attackTimer) {
        this.attackTimer = attackTimer;
    }
}
