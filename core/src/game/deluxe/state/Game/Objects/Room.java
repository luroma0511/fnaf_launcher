package game.deluxe.state.Game.Objects;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import game.deluxe.state.Game.Objects.Character.Characters;
import game.engine.util.CameraManager;
import game.engine.util.FrameBufferManager;
import game.engine.util.ImageManager;
import game.engine.util.RenderManager;
import game.engine.util.SpriteObject;

public class Room extends SpriteObject {
    private byte state;
    private float frame;

    public Room(){
        super();
    }

    public void reset(){
        state = 0;
        frame = 0;
    }

    public void update(){

    }

    public void render(RenderManager renderManager, Characters characters, Flashlight flashlight){
        SpriteBatch batch = renderManager.getBatch();
        ImageManager imageManager = renderManager.getImageManager();
        FrameBufferManager frameBufferManager = renderManager.getFrameBufferManager();
        CameraManager cameraManager = renderManager.getCameraManager();
        TextureRegion region;
        if ((int) frame == 0) {
            if (state != 2){
                frameBufferManager.begin();

                if (state == 0){
                    batch.setColor(0, 0, 0, 1);
                    batch.draw(frameBufferManager.getTexture(), cameraManager.getX(), cameraManager.getY());
                    renderManager.restoreColor(batch);
                    batch.draw(imageManager.get("game/room/FullRoomEffect"), 0, 0);
                    region = imageManager.get("game/Flashlight");
                    batch.draw(region,
                            flashlight.getX() - (float) region.getRegionWidth() / 2,
                            flashlight.getY() - (float) region.getRegionHeight() / 2);
                }

                frameBufferManager.end(batch);
                batch.begin();
            }

            if (state == 0) {
                region = imageManager.getRegion("game/room/Room", 3072, 0);
            } else if (state == 1){
                region = imageManager.get("game/room/UnderBed");
            } else {
                region = imageManager.get("game/Tape/Tape");
            }
        } else {
            if (state == 0) {
                region = imageManager.get("game/Moving/Turn Around/Moving" + (int) frame);
            } else if (state == 1){
                region = imageManager.get("game/Moving/Under Bed/Moving" + (int) frame);
            } else {
                region = imageManager.get("game/Moving/Turn Back/Moving" + (int) frame);
            }
        }

        batch.draw(region, 0, 0);
        characters.render(batch, imageManager);

        if (state != 2 && (int) frame == 0) {
            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            //DON'T CHANGE THIS!!!
            batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_SRC_COLOR);
            region = new TextureRegion(frameBufferManager.getTexture());
            region.flip(false, true);
            batch.draw(region, cameraManager.getX(), cameraManager.getY());
            batch.setBlendFunction(srcFunc, dstFunc);
        }
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public float getFrame() {
        return frame;
    }

    public void setFrame(float frame) {
        this.frame = frame;
    }
}
