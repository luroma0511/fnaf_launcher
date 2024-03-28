package deluxe.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import deluxe.Candys3Deluxe;
import deluxe.data.GameData;
import deluxe.state.Game.Game;
import deluxe.state.Menu.Menu;
import deluxe.state.Win.Win;
import util.FrameBufferManager;
import util.RenderManager;
import util.VideoManager;

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

    public void update(){
        if (state == 0) menu.update(gameData);
        else if (state == 1) game.update(gameData);
        else if (state == 2) win.update();

        if (prevState == state) return;
        if (state == 0) menu.load();
        else if (state == 1) game.load(gameData);
        else if (state == 2) win.load();
        prevState = state;
    }

    public void render(){
        ScreenUtils.clear(0, 0, 0, 1);
        SpriteBatch batch = RenderManager.batch;
        FrameBuffer screenBuffer = RenderManager.screenBuffer;

        if (state == 0){
            screenBuffer.begin();
            menu.render(batch);
        } else if (state == 1) game.render();
        else {
            screenBuffer.begin();
            ScreenUtils.clear(0, 0, 0, 1);
            win.render(batch);
        }
        FrameBufferManager.end(batch, screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, screenBuffer, true);

        VideoManager.updateRender(batch, Candys3Deluxe.width, Candys3Deluxe.height);
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
