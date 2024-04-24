package state.Game.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import deluxe.GameData;
import state.Game.Game;
import state.Game.Objects.Character.Characters;
import util.*;

public class Room extends SpriteObject {
    private byte state;
    private float frame;
    private boolean framePositive;
    private int boundsX;
    private int boundsY;
    private int limit;
    public boolean hoverButton;
    private boolean bed;
    private boolean tape;

    private float playFrame;
    private float stopFrame;
    private float rewindFrame;
    private byte tapeState;
    private boolean tapeStop;
    private float tapeFrame;
    private float tapeVolume;

    Music tapeMusic = Gdx.audio.newMusic(Gdx.files.local("assets/sounds/tapemusic.wav"));

    Pixmap button1;
    Pixmap button2;
    Pixmap play;
    Pixmap stop;
    Pixmap rewind;

    public Room(){
        super();
    }

    public void reset(){
        state = 0;
        frame = 0;
        limit = 10;
        boundsX = 1792;
        boundsY = 304;
        bed = false;
        tape = false;
        framePositive = false;
        playFrame = 0;
        stopFrame = 0;
        rewindFrame = 0;
        tapeState = 0;
        tapeFrame = 0;
        tapeStop = false;
        if (tapeMusic.isPlaying()) tapeMusic.stop();
    }

    public void load(){
        if (button1 == null) button1 = ImageManager.loadImageBuffer("game/Buttons/TapePlayer");
        if (button2 == null) button2 = ImageManager.loadImageBuffer("game/Buttons/UnderBed");
        if (play == null) play = ImageManager.loadImageBuffer("game/Tape/PlayButton");
        if (stop == null) stop = ImageManager.loadImageBuffer("game/Tape/StopButton");
        if (rewind == null) rewind = ImageManager.loadImageBuffer("game/Tape/RewindButton");
    }

    public void input(float mx, float my, Player player, boolean clicked){
        if (frame > 0) return;
        hoverButton = false;

        if (!framePositive && !player.isFreeze() && clicked){
            if (state == 0){
                if (button1 != null && ImageManager.isAlpha(button1, (int) mx - 128, (int) (128 - my))){
                    hoverButton = true;
                    tape = true;
                } else if (button2 != null && ImageManager.isAlpha(button2, (int) mx - 732, (int) (128 - my))){
                    hoverButton = true;
                    bed = true;
                }
            } else if (state == 1) hoverButton = button1 != null && ImageManager.isAlpha(button1, (int) mx - 430, (int) (128 - my));
            else {
                hoverButton = button2 != null && ImageManager.isAlpha(button2, (int) mx - 430, (int) (128 - my));
                if (tapeState == 0 && ImageManager.isAlpha(play, (int) ((mx - 541) / 1.25f), (int) ((326.25f - my) / 1.25f))){
                    SoundManager.play("tapeButton");
                    tapeMusic.play();
                    tapeMusic.setPosition(tapeFrame);
                    tapeState = 1;
                    stopFrame = 0;
                    playFrame = 0;
                    rewindFrame = 0;
                    tapeStop = false;
                } else if (tapeState != 0 && ImageManager.isAlpha(stop, (int) ((mx - 690) / 1.25f), (int) ((360 - my) / 1.25f))){
                    SoundManager.play("tapeStop");
                    SoundManager.stop("tapeRewind");
                    if (tapeMusic.isPlaying()) {
                        tapeFrame = tapeMusic.getPosition();
                        tapeMusic.stop();
                    }
                    if (tapeState == 1){
                        playFrame = 1;
                        rewindFrame = 0;
                    } else {
                        rewindFrame = 1;
                        playFrame = 0;
                    }
                    stopFrame = 0;
                    tapeState = 0;
                    tapeStop = true;
                } else if (tapeState == 0 && ImageManager.isAlpha(rewind, (int) ((mx - 763) / 1.25f), (int) ((376.25f - my) / 1.25f))){
                    SoundManager.play("tapeButton");
                    if (tapeFrame != 0 && !GameData.hardCassette) {
                        SoundManager.play("tapeRewind");
                        SoundManager.setLoop("tapeRewind", true);
                    }
                    tapeState = 2;
                    stopFrame = 0;
                    playFrame = 0;
                    rewindFrame = 0;
                    tapeStop = false;
                }
            }

            if (hoverButton) {
                SoundManager.play("flashlight");
                if (state == 0) SoundManager.play("movebed1");
                else SoundManager.play("movebed2");
                if (state == 1) SoundManager.play("lookbed1");
                framePositive = true;
                frame = 1;
                hoverButton = false;
            }
        }
    }

