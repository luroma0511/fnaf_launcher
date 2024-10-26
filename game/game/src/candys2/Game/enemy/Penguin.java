package candys2.Game.enemy;

import candys2.Game.player.Player;
import util.SoundHandler;
import util.TextureHandler;
import util.Time;

public class Penguin {
    public int ai;

    public float cooldown;
    public int turns;
    public float reactionTimer;
    public boolean shadow;

    public void load(TextureHandler textureHandler, boolean shadow){
        this.shadow = shadow;
        String dir = "game/enemy/penguin/";
        textureHandler.add(dir + "warning");
        textureHandler.add(dir + "glitch");
    }

    public void reset(int ai){
        this.ai = ai;
        cooldown = 4 + (20 - ai);
    }

    public void update(Player player){
        if (ai == 0) return;

        if (player.monitor.error || player.monitor.glitchCooldown > 0) return;

        float aiCounter = 20 - ai;

        if (cooldown > 0){
            cooldown -= Time.getDelta();
            if (cooldown <= 0) {
                turns = (int) (1 + Math.random() * 6);
                cooldown = 0;
                reactionTimer = 0.95f + aiCounter * 0.065f;
            }
        } else {
            if (turns == 0 && player.roomFrame < 17){
                cooldown = 4 + aiCounter;
                return;
            }

            if (player.monitor.switched) {
                if (turns == 0){
                    cooldown = 4 + aiCounter;
                    return;
                }
                turns--;
            }
        }

        if (cooldown == 0 && turns == 0){
            if (reactionTimer > 0) reactionTimer -= Time.getDelta();
            if (reactionTimer <= 0){
                reactionTimer = 0;
                player.monitor.error = true;
                turns = (int) (1 + Math.random() * 6);
                cooldown = 0;
                reactionTimer = 0.85f + aiCounter * 0.075f;
            }
        }
    }

    public boolean isBlockingView(){
        return turns == 0 && cooldown == 0;
    }
}
