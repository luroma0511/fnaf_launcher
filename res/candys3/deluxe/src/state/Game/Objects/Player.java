package state.Game.Objects;

import core.Candys3Deluxe;
import data.GameData;
import util.CameraManager;
import util.Time;
import util.Window;

public class Player {
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
    private float hoverLock;
    private byte side;
    private boolean jumpscare;

    private float purpleAlpha;
    private float purpleSpeed;

    private float blacknessAlpha;
    private float blacknessDelay;
    private int blacknessTimes;
    private float blacknessSpeed;

    private final float shakeLimit;
    private final int initX;
    private final int initY;
    private final float lookSpeed;

    public Player(float lookSpeed, int initX, int initY){
        this.lookSpeed = lookSpeed;
        this.initX = initX;
        this.initY = initY;
        shakeLimit = 4f;
    }

    public void reset(){
        x = initX;
        y = initY;
        side = 1;
        hoverLock = 0;
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
        blacknessAlpha = 0;
        blacknessSpeed = 2;
        blacknessTimes = 0;
        jumpscare = false;
    }

    public void update(Window window, Flashlight flashlight, Room room){
        if (room.getFrame() == 0 && room.getState() != 2 && !freeze) {
            boolean leftHover = Candys3Deluxe.inputManager.getX() < (float) window.getWidth() * 0.1f;
            boolean rightHover = Candys3Deluxe.inputManager.getX() > (float) window.getWidth() * 0.9f;
            if (!attack || GameData.freeScroll) {
                x += lookMechanic(x, Candys3Deluxe.inputManager.getX(), (float) window.getWidth() / 3, 1, room.getBoundsX(), shakeLimit);
                y += lookMechanic(y, Candys3Deluxe.inputManager.getY(), (float) window.getHeight() / 3, 4, room.getBoundsY(), 0);
                if (flashlight.getX() < 1024) side = 0;
                else if (flashlight.getX() >= 2048) side = 2;
                else side = 1;
            } else {
                if (hoverLock == 0.375f) {
                    if (leftHover && side > 0) side--;
                    if (rightHover && side < 2) side++;
                }
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
                float distanceX = Time.convertValue(targetX - x) * 4;
                x += distanceX;
                float distanceY = Time.convertValue(targetY - y) * 4;
                y += distanceY;
            }
            if (hoverLock == 0 && (leftHover || rightHover)) hoverLock = 0.375f;
            else hoverLock = Time.decreaseTimeValue(hoverLock, 0, 1);
        }
        overlayAlpha = Time.decreaseTimeValue(overlayAlpha, 0, 1);
        float distance = Time.convertValue(70);
        float leftOver;
        while (scared) {
            if (shakePositive) {
                if (shakeX + distance < shakeLimit) {
                    shakeX += distance;
                    break;
                }
                leftOver = shakeLimit - shakeX;
                shakeX = shakeLimit;
            } else {
                if (shakeX - distance > -shakeLimit){
                    shakeX -= distance;
                    break;
                }
                leftOver = shakeLimit + shakeX;
                shakeX = -shakeLimit;
            }
            distance -= leftOver;
            shakePositive = !shakePositive;
        }
        if (!scared && shakeX != 0) shakeX = 0;
    }

    public void updateEffects(Room room){
        if (!attack) overlayFade = Time.decreaseTimeValue(overlayFade, 0, 0.2f);
        else if (overlayFade != 1) overlayFade = 1;
        if (overlayAlpha <= 0.5f && overlayFade > 0) overlayAlpha = 1;

        blacknessDelay = Time.decreaseTimeValue(blacknessDelay, 0, 1);
        if (blacknessAlpha < 1 && (blacknessDelay == 0 || blacknessTimes > 0)) blacknessAlpha = Time.increaseTimeValue(blacknessAlpha, 1, blacknessSpeed);
        else if (blacknessTimes > 0){
            blacknessTimes--;
            blacknessAlpha = 0;
        }

        if (room.getFrame() == 0 && y == 0 && !freeze) buttonFade = Time.increaseTimeValue(buttonFade, 1, 4);
        else buttonFade = Time.decreaseTimeValue(buttonFade, 0, 4);

        if (GameData.hardCassette) {
            if (room.tapeMusic.isPlaying()) {
                purpleSpeed = 0;
                purpleAlpha = Time.decreaseTimeValue(purpleAlpha, 0, 3);
            } else if (!freeze) {
                purpleAlpha = Time.increaseTimeValue(purpleAlpha, 1, purpleSpeed);
                purpleSpeed = Time.increaseTimeValue(purpleSpeed, 1, 0.007f);
            }
        }
    }

    public void adjustCamera(Flashlight flashlight){
        if (!freeze) flashlight.setCoord(Candys3Deluxe.inputManager.getX() + x, Candys3Deluxe.inputManager.getY() + y);
        CameraManager.move(getX(), y);
    }

    private float lookMechanic(float position, float mouse, float offset, int multiplier, int bounds, float shakeLimit){
        float distance = 0;
        float limit = Time.convertValue(1000 * multiplier);
        if (mouse < offset * 1){
            distance = (mouse - offset * 1) * Time.convertValue(lookSpeed);
            if (position + distance < shakeLimit) distance = -position + shakeLimit;
            else if (distance < -limit) distance = -limit;
        } else if (mouse > offset * 2){
            distance = (mouse - offset * 2) * Time.convertValue(lookSpeed);
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

    public boolean isJumpscare() {
        return jumpscare;
    }

    public void setJumpscare() {
        jumpscare = !jumpscare;
    }
}
