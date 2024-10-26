package candys3.Game.Objects.Character;

import candys3.GameData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import util.*;

public class Characters {
    private Rat rat;
    private Cat cat;

    public void ratCatLoad(TextureHandler textureHandler){
        if (GameData.ratAI != 0 || GameData.night == 2) {
            rat = new Rat(GameData.night);
            textureHandler.addImages("game/" + rat.getName() + "/", "candys3/game/textures/characters/common.txt");
            textureHandler.addImages("game/" + rat.getName() + "/", "candys3/game/textures/characters/rat.txt");
            if (rat.getType() == 1) textureHandler.add("game/Shadow Rat/Classic/Jumpscare");
        } else {
            rat = null;
        }
        if (GameData.catAI != 0 || GameData.night == 2) {
            cat = new Cat(GameData.night);
            if (GameData.night == 0) {
                textureHandler.addImages("game/Cat/", "candys3/game/textures/characters/common.txt");
                textureHandler.addImages("game/Cat/", "candys3/game/textures/characters/cat.txt");
            } else {
                textureHandler.addImages("game/Shadow Cat/", "candys3/game/textures/characters/common.txt");
                textureHandler.addImages("game/Shadow Cat/", "candys3/game/textures/characters/shadowCat.txt");
            }
        } else {
            cat = null;
        }
    }

    public void reset(){
        if (rat != null) rat = new Rat(GameData.night);
        if (cat != null) cat = new Cat(GameData.night);
    }

    public void update(SoundHandler soundHandler, Player player, Room room){
        boolean twitch = false;
        if (rat != null) twitch = rat.execute(soundHandler, player, cat, room, false);
        if (cat != null) twitch = cat.execute(soundHandler, player, rat, room, twitch);
        if (twitch && !soundHandler.isPlaying("twitch")){
            soundHandler.play("twitch");
            soundHandler.setSoundEffect(soundHandler.LOOP, "twitch", 1);
        }
        if (player.isFreeze() || (!twitch && soundHandler.isPlaying("twitch"))) soundHandler.stop("twitch");
    }

