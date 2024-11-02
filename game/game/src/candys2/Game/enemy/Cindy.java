package candys2.Game.enemy;

import candys2.Game.player.Player;
import util.SoundHandler;
import util.TextureHandler;
import util.Time;

public class Cindy {
    public int ai;

    public float cooldown;
    public int switchCooldown;
    public float telephoneCooldown;
    public int prevCamera;
    public int camera;

    public void reset(int ai){
        this.ai = ai;
        telephoneCooldown = 0;
        camera = 0;
        prevCamera = camera;
        if (ai == 0) return;

        camera = (int) (3 + Math.random() * 2);
        int aiCounter = 20 - ai;
        cooldown = 2 + aiCounter * 0.2f;
        switchCooldown = (int) (1 + Math.random() * 7);
    }

    public void load(TextureHandler textureHandler){
        String dir = "game/enemy/cindy/";
        textureHandler.add(dir + "cameras");
        for (int i = 1; i <= 3; i++) textureHandler.add(dir + "hallPosition/" + i);
    }

    public void update(SoundHandler soundHandler, Player player, Chester chester, boolean noJumpscares){
        if (ai == 0) return;

        if (cooldown > 0){
            cooldown -= Time.getDelta();
            if (cooldown <= 0) cooldown = 0;
        }
        prevCamera = camera;
        int aiCounter = 20 - ai;

        if (player.telephone.ringing){
            boolean cam2Lure = camera == 1 && player.telephone.location == 2;
            boolean cam3Lure = camera == 2 && player.telephone.location == 3;
            boolean cam4Lure = camera == 5 && player.telephone.location == 4;
            boolean cam5Lure = camera == 6 && player.telephone.location == 5;
            if (player.telephone.location == camera
                    || cam2Lure || cam3Lure
                    || cam4Lure || cam5Lure) {
                telephoneCooldown += Time.getDelta();
                cooldown += Time.getDelta();
            } else telephoneCooldown = 0;

            if (player.telephone.location == camera && telephoneCooldown >= 3) {
                player.telephone.destroy(soundHandler, camera);
                telephoneCooldown = 0;
            } else if (telephoneCooldown >= 2 && (cam2Lure || cam3Lure || cam4Lure || cam5Lure)){
                telephoneCooldown = 0;
                if (cam2Lure || cam3Lure) camera++;
                else camera--;
                if ((player.monitor.activeCamera == camera || player.monitor.activeCamera == prevCamera)
                        && player.inCamera) player.setSignalLost();
                if (camera == 3 || camera == 4){
                    cooldown = 2 + aiCounter * 0.2f;
                    switchCooldown = (int) (1 + Math.random() * 7);
                } else {
                    cooldown = 7 + aiCounter * 0.5f;
                }
            }
        } else telephoneCooldown = 0;
        if (cooldown != 0) return;
        boolean moving = false;

        if (switchCooldown > 0){
            int nextCamera = camera;
            if (nextCamera == 3) nextCamera = 4;
            else nextCamera = 3;
            if (chester.camera != nextCamera) {
                camera = nextCamera;
                switchCooldown--;
                if (switchCooldown == 0){
                    cooldown = 7 + aiCounter * 0.5f;
                } else {
                    cooldown = 2 + aiCounter * 0.2f;
                }
                moving = true;
            }
        } else {
            int nextCamera = camera;
            if (nextCamera <= 3) nextCamera--;
            else nextCamera++;
            if (chester.camera != nextCamera || chester.camera == 0) {
                if ((nextCamera != 0 && nextCamera != 7) || !noJumpscares) {
                    camera = nextCamera;
                    moving = true;
                }
                cooldown = 7 + aiCounter * 0.5f;
                if (camera == 0 || camera == 7){
                    camera = 0;
                    player.setJumpscare("cindy", 5);
                }
            }
        }

        if (moving && (player.monitor.activeCamera == camera || player.monitor.activeCamera == prevCamera)
                && player.inCamera) player.setSignalLost();
    }
}
