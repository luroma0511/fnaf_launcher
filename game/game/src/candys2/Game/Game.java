package candys2.Game;

import candys2.Game.mode.CandysShowdown;
import candys2.Game.mode.RatCatTheater;
import candys2.Game.player.Player;
import candys2.Menu.Menu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.*;
import util.deluxe.Candys2Data;
import util.deluxe.StarData;
import util.gamejolt.Score;

public class Game {
    private final StringBuilder nightTimeBuilder = new StringBuilder();
    private final FrameBuffer fbo = FrameBufferManager.newFrameBuffer(1440, 768);
    private final FrameBuffer officeBuffer = FrameBufferManager.newFrameBuffer(1440, 768);
    private final FrameBuffer laserPointerBuffer = FrameBufferManager.newFrameBuffer(1440, 768);
    private final OrthographicCamera framebufferCamera = new OrthographicCamera();

    private final CandysShowdown candysShowdown = new CandysShowdown();
    private final RatCatTheater ratCatTheater = new RatCatTheater();
    private final Player player = new Player();

    private boolean reset;
    private boolean loaded;

    private boolean retryHover;
    private boolean menuHover;

    private boolean shadow;
    private boolean modifySave;
    private boolean maxMode;
    private int mode;
    private boolean ambiencePlaying;
    private float nightTime;

    private boolean laserPointer;
    private boolean faultyBattery;
    private boolean faultyPhones;

    private boolean cheating;
    private boolean mapDebug;
    private boolean hitboxDebug;
    private boolean noJumpscares;

    private boolean infiniteNight;
    private boolean perspectiveEffect;

    private boolean gameOver;
    private float gameOverAlpha;
    private float staticFrame;
    private float whiteAlpha;
    private final float minuteLength = 60;

    {
        framebufferCamera.setToOrtho(false, 1440, 768);
        framebufferCamera.update();
    }

    public void load(Engine engine, int mode, boolean shadow){
        loaded = false;
        this.shadow = shadow;
        this.mode = mode;
        var menu = engine.candys2Deluxe.menu;
        var textureHandler = engine.appHandler.getTextureHandler();

        maxMode = (mode == 0
                && menu.candy.getAi() == 20
                && menu.cindy.getAi() == 20
                && menu.chester.getAi() == 20
                && menu.penguin.getAi() == 20
                && menu.blank.getAi() == 20);

        laserPointer = menu.laserPointerButton.isSelected();
        faultyBattery = menu.faultyBatteryButton.isSelected();
        faultyPhones = menu.faultyPhonesButton.isSelected();

        var cheats = menu.getOptions().get(2);
        mapDebug = cheats.get(0).isSelected();
        hitboxDebug = cheats.get(1).isSelected();
        noJumpscares = cheats.get(2).isSelected();
        cheating = mapDebug || hitboxDebug || noJumpscares;
        modifySave = !cheating;

        var options = menu.getOptions().get(3);
        infiniteNight = options.get(0).isSelected();
        perspectiveEffect = options.get(1).isSelected();

        textureHandler.add("game/office/deskLayer");
        textureHandler.add("game/office/cabinetLayer");
        textureHandler.add("game/office/office");
        textureHandler.add("game/office/officeDark");
        textureHandler.add("game/office/Flashlight");

        textureHandler.add("game/camera/monitor");
        textureHandler.add("game/camera/cameras");
        textureHandler.add("game/camera/vignette");
        textureHandler.add("game/camera/reboot");
        textureHandler.add("game/camera/static");

        textureHandler.add("game/gui/button");
        textureHandler.add("game/gui/ui");
        textureHandler.add("game/gui/hover");
        textureHandler.add("game/gui/phone");
        textureHandler.add("game/gui/phoneCooldown");
        textureHandler.add("game/gui/error");

        textureHandler.add("static/static1");
        textureHandler.add("static/newspaper");
        textureHandler.add("game/newspaper");
        textureHandler.add("game/6am");
        for (int i = 1; i <= 6; i++){
            textureHandler.add("game/gui/camera" + i);
        }

        if (mode == 0){
            candysShowdown.load(menu, textureHandler, shadow, mapDebug);
        } else {
            ratCatTheater.load(menu, textureHandler, shadow, mapDebug);
        }
        engine.appHandler.soundHandler.addAll("res/data/candys2/game/sounds.txt");
        CameraManager.initShader("candys2");
    }

