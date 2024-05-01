package state.Game.Objects.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import core.Candys3Deluxe;
import deluxe.Paths;
import deluxe.GameData;
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
        if (GameData.night == 0) {
            if (ratAI != 0) rat = new Rat(ratAI);
            if (catAI != 0) cat = new Cat(catAI);
        }
        if (GameData.night == 1){
            if (shadowRatAI != 0) shadowRat = new ShadowRat(shadowRatAI);
            if (shadowCatAI != 0) shadowCat = new ShadowCat(shadowCatAI);
        }
        if (GameData.night == 2 && vinnieAI != 0) vinnie = new Vinnie(vinnieAI);
    }

    public void load(){
        if (rat != null) {
            ImageManager.addImages("game/Rat/", Paths.dataPath1 + "game/textures/characters/common.txt");
            ImageManager.addImages("game/Rat/", Paths.dataPath1 + "game/textures/characters/rat.txt");
        }
        if (cat != null) {
            ImageManager.addImages("game/Cat/", Paths.dataPath1 + "game/textures/characters/common.txt");
            ImageManager.addImages("game/Cat/", Paths.dataPath1 + "game/textures/characters/cat.txt");
        }
        if (shadowRat != null) {
            ImageManager.addImages("game/Shadow Rat/", Paths.dataPath1 + "game/textures/characters/common.txt");
            ImageManager.addImages("game/Shadow Rat/", Paths.dataPath1 + "game/textures/characters/rat.txt");
        }
        if (shadowCat != null) {
            ImageManager.addImages("game/Shadow Cat/", Paths.dataPath1 + "game/textures/characters/common.txt");
            ImageManager.addImages("game/Shadow Cat/", Paths.dataPath1 + "game/textures/characters/shadowCat.txt");
        }
    }

    public void update(Player player, Room room, Flashlight flashlight){
        boolean twitch = false;
        if (rat != null) twitch = rat.execute(player, cat, room, flashlight, false);
        if (cat != null) twitch = cat.execute(player, rat, room, flashlight, twitch);
        if (shadowRat != null) twitch = shadowRat.execute(player, shadowCat, room, flashlight, twitch);
        if (shadowCat != null) twitch = shadowCat.execute(player, shadowRat, room, flashlight, twitch);
        if (twitch && !twitchLock){
            SoundManager.play("twitch");
            SoundManager.setSoundEffect(SoundManager.LOOP, "twitch", 1);
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
                case 0:
                    if (shadowCat.getBedSide().getFrame() == 0 || room.getFrame() != 0 || room.getState() != 0) break;
                    int limit = (shadowCat.getBedSide().getPhase() - 2) * 12;
                    if (shadowCat.getBedSide().getFrame() < 8) region = ImageManager.getRegion(shadowCat.getPath(), (int) shadowCat.getWidth(), shadowCat.getBedSide().getFrame());
                    else {
                        StringBuilder path = new StringBuilder(shadowCat.getPath());
                        if (shadowCat.getTwitch().getFrame() == 1) path.append("Twitching/");
                        path.append(shadowCat.getBedSide().getPhase() - 1);
                        region = ImageManager.getRegion(path.toString(), (int) shadowCat.getWidth(), shadowCat.getBedSide().getFrame() - 8 - limit);
                    }
                    batch.draw(region, shadowCat.getX(), shadowCat.getY());
                    break;
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
        if (room.getFrame() == 0 && room.getState() == 0){
            if (cat != null && cat.getRoomState() == 0 && !cat.getPath().isEmpty()){
                if (cat.getBedSide().getPhase() == 1) region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame());
                else if (cat.getBedSide().getPhase() == 2) region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame() - 24);
                else region = ImageManager.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame() - 43);
                batch.draw(region, cat.getX(), cat.getY());
            }
        }
    }

    private void flashDebug(Window window, float attackHealth, boolean shadow){
        if (!GameData.flashDebug) return;
        RenderManager.shapeDrawer.setColor(0.2f, 0.2f, 0.2f, 1);
        RenderManager.shapeDrawer.filledRectangle(
                (float) window.width() / 4 + CameraManager.getX(),
                150 + CameraManager.getY() * 0.75f,
                640,
                50);
        if (!shadow) RenderManager.shapeDrawer.setColor(0.75f, 0, 0, 1);
        else RenderManager.shapeDrawer.setColor(0.5f, 0, 0.5f, 1);
        float value = Math.min(1, attackHealth);
        RenderManager.shapeDrawer.filledRectangle(
                (float) window.width() / 4 + CameraManager.getX(),
                150 + CameraManager.getY() * 0.75f,
                (int) (value * 640),
                50);
    }

    private void hitboxDebug(Hitbox hitbox, boolean shadow){
        if (!GameData.hitboxDebug || hitbox.size == 0) return;
        if (!shadow) RenderManager.shapeDrawer.setColor(0.75f, 0.5f, 0.5f, 0.5f);
        else RenderManager.shapeDrawer.setColor(0.75f, 0, 0.75f, 0.5f);
        RenderManager.shapeDrawer.filledCircle(
                hitbox.getX(),
                hitbox.getY(),
                hitbox.size);
    }

    public void debug(SpriteBatch batch, Window window, Player player, Room room) {
        if (rat != null) {
            if (player.getSide() == rat.getSide() && room.getState() == 0 && room.getFrame() == 0) hitboxDebug(rat.getHitbox(), false);
            if (rat.getAttack().isAttack()) flashDebug(window, rat.getAttackHealth(), false);
            FontManager.setFont(Candys3Deluxe.captionFont);
            FontManager.setSize(18);
            FontManager.setText("Limit: " + rat.getAttack().getLimit());
            FontManager.render(batch, CameraManager.getX() + 16, CameraManager.getY() + 654);
        }

        if (cat != null) {
            if (player.getSide() == cat.getSide() && room.getState() == 0 && room.getFrame() == 0) hitboxDebug(cat.getHitbox(), false);
            if (cat.getAttack().isAttack()) flashDebug(window, cat.getAttackHealth(), false);
        }

        if (shadowRat != null) {
            if (player.getSide() == shadowRat.getSide() && room.getState() == 0 && room.getFrame() == 0) hitboxDebug(shadowRat.getHitbox(), true);
            if (shadowRat.getAttack().isAttack()) flashDebug(window, shadowRat.getAttackHealth(), true);
        }

        if (shadowCat != null) {
            if (player.getSide() == shadowCat.getSide() && room.getState() == 0 && room.getFrame() == 0) hitboxDebug(shadowCat.getHitbox(), true);
            if (shadowCat.getAttack().isAttack()) flashDebug(window, shadowCat.getAttackHealth(), true);
        }
    }

    public boolean checkMode(){
        return (GameData.night == 0 && rat != null && cat != null) || (GameData.night == 1 && shadowRat != null && shadowCat != null);
    }
}
