package candys3.Game.Objects.Character;

import candys3.Game.Objects.Flashlight;
import candys3.Game.Objects.Functions.*;
import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import candys3.GameData;
import util.Hitbox;
import util.SoundHandler;
import util.SpriteObject;
import util.Time;

import java.util.Random;

public class Rat extends SpriteObject {
    private final Random random;

    private final Hitbox hitbox;
    private final Attack attack;
    private Door door;
    private final Bed bed;
    private final Peek peek;
    private final Leave leave;
    private final Twitch twitch;
    private final Tape tape;

    private float transitionCooldown;
    private float hellCooldown;

    private final String name;
    private final byte type;
    private boolean hellStart;
    private byte state;
    private byte side;

    public Rat(byte type){
        super();
        name = type == 0 ? "Rat" : "Shadow Rat";
        this.type = type;
        random = new Random();
        twitch = new Twitch();
        tape = new Tape();
        hitbox = new Hitbox(100);
        if (type == 0) door = new Door(6);
        else if (type == 1) door = new Door(3.5f);
        attack = new Attack();
        bed = new Bed();
        peek = new Peek();
        leave = new Leave(1.25f);
        if (type == 0) door.reset(5, 3);
        else if (type == 1) door.reset(5, 2);
        else {
            state = 1;
            side = (byte) (random.nextInt(2) * 2);
            attack.reset(0.375f, 15, 3, (byte) 4, (byte) 3, (byte) 0, 24);
            changePath();
            attack.setKillTimer(4);
        }
    }

    public boolean execute(SoundHandler soundHandler, Player player, Cat cat, Room room, boolean twitching){
        var flashlight = player.getFlashlight();
        twitching = input(soundHandler, cat, player, room, flashlight.getX(), flashlight.getY(), twitching);
        if (GameData.expandedPointer) hitbox.setSize(hitbox.size, 0.8f);
        twitching = update(soundHandler, cat, player, room, flashlight, twitching);
        return twitching;
    }

    private boolean input(SoundHandler soundHandler, Cat cat, Player player, Room room, float mx, float my, boolean twitching){
        boolean hovered = hitbox.isHovered(mx, my);
        boolean imageHovered = this.mouseOverWithPanning(mx, my);
        switch (state){
            case 0:
                if (door.input(soundHandler, room, hovered)) {
                    if (cat != null && cat.getState() == 1) cat.getAttack().setReactionTimer(1);
                    hitbox.setCoord(0, 0);
                }
                break;
            case 1:
                if (attack.input(soundHandler, player, room, imageHovered, hovered, 0, type == 2)) hitbox.setCoord(0, 0);
                if (room.getFrame() == 0 && room.getState() == 0 && twitch.update(hovered)) twitching = true;
                break;
            case 2:
                bed.input(soundHandler, player, room, imageHovered);
                break;
            case 3:
                if (peek.input(soundHandler, player, room, hovered, 1.25f) && type == 2) {
                    transitionCooldown = 1.15f;
                    player.setBlacknessDelay(1.25f);
                    attack.stopAudio(soundHandler);
                }
                if (room.getFrame() == 0 && room.getState() == 0 && twitch.update(hovered)) twitching = true;
                break;
        }
        return twitching;
    }

