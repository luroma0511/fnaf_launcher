package candys3.Game.Objects;

import candys3.Game.Objects.Character.Cat;
import candys3.Game.Objects.Character.Rat;
import candys3.GameData;
import util.*;

public class Player {
    private final Flashlight flashlight;
    private final float shakeLimit;
    private final int initX;
    private final int initY;

    private float x;
    private float y;
    private float shakeX;
    private boolean shakePositive;
    private boolean scared;
    private boolean attack;
    private boolean bedSpot;
    private boolean freeze;
    private float overlayAlpha;
    private float overlayFade;
    private float buttonFade;
    private float snapCooldown;
    private byte side;
    private float flashPoints;
    private boolean jumpscare;

    private float purpleAlpha;
    private float purpleSpeed;

    private float blacknessAlpha;
    private float blacknessDelay;
    private int blacknessTimes;
    private float blacknessSpeed;

    public Player(){
        initX = 896;
        initY = 152;
        shakeLimit = 4;
        flashlight = new Flashlight();
    }

    public void reset(){
        flashlight.setCoord(0, 0);
        flashPoints = 0;
        x = initX;
        y = initY;
        side = 1;
        snapCooldown = 0;
        freeze = false;
        bedSpot = false;
        shakeX = 0;
        shakePositive = false;
        overlayFade = 0;
        overlayAlpha = 0;
        scared = false;
        attack = false;
        purpleAlpha = 0;
        purpleSpeed = 0;
        blacknessDelay = 0;
        blacknessAlpha = 1;
        blacknessSpeed = 2;
        blacknessTimes = 0;
        buttonFade = 0;
        jumpscare = false;
    }

    public void update(Window window, InputManager inputManager, Room room, Rat rat, Cat cat){
        if (purpleAlpha == 1 && !GameData.noJumpscares) freeze = true;
        if (!attack || GameData.freeScroll) {
            if (room.getFrame() == 0 && room.getState() != 2 && !freeze) {
                x += lookMechanic(x, inputManager.getX(), (float) window.width() / 4, 1, 3, room.getBoundsX(), shakeLimit);
                y += lookMechanic(y, inputManager.getY(), (float) window.height() / 3, 4, 2, room.getBoundsY(), 0);
            }
            snapCooldown = 0.75f;
            if (flashlight.getX() < 1024) side = 0;
            else if (flashlight.getX() >= 2048) side = 2;
            else side = 1;
        } else {
            float targetX;
            float targetY;
            if (side == 0){
                targetX = 16;
                targetY = 300;
            } else if (side == 1){
                targetX = initX;
                targetY = 232;
            } else {
                targetX = 1792 - shakeLimit;
                targetY = 304;
            }
            float distanceX = Time.convertValue(targetX - x) * 6;
            float limit = Time.convertValue(2000);
            if (distanceX > limit) distanceX = limit;
            else if (distanceX < -limit) distanceX = -limit;
            x += distanceX;
            float distanceY = Time.convertValue(targetY - y) * 6;
            y += distanceY;
        }
        if (snapCooldown == 0) {
            boolean ratCondition = rat != null
                    && ((cat != null && cat.getState() == 1
                    && rat.getState() == 0 && rat.getDoor().getCooldown() == 0 && rat.getSide() != cat.getSide())
                    || (rat.getState() == 1 && rat.getSide() != side));
            boolean catCondition = cat != null && cat.getState() == 1 && cat.getSide() != side;
            snapCooldown = 0.5f;
            if (inputManager.getX() < (float) window.width() * 0.2f && side != 0
                    && (ratCondition || catCondition)) side--;
            else if (inputManager.getX() > (float) window.width() * 0.8f && side != 2
                    && (ratCondition || catCondition)) side++;
            else snapCooldown = 0;
        } else snapCooldown = Time.decreaseTimeValue(snapCooldown, 0, 1);
        overlayAlpha = Time.decreaseTimeValue(overlayAlpha, 0, 1);
        float distance = Time.convertValue(70);
        if (scared) {
            if (shakePositive) {
                shakeX += distance;
                if (shakeX < shakeLimit) return;
                shakeX -= (shakeLimit - shakeX);
            } else {
                shakeX -= distance;
                if (shakeX > -shakeLimit) return;
                shakeX -= (shakeLimit + shakeX);
            }
            shakePositive = !shakePositive;
        } else if (shakeX != 0) shakeX = 0;
    }

