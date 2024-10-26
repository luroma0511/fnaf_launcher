package candys3.Menu;

import candys3.GameData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.deluxe.Candys3Data;
import util.*;
import util.deluxe.StarData;
import util.ui.*;

import java.util.List;

public class Menu {
    private final FrameBuffer captionFBO;
    private final FrameBuffer rainbowFrameBuffer;

    private final MenuCharacter rat;
    private final MenuCharacter cat;
    private final MenuCharacter vinnie;
    private final Button optionButton;
    private final Button playButton;

    private Options options;

    private final Arrows arrows;
    private final Caption caption;
    private final Star star;
    private final Star laserVisionStar;
    private final Star laserPointerStar;
    private final Star hardCassetteStar;
    private final Star allChallengesStar;

    private int setNight;
    private int previousNight;
    private int hellTrigger;
    private float whiteAlpha;
    private float staticFrame;
    private float rainbowPos;
    private String modeName = "";

    private boolean playMenu;
    private boolean loadGame;
    private float loadTime;

    public Menu(){
        captionFBO = FrameBufferManager.newFrameBuffer();
        rainbowFrameBuffer = FrameBufferManager.newFrameBuffer();

        rat = new MenuCharacter(-45, 44, 632, 632, false, "rat.txt");
        cat = new MenuCharacter(790, 44, 428, 632, false, "cat.txt");
        vinnie = new MenuCharacter(436, 36, 391, 657, false, "vinnie.txt");

        optionButton = new Button("", 20, 20, 82, 82, null);
        playButton = new Button("", 1196, 20, 64, 76, null);
        arrows = new Arrows();
        caption = new Caption();
        setNight = 0;

        int size = 40;
        int xPos = (int) (640 - (float) size / 2);
        star = new Star("littleStar", xPos - 84, 40, size, size, "");
        laserVisionStar = new Star("littleStar", xPos - 28, 40, size, size, " w/ Laser Vision");
        laserPointerStar = new Star("littleStar", xPos + 28, 40, size, size, " w/ Laser Pointer");
        hardCassetteStar = new Star("littleStar", xPos + 84, 40, size, size, " w/ Hard Cassette");
        size = 96;
        xPos = (int) (640 - (float) size / 2);
        allChallengesStar = new Star("star", xPos, 80, size, size, " All Challenges");
    }

