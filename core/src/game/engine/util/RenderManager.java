package game.engine.util;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import game.engine.Candys3Deluxe;

public class RenderManager {
    private final SpriteBatch batch;
    private final FontManager fontManager;
    private final ImageManager imageManager;
    private final VideoManager videoManager;
    private final FrameBufferManager frameBufferManager;
    private final CameraManager cameraManager;
    private InputManager inputManager;

    public RenderManager(int width, int height){
        batch = new SpriteBatch();
        fontManager = new FontManager();
        imageManager = new ImageManager();
        videoManager = new VideoManager();
        frameBufferManager = new FrameBufferManager();
        frameBufferManager.createShapes(batch, imageManager);
        cameraManager = new CameraManager(width, height);
    }

    public void requests(SoundManager soundManager, Request request){
        if (request.imagesIsEmpty() && request.soundsIsEmpty()) return;
        long time = System.currentTimeMillis();

        while (soundManager.loadingSounds(request)){

        }

        while (imageManager.loadingTextures(request)){

        }
        System.out.println("Time: " + (System.currentTimeMillis() - time) + "ms");
    }

    public void viewportAdjust(){
        Vector3 v3 = cameraManager.viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        inputManager.x = v3.x - cameraManager.getX();
        inputManager.y = v3.y - cameraManager.getY();
        inputManager.readjust();

        inputManager.pressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !inputManager.clickLock;
        inputManager.clickLock = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
    }

    public void render(Engine engine){
        ScreenUtils.clear(0, 0, 0, 1);

        if (engine.getStateManager().getGameState() == 0){
            engine.getStateManager().getMenu().render(this);
        } else {
            engine.getStateManager().getGame().render(this);
        }

        videoManager.updateRender(batch, Candys3Deluxe.width, Candys3Deluxe.height);
        batch.end();
    }

    public void restoreColor(SpriteBatch batch){
        batch.setColor(1, 1, 1, 1);
    }

    public void dispose(){
        batch.dispose();
        fontManager.dispose();
        imageManager.dispose();
        videoManager.dispose();
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public VideoManager getVideoManager() {
        return videoManager;
    }

    public FrameBufferManager getFrameBufferManager() {
        return frameBufferManager;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public void setInputManager(InputManager inputManager) {
        this.inputManager = inputManager;
    }
}