    public void reset(TextureHandler textureHandler, Menu menu){
        player.reset();
        if (!loaded) player.load(textureHandler, mapDebug);
        loaded = true;
        if (mode == 0) {
            candysShowdown.reset(menu);
        } else {
            ratCatTheater.reset(menu);
        }

        reset = false;
        ambiencePlaying = false;
        nightTime = 0;
    }

    public void update(Engine engine){
        if (engine.appHandler.getRenderHandler().lock) return;
        if (!gameOver) updateGame(engine);
        else updateGameover(engine);
    }

    private void updateGame(Engine engine){
        var soundHandler = engine.appHandler.soundHandler;

        var restartGame = engine.user.restartGameKey;
        var returnMenu = engine.user.returnMenuKey;
        var flashKey = engine.user.candys2Data.flashKey;

        if (engine.appHandler.getInput().keyTyped(returnMenu)){
            engine.appHandler.getRenderHandler().screenAlpha = 0;
            engine.candys2Deluxe.setState(0);
            engine.appHandler.getTextureHandler().dispose();
            player.dispose();
            soundHandler.stopAllSounds();
            VideoManager.stop();
            return;
        } else if (engine.appHandler.getInput().keyTyped(restartGame)) {
            soundHandler.stopAllSounds();
            VideoManager.stop();
            reset = true;
        }

        if (reset) reset(engine.appHandler.getTextureHandler(), engine.candys2Deluxe.menu);

        if (!ambiencePlaying){
            soundHandler.play("ambience");
            soundHandler.setSoundEffect(SoundHandler.LOOP, "ambience", 1);
            ambiencePlaying = true;
        }

        Candys2Data data = engine.user.candys2Data;

        if (player.jumpscareDelay > 0 || player.jumpscareEnemy.isEmpty()) {
            player.update(engine, faultyBattery, faultyPhones, flashKey);
            if (mode == 0) candysShowdown.update(soundHandler, player, laserPointer, noJumpscares);
            else ratCatTheater.update(soundHandler, player, laserPointer, noJumpscares);
            nightTime += Time.getDelta();
        } else if (modifySave && maxMode){
            writeStars(engine, shadow ? data.shadowShowdownStars : data.newCandysShowdownStars, false);
            modifySave = false;
        }

        float length = minuteLength * 6;

        if (nightTime < length || !modifySave || !maxMode) return;
        modifySave = false;

        writeStars(engine, shadow ? data.shadowShowdownStars : data.newCandysShowdownStars, true);
        writeScore(engine);
    }

    private void updateGameover(Engine engine){
        var input = engine.appHandler.getInput();

        staticFrame += Time.getDelta() * 20;
        if (staticFrame >= 4) staticFrame = 0;

        whiteAlpha -= Time.getDelta() * 2;
        if (whiteAlpha < 0) whiteAlpha = 0;

        if (retryHover && input.isLeftPressed()){
            reset = true;
        }

        if (menuHover && input.isLeftPressed()){
            engine.appHandler.getRenderHandler().screenAlpha = 0;
            engine.candys2Deluxe.setState(0);
            engine.appHandler.getTextureHandler().dispose();
            player.dispose();
            engine.appHandler.soundHandler.stopAllSounds();
            reset = true;
            gameOver = false;
        }

        if (reset){
            gameOverAlpha -= Time.getDelta() * 1.5f;
            if (gameOverAlpha <= 0){
                gameOverAlpha = 0;
                gameOver = false;
                engine.appHandler.soundHandler.stopAllSounds();
                reset(engine.appHandler.getTextureHandler(), engine.candys2Deluxe.menu);
            }
        }
    }

    public void render(Engine engine){
        if (gameOver) renderGameover(engine);
        else renderGame(engine);
    }

