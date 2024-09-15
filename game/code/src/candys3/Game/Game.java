package candys3.Game;

import candys3.GameData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.deluxe.Candys3Data;
import util.deluxe.Paths;
import candys3.Game.Objects.*;
import candys3.Game.Objects.Character.Characters;
import candys3.Game.Objects.Functions.Jumpscare;
import util.*;
import util.gamejolt.Score;

public class Game {
    private boolean gameOver;
    private float scoreAlpha;
    private float whiteAlpha;
    private float gameOverAlpha;
    private float staticFrame;
    private boolean clicked;
    private boolean retry;
    private boolean menu;
    private boolean modifySave;
    private float nightTime;
    private float fastNightTime;
    private final float nightTimeLength;
    private final StringBuilder nightTimeBuilder;
    private final Characters characters;
    private final Player player;
    private final Room room;
    public boolean loaded;
    public boolean loadingBegun;

    public static FrameBuffer lightBuffer;
    public static FrameBuffer roomBuffer;

    public Game(){
        characters = new Characters();
        player = new Player();
        room = new Room();
        byte nightMinuteLength = 8;
        nightTimeLength = 0.25f * nightMinuteLength;
        nightTimeBuilder = new StringBuilder();
    }

    public void load(Engine engine){
        var textureHandler = engine.appHandler.getTextureHandler();
        var soundHandler = engine.appHandler.soundHandler;

        if (loadingBegun) return;
        loaded = false;
        loadingBegun = true;
        retry = false;
        characters.ratCatLoad(textureHandler);
        room.load(textureHandler);
        CameraManager.initShader();
        textureHandler.add("Static/Static");
        textureHandler.addImages("game/", Paths.dataPath + "candys3/game/textures/game.txt");
        if (GameData.night != 2) textureHandler.add("game/BattleOverlay");
        else textureHandler.add("game/HellOverlay");
        for (int i = 1; i <= 11; i++){
            textureHandler.add("game/Moving/Turn Back/Moving" + i);
            if (i == 11) break;
            textureHandler.add("game/Moving/Turn Around/Moving" + i);
            textureHandler.add("game/Moving/Under Bed/Moving" + i);
        }
        soundHandler.addAll(Paths.dataPath + "candys3/game/sounds.txt");
    }

