package state.Menu;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.Candys3Deluxe;
import deluxe.Paths;
import deluxe.GameData;
import state.Menu.Objects.Arrows;
import state.Menu.Objects.Button;
import state.Menu.Objects.Caption;
import state.Menu.Objects.MenuCharacter;
import util.*;

public class Menu {
    private final MenuCharacter rat;
    private final MenuCharacter cat;
    private final MenuCharacter shadowRat;
    private final MenuCharacter shadowCat;
    private final Button optionButton;
    private final Button playButton;
    private final Button laserPointerButton;
    private final Button hardCassetteButton;
    private final Button nightmareCandyButton;
    private final Button shadowChallengeButton;
    private final Button allChallengesButton;
    private final Button freeScrollButton;
    private final Button infiniteNightButton;
    private final Arrows arrows;
    private final Caption caption;

    private boolean shadow;
    private float thunderAlpha;
    private float staticFrame;
    private float optionWindowAlpha;
    private byte section;

    private boolean playMenu;
    public boolean menuLoaded;
    private boolean loadGame;
    private float loadTime;

    public Menu(){
        rat = new MenuCharacter(40, 44, 632, 632);
        cat = new MenuCharacter(692, 44, 428, 632);
        shadowRat = new MenuCharacter(40, 44, 632, 632);
        shadowCat = new MenuCharacter(692, 44, 428, 632);
        optionButton = new Button("OPTIONS", 365, 24, 200, 100);
        playButton = new Button("PLAY", 715, 24, 200, 100);
        laserPointerButton = new Button("Laser Pointer", 220, 470, 84, 84);
        hardCassetteButton = new Button("Hard Cassette", 220, 372, 84, 84);
        nightmareCandyButton = new Button("Nightmare Candy", 220, 274, 84, 84);
        shadowChallengeButton = new Button("The Shadow Cast", 220, 176, 84, 84);
        allChallengesButton = new Button("All Challenges", 670, 176, 84, 84);
        freeScrollButton = new Button("Free Scroll", 670, 470, 84, 84);
        infiniteNightButton = new Button("Infinite Night", 670, 372, 84, 84);
        arrows = new Arrows();
        caption = new Caption();
    }

    public void load(){
        ImageManager.add("Static/Static");
        ImageManager.addImages("menu/", Paths.dataPath1 + "menu/textures.txt");
        SoundManager.addSounds(Paths.dataPath1 + "menu/sounds.txt");
    }