    private void renderGame(Engine engine){
        if (engine.appHandler.getRenderHandler().lock) return;
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();
        var fontManager = engine.appHandler.getFontManager();
        var window = engine.appHandler.window;
        TextureRegion region;

        //adjust camera and begin batch
        CameraManager.setOrigin();
        renderHandler.shapeDrawer.update();
        batch.enableBlending();

        batch.setProjectionMatrix(framebufferCamera.combined);
        Gdx.gl.glViewport(0, 0, 1440, 768);
        renderHandler.batchBegin();

        if (laserPointer && player.flashAlpha > 0) {
            laserPointerBuffer.begin();
            renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
            renderHandler.shapeDrawer.filledRectangle(0, 0, 1440, 768);
            region = textureHandler.get("game/office/Flashlight");
            float length = (float) region.getRegionWidth() / 2;
            batch.draw(region, player.flashX - player.position - length, player.flashY + 24 - length);
            FrameBufferManager.end(batch, laserPointerBuffer, 1440, 768);
        }
        if (player.flashAlpha > 0) {
            officeBuffer.begin();
            renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
            renderHandler.shapeDrawer.filledRectangle(0, 0, 1440, 768);
            region = textureHandler.get("game/office/office");
            batch.draw(region, -player.position, 0);
            if (mode == 0) {
                candysShowdown.renderHall1(engine, -player.position);
                candysShowdown.renderHall2(engine, -player.position);
            } else {
                ratCatTheater.renderHall1(engine, -player.position);
                ratCatTheater.renderHall2(engine, -player.position);
            }
            region = textureHandler.get("game/office/cabinetLayer");
            batch.draw(region, -player.position, 0);

            if (mode == 0) candysShowdown.renderHall3(engine, -player.position);
            else ratCatTheater.renderHall3(engine, -player.position);

            region = textureHandler.get("game/office/deskLayer");
            batch.draw(region, -player.position, 0);

            if (laserPointer) {
                var srcFunc = batch.getBlendSrcFunc();
                var dstFunc = batch.getBlendDstFunc();
                batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
                FrameBufferManager.rawRender(batch, laserPointerBuffer);
                batch.flush();
                batch.setBlendFunction(srcFunc, dstFunc);
            }
            FrameBufferManager.end(batch, officeBuffer, 1440, 768);
        }

        fbo.begin();
        float lightAlpha = (float) Math.sin(player.flashAlpha * (Math.PI / 2));
        region = textureHandler.get("game/office/officeDark");
        batch.setColor(1, 1, 1, 1);
        batch.draw(region, -player.position, 0);

        var srcFunc = batch.getBlendSrcFunc();
        var dstFunc = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
        batch.setColor(1, 1, 1, lightAlpha);
        FrameBufferManager.rawRender(batch, officeBuffer);
        batch.flush();
        batch.setColor(1, 1, 1, 1);
        batch.setBlendFunction(srcFunc, dstFunc);

        FrameBufferManager.end(batch, fbo, 1440, 768);
        batch.end();

        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        Gdx.gl.glViewport(0, 0, window.width(), window.height());
        batch.begin();

        renderHandler.screenBuffer.begin();
        if (perspectiveEffect) {
            CameraManager.applyShader(batch);
            CameraManager.setUniform("u_cameraX", player.position - 80);
            CameraManager.setUniform("u_distortionAmount", 0.00125f);
        }

        float multiplier = (1 + 0.075f * player.zoomFactor);
        float distance = (1 + 0.0375f * player.zoomFactor);
        float xDistance = 1440 - (1440 * distance);
        float yDistance = 768 - (768 * distance);

        FrameBufferManager.rawRender(batch, fbo, xDistance, -24 + yDistance, 1440 * multiplier, 768 * multiplier);
        batch.setShader(null);

        if ((int) player.roomFrame > 0){
            region = textureHandler.getRegion("game/camera/monitor", 1088, (int) player.roomFrame - 1);
            batch.draw(region, 192, 0);
        }

        if (player.inCamera){
            int width = 812;
            int height = 609;
            int x = 234;
            int y = 20;
            if (player.monitor.error){
                if (mode == 0) {
                    batch.draw(textureHandler.get("game/enemy/penguin/glitch"), x, y, width, height);
                    region = textureHandler.getRegion("game/gui/error", 374, 0);
                    batch.draw(region, x + (float) width / 2 - (float) region.getRegionWidth() / 2,
                            y + (float) height / 2 - (float) region.getRegionHeight() / 2);
                } else batch.draw(textureHandler.get("game/enemy/rat/glitch"), x, y, width, height);
            } else if (player.monitor.glitchCooldown > 0) {
                region = textureHandler.getRegion("game/camera/reboot", 1024, (int) player.monitor.glitchFrame);
                region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                batch.draw(region, x, y, width, height);
            } else if (player.signalLost > 0) {
                region = textureHandler.getRegion("game/camera/static", 1024, (int) (6 - player.signalLost));
                batch.draw(region, x, y, width, height);
            } else {
                boolean character = false;
                if (mode == 0) character = candysShowdown.renderCamera(engine, player.monitor.activeCamera);
                else if (mode == 1) character = ratCatTheater.renderCamera(engine, player.monitor.activeCamera);
                if (!character) {
                    region = textureHandler.getRegion("game/camera/cameras", 1024, player.monitor.activeCamera - 1);
                    batch.draw(region, x, y, width, height);
                }

                srcFunc = batch.getBlendSrcFunc();
                dstFunc = batch.getBlendDstFunc();
                batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_SRC_ALPHA);
                region = textureHandler.getRegion("static/static1", 1024, (int) player.staticFrame);
                batch.setColor(0.5f, 0.5f, 0.5f, 1);
                batch.draw(region, x, y, width, height);
                batch.setColor(1, 1, 1, 1);
                batch.flush();
                batch.setBlendFunction(srcFunc, dstFunc);
            }

            if (!player.monitor.error && player.monitor.glitchCooldown == 0) {
                region = textureHandler.get("game/gui/ui");
                batch.draw(region, x, y, width, height);

                for (int i = 0; i < player.telephone.regions.length; i++) {
                    if (player.telephone.cooldowns[i] == 0 && player.telephone.cooldownSeconds[i] == 0) {
                        region = player.telephone.regions[i];
                        textureHandler.setRegion(region, 32, player.telephone.status[i]);
                        if (player.monitor.activeCamera == i + 1) batch.setColor(1, 1, 1, 1);
                        else batch.setColor(0.6f, 0.6f, 0.6f, 1);
                    } else {
                        region = player.telephone.cooldownRegions[i];
                        textureHandler.setRegion(region, 32, player.telephone.cooldownSeconds[i]);
                        batch.setColor(0.85f, 0.85f, 0.85f, 1);
                    }

                    batch.draw(region,
                            player.monitor.xButtonPos[i],
                            player.monitor.yButtonPos[i]);
                }

                batch.setColor(1, 1, 1, 1);

                for (int i = 0; i < player.monitor.regions.length; i++) {
                    region = player.monitor.regions[i];
                    textureHandler.setRegion(region, 48, player.monitor.activeCamera == i + 1 ? 1 : 0);
                    batch.draw(region,
                            player.monitor.xPos[i],
                            player.monitor.yPos[i]);
                }
            }
        }

