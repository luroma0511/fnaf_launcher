package util.ui;

import util.FontManager;
import util.InputManager;

public class Arrows {
    private byte hovered;

    public void update(InputManager input, FontManager fontManager, int currentNight, int totalNights){
        hovered = 0;
        float layoutWidth = fontManager.getLayout().width;
        if (currentNight != totalNights - 1 && input.mouseOver(640 + (int) (layoutWidth / 2) + 16, 673, 26, 26)){
            hovered = 1;
        } else if (currentNight != 0 && input.mouseOver(640 - (int) (layoutWidth / 2) - 42, 673, 26, 26)){
            hovered = 2;
        }
    }

    public byte getHovered() {
        return hovered;
    }
}
