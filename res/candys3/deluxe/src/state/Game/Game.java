package state.Game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Candys3Deluxe;
import deluxe.Paths;
import deluxe.GameData;
import deluxe.UserData;
import state.Game.Functions.Jumpscare;
import state.Game.Objects.Character.Characters;
import state.Game.Objects.Flashlight;
import state.Game.Objects.Player;
import state.Game.Objects.Room;
import util.*;

public class Game {
    private float nightTime;
    private boolean gameOver;
    private float whiteAlpha;
    private float gameOverAlpha;
    private float staticFrame;
    private boolean retry;
    private boolean menu;
    private final float nightTimeLength;
    private final StringBuilder nightTimeBuilder;
    private Characters characters;
    private final Player player;
    private final Flashlight flashlight;
    private final Room room;

    public static FrameBuffer lightBuffer;
    public static FrameBuffer roomBuffer;

    public Game(){
        player = new Player(2.75f, 896, 152);
        flashlight = new Flashlight();
        room = new Room();
        byte minute = 8;
        nightTimeLength = 60 * minute;
        nightTimeBuilder = new StringBuilder();
    }

    public void load(){
        reset();
        characters.load();
        room.load();
        ImageManager.add("Static/Static");
        ImageManager.addImages("game/", Paths.dataPath1 + "game/textures/game.txt");
        for (int i = 1; i <= 11; i++){
            ImageManager.add("game/Moving/Turn Back/Moving" + i);
            if (i == 11) break;
            ImageManager.add("game/Moving/Turn Around/Moving" + i);
            ImageManager.add("game/Moving/Under Bed/Moving" + i);
        }
        SoundManager.addAll(Paths.dataPath1 + "game/sounds.txt");
    }

    public void update(Window window, InputManager inputManager){
        boolean cond1 = gameOver && gameOverAlpha == 0;
        boolean cond2 = !gameOver && nightTime != 0;
        boolean cond3 = !player.isJumpscare() || GameData.restartOnJumpscare;
        if ((cond1 && retry)
                || (cond2 && cond3 && inputManager.keyTyped(Input.Keys.R))) {
            reset();
        } else if ((cond1 && menu)
                || (cond2 && cond3 && inputManager.keyTyped(Input.Keys.F2))) {
            Candys3Deluxe.stateManager.setState((byte) 0);
            Jumpscare.reset();
            room.stopMusic();
            SoundManager.stopAllSounds();
            return;
        }
        if (nightTime == 0) SoundManager.stopAllSounds();
        if (!gameOver) {
            RenderManager.screenAlpha = Time.increaseTimeValue(RenderManager.screenAlpha, 1, 2);
            if (!player.isJumpscare()) {
                player.update(window, flashlight, room);
                if (player.getY() == 0) room.input(inputManager.getX(), inputManager.getY(), player, inputManager.isLeftPressed());
                room.update(player);
                characters.update(player, room, flashlight);
            }
            player.updateEffects(room, flashlight.getY());
            if (!GameData.infiniteNight && !GameData.hardCassette && room.isMusicPlaying()) nightTime = Time.increaseTimeValue(nightTime, nightTimeLength, 2);
            else nightTime = Time.increaseTimeValue(nightTime, Short.MAX_VALUE, 1);
            if (nightTime < nightTimeLength) return;
            if (!GameData.infiniteNight) {
                Candys3Deluxe.stateManager.setState((byte) 2);
                room.stopMusic();
                SoundManager.stopAllSounds();
            }
            if (GameData.flashDebug || GameData.hitboxDebug || GameData.noJumpscares) return;
            byte[] modeStars = GameData.night == 0 ? UserData.mainCastStar : UserData.shadowCastStar;
            if (characters.checkMode()) modeStars[0] = 1;
            if (GameData.hitboxMultiplier < 1) modeStars[1] = 1;
            if (GameData.hardCassette) modeStars[2] = 1;
            if (GameData.hitboxMultiplier < 1 && GameData.hardCassette) modeStars[3] = 1;
        } else {
            whiteAlpha = Time.decreaseTimeValue(whiteAlpha, 0, 2);
            staticFrame = Time.increaseTimeValue(staticFrame, 8192, 30);
            if (staticFrame == 8192) staticFrame = 0;
            if (retry || menu) gameOverAlpha = Time.decreaseTimeValue(gameOverAlpha, 0, 1);
        }
    }

    public void reset(){
        characters = new Characters(GameData.ratAI, GameData.catAI, GameData.vinnieAI, GameData.shadowRatAI, GameData.shadowCatAI);
        player.reset();
        room.reset();
        Jumpscare.reset();
        nightTime = 0;
        RenderManager.screenAlpha = 0;
        gameOver = false;
//        SoundManager.setAllMuffle(1);
    }

