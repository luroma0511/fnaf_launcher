package title;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import core.Engine;
import util.CameraManager;
import util.FontManager;
import util.SoundHandler;
import util.Time;
import util.ui.Button;

public class TitleScreen {
    private final ShadowCat shadowCat = new ShadowCat();

    private final Button gamejoltButton;
    private final Button optionsButton;
    private final Button discordButton;
    private final Button backButton;

    private final Button candys1Button;
    private final Button candys2Button;
    private final Button candys3Button;

    private float xPosition;
    private boolean settings;
    private String settingsID = "";

    private final Color purple = new Color(185f/255, 169f/255, 236f/255, 1);
    private final Color yellow = new Color(85f/255, 85f/255, 33f/255, 1);
    private final Color blue = new Color(158f/255, 183f/255, 242f/255, 1);
    private final Color red = new Color(223f/255, 74f/255, 151f/255, 1);

    public TitleScreen(Engine engine){
        var window = engine.appHandler.window;
        int x = window.width() - 102;
        backButton = new Button("", 20, 20, 82, null);
        optionsButton = new Button("", x, 20, 82, null);
        x += 10;
        int y = window.height() - 92;
        gamejoltButton = new Button("", x, y, 72, null);
        x -= 92;
        discordButton = new Button("", x, y, 72, null);

        x = 54;
        y = 285;
        int w = 250;
        int h = 50;

        candys1Button = new Button("", x, y, w, h, null);
        y -= 70;
        candys2Button = new Button("", x, y, w, h, null);
        y -= 70;
        candys3Button = new Button("", x, y, w, h, null);

        load(engine);
    }

    public void load(Engine engine){
        xPosition = 0;
        var textureHandler = engine.appHandler.getTextureHandler();
        textureHandler.add("titleScreen/gamejolt");
        textureHandler.add("titleScreen/discord");
        shadowCat.load(textureHandler);
        engine.appHandler.soundHandler.load("assets", "titleMenu");
        engine.discord.setDetails("Title Screen");
        engine.discord.setState("");
    }

    public void update(Engine engine){
        if (engine.isOnline()) {
            engine.gamejoltTrophyUI.update(engine.game, engine.appHandler.soundHandler, engine.gamejoltManager);
        }

        var soundHandler = engine.appHandler.soundHandler;

        if (!soundHandler.isPlaying("titleMenu")){
            soundHandler.play("titleMenu");
            soundHandler.setSoundEffect(SoundHandler.LOOP, "titleMenu", 1);
        }

        float time = 25 * Time.getDelta() * 30;

        if (candys1Button.update(null, engine.appHandler.getInput(), true, true) && !settings){
            System.out.println("Test");
        }
        if (candys2Button.update(null, engine.appHandler.getInput(), true, true) && !settings){
            engine.game = "candys2";
            engine.discord.setDetails("Candy's 2: In Menu");
            engine.discord.setState("");
            engine.candys2Deluxe.menu.load(engine, true);
            soundHandler.stop("titleMenu");
        }
        if (candys3Button.update(null, engine.appHandler.getInput(), true, true) && !settings){
            engine.game = "candys3";
            engine.discord.setDetails("Candy's 3: In Menu");
            engine.discord.setState("");
            engine.candys3Deluxe.getMenu().load(engine, true);
            soundHandler.stop("titleMenu");
        }

        if (!settings) {
            if (time != 0) xPosition += (0 - xPosition) / time;

            if (optionsButton.update(null, engine.appHandler.getInput(), true)) {
                settingsID = "Options";
                settings = true;
            }
            if (gamejoltButton.update(null, engine.appHandler.getInput(), true)) {
                settingsID = "Gamejolt";
                settings = true;
            }
            if (discordButton.update(null, engine.appHandler.getInput(), true)) {
                System.out.println("Discord");
                if (discordButton.isSelected()) engine.discord.start("1126361619094589491");
                else engine.discord.end();
            }
        } else {
            int target = 1440;
            if (time != 0) xPosition += (target - xPosition) / time;

            if (backButton.update(null, engine.appHandler.getInput(), true)) {
                settings = false;
            }
        }

        shadowCat.update();
    }

    public void render(Engine engine){
        ScreenUtils.clear(0, 0, 0, 1);
        if (engine.appHandler.getRenderHandler().lock) return;

        var renderHandler = engine.appHandler.getRenderHandler();
        var textureHandler = engine.appHandler.getTextureHandler();
        var window = engine.appHandler.window;
        var batch = renderHandler.batch;

        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        renderHandler.shapeDrawer.update();
        batch.enableBlending();
        renderHandler.batchBegin();

        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();

        shadowCat.render(batch, engine.appHandler.getTextureHandler(), window, xPosition);

        var region = textureHandler.get("titleScreen/gamejolt");
        textureHandler.setFilter(region.getTexture());
        batch.draw(region,
                window.width() - 20 - region.getRegionWidth() - xPosition,
                window.height() - 20 - region.getRegionHeight());

        region = textureHandler.get("titleScreen/discord");
        textureHandler.setFilter(region.getTexture());
        batch.draw(region,
                window.width() - 40 - region.getRegionWidth() * 2 - xPosition,
                window.height() - 20 - region.getRegionHeight());

        region = engine.appHandler.getMenuUI().options;
        textureHandler.setFilter(region.getTexture());
        batch.setColor(purple);
        batch.draw(region, window.width() - 20 - region.getRegionWidth() - xPosition, 20);
        batch.setColor(1, 1, 1, 1);

        region = engine.appHandler.getMenuUI().back;
        textureHandler.setFilter(region.getTexture());
        batch.setColor(purple);
        batch.draw(region, 20 + 1440 - xPosition, 20);
        batch.setColor(1, 1, 1, 1);

        fontRender(batch, engine.appHandler.getFontManager(), engine.user.getUsername(), engine.version);

        renderHandler.batchEnd();
    }

