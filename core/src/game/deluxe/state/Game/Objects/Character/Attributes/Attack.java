package game.deluxe.state.Game.Objects.Character.Attributes;

import game.engine.util.Engine;

public class Attack {
    private float killTimer;
    private float flashTime;
    private float twitch;
    private float delayMovement;
    private byte teleports;
    private float movementFrame;
    private byte attackPosition;
    private byte previousAttackPosition;

    private final float initialKillTimer;
    private final float initialFlashTime;
    private final float initialDelayMovement;
    private final byte initialTeleports;
    private final float twitchSpeed;
    private final float moveSpeed;

    public Attack(float initialKillTimer, float initialFlashTime, float initialDelayMovement, byte initialTeleports, float twitchSpeed, float moveSpeed){
        this.initialKillTimer = initialKillTimer;
        this.initialFlashTime = initialFlashTime;
        this.initialDelayMovement = initialDelayMovement;
        this.initialTeleports = initialTeleports;
        this.twitchSpeed = twitchSpeed;
        this.moveSpeed = moveSpeed;
    }

    public void reset() {
        killTimer = initialKillTimer;
        flashTime = initialFlashTime;
        delayMovement = initialDelayMovement;
        teleports = initialTeleports;
        movementFrame = 0;
        twitch = 0;
        attackPosition = 0;
        previousAttackPosition = attackPosition;
    }

    public void input(Engine engine){

    }

    public boolean update(Engine engine){

        return false;
    }
}
