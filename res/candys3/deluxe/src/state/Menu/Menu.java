package state.Menu;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.Candys3Deluxe;
import data.GameData;
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
    private final Button hellCastButton;
    private final Arrows arrows;
    private final Caption caption;

    private boolean shadow;
    private float thunderAlpha;
    private float staticAnimation;
    private float optionWindowAlpha;
    private byte section;

    private boolean playMenu;
    public boolean menuLoaded;
    private float menuPitch;

    public Menu(){
        rat = new MenuCharacter("menu/rat", 40, 44, 632, 632, 0);
        cat = new MenuCharacter("menu/cat", 692, 44, 428, 632,0);
        shadowRat = new MenuCharacter("menu/shadow_rat", 40, 44, 632, 632, 0);
        shadowCat = new MenuCharacter("menu/shadow_cat", 692, 44, 428, 632,0);
        optionButton = new Button("OPTIONS", 365, 24, 200, 100, 0);
        playButton = new Button("PLAY", 715, 24, 200, 100, 0);
        laserPointerButton = new Button("Laser Pointer", 220, 470, 84, 84, 0);
        hardCassetteButton = new Button("Hard Cassette", 220, 372, 84, 84, 0);
        nightmareCandyButton = new Button("Old Candy", 220, 274, 84, 84, 0);
        shadowChallengeButton = new Button("The Shadow Cast", 220, 176, 84, 84, 0);
        allChallengesButton = new Button("All Challenges", 670, 176, 84, 84, 0);
        freeScrollButton = new Button("Free Scroll", 670, 470, 84, 84, 0);
        infiniteNightButton = new Button("Infinite Night", 670, 372, 84, 84, 0);
        hellCastButton = new Button("The Hell Cast", 670, 274, 84, 84, 0);
        arrows = new Arrows();
        caption = new Caption();
    }

    public void load(){
        ImageManager.add("Static/Static");
        ImageManager.add("menu/button");
        ImageManager.add("menu/window");
        ImageManager.add("menu/option");
        ImageManager.add("menu/scroll_bar");
        ImageManager.add("menu/arrow");
        ImageManager.add("menu/rat");
        ImageManager.add("menu/cat");
        ImageManager.add("menu/shadow_rat");
        ImageManager.add("menu/shadow_cat");
    }

    public void update(Window window) {
        InputManager inputManager = Candys3Deluxe.inputManager;
        String menuKey = "shadow_menu";
        if (!RenderManager.lock && !playMenu) {
            SoundManager.play(menuKey);
            SoundManager.setLoop(menuKey, true);
            SoundManager.setVolume(menuKey,0.5f);
            SoundManager.play("menu_ambience");
            SoundManager.setLoop("menu_ambience", true);
            SoundManager.setVolume("menu_ambience",0.075f);
            playMenu = true;
        }
        if (shadow && menuPitch != 0.75f) menuPitch = 0.75f;
        else if (!shadow && menuPitch != 1) menuPitch = 1;
        if (menuPitch != SoundManager.getPitch(menuKey)) SoundManager.setPitch(menuKey, menuPitch);

        thunderAlpha = Time.decreaseTimeValue(thunderAlpha, 0, 4);
        staticAnimation = Time.increaseTimeValue(staticAnimation, 8, 30);
        if (staticAnimation == 8) staticAnimation = 0;

        boolean focus = !optionButton.isSelected() && !playButton.isSelected();
        caption.setActive(false);
        if (shadow){
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
            if (allChallengesButton.isSelected()){
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
        }
        else if ((shadowChallengeButton.isSelected() && !shadow) || (!shadowChallengeButton.isSelected() && shadow)) {
            shadow = !shadow;
            optionWindowAlpha = 0;
            thunderAlpha = 1;
            if (shadow){
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
        playButton.setSelected();
        if (optionWindowAlpha > 0) return;
        GameData.ratAI = rat.getAi();
        GameData.catAI = cat.getAi();
        GameData.shadowRatAI = shadowRat.getAi();
        GameData.shadowCatAI = shadowCat.getAi();
        Candys3Deluxe.stateManager.setState((byte) 1);
        playMenu = false;
        menuLoaded = false;
        SoundManager.stopAllSounds();
    }

    public void render(SpriteBatch batch, Window window){
        renderNightMenu(batch, window);
        debugRender(batch);
    }

    private void renderNightMenu(SpriteBatch batch, Window window){
        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        RenderManager.shapeDrawer.update();
        batch.enableBlending();
        batch.begin();

        TextureRegion region = ImageManager.getRegion("Static/Static", 1024, (int) staticAnimation);
        GlyphLayout layout = FontManager.layout;
        nightSetColor(batch, 2);
        batch.draw(region, 0, 0, 1280, 720);
        batch.setColor(1, 1, 1, 1);

        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);

        batch.setColor(0.8f, 0.8f, 0.8f, 1);
        if (shadow) {
            batch.draw(ImageManager.get(shadowRat.getPath()), shadowRat.getX(), shadowRat.getY());
            batch.draw(ImageManager.get(shadowCat.getPath()), shadowCat.getX(), shadowCat.getY());
        } else {
            batch.draw(ImageManager.get(rat.getPath()), rat.getX(), rat.getY());
            batch.draw(ImageManager.get(cat.getPath()), cat.getX(), cat.getY());
        }

        batch.flush();
        batch.setBlendFunction(srcFunc, dstFunc);

        fontAlpha(Candys3Deluxe.aiFont, 1, true);
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

        fontAlpha(Candys3Deluxe.aiFont, 1, true);
        layout.setText(Candys3Deluxe.aiFont, "AI: " + cat.getAi());
        Candys3Deluxe.aiFont.draw(batch, layout,
                cat.getX() + cat.getWidth() / 2 - layout.width / 2,
                cat.getY() + cat.getHeight() / 2 - layout.height / 2);

        fontAlpha(Candys3Deluxe.candysFont, 1, true);
        if (!shadow) layout.setText(Candys3Deluxe.candysFont, "The Main Cast");
        else layout.setText(Candys3Deluxe.candysFont, "The Shadow Cast");
        Candys3Deluxe.candysFont.draw(batch, layout,
                (float) window.getWidth() / 2 - layout.width / 2, 696);

        region = ImageManager.get("menu/button");
        for (byte i = 0; i < 2; i++) {
            Button button;
            if (i == 0) button = optionButton;
            else button = playButton;

            nightSetColor(batch, 2 - button.getAlpha());
            batch.draw(region, button.getX(), button.getY());
            fontAlpha(Candys3Deluxe.candysFont, button.getAlpha(), true);
            layout.setText(Candys3Deluxe.candysFont, button.getPath());
            Candys3Deluxe.candysFont.draw(batch, layout,
                    button.getX() + button.getWidth() / 2 - layout.width / 2,
                    90);
        }

        if (optionWindowAlpha > 0) {
            region = ImageManager.get("menu/window");
            nightSetColor(batch, 1.5f, optionWindowAlpha);
            batch.draw(region, 190, 144);
            batch.setColor(1, 1, 1, 1);

            fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
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
                fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha / 2, false);
                region = ImageManager.getRegion("menu/option", 84, 0);
                challengeButtonColor(batch, false, false, optionWindowAlpha / 2);
                batch.draw(region, nightmareCandyButton.getX(), nightmareCandyButton.getY());
                layout.setText(Candys3Deluxe.candysFont, nightmareCandyButton.getPath());
                Candys3Deluxe.candysFont.draw(batch, layout, nightmareCandyButton.getX() + 100, nightmareCandyButton.getY() + 55);

                //shadow cast
                fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
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

        RenderManager.shapeDrawer.setColor(1, 1, 1, thunderAlpha);
        RenderManager.shapeDrawer.filledRectangle(0, 0, 1280, 720);
    }

    private void debugRender(SpriteBatch batch){
        InputManager inputManager = Candys3Deluxe.inputManager;
        batch.setColor(1, 1, 1, 1);
        Candys3Deluxe.debugFont.draw(batch,
                "Mouse: " + (int) inputManager.getX() + " | " + (int) inputManager.getY(),
                24, 696);

//        rat.debugRender(batch, renderManager);
//        cat.debugRender(batch, renderManager);
//
//        debugFont.draw(batch,
//                "Java path: " + PathConstant.getJavaPath(),
//                24, 666);
//
//        debugFont.draw(batch,
//                "Java version: " + PathConstant.getJre(),
//                24, 636);
    }

    private void fontAlpha(BitmapFont font, float alpha, boolean tweak){
        if (tweak) alpha = 0.5f + alpha / 2;
        if (!shadow) font.setColor(1, 0, 0, alpha);
        else font.setColor(0.5f, 0, 1, alpha);
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

    private void nightSetColor(SpriteBatch batch, float divider){
        nightSetColor(batch, divider, 1);
    }

    private void nightSetColor(SpriteBatch batch, float divider, float alpha){
        if (!shadow) batch.setColor((float) 1 / divider, 0, 0, alpha);
        else batch.setColor(0.5f / divider, 0, (float) 1 / divider, alpha);
    }
}
