package state.Game.Objects.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import state.Game.Objects.Flashlight;
import state.Game.Objects.Player;
import state.Game.Objects.Room;
import util.*;

public class Characters {
    private Rat rat;
    private Cat cat;
    private Vinnie vinnie;
    private ShadowRat shadowRat;
    private ShadowCat shadowCat;
    private boolean twitchLock;

    public Characters(byte ratAI, byte catAI, byte vinnieAI, byte shadowRatAI, byte shadowCatAI) {
        if (ratAI != 0) rat = new Rat(ratAI);
        if (catAI != 0) cat = new Cat(catAI);
        if (vinnieAI != 0) vinnie = new Vinnie(vinnieAI);
        if (shadowRatAI != 0) shadowRat = new ShadowRat(shadowRatAI);
        if (shadowCatAI != 0) shadowCat = new ShadowCat(shadowCatAI);
    }

    public void load(){
        if (rat != null) rat.load();
        if (cat != null) cat.load();
        if (shadowRat != null) shadowRat.load();
        if (shadowCat != null) shadowCat.load();
    }

    public void update(Player player, Room room, Flashlight flashlight){
        boolean twitch = false;
        if (rat != null) twitch = rat.execute(player, cat, room, flashlight, false);
        if (cat != null) twitch = cat.execute(player, rat, room, flashlight, twitch);
        if (shadowRat != null) twitch = shadowRat.execute(player, shadowCat, room, flashlight, twitch);
        if (shadowCat != null) twitch = shadowCat.execute(player, shadowRat, room, flashlight, twitch);
        if (twitch && !twitchLock){
            SoundManager.play("twitch");
            SoundManager.setLoop("twitch", true);
        } else if (!twitch && twitchLock) SoundManager.stop("twitch");
        if (player.isFreeze()) SoundManager.stop("twitch");
        twitchLock = twitch;
    }

