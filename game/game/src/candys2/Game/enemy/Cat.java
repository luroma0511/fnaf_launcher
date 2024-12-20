package candys2.Game.enemy;

import candys2.Game.mode.RatCatTheater;
import candys2.Game.player.Player;
import util.SoundHandler;
import util.TextureHandler;
import util.Time;

import java.util.HashSet;
import java.util.Set;

public class Cat {
    public int ai;
    public boolean shadow;

    public int camera;
    public int prevCamera;
    public float frame;
    public float cooldown;
    public float telephoneCooldown;
    public float deathCooldown;
    public float leaveCooldown;
    public int cameraVentCooldown;
    public float ventCooldown;
    public int vents;
    Set<Integer> reservedCameras = new HashSet<>();

    public void reset(byte ai){
        this.ai = ai;
        cameraVentCooldown = (int) (1 + Math.random() * 3);
        frame = 0;
        cooldown = 5 + 0.5f * (20 - ai);
        telephoneCooldown = 0;
        leaveCooldown = 0;
        vents = 0;
        camera = (int) (3 + Math.random() * 2);
        prevCamera = camera;
        reservedCameras.clear();
    }

    public void load(TextureHandler textureHandler, boolean shadow){
        this.shadow = shadow;
        String dir = "game/enemy/cat/";
        textureHandler.add(dir + "cameras");
        for (int i = 1; i <= 6; i++) textureHandler.add(dir + "vent/" + i);
    }

    public void update(SoundHandler soundHandler, Player player, RatCatTheater ratCatTheater, boolean noJumpscares) {
        if (ai == 0) return;

        if (cooldown > 0) {
            cooldown -= Time.getDelta();
            if (cooldown <= 0) cooldown = 0;
        }
        prevCamera = camera;
        int aiCounter = 20 - ai;
        float ventCooldownTarget = 7 + 0.5f * aiCounter;

        if (vents == 0) {
            if (player.telephone.ringing && cameraVentCooldown > 0) {
                if (player.telephone.location == camera) {
                    telephoneCooldown += Time.getDelta();
                    cooldown += Time.getDelta();
                } else telephoneCooldown = 0;

                if (telephoneCooldown >= 2) {
                    telephoneCooldown = 0;
                    if (camera <= 3) camera = 4;
                    else camera = 3;
                    cameraVentCooldown--;
                    if ((player.monitor.activeCamera == camera || player.monitor.activeCamera == prevCamera)
                            && player.inCamera) player.setSignalLost();
                    cooldown = 5 + aiCounter * 0.5f;

                    if (cameraVentCooldown == 0) cooldown = 2.5f + 0.1f * aiCounter;
                }
            } else telephoneCooldown = 0;
            if (cooldown != 0) return;
            boolean moving = false;

            if (cameraVentCooldown == 0){
                vents = 3;
                reservedCameras.clear();
                telephoneCooldown = 1;
                ventCooldown = ventCooldownTarget;
                camera = 0;
                while (camera == 0){
                    int cam = (int) (Math.random() * 6);
                    if (ratCatTheater.rat.camera == cam + 1
                            || reservedCameras.contains(cam + 1)
                            || (player.telephone.ringing && player.telephone.location == cam + 1)) continue;
                    camera = cam + 1;
                }
                if (player.monitor.activeCamera == camera
                    || player.monitor.activeCamera == prevCamera) player.setSignalLost();
                prevCamera = camera;
                soundHandler.play("ventKnocking");
                soundHandler.setSoundEffect(SoundHandler.LOOP, "ventKnocking", 1);
                soundHandler.setSoundEffect(SoundHandler.PAN, "ventKnocking", camera <= 3 ? -0.5f : 0.5f, -0.25f);
                return;
            }

            int nextCamera = camera;
            if (nextCamera <= 3) nextCamera--;
            else nextCamera++;
            if ((nextCamera != 0 && nextCamera != 7) || !noJumpscares) {
                camera = nextCamera;
                moving = true;
            }
            cooldown = 5 + aiCounter * 0.5f;
            if (camera == 0 || camera == 7) {
                camera = 0;
                player.setJumpscare("cat", 5);
            }

            if (moving && (player.monitor.activeCamera == camera || player.monitor.activeCamera == prevCamera)
                    && player.inCamera) player.setSignalLost();
        } else {
            if (leaveCooldown > 0){
                leaveCooldown -= Time.getDelta();
                if (leaveCooldown <= 0) {
                    leaveCooldown = 0;
                    vents--;
                    if (vents > 0){
                        reservedCameras.add(camera);
                        telephoneCooldown = 1;
                        ventCooldown = ventCooldownTarget;
                        camera = 0;
                        while (camera == 0){
                            int cam = (int) (Math.random() * 6);
                            if (ratCatTheater.rat.camera == cam + 1
                                    || reservedCameras.contains(cam + 1)
                                    || (player.telephone.ringing && player.telephone.location == cam + 1)) continue;
                            camera = cam + 1;
                            prevCamera = camera;
                        }
                        if (player.monitor.activeCamera == camera) player.setSignalLost();
                        soundHandler.play("ventKnocking");
                        soundHandler.setSoundEffect(SoundHandler.LOOP, "ventKnocking", 1);
                        soundHandler.setSoundEffect(SoundHandler.PAN, "ventKnocking", camera <= 3 ? -0.5f : 0.5f, -0.25f);
                    } else {
                        cameraVentCooldown = (int) (3 + (Math.random() * 3));
                        camera = (int) (1 + Math.random() * 6);
                        prevCamera = camera;
                        if (player.monitor.activeCamera == camera) player.setSignalLost();
                        cooldown = 5 + 0.5f * (20 - ai);
                    }
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
                    if (camera == player.monitor.activeCamera && player.inCamera) player.setSignalLost();
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
                    player.setJumpscare("cat", 0.5f);
                }
            }
        }
    }
}
