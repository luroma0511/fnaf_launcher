package candys2.Game.enemy;

import candys2.Game.mode.CandysShowdown;
import candys2.Game.player.Player;
import util.*;

public class Chester {
    public int ai;
    public boolean shadow;

    public int camera;
    public int prevCamera;
    public float cooldown;
    public float ventCooldown;
    public float telephoneCooldown;
    public float leaveCooldown;
    public float deathCooldown;
    public float frame;

    public void reset(int ai){
        this.ai = ai;
        leaveCooldown = 0;
        telephoneCooldown = 0;
        ventCooldown = 0;
        deathCooldown = 0;
        camera = 0;
        prevCamera = 0;
        frame = 0;
        if (ai == 0) return;

        float aiCounter = 20 - ai;
        if (shadow) cooldown = 1;
        else cooldown = 8 + 0.1f * aiCounter;
    }

    public void load(TextureHandler textureHandler, boolean shadow){
        this.shadow = shadow;
        String dir = "game/enemy/chester/";
        for (int i = 1; i <= 6; i++) textureHandler.add(dir + "camera" + i);
    }

    public void update(SoundHandler soundHandler, Player player, CandysShowdown candysShowdown, boolean noJumpscares){
        if (ai == 0) return;

        float aiCounter = 20 - ai;
        float cooldownTarget = 8 + 0.1f * aiCounter;
        if (shadow) cooldownTarget = 2;
        float ventCooldownTarget = 9 + 0.5f * aiCounter;

        if (leaveCooldown > 0){
            leaveCooldown -= Time.getDelta();
            if (leaveCooldown <= 0) {
                leaveCooldown = 0;
                cooldown = cooldownTarget;
                camera = 0;
            }
        } else if (cooldown > 0){
            cooldown -= Time.getDelta();
            if (cooldown <= 0) {
                cooldown = 0;
                telephoneCooldown = 1;
                ventCooldown = ventCooldownTarget;
                while (camera == 0){
                    int cam = (int) (Math.random() * 6);
                    if (candysShowdown.candy.camera == cam + 1
                        || candysShowdown.cindy.camera == cam + 1
                        || candysShowdown.blank.camera == cam + 1
                        || prevCamera == cam + 1
                        || (player.telephone.ringing && player.telephone.location == cam + 1)) continue;
                    camera = cam + 1;
                    prevCamera = camera;
                }
                if (camera == player.monitor.activeCamera) player.setSignalLost();
                soundHandler.play("ventKnocking");
                soundHandler.setSoundEffect(SoundHandler.LOOP, "ventKnocking", 1);
                soundHandler.setSoundEffect(SoundHandler.PAN, "ventKnocking", camera <= 3 ? -0.5f : 0.5f, -0.25f);
            }
        } else if (ventCooldown > 0){
            if (player.monitor.activeCamera == camera){
                frame += Time.getDelta() * 1.5f;
                if (frame >= 3) frame = 0;
            } else frame = 0;

            ventCooldown -= Time.getDelta();
            if (player.telephone.ringing && player.telephone.location == camera){
                if (ventCooldown < 0.5f) ventCooldown = 0.5f;
                telephoneCooldown -= Time.getDelta();
                if (telephoneCooldown <= 0){
                    telephoneCooldown = 0;
                    leaveCooldown = 0.65f;
                    frame = 0;
                    soundHandler.stop("ventKnocking");
                }
            } else telephoneCooldown = 1;

            if (ventCooldown <= 0 && !noJumpscares){
                ventCooldown = 0;
                deathCooldown = 8;
                if (camera == player.monitor.activeCamera && (int) player.roomFrame == 17) player.setSignalLost();
                camera = 0;
                soundHandler.play("ventOpen");
                soundHandler.setSoundEffect(SoundHandler.PAN, "ventOpen", camera <= 3 ? -0.5f : 0.5f, -0.25f);
                soundHandler.play("ventCrawl");
                soundHandler.setSoundEffect(SoundHandler.VOLUME, "ventCrawl", 0.4f);
                soundHandler.stop("ventKnocking");
            } else if (noJumpscares) ventCooldown = 2;
        } else if (deathCooldown > 0){
            deathCooldown -= Time.getDelta();
            if (deathCooldown <= 0) {
                deathCooldown = 0;
                soundHandler.play("ventOpen");
                player.setJumpscare("chester", 0.5f);
            }
        }
    }

    public void render(){

    }
}