    public void update(Engine engine){
        var input = engine.appHandler.getInput();
        var renderHandler = engine.appHandler.getRenderHandler();
        var soundHandler = engine.appHandler.soundHandler;
        var window = engine.appHandler.window;
        
        boolean cond1 = gameOver && gameOverAlpha == 0 && clicked;
        boolean cond2 = !gameOver && nightTime != 0 && (!player.isJumpscare() || GameData.restartOnJumpscare);
        if ((cond1 && retry) || (cond2 && input.keyTyped(Input.Keys.R))) reset(renderHandler);
        else if ((cond1 && menu) || (cond2 && input.keyTyped(Input.Keys.F2))) {
            characters.dispose();
            engine.candys3Deluxe.setState(0);
            engine.appHandler.getTextureHandler().dispose();
            Jumpscare.reset();
            room.stopMusic();
            soundHandler.stopAllSounds();
            return;
        }
        if (nightTime == 0) soundHandler.stopAllSounds();
        if (!gameOver) {
            renderHandler.screenAlpha = Time.increaseTimeValue(renderHandler.screenAlpha, 1, 2);
            if (scoreAlpha > 0 && !player.isAttack() && !player.isJumpscare()) scoreAlpha = Time.decreaseTimeValue(scoreAlpha, 0, 2);
            else if (scoreAlpha < 1 && (player.isAttack() || player.isJumpscare())) scoreAlpha = Time.increaseTimeValue(scoreAlpha, 1, 4);
            if (!player.isJumpscare()) {
                player.update(window, input, room, characters.getRat(), characters.getCat());
                if (player.getY() == 0) room.input(engine, player);
                room.update(soundHandler, player);
                characters.update(soundHandler, player, room);
            }
            if (player.isJumpscare() && modifySave){
                if (!GameData.flashDebug && !GameData.hitboxDebug && !GameData.noJumpscares
                        && characters.getRat() != null && characters.getCat() != null) writeScore(engine);
                modifySave = false;
            }
            player.updateEffects(room, soundHandler);
            if (!player.isJumpscare()) {
                if (!GameData.hardCassette && room.isMusicPlaying()) fastNightTime = Time.increaseTimeValue(fastNightTime, nightTimeLength / 2, 1);
                nightTime = Time.increaseTimeValue(nightTime, Integer.MAX_VALUE, 1);
            }
            if (nightTime + fastNightTime < nightTimeLength || !modifySave) return;
            if (!GameData.infiniteNight) {
                engine.candys3Deluxe.setState(2);
                engine.appHandler.getTextureHandler().dispose();
                room.stopMusic();
                soundHandler.stopAllSounds();
                if (VideoManager.isPlaying()) VideoManager.stop();
            }

            Candys3Data data = engine.user.candys3Data;

            if (GameData.flashDebug || GameData.hitboxDebug || GameData.noJumpscares
                    || (GameData.expandedPointer && GameData.hitboxMultiplier == 1)
                    || characters.getRat() == null || characters.getCat() == null) return;

            if (!GameData.infiniteNight) characters.dispose();

            if (GameData.night == 0) writeStars(data.mainCastStars);
            else if (GameData.night == 1) writeStars(data.shadowCastStars);
            else writeStars(data.hellCastStars);

            writeScore(engine);
            if (engine.gamejoltManager == null) {
                FileUtils.write(engine.jsonHandler, engine.user, 0);
                modifySave = false;
                return;
            }
            engine.gamejoltManager.execute(() -> {
                String value = engine.jsonHandler.write(engine.user);
                engine.gamejoltManager.dataStore.set(engine.gamejoltManager, "user_id=" + engine.gamejoltManager.id, value);
            });
            String trophyID;
            if (GameData.night == 0 && room.getTapePlayAchievement() < 1) engine.gamejoltManager.trophy.addID("233858");

            if (GameData.hitboxMultiplier < 1 && GameData.hardCassette && GameData.freeScroll) {
                if (GameData.night == 0) trophyID = "233026";
                else if (GameData.night == 1) trophyID = "233090";
                else trophyID = "233887";
                engine.gamejoltManager.trophy.addID(trophyID);
            }

            if (GameData.hitboxMultiplier < 1) {
                if (GameData.night == 0) trophyID = "233023";
                else if (GameData.night == 1) trophyID = "233088";
                else trophyID = "233885";
                engine.gamejoltManager.trophy.addID(trophyID);
            }

            if (GameData.hardCassette) {
                if (GameData.night == 0) trophyID = "233024";
                else if (GameData.night == 1) trophyID = "233089";
                else trophyID = "233886";
                engine.gamejoltManager.trophy.addID(trophyID);
            }

            if (GameData.freeScroll) {
                if (GameData.night == 0) trophyID = "245016";
                else if (GameData.night == 1) trophyID = "245017";
                else trophyID = "245018";
                engine.gamejoltManager.trophy.addID(trophyID);
            }

            if (GameData.night == 0) trophyID = "232928";
            else if (GameData.night == 1) trophyID = "233087";
            else trophyID = "233884";
            engine.gamejoltManager.trophy.addID(trophyID);
            modifySave = false;
            return;
        }
        whiteAlpha = Time.decreaseTimeValue(whiteAlpha, 0, 2);
        staticFrame = Time.increaseTimeValue(staticFrame, 8192, 30);
        if (staticFrame == 8192) staticFrame = 0;
        if (clicked) gameOverAlpha = Time.decreaseTimeValue(gameOverAlpha, 0, 1);
        else {
            retry = input.mouseOver(545, 297, 62, 17);
            menu = input.mouseOver(671, 297, 66, 17);
            clicked = input.isLeftPressed() && (retry || menu);
        }
    }

    public void reset(RenderHandler renderHandler){
        characters.reset();
        player.reset();
        room.reset();
        Jumpscare.reset();
        nightTime = 0;
        fastNightTime = 0;
        renderHandler.screenAlpha = 0;
        scoreAlpha = 1;
        gameOver = false;
        clicked = false;
        menu = false;
        retry = false;
        modifySave = true;
        loaded = true;
        loadingBegun = false;
    }

