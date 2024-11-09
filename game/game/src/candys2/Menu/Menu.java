package candys2.Menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import util.*;
import util.deluxe.Candys2Data;
import util.ui.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;

import java.util.List;

public class Menu {
    private final FrameBuffer captionFBO;

    public final MenuCharacter candy;
    public final MenuCharacter cindy;
    public final MenuCharacter chester;
    public final MenuCharacter penguin;
    public final MenuCharacter blank;

    public final MenuCharacter rat;
    public final MenuCharacter cat;
//    public final MenuCharacter vinnie;

    public final Button optionButton;
    public final Button playButton;

    public final Button laserPointerButton;
    public final Button faultyBatteryButton;
    public final Button faultyPhonesButton;
    public final Button shadowButton;

    public Options options;

    private final Caption caption;
    private final Star star;
    private final Star laserPointerStar;
    private final Star faultyBatteryStar;
    private final Star faultyPhonesStar;
    private final Star allChallengesStar;

    private float staticFrame;
    private boolean loadGame;
    private float loadTime;
    private boolean playMenu;
    private float thunder;
    private float shakeScreen;
    private boolean shakePositive;
    private float nightStartAlpha;
    private float nightStartFrame;
    private boolean loadStarted;
    private float modeSection;
    private int modeSectionTarget;

    public Menu(Options options){
        this.options = options;
        modeSection = 0;
        modeSectionTarget = 0;
        captionFBO = FrameBufferManager.newFrameBuffer();

        candy = new MenuCharacter(85, 76, 332, 590, true, "candy.txt");
        cindy = new MenuCharacter(440, 76, 422, 590, true, "cindy.txt");
        chester = new MenuCharacter(243, 32, 412, 550, true, "chester.txt");
        penguin = new MenuCharacter(635, 32, 350, 310, true, "penguin.txt");
        blank = new MenuCharacter(779, 76, 422, 630, true, "blank.txt");

        rat = new MenuCharacter(40, 36, 476, 664, true, "rat.txt");
        cat = new MenuCharacter(792, 36, 429, 664, true, "cat.txt");
//        vinnie = new MenuCharacter();

        optionButton = new Button("", 20, 20, 82, null);
        playButton = new Button("", 1196, 20, 64, 76, null);

        int x = 144;
        laserPointerButton = new Button("", x, 20, 82, "laserPointer");
        x += 86;
        faultyBatteryButton = new Button("", x, 20, 82, "faultyBattery");
        x += 86;
        faultyPhonesButton = new Button("", x, 20, 82, "faultyPhones");
        x += 86;
        shadowButton = new Button("", x, 20, 82, "unavailable");

        caption = new Caption();

        int size = 40;
        int xPos = (int) (640 - (float) size / 2);
        star = new Star("star", xPos - 84, 40, size, size, "");
        laserPointerStar = new Star("star", xPos - 28, 40, size, size, " w/ Laser Pointer");
        faultyBatteryStar = new Star("star", xPos + 28, 40, size, size, " w/ Faulty Battery");
        faultyPhonesStar = new Star("star", xPos + 84, 40, size, size, " w/ Faulty Phones");
        size = 96;
        xPos = (int) (640 - (float) size / 2);
        allChallengesStar = new Star("star", xPos, 80, size, size, " All Challenges");
    }

