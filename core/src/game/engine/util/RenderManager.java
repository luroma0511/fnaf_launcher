package game.engine.util;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class RenderManager {
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final FontManager fontManager;
    private final SpriteManager spriteManager;
    private final VideoManager videoManager;
    private final FrameBufferManager frameBufferManager;
    private final CameraManager cameraManager;

    private final short width;
    private final short height;

    public RenderManager(short width, short height){
        this.width = width;
        this.height = height;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fontManager = new FontManager();
        spriteManager = new SpriteManager();
        videoManager = new VideoManager();
        frameBufferManager = new FrameBufferManager();
        frameBufferManager.createShape(batch, shapeRenderer);
        cameraManager = new CameraManager(width, height);
    }

    public void requests(Engine engine){
        while(engine.getSpriteRequest() != null){
            spriteManager.create(engine.getSpriteRequest());
            engine.setSpriteRequest(engine.getSpriteRequest().getNext());
        }
    }

    public void render(Engine engine){
        ScreenUtils.clear(0, 0, 0, 1);
        batch.enableBlending();
        batch.begin();

        if (engine.getStateManager().getGameState() == 0){
            engine.getStateManager().getMenu().render(this);
        } else {
            engine.getStateManager().getGame().render(this);
        }

        videoManager.updateRender(batch);
        batch.end();
    }

    public void dispose(){
        batch.dispose();
        videoManager.dispose();
        spriteManager.dispose();
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public short getWidth() {
        return width;
    }

    public short getHeight() {
        return height;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public SpriteManager getSpriteManager() {
        return spriteManager;
    }

    public VideoManager getVideoManager() {
        return videoManager;
    }

    public FrameBufferManager getFrameBufferManager() {
        return frameBufferManager;
    }
}
