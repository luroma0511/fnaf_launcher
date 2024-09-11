package candys3;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import core.Engine;
import util.deluxe.GameData;
import candys3.Game.Game;
import candys3.Win.Win;
import candys3.Menu.Menu;
import util.*;

public class Candys3Deluxe extends StateManager {
    private final Game game;
    private final Menu menu;
    private final Win win;
    public final String version = "v1.0.2";

    public Candys3Deluxe(Engine engine){
        super();
        game = new Game();
        menu = new Menu();
        win = new Win();
        engine.appHandler.getFontManager().addFont("candys3/candysFont");
        engine.appHandler.getFontManager().addFont("candys3/captionFont");
    }

    public void update(Engine engine){
        if (getState() == 0) menu.update(engine);
        else if (getState() == 1) game.update(engine);
        else if (getState() == 2) win.update(engine);

        if (engine.isOnline()) {
            engine.gamejoltTrophyUI.update(engine.game, engine.appHandler.soundHandler, engine.gamejoltManager, engine.jsonHandler);
        }

        if (sameStates()) return;
        if (getState() == 0) menu.load(engine);
        else if (getState() == 1) game.load(engine);
        else if (getState() == 2) win.load(engine.appHandler.soundHandler, engine.appHandler.getTextureHandler());
        transitionState();
    }

    public void render(Engine engine){
        ScreenUtils.clear(0, 0, 0, 1);

        if (getState() == 0) menu.render(engine);
        else if (getState() == 1) game.render(engine);
        else win.render(engine);

        var candysFont = engine.appHandler.getFontManager().getFont("candys3/candysFont");
        if (engine.gamejoltManager != null) {
            engine.gamejoltTrophyUI.render(
                    engine.appHandler.getRenderHandler().batch,
                    engine.appHandler.getFontManager(), candysFont);
        }
//        debug(engine);
        engine.appHandler.getRenderHandler().batchEnd();
    }

    private void debug(Engine engine){
        var input = engine.appHandler.getInput();
        var batch = engine.appHandler.getRenderHandler().batch;
        var fontManager = engine.appHandler.getFontManager();

        var candysFont = fontManager.getFont("candys3/candysFont");
        fontManager.setCurrentFont(candysFont);
        fontManager.setSize(18);

        float position = 704;
        fontManager.setText("Mouse: " + (int) input.getX() + ", " + (int) input.getY());
        fontManager.setPosition(CameraManager.getX() + 16, CameraManager.getY() + position);
        fontManager.render(batch);
        position -= 25;
        fontManager.setText("Java version: " + Constants.jre);
        fontManager.setPosition(CameraManager.getX() + 16, CameraManager.getY() + position);
        fontManager.render(batch);
        position -= 25;
        fontManager.setText(Memory.getTotalMemory());
        fontManager.setPosition(CameraManager.getX() + 16, CameraManager.getY() + position);
        fontManager.render(batch);
        position -= 25;
        fontManager.setText(Memory.getFreeMemory());
        fontManager.setPosition(CameraManager.getX() + 16, CameraManager.getY() + position);
        fontManager.render(batch);
        position -= 25;
        fontManager.setText(Memory.getUsedMemory());
        fontManager.setPosition(CameraManager.getX() + 16, CameraManager.getY() + position);
        fontManager.render(batch);
    }

    public void fontAlpha(RenderHandler renderHandler, BitmapFont font, float alpha, boolean tweak){
        if (tweak) alpha = 0.5f + alpha / 2;
        if (GameData.night == 0 || GameData.night == 2) font.setColor(1, 0, 0, alpha * renderHandler.screenAlpha);
        else font.setColor(0.5f, 0, 1, alpha * renderHandler.screenAlpha);
    }

    public void setNightColor(Engine engine, float divider){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;

        if (GameData.night == 0 || GameData.night == 2) batch.setColor((float) 1 / divider, 0, 0, renderHandler.screenAlpha);
        else batch.setColor(0.5f / divider, 0, (float) 1 / divider, renderHandler.screenAlpha);
    }

    public void dispose(){
        menu.dispose();
        game.dispose();
    }
}