    public void load(Engine engine, boolean switchOptions){
        var textureHandler = engine.appHandler.getTextureHandler();
        engine.appHandler.soundHandler.addAll("res/data/candys2/menu/sounds.txt");
        textureHandler.add("menu/static");
        textureHandler.add("menu/candy");
        textureHandler.add("menu/cindy");
        textureHandler.add("menu/chester");
        textureHandler.add("menu/blank");
        textureHandler.add("menu/penguin");

        textureHandler.add("menu/rat");
        textureHandler.add("menu/cat");

        textureHandler.add("menu/background");

//        textureHandler.add("font/stripeEffect");
        textureHandler.add("menu/star");
        String[] arr = new String[]{"", "Off"};
        for (String extra: arr) {
            textureHandler.add("menu/laserPointer" + extra);
            textureHandler.add("menu/faultyBattery" + extra);
            textureHandler.add("menu/faultyPhones" + extra);
            textureHandler.add("menu/shadow" + extra);
        }

        if (switchOptions) {
            options.clear();
            var user = engine.user;
            options.add(1, Input.Keys.toString(user.fullscreenKey) + " - Fullscreen", null);
            options.add(1, Input.Keys.toString(user.restartGameKey) + " - Restart Game", null);
            options.add(1, Input.Keys.toString(user.returnMenuKey) + " - Return to Menu", null);
            options.add(1, Input.Keys.toString(user.candys2Data.flashKey) + " - Flash Key", null);
            options.add(2, "Map Debug", "mapDebug");
            options.add(2, "Hitbox Debug", "hitboxDebug");
            options.add(2, "No Jumpscares", "noJumpscares");
            options.add(3, "Infinite Night", "infiniteNight");
            options.add(3, "Perspective Effect", "perspective");

            if (engine.user.candys2Data.infiniteNight) options.get(3).getFirst().setSelected();
            if (engine.user.candys2Data.perspective) options.get(3).get(1).setSelected();
        }

        thunder = 0;
        shakePositive = false;
        shakeScreen = 0;
        optionButton.setAlpha(0);
        playButton.setAlpha(0);
        caption.setAlpha(0);
    }

