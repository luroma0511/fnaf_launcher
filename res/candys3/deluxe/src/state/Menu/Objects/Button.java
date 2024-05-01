package state.Menu.Objects;

import util.InputManager;
import util.SpriteObject;
import util.Time;

public class Button extends SpriteObject {
    private boolean selected;
    private boolean hovered;
    private final short captionID;

    public Button(String path, int x, int y, int width, int height, int captionID) {
        super(path, x, y, width, height, 0);
        this.captionID = (short) captionID;
    }

    public void update(Caption caption, InputManager inputManager, boolean alphaConfig){
        hovered = mouseOver(inputManager, false);
        if (hovered && inputManager.isLeftPressed()) selected = !selected;
        if (alphaConfig && (hovered || selected)) setAlpha(Time.increaseTimeValue(getAlpha(), 1, 8));
        else setAlpha(Time.decreaseTimeValue(getAlpha(), 0, 8));
        if (!hovered || captionID == 0) return;
        caption.setID(captionID);
        caption.setActive(true);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected() {
        selected = !selected;
    }

    public void setSelected(boolean cond){
        selected = cond;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
}
