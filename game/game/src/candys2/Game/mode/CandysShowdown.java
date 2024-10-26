package candys2.Game.mode;

import candys2.Game.player.Player;
import candys2.Game.enemy.*;
import candys2.Menu.Menu;
import core.Engine;
import util.SoundHandler;
import util.TextureHandler;

public class CandysShowdown {
    public final Candy candy = new Candy();
    public final Cindy cindy = new Cindy();
    public final Chester chester = new Chester();
    public final Penguin penguin = new Penguin();
    public final Blank blank = new Blank();

    public void load(Menu menu, TextureHandler textureHandler, boolean shadow, boolean mapDebug){
        if (mapDebug) textureHandler.add("game/cheat1");
        if (menu.candy.getAi() > 0) candy.load(textureHandler);
        if (menu.cindy.getAi() > 0) cindy.load(textureHandler);
        if (menu.chester.getAi() > 0) chester.load(textureHandler, shadow);
        if (menu.penguin.getAi() > 0) penguin.load(textureHandler, shadow);
        if (menu.blank.getAi() > 0) blank.load(textureHandler, shadow);

        if (menu.candy.getAi() > 0 && menu.cindy.getAi() > 0) textureHandler.add("game/enemy/candyAndCindy/cameras");
        if (menu.cindy.getAi() > 0 && menu.blank.getAi() > 0) textureHandler.add("game/enemy/cindyAndBlank/cameras");
    }

    public void reset(Menu menu){
        candy.reset(menu.candy.getAi());
        cindy.reset(menu.cindy.getAi());
        chester.reset(menu.chester.getAi());
        penguin.reset(menu.penguin.getAi());
        blank.reset(menu.blank.getAi());
    }

    public void update(SoundHandler soundHandler, Player player, boolean laserPointer, boolean noJumpscares){
        penguin.update(player);
        blank.update(soundHandler, player, this, laserPointer, noJumpscares);
        candy.update(soundHandler, this, player, laserPointer, noJumpscares);
        cindy.update(soundHandler, player, chester, noJumpscares);
        chester.update(soundHandler, player, this, noJumpscares);

        if (player.signalLost > 0
                && (player.monitor.error || (penguin.turns == 0 && penguin.cooldown == 0)
                || player.monitor.glitchCooldown > 0)) {
            player.cancelSignalLost();
        }
    }

    public boolean renderCamera(Engine engine, int activeCamera){
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;
        var window = engine.appHandler.window;
        String path = "game/enemy/";
        float yPos = 160;
        float height = window.width() * 0.75f;

        if (penguin.cooldown == 0 && penguin.turns == 0){
            batch.draw(textureHandler.get(path + "penguin/warning"), 0, 0, window.width(), window.height());
            return true;
        }

        if (cindy.ai > 0 && candy.camera == cindy.camera && candy.camera == activeCamera){
            var region = textureHandler.getRegion(path + "candyAndCindy/cameras", 1024, activeCamera - 1);
            batch.draw(region, 0, -yPos, window.width(), height);
            return true;
        }

        if (!blank.hallRender && cindy.ai > 0 && blank.camera == cindy.camera && blank.camera == activeCamera){
            var region = textureHandler.getRegion(path + "cindyAndBlank/cameras", 1024, activeCamera - 1);
            batch.draw(region, 0, -yPos, window.width(), height);
            return true;
        }

        if (candy.camera == activeCamera){
            var region = textureHandler.getRegion(path + "candy/cameras", 1024, activeCamera - 1);
            batch.draw(region, 0, -yPos, window.width(), height);
            return true;
        }

        if (cindy.camera == activeCamera){
            var region = textureHandler.getRegion(path + "cindy/cameras", 1024, activeCamera - 1);
            batch.draw(region, 0, -yPos, window.width(), height);
            return true;
        }

        if (chester.camera == activeCamera){
            var region = textureHandler.getRegion(path + "chester/camera" + chester.camera, 1024, chester.leaveCooldown > 0 ? 3: (int) chester.frame);
            batch.draw(region, 0, -yPos, window.width(), height);
            return true;
        }

        if (blank.camera == activeCamera){
            if (blank.shadow){
                var region = textureHandler.getRegion("game/camera/cameras", 1024, activeCamera - 1);
                batch.draw(region, 0, -yPos, window.width(), height);
                region = textureHandler.getRegion(path + "blank/shadow/cameras", 1024, activeCamera - 1);
                batch.draw(region, 0, -yPos, window.width(), height);
                if (blank.turns != 0) {
                    region = textureHandler.getRegion(path + "blank/shadow/cameras_eyes", 1024, activeCamera - 1);
                    batch.draw(region, 0, -yPos, window.width(), height);
                }
            } else {
                var region = textureHandler.getRegion(path + "blank/cameras", 1024, activeCamera - 1);
                batch.draw(region, 0, -yPos, window.width(), height);
            }
            return true;
        }

        return false;
    }

    public void renderHall1(Engine engine, float offset){
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;

        String path = "game/enemy/";
        if (blank.hallRender && blank.hallPosition == 1) {
            if (blank.shadow) {
                var region = textureHandler.get(path + "blank/shadow/hall/1");
                batch.draw(region, 720 + offset, 296);
            } else {
                var region = textureHandler.get(path + "blank/hall/1");
                batch.draw(region, 724 + offset, 300);
            }
        }

        if (candy.hallPosition == 1) {
            var region = textureHandler.get(path + "candy/hall/1");
            batch.draw(region, offset + (candy.side == 0 ? 580 : 810), 292);
        }
    }

    public void renderHall2(Engine engine, float offset){
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;

        String path = "game/enemy/";
        if (candy.hallPosition == 2) {
            var region = textureHandler.get(path + "candy/hall/2");
            batch.draw(region, offset + (candy.side == 0 ? 506 : 773), 212);
        }

        if (blank.hallRender && blank.hallPosition == 2) {
            if (blank.shadow){
                var region = textureHandler.get(path + "blank/shadow/hall/2");
                batch.draw(region, 584 + offset, 212);
            } else {
                var region = textureHandler.get(path + "blank/hall/2");
                batch.draw(region, 579 + offset, 212);
            }
        }
    }

    public void renderHall3(Engine engine, float offset){
        var textureHandler = engine.appHandler.getTextureHandler();
        var batch = engine.appHandler.getRenderHandler().batch;

        String path = "game/enemy/";

        if (candy.hallPosition == 3){
            var region = textureHandler.getRegion(path + "candy/hall/front", 470,
                    candy.side);
            batch.draw(region, (candy.side == 0 ? 280 : 705) + offset, 212);
        }

        if (blank.hallRender && blank.hallPosition == 3) {
            if (blank.shadow){
                var region = textureHandler.get(path + "blank/shadow/hall/3");
                batch.draw(region, 418 + offset, 212);
            } else {
                var region = textureHandler.get(path + "blank/hall/3");
                batch.draw(region, 445 + offset, 203);
            }
        }
    }
}
