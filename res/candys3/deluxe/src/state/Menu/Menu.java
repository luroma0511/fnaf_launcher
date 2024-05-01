package state.Menu;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Candys3Deluxe;
import deluxe.Paths;
import deluxe.GameData;
import deluxe.UserData;
import state.Menu.Objects.*;
import util.*;

public class Menu {
    private final FrameBuffer captionFBO;

    private final MenuCharacter rat;
    private final MenuCharacter cat;
    private final MenuCharacter shadowRat;
    private final MenuCharacter shadowCat;
    private final Button optionButton;
    private final Button playButton;
    private Button laserPointerButton;
    private Button hardCassetteButton;
    private Button shadowChallengeButton;
    private Button allChallengesButton;
    private Button flashDebugButton;
    private Button hitboxDebugButton;
    private Button noJumpscaresButton;
    private Button freeScrollButton;
    private Button infiniteNightButton;
    private Button restartOnJumpscareButton;
    private final Arrows arrows;
    private final Caption caption;
    private final Star star;
    private final Star laserStar;
    private final Star hardCassetteStar;
    private final Star allChallengesStar;

    private boolean shadow;
    private float whiteAlpha;
    private float staticFrame;
    private float optionWindowAlpha;
    private float windowX;
    private byte section;

    private boolean playMenu;
    public boolean menuLoaded;
    private boolean loadGame;
    private float loadTime;

    public Menu(){
        rat = new MenuCharacter(40, 44, 632, 632, false, 1);
        cat = new MenuCharacter(692, 44, 428, 632, false, 2);
        shadowRat = new MenuCharacter(40, 44, 632, 632, false, 3);
        shadowCat = new MenuCharacter(692, 44, 428, 632, false, 4);
        optionButton = new Button("OPTIONS", 365, 24, 200, 100, 0);
        playButton = new Button("PLAY", 715, 24, 200, 100, 0);
        arrows = new Arrows();
        caption = new Caption();

        captionFBO = FrameBufferManager.newFrameBuffer();

        int size = 40;
        int xPos = (int) (640 - (float) size / 2);
        star = new Star(xPos - 56, 128, size, size, 13);
        laserStar = new Star(xPos, 128, size, size, 14);
        hardCassetteStar = new Star(xPos + 56, 128, size, size, 15);
        size = 96;
        xPos = (int) (640 - (float) size / 2);
        allChallengesStar = new Star(xPos, 164, size, size, 16);
    }

    public void load(){
        ImageManager.add("Static/Static");
        ImageManager.addImages("menu/", Paths.dataPath1 + "menu/textures.txt");
        SoundManager.addAll(Paths.dataPath1 + "menu/sounds.txt");
        RenderManager.screenAlpha = 0;
    }

