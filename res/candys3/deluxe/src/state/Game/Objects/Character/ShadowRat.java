package state.Game.Objects.Character;

import data.GameData;
import state.Game.Functions.*;
import state.Game.Objects.Flashlight;
import state.Game.Objects.Player;
import state.Game.Objects.Room;
import util.*;

import java.util.Random;

public class ShadowRat extends SpriteObject {
    private final Random random;

    private final Hitbox hitbox;
    private final Attack attack;
    private final Door door;
    private final Bed bed;
    private final Peek peek;
    private final Leave leave;
    private final Twitch twitch;

    private float transitionCooldown;

    private byte ai;
    private byte roomState;
    private byte side;

    public ShadowRat(byte ai) {
        super();
        this.ai = ai;
        random = new Random();
        twitch = new Twitch();
        hitbox = new Hitbox(100);
        door = new Door(4);
        door.reset(5, 2);
        attack = new Attack();
        bed = new Bed();
        peek = new Peek();
        leave = new Leave(1.25f);
    }

    public boolean execute(Player player, ShadowCat cat, Room room, Flashlight flashlight, boolean twitching){
        twitching = input(player, room, flashlight.getX(), flashlight.getY(), twitching);
        update(cat, player, room, flashlight);
        return twitching;
    }

    private boolean input(Player player, Room room, float mx, float my, boolean twitching) {
        boolean hovered = hitbox.isHovered(mx, my);
        boolean imageHovered = this.mouseOverWithPanning(mx, my);
        switch (roomState) {
            case 0:
                if (door.input(room, hovered)) hitbox.setCoord(0, 0);
                break;
            case 1:
                if (attack.input(player, room, imageHovered, hovered, 0)) hitbox.setCoord(0, 0);
                if (room.getFrame() == 0 && room.getState() == 0) {
                    twitch.update(hovered);
                    if (twitch.isTwitching()) twitching = true;
                }
                break;
            case 2:
                bed.input(player, room, imageHovered);
                break;
            case 3:
                peek.input(player, room, hovered, 1.25f);
                if (room.getFrame() == 0 && room.getState() == 0) {
                    twitch.update(hovered);
                    if (twitch.isTwitching()) twitching = true;
                }
                break;
        }
        return twitching;
    }

