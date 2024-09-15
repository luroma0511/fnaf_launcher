package candys3.Menu.Objects;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import core.Engine;
import util.Text;
import util.*;

public class Caption extends SpriteObject {
    private boolean active;
    private short previousID;
    private short availableID;
    private String text;
    private final Map<String, CaptionData> captions;

    public Caption(){
        super(null, 0, 0, 0, 24, 0);
        availableID = 1;
        captions = new HashMap<>();
    }

    public void update(Engine engine, BitmapFont font){
        if (!active && getAlpha() > 0) {
            setAlpha(Time.decreaseTimeValue(getAlpha(), 0, 6));
            if (getAlpha() == 0) return;
        } else if (!active) return;

        if (getAlpha() < 1 && active) setAlpha(Time.increaseTimeValue(getAlpha(), 1, 6));
        if (previousID != 0 && captions.get(text) != null && previousID == captions.get(text).id()) return;
        else if (!captions.containsKey(text)) {
            var fontManager = engine.appHandler.getFontManager();
            fontManager.setCurrentFont(font);
            fontManager.setSize(15);
            String text = this.text.endsWith(".txt") ? Text.read("candys3", this.text) : this.text;
            List<String> list = wordWrap(text, 40);
            float width = 0;
            for (String caption: list) {
                fontManager.setText(caption);
                width = Math.max(width, fontManager.getLayout().width);
            }
            int height = (int) (getHeight() * list.size() + 8);
            CaptionData captionData = new CaptionData(availableID, (int) width, height, list);
            captions.put(this.text, captionData);
            availableID++;
        }
        previousID = captions.get(text).id();
    }

    public void prepare(Engine engine, FrameBuffer captionFBO) {
        if (getAlpha() == 0) return;
        captionFBO.begin();
        CaptionData captionData = captions.get(text);

        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;

        renderHandler.shapeDrawer.setColor(0, 0, 0, 0.75f);
        renderHandler.shapeDrawer.filledRectangle(0, 0, captionData.width() + 16, captionData.height());
        FrameBufferManager.end(batch, captionFBO, captionData.width() + 16, captionData.height());
    }

    public void render(Engine engine, FrameBuffer captionFBO, BitmapFont font){
        if (getAlpha() == 0) return;

        var renderHandler = engine.appHandler.getRenderHandler();
        var batch = renderHandler.batch;
        var input = engine.appHandler.getInput();
        var window = engine.appHandler.window;
        var fontManager = engine.appHandler.getFontManager();

        CaptionData captionData = captions.get(text);
        float screenAlpha = renderHandler.screenAlpha;
        TextureRegion region = new TextureRegion(captionFBO.getColorBufferTexture());
        region.setRegionWidth(captionData.width() + 16);
        region.setRegionHeight(captionData.height());
        region.flip(false, true);
        batch.setColor(1, 1, 1, screenAlpha * getAlpha());
        boolean left = input.getX() < (float) window.width() / 2;
        float y = Math.max(0, Math.min(window.height() - region.getRegionHeight(), input.getY() - region.getRegionHeight() + 16));
        if (left) batch.draw(region, input.getX() + 16, y);
        else batch.draw(region, input.getX() - region.getRegionWidth() - 16, y);

        font.setColor(1, 1, 1, screenAlpha * getAlpha());
        y = Math.max(0, input.getY() + 8);
        for (byte i = 0; i < captionData.captions().size(); i++) {
            fontManager.setText(captionData.captions().get(i));
            if (left) fontManager.setPosition(input.getX() + 24, y - getHeight() * i - 4);
            else fontManager.setPosition(input.getX() - region.getRegionWidth() - 8, y - getHeight() * i - 4);
            fontManager.render(batch);
        }
        font.setColor(1, 1, 1, 1);
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

    public void setText(String text) {
        this.text = text;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void dispose(){
        captions.clear();
    }
}
