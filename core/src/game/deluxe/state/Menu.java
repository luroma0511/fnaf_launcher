package game.deluxe.state;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;

import game.engine.util.Engine;
import game.engine.util.RenderManager;

public class Menu {
    private byte nightSelection = 0;
    private float staticAnimation;
    private boolean loaded;

    public Menu(){

    }

    public void update(Engine engine){
        if (!loaded) {
            engine.setSpriteRequest(engine.createSpriteRequest(engine.getSpriteRequest(), "Static/Static", (short) 1024));
            engine.setSpriteRequest(engine.createSpriteRequest(engine.getSpriteRequest(), "Static/DarkStatic", (short) 1024));
            loaded = true;
        }
        if (engine.getSpriteRequest() != null) return;

        staticAnimation += engine.getDeltaTime() * 30;
        if (staticAnimation > 8 || staticAnimation < 0){
            staticAnimation = 0;
        }
    }

    public void render(RenderManager renderManager){
        Sprite sprite;

        sprite = renderManager.getSpriteManager().spriteSheetMap.get("Static/Static").getRegion((byte) staticAnimation);
        sprite.setColor(0.25f, 0, 0, 1);
        sprite.draw(renderManager.getBatch());

        int srcFunc = renderManager.getBatch().getBlendSrcFunc();
        int dstFunc = renderManager.getBatch().getBlendDstFunc();
        renderManager.getBatch().setBlendFunction(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_DST_COLOR);

        sprite = renderManager.getSpriteManager().spriteSheetMap.get("Static/DarkStatic").getRegion((byte) staticAnimation);
        sprite.setColor(1, 0, 0, 1);
        sprite.draw(renderManager.getBatch());

        renderManager.getBatch().setBlendFunction(srcFunc, dstFunc);

        renderManager.getFontManager().getCandysFont().setColor(1, 0, 0, 1);
        GlyphLayout layout = new GlyphLayout();
        layout.setText(renderManager.getFontManager().getCandysFont(), "Custom Night");

        renderManager.getFontManager().getCandysFont().draw(renderManager.getBatch(), layout, (float) renderManager.getWidth() / 2 - layout.width / 2, 660);
    }
}
