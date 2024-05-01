package state.Menu.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import util.InputManager;
import util.SpriteObject;

public class Star extends SpriteObject {
    private boolean hovered;
    private short captionID;

    public Star(int x, int y, int width, int height, int captionID){
        super(null, x, y, width, height, 0);
        this.captionID = (short) captionID;
    }

    public void update(Caption caption, InputManager inputManager){
        hovered = mouseOver(inputManager, false);
        if (!hovered || captionID == 0) return;
        caption.setID(captionID);
        caption.setActive(true);
    }

    public boolean isHovered() {
        return hovered;
    }

    public void setCaptionID(int captionID) {
        this.captionID = (short) captionID;
    }
}