    public void update(Window window, InputManager inputManager) {
        if (RenderManager.lock) return;
        if (windowX == 0) {
            windowX = (float) window.width() / 2 - (float) ImageManager.get("menu/window").getRegionWidth() / 2;
            laserPointerButton = new Button("Laser Pointer", (int) (windowX + 32), 470, 84, 84, 5);
            hardCassetteButton = new Button("Hard Cassette", (int) (windowX + 32), 372, 84, 84, 6);
            shadowChallengeButton = new Button("The Shadow Cast", (int) (windowX + 32), 274, 84, 84, 0);
            allChallengesButton = new Button("All Challenges", (int) (windowX + 32), 176, 84, 84, 0);
            flashDebugButton = new Button("Flash Debug", (int) (windowX + 32), 470, 84, 84, 7);
            hitboxDebugButton = new Button("Hitbox Debug", (int) (windowX + 32), 372, 84, 84, 8);
            noJumpscaresButton = new Button("No Jumpscares", (int) (windowX + 32), 274, 84, 84, 9);
            freeScrollButton = new Button("Free Scroll", (int) (windowX + 32), 470, 84, 84, 10);
            infiniteNightButton = new Button("Infinite Night", (int) (windowX + 32), 372, 84, 84, 11);
            restartOnJumpscareButton = new Button("Restart on Jumpscare", (int) (windowX + 32), 274, 84, 84, 12);
        }
        if (!playMenu) {
            SoundManager.play("deluxeMenu");
            SoundManager.setSoundEffect(SoundManager.LOOP, "deluxeMenu", 1);
            SoundManager.setSoundEffect(SoundManager.VOLUME, "deluxeMenu",0.5f);
            SoundManager.play("menuAmbience");
            SoundManager.setSoundEffect(SoundManager.LOOP, "menuAmbience", 1);
            SoundManager.setSoundEffect(SoundManager.VOLUME, "menuAmbience",0.075f);
            playMenu = true;
        }
        if (shadow) SoundManager.setSoundEffect(SoundManager.PITCH, "deluxeMenu", 0.75f);
        else SoundManager.setSoundEffect(SoundManager.PITCH, "deluxeMenu", 1);

        if (!loadGame) {
            RenderManager.screenAlpha = Time.increaseTimeValue(RenderManager.screenAlpha, 1, 4);
            whiteAlpha = Time.decreaseTimeValue(whiteAlpha, 0, 3);
            staticFrame = Time.increaseTimeValue(staticFrame, 8192, 30);
            if (staticFrame == 8192) staticFrame = 0;

            boolean focus = !optionButton.isSelected() && !playButton.isSelected();
            caption.setActive(false);
            if (shadow) {
                shadowRat.update(caption, inputManager, window, focus);
                shadowCat.update(caption, inputManager, window, focus);
            } else {
                rat.update(caption, inputManager, window, focus);
                cat.update(caption, inputManager, window, focus);
            }

            optionButton.update(caption, inputManager, true);
            if (optionButton.isSelected()) {
                optionWindowAlpha = Time.increaseTimeValue(optionWindowAlpha, 1, 4);
                if (section == 0) {
                    laserPointerButton.update(caption, inputManager, false);
                    GameData.hitboxMultiplier = laserPointerButton.isSelected() ? 0.75f : 1;
                    hardCassetteButton.update(caption, inputManager, false);
                    GameData.hardCassette = hardCassetteButton.isSelected();
                    shadowChallengeButton.update(caption, inputManager, false);
                    allChallengesButton.update(caption, inputManager, false);
                    if (allChallengesButton.isSelected()) {
                        if (laserPointerButton.isSelected() && hardCassetteButton.isSelected()) {
                            laserPointerButton.setSelected();
                            hardCassetteButton.setSelected();
                        } else {
                            if (!laserPointerButton.isSelected()) laserPointerButton.setSelected();
                            if (!hardCassetteButton.isSelected()) hardCassetteButton.setSelected();
                        }
                        allChallengesButton.setSelected();
                    }
                } else if (section == 1){
                    flashDebugButton.update(caption, inputManager, false);
                    GameData.flashDebug = flashDebugButton.isSelected();
                    hitboxDebugButton.update(caption, inputManager, false);
                    GameData.hitboxDebug = hitboxDebugButton.isSelected();
                    noJumpscaresButton.update(caption, inputManager, false);
                    GameData.noJumpscares = noJumpscaresButton.isSelected();
                } else {
                    freeScrollButton.update(caption, inputManager, false);
                    GameData.freeScroll = freeScrollButton.isSelected();
                    infiniteNightButton.update(caption, inputManager, false);
                    GameData.infiniteNight = infiniteNightButton.isSelected();
                    restartOnJumpscareButton.update(caption, inputManager, false);
                    GameData.restartOnJumpscare = restartOnJumpscareButton.isSelected();
                }
                arrows.update(inputManager, section);
                if (inputManager.isLeftPressed() && arrows.getHovered() == 1) section--;
                else if (inputManager.isLeftPressed() && arrows.getHovered() == 2) section++;
            } else if ((shadowChallengeButton.isSelected() && !shadow) || (!shadowChallengeButton.isSelected() && shadow)) {
                shadow = !shadow;
                optionWindowAlpha = 0;
                whiteAlpha = 1;
                if (shadow) {
                    GameData.night = 1;
                    shadowRat.setAi(rat.getAi());
                    shadowCat.setAi(cat.getAi());
                    star.setCaptionID(17);
                    laserStar.setCaptionID(18);
                    hardCassetteStar.setCaptionID(19);
                    allChallengesStar.setCaptionID(20);
                } else {
                    GameData.night = 0;
                    rat.setAi(shadowRat.getAi());
                    cat.setAi(shadowCat.getAi());
                    star.setCaptionID(13);
                    laserStar.setCaptionID(14);
                    hardCassetteStar.setCaptionID(15);
                    allChallengesStar.setCaptionID(16);
                }
                SoundManager.play("thunder");
            } else {
                star.update(caption, inputManager);
                laserStar.update(caption, inputManager);
                hardCassetteStar.update(caption, inputManager);
                allChallengesStar.update(caption, inputManager);
                optionWindowAlpha = Time.decreaseTimeValue(optionWindowAlpha, 0, 4);
            }
            caption.update(Candys3Deluxe.captionFont);
            playButton.update(caption, inputManager, true);
            if (!playButton.isSelected()) return;
            if (optionWindowAlpha > 0) {
                playButton.setSelected();
                return;
            }
            SoundManager.stopAllSounds();
            SoundManager.play("thunder");
            whiteAlpha = 1;
            loadGame = true;
            RenderManager.screenAlpha = 1;
        } else {
            if (playButton.isSelected()) playButton.setSelected();
            else if (loadTime == 1) {
                GameData.ratAI = rat.getAi();
                GameData.catAI = cat.getAi();
                GameData.shadowRatAI = shadowRat.getAi();
                GameData.shadowCatAI = shadowCat.getAi();
                Candys3Deluxe.stateManager.setState((byte) 1);
                playMenu = false;
                menuLoaded = false;
                loadGame = false;
                loadTime = 0;
                return;
            }
            whiteAlpha = Time.decreaseTimeValue(whiteAlpha, 0, 2);
            loadTime = Time.increaseTimeValue(loadTime, 1, 1.5f);
        }
    }

