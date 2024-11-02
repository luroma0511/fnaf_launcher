package candys2.Game.enemy;

import candys2.Game.mode.CandysShowdown;
import candys2.Game.player.Player;
import util.Hitbox;
import util.SoundHandler;
import util.TextureHandler;
import util.Time;

public class Candy {
    public int ai;
    public boolean shadow;
    public final Hitbox hitbox = new Hitbox(0);

    public float hallwayCooldown;
    public float cooldown;
    public float telephoneCooldown;
    public int side;
    public boolean hall;
    public int hallPosition;
    public int prevCamera;
    public int camera;
    public boolean waitToMove;
    public boolean waitToLeave;
    public boolean flashed;

    public void reset(int ai){
        this.ai = ai;
        telephoneCooldown = 0;
        camera = 0;
        prevCamera = 0;
        hallPosition = 0;
        hall = false;
        flashed = false;
        hitbox.setCoord(0, 0);
        if (ai == 0) return;

        float aiCounter = 20 - ai;
        hallwayCooldown = 8 + 0.5f * aiCounter;
        cooldown = (float) (2 + 2 * Math.random());
        camera = (int) (1 + Math.random() * 6);
    }

    public void load(TextureHandler textureHandler){
        String dir = "game/enemy/candy/";
        textureHandler.add(dir + "cameras");
        for (int i = 1; i <= 2; i++) textureHandler.add(dir + "hall/" + i);
        textureHandler.add(dir + "hall/front");
    }

    public void update(SoundHandler soundHandler, CandysShowdown candysShowdown, Player player, boolean laserPointer, boolean noJumpscares){
        if (ai == 0) return;

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

        if (camera != 0){
            if (camera <= 3) side = 0;
            else side = 1;
        }

        prevCamera = camera;

        if (hall){
            //hallway logic
            if (!flashed) flashed = player.flashNow && (!laserPointer || hitbox.isHovered(player.flashX, player.flashY));

            int telephoneSide = player.telephone.location <= 3 ? 0 : 1;
            if (player.telephone.ringing && side == telephoneSide && flashed){
                telephoneCooldown -= Time.getDelta();
                hallwayCooldown += Time.getDelta();
                if (telephoneCooldown <= 0){
                    hallwayCooldown = 8 + 0.5f * aiCounter;
                    cooldown = (float) (2 + 2 * Math.random());
                    waitToLeave = player.flashAlpha > 0;
                    camera = player.telephone.location;
                    hall = false;
                    flashed = false;
                    if (!waitToLeave) {
                        hallPosition = 0;
                        setHitboxes();
                    }
                    if (player.monitor.activeCamera == camera && player.inCamera) player.setSignalLost();
                }
            } else telephoneCooldown = 2;

            if (hallwayCooldown == 0) {
                if (hallPosition == 3 && !noJumpscares && player.jumpscareEnemy.isEmpty()) {
                    player.setJumpscare("candy", 0.5f);
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
            //camera logic
            if (player.telephone.ringing && player.telephone.location == camera) {
                telephoneCooldown += Time.getDelta();
                if (telephoneCooldown >= 3) {
                    player.telephone.destroy(soundHandler, camera);
                    telephoneCooldown = 0;
                }
            } else telephoneCooldown -= Time.getDelta();

            if (telephoneCooldown < 0) telephoneCooldown = 0;

            if (cooldown == 0){
                if (hallwayCooldown == 0){
                    waitToMove = player.flashAlpha > 0;
                    telephoneCooldown = 2;
                    hallwayCooldown = 4 + (0.5f * aiCounter);
                    camera = 0;
                    side = (int) (Math.random() * 2);
                    hall = true;
                    if (!waitToMove) {
                        hallPosition++;
                        setHitboxes();
                    }
                } else {
                    cooldown = (float) (2 + 1.5f * Math.random());

                    int nextCamera = camera;
                    if (camera == 1) nextCamera++;
                    else if (camera == 6) nextCamera--;
                    else {
                        if (Math.random() < 0.5f) nextCamera--;
                        else nextCamera++;
                    }

                    if (candysShowdown.blank.camera != nextCamera
                            && candysShowdown.chester.camera != nextCamera) camera = nextCamera;
                }

                if ((player.monitor.activeCamera == camera || player.monitor.activeCamera == prevCamera)
                        && player.inCamera) player.setSignalLost();
            }
        }
    }

    public void setHitboxes(){
        if (hallPosition == 1) {
            if (side == 0) hitbox.setCoord(631, 488);
            else hitbox.setCoord(862, 488);
        } else if (hallPosition == 2) {
            if (side == 0) hitbox.setCoord(602, 509);
            else hitbox.setCoord(869, 509);
        } else if (hallPosition == 3) {
            if (side == 0) hitbox.setCoord(531, 635);
            else hitbox.setCoord(956, 635);
        }

        if (hallPosition == 1) hitbox.setSize(50, 1);
        else if (hallPosition == 2) hitbox.setSize(75, 1);
        else if (hallPosition == 3) hitbox.setSize(125, 1);
        else hitbox.setCoord(0, 0);
    }
}
