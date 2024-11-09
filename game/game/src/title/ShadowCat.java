package title;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import util.TextureHandler;
import util.Time;
import util.Window;

public class ShadowCat {
    private boolean moving;
    private boolean headPositive;
    private int headFrameTarget;
    private float headFrame;
    private float headCooldown;
    private float headTimer;
    private float twitchTimer;
    private float twitchFrame;
    private boolean twitchPositive;

    public void load(TextureHandler textureHandler){
        headTimer = 3;
        headCooldown = 1;
        textureHandler.add("titleScreen/shadowcat/pose");
        textureHandler.add("titleScreen/shadowcat/twitch");
    }

    public void update(){
        if (headTimer > 0) {
            headTimer -= Time.getDelta();
            if (headTimer <= 0){
                headTimer = 0;
                twitchTimer = (float) (0.5f + 0.75f * Math.random());
            }
        }

        if (twitchTimer > 0) {
            twitchTimer -= Time.getDelta();
            if (twitchTimer <= 0){
                twitchTimer = 0;
                headTimer = (float) (2 + 2.5f * Math.random());
            }
        }

        if (headCooldown > 0){
            headCooldown -= Time.getDelta();
            if (headCooldown <= 0){
                headCooldown = (float) (0.75f + Math.random() * 1.75f);
                headPositive = Math.random() < 0.5;
                if (headPositive) {
                    headFrameTarget += 4;
                    if (headFrameTarget == 12) headFrameTarget = 0;
                } else {
                    headFrameTarget -= 4;
                    if (headFrameTarget < 0) headFrameTarget = 8;
                }
                moving = true;
            }
        }

        float time = Time.getDelta() * 40;

        if (twitchPositive){
            twitchFrame += time;
            if (twitchFrame >= 3){
                float difference = twitchFrame - 3;
                twitchFrame = 2 - difference;
                twitchPositive = false;
            }
        } else {
            twitchFrame -= time;
            if (twitchFrame < 0){
                float difference = twitchFrame - 0;
                twitchFrame = 1 - difference;
                twitchPositive = true;
            }
        }

        if (moving) {
            if (headPositive) {
                headFrame += time;
                if (headFrame >= 12) headFrame -= 12;
                if (headFrame > headFrameTarget && headFrame - headFrameTarget < 4) {
                    headFrame = headFrameTarget;
                    moving = false;
                }
            } else {
                headFrame -= time;
                if (headFrame < headFrameTarget && headFrame - headFrameTarget > -4) {
                    headFrame = headFrameTarget;
                    moving = false;
                } else if (headFrame < 0) headFrame += 12;
            }
        }
    }

    public void render(SpriteBatch batch, TextureHandler textureHandler, Window window, float xPosition){
        TextureRegion region;
        if (headTimer == 0){
            region = textureHandler.getRegion("titleScreen/shadowcat/twitch", 724, (int) twitchFrame);
        } else {
            region = textureHandler.getRegion("titleScreen/shadowcat/pose", 724, (int) headFrame);
        }
        batch.draw(region, window.width() - 688 - xPosition, 0);
    }
}
