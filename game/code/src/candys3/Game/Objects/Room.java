package candys3.Game.Objects;

import candys3.GameData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.Engine;
import candys3.Game.Game;
import candys3.Game.Objects.Character.Characters;
import util.*;

public class Room extends SpriteObject {
    private byte state;
    private byte targetState;
    private float frame;
    private int boundsX;
    private int boundsY;
    private int limit;
    public boolean hoverButton;

    private float tapePlayAchievement;
    private boolean tapeWeasel;
    private float playFrame;
    private float stopFrame;
    private float rewindFrame;
    private byte tapeState;
    private boolean tapeStop;
    private float tapePos;
    private float tapeVolume;

    Music tapeMusic;

    Pixmap button1;
    Pixmap button2;
    Pixmap play;
    Pixmap stop;
    Pixmap rewind;

    public Room(){
        super();
    }

    public void reset(){
        if (tapeMusic == null) tapeMusic = Gdx.audio.newMusic(Gdx.files.local("assets/candys3/sounds/tapemusic.wav"));
        tapeWeasel = false;
        state = 0;
        targetState = 0;
        frame = 0;
        limit = 20;
        boundsX = 1792;
        boundsY = 304;
        playFrame = 0;
        stopFrame = 0;
        rewindFrame = 0;
        tapeState = 0;
        tapePos = 0;
        if (GameData.hardCassette) tapePlayAchievement = 0;
        else tapePlayAchievement = 1;
        tapeStop = false;
        if (tapeMusic.isPlaying()) tapeMusic.stop();
    }

    public void load(TextureHandler textureHandler){
        if (button1 == null) button1 = textureHandler.loadImageBuffer("assets/candys3", "game/Buttons/TapePlayer");
        if (button2 == null) button2 = textureHandler.loadImageBuffer("assets/candys3", "game/Buttons/UnderBed");
        if (play == null) play = textureHandler.loadImageBuffer("assets/candys3", "game/Tape/PlayButton");
        if (stop == null) stop = textureHandler.loadImageBuffer("assets/candys3", "game/Tape/StopButton");
        if (rewind == null) rewind = textureHandler.loadImageBuffer("assets/candys3", "game/Tape/RewindButton");
    }

    public void input(Engine engine, Player player){
        if (frame > 0) return;
        hoverButton = false;
        var mx = engine.appHandler.getInput().getX();
        var my = engine.appHandler.getInput().getY();
        var clicked = engine.appHandler.getInput().isLeftPressed();
        
        var textureHandler = engine.appHandler.getTextureHandler();
        var soundHandler = engine.appHandler.soundHandler;
        
        if ((int) frame == 0 && !player.isFreeze() && clicked){
            if (state == 0){
                if (button1 != null && textureHandler.isAlpha(button1, (int) mx - 128, (int) (128 - my))){
                    hoverButton = true;
                    targetState = 2;
                    limit = 21;
                } else if (button2 != null && textureHandler.isAlpha(button2, (int) mx - 732, (int) (128 - my))){
                    hoverButton = true;
                    targetState = 1;
                    limit = 20;
                }
            } else {
                if (state == 1 && button1 != null && textureHandler.isAlpha(button1, (int) mx - 430, (int) (128 - my))){
                    hoverButton = true;
                    targetState = 0;
                } else {
                    hoverButton = button2 != null && textureHandler.isAlpha(button2, (int) mx - 430, (int) (128 - my));
                    if (hoverButton) targetState = 0;
                    if (tapeState == 0 && textureHandler.isAlpha(play, (int) ((mx - 541) / 1.25f), (int) ((326.25f - my) / 1.25f))) {
                        soundHandler.play("tapeButton");
                        tapeMusic.play();
                        tapeMusic.setPosition(tapePos);
                        tapeState = 1;
                        stopFrame = 0;
                        playFrame = 0;
                        rewindFrame = 0;
                        tapeStop = false;
                    } else if (tapeState != 0 && textureHandler.isAlpha(stop, (int) ((mx - 690) / 1.25f), (int) ((360 - my) / 1.25f))) {
                        soundHandler.play("tapeStop");
                        soundHandler.stop("tapeRewind");
                        soundHandler.stop("tapeWeasel");
                        if (tapeMusic.isPlaying()) {
                            tapePos = tapeMusic.getPosition();
                            tapeMusic.stop();
                        }
                        if (tapeState == 1) {
                            playFrame = 1;
                            rewindFrame = 0;
                        } else {
                            rewindFrame = 1;
                            playFrame = 0;
                        }
                        stopFrame = 0;
                        tapeState = 0;
                        tapeStop = true;
                        tapeWeasel = false;
                    } else if (tapeState == 0 && textureHandler.isAlpha(rewind, (int) ((mx - 763) / 1.25f), (int) ((376.25f - my) / 1.25f))) {
                        soundHandler.play("tapeButton");
                        if (tapePos != 0 && !GameData.hardCassette) {
                            soundHandler.play("tapeRewind");
                            soundHandler.setSoundEffect(soundHandler.LOOP, "tapeRewind", 1);
                        }
                        tapeState = 2;
                        stopFrame = 0;
                        playFrame = 0;
                        rewindFrame = 0;
                        tapeStop = false;
                    }
                }
            }

            if (hoverButton) {
                soundHandler.play("flashlight");
                if (state == 0) soundHandler.play("movebed1");
                else soundHandler.play("movebed2");
                if (state == 1) soundHandler.play("lookbed1");
                frame = 1;
                hoverButton = false;
            }
        }
    }