    private void renderGame(Engine engine){
        var renderHandler = engine.appHandler.getRenderHandler();
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = renderHandler.batch;
        var window = engine.appHandler.window;
        var input = engine.appHandler.getInput();
        var fontManager = engine.appHandler.getFontManager();
        var candysFont = engine.appHandler.getFontManager().getFont("candys3/candysFont");

        player.adjustCamera(input);
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        renderHandler.shapeDrawer.update();
        batch.enableBlending();
        renderHandler.batchBegin();

        room.render(engine, characters, player.getFlashlight());

        if (player.getOverlayAlpha() > 0) {
            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            //DON'T CHANGE THIS!!!
            batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);
            TextureRegion region;
            if (GameData.night != 2) region = textureHandler.get("game/BattleOverlay");
            else region = textureHandler.get("game/HellOverlay");
            if (GameData.night == 0) batch.setColor(player.getOverlayAlpha() * 0.85f, 0, player.getOverlayAlpha() * 0.5f, 1);
            else if (GameData.night == 1) batch.setColor(player.getOverlayAlpha() * 0.75f, 0, player.getOverlayAlpha(), 1);
            else batch.setColor(player.getOverlayAlpha(), player.getOverlayAlpha(), player.getOverlayAlpha(), 1);
            batch.draw(region, CameraManager.getX(), CameraManager.getY(), 1280, 720);
            batch.setColor(1, 1, 1, 1);
            batch.flush();
            batch.setBlendFunction(srcFunc, dstFunc);
        }
        if (player.isJumpscare() && player.getBlacknessTimes() == 0){
            renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
            renderHandler.drawScreen();
            if (room.isMusicPlaying()) room.stopMusic();
            if ((Jumpscare.isVideo() && !Jumpscare.render(engine.appHandler.soundHandler, batch))
                || (!Jumpscare.isVideo() && !Jumpscare.classicShadowRender(engine.appHandler.soundHandler, batch, textureHandler))) {
                gameOver = true;
                whiteAlpha = 1;
                gameOverAlpha = 1;
                renderHandler.screenAlpha = 1;
                renderHandler.shapeDrawer.setColor(1, 1, 1, 1);
                renderHandler.drawScreen();
                return;
            }
        }
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - player.getBlacknessAlpha());
        renderHandler.drawScreen();

        renderHandler.shapeDrawer.setColor(0.2f, 0, 0.2f, player.getPurpleAlpha());
        renderHandler.drawScreen();

        if (player.getButtonFade() > 0) {
            batch.setColor(1, 1, 1, player.getButtonFade());
            TextureRegion region;
            switch (room.getState()) {
                case 0:
                    region = textureHandler.get("game/Buttons/TapePlayer");
                    batch.draw(region, CameraManager.getX() + 132, CameraManager.getY());

                    region = textureHandler.get("game/Buttons/UnderBed");
                    batch.draw(region, CameraManager.getX() + 732, CameraManager.getY());
                    break;
                case 1:
                    region = textureHandler.get("game/Buttons/UnderBedBack");
                    batch.draw(region, CameraManager.getX() + 430, CameraManager.getY());
                    break;
                case 2:
                    region = textureHandler.get("game/Buttons/TapePlayerBack");
                    batch.draw(region, CameraManager.getX() + 430, CameraManager.getY());
                    break;
            }
        }
        batch.setColor(1, 1, 1, 1);
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - renderHandler.screenAlpha);
        renderHandler.drawScreen();
        FrameBufferManager.end(batch, renderHandler.screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, renderHandler.screenBuffer, true);

        fontManager.setCurrentFont(candysFont);
        fontManager.setSize(40);
        candysFont.setColor(1, 1, 1, 1);
        nightTimeBuilder.delete(0, nightTimeBuilder.length());
        if (GameData.infiniteNight) {
            int hour = (int) nightTime / 60;
            nightTimeBuilder.append(hour).append(":");
            int tempTime = (int) (nightTime % 60);
            if (tempTime < 10) nightTimeBuilder.append(0);
            nightTimeBuilder.append(tempTime);
        } else {
            int hour = (int) ((nightTime + fastNightTime) / nightTimeLength * 6);
            if (hour == 0) hour = 12;
            nightTimeBuilder.append(hour).append(" AM");
        }
        fontManager.setText(nightTimeBuilder.toString());
        fontManager.setOutline(0.35f);
        fontManager.setRelativePosition(
                window.width() - fontManager.getLayout().width - 20,
                window.height() - fontManager.getLayout().height + 8);
        fontManager.render(batch);
        fontManager.setOutline(0.5f);

        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, scoreAlpha, false);
        fontManager.setText("Score: " + player.getFlashPoints());

        fontManager.setOutline(0.25f);
        if (GameData.night != 1) fontManager.setColor(0.2f, 0, 0, scoreAlpha);
        else fontManager.setColor(0.1f, 0, 0.2f, scoreAlpha);
        fontManager.setRelativePosition(20, window.height() - fontManager.getLayout().height + 8);
        fontManager.render(batch);
        fontManager.setOutline(0.5f);
        fontManager.setColor(0, 0, 0, 1);

        if (!GameData.flashDebug && !GameData.hitboxDebug && !GameData.noJumpscares
            && (!GameData.expandedPointer || GameData.hitboxMultiplier != 1)) return;
        fontManager.setSize(24);
        candysFont.setColor(1, 0, 0, 1);
        fontManager.setText("Cheats on!");
        fontManager.setOutline(0.25f);
        fontManager.setColor(0.15f, 0, 0, 1);
        fontManager.setRelativePosition(
                window.width() - fontManager.getLayout().width - 20,
                window.height() - fontManager.getLayout().height - 40);
        fontManager.render(batch);
        fontManager.setOutline(0.5f);
        fontManager.setColor(0, 0, 0, 1);
    }

    private void renderGameOver(Engine engine){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var window = engine.appHandler.window;
        var textureHandler = engine.appHandler.getTextureHandler();
        var fontManager = engine.appHandler.getFontManager();
        var candysFont = engine.appHandler.getFontManager().getFont("candys3/candysFont");

        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        renderHandler.shapeDrawer.update();
        batch.enableBlending();
        renderHandler.batchBegin();
        renderHandler.screenBuffer.begin();

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();

        TextureRegion region = textureHandler.getRegion("Static/Static", 1024, (int) staticFrame % 8);
        engine.candys3Deluxe.setNightColor(engine, 2);
        batch.draw(region, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, 1);
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - renderHandler.screenAlpha);
        renderHandler.drawScreen();
        FrameBufferManager.end(batch, renderHandler.screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, renderHandler.screenBuffer, true);

        fontManager.setCurrentFont(candysFont);
        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, 1, false);
        fontManager.setSize(54);
        fontManager.setText("GAME OVER");
        fontManager.setPosition(true, true,
                (float) window.width() / 2,
                (float) window.height() / 2 + 26);
        fontManager.render(batch);

        fontManager.setSize(26);
        if (!retry) engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, 1, false);
        else candysFont.setColor(1, 1, 1, 1);
        fontManager.setText("Retry");
        fontManager.setPosition(true, true,
                (float) window.width() / 2 - 64,
                (float) window.height() / 2 - 54);
        fontManager.render(batch);

        if (!menu) engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, 1, false);
        else candysFont.setColor(1, 1, 1, 1);
        fontManager.setText("Menu");
        fontManager.setPosition(true, true,
                (float) window.width() / 2 + 64,
                (float) window.height() / 2 - 54);
        fontManager.render(batch);

        renderHandler.shapeDrawer.setColor(1, 1, 1, whiteAlpha);
        renderHandler.drawScreen();

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - gameOverAlpha);
        renderHandler.drawScreen();
    }

    public void render(Engine engine){
        if (!gameOver) renderGame(engine);
        else renderGameOver(engine);
        if (GameData.hitboxDebug || GameData.flashDebug) characters.debug(
                engine.appHandler.getRenderHandler(),
                engine.appHandler.window, player, room);
    }

    private void writeScore(Engine engine){
        StringBuilder time = new StringBuilder();
        int hour = (int) nightTime / 60;
        time.append(hour).append(":");
        int tempTime = (int) (nightTime % 60);
        if (tempTime < 10) time.append(0);
        time.append(tempTime);

        if (engine.gamejoltManager != null) {
            engine.gamejoltManager.execute(() -> {
                String tableID = GameData.night == 0 ? "767298" : GameData.night == 1 ? "909490" : "909960";
                scoreFetch(engine, tableID, Integer.parseInt(player.getFlashPoints()), player.getFlashPoints() + " Points");
                tableID = GameData.night == 0 ? "909489" : GameData.night == 1 ? "909491" : "909961";
                scoreFetch(engine, tableID, (int) nightTime, time + " seconds");
            });
        }

        if (engine.scoreTable != null){
            engine.scoreTable.setScore(GameData.night, player.getFlashPoints() + " Points", time + " seconds");
            FileUtils.write(engine.jsonHandler, engine.scoreTable, 1);
        }
    }

    private void scoreFetch(Engine engine, String tableID, int sort, String value){
        assert engine.gamejoltManager != null;
        Score score = engine.gamejoltManager.score.fetch(engine.gamejoltManager, engine.jsonHandler, tableID);
        if (score == null || sort >= Integer.parseInt(score.getSort()))
            engine.gamejoltManager.score.add(engine.gamejoltManager, tableID, String.valueOf(sort), value);
    }

    private void writeStars(int[] modeStars) {
        modeStars[0] = 1;
        if (GameData.hitboxMultiplier < 1) modeStars[1] = 1;
        if (GameData.hardCassette) modeStars[2] = 1;
        if (GameData.freeScroll) modeStars[3] = 1;
        if (GameData.hitboxMultiplier < 1 && GameData.hardCassette && GameData.freeScroll) {
            modeStars[4] = 1;
        }
    }

    public void dispose(){
        room.dispose();
    }
}