    private boolean update(SoundHandler soundHandler, Cat cat, Player player, Room room, Flashlight flashlight, boolean twitching){
        if (!hellStart && type == 2){
            soundHandler.play("get_in");
            player.setBlacknessTimes(2);
            player.setBlacknessSpeed(6);
            hellStart = true;
        }
        switch (state){
            case 0:
                door.update(soundHandler, player, side);
                boolean catInRoom = cat != null && cat.getState() == 1;
                if (door.isSignal()) {
                    boolean catLeaving = cat != null && cat.getState() == 4;
                    if (catInRoom) {
                        door.check();
                        if (cat.getSide() == 0 || cat.getSide() == 2){
                            if (side == 1) door.setSameSide((byte) (door.getSameSide() + 1));
                            side = 1;
                        } else if (cat.getSide() == 1){
                            byte previousSide = side;
                            if (door.getSameSide() < 3) side = (byte) (2 * random.nextInt(2));
                            else if (side == 0) side = 2;
                            else side = 0;
                            if (previousSide == side) door.setSameSide((byte) (door.getSameSide() + 1));
                        }
                    } else if (door.getSameSide() >= 3) {
                        door.setSameSide((byte) 0);
                        int rand = random.nextInt(2);
                        if (side == 0) {
                            if (catLeaving){
                                if (cat.getSide() == 1) side = 2;
                                else side = 1;
                            } else side = (byte) (1 + rand);
                        } else if (side == 1) {
                            if (catLeaving){
                                if (cat.getSide() == 0) side = 2;
                                else side = 0;
                            } else side = (byte) (2 * rand);
                        } else {
                            if (catLeaving){
                                if (cat.getSide() == 0) side = 1;
                                else side = 0;
                            } else side = (byte) rand;
                        }
                    } else {
                        byte previousSide = side;
                        if (!catLeaving) side = (byte) random.nextInt(3);
                        else {
                            int rand = random.nextInt(2);
                            if (cat.getSide() == 0) side = (byte) (rand + 1);
                            else if (cat.getSide() == 1) side = (byte) (rand * 2);
                            else side = (byte) rand;
                        }
                        if (previousSide == side) door.setSameSide((byte) (door.getSameSide() + 1));
                    }
                    if (side == 0) hitbox.setCoord(481, 663);
                    else if (side == 1) hitbox.setCoord(1531, 696);
                    else hitbox.setCoord(2525, 715);
                    changePath();
                }
                if (!door.isTimeUp()) return twitching;
                transitionRoomState((byte) 1);
                soundHandler.play("get_in");
                player.setBlacknessTimes(2);
                player.setBlacknessSpeed(6);
                break;
            case 1:
                if (((attack.getKillTimer() == 0 && attack.getReactionTimer() == 0)
                        || (cat != null && cat.getType() != 0 && ((cat.getState() == 1 && cat.getSide() == side)
                        || (attack.isAttack() && cat.getAttack().isAttack()))))
                        && !GameData.noJumpscares){
                    player.setJumpscare();
                    if (type == 0) Jumpscare.set("game/Rat/Jumpscare/room");
                    else if (type == 1) {
                        if (!GameData.classicJumpscares) Jumpscare.set("game/Shadow Rat/Jumpscare", 0.75f);
                        else Jumpscare.classicSet("game/Shadow Rat/Classic/Jumpscare", 1.9f, 160, 960, 20);
                    }
                    else Jumpscare.set("game/Shadow Rat/HellJumpscare");
                    return twitching;
                }
                if (type == 2 && hellCooldown > 0) hellCooldown = Time.decreaseTimeValue(hellCooldown, 0, 1);
                if (roomUpdate(soundHandler, cat, player) || attack.notMoved()){
                    if (attack.isSignal() && attack.notMoved()){
                        twitching = false;
                        attack.setSignal();
                    }
                    return twitching;
                }
                if (side == 0) {
                    if (attack.getPosition() == 0) hitbox.setCoord(376, 765);
                    else if (attack.getPosition() == 1) hitbox.setCoord(289, 621);
                    else hitbox.setCoord(470, 595);
                } else if (side == 1) {
                    if (attack.getPosition() == 0) hitbox.setCoord(1521, 720);
                    else if (attack.getPosition() == 1) hitbox.setCoord(1315, 574);
                    else hitbox.setCoord(1686, 604);
                } else {
                    if (attack.getPosition() == 0) hitbox.setCoord(2560, 869);
                    else if (attack.getPosition() == 1) hitbox.setCoord(2402, 680);
                    else hitbox.setCoord(2734, 651);
                }
                if (side == 0) hitbox.setSize(75, GameData.hitboxMultiplier);
                else hitbox.setSize(100, GameData.hitboxMultiplier);
                attack.setMoved();
                break;
            case 2:
                if (bed.killTime() && !GameData.noJumpscares) {
                    player.setJumpscare();
                    if (type == 0) Jumpscare.set("game/Rat/Jumpscare/bed");
                    else if (type == 1) {
                        if (!GameData.classicJumpscares) Jumpscare.set("game/Shadow Rat/Jumpscare", 0.75f);
                        else Jumpscare.classicSet("game/Shadow Rat/Classic/Jumpscare", 1.9f, 160, 960, 20);
                    }
                    else Jumpscare.set("game/Shadow Rat/HellJumpscare");
                    return twitching;
                }
                if (tape.update(room)) room.setTapeWeasel();
                if (!bed.update(player, room)) return twitching;
                if (room.getState() == 0 && ((player.getSide() == 0 && side == 2) || (player.getSide() == 2 && side == 0))) {
                    soundHandler.play("peek");
                    if (type == 2) {
                        state = 1;
                        attack.reset(0.375f, 15, 3, (byte) 4, (byte) 3, (byte) 0, 24);
                        changePath();
                        attack.setKillTimer(2.5f);
                    } else transitionRoomState((byte) 3);
                    break;
                }
                if (GameData.noJumpscares) break;
                player.setJumpscare();
                if (type == 0) Jumpscare.set("game/Rat/Jumpscare/bed");
                else if (type == 1) {
                    if (!GameData.classicJumpscares) Jumpscare.set("game/Shadow Rat/Jumpscare", 0.75f);
                    else Jumpscare.classicSet("game/Shadow Rat/Classic/Jumpscare", 1.9f, 160, 960, 20);
                }
                else Jumpscare.set("game/Shadow Rat/HellJumpscare");
                break;
            case 3:
                if (transitionCooldown != 0) {
                    transitionCooldown = Time.decreaseTimeValue(transitionCooldown, 0, 1);
                    if (transitionCooldown <= 0.75f && player.isScared()) {
                        soundHandler.play("crawl");
                        hitbox.setCoord(0, 0);
                        player.setScared();
                    }
                    if (transitionCooldown == 0) {
                        if (cat == null || cat.getState() != 2) side = (byte) (random.nextInt(2) * 2);
                        else if (cat.getSide() == 0) side = 2;
                        else side = 0;
                        player.setFreeze();
                        player.setBlacknessSpeed(1);
                        state = 2;
                        bed.reset(30, 1);
                        tape.reset(random, 1);
                        changePath();
                        break;
                    }
                    break;
                }
                if (!peek.update()) {
                    if (peek.notKillTime() || GameData.noJumpscares) return twitching;
                    player.setJumpscare();
                    if (type == 0) Jumpscare.set("game/Rat/Jumpscare/bed");
                    else if (type == 1) {
                        if (!GameData.classicJumpscares) Jumpscare.set("game/Shadow Rat/Jumpscare", 0.75f);
                        else Jumpscare.classicSet("game/Shadow Rat/Classic/Jumpscare", 1.9f, 160, 960, 20);
                    }
                    else Jumpscare.set("game/Shadow Rat/HellJumpscare");
                    return twitching;
                }
                hitbox.setCoord(0, 0);
                transitionRoomState((byte) 4);
                soundHandler.play("leave");
                player.setY(220);
                if (side == 0){
                    player.setX(4);
                    flashlight.setCoord(398 + player.getX(), 412 + player.getY());
                } else {
                    player.setX(1788);
                    flashlight.setCoord(803 + player.getX(), 380 + player.getY());
                }
                break;
            case 4:
                if (!leave.update(player, side)) return twitching;
                transitionRoomState((byte) 0);
                player.setBlacknessSpeed(1);
                player.setFreeze();
                break;
        }
        return twitching;
    }

