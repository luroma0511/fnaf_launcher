package state.Menu.Objects;

import deluxe.GameData;
import util.InputManager;
import util.RenderManager;
import util.SpriteObject;
import util.Window;

public class MenuCharacter extends SpriteObject {
    private byte ai;
    private boolean aiCustom;
    private final int initX;
    private final int initY;
    private final short captionID;

    public MenuCharacter(int x, int y, int width, int height, boolean aiCustom, int captionID) {
        super(null, x, y, width, height, 0);
        this.aiCustom = aiCustom;
        this.captionID = (short) captionID;
        initX = x;
        initY = y;
        setX(x);
        setY(y);
    }

    public void update(Caption caption, InputManager inputManager, Window window, boolean focus){
        if (!Float.isNaN(inputManager.getX()) && !Float.isNaN(inputManager.getY())) {
            setX(getX() - characterPan(getX(), initX, inputManager.getX(), window.width()));
            setY(getY() - characterPan(getY(), initY, inputManager.getY(), window.height()));
        }
        boolean hovered = mouseOver(inputManager, true) && focus;
        if (!hovered) return;
        caption.setID(captionID);
        caption.setActive(true);
        if (inputManager.isLeftPressed() && !aiCustom) ai = (byte) (ai == 0 ? 1 : 0);
        else if (inputManager.getScrolled() == 0 || !aiCustom) return;
        ai += (byte) inputManager.getScrolled();
        if (ai < 0) ai = 0;
        if (ai > 20) ai = 20;
    }

    public void debugRender(){
        if (GameData.night == 0) RenderManager.shapeDrawer.setColor(1, 0, 0, 0.5f);
        else RenderManager.shapeDrawer.setColor(0.5f, 0, 1, 0.5f);
        RenderManager.shapeDrawer.filledRectangle(
                getX() + getWidth() / 4.75f, getY() + (int) (getHeight() / 4),
                (int) (getWidth() / 1.75f), (int) (getHeight() / 1.5f));
    }

    private float characterPan(float value, float init, float mouseCoord, int length){
        float target = init - (mouseCoord - (float) length / 2) * 0.075f;
        return (value - target) / 4;
    }

    public boolean isAiCustom() {
        return aiCustom;
    }

    public byte getAi() {
        return ai;
    }

    public void setAi(byte ai) {
        this.ai = ai;
    }
}