    public void update(Window window, InputManager inputManager) {
        if (!RenderManager.lock && !playMenu) {
            SoundManager.play("deluxeMenu");
            SoundManager.setLoop("deluxeMenu", true);
            SoundManager.setVolume("deluxeMenu",0.5f);
            SoundManager.play("menuAmbience");
            SoundManager.setLoop("menuAmbience", true);
            SoundManager.setVolume("menuAmbience",0.075f);
            playMenu = true;
        }
        if (shadow) SoundManager.setPitch("deluxeMenu", 0.75f);
        else SoundManager.setPitch("deluxeMenu", 1);

        if (!loadGame) {
            thunderAlpha = Time.decreaseTimeValue(thunderAlpha, 0, 4);
            staticFrame = Time.increaseTimeValue(staticFrame, 8192, 30);
            if (staticFrame == 8192) staticFrame = 0;

            boolean focus = !optionButton.isSelected() && !playButton.isSelected();
            caption.setActive(false);
            if (shadow) {
                shadowRat.update(caption, inputManager, window, focus, (short) 3);
                shadowCat.update(caption, inputManager, window, focus, (short) 4);
            } else {
                rat.update(caption, inputManager, window, focus, (short) 1);
                cat.update(caption, inputManager, window, focus, (short) 2);
            }

            optionButton.update(inputManager, true);
            if (optionButton.isSelected()) {
                optionWindowAlpha = Time.increaseTimeValue(optionWindowAlpha, 1, 4);
                laserPointerButton.update(caption, inputManager, false, (short) 5);
                GameData.hitboxMultiplier = laserPointerButton.isSelected() ? 0.75f : 1;
                hardCassetteButton.update(caption, inputManager, false, (short) 6);
                GameData.hardCassette = hardCassetteButton.isSelected();
                nightmareCandyButton.update(caption, inputManager, false, (short) 9);
//            Challenges.nightmareCandy = nightmareCandyButton.isSelected();
                shadowChallengeButton.update(inputManager, false);
                allChallengesButton.update(inputManager, false);
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
//            freeScrollButton.update(caption, inputManager, false, (short) 7);
//            GameData.freeScroll = freeScrollButton.isSelected();
//            infiniteNightButton.update(caption, inputManager, false, (short) 8);
//            GameData.infiniteNight = infiniteNightButton.isSelected();
//            hellCastButton.update(caption, inputManager, false, (short) 9);

                arrows.update(inputManager, section);
                if (inputManager.isPressed() && arrows.getHovered() == 1) section--;
                else if (inputManager.isPressed() && arrows.getHovered() == 2) section++;
            } else if ((shadowChallengeButton.isSelected() && !shadow) || (!shadowChallengeButton.isSelected() && shadow)) {
                shadow = !shadow;
                optionWindowAlpha = 0;
                thunderAlpha = 1;
                if (shadow) {
                    GameData.night = 1;
                    shadowRat.setAi(rat.getAi());
                    shadowCat.setAi(cat.getAi());
                } else {
                    GameData.night = 0;
                    rat.setAi(shadowRat.getAi());
                    cat.setAi(shadowCat.getAi());
                }
                SoundManager.play("thunder");
            } else optionWindowAlpha = Time.decreaseTimeValue(optionWindowAlpha, 0, 4);

            caption.update();
            playButton.update(inputManager, true);
            if (!playButton.isSelected()) return;
            if (optionWindowAlpha > 0) {
                playButton.setSelected();
                return;
            }
            SoundManager.stopAllSounds();
            SoundManager.play("thunder");
            thunderAlpha = 1;
            loadGame = true;
        } else {
            if (playButton.isSelected()) {
                playButton.setSelected();
                return;
            }
            if (loadTime == 1) {
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
            thunderAlpha = Time.decreaseTimeValue(thunderAlpha, 0, 2);
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

        RenderManager.shapeDrawer.setColor(1, 1, 1, thunderAlpha);
        RenderManager.shapeDrawer.filledRectangle(0, 0, 1280, 720);

//        debugRender();
    }

    private void renderLoading(SpriteBatch batch, Window window){
        RenderManager.shapeDrawer.setColor(0, 0, 0, 1);
        RenderManager.shapeDrawer.filledRectangle(0, 0, 1280, 720);

        GlyphLayout layout = FontManager.layout;
        Candys3Deluxe.fontAlpha(Candys3Deluxe.loadFont, 1, true);
        if (!shadow) layout.setText(Candys3Deluxe.loadFont, "The Main Cast");
        else layout.setText(Candys3Deluxe.loadFont, "The Shadow Cast");
        Candys3Deluxe.loadFont.draw(batch, layout,
                (float) window.getWidth() / 2 - layout.width / 2,
                (float) window.getHeight() / 2 + layout.height / 2);
    }

    private void renderNightMenu(SpriteBatch batch, Window window){
        GlyphLayout layout = FontManager.layout;
        TextureRegion region = ImageManager.getRegion("Static/Static", 1024, (int) staticFrame % 8);
        Candys3Deluxe.nightSetColor(batch, 2);
        batch.draw(region, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, 1);

        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);

        batch.setColor(0.8f, 0.8f, 0.8f, 1);
        if (shadow) {
            batch.draw(ImageManager.get("menu/shadowRat"), shadowRat.getX(), shadowRat.getY());
            batch.draw(ImageManager.get("menu/shadowCat"), shadowCat.getX(), shadowCat.getY());
        } else {
            batch.draw(ImageManager.get("menu/rat"), rat.getX(), rat.getY());
            batch.draw(ImageManager.get("menu/cat"), cat.getX(), cat.getY());
        }

        batch.flush();
        batch.setBlendFunction(srcFunc, dstFunc);

        Candys3Deluxe.fontAlpha(Candys3Deluxe.aiFont, 1, true);
        MenuCharacter rat;
        MenuCharacter cat;
        if (!shadow){
            rat = this.rat;
            cat = this.cat;
        } else {
            rat = shadowRat;
            cat = shadowCat;
        }
        layout.setText(Candys3Deluxe.aiFont, "AI: " + rat.getAi());
        Candys3Deluxe.aiFont.draw(batch, layout,
                rat.getX() + rat.getWidth() / 2 - layout.width / 2,
                rat.getY() + rat.getHeight() / 2 - layout.height / 2);

        Candys3Deluxe.fontAlpha(Candys3Deluxe.aiFont, 1, true);
        layout.setText(Candys3Deluxe.aiFont, "AI: " + cat.getAi());
        Candys3Deluxe.aiFont.draw(batch, layout,
                cat.getX() + cat.getWidth() / 2 - layout.width / 2,
                cat.getY() + cat.getHeight() / 2 - layout.height / 2);

        Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, 1, true);
        if (!shadow) layout.setText(Candys3Deluxe.candysFont, "The Main Cast");
        else layout.setText(Candys3Deluxe.candysFont, "The Shadow Cast");
        Candys3Deluxe.candysFont.draw(batch, layout,
                (float) window.getWidth() / 2 - layout.width / 2, 696);

        region = ImageManager.get("menu/button");
        for (byte i = 0; i < 2; i++) {
            Button button;
            if (i == 0) button = optionButton;
            else button = playButton;

            Candys3Deluxe.nightSetColor(batch, 2 - button.getAlpha());
            batch.draw(region, button.getX(), button.getY());
            Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, button.getAlpha(), true);
            layout.setText(Candys3Deluxe.candysFont, button.getPath());
            Candys3Deluxe.candysFont.draw(batch, layout,
                    button.getX() + button.getWidth() / 2 - layout.width / 2,
                    90);
        }