    private void fontRender(SpriteBatch batch, FontManager fontManager, String username, String version){

        var font = fontManager.getFont("font");
        fontManager.setCurrentFont(font);
        font.setColor(purple);
        fontManager.setSize(56);

        fontManager.setText("Candy's\nCustom Night\nJava Edition");
        fontManager.setRelativePosition(57 - xPosition, 629);
        fontManager.setShadowColor(purple);
        fontManager.setShadowSmoothing(0.5f);
        fontManager.setTextureSize(56);
        fontManager.setCurrentShader("shadow");
        fontManager.render(batch);
        fontManager.resetShadow();
        fontManager.setCurrentShader("outline");
        fontManager.setRelativePosition(56 - xPosition, 630);
        fontManager.render(batch);

        fontManager.setSize(18);
        fontManager.setText("Logged in as: " + username);
        fontManager.setRelativePosition(20.75f - xPosition, 699.25f);
        fontManager.setShadowColor(purple);
        fontManager.setShadowSmoothing(0.5f);
        fontManager.setTextureSize(32);
        fontManager.setCurrentShader("shadow");
        fontManager.render(batch);
        fontManager.resetShadow();
        fontManager.setCurrentShader("outline");
        fontManager.setRelativePosition(20 - xPosition, 700);
        fontManager.render(batch);

        fontManager.setText("Discord Rich Presence: " + (discordButton.isSelected() ? "On" : "Off"));
        fontManager.setRelativePosition(20.75f - xPosition, 674.25f);
        fontManager.setShadowColor(purple);
        fontManager.setShadowSmoothing(0.5f);
        fontManager.setTextureSize(32);
        fontManager.setCurrentShader("shadow");
        fontManager.render(batch);
        fontManager.resetShadow();
        fontManager.setCurrentShader("outline");
        fontManager.setRelativePosition(20 - xPosition, 675);
        fontManager.render(batch);

        fontManager.setText(version
                + "\nMod by: Official_LR"
                + "\nOriginal games by: Emil 'Ace' Macko");
        fontManager.setRelativePosition(20.75f - xPosition, 69.25f);
        fontManager.setShadowColor(purple);
        fontManager.setShadowSmoothing(0.5f);
        fontManager.setTextureSize(32);
        fontManager.setCurrentShader("shadow");
        fontManager.render(batch);
        fontManager.resetShadow();
        fontManager.setCurrentShader("outline");
        fontManager.setRelativePosition(20 - xPosition, 70);
        fontManager.render(batch);

        font.setColor(yellow);
        float offset = candys1Button.getAlpha() * 30;
        fontManager.setSize(40);
        fontManager.setText("Candy's 1");
        fontManager.setRelativePosition(56 - xPosition + offset, 330);
        fontManager.setShadowColor(yellow);
        fontManager.setShadowSmoothing(0.25f);
        fontManager.setTextureSize(48);
        fontManager.setCurrentShader("shadow");
        fontManager.render(batch);
        fontManager.resetShadow();
        fontManager.setCurrentShader("outline");
        font.setColor(0.35f, 0.35f, 0.35f, 1);
        fontManager.setText("Candy's 1");
        fontManager.setRelativePosition(56 - xPosition + offset, 330);
        fontManager.render(batch);

        font.setColor(blue);
        offset = candys2Button.getAlpha() * 30;
        fontManager.setSize(40);
        fontManager.setText("Candy's 2");
        fontManager.setRelativePosition(57 - xPosition + offset, 259);
        fontManager.setShadowColor(blue);
        fontManager.setShadowSmoothing(0.5f);
        fontManager.setTextureSize(48);
        fontManager.setCurrentShader("shadow");
        fontManager.render(batch);
        fontManager.resetShadow();
        fontManager.setCurrentShader("outline");
        fontManager.setRelativePosition(56 - xPosition + offset, 260);
        fontManager.render(batch);

        font.setColor(red);
        offset = candys3Button.getAlpha() * 30;
        fontManager.setSize(40);
        fontManager.setText("Candy's 3");
        fontManager.setRelativePosition(57 - xPosition + offset, 189);
        fontManager.setShadowColor(red);
        fontManager.setShadowSmoothing(0.5f);
        fontManager.setTextureSize(48);
        fontManager.setCurrentShader("shadow");
        fontManager.render(batch);
        fontManager.resetShadow();
        fontManager.setCurrentShader("outline");
        fontManager.setRelativePosition(56 - xPosition + offset, 190);
        fontManager.render(batch);
    }
}