    public void update(Player player){
        if (framePositive) frame = Time.increaseTimeValue(frame, limit + 1, 26);
        else if (frame > 0) {
            frame = Time.decreaseTimeValue(frame, 0, 26);
            if (frame == 0) SoundManager.play("flashlight");
        }

        if ((int) frame == limit + 1 && framePositive){
            if (bed){
                state = 1;
                SoundManager.play("lookbed2");
                player.setX(player.getX() / 2.34f);
                bed = false;
            } else if (tape){
                state = 2;
                tape = false;
            } else {
                if (state == 1) {
                    if (player.isBedSpot()) player.setBedSpot();
                    player.setX(player.getX() * 2.33f);
                }
                state = 0;
            }

            if (state == 2) limit++;
            else if (limit != 10) limit--;
            frame = limit + 0.99f;
            framePositive = false;
            switch (state){
                case 0:
                    boundsX = 1792;
                    boundsY = 304;
                    break;
                case 1:
                    boundsX = 768;
                    boundsY = 0;
                    break;
            }
        }

        if (state == 2) tapeVolume = Time.increaseTimeValue(tapeVolume, 0.1f, 0.2f);
        else tapeVolume = Time.decreaseTimeValue(tapeVolume, 0, 0.2f);

        if (tapeState == 0 && tapeStop && (playFrame > 0 || rewindFrame > 0)){
            stopFrame = Time.increaseTimeValue(stopFrame, 1.99f, 20);
        } else stopFrame = Time.decreaseTimeValue(stopFrame, 0, 20);

        if (tapeState == 1) playFrame = Time.increaseTimeValue(playFrame, 2, 20);
        else if (playFrame > 0){
            playFrame = Time.decreaseTimeValue(playFrame, 0, 20);
            if (playFrame == 0 && tapeStop) stopFrame = 1.99f;
        }

        if (tapeState == 2) rewindFrame = Time.increaseTimeValue(rewindFrame, 2, 20);
        else if (rewindFrame > 0){
            rewindFrame = Time.decreaseTimeValue(rewindFrame, 0, 20);
            if (rewindFrame == 0 && tapeStop) stopFrame = 1.99f;
        }

        if (tapeMusic.isPlaying()) tapeFrame = tapeMusic.getPosition();
        if (tapeState == 1 && !tapeMusic.isPlaying()){
            if (playFrame == 2) {
                SoundManager.play("tapeButton");
                tapeState = 0;
            }
        } else if (tapeState == 2){
            tapeFrame = Time.decreaseTimeValue(tapeFrame, 0, 3);
            if (tapeFrame == 0 && rewindFrame == 2){
                SoundManager.play("tapeButton");
                SoundManager.stop("tapeRewind");
                tapeState = 0;
            }
        }

        if (GameData.hardCassette) tapeMusic.setVolume(0);
        else if (tapeMusic.isPlaying()) tapeMusic.setVolume(0.065f + tapeVolume);
    }

