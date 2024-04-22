package state.Game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import core.Candys3Deluxe;
import data.GameData;
import state.Game.Functions.Jumpscare;
import state.Game.Objects.Character.Characters;
import state.Game.Objects.Flashlight;
import state.Game.Objects.Player;
import state.Game.Objects.Room;
import util.*;

public class Game {
    private float nightTime;
    private final float nightTimeLength;
    private final StringBuilder nightTimeBuilder;
    private Characters characters;
    private final Player player;
    private final Flashlight flashlight;
    private final Room room;

    public static FrameBuffer lightBuffer;
    public static FrameBuffer roomBuffer;

    public Game(){
        player = new Player(2.75f, 896, 152);
        flashlight = new Flashlight();
        room = new Room();
        byte minute = 8;
        nightTimeLength = 60 * minute;
        nightTimeBuilder = new StringBuilder();
    }

    public void load(){
        reset();
        characters.load();
        room.load();
        StringBuilder sb = new StringBuilder("game/");
        String prefix = sb.append("Buttons/").toString();
        ImageManager.add(prefix + "TapePlayer");
        ImageManager.add(prefix + "TapePlayerBack");
        ImageManager.add(prefix + "UnderBed");
        ImageManager.add(prefix + "UnderBedBack");

        sb.delete(sb.indexOf("/") + 1, sb.length());
        prefix = sb.append("room/").toString();
        ImageManager.add(prefix + "Room");
        ImageManager.add(prefix + "FullRoomEffect");
        ImageManager.add(prefix + "UnderBed");
        ImageManager.add(prefix + "UnderBedEffect");

        sb.delete(sb.indexOf("/") + 1, sb.length());
        prefix = sb.append("Tape/").toString();
        ImageManager.add(prefix + "Tape");
        prefix = sb.append("Buttons/").toString();
        ImageManager.add(prefix + "Buttons");

        sb.delete(sb.indexOf("/") + 1, sb.length());
        prefix = sb.append("Moving/").toString();

        for (int i = 1; i <= 11; i++){
            ImageManager.add(prefix + "Turn Back/Moving" + i);
            if (i == 11) break;
            ImageManager.add(prefix + "Turn Around/Moving" + i);
            ImageManager.add(prefix + "Under Bed/Moving" + i);
        }

        prefix = sb.delete(sb.indexOf("/") + 1, sb.length()).toString();
        ImageManager.add(prefix + "BattleOverlay");
        ImageManager.add(prefix + "Clock");
        ImageManager.add(prefix + "Flashlight");

        SoundManager.addAll(gameSoundData);
    }

    public void update(Window window){
        if (nightTime != 0 && Candys3Deluxe.inputManager.keyTyped(Input.Keys.R)) {
            reset();
            SoundManager.stopAllSounds();
        }

        if (nightTime != 0 && Candys3Deluxe.inputManager.keyTyped(Input.Keys.F2)) {
            Candys3Deluxe.stateManager.setState((byte) 0);
            room.stopMusic();
            SoundManager.stopAllSounds();
            return;
        }

        if (!player.isJumpscare()) {
            player.update(window, flashlight, room);
            if (player.getY() == 0)
                room.input(Candys3Deluxe.inputManager.getX(), Candys3Deluxe.inputManager.getY(), player, Candys3Deluxe.inputManager.isPressed());
            room.update(player);
            characters.update(player, room, flashlight);
        }

        player.updateEffects(room);

        if (!GameData.infiniteNight && !GameData.hardCassette && room.isMusicPlaying()) nightTime = Time.increaseTimeValue(nightTime, nightTimeLength, 2);
        else nightTime = Time.increaseTimeValue(nightTime, nightTimeLength, 1);
        if (nightTime != nightTimeLength) return;
        Candys3Deluxe.stateManager.setState((byte) 2);
        room.stopMusic();
        SoundManager.stopAllSounds();
    }

    public void reset(){
        characters = new Characters(GameData.ratAI, GameData.catAI, GameData.vinnieAI, GameData.shadowRatAI, GameData.shadowCatAI);
        player.reset();
        room.reset();
        Jumpscare.reset();
        nightTime = 0;
    }

