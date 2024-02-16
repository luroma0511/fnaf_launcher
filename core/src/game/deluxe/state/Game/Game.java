package game.deluxe.state.Game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import game.deluxe.data.GameData;
import game.deluxe.state.Game.Objects.Character.Characters;
import game.deluxe.state.Game.Objects.Flashlight;
import game.deluxe.state.Game.Objects.Player;
import game.deluxe.state.Game.Objects.Room;
import game.engine.util.Engine;
import game.engine.util.InputManager;
import game.engine.util.Request;
import game.engine.util.RenderManager;

public class Game {
    private byte nightState;
    private boolean loaded;
    private boolean reset;
    private final Characters characters;
    private final Player player;
    private final Flashlight flashlight;
    private final Room room;

    public Game(){
        characters = new Characters();
        player = new Player(2, 896, 152);
        flashlight = new Flashlight();
        room = new Room();
        reset = true;
    }

    public void reset(byte ratAI, byte catAI, byte vinnieAI, byte shadowRatAI, byte shadowCatAI, Request request){
        characters.reset(ratAI, catAI, vinnieAI, shadowRatAI, shadowCatAI);
        if (loaded) return;

        characters.load(request);
        StringBuilder sb = new StringBuilder("game/");
        String prefix = sb.append("Buttons/").toString();
        request.addImageRequest(prefix + "TapePlayer");
        request.addImageRequest(prefix + "TapePlayerBack");
        request.addImageRequest(prefix + "UnderBed");
        request.addImageRequest(prefix + "UnderBedBack");

        sb.delete(sb.indexOf("/") + 1, sb.length());
        prefix = sb.append("room/").toString();
        request.addImageRequest(prefix + "Room");
        request.addImageRequest(prefix + "FullRoomEffect");
        request.addImageRequest(prefix + "UnderBed");
        request.addImageRequest(prefix + "UnderBedEffect");

        sb.delete(sb.indexOf("/") + 1, sb.length());
        prefix = sb.append("Tape/").toString();
        request.addImageRequest(prefix + "Tape");
        prefix = sb.append("Buttons/").toString();
        request.addImageRequest(prefix + "Buttons");
        request.addImageRequest(prefix + "PlayButton");
        request.addImageRequest(prefix + "StopButton");
        request.addImageRequest(prefix + "RewindButton");

        sb.delete(sb.indexOf("/") + 1, sb.length());
        prefix = sb.append("Moving/").toString();

        for (int i = 1; i <= 10; i++){
            request.addImageRequest(prefix + "Turn Around/Moving" + i);
        }

        for (int i = 1; i <= 10; i++){
            request.addImageRequest(prefix + "Under Bed/Moving" + i);
        }

        for (int i = 1; i <= 11; i++){
            request.addImageRequest(prefix + "Turn Back/Moving" + i);
        }

        prefix = sb.delete(sb.indexOf("/") + 1, sb.length()).toString();
        request.addImageRequest(prefix + "BattleOverlay");
        request.addImageRequest(prefix + "Clock");
        request.addImageRequest(prefix + "Flashlight");

        loaded = true;
    }

    public void setNightState(byte nightState){
        this.nightState = nightState;
    }

    public void update(Engine engine, GameData gameData){
        if (reset) {
            reset(gameData.getRatAI(),
                    gameData.getCatAI(),
                    gameData.getVinnieAI(),
                    gameData.getShadowRatAI(),
                    gameData.getShadowCatAI(),
                    engine.getRequest());
            player.reset();
            reset = false;
        }
        if (!engine.getRequest().imagesIsEmpty()) return;

        player.update(engine, engine.getInputManager());
        characters.update(engine);
    }

    public void quit(){
        loaded = false;
    }

    public void render(RenderManager renderManager){
        if (!loaded) return;
        SpriteBatch batch = renderManager.getBatch();
        player.adjustCamera(flashlight, renderManager.getInputManager(), renderManager.getCameraManager());

        batch.setProjectionMatrix(renderManager.getCameraManager().getViewport().getCamera().combined);
        batch.enableBlending();
        batch.begin();

        room.render(renderManager, flashlight);

        debugRender(renderManager, renderManager.getFontManager().getDebugFont());
    }

    private void debugRender(RenderManager renderManager, BitmapFont debugFont){
        SpriteBatch batch = renderManager.getBatch();
        InputManager inputManager = renderManager.getInputManager();
        renderManager.restoreColor(batch);

        debugFont.draw(batch,
                "Mouse: " + (int) inputManager.getX() + " | " + (int) inputManager.getY(),
                24 + renderManager.getCameraManager().getX(), 696 + renderManager.getCameraManager().getY());

        debugFont.draw(batch,
                "Panning: " + player.getX() + " | " + player.getY(),
                24 + renderManager.getCameraManager().getX(), 666 + renderManager.getCameraManager().getY());
    }

    public void roomRender(RenderManager renderManager){

    }

    public void bedRender(RenderManager renderManager){

    }

    public void tapeRender(RenderManager renderManager){

    }
}