    public void changePath(){
        hitbox.setSize(100, GameData.hitboxMultiplier);
        if (state == 0) {
            if (side == 0) {
                setPath("game/" + name + "/Door/Left");
                setX(380);
                setY(372);
                setWidth(130);
            } else if (side == 1) {
                setPath("game/" + name + "/Door/Middle");
                setX(1411);
                setY(382);
                setWidth(232);
            } else {
                setPath("game/" + name + "/Door/Right");
                if (type == 0) setX(2483);
                else setX(2484);
                setY(321);
                setWidth(105);
            }
        } else if (state == 1){
            if (side == 0) {
                hitbox.setSize(75, GameData.hitboxMultiplier);
                setPath("game/" + name + "/Battle/Left");
                setX(142);
                setY(327);
                setWidth(464);
                setHeight(512);
            } else if (side == 1) {
                setPath("game/" + name + "/Battle/Middle");
                setX(1144);
                setY(45);
                setWidth(736);
                setHeight(801);
            } else {
                setPath("game/" + name + "/Battle/Right");
                setX(2165);
                setY(195);
                setWidth(826);
                setHeight(829);
            }
        } else if (state == 2){
            if (side == 0) {
                setPath("game/" + name + "/Bed/Left");
                setX(0);
                setY(155);
                setWidth(915);
                setHeight(529);
            } else {
                setPath("game/" + name + "/Bed/Right");
                setX(1438);
                setY(190);
                setWidth(610);
                setHeight(521);
            }
        } else if (state == 3){
            if (side == 0) {
                hitbox.setCoord(707, 450);
                setPath("game/" + name + "/Peek/Left");
                setX(415);
                setY(217);
                setWidth(425);
                setHeight(369);
            } else {
                hitbox.setCoord(2362, 470);
                setPath("game/" + name + "/Peek/Right");
                setX(2128);
                setY(195);
                setWidth(589);
                setHeight(496);
            }
        } else {
            hitbox.setCoord(0, 0);
            if (side == 0){
                setPath("game/" + name + "/Leaving/Left");
                setX(280);
                setY(376);
                setWidth(210);
                setHeight(392);
            } else {
                setPath("game/" + name + "/Leaving/Right");
                setX(2490);
                setY(285);
                setWidth(311);
                setHeight(567);
            }
        }
    }