    public void render(SpriteBatch batch, Window window){
        player.adjustCamera(flashlight);

        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        RenderManager.shapeDrawer.update();
        batch.enableBlending();
        batch.begin();

        room.render(batch, characters, room, flashlight);

        if (player.getOverlayAlpha() > 0) {
            int srcFunc = batch.getBlendSrcFunc();
            int dstFunc = batch.getBlendDstFunc();
            //DON'T CHANGE THIS!!!
            batch.setBlendFunction(GL20.GL_SRC_COLOR, GL20.GL_DST_ALPHA);
            TextureRegion region = ImageManager.get("game/BattleOverlay");
            batch.setColor(player.getOverlayAlpha(), player.getOverlayAlpha(), player.getOverlayAlpha(), 1);
            batch.draw(region, CameraManager.getX(), CameraManager.getY(), 1280, 720);
            batch.setColor(1, 1, 1, 1);
            batch.flush();
            batch.setBlendFunction(srcFunc, dstFunc);
        }

        if (player.isJumpscare() && player.getBlacknessTimes() == 0){
            ScreenUtils.clear(0, 0, 0, 1);
            if (room.isMusicPlaying()) room.stopMusic();
            Jumpscare.render(batch);
        }

        RenderManager.shapeDrawer.setColor(0, 0, 0, 1 - player.getBlacknessAlpha());
        RenderManager.shapeDrawer.filledRectangle(CameraManager.getX(), CameraManager.getY(), 1280, 720);

        RenderManager.shapeDrawer.setColor(0.2f, 0, 0.2f, player.getPurpleAlpha());
        RenderManager.shapeDrawer.filledRectangle(CameraManager.getX(), CameraManager.getY(), 1280, 720);

        if (player.getButtonFade() > 0) {
            batch.setColor(1, 1, 1, player.getButtonFade());
            TextureRegion region;
            switch (room.getState()) {
                case 0:
                    region = ImageManager.get("game/Buttons/TapePlayer");
                    batch.draw(region, CameraManager.getX() + 132, CameraManager.getY());

                    region = ImageManager.get("game/Buttons/UnderBed");
                    batch.draw(region, CameraManager.getX() + 732, CameraManager.getY());
                    break;
                case 1:
                    region = ImageManager.get("game/Buttons/UnderBedBack");
                    batch.draw(region, CameraManager.getX() + 430, CameraManager.getY());
                    break;
                case 2:
                    region = ImageManager.get("game/Buttons/TapePlayerBack");
                    batch.draw(region, CameraManager.getX() + 430, CameraManager.getY());
                    break;
            }
            batch.setColor(1, 1, 1, 1);
        }

        GlyphLayout layout = FontManager.layout;
        Candys3Deluxe.candysFont.setColor(1, 1, 1, 1);
        nightTimeBuilder.delete(0, nightTimeBuilder.length());
        if (GameData.infiniteNight) {
            int hour = (int) nightTime / 60;
            nightTimeBuilder.append(hour).append(":");
            int tempTime = (int) (nightTime % 60);
            if (tempTime < 10) nightTimeBuilder.append(0);
            nightTimeBuilder.append(tempTime);
        } else {
            int hour = (int) (nightTime / (nightTimeLength / 6));
            if (hour == 0) hour = 12;
            nightTimeBuilder.append(hour).append(" AM");
        }
        layout.setText(Candys3Deluxe.candysFont, nightTimeBuilder.toString());
        Candys3Deluxe.candysFont.draw(batch, layout,
                CameraManager.getX() + window.getWidth() - layout.width - 20,
                CameraManager.getY() + window.getHeight() - layout.height + 8);

        debugRender(batch, window);
    }

    private void debugRender(SpriteBatch batch, Window window){
        InputManager inputManager = Candys3Deluxe.inputManager;

        Candys3Deluxe.debugFont.draw(batch,
                "Mouse: " + (int) inputManager.getX() + " | " + (int) inputManager.getY(),
                24 + CameraManager.getX(), 696 + CameraManager.getY());

        characters.debug(batch, window);
    }

    public void dispose(){
        room.dispose();
    }

    public static final String[] gameSoundData = new String[] {
            "attack_begin",
            "attack",
            "bed",
            "cat",
            "catLeft",
            "catRight",
            "catEerie",
            "catPulse",
            "ambience",
            "crawl",
            "spotted",
            "knock",
            "hard_knock",
            "knockLeft",
            "hard_knockLeft",
            "knockRight",
            "hard_knockRight",
            "peek",
            "get_in",
            "twitch",
            "dodge",
            "dodgeLeft",
            "dodgeRight",
            "thunder",
            "leave",
            "tapeButton",
            "tapeStop",
            "tapeRewind",
            "movebed1",
            "movebed2",
            "flashlight",
            "lookbed1",
            "lookbed2"
    };
}