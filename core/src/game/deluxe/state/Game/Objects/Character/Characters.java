package game.deluxe.state.Game.Objects.Character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import game.deluxe.state.Game.Objects.Flashlight;
import game.engine.util.Engine;
import game.engine.util.ImageManager;
import game.engine.util.Request;

public class Characters {
    private Rat rat;
    private Cat cat;
    private Vinnie vinnie;
    private ShadowRat shadowRat;
    private ShadowCat shadowCat;

    public void reset(byte ratAI, byte catAI, byte vinnieAI, byte shadowRatAI, byte shadowCatAI) {
        if (ratAI != 0) rat = new Rat(ratAI);
        if (catAI != 0) cat = new Cat(catAI);
        if (vinnieAI != 0) vinnie = new Vinnie(vinnieAI);
        if (shadowRatAI != 0) shadowRat = new ShadowRat(shadowRatAI);
        if (shadowCatAI != 0) shadowCat = new ShadowCat(shadowCatAI);
    }

    public void load(Request request){
        if (rat != null) rat.load(request);
//        if (characters.getCat() != null) characters.getCat().load(engine);
//        if (characters.getVinnie() != null) characters.getVinnie().load(engine);
//        if (characters.getShadowRat() != null) characters.getShadowRat().load(engine);
//        if (characters.getShadowCat() != null) characters.getShadowCat().load(engine);
    }

    public void update(Engine engine, Flashlight flashlight){
        if (rat != null) {
            rat.input(engine, flashlight.getX(), flashlight.getY());
            rat.update(engine);
        }
    }

    public void render(SpriteBatch batch, ImageManager imageManager){
        TextureRegion region;
        if (rat != null){
            rat.render();
            if (rat.getDoor().getFrame() != 13) {
                if (rat.getSide() == 0){
                    region = imageManager.getRegion(getRat().getPath(), 130, (int) getRat().getDoor().getFrame());
                } else if (rat.getSide() == 1){
                    region = imageManager.getRegion(getRat().getPath(), 232, (int) getRat().getDoor().getFrame());
                } else {
                    region = imageManager.getRegion(getRat().getPath(), 105, (int) getRat().getDoor().getFrame());
                }

                batch.draw(region, getRat().getX(), getRat().getY());
            }
        }
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

    public ShadowRat getShadowRat() {
        return shadowRat;
    }

    public ShadowCat getShadowCat() {
        return shadowCat;
    }
}