    public void updateEffects(Room room, SoundHandler soundHandler){
        float mouseY = flashlight.getY();
        if (!attack) overlayFade = Time.decreaseTimeValue(overlayFade, 0, 0.2f);
        else if (overlayFade != 1) overlayFade = 1;
        if (overlayAlpha <= 0.5f && overlayFade > 0) overlayAlpha = 1;

        blacknessDelay = Time.decreaseTimeValue(blacknessDelay, 0, 1);
        if (blacknessAlpha < 1 && (blacknessDelay == 0 || blacknessTimes > 0)) blacknessAlpha = Time.increaseTimeValue(blacknessAlpha, 1, blacknessSpeed);
        else if (blacknessTimes > 0){
            blacknessTimes--;
            blacknessAlpha = 0;
        }

        if ((mouseY <= 256 || room.getState() != 0) && room.getFrame() == 0 && y == 0 && !freeze) buttonFade = Time.increaseTimeValue(buttonFade, 1, 4);
        else buttonFade = Time.decreaseTimeValue(buttonFade, 0, 4);

        if (GameData.hardCassette) {
            if (room.tapeMusic.isPlaying() || (jumpscare && blacknessTimes == 0)) {
                purpleSpeed = 0;
                purpleAlpha = Time.decreaseTimeValue(purpleAlpha, 0, 3);
            } else if (!freeze) {
                purpleAlpha = Time.increaseTimeValue(purpleAlpha, 1, purpleSpeed);
                purpleSpeed = Time.increaseTimeValue(purpleSpeed, 1, 0.00375f);
            }
            soundHandler.setAllSoundEffect(soundHandler.MUFFLE, 1 - purpleAlpha * 0.99f);
        }
    }

    public void adjustCamera(InputManager inputManager){
        CameraManager.move(getX(), y);
        if (!freeze) flashlight.setCoord(inputManager.getX() + x, inputManager.getY() + y);
    }

    private float lookMechanic(float position, float mouse, float offset, int multiplier, int rightOffset, int bounds, float shakeLimit){
        float distance = 0;
        float lookSpeed = 4.5f;
        float limit = Time.convertValue(1100 * multiplier);
        if (mouse < offset * 1){
            distance = (mouse - offset * 1) * Time.convertValue(lookSpeed);
            if (position + distance < shakeLimit) distance = -position + shakeLimit;
            else if (distance < -limit) distance = -limit;
        } else if (mouse > offset * rightOffset){
            distance = (mouse - offset * rightOffset) * Time.convertValue(lookSpeed);
            if (position + distance > bounds - shakeLimit) distance = bounds - shakeLimit - position;
            else if (distance > limit) distance = limit;
        }
        return distance;
    }

    public float getOverlayAlpha() {
        return overlayFade * overlayAlpha;
    }

    public int getBlacknessTimes() {
        return blacknessTimes;
    }

    public float getBlacknessDelay() {
        return blacknessDelay;
    }

    public void setBlacknessDelay(float blacknessDelay) {
        this.blacknessDelay = blacknessDelay;
    }

    public void setBlacknessSpeed(float blacknessSpeed) {
        this.blacknessSpeed = blacknessSpeed;
    }

    public void setBlacknessTimes(int blacknessTimes) {
        this.blacknessTimes = blacknessTimes;
    }

    public float getBlacknessAlpha() {
        return blacknessAlpha;
    }

    public float getPurpleAlpha() {
        return purpleAlpha;
    }

    public float getX() {
        return x + shakeX;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isFreeze() {
        return freeze;
    }

    public void setFreeze() {
        freeze = !freeze;
    }

    public boolean isBedSpot(){
        return bedSpot;
    }

    public void setBedSpot(){
        bedSpot = !bedSpot;
    }

    public boolean isScared() {
        return scared;
    }

    public void setScared() {
        scared = !scared;
    }

    public boolean isAttack() {
        return attack;
    }

    public void setAttack() {
        attack = !attack;
    }

    public float getButtonFade() {
        return buttonFade;
    }

    public byte getSide() {
        return side;
    }

    public String getFlashPoints() {
        return String.valueOf((int) flashPoints);
    }

    public void addFlashPoints(){
        flashPoints = Time.increaseTimeValue(flashPoints, Float.MAX_VALUE, 100);
    }

    public boolean isJumpscare() {
        return jumpscare;
    }

    public void setJumpscare() {
        blacknessTimes = 3;
        blacknessSpeed = 6;
        freeze = true;
        jumpscare = true;
    }

    public Flashlight getFlashlight() {
        return flashlight;
    }
}
