package candys2;

import candys2.Game.Game;
import candys2.Menu.Menu;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import core.Engine;
import util.StateManager;

public class Candys2Deluxe extends StateManager {
    private final Menu menu;
    private final Game game;

    public Candys2Deluxe(Engine engine){
        super();
        setState(1);
        menu = new Menu();
        game = new Game();
    }

    public void update(Engine engine){
        if (getState() == 0) menu.update(engine);
        else if (getState() == 1) game.update(engine);

        if (engine.isOnline()) {
            engine.gamejoltTrophyUI.update(engine.game, engine.appHandler.soundHandler, engine.gamejoltManager, engine.jsonHandler);
        }

        if (sameStates()) return;
        if (getState() == 0) menu.load(engine);
        else if (getState() == 1) game.load(engine, 0);
        transitionState();
    }

    public void render(Engine engine){
        ScreenUtils.clear(0, 0, 0, 1);
        FrameBuffer screenBuffer = engine.appHandler.getRenderHandler().screenBuffer;

        if (getState() == 0) menu.render(engine);
        else if (getState() == 1) game.render(engine);
    }

    public void dispose(){

    }
}