    public void render(SpriteBatch batch, TextureHandler textureHandler, Room room){
        if (rat != null){
            TextureRegion ratRegion = null;
            switch (rat.getState()){
                case 0:
                    if (rat.getDoor().getFrame() == 13 || room.getState() != 0 || room.getFrame() != 0) break;
                    ratRegion = textureHandler.getRegion(rat.getPath(), (int) rat.getWidth(), (int) rat.getDoor().getFrame());
                    break;
                case 1:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    ratRegion = textureHandler.getRegion(rat.getPath(), (int) rat.getWidth(), rat.getAttack().getRegion(rat.getTwitchFrame()));
                    break;
                case 2:
                    if (room.getState() == 1 && room.getFrame() == 0) ratRegion = textureHandler.get(rat.getPath());
                    break;
                case 3:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    ratRegion = textureHandler.getRegion(rat.getPath(), (int) rat.getWidth(), rat.getTwitchFrame());
                    break;
                case 4:
                    if (room.getState() != 0 || room.getFrame() != 0 || rat.getLeaveFrame() == 18) break;
                    ratRegion = textureHandler.getRegion(rat.getPath(), (int) rat.getWidth(), rat.getLeaveFrame());
            }
            if (ratRegion != null) batch.draw(ratRegion, rat.getX(), rat.getY());

            if (rat.getState() == 2 && room.getState() == 2 && room.getFrame() == 0 && rat.getTapeFrame() != 0){
                batch.draw(textureHandler.getRegion("game/" + rat.getName() + "/Tape/Tape", 750, 12 - rat.getTapeFrame()),
                        CameraManager.getX() + 194, CameraManager.getY() + 338);
            }
        }

        if (cat != null){
            TextureRegion catRegion = null;
            switch (cat.getState()){
                case 0:
                    if (cat.getType() == 0 || cat.getBedSide().getFrame() == 0 || room.getFrame() != 0 || room.getState() != 0 || cat.getSide() == 0) break;
                    int limit = (cat.getBedSide().getPhase() - 2) * 12;
                    if (cat.getBedSide().getFrame() < 8) catRegion = textureHandler.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame());
                    else {
                        StringBuilder path = new StringBuilder(cat.getPath());
                        if (cat.getTwitchFrame() == 1) path.append("Twitching/");
                        path.append(cat.getBedSide().getPhase() - 1);
                        catRegion = textureHandler.getRegion(path.toString(), (int) cat.getWidth(), cat.getBedSide().getFrame() - 8 - limit);
                    }
                    batch.draw(catRegion, cat.getX(), cat.getY());
                    break;
                case 1:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    catRegion = textureHandler.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getAttack().getRegion(cat.getTwitchFrame()));
                    break;
                case 2:
                    if (room.getState() == 1 && room.getFrame() == 0) catRegion = textureHandler.get(cat.getPath());
                    break;
                case 3:
                    if (room.getState() == 0 && room.getFrame() == 0) catRegion = textureHandler.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getTwitchFrame());
                    break;
                case 4:
                    if (room.getState() != 0 || room.getFrame() != 0 || cat.getLeaveFrame() == 18) break;
                    catRegion = textureHandler.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getLeaveFrame());
            }
            if (catRegion != null) batch.draw(catRegion, cat.getX(), cat.getY());
        }
    }

    public void renderForward(SpriteBatch batch, TextureHandler textureHandler, Room room){
        TextureRegion catRegion = null;
        if (room.getFrame() == 0 && room.getState() == 0){
            if (cat != null && cat.getState() == 0 && !cat.getPath().isEmpty()){
                if (cat.getType() != 0 && cat.getSide() == 0 && cat.getBedSide().getFrame() > 0){
                    int limit = (cat.getBedSide().getPhase() - 2) * 12;
                    if (cat.getBedSide().getFrame() < 8) catRegion = textureHandler.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame());
                    else {
                        StringBuilder path = new StringBuilder(cat.getPath());
                        if (cat.getTwitchFrame() == 1) path.append("Twitching/");
                        path.append(cat.getBedSide().getPhase() - 1);
                        catRegion = textureHandler.getRegion(path.toString(), (int) cat.getWidth(), cat.getBedSide().getFrame() - 8 - limit);
                    }
                    if (catRegion != null) batch.draw(catRegion, cat.getX(), cat.getY());
                } else if (cat.getType() == 0){
                    if (cat.getBedSide().getPhase() == 1) catRegion = textureHandler.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame());
                    else if (cat.getBedSide().getPhase() == 2) catRegion = textureHandler.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame() - 24);
                    else catRegion = textureHandler.getRegion(cat.getPath(), (int) cat.getWidth(), cat.getBedSide().getFrame() - 43);
                }
                if (catRegion != null) batch.draw(catRegion, cat.getX(), cat.getY());
            }
        }
    }

    private void flashDebug(RenderHandler renderHandler, Window window, float attackHealth, byte type){
        if (!GameData.flashDebug) return;
        renderHandler.shapeDrawer.setColor(0.2f, 0.2f, 0.2f, 1);
        renderHandler.shapeDrawer.filledRectangle(
                (float) window.width() / 4 + CameraManager.getX(),
                150 + CameraManager.getY() * 0.75f,
                640,
                50);
        if (type == 0) renderHandler.shapeDrawer.setColor(0.75f, 0, 0, 1);
        else if (type == 1) renderHandler.shapeDrawer.setColor(0.5f, 0, 0.5f, 1);
        else renderHandler.shapeDrawer.setColor(0.75f, 0.25f, 0.25f, 1);
        float value = Math.min(1, attackHealth);
        renderHandler.shapeDrawer.filledRectangle(
                (float) window.width() / 4 + CameraManager.getX(),
                150 + CameraManager.getY() * 0.75f,
                (int) (value * 640),
                50);
    }

    private void hitboxDebug(RenderHandler renderHandler, Hitbox hitbox, byte type){
        if (!GameData.hitboxDebug || hitbox.size == 0) return;
        if (type == 0) renderHandler.shapeDrawer.setColor(0.75f, 0.5f, 0.5f, 0.5f);
        else if (type == 1) renderHandler.shapeDrawer.setColor(0.75f, 0, 0.75f, 0.5f);
        else renderHandler.shapeDrawer.setColor(0.75f, 0.25f, 0.25f, 0.5f);
        renderHandler.shapeDrawer.filledCircle(
                hitbox.getX(),
                hitbox.getY(),
                hitbox.size);
    }

    public void debug(RenderHandler renderHandler, Window window, Player player, Room room) {
        if (player.isJumpscare()) return;
        if (rat != null) {
            if (player.getSide() == rat.getSide() && room.getState() == 0 && room.getFrame() == 0) {
                hitboxDebug(renderHandler, rat.getHitbox(), rat.getType());
            }
            if (rat.getAttack().isAttack()) flashDebug(renderHandler, window, rat.getAttack().getKillTimer() / 2, rat.getType());
        }

        if (cat != null) {
            if (player.getSide() == cat.getSide() && room.getState() == 0 && room.getFrame() == 0) {
                hitboxDebug(renderHandler, cat.getHitbox(), cat.getType());
            }
            if (cat.getAttack().isAttack()) flashDebug(renderHandler, window, cat.getAttack().getKillTimer() / 2, cat.getType());
        }
    }

    public void dispose(){
        rat = null;
        cat = null;
    }

    public Rat getRat() {
        return rat;
    }

    public Cat getCat() {
        return cat;
    }
}