package game.engine.util;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class RenderManager {
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final FontManager fontManager;
    private final SpriteManager spriteManager;
    private final TextureManager textureManager;
    private final VideoManager videoManager;
    private final FrameBufferManager frameBufferManager;
    private final CameraManager cameraManager;
    private final InputManager inputManager;
    private final GlyphLayout layout;

    private final short width;
    private final short height;

    public RenderManager(short width, short height){
        this.width = width;
        this.height = height;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fontManager = new FontManager();
        spriteManager = new SpriteManager();
        textureManager = new TextureManager();
        videoManager = new VideoManager();
        frameBufferManager = new FrameBufferManager();
        frameBufferManager.createShape(batch, shapeRenderer);
        cameraManager = new CameraManager(width, height);
        inputManager = new InputManager();
        layout = new GlyphLayout();
    }

    public void requests(Engine engine){
        while(engine.getSpriteRequest() != null){
            spriteManager.create(engine.getSpriteRequest());
            engine.setSpriteRequest(engine.getSpriteRequest().getNext());
        }

        while (engine.getTextureRequest() != null){
            textureManager.create(engine.getTextureRequest());
            engine.setTextureRequest(engine.getTextureRequest().getNext());
        }
    }

    public void viewportAdjust(Engine engine){
        Vector3 v3 = cameraManager.viewport.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        inputManager.x = v3.x - (float) width / 2;
        inputManager.y = v3.y - (float) height / 2;
        inputManager.readjust();

        if (!engine.isPressed() && !inputManager.pressed) {
            engine.setPressed(Gdx.input.isButtonPressed(Input.Buttons.LEFT));
        }
        inputManager.pressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
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
        shapeRenderer.dispose();
        fontManager.dispose();
        spriteManager.dispose();
        videoManager.dispose();
        frameBufferManager.dispose();
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

    public TextureManager getTextureManager() {
        return textureManager;
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

    public GlyphLayout getLayout() {
        return layout;
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}
