package util.ui;

import candys3.GameData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.Engine;
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

    public boolean update(Caption caption, InputManager inputManager, boolean alphaConfig){
        return update(caption, inputManager, false, alphaConfig);
    }

    public boolean update(Caption caption, InputManager inputManager, boolean lockHover, boolean alphaConfig){
        hovered = mouseOver(inputManager);
        if (hovered && !lockHover && inputManager.isLeftPressed()) selected = !selected;
        if (alphaConfig && (hovered || selected)) setAlpha(Time.increaseTimeValue(getAlpha(), 1, 8));
        else setAlpha(Time.decreaseTimeValue(getAlpha(), 0, 8));
        if (!hovered || text == null || caption == null) return hovered && inputManager.isLeftPressed();
        caption.setText(text);
        caption.setActive(true);
        return hovered && inputManager.isLeftPressed();
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

    public void render(Engine engine, boolean exclamation){
        render(engine, selected, hovered, exclamation);
    }

    public void render(Engine engine, boolean cond, boolean hovered, boolean exclamation){
        TextureRegion region;
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;
        var menuUI = engine.appHandler.getMenuUI();

        if (cond) {
            region = textureHandler.getRegion(menuUI.checkbox, 84, exclamation ? 2 : 1);
        } else region = textureHandler.getRegion(menuUI.checkbox, 84, 0);
        if (hovered) batch.setColor(1, 1, 1, 1);
        else if (engine.game.equals("candys3")) {
            if (GameData.night == 1) {
                if (cond) batch.setColor(0.85f, 0.7f, 1, 1);
                else batch.setColor(0.5f, 0, 1, 1);
            } else if (GameData.night == 2) {
                if (cond) batch.setColor(1, 0.7f, 0.7f, 1);
                else batch.setColor(1, 0, 0, 1);
            } else {
                if (cond) batch.setColor(1, 0, 0.6f, 1);
                else batch.setColor(0.85f, 0, 0.5f, 1);
            }
        } else {
            batch.setColor(0.65f, 0.65f, 0.85f, 1);
        }
        batch.draw(region, getX(), getY(), 32, 32);
    }
}