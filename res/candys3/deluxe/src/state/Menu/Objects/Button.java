package state.Menu.Objects;

import util.InputManager;
import util.SpriteObject;
import util.Time;

public class Button extends SpriteObject {
    private boolean selected;
    private boolean hovered;

    public Button(String path, int x, int y, int width, int height, float alpha) {
        super(path, x, y, width, height, alpha);
    }

    public void update(InputManager inputManager, boolean alphaConfig){
        update(null, inputManager, alphaConfig, (short) 0);
    }

    public void update(Caption caption, InputManager inputManager, boolean alphaConfig, short captionID){
        hovered = mouseOver(inputManager, false);
        if (hovered && inputManager.isPressed()) selected = !selected;
        if (alphaConfig && (hovered || selected)) setAlpha(Time.increaseTimeValue(getAlpha(), 1, 8));
        else setAlpha(Time.decreaseTimeValue(getAlpha(), 0, 8));
        if (!hovered || captionID == 0) return;
        caption.setID(captionID);
        caption.setActive(true);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
}