        FrameBufferManager.end(batch, renderHandler.screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float length = minuteLength * 6;
        boolean winTransition = nightTime >= length && !infiniteNight;
        if (winTransition) {
            engine.candys2Deluxe.setState(2);
            engine.candys2Deluxe.win.reset(!maxMode || cheating);
            player.dispose();
            engine.appHandler.soundHandler.stopAllSounds();
        }

        if (winTransition) engine.candys2Deluxe.win.lastGameFrameBuffer.begin();

        batch.setColor(1, 1, 1, player.viewAlpha);
        FrameBufferManager.render(batch, renderHandler.screenBuffer, true);
        batch.setColor(1, 1, 1, 1);

        if ((int) player.roomFrame == 0 && hitboxDebug){
            if (candysShowdown.blank.hallPosition != 0){
                renderHandler.shapeDrawer.setColor(0.9f, 0.9f, 0.75f, 0.5f);
                var hitbox = candysShowdown.blank.hitbox;
                renderHandler.shapeDrawer.filledCircle(hitbox.getX() - player.position,
                        hitbox.getY() - 24,
                        hitbox.size);
            }

            if (candysShowdown.candy.hallPosition != 0){
                renderHandler.shapeDrawer.setColor(0.2f, 0.2f, 1, 0.5f);
                var hitbox = candysShowdown.candy.hitbox;
                renderHandler.shapeDrawer.filledCircle(hitbox.getX() - player.position,
                        hitbox.getY() - 24,
                        hitbox.size);
            }
        }

        if (!player.jumpscareEnemy.isEmpty() && player.jumpscareDelay == 0
                && !VideoManager.render(batch, engine.game, false, false, 1280, 720)){
            gameOver = true;
            whiteAlpha = 1;
            staticFrame = 0;
            gameOverAlpha = 1;
            renderHandler.screenAlpha = 1;
            renderHandler.shapeDrawer.setColor(1, 1, 1, 1);
            renderHandler.drawScreen();
            engine.appHandler.soundHandler.stopAllSounds();
            return;
        }

        var font2 = fontManager.getFont("candys2/font2");
        fontManager.setCurrentFont(font2);
        font2.setColor(0.8f, 0.8f, 0.8f, 1);
        fontManager.setSize(24);
        int yPosition = 618;
        fontManager.setText("Map Radar: " + (mapDebug ? "On" : "Off"));
        fontManager.setPosition(248, yPosition);
//        fontManager.render(batch);
        if (mapDebug){

        }

        fontManager.setSize(32);
        if (infiniteNight){
            nightTimeBuilder.delete(0, nightTimeBuilder.length());
            int hour = (int) nightTime / 60;
            nightTimeBuilder.append(hour).append(":");
            int tempTime = (int) (nightTime % 60);
            if (tempTime < 10) nightTimeBuilder.append(0);
            nightTimeBuilder.append(tempTime);
            fontManager.setText(nightTimeBuilder.toString());
        } else {
            int hour = (int) (nightTime / minuteLength);
            if (hour == 0) hour = 12;
            fontManager.setText(hour + " AM");
        }
        fontManager.setPosition(window.width() - fontManager.getLayout().width - 16, 708);
        fontManager.render(batch);
        if (faultyBattery) {
            fontManager.setText("Battery: " + (100 - player.cameraFlashes) + "%");
            fontManager.setPosition(12, 708);
            fontManager.render(batch);
        }

        if (!winTransition) return;
        FrameBufferManager.end(batch, engine.candys2Deluxe.win.lastGameFrameBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, engine.candys2Deluxe.win.lastGameFrameBuffer, true);
    }