    public void render(SpriteBatch batch, Characters characters, Room room, Flashlight flashlight){
        TextureRegion region;
        if ((int) frame == 0) {
            if (state != 2){
                if (Game.lightBuffer == null) Game.lightBuffer = FrameBufferManager.newFrameBuffer();
                Game.lightBuffer.begin();
                RenderManager.shapeDrawer.setColor(0, 0, 0, 1);
                RenderManager.shapeDrawer.filledRectangle(CameraManager.getX(), CameraManager.getY(), 1280, 720);
                if (state == 0) batch.draw(ImageManager.get("game/room/FullRoomEffect"), 0, 0);
                else batch.draw(ImageManager.get("game/room/UnderBedEffect"), 0, 0);
                region = ImageManager.get("game/Flashlight");

                float multiplier = 1;
                if (GameData.hitboxMultiplier != 1) multiplier = 1.75f;

                float width = (float) region.getRegionWidth() / multiplier;
                float height = (float) region.getRegionHeight() / multiplier;

                batch.draw(region,
                        flashlight.getX() - width / 2,
                        flashlight.getY() - height / 2,
                        width,
                        height);

                FrameBufferManager.end(batch, Game.lightBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }

            if (state == 0) region = ImageManager.getRegion("game/room/Room", 3072, 0);
            else if (state == 1) region = ImageManager.get("game/room/UnderBed");
            else region = ImageManager.get("game/Tape/Tape");
        } else {
            if (state == 0) region = ImageManager.get("game/Moving/Turn Around/Moving" + (int) frame);
            else if (state == 1) region = ImageManager.get("game/Moving/Under Bed/Moving" + (int) frame);
            else region = ImageManager.get("game/Moving/Turn Back/Moving" + (int) frame);
        }

        if (Game.roomBuffer == null) Game.roomBuffer = FrameBufferManager.newFrameBuffer();
        Game.roomBuffer.begin();
        if (state != 2) batch.draw(region, 0, 0);
        else {
            batch.draw(region, CameraManager.getX(), CameraManager.getY());
            if ((int) frame == 0) {
                int spriteRegion = getTapeRegion();
                region = ImageManager.getRegion("game/Tape/Buttons", 394, spriteRegion);
                batch.draw(region, CameraManager.getX() + 416, CameraManager.getY() + 126);
            }
        }
        characters.render(batch, room);
        if (state == 0 && (int) frame == 0) {
            region = ImageManager.getRegion("game/room/Room", 3072, 1);
            batch.draw(region, 0, 0);
        }
        characters.renderForward(batch, room);

        FrameBufferManager.end(batch, Game.roomBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        RenderManager.screenBuffer.begin();

        if (state != 2 && (int) frame == 0) {
            FrameBufferManager.render(batch, Game.lightBuffer, false);
            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            //DON'T CHANGE THIS!!!
            batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR);
            FrameBufferManager.render(batch, Game.roomBuffer, false);
            batch.setBlendFunction(srcFunc, dstFunc);
            batch.flush();
        } else {
            if (state == 2) FrameBufferManager.render(batch, Game.roomBuffer, false, 1024, 576);
            else FrameBufferManager.render(batch, Game.roomBuffer, false);
        }
    }

    public void dispose(){
        if (play != null) play.dispose();
        if (stop != null) stop.dispose();
        if (rewind != null) rewind.dispose();
        if (button1 != null) button1.dispose();
        if (button2 != null) button2.dispose();
    }

    public int getTapeRegion(){
        byte region = -1;
        if (playFrame > 0){
            if (stopFrame == 0) region = (byte) playFrame;
            else region = 6;
        } else if (rewindFrame > 0){
            if (stopFrame == 0) region = (byte) (rewindFrame + 3);
            else region = 7;
        } else if (stopFrame > 0) region = (byte) (9 - (int) stopFrame);
        return region;
    }

    public void stopMusic(){
        tapeMusic.stop();
    }

    public boolean isMusicPlaying(){
        return tapeMusic.isPlaying();
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public int getFrame() {
        return (int) frame;
    }

    public void setFrame(float frame) {
        this.frame = frame;
    }

    public int getBoundsX() {
        return boundsX;
    }

    public int getBoundsY() {
        return boundsY;
    }
}
