package util.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Options {
    private final Map<Integer, List<Button>> buttons = new HashMap<>();
    private final FrameBuffer fbo = FrameBufferManager.newFrameBuffer();
    public final TextureRegion windowRegion;
    public float alpha;
    private int section;
    public Button currentKeyButton;
    public boolean keySwitching;

    public Options(TextureHandler textureHandler){
        var pixmap = textureHandler.loadImageBuffer("assets", "ui/window");
        windowRegion = new TextureRegion(new Texture(pixmap));
    }

    public boolean input(InputManager input){
        if (keySwitching) return false;
        boolean switchSection = true;
        var arrowLeft = section != 0 && input.mouseOver(60, 325, 26, 26);
        var arrowRight = section != 2 && input.mouseOver(260, 325, 26, 26);
        if (input.isLeftPressed()) {
            if (arrowLeft) section--;
            else if (arrowRight) section++;
            else switchSection = false;
        } else switchSection = false;
        return switchSection;
    }

    public void fboDraw(Engine engine, int night){
        if (alpha == 0) return;
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();

        fbo.begin();
        if (engine.game.equals("candys3")){
            engine.candys3Deluxe.setNightColor(engine, night, 1.5f);
        } else {
            batch.setColor(0.75f, 0.75f, 1f, renderHandler.screenAlpha);
        }

        batch.draw(windowRegion, 20, 118);
        batch.setColor(1, 1, 1, 1);

        var buttonsList = buttons.get(section + 1);
        for (Button button : buttonsList) {
            if (!keySwitching) {
                button.render(engine, night, section == 0);
            } else {
                button.render(engine, night, button.isSelected(), currentKeyButton != null && currentKeyButton == button, section == 0);
            }
        }

        if (engine.game.equals("candys3")){
            engine.candys3Deluxe.setNightColor(engine, night, 1);
        } else {
            batch.setColor(0.65f, 0.65f, 0.85f, renderHandler.screenAlpha);
        }
        var region = engine.appHandler.getMenuUI().arrow;
        textureHandler.setFilter(region.getTexture());
        region.flip(true, false);
        if (section != 0) batch.draw(region, 60, 325);
        region.flip(true, false);
        if (section != 2) batch.draw(region, 260, 325);

        batch.setColor(1, 1, 1, 1);
        FrameBufferManager.end(batch, fbo, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(Engine engine, int night){
        if (alpha == 0) return;

        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();
        var fontManager = engine.appHandler.getFontManager();
        var texture = fbo.getColorBufferTexture();
        textureHandler.setFilter(texture);
        var region = new TextureRegion(texture);

        region.flip(false, true);
        batch.setColor(1, 1, 1, alpha);
        batch.draw(region, CameraManager.getX(), CameraManager.getY());

        fontManager.setSize(34);
        if (engine.game.equals("candys3")){
            engine.candys3Deluxe.fontAlpha(renderHandler,
                    fontManager.getFont("candys3/candysFont"),
                    night, alpha,
                    false);
        } else {
            engine.candys2Deluxe.fontAlpha(renderHandler,
                    fontManager.getFont("font"),
                    0,
                    alpha,
                    false);
        }
        if (section == 0) fontManager.setText("Keybinds");
        else if (section == 1) fontManager.setText("Cheats");
        else fontManager.setText("Options");


        fontManager.setPosition(true, false, (float) 308 / 2 + 20, 351);
        fontManager.render(batch);

        fontManager.setSize(20);

        for (Button button: buttons.get(section + 1)){
            if (engine.game.equals("candys3")){
                engine.candys3Deluxe.fontAlpha(renderHandler,
                        fontManager.getFont("candys3/candysFont"),
                        night, alpha,
                        false);
            } else {
                engine.candys2Deluxe.fontAlpha(renderHandler,
                        fontManager.getFont("font"),
                        0,
                        alpha,
                        false);
            }
            fontManager.setText(button.getPath());
            fontManager.setPosition(button.getX() + 44, button.getY() + 22);
            fontManager.render(batch);
        }
    }

    public void updateAlpha(boolean selected){
        if (selected) alpha = Time.increaseTimeValue(alpha, 1, 6);
        else alpha = Time.decreaseTimeValue(alpha, 0, 6);
    }

    public void add(int key, String path, String text){
        buttons.putIfAbsent(key, new ArrayList<>());
        var buttonsList = buttons.get(key);
        var button = new Button(path, 36, 274 - 44 * buttonsList.size(), 32, text);
        buttonsList.add(button);
    }

    public List<Button> get(int key){
        return buttons.get(key);
    }

    public void remove(int key){
        buttons.remove(key);
    }

    public void clear(){
        buttons.clear();
    }

    public int getSection() {
        return section;
    }

    public void dispose(){
        windowRegion.getTexture().dispose();
    }
}
