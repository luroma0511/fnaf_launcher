package candys2.Win;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.*;

public class Win {
    public final FrameBuffer lastGameFrameBuffer = FrameBufferManager.newFrameBuffer();
    private int beepInterval;
    private float beepMS;
    private int beepTimes;
    private float winAlpha;
    private boolean displayGame;
    private float width;
    private boolean newspaper;
    private boolean skipNewspaper;
    private boolean clicked;
    private float newspaperAlpha;
    private boolean play6AM;
    private float staticPosition;
    private float staticDelay;

    public void reset(boolean skipNewspaper){
        this.skipNewspaper = skipNewspaper;
        play6AM = true;
        beepTimes = 6;
        beepMS = 0;
        beepInterval = 0;
        winAlpha = 0;
        displayGame = true;
        width = 300;
        newspaper = false;
        newspaperAlpha = 0;
        clicked = false;
        staticPosition = 0;
    }

    public void update(Engine engine){
        var soundHandler = engine.appHandler.soundHandler;
        if (width > 0){
            width -= Time.getDelta() * 100;
            if (width <= 0) width = 0;
        }

        if (!newspaper) {
            if (beepTimes > 1 && winAlpha < 1) {
                winAlpha += Time.getDelta() * 1.25f;
                if (winAlpha >= 1) {
                    winAlpha = 1;
                    displayGame = false;
                }
            } else if (beepTimes <= 1) {
                winAlpha -= Time.getDelta() * 1.25f;
                if (winAlpha <= 0) {
                    winAlpha = 0;
                    engine.appHandler.getTextureHandler().dispose();
                    if (skipNewspaper){
                        engine.appHandler.getRenderHandler().screenAlpha = 0;
                        engine.candys2Deluxe.setState(0);
                        return;
                    }
                    newspaper = true;
                    engine.appHandler.getTextureHandler().add("game/newspaper");
                    engine.appHandler.getTextureHandler().add("static/ending");
                    soundHandler.add("ending");
                }
            }
            if (soundHandler.isPlaying("6am")) soundHandler.setSoundEffect(SoundHandler.VOLUME, "6am", beepTimes > 1 ? 1 : winAlpha);

            if (beepTimes == 0) return;
            beepMS += Time.getDelta();
            if (beepMS >= 0.08f) {
                beepMS -= 0.08f;
                beepInterval++;
                if (beepInterval == 12) {
                    beepInterval = 0;
                    beepTimes--;
                    if (beepTimes > 0) play6AM = true;
                }
            }
            if (play6AM) {
                soundHandler.play("6am");
                play6AM = false;
            }
            return;
        } else if (!soundHandler.isPlaying("ending")){
            soundHandler.play("ending");
            soundHandler.setSoundEffect(SoundHandler.VOLUME, "ending", 0.65f);
            soundHandler.setSoundEffect(SoundHandler.LOOP, "ending", 1);
        }
        if (staticDelay <= 0) {
            staticPosition = (float) (Math.random() * 1025);
            staticDelay += 0.1f;
        } else staticDelay -= Time.getDelta();
        if (engine.appHandler.getInput().isLeftPressed() && newspaperAlpha == 1) clicked = true;
        if (!clicked) {
            newspaperAlpha += Time.getDelta() * 1.5f;
            if (newspaperAlpha >= 1) newspaperAlpha = 1;
        } else {
            soundHandler.setSoundEffect(SoundHandler.VOLUME, "ending", 0.65f * newspaperAlpha);
            newspaperAlpha -= Time.getDelta() * 1.5f;
            if (newspaperAlpha > 0) return;
            newspaper = false;
            beepTimes = 6;
            soundHandler.stopAllSounds();
            engine.appHandler.getRenderHandler().screenAlpha = 0;
            engine.candys2Deluxe.setState(0);
            engine.appHandler.getTextureHandler().dispose();
        }
    }

    public void render(Engine engine){
        var textureHandler = engine.appHandler.getTextureHandler();
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var window = engine.appHandler.window;

        CameraManager.setOrigin();
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        batch.enableBlending();
        renderHandler.batchBegin();

        Runnable runnable = () -> {
            if (displayGame){
                var region = new TextureRegion(lastGameFrameBuffer.getColorBufferTexture());
                region.flip(false, true);
                batch.draw(region, 0, 0);
                renderHandler.shapeDrawer.setColor(0, 0, 0, winAlpha);
                renderHandler.drawScreen();
            }
        };

        if (renderHandler.lock) {
            runnable.run();
            return;
        }

        if (newspaper){
            renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
            renderHandler.drawScreen();

            batch.setColor(0.95f, 0.95f, 0.95f, 1);

            var region = textureHandler.get("static/ending");
            batch.draw(region, -staticPosition, 0, 2560, window.height());

            batch.setColor(1, 1, 1, 1);

            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            batch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_SRC_COLOR);
            region = textureHandler.getRegion("game/newspaper", 1024, 2);
            batch.draw(region, 0, 0, window.width(), window.height());
            batch.flush();
            batch.setBlendFunction(srcFunc, dstFunc);

            renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - newspaperAlpha);
            renderHandler.drawScreen();
            return;
        }
        var region = textureHandler.get("game/6am");
        float visible = (beepInterval % 2 == 0 && beepInterval < 8) ? 1 : 0;
        batch.setColor(1, 1, 1, visible);

        batch.draw(region, 440, 160);

        batch.setColor(1, 1, 1, 1);

        renderHandler.shapeDrawer.setColor(0, 0, 0, visible);
        renderHandler.shapeDrawer.filledRectangle(490 + (150 - width / 2), 260, width, 200);

        if (displayGame) {
            runnable.run();
            return;
        }
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1 - winAlpha);
        renderHandler.drawScreen();
    }
}
