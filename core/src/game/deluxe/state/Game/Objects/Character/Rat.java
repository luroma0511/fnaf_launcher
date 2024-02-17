package game.deluxe.state.Game.Objects.Character;

import java.util.Random;

import game.deluxe.state.Game.Objects.Character.Attributes.Attack;
import game.deluxe.state.Game.Objects.Character.Attributes.Door;
import game.deluxe.state.Game.Objects.Character.Attributes.Hitbox;
import game.engine.util.*;

public class Rat extends SpriteObject {
    private final Random random;

    private final Hitbox hitbox;
    private final Attack attack;
    private final Door door;

    private byte ai;
    private byte roomState;
    private byte side;

    public Rat(byte ai){
        super();
        this.ai = ai;
        random = new Random();
        hitbox = new Hitbox();
        hitbox.initLeftDoor(481, 663);
        hitbox.initMiddleDoor(1531, 696);
        hitbox.initRightDoor(2525, 715);
        door = new Door(4, 4);
        attack = new Attack(1, 1.15f, 0.225f, (byte) 0, 24, 30);
    }

    public void input(Engine engine, float mx, float my){
        switch (roomState){
            case 0:
                door.input(hitbox, mx, my);
                break;
            case 1:
                attack.input(engine);
                break;
        }
    }

    public void update(Engine engine){
        switch (roomState){
            case 0:
                side = door.update(engine, hitbox, random, side);
                if (!door.isTimeUp()) return;
                transitionRoomState((byte) 1);
                break;
            case 1:
                if (roomUpdate(engine)) return;
                break;
        }
    }

    public void render(){
        if (roomState == 0) {
            if (side == 0) {
                setPath("game/Rat/Looking Away/Left");
                setX(380);
                setY(372);
            } else if (side == 1) {
                setPath("game/Rat/Looking Away/Middle");
                setX(1411);
                setY(382);
            } else {
                setPath("game/Rat/Looking Away/Right");
                setX(2483);
                setY(321);
            }
        }
    }

    private boolean roomUpdate(Engine engine){
        if (attack.update(engine)){
            transitionRoomState((byte) 2);
            return true;
        }
        return false;
    }

    public void transitionRoomState(byte roomState){
        this.roomState = roomState;
        if (roomState == 0){
            door.reset();
        } else if (roomState == 1){
            attack.reset();
        }
    }

    public void jumpscare(VideoManager videoManager){
        videoManager.setRequest("game/Rat/Jumpscare/room");
    }

    private final String[] textures = new String[]{
            "Battle/Left",
            "Battle/Middle",
            "Battle/Right",
            "Bed/LeftPeek",
            "Bed/RightPeek",
            "Leaving/Left",
            "Leaving/Right",
            "Tape/Tape",
            "Looking Away/Left",
            "Looking Away/Middle",
            "Looking Away/Right"
    };

    public void load(Request request){
        String prefix = "game/Rat/";
        for (String file: textures){
            request.addImageRequest(prefix + file);
        }
    }

    public byte getSide() {
        return side;
    }

    public Door getDoor() {
        return door;
    }
}
