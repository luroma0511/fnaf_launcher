package candys3.Menu.Objects;

import candys3.GameData;
import util.*;

public class MenuCharacter extends SpriteObject {
    private byte ai;
    private final boolean aiCustom;
    private int initX;
    private int initY;
    private String filename;

    public MenuCharacter(int x, int y, int width, int height, boolean aiCustom, String filename) {
        super(null, x, y, width, height, 0);
        this.aiCustom = aiCustom;
        this.filename = filename;
        initX = x;
        initY = y;
        setX(x);
        setY(y);
    }

    public void update(Caption caption, InputManager inputManager, Window window, boolean focus){
        update(caption, inputManager, window, focus, true);
    }

    public void update(Caption caption, InputManager inputManager, Window window, boolean focus, boolean clickable){
        if (!Float.isNaN(inputManager.getX()) && !Float.isNaN(inputManager.getY())) {
            setX(getX() - characterPan(getX(), initX, inputManager.getX(), window.width()));
            setY(getY() - characterPan(getY(), initY, inputManager.getY(), window.height()));
        }
        boolean hovered = mouseOver(inputManager, true) && focus;
        if (!hovered) return;
        caption.setText(filename);
        caption.setActive(true);
        if (inputManager.isLeftPressed() && !aiCustom && clickable) ai = (byte) (ai == 0 ? 1 : 0);
        else if (inputManager.getScrolled() == 0 || !aiCustom) return;
        ai += (byte) inputManager.getScrolled();
        if (ai < 0) ai = 0;
        if (ai > 20) ai = 20;
    }

    public void debugRender(RenderHandler renderHandler){
        if (GameData.night == 0) renderHandler.shapeDrawer.setColor(1, 0, 0, 0.5f);
        else renderHandler.shapeDrawer.setColor(0.5f, 0, 1, 0.5f);
        renderHandler.shapeDrawer.filledRectangle(
                getX() + getWidth() / 4.75f, getY() + (int) (getHeight() / 4),
                (int) (getWidth() / 1.75f), (int) (getHeight() / 1.5f));
    }

    private float characterPan(float value, float init, float mouseCoord, int length){
        float target = init - (mouseCoord - (float) length / 2) * 0.075f;
        return (value - target) * Time.convertValue(12);
    }

    public boolean isAiCustom() {
        return aiCustom;
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