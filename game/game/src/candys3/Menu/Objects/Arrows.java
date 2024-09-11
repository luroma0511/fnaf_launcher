package candys3.Menu.Objects;

import util.InputManager;

public class Arrows {
    private byte hovered;

    public void update(InputManager inputManager, byte section){
        if (section != 0 && inputManager.mouseOver(488, 594, 40, 40)) hovered = 1;
        else if (section != 2 && inputManager.mouseOver(752, 594, 40, 40)) hovered = 2;
        else hovered = 0;
    }

    public byte getHovered() {
        return hovered;
    }
}
