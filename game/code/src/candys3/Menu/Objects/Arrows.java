package candys3.Menu.Objects;

import candys3.GameData;
import util.FontManager;
import util.InputManager;

public class Arrows {
    private byte hovered;

    public void update(InputManager inputManager, FontManager fontManager, boolean optionButtonSelected, byte section){
        hovered = 0;
        if (optionButtonSelected) {
            if (section != 0 && inputManager.mouseOver(502, 601, 26, 26)) hovered = 1;
            else if (section != 2 && inputManager.mouseOver(752, 601, 26, 26)) hovered = 2;
        } else {
            if (GameData.night == 2) return;
            float layoutWidth = fontManager.getLayout().width;
            if (GameData.night != 1 && inputManager.mouseOver(640 + (int) (layoutWidth / 2) + 16, 673, 26, 26)){
                hovered = 3;
            } else if (GameData.night != 0 && inputManager.mouseOver(640 - (int) (layoutWidth / 2) - 42, 673, 26, 26)){
                hovered = 4;
            }
        }
    }

    public byte getHovered() {
        return hovered;
    }
}
