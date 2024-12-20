package candys2;

import candys2.Game.Game;
import candys2.Menu.Menu;
import candys2.Win.Win;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import core.Engine;
import util.RenderHandler;
import util.StateManager;
import util.ui.Options;

public class Candys2Deluxe extends StateManager {
    public final Menu menu;
    public final Game game;
    public final Win win;

    public Candys2Deluxe(Engine engine, Options options){
        super();
        menu = new Menu(options);
        game = new Game();
        win = new Win();
        engine.appHandler.getFontManager().addFont("candys2/fontPixel");
    }

    public void update(Engine engine){
        if (getState() == 0) menu.update(engine);
        else if (getState() == 1) game.update(engine);
        else win.update(engine);

        if (engine.isOnline()) {
            engine.gamejoltTrophyUI.update(engine.game, engine.appHandler.soundHandler, engine.gamejoltManager);
        }

        if (sameStates()) return;
        if (getState() == 0) menu.load(engine, true);
        else if (getState() == 1) game.reset(engine.appHandler.getTextureHandler(), engine.candys2Deluxe.menu);
        transitionState();
    }

    public void render(Engine engine){
        ScreenUtils.clear(0, 0, 0, 1);

        if (getState() == 0) menu.render(engine);
        else if (getState() == 1) game.render(engine);
        else win.render(engine);

        var font1 = engine.appHandler.getFontManager().getFont("font");
        if (engine.gamejoltManager != null) {
            engine.gamejoltTrophyUI.render(
                    engine.appHandler.getRenderHandler().batch,
                    engine.appHandler.getFontManager(), font1);
        }

        engine.appHandler.getRenderHandler().batchEnd();
    }

    public void fontAlpha(RenderHandler renderHandler, BitmapFont font, int night, float alpha, boolean tweak){
        if (tweak) alpha = 0.5f + alpha / 2;
        if (night == 0) font.setColor(0.65f, 0.65f, 0.85f, alpha * renderHandler.screenAlpha);
        else font.setColor(0.5f, 0, 1, alpha * renderHandler.screenAlpha);
    }

    public void dispose(){

    }
}
