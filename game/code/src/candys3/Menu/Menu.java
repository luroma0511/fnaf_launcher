package candys3.Menu;

import candys3.GameData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.deluxe.Candys3Data;
import util.deluxe.Paths;
import candys3.Menu.Objects.*;
import util.*;

public class Menu {
    private final FrameBuffer captionFBO;
    private final FrameBuffer windowFrameBuffer;
    private final FrameBuffer rainbowFrameBuffer;

    private final MenuCharacter rat;
    private final MenuCharacter cat;
    private final MenuCharacter vinnie;
    private final Button optionButton;
    private final Button playButton;
    private Button laserPointerButton;
    private Button hardCassetteButton;
    private Button freeScrollButton;
    private Button allChallengesButton;
    private Button flashDebugButton;
    private Button hitboxDebugButton;
    private Button noJumpscaresButton;
    private Button expandedPointerButton;
    private Button perspectiveButton;
    private Button infiniteNightButton;
    private Button restartOnJumpscareButton;
    private Button classicJumpscaresButton;
    private final Arrows arrows;
    private final Caption caption;
    private final Star star;
    private final Star laserStar;
    private final Star hardCassetteStar;
    private final Star freeScrollStar;
    private final Star allChallengesStar;

    private int setNight;
    private int previousNight;
    private int hellTrigger;
    private float whiteAlpha;
    private float staticFrame;
    private float optionWindowAlpha;
    private float windowX;
    private byte section;
    private byte prevSection;
    private float rainbowPos;
    private String modeName = "";

    private boolean playMenu;
    private boolean loadGame;
    private float loadTime;

    public Menu(){
        windowFrameBuffer = FrameBufferManager.newFrameBuffer();
        captionFBO = FrameBufferManager.newFrameBuffer();
        rainbowFrameBuffer = FrameBufferManager.newFrameBuffer();

        rat = new MenuCharacter(-45, 44, 632, 632, false, "rat.txt");
        cat = new MenuCharacter(781, 44, 428, 632, false, "cat.txt");
        vinnie = new MenuCharacter(436, 36, 391, 657, false, "vinnie.txt");
        optionButton = new Button("OPTIONS", 365, 20, 200, 84, null);
        playButton = new Button("PLAY", 715, 20, 200, 84, null);
        arrows = new Arrows();
        caption = new Caption();
        setNight = 0;

        int size = 40;
        int xPos = (int) (640 - (float) size / 2);
        star = new Star("littleStar", xPos - 84, 116, size, size, "");
        laserStar = new Star("littleStar", xPos - 28, 116, size, size, " w/ Laser Pointer");
        hardCassetteStar = new Star("littleStar", xPos + 28, 116, size, size, " w/ Hard Cassette");
        freeScrollStar = new Star("littleStar", xPos + 84, 116, size, size, " w/ Free Scroll");
        size = 96;
        xPos = (int) (640 - (float) size / 2);
        allChallengesStar = new Star("star", xPos, 156, size, size, " All Challenges");
    }

    public void load(Engine engine){
        engine.appHandler.soundHandler.addAll(Paths.dataPath + "candys3/menu/sounds.txt");
        engine.appHandler.getTextureHandler().add("Static/Static");
        engine.appHandler.getTextureHandler().addImages("menu/", Paths.dataPath + "candys3/menu/textures.txt");
        engine.appHandler.getRenderHandler().screenAlpha = 0;
        optionWindowAlpha = 0;
        optionButton.setAlpha(0);
        playButton.setAlpha(0);
        caption.setAlpha(0);
        hellTrigger = 0;
    }