    public void update(SoundHandler soundHandler, Player player){
        if (frame > 0) frame = Time.increaseTimeValue(frame, limit + 1, 27);
        if ((int) frame == limit + 1){
            frame = 0;
            soundHandler.play("flashlight");
        }

        if (((state == 2 && (int) frame == 12) || (state != 2 && (int) frame == 11)) && state != targetState){
            if (targetState == 1){
                soundHandler.play("lookbed2");
                player.setX(player.getX() / 2.34f);
            } else if (targetState == 0){
                if (state == 1) {
                    if (player.isBedSpot()) player.setBedSpot();
                    player.setX(player.getX() * 2.33f);
                }
            }
            state = targetState;
            if (state == 0) {
                boundsX = 1792;
                boundsY = 304;
            } else if (state == 1){
                boundsX = 768;
                boundsY = 0;
            }
        }

        if (tapeWeasel && tapeMusic.isPlaying()){
            tapeMusic.stop();
            soundHandler.play("tapeWeasel");
            soundHandler.setSoundEffect(Constants.LOOP, "tapeWeasel", 1);
        }

        if (state == 2) tapeVolume = Time.increaseTimeValue(tapeVolume, 0.1f, 0.2f);
        else tapeVolume = Time.decreaseTimeValue(tapeVolume, 0, 0.2f);

        if (tapeState == 2) rewindFrame = Time.increaseTimeValue(rewindFrame, 2, 20);
        else if (tapeState == 1) playFrame = Time.increaseTimeValue(playFrame, 2, 20);
        else {
            if (playFrame > 0){
                playFrame = Time.decreaseTimeValue(playFrame, 0, 20);
                if (playFrame == 0 && tapeStop) stopFrame = 1.99f;
            } else if (rewindFrame > 0){
                rewindFrame = Time.decreaseTimeValue(rewindFrame, 0, 20);
                if (rewindFrame == 0 && tapeStop) stopFrame = 1.99f;
            }
            if (tapeStop && (playFrame > 0 || rewindFrame > 0)) stopFrame = Time.increaseTimeValue(stopFrame, 1.99f, 20);
            else stopFrame = Time.decreaseTimeValue(stopFrame, 0, 20);
        }

        if (tapeState == 1) tapePlayAchievement = Time.increaseTimeValue(tapePlayAchievement, 1, 1);
        else if (tapePlayAchievement < 1) tapePlayAchievement = Time.decreaseTimeValue(tapePlayAchievement, 0, 1);

        if (tapeMusic.isPlaying()) tapePos = tapeMusic.getPosition();
        if (tapeState == 1 && !tapeMusic.isPlaying() && !tapeWeasel){
            if (playFrame == 2) {
                soundHandler.play("tapeButton");
                tapeState = 0;
            }
        } else if (tapeState == 2){
            tapePos = Time.decreaseTimeValue(tapePos, 0, 3);
            if (tapePos == 0 && rewindFrame == 2){
                soundHandler.play("tapeButton");
                soundHandler.stop("tapeRewind");
                tapeState = 0;
            }
        }
        if (GameData.hardCassette) tapeMusic.setVolume(0);
        else if (tapeMusic.isPlaying()) tapeMusic.setVolume(0.065f + tapeVolume);
    }