    public void render(SpriteBatch batch, Room room){
        if (rat != null){
            TextureRegion region = null;
            switch (rat.getRoomState()){
                case 0:
                    if (rat.getDoor().getFrame() == 13 || room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(rat.getPath(), (int) rat.getWidth(), (int) rat.getDoor().getFrame());
                    break;
                case 1:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(rat.getPath(), (int) rat.getWidth(), rat.getAttack().getRegion(rat.getTwitch()));
                    break;
                case 2:
                    if (room.getState() == 1 && room.getFrame() == 0) region = ImageManager.get(rat.getPath());
                    break;
                case 3:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(rat.getPath(), (int) rat.getWidth(), rat.getTwitch().getFrame());
                    break;
                case 4:
                    if (room.getState() != 0 || room.getFrame() != 0 || rat.getLeaveFrame() == 18) break;
                    region = ImageManager.getRegion(rat.getPath(), (int) rat.getWidth(), rat.getLeaveFrame());
            }
            if (region != null) batch.draw(region, rat.getX(), rat.getY());
        }

        if (cat != null){
            TextureRegion region = null;
            switch (cat.getRoomState()){
                case 1:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getAttack().getRegion(cat.getTwitch()));
                    break;
                case 2:
                    if (room.getState() == 1 && room.getFrame() == 0) region = ImageManager.get(cat.getPath());
                    break;
                case 3:
                    if (room.getState() == 0 && room.getFrame() == 0) region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getTwitch().getFrame());
                    break;
                case 4:
                    if (room.getState() != 0 || room.getFrame() != 0 || cat.getLeaveFrame() == 18) break;
                    region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getLeaveFrame());
            }
            if (region != null) batch.draw(region, cat.getX(), cat.getY());
        }

        if (shadowRat != null){
            TextureRegion region = null;
            switch (shadowRat.getRoomState()){
                case 0:
                    if (shadowRat.getDoor().getFrame() == 13 || room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(shadowRat.getPath(), (int) shadowRat.getWidth(), (int) shadowRat.getDoor().getFrame());
                    break;
                case 1:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(shadowRat.getPath(), (int) shadowRat.getWidth(), shadowRat.getAttack().getRegion(shadowRat.getTwitch()));
                    break;
                case 2:
                    if (room.getState() == 1 && room.getFrame() == 0) region = ImageManager.get(shadowRat.getPath());
                    break;
                case 3:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(shadowRat.getPath(), (int) shadowRat.getWidth(), shadowRat.getTwitch().getFrame());
                    break;
                case 4:
                    if (room.getState() != 0 || room.getFrame() != 0 || shadowRat.getLeaveFrame() == 18) break;
                    region = ImageManager.getRegion(shadowRat.getPath(), (int) shadowRat.getWidth(), shadowRat.getLeaveFrame());
            }
            if (region != null) batch.draw(region, shadowRat.getX(), shadowRat.getY());
        }

        if (shadowCat != null){
            TextureRegion region = null;
            switch (shadowCat.getRoomState()){
//                case 0:
//                    if (shadowCat.getPath().isEmpty() || room.getState() != 0 || room.getFrame() != 0) break;
//                    if (shadowCat.getBedSide().getPhase() == 1) region = ImageManager.getRegion(shadowCat.getPath(), (int) shadowCat.getWidth(), shadowCat.getBedSide().getFrame());
//                    else if (shadowCat.getBedSide().getPhase() == 2) region = ImageManager.getRegion(shadowCat.getPath(), (int) shadowCat.getWidth(), shadowCat.getBedSide().getFrame() - 24);
//                    else region = ImageManager.getRegion(shadowCat.getPath(), (int) shadowCat.getWidth(), shadowCat.getBedSide().getFrame() - 43);
//                    break;
                case 1:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(shadowCat.getPath(), (int) shadowCat.getWidth(), shadowCat.getAttack().getRegion(shadowCat.getTwitch()));
                    break;
                case 2:
                    if (room.getState() == 1 && room.getFrame() == 0) region = ImageManager.get(shadowCat.getPath());
                    break;
                case 3:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    region = ImageManager.getRegion(shadowCat.getPath(), (int) shadowCat.getWidth(), shadowCat.getTwitch().getFrame());
                    break;
                case 4:
                    if (room.getState() != 0 || room.getFrame() != 0 || shadowCat.getLeaveFrame() == 18) break;
                    region = ImageManager.getRegion(shadowCat.getPath(), (int) shadowCat.getWidth(), shadowCat.getLeaveFrame());
            }
            if (region != null) batch.draw(region, shadowCat.getX(), shadowCat.getY());
        }
    }

    public void renderForward(SpriteBatch batch, Room room){
        TextureRegion region;
        if (cat != null && cat.getRoomState() == 0 && !cat.getPath().isEmpty() && room.getFrame() == 0 && room.getState() == 0){
            if (cat.getBedSide().getPhase() == 1) region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame());
            else if (cat.getBedSide().getPhase() == 2) region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame() - 24);
            else region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame() - 43);
            batch.draw(region, cat.getX(), cat.getY());
        }
    }

    public void debug(SpriteBatch batch, Window window) {
        if (rat != null) {
            if (rat.getRoomState() == 1) {
                RenderManager.shapeDrawer.setColor(0.2f, 0.2f, 0.2f, 1);
                RenderManager.shapeDrawer.filledRectangle(
                        (float) window.getWidth() / 4 + CameraManager.getX(),
                        50 + CameraManager.getY(),
                        640,
                        50);

                RenderManager.shapeDrawer.setColor(0.75f, 0, 0, 1);
                float value = Math.min(1, rat.getAttackHealth());
                RenderManager.shapeDrawer.filledRectangle(
                        (float) window.getWidth() / 4 + CameraManager.getX(),
                        50 + CameraManager.getY(),
                        (int) (value * 640),
                        50);
            }
        }

        if (cat != null) {
            if (cat.getRoomState() == 1) {
                RenderManager.shapeDrawer.setColor(0.2f, 0.2f, 0.2f, 1);
                RenderManager.shapeDrawer.filledRectangle(
                        (float) window.getWidth() / 4 + CameraManager.getX(),
                        150 + CameraManager.getY(),
                        640,
                        50);

                RenderManager.shapeDrawer.setColor(0.75f, 0, 0, 1);
                float value = Math.min(1, cat.getAttackHealth());
                RenderManager.shapeDrawer.filledRectangle(
                        (float) window.getWidth() / 4 + CameraManager.getX(),
                        150 + CameraManager.getY(),
                        (int) (value * 640),
                        50);
            }

//            RenderManager.shapeDrawer.setColor(0.75f, 0.5f, 0.5f, 0.5f);
//            RenderManager.shapeDrawer.filledCircle(
//                    characters.getCat().getHitbox().getX(),
//                    characters.getCat().getHitbox().getY(),
//                    characters.getCat().getHitbox().size);
        }

//        if (shadowRat != null) {
//            RenderManager.shapeDrawer.setColor(0.2f, 0.2f, 0.2f, 1);
//            RenderManager.shapeDrawer.filledRectangle(
//                    (float) core.core.Candys3Deluxe.width / 4 + CameraManager.getX(),
//                    650 + CameraManager.getY(),
//                    640,
//                    50);
//
//            RenderManager.shapeDrawer.setColor(0.75f, 0, 0, 1);
//            float value = Math.min(1, shadowRat.getAttackHealth());
//            RenderManager.shapeDrawer.filledRectangle(
//                    (float) core.core.Candys3Deluxe.width / 4 + CameraManager.getX(),
//                    650 + CameraManager.getY(),
//                    (int) (value * 640),
//                    50);
//
////            color = new Color(0.75f, 0.5f, 0.5f, 0.5f);
////            RenderManager.shapeDrawer.setColor(color);
////            RenderManager.shapeDrawer.filledCircle(
////                    characters.getShadowRat().getHitbox().getX(),
////                    characters.getShadowRat().getHitbox().getY(),
////                    characters.getShadowRat().getHitbox().size);
//        }

        if (shadowCat != null) {
            RenderManager.shapeDrawer.setColor(0.2f, 0.2f, 0.2f, 1);
            RenderManager.shapeDrawer.filledRectangle(
                    (float) window.getWidth() / 4 + CameraManager.getX(),
                    650 + CameraManager.getY(),
                    640,
                    50);

            RenderManager.shapeDrawer.setColor(0.5f, 0, 0.5f, 1);
            float value = Math.min(1, shadowCat.getAttackHealth());
            RenderManager.shapeDrawer.filledRectangle(
                    (float) window.getWidth() / 4 + CameraManager.getX(),
                    650 + CameraManager.getY(),
                    (int) (value * 640),
                    50);

            if (shadowCat.getRoomState() == 0) {
                RenderManager.shapeDrawer.setColor(0.75f, 0, 0.75f, 0.5f);
                RenderManager.shapeDrawer.filledCircle(
                        shadowCat.getHitbox().getX(),
                        shadowCat.getHitbox().getY(),
                        shadowCat.getHitbox().size);
            }
        }
    }
}