    public void update(Engine engine){
        var input = engine.appHandler.getInput();
        var window = engine.appHandler.window;
        var renderHandler = engine.appHandler.getRenderHandler();
        var fontManager = engine.appHandler.getFontManager();
        var soundHandler = engine.appHandler.soundHandler;
        var textureHandler = engine.appHandler.getTextureHandler();

        if (!loadGame && !engine.appHandler.getRenderHandler().lock) {
            if (!playMenu) {
                soundHandler.play("menu");
                soundHandler.setSoundEffect(soundHandler.LOOP, "menu", 1);
                soundHandler.setSoundEffect(soundHandler.VOLUME, "menu",0.75f);
                soundHandler.play("whoosh");
                soundHandler.setSoundEffect(soundHandler.VOLUME, "menuAmbience",0.5f);
                playMenu = true;
            }

            if (input.keyTyped(Input.Keys.A)) modeSectionTarget = modeSection <= 0.1f ? 1 : modeSection >= 0.9f ? 0 : modeSectionTarget;

            if (modeSection != modeSectionTarget){
                float difference = (modeSectionTarget - modeSection) * Time.getDelta() * 10;
                modeSection += difference;
            }

            if (shakePositive){
                shakeScreen += Time.getDelta() * 24;
                if (shakeScreen > 1.5f) {
                    shakeScreen = 1.5f;
                    shakePositive = false;
                }
            } else {
                shakeScreen -= Time.getDelta() * 24;
                if (shakeScreen < -1.5f) {
                    shakeScreen = -1.5f;
                    shakePositive = true;
                }
            }

            if (thunder > 0.5f){
                renderHandler.screenAlpha += Time.getDelta() * 4;
                if (renderHandler.screenAlpha > 1) renderHandler.screenAlpha = 1;
                thunder += Time.getDelta() / 1.5f;
            } else {
                thunder += Time.getDelta() * 4;
            }
            if (thunder > 1) thunder = 1;

            staticFrame += Time.getDelta() * 15;
            if (staticFrame >= 4) {
                staticFrame = 0;
            }

            boolean focus = !optionButton.isSelected() && !playButton.isSelected();
            caption.setActive(false);

            candy.characterPan(input, window);
            penguin.characterPan(input, window);
            cindy.characterPan(input, window);
            chester.characterPan(input, window);
            blank.characterPan(input, window);
            rat.characterPan(input, window);
            cat.characterPan(input, window);

            if (modeSectionTarget == 0) {
                candy.hover(input, 4.75f, 4, 1.75f, 1.5f);
                if (candy.update(caption, input, focus)) soundHandler.play("select");

                penguin.hover(input, 4, 4, 2, 1.5f);
                if (penguin.update(caption, input, focus)) soundHandler.play("select");

                cindy.hover(input, 3.75f, 4, 2.25f, 1.5f);
                if (cindy.update(caption, input, focus && !penguin.isHovered())) soundHandler.play("select");

                chester.hover(input, 3.5f, 4, 2.25f, 1.5f);
                if (chester.update(caption, input, focus)) soundHandler.play("select");

                blank.hover(input, 3.5f, 4, 2.25f, 1.5f);
                if (blank.update(caption, input, focus)) soundHandler.play("select");
            }

            if (modeSectionTarget == 1){
                rat.hover(input, 4, 4, 2, 1.5f);
                if (rat.update(caption, input, focus)) soundHandler.play("select");

                cat.hover(input, 4, 4, 2, 1.5f);
                if (cat.update(caption, input, focus)) soundHandler.play("select");
            }

            String modeName = modeSectionTarget == 0 ? "Candy's Showdown" : "Rat & Cat Theater";

            if (options.input(input)) soundHandler.play("select");

            if (!options.keySwitching) optionButton.update(null, input, true);
            options.updateAlpha(optionButton.isSelected());

            if (optionButton.isSelected()){
                List<Button> buttonsList = options.get(options.getSection() + 1);
                for (Button button: buttonsList){
                    boolean select = button.update(caption, input, false);
                    if (select && options.keySwitching && options.currentKeyButton != null && options.currentKeyButton != button){
                        button.setSelected();
                    }

                    if (!options.keySwitching && select && options.getSection() == 2){
                        engine.user.candys2Data.infiniteNight = buttonsList.get(0).isSelected();
                        engine.user.candys2Data.perspective = buttonsList.get(1).isSelected();
                        if (engine.gamejoltManager == null) FileUtils.writeUser(engine.jsonHandler, engine.user);
                        else {
                            engine.gamejoltManager.execute(() -> {
                                String value = engine.jsonHandler.writeCandysUser(engine.user);
                                engine.gamejoltManager.dataStore.set(engine.gamejoltManager, "user_id=" + engine.gamejoltManager.id, value);
                            });
                        }
                    }

                    if ((!options.keySwitching
                            || (options.currentKeyButton != null && options.currentKeyButton == button))
                        && select) {
                        soundHandler.play("select");
                        if (options.getSection() == 0) {
                            options.keySwitching = !options.keySwitching;
                            if (options.keySwitching) options.currentKeyButton = button;
                            else options.currentKeyButton = null;
                        }
                    }

                    if (options.keySwitching && options.currentKeyButton != null && options.currentKeyButton == button
                        && input.getCurrentKey() != -1){
                        boolean duplicate = true;
                        int keyCode = input.getCurrentKey();
                        if (button.getPath().equals(Input.Keys.toString(engine.user.fullscreenKey) + " - Fullscreen")){
                            if (engine.user.restartGameKey != keyCode && engine.user.returnMenuKey != keyCode
                                && engine.user.candys2Data.flashKey != keyCode) {
                                engine.user.fullscreenKey = keyCode;
                                button.setPath(Input.Keys.toString(engine.user.fullscreenKey) + " - Fullscreen");
                                duplicate = false;
                            }
                        } else if (button.getPath().equals(Input.Keys.toString(engine.user.restartGameKey) + " - Restart Game")){
                            if (engine.user.fullscreenKey != keyCode && engine.user.returnMenuKey != keyCode
                                    && engine.user.candys2Data.flashKey != keyCode) {
                                engine.user.restartGameKey = keyCode;
                                button.setPath(Input.Keys.toString(engine.user.restartGameKey) + " - Restart Game");
                                duplicate = false;
                            }
                        } else if (button.getPath().equals(Input.Keys.toString(engine.user.returnMenuKey) + " - Return to Menu")){
                            if (engine.user.fullscreenKey != keyCode && engine.user.restartGameKey != keyCode
                                    && engine.user.candys2Data.flashKey != keyCode) {
                                engine.user.returnMenuKey = keyCode;
                                button.setPath(Input.Keys.toString(engine.user.returnMenuKey) + " - Return to Menu");
                                duplicate = false;
                            }
                        } else if (button.getPath().equals(Input.Keys.toString(engine.user.candys2Data.flashKey) + " - Flash Key")){
                            if (engine.user.fullscreenKey != keyCode && engine.user.restartGameKey != keyCode
                                    && engine.user.returnMenuKey != keyCode) {
                                engine.user.candys2Data.flashKey = keyCode;
                                button.setPath(Input.Keys.toString(engine.user.candys2Data.flashKey) + " - Flash Key");
                                duplicate = false;
                            }
                        }

                        if (!duplicate) {
                            options.keySwitching = !options.keySwitching;
                            options.currentKeyButton = null;
                            button.setSelected();
                            soundHandler.play("select");
                            if (engine.gamejoltManager == null) FileUtils.writeUser(engine.jsonHandler, engine.user);
                            else {
                                engine.gamejoltManager.execute(() -> {
                                    String value = engine.jsonHandler.writeCandysUser(engine.user);
                                    engine.gamejoltManager.dataStore.set(engine.gamejoltManager, "user_id=" + engine.gamejoltManager.id, value);
                                });
                            }
                        } else {
                            soundHandler.play("error");
                        }
                    }
                }
            } else {
                if (laserPointerButton.update(caption, input, false)) soundHandler.play("select");
                if (faultyBatteryButton.update(caption, input, false)) soundHandler.play("select");
                if (faultyPhonesButton.update(caption, input, false)) soundHandler.play("select");
                shadowButton.update(caption, input, true, false);

                Candys2Data data = engine.user.candys2Data;
                var stars = modeSectionTarget == 0 ? data.newCandysShowdownStars : data.ratAndCatTheaterStars;

                if (star.update(modeName, caption, input, stars[0].time)) {
                    setMaxMode();
                    laserPointerButton.setSelected(false);
                    faultyBatteryButton.setSelected(false);
                    faultyPhonesButton.setSelected(false);
                    soundHandler.play("select");
                }
                if (laserPointerStar.update(modeName, caption, input, stars[1].time)){
                    setMaxMode();
                    laserPointerButton.setSelected(true);
                    faultyBatteryButton.setSelected(false);
                    faultyPhonesButton.setSelected(false);
                    soundHandler.play("select");
                }
                if (faultyBatteryStar.update(modeName, caption, input, stars[2].time)){
                    setMaxMode();
                    laserPointerButton.setSelected(false);
                    faultyBatteryButton.setSelected(true);
                    faultyPhonesButton.setSelected(false);
                    soundHandler.play("select");
                }
                if (faultyPhonesStar.update(modeName, caption, input, stars[3].time)){
                    setMaxMode();
                    laserPointerButton.setSelected(false);
                    faultyBatteryButton.setSelected(false);
                    faultyPhonesButton.setSelected(true);
                    soundHandler.play("select");
                }
                if (allChallengesStar.update(modeName, caption, input, stars[4].time)){
                    setMaxMode();
                    laserPointerButton.setSelected(true);
                    faultyBatteryButton.setSelected(true);
                    faultyPhonesButton.setSelected(true);
                    soundHandler.play("select");
                }
            }

            caption.update(engine, fontManager.getFont("font"), "candys2");
            playButton.update(null, input, true);
            if (optionButton.isSelected()) playButton.reset();
            if (!playButton.isSelected()) return;
            soundHandler.stopAllSounds();
            loadGame = true;
            options.alpha = 0;
            textureHandler.dispose();
            renderHandler.screenAlpha = 1;
            soundHandler.play("nightStart");
            loadStarted = false;
            nightStartFrame = 1;
            nightStartAlpha = 1;
        } else if (loadGame){
            boolean loaded = !engine.appHandler.getRenderHandler().lock;
            nightStartFrame -= Time.getDelta() * 1.5f;
            if (nightStartFrame <= 0) nightStartFrame = 0;

            if (nightStartFrame == 0){
                nightStartAlpha -= Time.getDelta() * 1.5f;
                if (nightStartAlpha <= 0) nightStartAlpha = 0;
            }

            if (nightStartFrame == 0 && nightStartAlpha == 0 && !loadStarted) {
                engine.candys2Deluxe.game.load(engine, modeSectionTarget, false);
                loadStarted = true;
            }

            float loadTimeTarget = 1.5f;
            if (playButton.isSelected()) playButton.setSelected();
            else if (loadTime == loadTimeTarget && loaded) {
                engine.candys2Deluxe.setState(1);
                playMenu = false;
                loadGame = false;
                loadTime = 0;
                return;
            }
            loadTime += Time.getDelta();
            if (loadTime > loadTimeTarget) loadTime = loadTimeTarget;
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
        if (!loadGame) renderMenu(engine);
        else renderLoading(engine);
    }

    private void renderLoading(Engine engine){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var window = engine.appHandler.window;
        var fontManager = engine.appHandler.getFontManager();
        var textureHandler = engine.appHandler.getTextureHandler();
        var font1 = fontManager.getFont("font");

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();

        fontManager.setCurrentFont(font1);
        font1.setColor(0.65f, 0.65f, 0.85f, 1);
        fontManager.setSize(54);
        String modeName = modeSectionTarget == 0 ? "Candy's Showdown" : "Rat & Cat Theater";
        fontManager.setText(modeName);

        fontManager.setPosition(true, true,
                (float) window.width() / 2,
                (float) window.height() / 2 + 16);
        fontManager.render(batch);

        int loadPercent = (int) (((float) textureHandler.currentPercent / textureHandler.maxPercent) * 100);
        font1.setColor(0.65f, 0.65f, 0.85f, 1 - nightStartAlpha);
        fontManager.setSize(26);
        fontManager.setText("Loading: " + loadPercent + "%");
        fontManager.setPosition(false, false, 16, 32);
        fontManager.render(batch);

        renderHandler.shapeDrawer.setColor(0.85f, 0.85f, 1, nightStartAlpha);

        float frame = (float) Math.sin((1 - nightStartFrame) * Math.PI / 2);
        float y = 340 * frame;
        float height = window.height() - 656 * frame;
        renderHandler.shapeDrawer.filledRectangle(0, y, window.width(), height);
    }

    private void renderMenu(Engine engine){
        if (engine.appHandler.getRenderHandler().lock) return;
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();
        var window = engine.appHandler.window;

        var fontManager = engine.appHandler.getFontManager();
        var font1 = fontManager.getFont("font");
        var font2 = fontManager.getFont("candys2/fontPixel");

        TextureRegion region;
        fontManager.setCurrentFont(font1);
        fontManager.setSize(15);
        caption.prepare(engine, captionFBO);

        options.fboDraw(engine, modeSectionTarget);

        renderHandler.screenBuffer.begin();
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();
        region = textureHandler.get("menu/background");
        batch.setColor(0.5f, 0.5f, 0.8f, renderHandler.screenAlpha);
        batch.draw(region,
                -49.5f + shakeScreen - candy.getInitX() + candy.getX(),
                -candy.getInitY() + candy.getY() - 28);
//        if (modeSectionTarget == 0) renderHandler.shapeDrawer.setOutlineColor(0.025f * renderHandler.screenAlpha, 0, 0.0625f * renderHandler.screenAlpha, 1);
//        else renderHandler.shapeDrawer.setOutlineColor(0.065f * renderHandler.screenAlpha, 0, 0.025f * renderHandler.screenAlpha, 1);
//        renderHandler.drawScreen();

        batch.setColor(0.65f, 0.65f, 0.85f, renderHandler.screenAlpha);

        float offset = modeSection * window.width();
        region = textureHandler.get("menu/candy");
        batch.draw(region, candy.getX() + shakeScreen - offset, candy.getY());

        region = textureHandler.get("menu/blank");
        batch.draw(region, blank.getX() + shakeScreen - offset, blank.getY());

        region = textureHandler.get("menu/cindy");
        batch.draw(region, cindy.getX() + shakeScreen - offset, cindy.getY());

        region = textureHandler.get("menu/chester");
        batch.draw(region, chester.getX() + shakeScreen - offset, chester.getY());

        region = textureHandler.get("menu/penguin");
        batch.draw(region, penguin.getX() + shakeScreen - offset, penguin.getY());

        offset = 1280 - offset;
        region = textureHandler.get("menu/rat");
        batch.draw(region, rat.getX() + shakeScreen + offset, rat.getY());

        region = textureHandler.get("menu/cat");
        batch.draw(region, cat.getX() + shakeScreen + offset, cat.getY());


        batch.flush();
        batch.setColor(1, 1, 1, 1);
        FrameBufferManager.end(batch, renderHandler.screenBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        FrameBufferManager.render(batch, renderHandler.screenBuffer, true);

        batch.setColor(0.65f, 0.65f, 0.85f, renderHandler.screenAlpha);
        region = textureHandler.getRegion("menu/static", 1024, (int) staticFrame);
        batch.draw(region, 0, 0, window.width(), window.height());
        batch.setColor(1, 1, 1, 1);

        fontManager.setCurrentFont(font2);
        fontManager.setOutlineLength(0.35f);
        fontManager.setOutlineColor(0.1f, 0, 0.2f, renderHandler.screenAlpha);
        font2.setColor(0.65f, 0.65f, 0.85f, renderHandler.screenAlpha);
        fontManager.setCurrentShader("outline");

        offset = modeSection * window.width();

        renderAILevel(batch, fontManager, candy, 2.5f, offset);
        renderAILevel(batch, fontManager, cindy, 2.5f, offset);
        renderAILevel(batch, fontManager, chester, 2.5f, offset);
        renderAILevel(batch, fontManager, penguin, 2.25f, offset);
        renderAILevel(batch, fontManager, blank, 2.5f, offset);

        offset = 1280 - offset;

        renderAILevel(batch, fontManager, rat, 2.5f, -offset);
        renderAILevel(batch, fontManager, cat, 2.5f, -offset);

        fontManager.resetOutline();

        Candys2Data data = engine.user.candys2Data;
        String modeName = modeSectionTarget == 0 ? "Candy's Showdown" : "Rat & Cat Theater";

        var stars = modeSectionTarget == 0 ? data.newCandysShowdownStars : data.ratAndCatTheaterStars;

        star.renderCandys2(engine, 0, stars[0].complete, false);
        laserPointerStar.renderCandys2(engine, 1.5f, stars[1].complete, false);
        faultyBatteryStar.renderCandys2(engine, 3, stars[2].complete, false);
        faultyPhonesStar.renderCandys2(engine, 0.5f, stars[3].complete, false);
        allChallengesStar.renderCandys2(engine, 2, stars[4].complete, false);

        fontManager.setCurrentFont(font1);
        fontManager.setSize(32);

        region = engine.appHandler.getMenuUI().play;
        textureHandler.setFilter(region.getTexture());
        float divider = 2 - playButton.getAlpha();
        batch.setColor(0.8f / divider, 0.8f / divider, 1 / divider, renderHandler.screenAlpha);
        batch.draw(region, playButton.getX(), playButton.getY());

        region = engine.appHandler.getMenuUI().options;
        textureHandler.setFilter(region.getTexture());
        divider = 2 - optionButton.getAlpha();
        batch.setColor(0.8f / divider, 0.8f / divider, 1 / divider, renderHandler.screenAlpha);
        batch.draw(region, optionButton.getX(), optionButton.getY());

        options.render(engine, modeSectionTarget);

        batch.setColor(0.6f, 0.6f, 0.8f, renderHandler.screenAlpha);

        region = textureHandler.get("menu/laserPointer" + (laserPointerButton.isSelected() ? "" : "Off"));
        batch.draw(region, laserPointerButton.getX(), laserPointerButton.getY());
        region = textureHandler.get("menu/faultyBattery" + (faultyBatteryButton.isSelected() ? "" : "Off"));
        batch.draw(region, faultyBatteryButton.getX(), faultyBatteryButton.getY());
        region = textureHandler.get("menu/faultyPhones" + (faultyPhonesButton.isSelected() ? "" : "Off"));
        batch.draw(region, faultyPhonesButton.getX(), faultyPhonesButton.getY());
        region = textureHandler.get("menu/shadow" + (shadowButton.isSelected() ? "" : "Off"));
        batch.draw(region, shadowButton.getX(), shadowButton.getY());

        batch.setColor(1, 1, 1, 1);
        font1.setColor(0.8f, 0.8f, 1, renderHandler.screenAlpha);

        fontManager.setSize(32);
        fontManager.setText(modeName);

        fontManager.setPosition(true, false, (float) window.width() / 2, 700);
        fontManager.render(batch);

        fontManager.setSize(15);
        if (caption.getText() != null) {
            if (caption.getText().equals("candy.txt")) {
                caption.render(engine, captionFBO, font1, candy.getX() + candy.getWidth() / 2,
                        candy.getY() + candy.getHeight() / 2.15f);
            } else if (caption.getText().equals("cindy.txt")) {
                caption.render(engine, captionFBO, font1, cindy.getX() + cindy.getWidth() / 2,
                        cindy.getY() + cindy.getHeight() / 2.15f);
            } else if (caption.getText().equals("chester.txt")) {
                caption.render(engine, captionFBO, font1, chester.getX() + chester.getWidth() / 2,
                        chester.getY() + chester.getHeight() / 2.15f);
            } else if (caption.getText().equals("penguin.txt")) {
                caption.render(engine, captionFBO, font1, penguin.getX() + penguin.getWidth() / 2,
                        penguin.getY() + penguin.getHeight());
            } else if (caption.getText().equals("blank.txt")) {
                caption.render(engine, captionFBO, font1, blank.getX() + blank.getWidth() / 2,
                        blank.getY() + blank.getHeight() / 2.15f);
            } else if (caption.getText().equals("rat.txt")) {
                caption.render(engine, captionFBO, font1, rat.getX() + rat.getWidth() / 2,
                    rat.getY() + rat.getHeight() / 2.15f);
            } else if (caption.getText().equals("cat.txt")) {
                caption.render(engine, captionFBO, font1, cat.getX() + cat.getWidth() / 2,
                        cat.getY() + cat.getHeight() / 2.15f);
            } else caption.render(engine, captionFBO, font1, (float) window.width() / 2, 180);
        }
        font1.setColor(0.8f, 0.8f, 1, renderHandler.screenAlpha);
        fontManager.setSize(18);
        fontManager.setText("Logged in as: " + engine.user.getUsername());
        fontManager.setRelativePosition(20, 700);
        fontManager.render(batch);

        if (engine.gamejoltManager != null && engine.gamejoltManager.threadRunning()) {
            if (!engine.gamejoltManager.session.isPinged()) fontManager.setText("Pinging session...");
            else if (!engine.gamejoltManager.dataStore.loaded) fontManager.setText("Loading save...");
            fontManager.setRelativePosition(20, 678);
            fontManager.render(batch);
        }

        fontManager.setText("Patch " + engine.version);
        fontManager.setRelativePosition(window.width() - fontManager.getLayout().width - 20, 700);
        fontManager.render(batch);

        renderHandler.shapeDrawer.setColor(0.25f, 0, 0.5f, (float) Math.sin(thunder * Math.PI));
        renderHandler.drawScreen();

//        candy.debugRender(renderHandler, engine.game);
//        cindy.debugRender(renderHandler, engine.game);
//        chester.debugRender(renderHandler, engine.game);
//        penguin.debugRender(renderHandler, engine.game);
//        blank.debugRender(renderHandler, engine.game);

//        if (modeSectionTarget == 1) {
//            rat.debugRender(renderHandler, engine.game);
//            cat.debugRender(renderHandler, engine.game);
//        }
    }

    private void renderAILevel(SpriteBatch batch, FontManager fontManager, MenuCharacter menuCharacter, float multiplier, float offset){
        fontManager.setSize(26);
        fontManager.setText("AI LEVEL");
        fontManager.setPosition(true, true,
                menuCharacter.getX() + shakeScreen + menuCharacter.getWidth() / 2 - offset,
                menuCharacter.getY() + menuCharacter.getHeight() / multiplier);
        fontManager.render(batch);
        fontManager.setSize(96);
        fontManager.setText(String.valueOf(menuCharacter.getAi()));
        fontManager.setPosition(true, true,
                menuCharacter.getX() + shakeScreen + menuCharacter.getWidth() / 2 - offset,
                menuCharacter.getY() + menuCharacter.getHeight() / multiplier - 52);
        fontManager.render(batch);
    }

    private void setMaxMode(){
        if (modeSectionTarget == 0) {
            candy.setAi(20);
            cindy.setAi(20);
            chester.setAi(20);
            penguin.setAi(20);
            blank.setAi(20);
        } else {
            rat.setAi(20);
            cat.setAi(20);
        }
    }
}
