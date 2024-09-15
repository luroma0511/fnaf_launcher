package util.gamejolt;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import util.*;

public class GamejoltTrophyUI {
    private Trophy trophy;
    private Texture ui;
    private Texture outline;
    private Texture image;
    private float frame;
    private float cooldown;
    private float alpha;
    
    public void update(String game, SoundHandler soundHandler, GamejoltManager gamejoltManager, JSONHandler jsonHandler){
        if (ui == null) ui = new Texture("assets/trophy/ui.png");
        if (outline == null) outline = new Texture("assets/trophy/outline.png");

        if (!gamejoltManager.trophy.isIDsEmpty() && !gamejoltManager.threadRunning()) gamejoltManager.execute(() -> {
            gamejoltManager.trophy.add(gamejoltManager);
            if (gamejoltManager.trophy.getNewTrophies().isEmpty()) return;
            if (gamejoltManager.trophy.fetch(gamejoltManager,
                    jsonHandler, gamejoltManager.trophy.getNewTrophies().getFirst()))
                gamejoltManager.trophy.getNewTrophies().removeFirst();
        });

        if (frame == 0 && cooldown == 0 && !gamejoltManager.trophy.getUnlockedTrophies().isEmpty()) {
            trophy = gamejoltManager.trophy.retrieveNext();
            cooldown = 4.25f;
            alpha = 0;
        }
        if (trophy != null) {
            cooldown = Time.decreaseTimeValue(cooldown, 0, 1);
            if (cooldown > 0) {
                if (cooldown <= 4) {
                    if (frame == 0) {
                        soundHandler.load("assets/trophy/", "GamejoltTrophy");
                        image = new Texture("assets/trophy/" + game + "/" + trophy.getID() + ".png");
                        soundHandler.play("GamejoltTrophy");
                        soundHandler.setSoundEffect(Constants.VOLUME, "GamejoltTrophy", 0.375f);
                    }
                    frame = Time.increaseTimeValue(frame, 1, 4);
                }
                if (cooldown <= 3.175f) alpha = Time.increaseTimeValue(alpha, 1, 3);
            } else frame = Time.decreaseTimeValue(frame, 0, 4);
        }
    }

    public void render(SpriteBatch batch, FontManager fontManager, BitmapFont font){
        if (frame == 0) return;
        float trophyX = CameraManager.getX() + 1016 + (264 * (1 - frame));
        float trophyY = CameraManager.getY() + 596;
        batch.setColor(1, 1, 1, 1);
        batch.draw(ui, trophyX, trophyY);
        if (trophy.getDifficulty().equals("Silver")) batch.setColor(192f/255, 192f/255, 192f/255, 1);
        else if (trophy.getDifficulty().equals("Gold")) batch.setColor(1, 1, 0, 1);
        else batch.setColor(110f/255, 77f/255, 37f/255, 1);
        batch.draw(outline, trophyX, trophyY);
        if (alpha > 0) {
            batch.setColor(1, 1, 1, alpha);
            image.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            batch.draw(image, trophyX + 3, trophyY + 3, 58, 58);
        }
        batch.setColor(1, 1, 1, 1);
        font.setColor(1, 1, 1, 1);
        fontManager.setCurrentFont(font);
        fontManager.setSize(13);
        fontManager.setText(trophy.getDifficulty() + " Trophy Unlocked!");
        fontManager.setPosition(true, true ,
                trophyX + (float) ui.getWidth() * 0.625f,
                trophyY + (float) ui.getHeight() / 2 + 12);
        fontManager.render(batch);

        fontManager.setText(trophy.getTitle());
        fontManager.setPosition(true, true ,
                trophyX + (float) ui.getWidth() * 0.62f,
                trophyY + (float) ui.getHeight() / 2 - 12);
        fontManager.render(batch);
    }
}