package candys3.Game;

import candys3.GameData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.deluxe.Candys3Data;
import candys3.Game.Objects.*;
import candys3.Game.Objects.Character.Characters;
import candys3.Game.Objects.Functions.Jumpscare;
import util.*;
import util.deluxe.StarData;
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
    public final Characters characters;
    private final Player player;
    private final Room room;
    public boolean loaded;
    public boolean loadingBegun;

    public int night;
    public boolean laserVision;
    public boolean laserPointer;
    public boolean hardCassette;

    public boolean flashDebug;
    public boolean hitboxDebug;
    public boolean noJumpscares;
    public boolean expandedVision;

    public boolean freeScroll;
    public boolean infiniteNight;
    public boolean perspectiveEffect;
    public boolean classicJumpscares;

    public static FrameBuffer lightBuffer;
    public static FrameBuffer roomBuffer;
    public static FrameBuffer ratDebugBuffer;
    public static FrameBuffer catDebugBuffer;
    public static FrameBuffer vinnieDebugBuffer;

    public Game(){
        characters = new Characters();
        player = new Player();
        room = new Room();
        byte nightMinuteLength = 8;
        byte minute = 60;
        float debugMinute = 0.25f;
        nightTimeLength = minute * nightMinuteLength;
        nightTimeBuilder = new StringBuilder();
    }

    public void load(Engine engine){
        var textureHandler = engine.appHandler.getTextureHandler();
        var soundHandler = engine.appHandler.soundHandler;
        var menu = engine.candys3Deluxe.getMenu();
        
        if (loadingBegun) return;
        loaded = false;
        loadingBegun = true;
        retry = false;
        night = menu.night;
        laserVision = menu.laserVisionButton.isSelected();
        laserPointer = menu.laserPointerButton.isSelected();
        hardCassette = menu.hardCassetteButton.isSelected();

        GameData.hitboxMultiplier = laserPointer ? 0.75f : 1;

        var cheats = menu.options.get(2);
        flashDebug = cheats.get(0).isSelected();
        hitboxDebug = cheats.get(1).isSelected();
        noJumpscares = cheats.get(2).isSelected();
        expandedVision = cheats.get(3).isSelected();

        var options = menu.options.get(3);
        freeScroll = options.get(0).isSelected();
        infiniteNight = options.get(1).isSelected();
        perspectiveEffect = options.get(2).isSelected();
        classicJumpscares = options.get(3).isSelected();
        
        characters.ratCatLoad(textureHandler, menu);
        room.load(textureHandler);
        CameraManager.initShader("perspective");
        textureHandler.add("Static/Static");
        textureHandler.addImages("game/", "candys3/game/textures/game.txt");
        if (night != 2) textureHandler.add("game/BattleOverlay");
        else textureHandler.add("game/HellOverlay");
        for (int i = 1; i <= 11; i++){
            textureHandler.add("game/Moving/Turn Back/Moving" + i);
            if (i == 11) break;
            textureHandler.add("game/Moving/Turn Around/Moving" + i);
            textureHandler.add("game/Moving/Under Bed/Moving" + i);
        }
        soundHandler.addAll("res/data/candys3/game/sounds.txt");
    }

    public void reset(RenderHandler renderHandler){
        characters.reset(night);
        room.reset(hardCassette);
        player.reset(room);
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

    public void update(Engine engine){
        var input = engine.appHandler.getInput();
        var renderHandler = engine.appHandler.getRenderHandler();
        var soundHandler = engine.appHandler.soundHandler;
        var window = engine.appHandler.window;

        boolean cond1 = gameOver && gameOverAlpha == 0 && clicked;
        boolean cond2 = !gameOver && nightTime != 0 && !player.isJumpscare();
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
            if (player.isAttack() || player.isJumpscare()) scoreAlpha = Time.increaseTimeValue(scoreAlpha, 1, 4);
            else scoreAlpha = Time.decreaseTimeValue(scoreAlpha, 0, 2);
            if (!player.isJumpscare()) {
                player.update(this, window, input, room, characters.getRat(), characters.getCat());
                if (player.getY() < 720 - room.height) room.input(engine, this, player);
                room.update(soundHandler, this, player);
                characters.update(soundHandler, this, player, room);
                if (!hardCassette && room.isMusicPlaying()) fastNightTime = Time.increaseTimeValue(fastNightTime, nightTimeLength / 2, 1);
                nightTime = Time.increaseTimeValue(nightTime, Integer.MAX_VALUE, 1);
            }

            boolean cheating = flashDebug || hitboxDebug || noJumpscares
                    || (expandedVision && !laserVision)
                    || characters.getRat() == null || characters.getCat() == null;

            Candys3Data data = engine.user.candys3Data;

            if (player.isJumpscare() && modifySave){
                if (!cheating) {
                    if (night == 0) writeStars(engine, data.mainCastStars, false);
                    else if (night == 1) writeStars(engine, data.shadowCastStars, false);
                    else writeStars(engine, data.hellCastStars, false);
                    writeScore(engine);
                }
                modifySave = false;
            }
            player.updateEffects(this, room, soundHandler);
            if (nightTime + fastNightTime < nightTimeLength || !modifySave) return;
            if (!infiniteNight) {
                engine.candys3Deluxe.setState(2);
                engine.appHandler.getTextureHandler().dispose();
                room.stopMusic();
                soundHandler.stopAllSounds();
                if (VideoManager.isPlaying()) VideoManager.stop();
            }

            if (cheating) return;

            if (!infiniteNight) characters.dispose();

            StarData[] starData;

            if (night == 0) starData = data.mainCastStars;
            else if (night == 1) starData = data.shadowCastStars;
            else starData = data.hellCastStars;

            boolean gamejoltExists = writeStars(engine, starData, true);

            writeScore(engine);

            if (!gamejoltExists || engine.gamejoltManager == null) return;

            String trophyID;
            if (night == 0 && room.getTapePlayAchievement() < 1) engine.gamejoltManager.trophy.addID("233858");

            if (laserPointer && laserVision && hardCassette) {
                if (night == 0) trophyID = "233026";
                else if (night == 1) trophyID = "233090";
                else trophyID = "233887";
                engine.gamejoltManager.trophy.addID(trophyID);
            }

            if (laserPointer) {
                if (night == 0) trophyID = "233023";
                else if (night == 1) trophyID = "233088";
                else trophyID = "233885";
                engine.gamejoltManager.trophy.addID(trophyID);
            }

            if (hardCassette) {
                if (night == 0) trophyID = "233024";
                else if (night == 1) trophyID = "233089";
                else trophyID = "233886";
                engine.gamejoltManager.trophy.addID(trophyID);
            }

            if (laserVision) {
                if (night == 0) trophyID = "245016";
                else if (night == 1) trophyID = "245017";
                else trophyID = "245018";
                engine.gamejoltManager.trophy.addID(trophyID);
            }

            if (night == 0) trophyID = "232928";
            else if (night == 1) trophyID = "233087";
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

    private void renderGame(Engine engine){
        var renderHandler = engine.appHandler.getRenderHandler();
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = renderHandler.batch;
        var window = engine.appHandler.window;
        var input = engine.appHandler.getInput();
        var fontManager = engine.appHandler.getFontManager();
        var candysFont = engine.appHandler.getFontManager().getFont("candys3/candysFont");

        player.adjustCamera(room, input);
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        renderHandler.shapeDrawer.update();
        batch.enableBlending();
        renderHandler.batchBegin();

        if (hitboxDebug || flashDebug){
            characters.debug(batch, engine.appHandler.getRenderHandler(), engine.appHandler.window, flashDebug, hitboxDebug, player, room);
        }

        room.render(engine, characters, player.getFlashlight(), this);

        if (player.getOverlayAlpha() > 0) {
            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            //DON'T CHANGE THIS!!!
            batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);
            TextureRegion region;
            if (night != 2) region = textureHandler.get("game/BattleOverlay");
            else region = textureHandler.get("game/HellOverlay");
            if (night == 0) batch.setColor(player.getOverlayAlpha() * 0.85f, 0, player.getOverlayAlpha() * 0.5f, 1);
            else if (night == 1) batch.setColor(player.getOverlayAlpha() * 0.75f, 0, player.getOverlayAlpha(), 1);
            else batch.setColor(player.getOverlayAlpha(), player.getOverlayAlpha(), player.getOverlayAlpha(), 1);
            batch.draw(region, CameraManager.getX(), CameraManager.getY(), 1280, 720);
            batch.setColor(1, 1, 1, 1);
            batch.flush();
            batch.setBlendFunction(srcFunc, dstFunc);
        }
        if (player.isJumpscare() && player.getBlacknessTimes() == 0){
            renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
            renderHandler.drawScreen();
            room.stopMusic();
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

        if (hitboxDebug) {
            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            //DON'T CHANGE THIS!!!
            batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);

            if (characters.getRat() != null) {
                int type = characters.getRat().getType();
                if (type == 0) batch.setColor(0.5f, 0.35f, 0.35f, 1);
                else if (type == 1) batch.setColor(0.5f, 0, 0.5f, 1);
                else batch.setColor(0.5f, 0.375f, 0.375f, 1);
                FrameBufferManager.render(batch, ratDebugBuffer, true, room.width, room.height);
            }

            if (characters.getCat() != null) {
                int type = characters.getCat().getType();
                if (type == 0) batch.setColor(0.75f, 0.5f, 0.5f, 1);
                else if (type == 1) batch.setColor(0.75f, 0, 0.75f, 1);
                else batch.setColor(0.75f, 0.25f, 0.25f, 1);
                FrameBufferManager.render(batch, catDebugBuffer, true, room.width, room.height);
            }

            batch.setColor(1, 1, 1, 1);
            batch.flush();
            batch.setBlendFunction(srcFunc, dstFunc);
        }

        fontManager.setCurrentFont(candysFont);
        fontManager.setSize(40);
        candysFont.setColor(1, 1, 1, 1);
        nightTimeBuilder.delete(0, nightTimeBuilder.length());
        if (infiniteNight) {
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
        fontManager.setOutlineLength(0.35f);
        fontManager.setRelativePosition(
                window.width() - fontManager.getLayout().width - 20,
                window.height() - fontManager.getLayout().height + 8);
        fontManager.render(batch);
        fontManager.setOutlineLength(0.5f);

        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, night, scoreAlpha, false);
        fontManager.setText("Score: " + player.getFlashPoints());

        fontManager.setOutlineLength(0.25f);
        if (night != 1) fontManager.setOutlineColor(0.2f, 0, 0, scoreAlpha);
        else fontManager.setOutlineColor(0.1f, 0, 0.2f, scoreAlpha);
        fontManager.setRelativePosition(20, window.height() - fontManager.getLayout().height + 8);
        fontManager.render(batch);
        fontManager.setOutlineLength(0.5f);
        fontManager.setOutlineColor(0, 0, 0, 1);

        if (!flashDebug && !hitboxDebug && !noJumpscares
            && (!expandedVision || laserPointer)) return;
        fontManager.setSize(24);
        candysFont.setColor(1, 0, 0, 1);
        fontManager.setText("Cheats on!");
        fontManager.setOutlineLength(0.25f);
        fontManager.setOutlineColor(0.15f, 0, 0, 1);
        fontManager.setRelativePosition(
                window.width() - fontManager.getLayout().width - 20,
                window.height() - fontManager.getLayout().height - 40);
        fontManager.render(batch);
        fontManager.setOutlineLength(0.5f);
        fontManager.setOutlineColor(0, 0, 0, 1);
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
        engine.candys3Deluxe.setNightColor(engine, night, 2);
        batch.draw(region, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, 1);
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - renderHandler.screenAlpha);
        renderHandler.drawScreen();
        FrameBufferManager.end(batch, renderHandler.screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, renderHandler.screenBuffer, true);

        fontManager.setCurrentFont(candysFont);
        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, night, 1, false);
        fontManager.setSize(54);
        fontManager.setText("GAME OVER");
        fontManager.setPosition(true, true,
                (float) window.width() / 2,
                (float) window.height() / 2 + 26);
        fontManager.render(batch);

        fontManager.setSize(26);
        if (!retry) engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, night, 1, false);
        else candysFont.setColor(1, 1, 1, 1);
        fontManager.setText("Retry");
        fontManager.setPosition(true, true,
                (float) window.width() / 2 - 64,
                (float) window.height() / 2 - 54);
        fontManager.render(batch);

        if (!menu) engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, night, 1, false);
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
                String tableID = night == 0 ? "767298" : night == 1 ? "909490" : "909960";
                scoreFetch(engine, tableID, Integer.parseInt(player.getFlashPoints()), player.getFlashPoints() + " Points");
                tableID = night == 0 ? "909489" : night == 1 ? "909491" : "909961";
                scoreFetch(engine, tableID, (int) nightTime, time + " seconds");
            });
        }

        if (engine.scoreTable == null) return;
        engine.scoreTable.setCandys3Score(night, player.getFlashPoints() + " Points", time + " seconds");
        FileUtils.writeTable(engine.jsonHandler, engine.scoreTable);
    }

    private void scoreFetch(Engine engine, String tableID, int sort, String value){
        assert engine.gamejoltManager != null;
        Score score = engine.gamejoltManager.score.fetch(engine.gamejoltManager, engine.jsonHandler, tableID);
        if (score == null || sort >= Integer.parseInt(score.getSort()))
            engine.gamejoltManager.score.add(engine.gamejoltManager, tableID, String.valueOf(sort), value);
    }

    private boolean writeStars(Engine engine, StarData[] starsData, boolean complete) {
        setStar(starsData[0], complete);
        if (laserVision) setStar(starsData[1], complete);
        if (laserPointer) setStar(starsData[2], complete);
        if (hardCassette) setStar(starsData[3], complete);
        if (laserVision && laserPointer && hardCassette) setStar(starsData[4], complete);

        if (engine.gamejoltManager == null) {
            FileUtils.writeUser(engine.jsonHandler, engine.user);
            return false;
        }
        engine.gamejoltManager.execute(() -> {
            String value = engine.jsonHandler.writeCandysUser(engine.user);
            engine.gamejoltManager.dataStore.set(engine.gamejoltManager, "user_id=" + engine.gamejoltManager.id, value);
        });

        return true;
    }

    private void setStar(StarData starData, boolean complete){
        starData.time = Math.max((int) nightTime, starData.time);
        if (!complete) return;
        starData.complete = true;
        if (freeScroll) starData.special = true;
    }

    public void dispose(){
        room.dispose();
    }
}