    public void render(SpriteBatch batch, Window window){
        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        RenderManager.shapeDrawer.update();
        batch.enableBlending();
        batch.begin();
        if (!loadGame) renderNightMenu(batch, window);
        else renderLoading(batch, window);

        RenderManager.shapeDrawer.setColor(1, 1, 1, whiteAlpha);
        RenderManager.shapeDrawer.filledRectangle(0, 0, 1280, 720);
    }

    private void renderLoading(SpriteBatch batch, Window window){
        RenderManager.screenBuffer.begin();
        RenderManager.shapeDrawer.setColor(0, 0, 0, 1);
        RenderManager.shapeDrawer.filledRectangle(0, 0, 1280, 720);

        FontManager.setFont(Candys3Deluxe.candysFont);
        Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, 1, true);
        if (!shadow) FontManager.setText("The Main Cast");
        else FontManager.setText("The Shadow Cast");
        FontManager.setSize(54);
        FontManager.render(batch, true, true,
                (float) window.width() / 2,
                (float) window.height() / 2);
    }

    private void renderNightMenu(SpriteBatch batch, Window window){
        FontManager.setFont(Candys3Deluxe.captionFont);
        FontManager.setSize(15);
        caption.prepare(batch, captionFBO, Candys3Deluxe.captionFont);

        RenderManager.screenBuffer.begin();

        TextureRegion region = ImageManager.getRegion("Static/Static", 1024, (int) staticFrame % 8);
        Candys3Deluxe.setNightColor(batch, 2);
        batch.draw(region, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, 1);

        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);

        batch.setColor(0.8f, 0.8f, 0.8f, 1);
        if (shadow) {
            batch.setColor(0.8f, 0.8f, 0.8f, 1);
            batch.draw(ImageManager.get("menu/shadowRat"), shadowRat.getX(), shadowRat.getY());
            batch.draw(ImageManager.get("menu/shadowCat"), shadowCat.getX(), shadowCat.getY());
        } else {
            batch.draw(ImageManager.get("menu/rat"), rat.getX(), rat.getY());
            batch.draw(ImageManager.get("menu/cat"), cat.getX(), cat.getY());
        }

        batch.flush();
        batch.setBlendFunction(srcFunc, dstFunc);

        byte[] modeStars;
        if (GameData.night == 0) modeStars = UserData.mainCastStar;
        else modeStars = UserData.shadowCastStar;

        region = ImageManager.get("menu/star");
        region.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Star star;
        for (byte i = 0; i <= 3; i++){
            starColor(batch, modeStars, i);
            if (i == 0) star = this.star;
            else if (i == 1) star = laserStar;
            else if (i == 2) star = hardCassetteStar;
            else star = allChallengesStar;
            batch.draw(region, star.getX(), star.getY(), star.getWidth(), star.getHeight());
        }
        batch.setColor(1, 1, 1, 1);

        MenuCharacter rat;
        MenuCharacter cat;
        if (!shadow){
            rat = this.rat;
            cat = this.cat;
        } else {
            rat = shadowRat;
            cat = shadowCat;
        }

        FontManager.setFont(Candys3Deluxe.candysFont);
        FontManager.setSize(72);
        Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, 1, true);
        FontManager.setText(!rat.isAiCustom() ? "AI: " + (rat.getAi() == 0 ? "Off": "On"): "AI: " + rat.getAi());
        FontManager.render(batch, true, true,
                rat.getX() + rat.getWidth() / 2,
                rat.getY() + rat.getHeight() / 2.5f);

        Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, 1, true);
        FontManager.setText(!cat.isAiCustom() ? "AI: " + (cat.getAi() == 0 ? "Off": "On"): "AI: " + cat.getAi());
        FontManager.render(batch, true, true,
                cat.getX() + cat.getWidth() / 2,
                cat.getY() + cat.getHeight() / 2.5f);

        Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, 1, true);
        FontManager.setSize(40);
        if (!shadow) FontManager.setText("The Main Cast");
        else FontManager.setText("The Shadow Cast");
        FontManager.render(batch, true, false, (float) window.width() / 2, 696);

        region = ImageManager.get("menu/button");
        for (byte i = 0; i < 2; i++) {
            Button button;
            if (i == 0) button = optionButton;
            else button = playButton;

            Candys3Deluxe.setNightColor(batch, 2 - button.getAlpha());
            batch.draw(region, button.getX(), button.getY());
            Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, button.getAlpha(), true);
            FontManager.setText(button.getPath());
            FontManager.render(batch, true, false, button.getX() + button.getWidth() / 2, 90);
        }

        if (optionWindowAlpha > 0) {
            region = ImageManager.get("menu/window");
            Candys3Deluxe.setNightColor(batch, 1.5f, optionWindowAlpha);
            batch.draw(region, windowX, 144);
            batch.setColor(1, 1, 1, 1);

            Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
            if (section == 0) FontManager.setText("Challenges");
            else if (section == 1) FontManager.setText("Cheats");
            else FontManager.setText("Settings");

            FontManager.setOutline(0.35f);
            if (GameData.night == 0) FontManager.setColor(0.2f, 0, 0, optionWindowAlpha);
            else FontManager.setColor(0.1f, 0, 0.2f, optionWindowAlpha);
            FontManager.render(batch, true, false, (float) window.width() / 2, 627);
            FontManager.setOutline(0.5f);
            FontManager.setColor(0, 0, 0, 1);

            Candys3Deluxe.setNightColor(batch, 1, optionWindowAlpha);
            region = ImageManager.get("menu/arrow");
            region.flip(true, false);
            if (section != 0) batch.draw(region, 488, 594);
            region.flip(true, false);
            if (section != 2) batch.draw(region, 752, 594);
            batch.setColor(1, 1, 1, 1);

            if (section == 0){
                buttonRender(batch, laserPointerButton);
                buttonRender(batch, hardCassetteButton);
                buttonRender(batch, shadowChallengeButton);
                boolean allChallenges = allChallengesButton.isSelected();
                allChallengesButton.setSelected(laserPointerButton.isSelected() && hardCassetteButton.isSelected());
                buttonRender(batch, allChallengesButton);
                allChallengesButton.setSelected(allChallenges);
            } else if (section == 1){
                buttonRender(batch, flashDebugButton);
                buttonRender(batch, hitboxDebugButton);
                buttonRender(batch, noJumpscaresButton);
            } else {
                buttonRender(batch, freeScrollButton);
                buttonRender(batch, infiniteNightButton);
                buttonRender(batch, restartOnJumpscareButton);
            }
        }
        FontManager.setFont(Candys3Deluxe.captionFont);
        caption.render(batch, Candys3Deluxe.inputManager, captionFBO, window);
        batch.setColor(1, 1, 1, 1);