    private void update(ShadowCat cat, Player player, Room room, Flashlight flashlight) {
        switch (roomState) {
            case 0:
                door.update(side, player);
                boolean catInRoom = cat != null && (cat.getRoomState() == 1 || cat.getRoomState() == 4);
                if (door.isSignal()) {
                    if (catInRoom && cat.getAttack().getTeleports() == 0) door.pause();
                    if (door.getSameSide() == 3) {
                        door.setSameSide((byte) 0);
                        int rand = random.nextInt(2);
                        if (side == 0) {
                            if (catInRoom && cat.getSide() == 0) side = 1;
                            else side = (byte) (1 + rand);
                        } else if (side == 1) {
                            side = (byte) (2 * rand);
                            if (catInRoom && cat.getSide() != 1){
                                if (cat.getSide() == 0) side = 0;
                                else side = 2;
                            } else side = (byte) (2 * rand);
                        } else {
                            if (catInRoom && cat.getSide() == 2) side = 1;
                            else side = (byte) rand;
                        }
                    } else {
                        byte previousSide = side;
                        if (!catInRoom || cat.getSide() == 1) side = (byte) random.nextInt(3);
                        else if (cat.getSide() == 0) side = (byte) random.nextInt(2);
                        else side = (byte) (1 + random.nextInt(2));
                        if (previousSide == side) door.setSameSide((byte) (door.getSameSide() + 1));
                    }
                    if (side == 0) hitbox.setCoord(481, 663);
                    else if (side == 1) hitbox.setCoord(1531, 696);
                    else hitbox.setCoord(2525, 715);
                    changePath();
                }
                if (!door.isTimeUp()) return;
                transitionRoomState((byte) 1);
                SoundManager.play("get_in");
                player.setBlacknessTimes(2);
                player.setBlacknessSpeed(6);
                break;
            case 1:
                if (attack.getKillTimer() == 0){
                    player.setBlacknessTimes(3);
                    player.setBlacknessSpeed(6);
                    player.setFreeze();
                    player.setJumpscare();
                    Jumpscare.set("game/Shadow Rat/Jumpscare", 3);
                    return;
                }
                if (roomUpdate(cat, player) || !attack.isMoved()) return;
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
                if (attack.getFlashTime() != 0) {
                    attack.setLimit(4);
                    break;
                }
                if (attack.getLimit() != 0 && random.nextInt(5) == 2) {
                    attack.setLimit(attack.getLimit() - 1);
                    break;
                }
                attack.setLimit(4);
                attack.setFlashTime(0.19f + random.nextInt(5) * 0.065f);
//                    if (random.nextInt(5) == 2) attack.setSkip();
                break;
            case 2:
                if (bed.killTime()) {
                    player.setBlacknessTimes(3);
                    player.setBlacknessSpeed(6);
                    player.setFreeze();
                    player.setJumpscare();
                    Jumpscare.set("game/Shadow Rat/Jumpscare", 3);
                    return;
                }
                if (!bed.update(player, room)) return;
                if ((player.getSide() == 0 && side == 2) || (player.getSide() == 2 && side == 0)) {
                    transitionRoomState((byte) 3);
                    SoundManager.play("peek");
                    break;
                }
                player.setBlacknessTimes(3);
                player.setBlacknessSpeed(6);
                player.setFreeze();
                player.setJumpscare();
                Jumpscare.set("game/Shadow Rat/Jumpscare", 3);
                break;
            case 3:
                if (!peek.update()) {
                    if (!peek.isKillTime()) return;
                    player.setBlacknessTimes(3);
                    player.setBlacknessSpeed(6);
                    player.setFreeze();
                    player.setJumpscare();
                    Jumpscare.set("game/Shadow Rat/Jumpscare", 3);
                    return;
                }
                hitbox.setCoord(0, 0);
                transitionRoomState((byte) 4);
                SoundManager.play("leave");
                player.setY(220);
                if (side == 0) {
                    player.setX(4);
                    flashlight.setCoord(398 + player.getX(), 412 + player.getY());
                } else {
                    player.setX(1788);
                    flashlight.setCoord(803 + player.getX(), 380 + player.getY());
                }
                break;
            case 4:
                if (!leave.update(player, side)) return;
                transitionRoomState((byte) 0);
                player.setBlacknessSpeed(1);
                player.setFreeze();
                break;
        }
    }

    public void changePath() {
        hitbox.setSize(100, GameData.hitboxMultiplier);
        if (roomState == 0) {
            if (side == 0) {
                setPath("game/Shadow Rat/Looking Away/Left");
                setX(380);
                setY(372);
                setWidth(130);
            } else if (side == 1) {
                setPath("game/Shadow Rat/Looking Away/Middle");
                setX(1411);
                setY(382);
                setWidth(232);
            } else {
                setPath("game/Shadow Rat/Looking Away/Right");
                setX(2484);
                setY(321);
                setWidth(105);
            }
        } else if (roomState == 1) {
            if (side == 0) {
                hitbox.setSize(75, GameData.hitboxMultiplier);
                setPath("game/Shadow Rat/Battle/Left");
                setX(142);
                setY(327);
                setWidth(464);
                setHeight(512);
            } else if (side == 1) {
                setPath("game/Shadow Rat/Battle/Middle");
                setX(1144);
                setY(45);
                setWidth(736);
                setHeight(801);
            } else {
                setPath("game/Shadow Rat/Battle/Right");
                setX(2165);
                setY(195);
                setWidth(826);
                setHeight(829);
            }
        } else if (roomState == 2) {
            if (side == 0) {
                setPath("game/Shadow Rat/Bed/LeftUnder");
                setX(0);
                setY(155);
                setWidth(915);
                setHeight(529);
            } else {
                setPath("game/Shadow Rat/Bed/RightUnder");
                setX(1438);
                setY(190);
                setWidth(610);
                setHeight(521);
            }
        } else if (roomState == 3) {
            if (side == 0) {
                hitbox.setCoord(707, 450);
                setPath("game/Shadow Rat/Bed/LeftPeek");
                setX(415);
                setY(217);
                setWidth(425);
                setHeight(369);
            } else {
                hitbox.setCoord(2362, 470);
                setPath("game/Shadow Rat/Bed/RightPeek");
                setX(2128);
                setY(195);
                setWidth(589);
                setHeight(496);
            }
        } else {
            hitbox.setCoord(0, 0);
            if (side == 0) {
                setPath("game/Shadow Rat/Leaving/Left");
                setX(280);
                setY(376);
                setWidth(210);
                setHeight(392);
            } else {
                setPath("game/Shadow Rat/Leaving/Right");
                setX(2490);
                setY(285);
                setWidth(311);
                setHeight(567);
            }
        }
    }

