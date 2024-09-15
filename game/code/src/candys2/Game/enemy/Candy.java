package candys2.Game.enemy;

import util.Time;

public class Candy {
    public int ai;

    public float hallwayCooldown;
    public float cooldown;
    public float telephoneCooldown;
    public int switchCooldown;
    public int state;
    public int side;
    public int hall;

    public void reset(int ai){
        this.ai = ai;
        //assume that his ai is 20
        hallwayCooldown = 15;
        cooldown = 0;
        switchCooldown = 0;
        state = (int) (Math.random() * 3 + 1);
        hall = 0;
        side = (int) (Math.random() * 2);
        telephoneCooldown = 0;
    }

    public void update(){
        if (hallwayCooldown > 0){
            hallwayCooldown -= Time.getDelta();
            if (hallwayCooldown <= 0){
                hallwayCooldown = 0;
            }
        }

        if (state == 0){
            //hallway logic
            if (hallwayCooldown == 0){
                if (switchCooldown == 0){
                    side = side == 0 ? 1 : 0;
                    switchCooldown = (int) (Math.random() * 3);
                } else {
                    switchCooldown--;
                }
                telephoneCooldown = 0;
            }
        } else {
            //camera logic
            if (cooldown == 0){
                if (hallwayCooldown == 0){
                    hallwayCooldown = 5;
                    state = 0;
                } else {
                    cooldown = (float) (2 + 2 * Math.random());
                }
            }
        }
    }
}
