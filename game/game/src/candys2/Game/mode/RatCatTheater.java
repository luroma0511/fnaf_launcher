package candys2.Game.mode;

import candys2.Game.enemy.Cat;
import candys2.Game.enemy.Rat;
import candys2.Game.enemy.Vinnie;
import candys2.Game.player.Player;
import candys2.Menu.Menu;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.Engine;
import util.SoundHandler;
import util.TextureHandler;

public class RatCatTheater {
    public Rat rat = new Rat();
    public Cat cat = new Cat();
    public Vinnie vinnie = new Vinnie();

    public void load(Menu menu, TextureHandler textureHandler, boolean shadow, boolean mapDebug){
        if (mapDebug) textureHandler.add("game/cheat2");
        if (menu.rat.getAi() > 0) rat.load(textureHandler, shadow);
        if (menu.cat.getAi() > 0) cat.load(textureHandler, shadow);
        if (menu.rat.getAi() > 0 && menu.cat.getAi() > 0) textureHandler.add("game/enemy/ratAndCat/cameras");
    }

    public void reset(Menu menu){
        rat.reset(menu.rat.getAi());
        cat.reset(menu.cat.getAi());
    }

    public void update(SoundHandler soundHandler, Player player, boolean laserPointer, boolean noJumpscares){
        rat.update(soundHandler, player, this, laserPointer, noJumpscares);
        cat.update(soundHandler, player, this, noJumpscares);

        if (player.signalLost > 0
                && (player.monitor.error || (rat.turns == 0 && rat.monitorCooldown == 0)
                || player.monitor.glitchCooldown > 0)) {
            player.cancelSignalLost();
        }
    }

    public boolean renderCamera(Engine engine, int activeCamera){
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;
        String path = "game/enemy/";
        int width = 812;
        int height = 609;
        int x = 234;
        int y = 20;

        if (rat.ai > 0 && rat.monitorCooldown == 0 && rat.turns == 0){
            batch.draw(textureHandler.get(path + "rat/warning"), x, y, width, height);
            return true;
        }

        if (cat.ai > 0 && rat.camera == cat.camera && rat.camera == activeCamera){
            var region = textureHandler.getRegion(path + "ratAndCat/cameras", 1024, activeCamera - 1);
            batch.draw(region, x, y, width, height);
            return true;
        }

        if (rat.camera == activeCamera){
            var region = textureHandler.getRegion(path + "rat/cameras", 1024, activeCamera - 1);
            batch.draw(region, x, y, width, height);
            return true;
        }

        if (cat.camera == activeCamera){
            TextureRegion region;
            if (cat.vents == 0) region = textureHandler.getRegion(path + "cat/cameras", 1024, activeCamera - 1);
            else region = textureHandler.getRegion(path + "cat/vent/" + cat.camera, 1024, cat.leaveCooldown > 0 ? 3: (int) cat.frame);
            batch.draw(region, x, y, width, height);
            return true;
        }

        return false;
    }

    public void renderHall1(Engine engine, float offset){
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;

        String path = "game/enemy/";

        if (rat.hallPosition == 1) {
            var region = textureHandler.get(path + "rat/hall/1");
            batch.draw(region, offset, 0);
        }
    }

    public void renderHall2(Engine engine, float offset){
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;

        String path = "game/enemy/";
        if (rat.hallPosition == 2) {
            var region = textureHandler.get(path + "rat/hall/2");
            batch.draw(region, offset, 0);
        }
    }

    public void renderHall3(Engine engine, float offset) {
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;

        String path = "game/enemy/";

        if (rat.hallPosition == 3) {
            var region = textureHandler.get(path + "rat/hall/3");
            batch.draw(region, offset, 0);
        }
    }
}
