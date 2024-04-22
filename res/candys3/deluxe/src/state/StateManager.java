package state;

import com.badlogic.gdx.Gdx;
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
    private int state;
    private int prevState;

    public StateManager(){
        game = new Game();
        menu = new Menu();
        win = new Win();
        state = 0;
        prevState = -1;
    }

    public void update(Window window){
        if (state == 0) menu.update(window);
        else if (state == 1) game.update(window);
        else if (state == 2) win.update();

        if (prevState == state) return;
        if (state == 0) menu.load();
        else if (state == 1) game.load();
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
        FrameBufferManager.end(batch, screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, screenBuffer, true);
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
