package core.state.Menu;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.Candys3Deluxe;
import core.data.Challenges;
import core.data.GameData;
import core.state.Menu.Objects.Button;
import core.state.Menu.Objects.Caption;
import core.state.Menu.Objects.MenuCharacter;
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
    private final Button freeScrollButton;
    private final Button infiniteNightButton;
    private final Button hellCastButton;
    private final Caption caption;

    private boolean shadow;
    private float thunderAlpha;
    private float staticAnimation;
    private float optionWindowAlpha;

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
        freeScrollButton = new Button("Free Scroll", 664, 470, 84, 84, 0);
        infiniteNightButton = new Button("Infinite Night", 664, 372, 84, 84, 0);
        hellCastButton = new Button("The Hell Cast", 664, 274, 84, 84, 0);
        caption = new Caption();
    }

    public void load(){
        ImageManager.add("Static/Static");
        ImageManager.add("menu/button");
        ImageManager.add("menu/window");
        ImageManager.add("menu/option");
        ImageManager.add("menu/scroll_bar");
        ImageManager.add("menu/rat");
        ImageManager.add("menu/cat");
        ImageManager.add("menu/shadow_rat");
        ImageManager.add("menu/shadow_cat");
    }

    public void update(GameData gameData) {
        InputManager inputManager = Candys3Deluxe.inputManager;
        if (!RenderManager.lock && !playMenu) {
            SoundManager.play("menu");
            SoundManager.setLoop("menu", true);
            SoundManager.setVolume("menu",0.25f);
            playMenu = true;
        }
        if (shadow && menuPitch != 0.75f) menuPitch = 0.75f;
        else if (!shadow && menuPitch != 1) menuPitch = 1;
        if (menuPitch != SoundManager.getPitch("menu")) SoundManager.setPitch("menu", menuPitch);

        thunderAlpha = Time.decreaseTimeValue(thunderAlpha, 0, 4);
        staticAnimation = Time.increaseTimeValue(staticAnimation, 8, 30);
        if (staticAnimation == 8) staticAnimation = 0;

        boolean focus = !optionButton.isSelected() && !playButton.isSelected();
        caption.setActive(false);
        if (shadow){
            shadowRat.update(caption, inputManager, focus, (short) 3);
            shadowCat.update(caption, inputManager, focus, (short) 4);
        } else {
            rat.update(caption, inputManager, focus, (short) 1);
            cat.update(caption, inputManager, focus, (short) 2);
        }

        optionButton.update(inputManager, true);
        if (optionButton.isSelected()) {
            optionWindowAlpha = Time.increaseTimeValue(optionWindowAlpha, 1, 4);
            laserPointerButton.update(caption, inputManager, false, (short) 5);
            Challenges.laserPointer = laserPointerButton.isSelected();
            hardCassetteButton.update(caption, inputManager, false, (short) 6);
            Challenges.hardCassette = hardCassetteButton.isSelected();
            nightmareCandyButton.update(caption, inputManager, false, (short) 9);
//            Challenges.nightmareCandy = nightmareCandyButton.isSelected();
            shadowChallengeButton.update(inputManager, false);
            freeScrollButton.update(caption, inputManager, false, (short) 7);
            Challenges.freeScroll = freeScrollButton.isSelected();
            infiniteNightButton.update(caption, inputManager, false, (short) 8);
            Challenges.infiniteNight = infiniteNightButton.isSelected();
            hellCastButton.update(caption, inputManager, false, (short) 9);
        }
        else if ((shadowChallengeButton.isSelected() && !shadow) || (!shadowChallengeButton.isSelected() && shadow)) {
            shadow = !shadow;
            optionWindowAlpha = 0;
            thunderAlpha = 1;
            if (shadow){
                shadowRat.setAi(rat.getAi());
                shadowCat.setAi(cat.getAi());
                rat.setAi((byte) 0);
                cat.setAi((byte) 0);
            } else {
                rat.setAi(shadowRat.getAi());
                cat.setAi(shadowCat.getAi());
                shadowRat.setAi((byte) 0);
                shadowCat.setAi((byte) 0);
            }
            SoundManager.play("thunder");
        } else optionWindowAlpha = Time.decreaseTimeValue(optionWindowAlpha, 0, 4);

        caption.update();
        playButton.update(inputManager, true);
        if (!playButton.isSelected()) return;
        playButton.setSelected(false);
        if (optionWindowAlpha > 0) return;
        gameData.writeData(rat.getAi(), cat.getAi(), (byte) 0, shadowRat.getAi(), shadowCat.getAi());
        Candys3Deluxe.stateManager.setState((byte) 1);
        playMenu = false;
        menuLoaded = false;
        SoundManager.stopAllSounds();
    }

    public void render(SpriteBatch batch){
        renderNightMenu(batch);
        debugRender(batch);
    }

    private void renderNightMenu(SpriteBatch batch){
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
                (float) Candys3Deluxe.width / 2 - layout.width / 2, 696);

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
            nightSetColor(batch, 1, optionWindowAlpha);
            batch.draw(region, 190, 144);
            batch.setColor(1, 1, 1, 1);

            fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
            layout.setText(Candys3Deluxe.candysFont, "Challenges");
            Candys3Deluxe.candysFont.draw(batch, layout,
                    (float) Candys3Deluxe.width / 2 - layout.width / 2, 629);

            byte fileIndex = -1;
            for (byte i = 0; i < 7; i++){
                Button button;
                if (i == 0) button = laserPointerButton;
                else if (i == 1) button = hardCassetteButton;
                else if (i == 2) {
                    button = nightmareCandyButton;
                    fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha / 2, false);
                } else if (i == 3){
                    button = shadowChallengeButton;
                    fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
                } else if (i == 4) button = freeScrollButton;
                else if (i == 5) button = infiniteNightButton;
                else {
                    button = hellCastButton;
                    fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha / 2, false);
                }
                layout.setText(Candys3Deluxe.candysFont, button.getPath());
                Candys3Deluxe.candysFont.draw(batch, layout, button.getX() + 100, button.getY() + 55);

                if (fileIndex != 1 && button.isSelected()) fileIndex = 1;
                else if (fileIndex != 0 && !button.isSelected()) fileIndex = 0;
                region = ImageManager.getRegion("menu/option", 84, fileIndex);
                if (i == 2 || i == 6) challengeButtonColor(batch, false, false, optionWindowAlpha / 2);
                else challengeButtonColor(batch, button.isHovered(), button.isSelected(), optionWindowAlpha);
                batch.draw(region, button.getX(), button.getY());
                batch.setColor(1, 1, 1, 1);
            }
        }

        caption.render(batch);

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
