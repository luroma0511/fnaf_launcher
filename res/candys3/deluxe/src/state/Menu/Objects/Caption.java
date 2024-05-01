package state.Menu.Objects;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import deluxe.Text;
import util.*;

public class Caption extends SpriteObject {
    private boolean active;
    private short previousID;
    private short ID;
    private final Map<Short, CaptionData> captions;

    public Caption(){
        super(null, 0, 0, 0, 24, 0);
        captions = new HashMap<>();
    }

    public void update(BitmapFont font){
        if (!active && getAlpha() > 0) {
            setAlpha(Time.decreaseTimeValue(getAlpha(), 0, 6));
            if (getAlpha() == 0) return;
        } else if (!active) return;

        if (getAlpha() < 1 && active) setAlpha(Time.increaseTimeValue(getAlpha(), 1, 6));
        if (previousID == ID) return;
        else if (!captions.containsKey(ID)) {
            FontManager.setFont(font);
            FontManager.setSize(15);
            String text = Text.read(ID);
            List<String> list = wordWrap(text, 40);
            float width = 0;
            for (String caption: list) {
                FontManager.setText(caption);
                width = Math.max(width, FontManager.getLayout().width);
            }
            int height = (int) (getHeight() * list.size() + 8);
            CaptionData captionData = new CaptionData((int) width, height, list);
            captions.put(ID, captionData);
        }
        previousID = ID;
    }

    public void prepare(SpriteBatch batch, FrameBuffer captionFBO, BitmapFont font) {
        if (getAlpha() == 0) return;
        captionFBO.begin();
        CaptionData captionData = captions.get(ID);

        RenderManager.shapeDrawer.setColor(0, 0, 0, 0.75f);
        RenderManager.shapeDrawer.filledRectangle(0, 0, captionData.width() + 16, captionData.height());

        font.setColor(1, 1, 1, 1);
        for (byte i = 0; i < captionData.captions().size(); i++) {
            FontManager.setText(captionData.captions().get(i));
            FontManager.render(batch, 8, getHeight() * (captionData.captions().size() - i) - 4);
        }
        FrameBufferManager.end(batch, captionFBO, captionData.width() + 16, captionData.height());
    }

    public void render(SpriteBatch batch, InputManager inputManager, FrameBuffer captionFBO, Window window){
        if (getAlpha() == 0) return;
        TextureRegion region = new TextureRegion(captionFBO.getColorBufferTexture());
        region.setRegionWidth(captions.get(ID).width() + 16);
        region.setRegionHeight(captions.get(ID).height());
        region.flip(false, true);
        batch.setColor(1, 1, 1, getAlpha());
        float y = Math.max(0, Math.min(window.height() - region.getRegionHeight(), inputManager.getY() - region.getRegionHeight() + 16));
        if (inputManager.getX() < (float) window.width() / 2) batch.draw(region, inputManager.getX() + 16, y);
        else batch.draw(region, inputManager.getX() - region.getRegionWidth() - 16, y);
    }

    public List<String> wordWrap(String text, int characterLimit) {
        List<String> captions = new ArrayList<>();
        StringBuilder sb = new StringBuilder(text);
        while (characterLimit < sb.length()){
            short index = (short) sb.lastIndexOf(" ", characterLimit);
            if (index == -1) index = (short) sb.indexOf(" ");
            captions.add(sb.substring(0, index));
            sb.delete(0, index + 1);
        }
        captions.add(sb.toString());
        return captions;
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

    public void dispose(){
        captions.clear();
    }
}
