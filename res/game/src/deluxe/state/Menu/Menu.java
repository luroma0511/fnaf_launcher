package deluxe.state.Menu;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import deluxe.data.Challenges;
import deluxe.data.GameData;
import deluxe.state.Menu.Objects.Button;
import deluxe.state.Menu.Objects.Caption;
import deluxe.state.Menu.Objects.MenuCharacter;
import deluxe.Candys3Deluxe;
import util.*;

public class Menu {
    private final MenuCharacter rat;
    private final MenuCharacter cat;
    private final Button optionButton;
    private final Button playButton;
    private final Button laserPointerButton;
    private final Button hardCassetteButton;
    private final Button nightmareCandyButton;
    private final Button shadowChallengeButton;
    private final Caption caption;

    private boolean shadow;
    private float thunderAlpha;
    private float staticAnimation;
    private float optionWindowAlpha;

    private boolean playMenu;
    public boolean menuLoaded;
    private float menuPitch;

    public Menu(){
        rat = new MenuCharacter(null, 40, 44, 632, 632, 0);
        cat = new MenuCharacter(null, 692, 44, 428, 632,0);
        optionButton = new Button("OPTIONS", 365, 24, 200, 100, 0);
        playButton = new Button("PLAY", 715, 24, 200, 100, 0);
        laserPointerButton = new Button("Laser Pointer", 236, 470, 84, 84, 0);
        hardCassetteButton = new Button("Hard Cassette", 236, 372, 84, 84, 0);
        nightmareCandyButton = new Button("Nightmare Candy", 236, 274, 84, 84, 0);
        shadowChallengeButton = new Button("Shadow Challenge", 236, 176, 84, 84, 0);
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
        rat.update(caption, inputManager, focus, (short) 1);
        cat.update(caption, inputManager, focus, (short) 2);
        caption.update();

        optionButton.update(inputManager, true);
        if (optionButton.isSelected()) {
            optionWindowAlpha = Time.increaseTimeValue(optionWindowAlpha, 1, 4);
            laserPointerButton.update(inputManager, false);
            Challenges.laserPointer = laserPointerButton.isSelected();
            hardCassetteButton.update(inputManager, false);
            Challenges.hardCassette = hardCassetteButton.isSelected();
//            nightmareCandyButton.update(inputManager, false);
//            Challenges.nightmareCandy = nightmareCandyButton.isSelected();
            shadowChallengeButton.update(inputManager, false);
        }
        else if ((shadowChallengeButton.isSelected() && !shadow) || (!shadowChallengeButton.isSelected() && shadow)) {
            shadow = !shadow;
            optionWindowAlpha = 0;
            thunderAlpha = 1;
            SoundManager.play("thunder");
        } else optionWindowAlpha = Time.decreaseTimeValue(optionWindowAlpha, 0, 4);

        playButton.update(inputManager, true);
        if (!playButton.isSelected()) return;
        playButton.setSelected(false);
        if (optionWindowAlpha > 0) return;
        if (shadow) gameData.writeData((byte) 1, (byte) 0, (byte) 0, (byte) 0, rat.getAi(), cat.getAi());
        else gameData.writeData((byte) 0, rat.getAi(), cat.getAi(), (byte) 0, (byte) 0, (byte) 0);
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
        if (shadow) region = ImageManager.get("menu/shadow_rat");
        else region = ImageManager.get("menu/rat");
        batch.draw(region, rat.getX(), rat.getY());
        if (shadow) region = ImageManager.get("menu/shadow_cat");
        else region = ImageManager.get("menu/cat");
        batch.draw(region, cat.getX(), cat.getY());

        batch.flush();
        batch.setBlendFunction(srcFunc, dstFunc);

        fontAlpha(Candys3Deluxe.aiFont, 1, true);
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
            for (byte i = 0; i < 4; i++){
                Button button;
                if (i == 0) button = laserPointerButton;
                else if (i == 1) button = hardCassetteButton;
                else if (i == 2) {
                    button = nightmareCandyButton;
                    fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha / 2, false);
                } else {
                    button = shadowChallengeButton;
                    fontAlpha(Candys3Deluxe.candysFont, optionWindowAlpha, false);
                }
                layout.setText(Candys3Deluxe.candysFont, button.getPath());
                Candys3Deluxe.candysFont.draw(batch, layout, 336, 525 - 98 * i);

                if (fileIndex != 1 && button.isSelected()) fileIndex = 1;
                else if (fileIndex != 0 && !button.isSelected()) fileIndex = 0;
                region = ImageManager.getRegion("menu/option", 84, fileIndex);
                if (i == 2) challengeButtonColor(batch, button, optionWindowAlpha / 2);
                else challengeButtonColor(batch, button, optionWindowAlpha);
                batch.draw(region, 236, 470 - 98 * i);
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

    private void challengeButtonColor(SpriteBatch batch, Button button, float alpha){
        if (button.isHovered()) batch.setColor(1, 1, 1, alpha);
        else if (shadow){
            if (button.isSelected()) batch.setColor(0.85f, 0.7f, 1, alpha);
            else batch.setColor(0.5f, 0, 1, alpha);
        } else {
            if (button.isSelected()) batch.setColor(1, 0.7f, 0.7f, alpha);
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