//        rat.debugRender();
//        cat.debugRender();
    }

    private void starColor(SpriteBatch batch, byte[] modeStars, byte index){
        if (modeStars[index] == 0) batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        else if (index == 0) batch.setColor(1, 0, 0, 1);
        else if (index != 3) batch.setColor(1, 0.25f, 1, 1);
        else batch.setColor(1, 1, 0, 1);
    }

    private void buttonRender(SpriteBatch batch, Button button){
        Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
        TextureRegion region;
        if (button.isSelected()) region = ImageManager.getRegion("menu/option", 84, 1);
        else region = ImageManager.getRegion("menu/option", 84, 0);
        challengeButtonColor(batch, button.isHovered(), button.isSelected(), optionWindowAlpha);
        batch.draw(region, button.getX(), button.getY());
        FontManager.setText(button.getPath());
        FontManager.render(batch, button.getX() + 100, button.getY() + 55);
    }

    private void challengeButtonColor(SpriteBatch batch, boolean hover, boolean select, float alpha){
        if (hover) batch.setColor(1, 1, 1, alpha);
        else if (shadow){
            if (select) batch.setColor(0.85f, 0.7f, 1, alpha);
            else batch.setColor(0.5f, 0, 1, alpha);
        } else {
            if (select) batch.setColor(1, 0.7f, 0.7f, alpha);
            else batch.setColor(1, 0, 0, alpha);
        }
    }
}
