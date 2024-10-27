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

    public Options(TextureHandler textureHandler){
        var pixmap = textureHandler.loadImageBuffer("assets", "ui/window");
        windowRegion = new TextureRegion(new Texture(pixmap));
    }

    public void input(InputManager input){
        var arrowLeft = section != 0 && input.mouseOver(60, 325, 26, 26);
        var arrowRight = section != 2 && input.mouseOver(260, 325, 26, 26);
        if (input.isLeftPressed()) {
            if (arrowLeft) section--;
            else if (arrowRight) section++;
        }
    }

    public void fboDraw(Engine engine){
        if (alpha == 0) return;
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();

        fbo.begin();
        if (engine.game.equals("candys3")){
            engine.candys3Deluxe.setNightColor(engine, 1.5f);
        } else {
            batch.setColor(0.75f, 0.75f, 1f, renderHandler.screenAlpha);
        }

        batch.draw(windowRegion, 20, 118);
        batch.setColor(1, 1, 1, 1);

        var buttonsList = buttons.get(section + 1);
        for (int i = 0; i < buttonsList.size(); i++){
            var button = buttonsList.get(i);
            if (section == 0 && i == 3) {
                boolean allChallenges = buttonsList.get(0).isSelected()
                        && buttonsList.get(1).isSelected()
                        && buttonsList.get(2).isSelected();
                button.render(engine, allChallenges);
            } else button.render(engine);
        }

        if (engine.game.equals("candys3")){
            engine.candys3Deluxe.setNightColor(engine, 1);
        } else {
            batch.setColor(0.8f, 0.8f, 1, renderHandler.screenAlpha);
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

    public void render(Engine engine){
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
                    alpha,
                    false);
        } else {
            engine.candys2Deluxe.fontAlpha(renderHandler,
                    fontManager.getFont("candys2/font1"),
                    0,
                    alpha,
                    false);
        }
        if (section == 0) fontManager.setText("Challenges");
        else if (section == 1) fontManager.setText("Cheats");
        else fontManager.setText("Options");


        fontManager.setPosition(true, false, (float) 308 / 2 + 20, 351);
        fontManager.render(batch);

        fontManager.setSize(20);

        for (Button button: buttons.get(section + 1)){
            if (engine.game.equals("candys3")){
                engine.candys3Deluxe.fontAlpha(renderHandler,
                        fontManager.getFont("candys3/candysFont"),
                        alpha,
                        false);
            } else {
                engine.candys2Deluxe.fontAlpha(renderHandler,
                        fontManager.getFont("candys2/font1"),
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
        if (selected) alpha = Time.increaseTimeValue(alpha, 1, 4);
        else alpha = Time.decreaseTimeValue(alpha, 0, 4);
    }

    public void updateAllChallenges(List<Button> buttonsList){
        if (buttonsList.get(3).isSelected()) {
            if (buttonsList.get(0).isSelected()
                    && buttonsList.get(1).isSelected()
                    && buttonsList.get(2).isSelected()) {
                buttonsList.get(0).setSelected();
                buttonsList.get(1).setSelected();
                buttonsList.get(2).setSelected();
            } else {
                if (!buttonsList.get(0).isSelected()) buttonsList.get(0).setSelected();
                if (!buttonsList.get(1).isSelected()) buttonsList.get(1).setSelected();
                if (!buttonsList.get(2).isSelected()) buttonsList.get(2).setSelected();
            }
            buttonsList.get(3).setSelected();
        }
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

    public int getSection() {
        return section;
    }

    public void dispose(){
        windowRegion.getTexture().dispose();
    }
}