    private void renderGameover(Engine engine){
        var textureHandler = engine.appHandler.getTextureHandler();
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var window = engine.appHandler.window;
        var fontManager = engine.appHandler.getFontManager();
        var input = engine.appHandler.getInput();

        batch.enableBlending();
        renderHandler.batchBegin();

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();

        batch.setColor(0.8f, 0.8f, 0.8f, 1);

        var region = textureHandler.getRegion("static/newspaper", 1024, (int) staticFrame);
        batch.draw(region, 0, 0, window.width(), window.height());

        batch.setColor(1, 1, 1, 1);

        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
        region = textureHandler.getRegion("game/newspaper", 1024, 0);
        batch.draw(region, 0, 0, window.width(), window.height());
        batch.flush();
        batch.setBlendFunction(srcFunc, dstFunc);

        var font1 = fontManager.getFont("candys2/font1");
        fontManager.setCurrentFont(font1);
        fontManager.setSize(60);
        font1.setColor(1, 1, 1, 1);
        float x = window.width() - 62;

        fontManager.setText("RETRY");
        float width = fontManager.getLayout().width;
        fontManager.setPosition(x - width, 170);
        retryHover = input.mouseOver(x - width, 125, width, fontManager.getLayout().height);
        if (!retryHover && !reset){
            font1.setColor(0.5f, 0.1f, 0.2f, 1);
            fontManager.setText("RETRY");
            fontManager.setColor(0.15f, 0, 0.05f, 1);
        } else {
            fontManager.setColor(0.1f, 0.1f, 0.1f, 1);
        }
        fontManager.setOutline(0.25f);
        fontManager.render(batch);
        fontManager.setColor(0, 0,0, 1);
        font1.setColor(1, 1, 1, 1);
        fontManager.setOutline(0.5f);

        fontManager.setText("MENU");
        width = fontManager.getLayout().width;
        fontManager.setPosition(x - width, 100);
        menuHover = input.mouseOver(x - width, 55, width, fontManager.getLayout().height);
        if (!menuHover){
            font1.setColor(0.5f, 0.1f, 0.2f, 1);
            fontManager.setText("MENU");
            fontManager.setColor(0.15f, 0, 0.05f, 1);
        } else {
            fontManager.setColor(0.1f, 0.1f, 0.1f, 1);
        }
        fontManager.setOutline(0.25f);
        fontManager.render(batch);
        fontManager.setColor(0, 0,0, 1);
        font1.setColor(1, 1, 1, 1);
        fontManager.setOutline(0.5f);

        fontManager.render(batch);

        renderHandler.shapeDrawer.setColor(1, 1, 1, whiteAlpha);
        renderHandler.drawScreen();

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - gameOverAlpha);
        renderHandler.drawScreen();
    }

    private void writeScore(Engine engine){
        StringBuilder time = new StringBuilder();
        int hour = (int) nightTime / 60;
        time.append(hour).append(":");
        int tempTime = (int) (nightTime % 60);
        if (tempTime < 10) time.append(0);
        time.append(tempTime);

        if (engine.gamejoltManager != null) {
//            engine.gamejoltManager.execute(() -> {
//                String tableID = GameData.night == 0 ? "767298" : GameData.night == 1 ? "909490" : "909960";
//                scoreFetch(engine, tableID, Integer.parseInt(player.getFlashPoints()), player.getFlashPoints() + " Points");
//                tableID = GameData.night == 0 ? "909489" : GameData.night == 1 ? "909491" : "909961";
//                scoreFetch(engine, tableID, (int) nightTime, time + " seconds");
//            });
        }

        if (engine.scoreTable == null) return;
        engine.scoreTable.setCandys2Score(mode, player.cameraFlashes + " Flashes", time + " seconds");
        FileUtils.writeTable(engine.jsonHandler, engine.scoreTable);
    }

    private void scoreFetch(Engine engine, String tableID, int sort, String value){
        assert engine.gamejoltManager != null;
        Score score = engine.gamejoltManager.score.fetch(engine.gamejoltManager, engine.jsonHandler, tableID);
        if (score == null || sort >= Integer.parseInt(score.getSort()))
            engine.gamejoltManager.score.add(engine.gamejoltManager, tableID, String.valueOf(sort), value);
    }

    private void writeStars(Engine engine, StarData[] starsData, boolean complete) {
        setStar(starsData[0], complete);
        if (laserPointer) setStar(starsData[1], complete);
        if (faultyBattery) setStar(starsData[2], complete);
        if (faultyPhones) setStar(starsData[3], complete);
        if (laserPointer && faultyBattery && faultyPhones) setStar(starsData[4], complete);

        if (engine.gamejoltManager == null) {
            FileUtils.writeUser(engine.jsonHandler, engine.user);
            return;
        }
        engine.gamejoltManager.execute(() -> {
            String value = engine.jsonHandler.writeCandysUser(engine.user);
            engine.gamejoltManager.dataStore.set(engine.gamejoltManager, "user_id=" + engine.gamejoltManager.id, value);
        });
    }

    private void setStar(StarData starData, boolean complete){
        starData.time = Math.max((int) nightTime, starData.time);
        if (complete) starData.complete = true;
    }
}