    public void update(Engine engine) {
        var renderHandler = engine.appHandler.getRenderHandler();
        var window = engine.appHandler.window;
        var soundHandler = engine.appHandler.soundHandler;
        var textureHandler = engine.appHandler.getTextureHandler();
        var input = engine.appHandler.getInput();
        var fontManager = engine.appHandler.getFontManager();
        var captionFont = fontManager.getFont("candys3/captionFont");

        if (GameData.night == 0 || GameData.night == 3) soundHandler.setSoundEffect(soundHandler.PITCH, "deluxeMenu", 1);
        else if (GameData.night == 1 || GameData.night == 4) soundHandler.setSoundEffect(soundHandler.PITCH, "deluxeMenu", 0.75f);
        else soundHandler.setSoundEffect(soundHandler.PITCH, "deluxeMenu", 0.56f);

        if (!loadGame && !engine.appHandler.getRenderHandler().lock) {
            if (!playMenu) {
                soundHandler.play("deluxeMenu");
                soundHandler.setSoundEffect(soundHandler.LOOP, "deluxeMenu", 1);
                soundHandler.setSoundEffect(soundHandler.VOLUME, "deluxeMenu",0.75f);
                soundHandler.play("menuAmbience");
                soundHandler.setSoundEffect(soundHandler.LOOP, "menuAmbience", 1);
                soundHandler.setSoundEffect(soundHandler.VOLUME, "menuAmbience",0.075f);
                playMenu = true;
            }
            
            if (windowX == 0) {
                windowX = (float) window.width() / 2 - (float) textureHandler.get("menu/window").getRegionWidth() / 2;
                int xPos = (int) (windowX + 32);
                laserPointerButton = new Button("Laser Pointer", xPos, 470, 84, "laserPointer");
                hardCassetteButton = new Button("Hard Cassette", xPos, 372, 84, "hardCassette");
                freeScrollButton = new Button("Free Scroll", xPos, 274, 84, "freeScroll");
                allChallengesButton = new Button("All Challenges", xPos, 176, 84, null);
                flashDebugButton = new Button("Flash Debug", xPos, 470, 84, "flashDebug");
                hitboxDebugButton = new Button("Hitbox Debug", xPos, 372, 84, "hitboxDebug");
                noJumpscaresButton = new Button("No Jumpscares", xPos, 274, 84, "noJumpscares");
                expandedPointerButton = new Button("Expanded Pointer", xPos, 176, 84, "expandedPointer");
                perspectiveButton = new Button("Perspective Effect", xPos, 470, 84, "perspective");
                infiniteNightButton = new Button("Infinite Night", xPos, 372, 84, "infiniteNight");
                restartOnJumpscareButton = new Button("Restart on Jumpscare", xPos, 274, 84, "restartOnJumpscare");
                classicJumpscaresButton = new Button("Classic Jumpscares", xPos, 176, 84, "classicJumpscares");
            }

            renderHandler.screenAlpha = Time.increaseTimeValue(renderHandler.screenAlpha, 1, 4);
            whiteAlpha = Time.decreaseTimeValue(whiteAlpha, 0, 3);
            staticFrame = Time.increaseTimeValue(staticFrame, 8192, 40);
            if (staticFrame == 8192) staticFrame = 0;

            if (input.keyTyped(Input.Keys.H) && hellTrigger == 0) hellTrigger++;
            else if (input.keyTyped(Input.Keys.E) && hellTrigger == 1) hellTrigger++;
            else if (input.keyTyped(Input.Keys.L) && hellTrigger > 1) hellTrigger++;
            if (hellTrigger == 4) {
                hellTrigger = 0;
                setNight = GameData.night != 2 ? 2 : previousNight;
            }

            boolean focus = !optionButton.isSelected() && !playButton.isSelected();
            caption.setActive(false);
            rat.update(caption, input, window, focus);
            cat.update(caption, input, window, focus);
            vinnie.update(caption, input, window, focus && GameData.night < 2, false);

            rainbowPos += Time.convertValue(96);
            if (rainbowPos > 256) rainbowPos -= 256;

            var candysFont = fontManager.getFont("candys3/candysFont");
            fontManager.setCurrentFont(candysFont);
            fontManager.setSize(40);
            changeMode();
            fontManager.setText(modeName);

            arrows.update(input, fontManager, optionButton.isSelected(), section);
            if (input.isLeftPressed()) {
                if (arrows.getHovered() == 1) section--;
                else if (arrows.getHovered() == 2) section++;
                else if (GameData.night != 2){
                    if (arrows.getHovered() == 3){
                        setNight++;
                        if (setNight == 2) setNight++;
                    } else if (arrows.getHovered() == 4){
                        setNight--;
                        if (setNight == 2) setNight--;
                    }
                    previousNight = setNight;
                }
            }

            optionButton.update(caption, input, true);
            if (optionButton.isSelected()) {
                if (section == 3) section = prevSection;
                prevSection = section;
                optionWindowAlpha = Time.increaseTimeValue(optionWindowAlpha, 1, 4);
                if (section == 0) {
                    laserPointerButton.update(caption, input, false);
                    hardCassetteButton.update(caption, input, false);
                    freeScrollButton.update(caption, input, false);
                    allChallengesButton.update(caption, input, false);

                    if (allChallengesButton.isSelected()) {
                        if (laserPointerButton.isSelected()
                                && hardCassetteButton.isSelected()
                                && freeScrollButton.isSelected()) {
                            laserPointerButton.setSelected();
                            hardCassetteButton.setSelected();
                            freeScrollButton.setSelected();
                        } else {
                            if (!laserPointerButton.isSelected()) laserPointerButton.setSelected();
                            if (!hardCassetteButton.isSelected()) hardCassetteButton.setSelected();
                            if (!freeScrollButton.isSelected()) freeScrollButton.setSelected();
                        }
                        allChallengesButton.setSelected();
                    }
                    GameData.hitboxMultiplier = laserPointerButton.isSelected() ? 0.75f : 1;
                    GameData.hardCassette = hardCassetteButton.isSelected();
                    GameData.freeScroll = freeScrollButton.isSelected();
                } else if (section == 1){
                    flashDebugButton.update(caption, input, false);
                    GameData.flashDebug = flashDebugButton.isSelected();
                    hitboxDebugButton.update(caption, input, false);
                    GameData.hitboxDebug = hitboxDebugButton.isSelected();
                    noJumpscaresButton.update(caption, input, false);
                    GameData.noJumpscares = noJumpscaresButton.isSelected();
                    expandedPointerButton.update(caption, input, false);
                    GameData.expandedPointer = expandedPointerButton.isSelected();
                } else {
                    perspectiveButton.update(caption, input, false);
                    GameData.perspective = perspectiveButton.isSelected();
                    infiniteNightButton.update(caption, input, false);
                    GameData.infiniteNight = infiniteNightButton.isSelected();
                    restartOnJumpscareButton.update(caption, input, false);
                    GameData.restartOnJumpscare = restartOnJumpscareButton.isSelected();
                    classicJumpscaresButton.update(caption, input, false);
                    GameData.classicJumpscares = classicJumpscaresButton.isSelected();
                }
            } else if (GameData.night != setNight) {
                byte previousNight = GameData.night;
                GameData.night = (byte) setNight;
                if (GameData.night == 0) {
                    rat.setFilename("rat.txt");
                    cat.setFilename("cat.txt");
                    vinnie.setFilename("vinnie.txt");
                } else if (GameData.night == 1){
                    rat.setFilename("shadowRat.txt");
                    cat.setFilename("shadowCat.txt");
                    vinnie.setFilename("shadowVinnie.txt");
                } else {
                    rat.setFilename("hellRat.txt");
                    cat.setFilename("hellCat.txt");
                }

                if (previousNight == 2 && GameData.night != 2){
                    rat.setInitX(rat.getInitX() - 75);
                    cat.setInitX(cat.getInitX() + 75);
                } else if (previousNight != 2 && GameData.night == 2){
                    rat.setInitX(rat.getInitX() + 75);
                    cat.setInitX(cat.getInitX() - 75);
                }

                optionWindowAlpha = 0;
                whiteAlpha = 1;
                soundHandler.play("thunder");
            } else {
                star.update(modeName, caption, input);
                laserStar.update(modeName, caption, input);
                hardCassetteStar.update(modeName, caption, input);
                freeScrollStar.update(modeName, caption, input);
                allChallengesStar.update(modeName, caption, input);
                optionWindowAlpha = Time.decreaseTimeValue(optionWindowAlpha, 0, 4);
            }
            changeMode();

            caption.update(engine, captionFont);
            playButton.update(caption, input, true);
            if (optionButton.isSelected()) playButton.reset();
            if (!playButton.isSelected()) return;
            soundHandler.stopAllSounds();
            soundHandler.play("thunder");
            GameData.ratAI = rat.getAi();
            GameData.catAI = cat.getAi();
            whiteAlpha = 1;
            loadGame = true;
            textureHandler.dispose();
            engine.candys3Deluxe.getGame().load(engine);
            if (VideoManager.isPlaying()) VideoManager.stop();
            renderHandler.screenAlpha = 1;
        } else {
            boolean loaded = !engine.appHandler.getRenderHandler().lock;
            if (playButton.isSelected()) playButton.setSelected();
            else if (loadTime == 1 && loaded) {
                engine.candys3Deluxe.setState(1);
                playMenu = false;
                loadGame = false;
                loadTime = 0;
                return;
            }
            whiteAlpha = Time.decreaseTimeValue(whiteAlpha, 0, 2);
            loadTime = Time.increaseTimeValue(loadTime, 1, 1.5f);
        }
    }

