package candys2.Game;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import core.Engine;
import util.CameraManager;

public class Game {
    public Player player = new Player();
    public boolean loaded;

    public void load(Engine engine, int mode){
        var textureHandler = engine.appHandler.getTextureHandler();
        textureHandler.add("game/office/deskOverlay");
        textureHandler.add("game/office/light");
        textureHandler.add("game/office/lookDown");
        textureHandler.add("game/office/pickupMonitor");
        if (mode == 0){

        }
//        candys2Deluxe.appHandler.soundHandler.addAll(Paths.dataPath + "game/sounds.txt");
        loaded = true;
    }

    public void update(Engine engine){
        if (!loaded) return;
        player.update(engine);
    }

    public void render(Engine engine){
        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var textureHandler = engine.appHandler.getTextureHandler();
        var window = engine.appHandler.window;
        TextureRegion region;

        //adjust camera and begin batch
        CameraManager.move(player.position, 0);
        batch.setProjectionMatrix(CameraManager.getViewport().getCamera().combined);
        renderHandler.shapeDrawer.update();
        batch.enableBlending();
        renderHandler.batchBegin();

        region = textureHandler.get("game/office/light");
        float lightAlpha = (float) Math.sin(player.flashAlpha * (Math.PI / 2));
        batch.setColor(1, 1, 1, lightAlpha);
        batch.draw(region, 0, CameraManager.getY() - 48);
        batch.setColor(1, 1, 1, 1);

        if ((int) player.roomFrame < 8) {
            region = textureHandler.getRegion("game/office/lookDown", 1440, (int) player.roomFrame);
            batch.draw(region, 0, CameraManager.getY() - 48);
        } else {
            region = textureHandler.getRegion("game/office/pickupMonitor", 1024, (int) player.roomFrame - 8);
            batch.draw(region, CameraManager.getX(), CameraManager.getY(), window.width(), window.height());
        }

        renderHandler.batchEnd();
    }
}