        if (optionWindowAlpha > 0) {
            region = ImageManager.get("menu/window");
            Candys3Deluxe.nightSetColor(batch, 1.5f, optionWindowAlpha);
            batch.draw(region, 190, 144);
            batch.setColor(1, 1, 1, 1);

            Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
            if (section == 0) layout.setText(Candys3Deluxe.candysFont, "Challenges");
            else if (section == 1) layout.setText(Candys3Deluxe.candysFont, "Cheats");
            else if (section == 2) layout.setText(Candys3Deluxe.candysFont, "Settings");

            float sectionX = (float) window.getWidth() / 2 - layout.width / 2;
            Candys3Deluxe.candysFont.draw(batch, layout, sectionX, 627);

            region = ImageManager.get("menu/arrow");
            region.flip(true, false);
            if (section != 0) batch.draw(region, 488, 594);
            region.flip(true, false);
            if (section != 2) batch.draw(region, 752, 594);

            if (section == 0){
                //laser pointer
                if (laserPointerButton.isSelected()) region = ImageManager.getRegion("menu/option", 84, 1);
                else region = ImageManager.getRegion("menu/option", 84, 0);
                challengeButtonColor(batch, laserPointerButton.isHovered(), laserPointerButton.isSelected(), optionWindowAlpha);
                batch.draw(region, laserPointerButton.getX(), laserPointerButton.getY());
                layout.setText(Candys3Deluxe.candysFont, laserPointerButton.getPath());
                Candys3Deluxe.candysFont.draw(batch, layout, laserPointerButton.getX() + 100, laserPointerButton.getY() + 55);

                //hard Cassette
                if (hardCassetteButton.isSelected()) region = ImageManager.getRegion("menu/option", 84, 1);
                else region = ImageManager.getRegion("menu/option", 84, 0);
                challengeButtonColor(batch, hardCassetteButton.isHovered(), hardCassetteButton.isSelected(), optionWindowAlpha);
                batch.draw(region, hardCassetteButton.getX(), hardCassetteButton.getY());
                layout.setText(Candys3Deluxe.candysFont, hardCassetteButton.getPath());
                Candys3Deluxe.candysFont.draw(batch, layout, hardCassetteButton.getX() + 100, hardCassetteButton.getY() + 55);

                //nightmare candy
                Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha / 2, false);
                region = ImageManager.getRegion("menu/option", 84, 0);
                challengeButtonColor(batch, false, false, optionWindowAlpha / 2);
                batch.draw(region, nightmareCandyButton.getX(), nightmareCandyButton.getY());
                layout.setText(Candys3Deluxe.candysFont, nightmareCandyButton.getPath());
                Candys3Deluxe.candysFont.draw(batch, layout, nightmareCandyButton.getX() + 100, nightmareCandyButton.getY() + 55);

                //shadow cast
                Candys3Deluxe.fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
                if (shadowChallengeButton.isSelected()) region = ImageManager.getRegion("menu/option", 84, 1);
                else region = ImageManager.getRegion("menu/option", 84, 0);
                challengeButtonColor(batch, shadowChallengeButton.isHovered(), shadowChallengeButton.isSelected(), optionWindowAlpha);
                batch.draw(region, shadowChallengeButton.getX(), shadowChallengeButton.getY());
                layout.setText(Candys3Deluxe.candysFont, shadowChallengeButton.getPath());
                Candys3Deluxe.candysFont.draw(batch, layout, shadowChallengeButton.getX() + 100, shadowChallengeButton.getY() + 55);

                //all challenges
                boolean allChallenges = laserPointerButton.isSelected() && hardCassetteButton.isSelected();
                if (allChallenges) region = ImageManager.getRegion("menu/option", 84, 1);
                else region = ImageManager.getRegion("menu/option", 84, 0);
                challengeButtonColor(batch, allChallengesButton.isHovered(), allChallenges, optionWindowAlpha);
                batch.draw(region, allChallengesButton.getX(), allChallengesButton.getY());
                layout.setText(Candys3Deluxe.candysFont, allChallengesButton.getPath());
                Candys3Deluxe.candysFont.draw(batch, layout, allChallengesButton.getX() + 100, allChallengesButton.getY() + 55);

            }
        }
        caption.render(batch, window);
        batch.setColor(1, 1, 1, 1);
    }

    private void debugRender(){
        rat.debugRender();
        cat.debugRender();
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