    public void render(Engine engine){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;

        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        renderHandler.shapeDrawer.update();
        batch.enableBlending();
        renderHandler.batchBegin();
        if (!loadGame) renderNightMenu(engine);
        else renderLoading(engine);

        renderHandler.shapeDrawer.setColor(1, 1, 1, whiteAlpha);
        renderHandler.drawScreen();
    }

    private void renderLoading(Engine engine){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var window = engine.appHandler.window;
        var fontManager = engine.appHandler.getFontManager();
        var textureHandler = engine.appHandler.getTextureHandler();
        var candysFont = fontManager.getFont("candys3/candysFont");

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();

        fontManager.setCurrentFont(candysFont);
        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, 1, true);
        fontManager.setSize(54);
        fontManager.setText(modeName);

        fontManager.setPosition(true, true,
                (float) window.width() / 2,
                (float) window.height() / 2 + 16);
        fontManager.render(batch);

        int loadPercent = (int) (((float) textureHandler.currentPercent / textureHandler.maxPercent) * 100);
        fontManager.setSize(26);
        fontManager.setText("Loading: " + loadPercent + "%");
        fontManager.setPosition(false, false, 16, 32);
        fontManager.render(batch);
    }

    private void renderNightMenu(Engine engine){
        if (engine.appHandler.getRenderHandler().lock) return;

        var window = engine.appHandler.window;
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var fontManager = engine.appHandler.getFontManager();
        var textureHandler = engine.appHandler.getTextureHandler();
        var candysFont = fontManager.getFont("candys3/candysFont");
        var captionFont = fontManager.getFont("candys3/captionFont");

        TextureRegion region;
        fontManager.setCurrentFont(captionFont);
        fontManager.setSize(15);
        caption.prepare(engine, captionFBO);

        windowFrameBuffer.begin();
        if (windowX != 0) {
            region = textureHandler.get("menu/window");
            engine.candys3Deluxe.setNightColor(engine, 1.5f);
            batch.draw(region, windowX, 144);
            batch.setColor(1, 1, 1, 1);

            if (section != 3) {
                if (section == 0) {
                    boolean allChallenges = laserPointerButton.isSelected() && hardCassetteButton.isSelected() && freeScrollButton.isSelected();
                    laserPointerButton.render(textureHandler, batch);
                    hardCassetteButton.render(textureHandler, batch);
                    freeScrollButton.render(textureHandler, batch);
                    allChallengesButton.render(textureHandler, batch, allChallenges);
                } else if (section == 1) {
                    flashDebugButton.render(textureHandler, batch);
                    hitboxDebugButton.render(textureHandler, batch);
                    noJumpscaresButton.render(textureHandler, batch);
                    expandedPointerButton.render(textureHandler, batch);
                } else {
                    perspectiveButton.render(textureHandler, batch);
                    infiniteNightButton.render(textureHandler, batch);
                    restartOnJumpscareButton.render(textureHandler, batch);
                    classicJumpscaresButton.render(textureHandler, batch);
                }

                engine.candys3Deluxe.setNightColor(engine, 1);
                region = textureHandler.get("menu/arrow");
                textureHandler.setFilter("menu/arrow");
                region.flip(true, false);
                if (section != 0) batch.draw(region, 502, 601);
                region.flip(true, false);
                if (section != 2) batch.draw(region, 752, 601);
            }
            batch.setColor(1, 1, 1, 1);
        }
        FrameBufferManager.end(batch, windowFrameBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        rainbowFrameBuffer.begin();
        batch.draw(textureHandler.get("menu/rainbow"), rainbowPos, 0, 256, 256);
        batch.draw(textureHandler.get("menu/rainbow"), rainbowPos - 256, 0, 256, 256);
        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_SRC_COLOR);
        batch.draw(textureHandler.get("menu/star"), 0, 0);
        batch.flush();
        batch.setBlendFunction(srcFunc, dstFunc);
        FrameBufferManager.end(batch, rainbowFrameBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        renderHandler.screenBuffer.begin();
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();
        if (GameData.night != 2) batch.setColor(0.8f, 0.8f, 0.8f, 1);
        else batch.setColor(0.9f, 0, 0, 1);
        if (GameData.night == 0) {
            batch.draw(textureHandler.get("menu/vinnie"), vinnie.getX(), vinnie.getY());
            batch.draw(textureHandler.get("menu/rat"), rat.getX(), rat.getY());
            batch.draw(textureHandler.get("menu/cat"), cat.getX(), cat.getY());
        } else if (GameData.night == 2){
            batch.draw(textureHandler.get("menu/shadowRat"), rat.getX(), rat.getY());
            batch.draw(textureHandler.get("menu/shadowCat"), cat.getX(), cat.getY());
        } else {
            batch.draw(textureHandler.get("menu/shadowVinnie"), vinnie.getX(), vinnie.getY());
            batch.draw(textureHandler.get("menu/shadowRat"), rat.getX(), rat.getY());
            batch.draw(textureHandler.get("menu/shadowCat"), cat.getX(), cat.getY());
        }

        batch.flush();
        batch.setColor(1, 1, 1, 1);
        FrameBufferManager.end(batch, renderHandler.screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        region = textureHandler.getRegion("Static/Static", 1024, (int) staticFrame % 8);
        if (region != null) textureHandler.setFilter(region.getTexture());
        engine.candys3Deluxe.setNightColor(engine, 2);
        batch.draw(region, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, 1);

        srcFunc = batch.getBlendSrcFunc();
        dstFunc = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
        FrameBufferManager.render(batch, renderHandler.screenBuffer, true);
        batch.flush();
        batch.setBlendFunction(srcFunc, dstFunc);

        fontManager.setCurrentFont(candysFont);
        fontManager.setSize(40);
        fontManager.setText(modeName);
        float layoutWidth = fontManager.getLayout().width;

        engine.candys3Deluxe.setNightColor(engine, 1);
        region = textureHandler.get("menu/arrow");
        region.flip(true, false);
        if (GameData.night != 0 && GameData.night != 2) batch.draw(region, 640 - (int) (layoutWidth / 2) - 42, 673);
        region.flip(true, false);
        if (GameData.night != 1 && GameData.night != 2) batch.draw(region, 640 + (int) (layoutWidth / 2) + 16, 673);
        batch.setColor(1, 1, 1, 1);

        int[] modeStars = null;
        Candys3Data data = engine.user.candys3Data;

        if (GameData.night == 0) {
            if (rat.getAi() != 0 && cat.getAi() != 0) {
                if (vinnie.getAi() == 0) modeStars = data.mainCastStars;
                else modeStars = data.ratAndCatTheaterStars;
            }
        } else if (GameData.night == 1) {
            if (rat.getAi() != 0 && cat.getAi() != 0) {
                if (vinnie.getAi() == 0) modeStars = data.shadowCastStars;
                else modeStars = data.theaterTraumaStars;
            }
        } else modeStars = data.hellCastStars;

        star.render(textureHandler, batch, rainbowFrameBuffer, modeStars != null && modeStars[0] == 1, false);
        laserStar.render(textureHandler, batch, rainbowFrameBuffer, modeStars != null && modeStars[1] == 1, false);
        hardCassetteStar.render(textureHandler, batch, rainbowFrameBuffer, modeStars != null && modeStars[2] == 1, false);
        freeScrollStar.render(textureHandler, batch, rainbowFrameBuffer, modeStars != null && modeStars[3] == 1, false);
        allChallengesStar.render(textureHandler, batch, rainbowFrameBuffer, modeStars != null && modeStars[4] == 1, false);

        //start rendering texts
        fontManager.setCurrentFont(candysFont);
        fontManager.setSize(72);
        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, 1, false);
        if (GameData.night == 2) fontManager.setText("AI: HELL");
        else fontManager.setText(!rat.isAiCustom() ? "AI: " + (rat.getAi() == 0 ? "Off": "On"): "AI: " + rat.getAi());
        fontManager.setPosition(true, true,
                rat.getX() + rat.getWidth() / 2,
                rat.getY() + rat.getHeight() / 2.5f);
        fontManager.render(batch);

        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, 1, false);
        if (GameData.night == 2) fontManager.setText("AI: HELL");
        else fontManager.setText(!cat.isAiCustom() ? "AI: " + (cat.getAi() == 0 ? "Off": "On"): "AI: " + cat.getAi());
        fontManager.setPosition(true, true,
                cat.getX() + cat.getWidth() / 2,
                cat.getY() + cat.getHeight() / 2.5f);
        fontManager.render(batch);

        if (GameData.night < 2) {
            engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, 1, false);
            fontManager.setText(!vinnie.isAiCustom() ? "AI: " + (vinnie.getAi() == 0 ? "Off" : "On") : "AI: " + vinnie.getAi());
            fontManager.setPosition(true, true,
                    vinnie.getX() + vinnie.getWidth() / 2,
                    vinnie.getY() + vinnie.getHeight() / 2.5f);
            fontManager.render(batch);
        }

        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, 1, false);
        fontManager.setSize(40);
        fontManager.setText(modeName);

        fontManager.setPosition(true, false, (float) window.width() / 2, 700);
        fontManager.render(batch);

        if (GameData.night == 2) {
            if (!VideoManager.isPlaying()) VideoManager.setRequest("menu/hell");
            srcFunc = batch.getBlendSrcFunc();
            dstFunc = batch.getBlendDstFunc();
            batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);
            batch.setColor(1, 1, 1, 1);
            VideoManager.render(batch, false, true, 1280, 720);
            batch.flush();
            batch.setBlendFunction(srcFunc, dstFunc);
        }
        else VideoManager.stop();

        region = textureHandler.get("menu/button");
        textureHandler.setFilter(region.getTexture());
        for (byte i = 0; i < 2; i++) {
            Button button;
            if (i == 0) button = optionButton;
            else button = playButton;
            engine.candys3Deluxe.setNightColor(engine, 2 - button.getAlpha());
            batch.draw(region, button.getX(), button.getY());
            engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, button.getAlpha(), true);
            fontManager.setText(button.getPath());
            fontManager.setPosition(true, false, button.getX() + button.getWidth() / 2, 76);
            fontManager.render(batch);
        }

        if (optionWindowAlpha > 0) {
            Texture texture = windowFrameBuffer.getColorBufferTexture();
            textureHandler.setFilter(texture);
            region = new TextureRegion(texture);
            region.flip(false, true);
            batch.setColor(1, 1, 1, optionWindowAlpha);
            batch.draw(region, CameraManager.getX(), CameraManager.getY());

            engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, optionWindowAlpha, false);
            if (section == 3) fontManager.setText("Trophies");
            else if (section == 0) fontManager.setText("Challenges");
            else if (section == 1) fontManager.setText("Cheats");
            else fontManager.setText("Options");

            fontManager.setOutline(0.25f);
            if (GameData.night != 1) fontManager.setColor(0.2f, 0, 0, optionWindowAlpha);
            else fontManager.setColor(0.1f, 0, 0.2f, optionWindowAlpha);
            fontManager.setPosition(true, false, (float) window.width() / 2, 627);
            fontManager.render(batch);
            fontManager.setOutline(0.5f);
            fontManager.setColor(0, 0, 0, 1);

            if (section == 0){
                buttonFontRender(engine, laserPointerButton);
                buttonFontRender(engine, hardCassetteButton);
                buttonFontRender(engine, freeScrollButton);
                buttonFontRender(engine, allChallengesButton);
            } else if (section == 1){
                buttonFontRender(engine, flashDebugButton);
                buttonFontRender(engine, hitboxDebugButton);
                buttonFontRender(engine, noJumpscaresButton);
                buttonFontRender(engine, expandedPointerButton);
            } else {
                buttonFontRender(engine, perspectiveButton);
                buttonFontRender(engine, infiniteNightButton);
                buttonFontRender(engine, restartOnJumpscareButton);
                buttonFontRender(engine, classicJumpscaresButton);
            }
        }

        fontManager.setCurrentFont(captionFont);
        caption.render(engine, captionFBO, captionFont);
        batch.setColor(1, 1, 1, 1);

        fontManager.setSize(15);
        engine.candys3Deluxe.fontAlpha(renderHandler, captionFont, renderHandler.screenAlpha, false);
        fontManager.setText("Logged in as: " + engine.user.getUsername());
        fontManager.setRelativePosition(20, 700);
        fontManager.render(batch);

        if (engine.gamejoltManager != null && engine.gamejoltManager.threadRunning()) {
            if (!engine.gamejoltManager.session.isPinged()) fontManager.setText("Pinging session...");
            else if (!engine.gamejoltManager.dataStore.loaded) fontManager.setText("Loading save...");
            fontManager.setRelativePosition(20, 678);
            fontManager.render(batch);
        }
        fontManager.setText("F11 = Fullscreen");
        fontManager.setRelativePosition(20, 100);
        fontManager.render(batch);
        fontManager.setText("F2 = Return to Menu");
        fontManager.setRelativePosition(20, 78);
        fontManager.render(batch);
        fontManager.setText("R = Restart Night");
        fontManager.setRelativePosition(20, 56);
        fontManager.render(batch);
        fontManager.setText("Patch " + engine.candys3Deluxe.version);
        fontManager.setRelativePosition(20, 34);
        fontManager.render(batch);

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - renderHandler.screenAlpha);
        renderHandler.drawScreen();

//        rat.debugRender();
//        cat.debugRender();
    }

    private void buttonFontRender(Engine engine, Button button){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var fontManager = engine.appHandler.getFontManager();
        var candysFont = fontManager.getFont("candys3/candysFont");

        engine.candys3Deluxe.fontAlpha(renderHandler, candysFont, optionWindowAlpha, false);
        fontManager.setText(button.getPath());
        fontManager.setPosition(button.getX() + 100, button.getY() + 55);
        fontManager.render(batch);
    }

    private void changeMode(){
        if (GameData.night == 0){
            if (rat.getAi() == 0 || cat.getAi() == 0) modeName = "Custom Night";
            else if (vinnie.getAi() == 0) modeName = "The Main Cast";
            else modeName = "Rat & Cat Theater";
        } else if (GameData.night == 1){
            if (rat.getAi() == 0 || cat.getAi() == 0) modeName = "Shadow Night";
            else if (vinnie.getAi() == 0) modeName = "The Shadow Cast";
            else modeName = "Theater Traumatization";
        } else modeName = "The Hell Cast";
    }

    public void dispose(){
        caption.dispose();
    }
}