    public void load(Engine engine){
        engine.appHandler.soundHandler.addAll("res/data/candys3/menu/sounds.txt");
        engine.appHandler.getTextureHandler().add("Static/Static");
        engine.appHandler.getTextureHandler().addImages("menu/", "candys3/menu/textures.txt");

        engine.appHandler.getRenderHandler().screenAlpha = 0;

        if (options == null) {
            options = new Options(engine.appHandler.getTextureHandler(), engine.appHandler.window);
            options.add(1, "Laser Vision", "laserVision");
            options.add(1, "Laser Pointer", "laserPointer");
            options.add(1, "Hard Cassette", "hardCassette");
            options.add(1, "All Challenges", null);
            options.add(2, "Flash Debug", "flashDebug");
            options.add(2, "Hitbox Debug", "hitboxDebug");
            options.add(2, "No Jumpscares", "noJumpscares");
            options.add(2, "Expanded Vision", "expandedVision");
            options.add(3, "Free Scroll", "freeScroll");
            options.add(3, "Infinite Night", "infiniteNight");
            options.add(3, "Perspective Effect", "perspective");
            options.add(3, "Classic Jumpscares", "classicJumpscares");
        }

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

            rat.characterPan(input, window);
            rat.hover(input, 3.75f, 4, 2.25f, 1.5f);
            rat.update(caption, input, focus);

            cat.characterPan(input, window);
            cat.hover(input, 6.25f, 4, 1.625f, 1.5f);
            cat.update(caption, input, focus);

            vinnie.characterPan(input, window);
            vinnie.hover(input, 7, 4, 1.375f, 1.5f);
            vinnie.update(caption, input, focus && GameData.night < 2, false);

            rainbowPos += Time.convertValue(96);
            if (rainbowPos > 256) rainbowPos -= 256;

            var candysFont = fontManager.getFont("candys3/candysFont");
            fontManager.setCurrentFont(candysFont);
            fontManager.setSize(40);
            changeMode();
            fontManager.setText(modeName);

            options.input(input);

            if (GameData.night != 2){
                arrows.update(input, fontManager, GameData.night, 2);
                if (input.isLeftPressed()){
                    if (arrows.getHovered() == 1){
                        setNight++;
                        if (setNight == 2) setNight++;
                    } else if (arrows.getHovered() == 2){
                        setNight--;
                        if (setNight == 2) setNight--;
                    }
                    previousNight = setNight;
                }
            }

            optionButton.update(null, input, true);

            options.updateAlpha(optionButton.isSelected());

            if (optionButton.isSelected()) {
                List<Button> buttonsList = options.get(options.getSection() + 1);
                for (Button button: buttonsList){
                    button.update(caption, input, false);
                }

                if (options.getSection() == 0) {
                    options.updateAllChallenges(buttonsList);
                    GameData.laserVision = buttonsList.get(0).isSelected();
                    GameData.hitboxMultiplier = buttonsList.get(1).isSelected() ? 0.75f : 1;
                    GameData.hardCassette = buttonsList.get(2).isSelected();
                } else if (options.getSection() == 1){
                    GameData.flashDebug = buttonsList.get(0).isSelected();
                    GameData.hitboxDebug = buttonsList.get(1).isSelected();
                    GameData.noJumpscares = buttonsList.get(2).isSelected();
                    GameData.expandedVision = buttonsList.get(3).isSelected();
                } else {
                    GameData.freeScroll = buttonsList.get(0).isSelected();
                    GameData.infiniteNight = buttonsList.get(1).isSelected();
                    GameData.perspective = buttonsList.get(2).isSelected();
                    GameData.classicJumpscares = buttonsList.get(3).isSelected();
                }
            } else {
                StarData[] modeStars = null;
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

                if (modeStars == null) {
                    star.update(modeName, caption, input);
                    laserVisionStar.update(modeName, caption, input);
                    laserPointerStar.update(modeName, caption, input);
                    hardCassetteStar.update(modeName, caption, input);
                    allChallengesStar.update(modeName, caption, input);
                } else {
                    star.update(modeName, caption, input, modeStars[0].time);
                    laserVisionStar.update(modeName, caption, input, modeStars[1].time);
                    laserPointerStar.update(modeName, caption, input, modeStars[2].time);
                    hardCassetteStar.update(modeName, caption, input, modeStars[3].time);
                    allChallengesStar.update(modeName, caption, input, modeStars[4].time);
                }
            }

            if (GameData.night != setNight) {
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

                options.alpha = 0;
                whiteAlpha = 1;
                soundHandler.play("thunder");
            }
            changeMode();

            caption.update(engine, captionFont, "candys3");
            playButton.update(null, input, true);
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
            loadTime = Time.increaseTimeValue(loadTime, 1, 1);
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

        options.fboDraw(engine);

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
        region = engine.appHandler.getMenuUI().arrow;
        region.flip(true, false);
        if (GameData.night != 0 && GameData.night != 2) batch.draw(region, 640 - (int) (layoutWidth / 2) - 42, 673);
        region.flip(true, false);
        if (GameData.night != 1 && GameData.night != 2) batch.draw(region, 640 + (int) (layoutWidth / 2) + 16, 673);
        batch.setColor(1, 1, 1, 1);

        StarData[] modeStars = null;
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

        if (modeStars == null){
            star.renderCandys3(textureHandler, batch, rainbowFrameBuffer, false, false);
            laserVisionStar.renderCandys3(textureHandler, batch, rainbowFrameBuffer, false, false);
            laserPointerStar.renderCandys3(textureHandler, batch, rainbowFrameBuffer, false, false);
            hardCassetteStar.renderCandys3(textureHandler, batch, rainbowFrameBuffer, false, false);
            allChallengesStar.renderCandys3(textureHandler, batch, rainbowFrameBuffer, false, false);
        } else {
            star.renderCandys3(textureHandler, batch, rainbowFrameBuffer, modeStars[0].complete, modeStars[0].special);
            laserVisionStar.renderCandys3(textureHandler, batch, rainbowFrameBuffer, modeStars[1].complete, modeStars[1].special);
            laserPointerStar.renderCandys3(textureHandler, batch, rainbowFrameBuffer, modeStars[2].complete, modeStars[2].special);
            hardCassetteStar.renderCandys3(textureHandler, batch, rainbowFrameBuffer, modeStars[3].complete, modeStars[3].special);
            allChallengesStar.renderCandys3(textureHandler, batch, rainbowFrameBuffer, modeStars[4].complete, modeStars[4].special);
        }

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
            VideoManager.render(batch, engine.game, false, true, 1280, 720);
            batch.flush();
            batch.setBlendFunction(srcFunc, dstFunc);
        }
        else VideoManager.stop();

        region = engine.appHandler.getMenuUI().play;
        textureHandler.setFilter(region.getTexture());
        engine.candys3Deluxe.setNightColor(engine, 2 - playButton.getAlpha());
        batch.draw(region, playButton.getX(), playButton.getY());

        region = engine.appHandler.getMenuUI().options;
        textureHandler.setFilter(region.getTexture());
        engine.candys3Deluxe.setNightColor(engine, 2 - optionButton.getAlpha());
        batch.draw(region, optionButton.getX(), optionButton.getY());

        options.render(engine);

        fontManager.setCurrentFont(captionFont);
        String orientation = "middle";
        if (optionButton.isSelected()) orientation = "left";
        caption.render(engine, captionFBO, captionFont, orientation);
        batch.setColor(1, 1, 1, 1);

        fontManager.setSize(18);
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

        float position = 672;
        fontManager.setSize(15);
        fontManager.setText("F11 - Fullscreen");
        fontManager.setRelativePosition(20, position);
        fontManager.render(batch);
        position -= 22;
        fontManager.setText("F2 - Return to Menu");
        fontManager.setRelativePosition(20, position);
        fontManager.render(batch);
        position -= 22;
        fontManager.setText("R - Restart Night");
        fontManager.setRelativePosition(20, position);
        fontManager.render(batch);
        position -= 22;
        fontManager.setText("Patch " + engine.version);
        fontManager.setRelativePosition(20, position);
        fontManager.render(batch);

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - renderHandler.screenAlpha);
        renderHandler.drawScreen();

//        rat.debugRender(renderHandler, engine.game);
//        cat.debugRender(renderHandler, engine.game);
//        vinnie.debugRender(renderHandler, engine.game);
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
