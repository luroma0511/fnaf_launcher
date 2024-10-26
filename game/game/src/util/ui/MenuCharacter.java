package util.ui;

import candys3.GameData;
import util.*;

public class MenuCharacter extends SpriteObject {
    private byte ai;
    private final boolean aiCustom;
    private int initX;
    private int initY;
    private boolean hovered;
    private String filename;

    private float xDivider;
    private float yDivider;
    private float wDivider;
    private float hDivider;

    public MenuCharacter(int x, int y, int width, int height, boolean aiCustom, String filename) {
        super(null, x, y, width, height, 0);
        this.aiCustom = aiCustom;
        this.filename = filename;
        initX = x;
        initY = y;
        setX(x);
        setY(y);
    }

    public void characterPan(InputManager input, Window window){
        if (!Float.isNaN(input.getX()) && !Float.isNaN(input.getY())) {
            setX(getX() - characterPan(getX(), initX, input.getX(), window.width()));
            setY(getY() - characterPan(getY(), initY, input.getY(), window.height()));
        }
    }

    public void hover(InputManager input, float xDivider, float yDivider, float wDivider, float hDivider){
        this.xDivider = xDivider;
        this.yDivider = yDivider;
        this.wDivider = wDivider;
        this.hDivider = hDivider;
        hovered = characterMouseOver(input, xDivider, yDivider, wDivider, hDivider);
    }

    public void update(Caption caption, InputManager inputManager, boolean focus){
        update(caption, inputManager, focus, true);
    }

    public void update(Caption caption, InputManager inputManager, boolean focus, boolean clickable){
        boolean hovering = hovered && focus;
        if (!hovering) return;
        caption.setText(filename);
        caption.setActive(true);
        if (!aiCustom && inputManager.isLeftPressed() && clickable) ai = (byte) (ai == 0 ? 1 : 0);
        else if (aiCustom && clickable){
            if (inputManager.isLeftPressed()) ai = 20;
            else if (inputManager.isRightPressed()) ai = 0;
        }
        if (inputManager.getScrolled() == 0 || !aiCustom) return;
        ai += (byte) inputManager.getScrolled();
        if (ai < 0) ai = 0;
        if (ai > 20) ai = 20;
    }

    public void debugRender(RenderHandler renderHandler, String game){
        if (game.equals("candys2")){
            renderHandler.shapeDrawer.setColor(0.85f, 0.85f, 0.85f, 0.5f);
        } else {
            if (GameData.night == 0) renderHandler.shapeDrawer.setColor(1, 0, 0, 0.5f);
            else renderHandler.shapeDrawer.setColor(0.5f, 0, 1, 0.5f);
        }
        renderHandler.shapeDrawer.filledRectangle(
                getX() + getWidth() / xDivider, getY() + (int) (getHeight() / yDivider),
                (int) (getWidth() / wDivider), (int) (getHeight() / hDivider));

    }

    private float characterPan(float value, float init, float mouseCoord, int length){
        float target = init - (mouseCoord - (float) length / 2) * 0.075f;
        return (value - target) * Time.convertValue(12);
    }

    public boolean isAiCustom() {
        return aiCustom;
    }

    public boolean isHovered() {
        return hovered;
    }

    public byte getAi() {
        return ai;
    }

    public void setFilename(String filename){
        this.filename = filename;
    }

    public int getInitX() {
        return initX;
    }

    public int getInitY() {
        return initY;
    }

    public void setInitX(int initX) {
        this.initX = initX;
    }

    public void setInitY(int initY) {
        this.initY = initY;
    }
}