    private void renderGame(SpriteBatch batch, Window window){
        player.adjustCamera(flashlight);
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        RenderManager.shapeDrawer.update();
        batch.enableBlending();
        batch.begin();

        room.render(batch, characters, room, flashlight);
        if (player.getOverlayAlpha() > 0) {
            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            //DON'T CHANGE THIS!!!
            batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);
            TextureRegion region = ImageManager.get("game/BattleOverlay");
            if (GameData.night == 0) batch.setColor(player.getOverlayAlpha(), 0, 0, 1);
            else batch.setColor(player.getOverlayAlpha() * 0.75f, 0, player.getOverlayAlpha(), 1);
            batch.draw(region, CameraManager.getX(), CameraManager.getY(), 1280, 720);
            batch.setColor(1, 1, 1, 1);
            batch.flush();
            batch.setBlendFunction(srcFunc, dstFunc);
        }
        if (player.isJumpscare() && player.getBlacknessTimes() == 0){
            RenderManager.shapeDrawer.setColor(0, 0, 0, 1);
            RenderManager.shapeDrawer.filledRectangle(CameraManager.getX(), CameraManager.getY(), 1280, 720);
            if (room.isMusicPlaying()) room.stopMusic();
            if (!Jumpscare.render(batch)) {
                gameOver = true;
                whiteAlpha = 1;
                gameOverAlpha = 1;
                RenderManager.shapeDrawer.setColor(1, 1, 1, 1);
                RenderManager.shapeDrawer.filledRectangle(CameraManager.getX(), CameraManager.getY(), 1280, 720);
                return;
            }
        }
        RenderManager.shapeDrawer.setColor(0, 0, 0, 1 - player.getBlacknessAlpha());
        RenderManager.shapeDrawer.filledRectangle(CameraManager.getX(), CameraManager.getY(), 1280, 720);

        RenderManager.shapeDrawer.setColor(0.2f, 0, 0.2f, player.getPurpleAlpha());
        RenderManager.shapeDrawer.filledRectangle(CameraManager.getX(), CameraManager.getY(), 1280, 720);

        if (player.getButtonFade() > 0) {
            batch.setColor(1, 1, 1, player.getButtonFade());
            TextureRegion region;
            switch (room.getState()) {
                case 0:
                    region = ImageManager.get("game/Buttons/TapePlayer");
                    batch.draw(region, CameraManager.getX() + 132, CameraManager.getY());

                    region = ImageManager.get("game/Buttons/UnderBed");
                    batch.draw(region, CameraManager.getX() + 732, CameraManager.getY());
                    break;
                case 1:
                    region = ImageManager.get("game/Buttons/UnderBedBack");
                    batch.draw(region, CameraManager.getX() + 430, CameraManager.getY());
                    break;
                case 2:
                    region = ImageManager.get("game/Buttons/TapePlayerBack");
                    batch.draw(region, CameraManager.getX() + 430, CameraManager.getY());
                    break;
            }
            batch.setColor(1, 1, 1, 1);
        }

        FontManager.setFont(Candys3Deluxe.candysFont);
        FontManager.setSize(40);
        Candys3Deluxe.candysFont.setColor(1, 1, 1, 1);
        nightTimeBuilder.delete(0, nightTimeBuilder.length());
        if (GameData.infiniteNight) {
            int hour = (int) nightTime / 60;
            nightTimeBuilder.append(hour).append(":");
            int tempTime = (int) (nightTime % 60);
            if (tempTime < 10) nightTimeBuilder.append(0);
            nightTimeBuilder.append(tempTime);
        } else {
            int hour = (int) (nightTime / (nightTimeLength / 6));
            if (hour == 0) hour = 12;
            nightTimeBuilder.append(hour).append(" AM");
        }
        FontManager.setText(nightTimeBuilder.toString());
        FontManager.setOutline(0.35f);
        FontManager.render(batch,
                CameraManager.getX() + window.width() - FontManager.getLayout().width - 20,
                CameraManager.getY() + window.height() - FontManager.getLayout().height + 8);
        FontManager.setOutline(0.5f);
    }

    private void renderGameOver(SpriteBatch batch, Window window){
        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        RenderManager.shapeDrawer.update();
        batch.enableBlending();
        batch.begin();
        RenderManager.screenBuffer.begin();

        RenderManager.shapeDrawer.setColor(0, 0, 0, 1);
        RenderManager.shapeDrawer.filledRectangle(0, 0, 1280, 720);

        FontManager.setFont(Candys3Deluxe.candysFont);
        Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, 1, true);
        FontManager.setText("GAME OVER");

        TextureRegion region = ImageManager.getRegion("Static/Static", 1024, (int) staticFrame % 8);
        Candys3Deluxe.setNightColor(batch, 2);
        batch.draw(region, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, 1);

        FontManager.setSize(54);
        FontManager.render(batch, true, true,
                (float) window.width() / 2,
                (float) window.height() / 2);

        RenderManager.shapeDrawer.setColor(1, 1, 1, whiteAlpha);
        RenderManager.shapeDrawer.filledRectangle(0, 0, 1280, 720);

        RenderManager.shapeDrawer.setColor(0, 0, 0, 1 - gameOverAlpha);
        RenderManager.shapeDrawer.filledRectangle(0, 0, 1280, 720);
    }

    public void render(SpriteBatch batch, Window window){
        if (!gameOver) renderGame(batch, window);
        else renderGameOver(batch, window);
        if (GameData.hitboxDebug || GameData.flashDebug) characters.debug(batch, window, player, room);
    }

    public void dispose(){
        room.dispose();
    }
}
