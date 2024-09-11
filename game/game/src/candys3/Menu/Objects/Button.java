package candys3.Menu.Objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import util.deluxe.GameData;
import util.InputManager;
import util.SpriteObject;
import util.TextureHandler;
import util.Time;

public class Button extends SpriteObject {
    private boolean selected;
    private boolean hovered;
    private String text;

    public Button(String path, int x, int y, int size, String text) {
        this(path, x, y, size, size, text);
    }

    public Button(String path, int x, int y, int width, int height, String text) {
        super(path, x, y, width, height, 0);
        if (text != null) this.text = text + ".txt";
    }

    public void update(Caption caption, InputManager inputManager, boolean alphaConfig){
        update(caption, inputManager, false, alphaConfig);
    }

    public void update(Caption caption, InputManager inputManager, boolean lockHover, boolean alphaConfig){
        hovered = !lockHover && mouseOver(inputManager, false);
        if (hovered && inputManager.isLeftPressed()) selected = !selected;
        if (alphaConfig && (hovered || selected)) setAlpha(Time.increaseTimeValue(getAlpha(), 1, 8));
        else setAlpha(Time.decreaseTimeValue(getAlpha(), 0, 8));
        if (!hovered || text == null) return;
        caption.setText(text);
        caption.setActive(true);
    }

    public void reset(){
        setAlpha(0);
        hovered = false;
        selected = false;
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

    public void render(TextureHandler textureHandler, SpriteBatch batch){
        render(textureHandler, batch, selected);
    }

    public void render(TextureHandler textureHandler, SpriteBatch batch, boolean cond){
        TextureRegion region;
        if (cond) region = textureHandler.getRegion("menu/checkbox", 84, 1);
        else region = textureHandler.getRegion("menu/checkbox", 84, 0);
        if (hovered) batch.setColor(1, 1, 1, 1);
        else if (GameData.night == 1){
            if (cond) batch.setColor(0.85f, 0.7f, 1, 1);
            else batch.setColor(0.5f, 0, 1, 1);
        } else {
            if (cond) batch.setColor(1, 0.7f, 0.7f, 1);
            else batch.setColor(1, 0, 0, 1);
        }
        batch.draw(region, getX(), getY());
    }
}