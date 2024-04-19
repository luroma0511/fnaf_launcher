package state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import data.GameData;
import state.Game.Game;
import state.Win.Win;
import state.Menu.Menu;
import util.FrameBufferManager;
import util.RenderManager;
import util.VideoManager;
import util.Window;

public class StateManager {
    private final Game game;
    private final Menu menu;
    private final Win win;
    private final GameData gameData;
    private int state;
    private int prevState;

    public StateManager(){
        game = new Game();
        menu = new Menu();
        win = new Win();
        gameData = new GameData();
        state = 0;
        prevState = -1;
    }

    public void update(Window window){
        if (state == 0) menu.update(window, gameData);
        else if (state == 1) game.update(window, gameData);
        else if (state == 2) win.update();

        if (prevState == state) return;
        if (state == 0) menu.load();
        else if (state == 1) game.load(gameData);
        else if (state == 2) win.load();
        prevState = state;
    }

    public void render(Window window){
        ScreenUtils.clear(0, 0, 0, 1);
        SpriteBatch batch = RenderManager.batch;
        FrameBuffer screenBuffer = RenderManager.screenBuffer;

        if (state == 0){
            screenBuffer.begin();
            menu.render(batch, window);
        } else if (state == 1) game.render(batch, window);
        else {
            screenBuffer.begin();
            ScreenUtils.clear(0, 0, 0, 1);
            win.render(batch);
        }
        FrameBufferManager.end(batch, screenBuffer, window.getWidth(), window.getHeight());
        FrameBufferManager.render(batch, screenBuffer, true);

        VideoManager.updateRender(batch, window.getWidth(), window.getHeight());
        batch.end();
    }

    public void dispose(){
        game.dispose();
    }

    public int getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }
}
