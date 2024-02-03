package game.deluxe.state;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;

import game.deluxe.data.ChallengesData;
import game.deluxe.data.EnemyAI;
import game.engine.util.Engine;
import game.engine.util.RenderManager;

public class Menu {
    private byte nightSelection = 1;
    private float staticAnimation;
    private boolean loaded;
    private final EnemyAI enemyAI;
    private final ChallengesData challengesData;

    private float characterPanX;
    private float characterPanY;

    private boolean options;
    private float optionsAlpha;

    public Menu(){
        enemyAI = new EnemyAI();
        challengesData = new ChallengesData();
    }

    public void update(Engine engine){
        if (!loaded) {
            engine.setSpriteRequest(engine.createSpriteRequest(engine.getSpriteRequest(), "Static/Static", (short) 1024));
            engine.setSpriteRequest(engine.createSpriteRequest(engine.getSpriteRequest(), "Static/DarkStatic", (short) 1024));
            engine.setSpriteRequest(engine.createSpriteRequest(engine.getSpriteRequest(), "menu/buttons", (short) 200));
            engine.setSpriteRequest(engine.createSpriteRequest(engine.getSpriteRequest(), "menu/option", (short) 84));
            engine.setTextureRequest(engine.createTextureRequest(engine.getTextureRequest(), "menu/window"));
            engine.setTextureRequest(engine.createTextureRequest(engine.getTextureRequest(), "menu/scroll_bar"));
            engine.setTextureRequest(engine.createTextureRequest(engine.getTextureRequest(), "menu/shadow_rat"));
            engine.setTextureRequest(engine.createTextureRequest(engine.getTextureRequest(), "menu/shadow_cat"));
            loaded = true;
        }
        if (engine.getSpriteRequest() != null) return;

        float deltaTime = engine.getDeltaTime();

        staticAnimation += deltaTime * 30;
        if (staticAnimation > 8 || staticAnimation < 0){
            staticAnimation = 0;
        }

        if (options && optionsAlpha < 1) {
            optionsAlpha += deltaTime * 4;
            if (optionsAlpha > 1) optionsAlpha = 1;
        } else if (optionsAlpha > 0){
            optionsAlpha -= deltaTime * 4;
            if (optionsAlpha < 0) optionsAlpha = 0;
        }

        enemyAI.update(deltaTime);
    }

    public void render(RenderManager renderManager){
        BitmapFont candysFont = renderManager.getFontManager().getCandysFont();
        BitmapFont captionFont = renderManager.getFontManager().getCaptionFont();

        renderNightMenu(renderManager, candysFont, captionFont);
    }

    public void renderNightMenu(RenderManager renderManager, BitmapFont candysFont, BitmapFont captionFont){
        Sprite sprite = renderManager.getSpriteManager().getSpriteSheetMap().get("Static/Static").getRegion((byte) staticAnimation);
        nightSetColor(sprite, 4);
        sprite.draw(renderManager.getBatch());

        int srcFunc = renderManager.getBatch().getBlendSrcFunc();
        int dstFunc = renderManager.getBatch().getBlendDstFunc();
        renderManager.getBatch().setBlendFunction(GL20.GL_ONE_MINUS_SRC_COLOR, GL20.GL_DST_COLOR);

        sprite = renderManager.getSpriteManager().getSpriteSheetMap().get("Static/DarkStatic").getRegion((byte) staticAnimation);
        nightSetColor(sprite, 1);
        sprite.draw(renderManager.getBatch());

        renderManager.getBatch().flush();
        renderManager.getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);

        characterPanX = characterPan(characterPanX, renderManager.getInputManager().getX(), renderManager.getWidth());
        characterPanY = characterPan(characterPanY, renderManager.getInputManager().getY(), renderManager.getHeight());

        Sprite ratSprite = new Sprite(renderManager.getTextureManager().getTextureMap().get("menu/shadow_rat"));
        ratSprite.setSize(553, 553);
        ratSprite.setColor(1, 1, 1, 0.65f);
        ratSprite.setPosition(100 - characterPanX, 100 - characterPanY);
        ratSprite.draw(renderManager.getBatch());

        Sprite catSprite = new Sprite(renderManager.getTextureManager().getTextureMap().get("menu/shadow_cat"));
        catSprite.setSize(374.5f, 553);
        catSprite.setColor(1, 1, 1, 0.65f);
        catSprite.setPosition(Math.max(725, 725 - catSprite.getWidth()) - characterPanX, 100 - characterPanY);
        catSprite.draw(renderManager.getBatch());

