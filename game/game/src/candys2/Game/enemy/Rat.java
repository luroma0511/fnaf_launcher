package candys2.Game.enemy;

import candys2.Game.mode.RatCatTheater;
import candys2.Game.player.Player;
import util.Hitbox;
import util.SoundHandler;
import util.TextureHandler;
import util.Time;

public class Rat {
    public int ai;
    public boolean shadow;
    public final Hitbox hitbox = new Hitbox(0);

    public int side;
    public float cooldown;
    public float hallwayCooldown;
    public float telephoneCooldown;
    public float reactionTimer;
    public float monitorCooldown;
    public boolean hall;
    public boolean waitToMove;
    public boolean waitToLeave;
    public int hallPosition;
    public int cameraLure;
    public int camera;
    public int prevCamera;
    public int flashRemaining;
    public int turns;
    public int cameraSwitches;
    public boolean error;

    public void reset(byte ai){
        this.ai = ai;
        telephoneCooldown = 0;
        camera = 0;
        prevCamera = 0;
        cameraLure = 0;
        hallPosition = 0;
        hall = false;
        hitbox.setCoord(0, 0);
        monitorCooldown = 8 + (20 - ai);
        if (ai == 0) return;

        float aiCounter = 20 - ai;
        hallwayCooldown = 10 + 0.5f * aiCounter;
        cooldown = (float) (1.25f + 1 * Math.random());
        camera = (int) (1 + Math.random() * 6);
    }

    public void load(TextureHandler textureHandler, boolean shadow){
        this.shadow = shadow;

        String dir = "game/enemy/rat/";
        textureHandler.add(dir + "cameras");
        textureHandler.add(dir + "warning");
        textureHandler.add(dir + "glitch");
        for (int i = 1; i <= 3; i++) textureHandler.add(dir + "hall/" + i);
    }

    public void monitorUpdate(Player player){
        if (player.monitor.error || player.monitor.glitchCooldown > 0) return;

        float aiCounter = 20 - ai;

        if (monitorCooldown > 0){
            monitorCooldown -= Time.getDelta();
            if (monitorCooldown <= 0) {
                turns = (int) (1 + Math.random() * 6);
                monitorCooldown = 0;
                cameraSwitches = (int) (2 + Math.random() * 2);
                reactionTimer = 1.15f + aiCounter * 0.05f;
            }
        } else {
            if (player.monitor.switched) {
                if (turns == 0 && cameraSwitches == 0){
                    monitorCooldown = 8 + aiCounter;
                    return;
                } else if (turns == 0 && cameraSwitches > 0){
                    cameraSwitches--;
                    reactionTimer = 0.75f + aiCounter * 0.05f;
                    turns++;
                }
                turns--;
            }
        }

        if (monitorCooldown == 0 && turns == 0){
            if (reactionTimer > 0 && player.inCamera) reactionTimer -= Time.getDelta();
            if (reactionTimer <= 0){
                reactionTimer = 0;
                player.monitor.error = true;
                turns = (int) (1 + Math.random() * 6);
                monitorCooldown = 0;
            }
        }
    }

    public void update(SoundHandler soundHandler, Player player, RatCatTheater ratCatTheater, boolean laserPointer, boolean noJumpscares){
        if (ai == 0) return;

        monitorUpdate(player);

        if (hallwayCooldown > 0){
            hallwayCooldown -= Time.getDelta();
            if (hallwayCooldown <= 0){
                hallwayCooldown = 0;
            }
        }

        if (cooldown > 0 && (!player.telephone.ringing || player.telephone.location != camera)){
            cooldown -= Time.getDelta();
            if (cooldown <= 0){
                cooldown = 0;
            }
        }

        float aiCounter = 20 - ai;

        if (waitToMove && player.flashAlpha == 0){
            hallPosition++;
            setHitboxes();
            waitToMove = false;
        }

        if (waitToLeave && player.flashAlpha == 0){
            hallPosition = 0;
            setHitboxes();
            waitToLeave = false;
        }

        prevCamera = camera;

        if (hall){
            if (flashRemaining > 0 && player.flashNow && (!laserPointer || hitbox.isHovered(player.flashX, player.flashY))){
                flashRemaining--;
            }

            if (player.telephone.ringing && flashRemaining == 0
                && ((side == 0 && player.telephone.location <= 3)
                || (side == 1 && player.telephone.location > 3))){
                telephoneCooldown -= Time.getDelta();
                hallwayCooldown += Time.getDelta();
                if (telephoneCooldown <= 0){
                    hallwayCooldown = 8 + 0.5f * aiCounter;
                    cooldown = (float) (1.25f + 1 * Math.random());
                    waitToLeave = player.flashAlpha > 0;
                    camera = player.telephone.location;
                    hall = false;
                    if (!waitToLeave) {
                        hallPosition = 0;
                        setHitboxes();
                    }
                    if (player.monitor.activeCamera == camera && player.inCamera) player.setSignalLost();
                }
            } else telephoneCooldown = 2;

            if (hallwayCooldown == 0) {
                if (hallPosition == 3 && !noJumpscares && player.jumpscareEnemy.isEmpty()) {
                    player.setJumpscare("rat", 0.5f);
                } else if (hallPosition < 3){
                    waitToMove = player.flashAlpha > 0;
                    hallwayCooldown = 4 + (0.5f * aiCounter);
                    if (!waitToMove) {
                        hallPosition++;
                        setHitboxes();
                    }
                }
            }
        } else {
            if (player.telephone.ringing && player.telephone.location == camera) {
                telephoneCooldown += Time.getDelta();
                if (telephoneCooldown >= 3) {
                    player.telephone.destroy(soundHandler, camera);
                    telephoneCooldown = 0;
                }
            } else telephoneCooldown -= Time.getDelta();

            if (telephoneCooldown < 0) telephoneCooldown = 0;

            if (cooldown == 0) {
                if (hallwayCooldown == 0) {
                    waitToMove = player.flashAlpha > 0;
                    telephoneCooldown = 2;
                    hallwayCooldown = 4 + (0.5f * aiCounter);
                    cameraLure = camera;
                    camera = 0;
                    hall = true;
                    side = (int) (Math.random() * 2);
                    flashRemaining = 3;
                    if (!waitToMove) {
                        hallPosition++;
                        setHitboxes();
                    }
                } else {
                    cooldown = (float) (1.25f + 1 * Math.random());
                    int nextCamera = camera;
                    if (camera == 1) nextCamera++;
                    else if (camera == 6) nextCamera--;
                    else {
                        if (Math.random() < 0.5f) nextCamera--;
                        else nextCamera++;
                    }

                    if (ratCatTheater.cat.camera != nextCamera
                            || ratCatTheater.cat.vents == 0) camera = nextCamera;
                }

                if ((player.monitor.activeCamera == camera || player.monitor.activeCamera == prevCamera)
                        && player.inCamera) player.setSignalLost();
            }
        }
    }

    public void setHitboxes(){
        if (hallPosition == 1) hitbox.setCoord(622, 502);
        else if (hallPosition == 2) hitbox.setCoord(808, 543);
        else if (hallPosition == 3) hitbox.setCoord(738, 610);

        if (hallPosition == 1) hitbox.setSize(50, 1);
        else if (hallPosition == 2) hitbox.setSize(75, 1);
        else if (hallPosition == 3) hitbox.setSize(125, 1);
        else hitbox.setCoord(0, 0);
    }
}