    public void render(Engine engine, Characters characters, Flashlight flashlight){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();


        TextureRegion region;
        if ((int) frame == 0) {
            if (state != 2){
                if (Game.lightBuffer == null) Game.lightBuffer = FrameBufferManager.newFrameBuffer();
                Game.lightBuffer.begin();
                renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
                renderHandler.drawScreen();
                if (state == 0) batch.draw(textureHandler.get("game/room/FullRoomEffect"), 0, 0);
                else batch.draw(textureHandler.get("game/room/UnderBedEffect"), 0, 0);
                region = textureHandler.get("game/Flashlight");

                float multiplier = 1;
                if (GameData.hitboxMultiplier != 1) {
                    if (GameData.expandedPointer) multiplier = 1.3125f;
                    else multiplier = 1.75f;
                } else if (GameData.expandedPointer) multiplier = 0.75f;

                float width = (float) region.getRegionWidth() / multiplier;
                float height = (float) region.getRegionHeight() / multiplier;

                batch.draw(region,
                        flashlight.getX() - width / 2,
                        flashlight.getY() - height / 2,
                        width,
                        height);

                FrameBufferManager.end(batch, Game.lightBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }

            if (state == 0) region = textureHandler.getRegion("game/room/Room", 3072, 0);
            else if (state == 1) region = textureHandler.get("game/room/UnderBed");
            else region = textureHandler.get("game/Tape/Tape");
        } else {
            int frame = (int) this.frame;
            if ((limit == 20 || targetState == 2) && frame > 10) frame = (int) (limit + 2 - Math.ceil(this.frame));
            else if (frame > 11) frame = (int) (limit + 2 - Math.ceil(this.frame));
            if (state == 0) region = textureHandler.get("game/Moving/Turn Around/Moving" + frame);
            else if (state == 1) region = textureHandler.get("game/Moving/Under Bed/Moving" + frame);
            else region = textureHandler.get("game/Moving/Turn Back/Moving" + frame);
        }

        if (Game.roomBuffer == null) Game.roomBuffer = FrameBufferManager.newFrameBuffer();
        Game.roomBuffer.begin();
        if (GameData.night == 2) batch.setColor(1, 0, 0, 1);
        if (state != 2) batch.draw(region, 0, 0);
        else {
            batch.draw(region, CameraManager.getX(), CameraManager.getY());
            if ((int) frame == 0) {
                int spriteRegion = getTapeRegion();
                region = textureHandler.getRegion("game/Tape/Buttons", 394, spriteRegion);
                batch.draw(region, CameraManager.getX() + 416, CameraManager.getY() + 126);
            }
        }
        if ((int) frame == 0) {
            characters.render(batch, textureHandler, this);
            if (state == 0){
                region = textureHandler.getRegion("game/room/Room", 3072, 1);
                batch.draw(region, 0, 0);
            }
        }
        characters.renderForward(batch, textureHandler, this);

        batch.setColor(1, 1, 1, 1);
        FrameBufferManager.end(batch, Game.roomBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        renderHandler.screenBuffer.begin();

        if (state != 2 && GameData.perspective) CameraManager.applyShader(batch);

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

        batch.setShader(null);
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

    public float getTapePlayAchievement() {
        return tapePlayAchievement;
    }

    public byte getState() {
        return state;
    }

    public int getFrame() {
        return (int) frame;
    }

    public int getBoundsX() {
        return boundsX;
    }

    public int getBoundsY() {
        return boundsY;
    }

    public boolean notTapeWeasel() {
        return !tapeWeasel;
    }

    public void setTapeWeasel() {
        tapeWeasel = !tapeWeasel;
    }
}