        renderManager.getBatch().flush();
        renderManager.getBatch().setBlendFunction(srcFunc, dstFunc);

        ratSprite.setColor(1, 1, 1, enemyAI.getShadowNight_RatAlpha());
        ratSprite.draw(renderManager.getBatch());
        catSprite.setColor(1, 1, 1, enemyAI.getShadowNight_CatAlpha());
        catSprite.draw(renderManager.getBatch());

        sprite = renderManager.getSpriteManager().getSpriteSheetMap().get("menu/buttons").getRegion((byte) 0);
        nightSetColor(sprite, 2);
        sprite.setPosition(715, 24);
        sprite.draw(renderManager.getBatch());

        sprite = renderManager.getSpriteManager().getSpriteSheetMap().get("menu/buttons").getRegion((byte) 1);
        nightSetColor(sprite, 2);
        sprite.setPosition(365, 24);
        sprite.draw(renderManager.getBatch());

        if (nightSelection == 0){
            candysFont.setColor(1, 0, 0, 1);
        } else {
            candysFont.setColor(0.5f, 0, 1, 1);
        }
        renderManager.getLayout().setText(candysFont, "Shadow Night");
        candysFont.draw(renderManager.getBatch(), renderManager.getLayout(),
                (float) renderManager.getWidth() / 2 - renderManager.getLayout().width / 2, 696);

        optionsAlpha = 1;

        sprite = new Sprite(renderManager.getTextureManager().getTextureMap().get("menu/window"));
        sprite.setColor(1, 1, 1, optionsAlpha);
        sprite.setPosition(190, 144);
        sprite.draw(renderManager.getBatch());

        candysFont.setColor(1, 1, 1, optionsAlpha);
        renderManager.getLayout().setText(candysFont, "Challenges");
        candysFont.draw(renderManager.getBatch(), renderManager.getLayout(),
                (float) renderManager.getWidth() / 2 - renderManager.getLayout().width / 2, 629);

        for (int i = 0; i < 4; i++) {
            sprite = new Sprite(renderManager.getSpriteManager().getSpriteSheetMap().get("menu/option").getRegion((byte) 0));
            sprite.setPosition(236, 470 - 98 * i);
            sprite.draw(renderManager.getBatch());

            if (i < 2) {
                sprite = new Sprite(renderManager.getTextureManager().getTextureMap().get("menu/scroll_bar"));
                if (!((i == 0 && challengesData.isLaserPointer()) || challengesData.isHardCassette())) {
                    sprite.setColor(1, 1, 1, 0.25f);
                }
                sprite.setPosition(680, 496 - 98 * i);
                sprite.draw(renderManager.getBatch());

            }

            if (i == 0){
                renderManager.getLayout().setText(candysFont, "Laser Pointer");
            } else if (i == 1){
                renderManager.getLayout().setText(candysFont, "Hard Cassette");
            } else if (i == 2){
                renderManager.getLayout().setText(candysFont, "Classic Cat");
            } else {
                renderManager.getLayout().setText(candysFont, "Free Scroll");
            }
            candysFont.draw(renderManager.getBatch(), renderManager.getLayout(), 336, 525 - 98 * i);
        }

        sprite = new Sprite(renderManager.getSpriteManager().getSpriteSheetMap().get("menu/option").getRegion((byte) 2));
        short distance = 354;
        for (byte i = 0; i < 2; i++){
            if ((i == 0 && challengesData.isLaserPointer()) || challengesData.isHardCassette()) {
                sprite.setColor(1, 1, 1, 1);
            } else {
                sprite.setColor(1, 1, 1, 0.25f);
            }
            sprite.setPosition(638, 470 - 98 * i);
            sprite.draw(renderManager.getBatch());
        }

        captionFont.draw(renderManager.getBatch(),
                "Mouse: " + (int) renderManager.getInputManager().getX() + " | " + (int) renderManager.getInputManager().getY(),
                24, 696);

        captionFont.draw(renderManager.getBatch(),
                "Panning: " + characterPanX + " | " + characterPanY,
                24, 666);
    }

    private float characterPan(float value, float mouseCoord, short length){
        if (Float.isNaN(mouseCoord)) return value;
        float target = (mouseCoord - (float) length / 2) * 0.1f;
        float distance = (value - target) / 4;
        return value - distance;
    }

    private void nightSetColor(Sprite sprite, float divider){
        if (nightSelection == 0){
            sprite.setColor((float) 1 / divider, 0, 0, 1);
        } else {
            sprite.setColor(0.5f / divider, 0, (float) 1 / divider, 1);
        }
    }
}
