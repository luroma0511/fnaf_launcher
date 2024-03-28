package deluxe.state.Menu.Objects;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

import deluxe.Candys3Deluxe;
import util.*;

public class Caption extends SpriteObject {
    private boolean active;
    private short previousID;
    private short ID;
    private boolean change;
    private final List<String> captions;

    public Caption(){
        super(null, 0, 0, 0, 24, 0);
        captions = new ArrayList<>();
    }

    public void update(){
        if (!active && getAlpha() > 0) {
            setAlpha(Time.decreaseTimeValue(getAlpha(), 0, 6));
            if (getAlpha() == 0) return;
        } else if (!active) return;

        if (getAlpha() < 1 && active) setAlpha(Time.increaseTimeValue(getAlpha(), 1, 6));
        setX(Candys3Deluxe.inputManager.getX() + 16);
        if (previousID == ID) return;
        captions.clear();
        String text = retrieveText();
        wordWrap(text, 40);
        change = true;
        previousID = ID;
    }

    public void render(SpriteBatch batch){
        if (getAlpha() == 0) return;
        if (change) {
            float width = 0;
            for (String caption: captions) {
                FontManager.layout.setText(Candys3Deluxe.captionFont, caption);
                width = Math.max(width, FontManager.layout.width);
            }
            setWidth(width);
            change = false;
        }
        boolean leftSide = Candys3Deluxe.inputManager.getX() < (float) Candys3Deluxe.width / 2;
        float height = Math.max(0, Candys3Deluxe.inputManager.getY() - 24 * captions.size() - 4);
        float x;
        if (leftSide) x = getX() + 8;
        else x = getX() - getWidth() - 32;

        RenderManager.shapeDrawer.setColor(0, 0, 0, getAlpha() * 0.75f);
        RenderManager.shapeDrawer.filledRectangle(x, height,
                (int) getWidth() + 16, (int) getHeight() * captions.size() + 8);

        Candys3Deluxe.captionFont.setColor(1, 1, 1, getAlpha());
        for (byte i = 0; i < captions.size(); i++) {
            if (!leftSide) {
                Candys3Deluxe.captionFont.draw(batch, captions.get(i),
                        getX() - getWidth() - 24, height + getHeight() * (captions.size() - i) - 4);
            } else {
                Candys3Deluxe.captionFont.draw(batch, captions.get(i),
                        getX() + 16, height + getHeight() * (captions.size() - i) - 4);
            }
        }
    }

    public String retrieveText(){
        if (ID == 1) return Text.read("ratShadowText");
        else if (ID == 2) return Text.read("catShadowText");
        return "";
    }

    public void wordWrap(String text, int characterLimit) {
        StringBuilder sb = new StringBuilder(text);
        while (characterLimit < sb.length()){
            short index = (short) sb.lastIndexOf(" ", characterLimit);
            if (index == -1) index = (short) sb.indexOf(" ");
            captions.add(sb.substring(0, index));
            sb.delete(0, index + 1);
        }
        captions.add(sb.toString());
    }

    public void setID(short ID) {
        this.ID = ID;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
