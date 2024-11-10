package candys3.Game.Objects.Character;

import candys3.Game.Game;
import candys3.Menu.Menu;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import util.*;

public class Characters {
    private Rat rat;
    private Cat cat;
    private Vinnie vinnie;

    public void ratCatLoad(TextureHandler textureHandler, Menu menu){
        if (menu.rat.getAi() != 0 || menu.night == 2) {
            rat = new Rat((byte) menu.night);
            textureHandler.addImages("game/" + rat.getName() + "/", "candys3/game/textures/characters/common.txt");
            textureHandler.addImages("game/" + rat.getName() + "/", "candys3/game/textures/characters/battleCommon.txt");
            textureHandler.addImages("game/" + rat.getName() + "/", "candys3/game/textures/characters/rat.txt");
            if (rat.getType() == 1) textureHandler.add("game/Shadow Rat/Classic/Jumpscare");
        } else {
            rat = null;
        }
        if (menu.cat.getAi() != 0 || menu.night == 2) {
            cat = new Cat((byte) menu.night);
            if (menu.night == 0) {
                textureHandler.addImages("game/Cat/", "candys3/game/textures/characters/common.txt");
                textureHandler.addImages("game/Cat/", "candys3/game/textures/characters/battleCommon.txt");
                textureHandler.addImages("game/Cat/", "candys3/game/textures/characters/cat.txt");
            } else {
                textureHandler.addImages("game/Shadow Cat/", "candys3/game/textures/characters/common.txt");
                textureHandler.addImages("game/Shadow Cat/", "candys3/game/textures/characters/battleCommon.txt");
                textureHandler.addImages("game/Shadow Cat/", "candys3/game/textures/characters/shadowCat.txt");
            }
        } else {
            cat = null;
        }
        if (menu.vinnie.getAi() != 0 && menu.night != 2){
            vinnie = new Vinnie((byte) menu.night);
            textureHandler.addImages("game/Vinnie/", "candys3/game/textures/characters/common.txt");
            textureHandler.addImages("game/Vinnie/", "candys3/game/textures/characters/vinnie.txt");
        } else {
            vinnie = null;
        }
    }

    public void reset(int night){
        if (rat != null) rat = new Rat((byte) night);
        if (cat != null) cat = new Cat((byte) night);
        if (vinnie != null) vinnie = new Vinnie((byte) night);
    }

    public void update(SoundHandler soundHandler, Game game, Player player, Room room){
        boolean twitch = false;
        if (rat != null) twitch = rat.execute(soundHandler, game, player, room, false);
        if (cat != null) twitch = cat.execute(soundHandler, game, player, room, twitch);
        if (vinnie != null) twitch = vinnie.execute(soundHandler, game, player, room, twitch);
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

        if (vinnie != null){
            TextureRegion vinnieRegion = null;
            switch (vinnie.getState()){
                case 0:
                    if (vinnie.getDoor().getFrame() == 13 || room.getState() != 0 || room.getFrame() != 0) break;
                    vinnieRegion = textureHandler.getRegion(vinnie.getPath(), (int) vinnie.getWidth(), (int) vinnie.getDoor().getFrame());
                    break;
                case 1:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    String path = "1";
                    int attackPosition = (int) (vinnie.getAttackPosition() / 2);
                    if (vinnie.getTwitchFrame() == 1) path = "2";
                    else if ((int) vinnie.getAttackPosition() % 2 == 1) path = "3";
                    vinnieRegion = textureHandler.getRegion(vinnie.getPath() + path, (int) vinnie.getWidth(), attackPosition);
                    break;
                case 2:
                    if (room.getState() == 1 && room.getFrame() == 0) vinnieRegion = textureHandler.get(vinnie.getPath());
                    break;
                case 3:
                    if (room.getState() != 0 || room.getFrame() != 0) break;
                    vinnieRegion = textureHandler.getRegion(vinnie.getPath(), (int) vinnie.getWidth(), vinnie.getTwitchFrame());
                    break;
                case 4:
                    if (room.getState() != 0 || room.getFrame() != 0 || vinnie.getLeaveFrame() == 18) break;
                    vinnieRegion = textureHandler.getRegion(vinnie.getPath(), (int) vinnie.getWidth(), vinnie.getLeaveFrame());
            }
            if (vinnieRegion != null) batch.draw(vinnieRegion, vinnie.getX(), vinnie.getY());

            if (vinnie.getState() == 2 && room.getState() == 2 && room.getFrame() == 0 && vinnie.getTapeFrame() != 0){
                batch.draw(textureHandler.getRegion("game/" + vinnie.getName() + "/Tape/Tape", 750, 12 - rat.getTapeFrame()),
                        CameraManager.getX() + 194, CameraManager.getY() + 338);
            }
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

    private void flashDebug(RenderHandler renderHandler, Window window, boolean flashDebug, float attackHealth, byte type){
        if (!flashDebug) return;
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

    private void hitboxDebug(RenderHandler renderHandler, Hitbox hitbox, boolean hitboxDebug){
        if (!hitboxDebug || hitbox.size == 0) return;
        renderHandler.shapeDrawer.setColor(1, 1, 1, 1);
        renderHandler.shapeDrawer.filledCircle(
                hitbox.getX(),
                hitbox.getY(),
                hitbox.size);
    }

    public void debug(SpriteBatch batch, RenderHandler renderHandler, Window window, boolean flashDebug, boolean hitboxDebug, Player player, Room room) {
        if (player.isJumpscare()) return;
        boolean roomView = room.getState() == 0 && room.getFrame() == 0;

        if (Game.ratDebugBuffer == null) Game.ratDebugBuffer = FrameBufferManager.newFrameBuffer();
        Game.ratDebugBuffer.begin();
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();
        if (rat != null && player.getSide() == rat.getSide() && roomView) {
            hitboxDebug(renderHandler, rat.getHitbox(), hitboxDebug);
        }
        FrameBufferManager.end(batch, Game.ratDebugBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (Game.catDebugBuffer == null) Game.catDebugBuffer = FrameBufferManager.newFrameBuffer();
        Game.catDebugBuffer.begin();
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();
        if (cat != null && player.getSide() == cat.getSide() && roomView) {
            hitboxDebug(renderHandler, cat.getHitbox(), hitboxDebug);
        }
        FrameBufferManager.end(batch, Game.catDebugBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (Game.vinnieDebugBuffer == null) Game.vinnieDebugBuffer = FrameBufferManager.newFrameBuffer();
        Game.vinnieDebugBuffer.begin();
        renderHandler.shapeDrawer.setColor(0, 0, 0, 1);
        renderHandler.drawScreen();
        if (vinnie != null && player.getSide() == vinnie.getSide() && roomView) {
            hitboxDebug(renderHandler, vinnie.getHitbox(), hitboxDebug);
        }
        FrameBufferManager.end(batch, Game.catDebugBuffer, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (rat != null && rat.getAttack().isAttack()) {
            flashDebug(renderHandler, window, flashDebug, rat.getAttack().getKillTimer() / 2, rat.getType());
        }
        if (cat != null && cat.getAttack().isAttack()) {
            flashDebug(renderHandler, window, flashDebug, cat.getAttack().getKillTimer() / 2, cat.getType());
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

    public Vinnie getVinnie() {
        return vinnie;
    }
}