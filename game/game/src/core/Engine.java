package core;

import candys2.Candys2Deluxe;
import candys3.Candys3Deluxe;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import util.deluxe.*;
import util.*;
import util.error.GameError;
import util.gamejolt.GamejoltManager;
import util.gamejolt.GamejoltTrophyUI;

public class Engine extends ApplicationAdapter implements CandysDeluxeKeys {
    public final AppHandler appHandler;
    private GameError gameError;
    public Candys2Deluxe candys2Deluxe;
    public Candys3Deluxe candys3Deluxe;

    public final String game;
    public final String version = "v1.1.4";
    public final CandysUser user;
    public final CandysJSONHandler jsonHandler;
    public final GamejoltManager gamejoltManager;
    public final GamejoltTrophyUI gamejoltTrophyUI;
    public final DeluxeGuestStoreTable scoreTable;

    public Engine(int width, int height, String[] arg){
        appHandler = new AppHandler(width, height);
        jsonHandler = new CandysJSONHandler();
        game = arg[1];

        if (arg[0].equals("Guest")){
            user = new CandysUser(arg[0]);
            gamejoltManager = null;
            gamejoltTrophyUI = null;

            FileUtils.init("candysDeluxe");

            String userData = FileUtils.readFile(0);
            System.out.println(userData);
            if (!userData.isEmpty()) {
                CandysUser tempUser;
                try {
                    tempUser = jsonHandler.readUser(userData);
                } catch (Exception e){
                    tempUser = user;
                }
                user.setUser(tempUser);
            }
            FileUtils.writeUser(jsonHandler, user);

            String scoreData = FileUtils.readFile(1);
            System.out.println(scoreData);
            if (!scoreData.isEmpty()) {
                scoreTable = jsonHandler.readScoreTable(scoreData);
            } else {
                scoreTable = new DeluxeGuestStoreTable();
            }
            scoreTable.init();
            FileUtils.writeTable(jsonHandler, scoreTable);
            System.out.println("Completed");
            return;
        }
        scoreTable = null;
        String[] userInfo = arg[0].split(", ");
        user = new CandysUser(userInfo[0]);
        gamejoltManager = new GamejoltManager(gameID, key, userInfo[0], userInfo[1], userInfo[2]);
        gamejoltTrophyUI = new GamejoltTrophyUI();
    }

    @Override
    public void create(){

        if (gamejoltManager != null) {
            gamejoltManager.initialize();
            gamejoltManager.execute(() -> {
                String userKey = "user_id=" + gamejoltManager.id;
                gamejoltManager.dataStore.fetch(gamejoltManager, userKey);
                String data = gamejoltManager.dataStore.getFetch();
                if (data.contains("hell")){
                    CandysUser tempUser;
                    try {
                        tempUser = jsonHandler.readUser(gamejoltManager.dataStore.getFetch());
                    } catch (Exception e){
                        tempUser = user;
                    }
                    user.setUser(tempUser);
                } else {
                    String oldUserKey = "user=" + gamejoltManager.username;
                    gamejoltManager.dataStore.fetch(gamejoltManager, oldUserKey);
                    data = gamejoltManager.dataStore.getFetch();
                    if (!data.isEmpty()) gamejoltManager.dataStore.remove(gamejoltManager, oldUserKey);
                }
                String value = jsonHandler.writeCandysUser(user);
                System.out.println(value);
                gamejoltManager.dataStore.set(gamejoltManager, userKey, value);
                gamejoltManager.dataStore.loaded = true;
            });
        }
        appHandler.init();
        if (game.equals("candys2")) candys2Deluxe = new Candys2Deluxe(this);
        if (game.equals("candys3")) candys3Deluxe = new Candys3Deluxe(this);
    }

    @Override
    public void resize(int width, int height){
        if (CameraManager.getViewport() != null) {
            CameraManager.apply(width, height);
        }
    }

    @Override
    public void render(){
        Time.update();
        boolean lock = (candys2Deluxe != null && candys2Deluxe.menu.options != null && candys2Deluxe.menu.options.keySwitching)
                || (candys3Deluxe != null && candys3Deluxe.getMenu().options != null && candys3Deluxe.getMenu().options.keySwitching);
        if (!lock) appHandler.getInput().fullscreen(appHandler.window, user);

        try {
            if (gameError == null) {
                if (candys2Deluxe != null) candys2Deluxe.update(this);
                else if (candys3Deluxe != null) candys3Deluxe.update(this);
            }

            if (appHandler.getRenderHandler().requests(appHandler, game)) appHandler.getInput().setLock();
            appHandler.getRenderHandler().viewportAdjust(appHandler.getInput());

            if (gameError == null) {
                if (candys2Deluxe != null) candys2Deluxe.render(this);
                else if (candys3Deluxe != null) candys3Deluxe.render(this);
            } else {
                var fontManager = appHandler.getFontManager();
                var renderHandler = appHandler.getRenderHandler();
                var batch = renderHandler.batch;

                ScreenUtils.clear(0, 0, 0, 1);
                CameraManager.setOrigin();
                batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
                batch.enableBlending();
                renderHandler.shapeDrawer.update();
                renderHandler.batchBegin();

                appHandler.getRenderHandler().screenBuffer.begin();

                renderHandler.shapeDrawer.setColor(0.075f, 0, 0, 1);
                renderHandler.drawScreen();
                FrameBufferManager.end(batch, renderHandler.screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                FrameBufferManager.render(batch, renderHandler.screenBuffer, true);

                if (gameError.font == null) {
                    if (game.equals("candys2")) gameError.font = fontManager.getFont("candys2/font1");
                    else gameError.font = fontManager.getFont("candys3/captionFont");
                }
                fontManager.setCurrentFont(gameError.font);
                gameError.font.setColor(1, 1, 1, 1);
                fontManager.setSize(24);
                fontManager.setText("Something went wrong! Please report this error to the Candy's Deluxe Gamejolt Page!");
                fontManager.setPosition(true, true,
                        (float) appHandler.window.width() / 2,
                        (float) appHandler.window.height() / 4 * 3);
                fontManager.render(batch);

                fontManager.setSize(12);
                fontManager.setText(gameError.text);
                fontManager.setPosition(true, true,
                        (float) appHandler.window.width() / 2,
                        (float) appHandler.window.height() / 3);
                fontManager.render(batch);

                renderHandler.batchEnd();

                if (appHandler.getInput().isLeftPressed()) {
                    gameError = null;
                }
            }
        } catch (Exception e) {
            gameError = new GameError(e);
            if (candys2Deluxe != null) candys2Deluxe.setState(0);
            else if (candys3Deluxe != null) candys3Deluxe.setState(0);
            appHandler.soundHandler.stopAllSounds();
            appHandler.getRenderHandler().batchEnd();
        }
        appHandler.getInput().reset();
    }

    @Override
    public void dispose(){
        if (gamejoltManager != null) gamejoltManager.dispose();
        if (candys2Deluxe != null) candys2Deluxe.dispose();
        if (candys3Deluxe != null) candys3Deluxe.dispose();
        VideoManager.dispose();
        appHandler.dispose();
    }

    public boolean isOnline(){
        return gamejoltManager != null && gamejoltTrophyUI != null;
    }
}