    private boolean roomUpdate(ShadowCat cat, Player player) {
        if (transitionCooldown != 0) {
            transitionCooldown = Time.decreaseTimeValue(transitionCooldown, 0, 1);
            if (transitionCooldown <= 0.75f && player.isScared()) {
                SoundManager.play("crawl");
                player.setScared();
                player.setAttack();
            }
            if (transitionCooldown == 0) {
                if (cat == null || cat.getRoomState() != 2) side = (byte) (random.nextInt(2) * 2);
                else if (cat.getSide() == 0) side = 2;
                else side = 0;
                transitionRoomState((byte) 2);
                player.setFreeze();
                player.setBlacknessSpeed(1);
            }
            return true;
        }

        if (attack.update(random, (byte) 0, player.isScared(), 0.02f)) {
            player.setBlacknessSpeed(6);
            if (attack.getTeleports() > 0){
                attack.decreaseTeleports();
                attack.setFlashTime(0.8f);
                attack.setLimit(4);
                attack.setPosition((byte) 0);
                attack.setKillTimer(2);
                attack.setAttackTimer(4);
                attack.setMoved();
                player.setBlacknessTimes(1);
                if (side == 0){
                    side = 1;
                    SoundManager.play("dodgeRight");
                } else if (side == 2){
                    side = 1;
                    SoundManager.play("dodgeLeft");
                } else {
                    side = (byte) (random.nextInt(2) * 2);
                    if (side == 0) SoundManager.play("dodgeLeft");
                    else SoundManager.play("dodgeRight");
                }
                changePath();
            } else {
                transitionCooldown = 1.25f;
                SoundManager.play("thunder");
                player.setFreeze();
                player.setBlacknessDelay(1.25f);
                player.setBlacknessTimes(2);
            }
            return true;
        }
        return false;
    }

    public void transitionRoomState(byte roomState) {
        this.roomState = roomState;
        if (roomState == 0) {
            door.reset(5);
        } else if (roomState == 1) {
            attack.reset(0.8f, 0.025f, 4, (byte) 2, (byte) 0, 30);
            changePath();
        } else if (roomState == 2) {
            bed.reset(10, 1.15f);
            changePath();
        } else if (roomState == 3) {
            peek.reset(2, 2.5f, 1.25f);
            changePath();
        } else if (roomState == 4) {
            leave.reset();
            changePath();
        }
    }

    public void jumpscare() {
        VideoManager.setRequest("game/Shadow Rat/Jumpscare");
    }

    private final String[] textures = new String[]{
            "Battle/Left",
            "Battle/Middle",
            "Battle/Right",
            "Bed/LeftUnder",
            "Bed/RightUnder",
            "Bed/LeftPeek",
            "Bed/RightPeek",
            "Leaving/Left",
            "Leaving/Right",
            "Tape/Tape",
            "Looking Away/Left",
            "Looking Away/Middle",
            "Looking Away/Right"
    };

    public void load() {
        for (String file : textures) ImageManager.add("game/Shadow Rat/" + file);
    }

    public byte getRoomState() {
        return roomState;
    }

    public byte getSide() {
        return side;
    }

    public Twitch getTwitch() {
        return twitch;
    }

    public Door getDoor() {
        return door;
    }

    public Attack getAttack() {
        return attack;
    }

    public Peek getPeek() {
        return peek;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public float getAttackHealth() {
        return attack.getKillTimer() / 2;
    }

    public int getLeaveFrame() {
        return leave.getFrame();
    }
}
