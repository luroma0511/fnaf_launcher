package candys2.Game.enemy;

import candys2.Game.mode.CandysShowdown;
import candys2.Game.player.Player;
import util.*;

public class Blank {
    public int ai;
    public boolean shadow;
    public final Hitbox hitbox = new Hitbox(0);

    public int turns;
    public float eyeStareTimer;
    public float stareTimer;
    public float cameraTimer;
    public float hallwayCooldown;
    public float telephoneCooldown;
    public float flashCooldown;
    public int hallPosition;
    public boolean hallRender;
    public int flashRemaining;
    public int camera;
    public int prevCamera;
    public boolean waitToMove;
    public float hallVolume;

    public void reset(int ai){
        this.ai = ai;
        camera = 0;
        hallPosition = 0;
        prevCamera = camera;
        flashRemaining = 0;
        int aiCounter = 20 - ai;
        cameraTimer = 10 + 0.5f * aiCounter;
        stareTimer = 0;
        hitbox.setCoord(0, 0);
        hallRender = false;
        hallVolume = 1;
        telephoneCooldown = 0;
        eyeStareTimer = 0;
    }

    public void load(TextureHandler textureHandler, boolean shadow){
        this.shadow = shadow;
        String dir = shadow ? "game/enemy/blank/shadow/" : "game/enemy/blank/";
        textureHandler.add(dir + "cameras");
        if (shadow) textureHandler.add(dir + "cameras_eyes");
        for (int i = 1; i <= 3; i++) textureHandler.add(dir + "hall/" + i);
    }

    public void update(SoundHandler soundHandler, Player player, CandysShowdown candysShowdown, boolean laserPointer, boolean noJumpscares){
        if (ai == 0) return;

        var candy = candysShowdown.candy;
        var chester = candysShowdown.chester;
        var penguin = candysShowdown.penguin;

        while (camera == 0 && cameraTimer > 0){
            int cam = (int) (Math.random() * 6);
            if (candy.camera == cam + 1
                    || chester.camera == cam + 1
                    || prevCamera == cam + 1
                    || (player.telephone.ringing && player.telephone.location == cam + 1)) continue;
            camera = cam + 1;
        }

        if (waitToMove && player.flashAlpha == 0){
            hallRender = true;
            hallPosition++;
            setHitboxes();
            waitToMove = false;
        }

        float aiCounter = 20 - ai;

        if (cameraTimer > 0){
            //camera logic

            if (shadow && player.monitor.switched) {
                if (turns == 0) turns = 3;
                if (player.monitor.activeCamera == camera) turns--;
                if (turns == 0 && penguin.isBlockingView()) turns++;
            }

            float cameraTimerTarget = 10 + 0.5f * aiCounter;

            if (player.inCamera && player.monitor.activeCamera == camera){
                if (turns == 0 && shadow && !penguin.isBlockingView()) {
                    if (!soundHandler.isPlaying("shadowblank")){
                        soundHandler.play("shadowblank");
                    }
                    eyeStareTimer += Time.getDelta();
                } else if (!penguin.isBlockingView()) {
                    stareTimer += Time.getDelta();
                    cameraTimer += Time.getDelta() * 1.5f;
                    if (cameraTimer >= cameraTimerTarget) {
                        cameraTimer = cameraTimerTarget;
                    }
                }
                if (stareTimer >= 2) {
                    stareTimer = 0;
                    prevCamera = camera;
                    camera = 0;
                    player.setSignalLost();
                    return;
                }
            } else {
                if (soundHandler.isPlaying("shadowblank")){
                    soundHandler.stop("shadowblank");
                }
                eyeStareTimer -= Time.getDelta();
                if (eyeStareTimer < 0) eyeStareTimer = 0;
                stareTimer -= Time.getDelta() / 2;
                if (stareTimer < 0) stareTimer = 0;
                cameraTimer -= Time.getDelta();
                if (cameraTimer <= 0){
                    cameraTimer = 0;
                }
            }

            if (player.telephone.ringing && player.telephone.location == camera){
                telephoneCooldown += Time.getDelta();
            } else telephoneCooldown = 0;

            if (eyeStareTimer >= 1 || telephoneCooldown >= 2){
                telephoneCooldown = 0;
                player.setSignalLost();
                soundHandler.stop("shadowblank");
                player.telephone.destroy(soundHandler, camera);
            }

            if (cameraTimer <= 0) {
                transitionToHall(player);
                soundHandler.play("blank");
            }
        } else if (hallPosition > 0 && hallwayCooldown > 0){
            //hallway logic

            float flashCooldownAI = 0.8f + 0.05f * aiCounter;
            if (shadow) flashCooldownAI = 0.6f;
            if (player.flashAlpha > 0){
                float x = player.flashX;
                float y = player.flashY;
                if (player.flashNow &&
                        (!laserPointer || hitbox.isHovered(x, y))) {
                    flashRemaining--;
                    flashCooldown += flashCooldownAI;
                }
            } else {
                if (flashRemaining == 0){
                    hallPosition = 0;
                    hallRender = false;
                    cameraTimer = 10 + (20 - ai);
                    stareTimer = 0;
                    return;
                }
            }

            if (flashCooldown > 0){
                flashCooldown -= Time.getDelta();
                if (flashCooldown < 0) flashCooldown = 0;
            }
            if (flashCooldown == 0) hallwayCooldown -= Time.getDelta();

            if ((hallwayCooldown <= 6 && hallPosition == 1)
                || (hallwayCooldown <= 3 && hallPosition == 2)) {
                waitToMove = player.flashAlpha > 0;
                if (!waitToMove) {
                    hallPosition++;
                    setHitboxes();
                }
            }

            if (hallwayCooldown <= 0 && !noJumpscares) {
                player.setJumpscare("blank", 0.5f);
                hallwayCooldown = 0;
            }
            else if (noJumpscares) hallwayCooldown = 0.25f;
        }

        if (flashRemaining == 0 && soundHandler.isPlaying("blank")){
            hallVolume -= Time.getDelta() * 1.5f;
            if (hallVolume <= 0) {
                hallVolume = 0;
                soundHandler.stop("blank");
            }
        }
        soundHandler.setSoundEffect(SoundHandler.VOLUME, "blank", hallVolume * (0.1f + (0.5f * (1 - hallwayCooldown / 9))));
    }

    private void transitionToHall(Player player){
        camera = 0;
        hallVolume = 1;
        flashRemaining = 6;
        hallwayCooldown = 9;
        cameraTimer = 0;
        waitToMove = player.flashAlpha > 0;
        flashCooldown = 0;
        if (!waitToMove) {
            hallPosition++;
            setHitboxes();
            hallRender = true;
        }
    }

    public void setHitboxes(){
        if (!shadow) {
            if (hallPosition == 1) hitbox.setCoord(772, 511);
            else if (hallPosition == 2) hitbox.setCoord(710, 607);
            else if (hallPosition == 3) hitbox.setCoord(758, 634);
        } else {
            if (hallPosition == 1) hitbox.setCoord(773, 513);
            else if (hallPosition == 2) hitbox.setCoord(713, 613);
            else if (hallPosition == 3) hitbox.setCoord(758, 631);
        }

        if (hallPosition == 1) hitbox.setSize(50, 1);
        else if (hallPosition == 2) hitbox.setSize(75, 1);
        else if (hallPosition == 3) hitbox.setSize(125, 1);
        else hitbox.setCoord(0, 0);
    }
}