    private boolean roomUpdate(SoundHandler soundHandler, Cat cat, Player player){
        if (transitionCooldown != 0) {
            transitionCooldown = Time.decreaseTimeValue(transitionCooldown, 0, 1);
            if (transitionCooldown <= 0.75f && player.isScared()){
                soundHandler.play("crawl");
                hitbox.setCoord(0, 0);
                player.setScared();
                player.setAttack();
            }
            if (transitionCooldown == 0) {
                if (cat == null || cat.getState() != 2) side = (byte) (random.nextInt(2) * 2);
                else if (cat.getSide() == 0) side = 2;
                else side = 0;
                player.setFreeze();
                player.setBlacknessSpeed(1);
                transitionRoomState((byte) 2);
            }
            return true;
        }

        float pitchSpeed;
        if (type == 0) pitchSpeed = 0.01f;
        else if (type == 1) pitchSpeed = 0.02f;
        else pitchSpeed = 0;
        if (type == 2 && attack.isAttack()) attack.setAudioPitch(soundHandler, 0.875f);

        if (attack.update(soundHandler, random, (byte) 0, player.isScared(), pitchSpeed)){
            player.setBlacknessSpeed(6);
            if (attack.getTeleports() > 0){
                if (type == 2) attack.setFlashTime(0.375f);
                else attack.setFlashTime(0.8f);
                if (type == 2) hellCooldown = 3;
                if (type != 2) attack.setMoves(12);
                else attack.setMoves(15);
                side = attack.dodgeUpdate(soundHandler, random, null, player, side, 3, type == 2);
                changePath();
            } else {
                if (type == 2){
                    player.setAttack();
                    player.setBlacknessTimes(1);
                    side = (byte) (random.nextInt(2) * 2);
                    attack.playDodge(soundHandler, player, side);
                    transitionRoomState((byte) 3);
                } else {
                    transitionCooldown = 1.15f;
                    attack.stopAudio(soundHandler);
                    soundHandler.play("thunder");
                    player.setFreeze();
                    player.setBlacknessDelay(1.25f);
                    player.setBlacknessTimes(2);
                }
            }
            return true;
        }
        if (attack.notLimitCheck(random, false, type)) return false;
        if ((type == 0 && attack.getLimit() < 2) || (type != 0 && attack.getLimit() < 3)) attack.increaseLimit();
        if (type == 0) attack.setFlashTime(0.24f + random.nextInt(3) * 0.08f);
        else if (type == 1) attack.setFlashTime(0.16f + random.nextInt(3) * 0.08f);
        else attack.setFlashTime(0.1f + random.nextInt(2) * 0.1f);
        return false;
    }

    public void transitionRoomState(byte state){
        this.state = state;
        if (state == 0) door.reset(5);
        else if (state == 1){
            if (type == 0) attack.reset(0.85f, 20, 3, (byte) 2, (byte) 0, (byte) 0, 20);
            else attack.reset(0.8f, 12, 3, (byte) 3, (byte) 2, (byte) 0, 24);
        } else if (state == 2){
            if (type == 0) bed.reset(10, 1.15f);
            else bed.reset(8, 1.15f);
            tape.reset(random, 4 - type);
        } else if (state == 3) {
            if (type == 0) peek.reset(3, 2.25f, 1.25f);
            else peek.reset(1.5f, 2.75f, 1.25f);
        }
        else if (state == 4) leave.reset();
        if (state != 0) changePath();
    }

    public String getName() {
        return name;
    }

    public byte getType() {
        return type;
    }

    public byte getState() {
        return state;
    }

    public byte getSide() {
        return side;
    }

    public int getTwitchFrame() {
        return twitch.getFrame();
    }

    public Door getDoor() {
        return door;
    }

    public Attack getAttack() {
        return attack;
    }

    public float getHellCooldown() {
        return hellCooldown;
    }

    public float getBedCooldown() {
        return bed.getCooldown();
    }

    public int getTapeFrame() {
        return tape.getFrame();
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public int getLeaveFrame(){
        return leave.getFrame();
    }
}