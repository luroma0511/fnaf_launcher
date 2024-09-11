package candys3.Game.Objects.Character;

import util.deluxe.GameData;
import candys3.Game.Objects.Functions.*;
import candys3.Game.Objects.Player;
import candys3.Game.Objects.Room;
import util.Hitbox;
import util.SoundHandler;
import util.SpriteObject;
import util.Time;

import java.util.Arrays;
import java.util.Random;

public class Cat extends SpriteObject {
    private final Random random;

    private final Hitbox hitbox;
    private final Attack attack;
    private final BedSide bedSide;
    private final Bed bed;
    private final Peek peek;
    private final Leave leave;
    private final Twitch twitch;

    private final String name;
    private final byte type;
    private byte state;
    private byte side;
    private boolean start;

    private float transitionCooldown;

    public Cat(byte type){
        super();
        name = type == 0 ? "Cat" : "Shadow Cat";
        this.type = type;
        random = new Random();
        twitch = new Twitch();
        hitbox = new Hitbox(100);
        if (type != 2) bedSide = new BedSide(8, 10, type == 1 ? 45 : -1, false);
        else bedSide = new BedSide(3, 0, -1, true);
        attack = new Attack();
        bed = new Bed();
        peek = new Peek();
        leave = new Leave(0);
        side = (byte) (random.nextInt(2) * 2);
        if (type != 1) bedSide.reset();
        else {
            state = 2;
            bed.reset(8, 1.15f);
            changePath();
        }
    }

    public boolean execute(SoundHandler soundHandler, Player player, Rat rat, Room room, boolean twitching){
        var flashlight = player.getFlashlight();
        twitching = input(soundHandler, player, rat, room, flashlight.getX(), flashlight.getY(), twitching);
        twitching = update(soundHandler, rat, player, room, twitching);
        return twitching;
    }

    private boolean input(SoundHandler soundHandler, Player player, Rat rat, Room room, float mx, float my, boolean twitching){
        boolean hovered = hitbox.isHovered(mx, my);
        boolean imageHovered = this.mouseOverWithPanning(mx, my);
        switch (state){
            case 0:
                if (bedSide.getFrame() > 0){
                    float limit = type != 2 ? 0.15f : 0.3f; 
                    float catVolume = Time.increaseTimeValue(soundHandler.getSoundEffect(soundHandler.VOLUME, "cat"), limit, 0.25f);
                    soundHandler.setSoundEffect(soundHandler.VOLUME, "cat", catVolume);
                }
                if (type == 0) {
                    if (room.getFrame() == 0 && room.getState() == 0 && !player.isFreeze() && bedSide.input(rat, hovered, true)) {
                        if (!bedSide.getSoundLock()) {
                            soundHandler.play("catPulse");
                            soundHandler.setSoundEffect(soundHandler.LOOP, "catPulse", 1);
                            bedSide.setSoundLock();
                        }
                    } else if (bedSide.getSoundLock()) {
                        soundHandler.stop("catPulse");
                        bedSide.setSoundLock();
                    }
                } else if (!player.isFreeze()){
                    bedSide.input(rat, hovered && room.getFrame() == 0 && room.getState() == 0, false);
                    if (room.getFrame() == 0 && room.getState() == 0) {
                        if (twitch.update(hovered)) twitching = true;
                        if (hovered && !player.isScared()) player.setScared();
                        else if (!twitching && !hovered && !player.isAttack() && player.isScared()) player.setScared();
                    }
                }
                break;
            case 1:
                if (attack.input(soundHandler, player, room, imageHovered, hovered, 1, type == 2)) hitbox.setCoord(0, 0);
                if (room.getFrame() == 0 && room.getState() == 0 && twitch.update(hovered)) twitching = true;
                break;
            case 2:
                bed.input(soundHandler, player, room, imageHovered);
                break;
            case 3:
                if (peek.input(soundHandler, player, room, hovered, 0.75f) && type == 2){
                    transitionCooldown = 1.15f;
                    player.setBlacknessDelay(1.25f);
                    attack.stopAudio(soundHandler);
                }
                if (room.getFrame() == 0 && room.getState() == 0 && twitch.update(hovered)) twitching = true;
                break;
        }
        return twitching;
    }

    private boolean update(SoundHandler soundHandler, Rat rat, Player player, Room room, boolean twitching){
        if (!start){
            for (String sound: Arrays.asList("cat", "catLeft", "catRight")) {
                soundHandler.play(sound);
                if (type == 2) soundHandler.setSoundEffect(soundHandler.PITCH, sound, 0.9125f);
                soundHandler.setSoundEffect(soundHandler.VOLUME, sound, 0);
                soundHandler.setSoundEffect(soundHandler.LOOP, sound, 1);
            }
            start = true;
        }
        if (state != 0 && state != 4){
            float catVolume = Time.increaseTimeValue(soundHandler.getSoundEffect(soundHandler.VOLUME, "cat"), 0.65f, 0.5f);
            soundHandler.setSoundEffect(soundHandler.VOLUME, "cat", catVolume);
            if (type != 2) {
                float catPitch = Time.increaseTimeValue(soundHandler.getSoundEffect(soundHandler.PITCH, "cat"), 2, 0.01f);
                soundHandler.setSoundEffect(soundHandler.PITCH, "cat", catPitch);
            }
        } else if (type == 2) soundHandler.setSoundEffect(soundHandler.VOLUME, "cat", 0.3f);
        switch (state){
            case 0:
                if (!GameData.noJumpscares && ((type == 0 && bedSide.getPhase() == 3 && bedSide.getDelay() == 0) || (type != 0 && bedSide.getFrame() == 43))){
                    player.setJumpscare();
                    if (type == 0) {
                        if (player.getSide() != side) Jumpscare.set("game/Cat/Jumpscare/side", 0, side == 0);
                        else Jumpscare.set("game/Cat/Jumpscare/bed");
                    } else if (type == 1) Jumpscare.set("game/Shadow Cat/Jumpscare", 0.75f);
                    else Jumpscare.set("game/Shadow Cat/HellJumpscare");
                    return twitching;
                }
                if (type == 2 && rat != null && rat.getState() == 2 && rat.getBedCooldown() > 28){
                    state = 1;
                    hitbox.setCoord(0, 0);
                    if (side == 0) soundHandler.setSoundEffect(soundHandler.VOLUME, "catLeft", 0);
                    else soundHandler.setSoundEffect(soundHandler.VOLUME, "catRight", 0);
                    if (side != player.getSide()) side = 1;
                    attack.reset(0.375f, 15, 2, (byte) 2, (byte) 1, (byte) 0, 24);
                    attack.setKillTimer(4);
                    changePath();
                    return twitching;
                }
                side = (byte) bedSide.update(player, rat != null && rat.getState() == 1, side, type == 0);
                if (type == 0 && bedSide.getFrame() != 23 && bedSide.getFrame() != 42 && bedSide.getFrame() != 57) hitbox.setCoord(0, 0);
                if (bedSide.getFrame() > 0){
                    boolean change = true;
                    float bedPhase = bedSide.getPhase();
                    if (type == 0) {
                        if (bedSide.getFrame() <= 23) bedSide.setPhase(1);
                        else if (bedSide.getFrame() <= 42) bedSide.setPhase(2);
                        else bedSide.setPhase(3);
                    } else {
                        if (bedSide.getFrame() < 8) bedSide.setPhase(1);
                        else if (bedSide.getFrame() < 20) bedSide.setPhase(2);
                        else if (bedSide.getFrame() < 32) bedSide.setPhase(3);
                        else bedSide.setPhase(4);
                    }
                    if (bedPhase == bedSide.getPhase()) change = false;
                    if (change) changePath();
                    if (type == 0) {
                        if (side == 0) soundHandler.setSoundEffect(soundHandler.VOLUME, "catLeft", (float) (bedSide.getFrame() - 23) / 45);
                        else soundHandler.setSoundEffect(soundHandler.VOLUME, "catRight", (float) (bedSide.getFrame() - 23) / 45);
                    } else {
                        if (side == 0) soundHandler.setSoundEffect(soundHandler.VOLUME, "catLeft", (float) (bedSide.getFrame() - 8) / 50);
                        else soundHandler.setSoundEffect(soundHandler.VOLUME, "catRight", (float) (bedSide.getFrame() - 8) / 50);
                    }
                }
                if (type == 0 && bedSide.isSignal()) {
                    hitbox.setSize(100, GameData.hitboxMultiplier);
                    if (side == 0) {
                        if (bedSide.getFrame() <= 23) hitbox.setCoord(192, 548);
                        else if (bedSide.getFrame() <= 42) hitbox.setCoord(186, 720);
                        else hitbox.setCoord(187, 660);
                    } else {
                        if (bedSide.getFrame() <= 23) hitbox.setCoord(2910, 448);
                        else if (bedSide.getFrame() <= 42) hitbox.setCoord(2838, 547);
                        else hitbox.setCoord(2805, 619);
                    }
                } else if (type != 0){
                    hitbox.setSize(100, GameData.hitboxMultiplier);
                    int hitboxPosition = bedSide.getFrame() - 7;
                    switch (hitboxPosition){
                        case 1:
                            if (side == 2) hitbox.setCoord(2857, 512);
                            else hitbox.setCoord(211, 624);
                            break;
                        case 2:
                            if (side == 2) hitbox.setCoord(2857, 513);
                            else hitbox.setCoord(211, 641);
                            break;
                        case 3:
                            if (side == 2) hitbox.setCoord(2856, 514);
                            else hitbox.setCoord(211, 657);
                            break;
                        case 4:
                            if (side == 2) hitbox.setCoord(2855, 515);
                            else hitbox.setCoord(211, 671);
                            break;
                        case 5:
                            if (side == 2) hitbox.setCoord(2854, 516);
                            else hitbox.setCoord(212, 682);
                            break;
                        case 6:
                            if (side == 2) hitbox.setCoord(2852, 518);
                            else hitbox.setCoord(213, 691);
                            break;
                        case 7:
                            if (side == 2) hitbox.setCoord(2850, 521);
                            else hitbox.setCoord(214, 696);
                            break;
                        case 8:
                            if (side == 2) hitbox.setCoord(2847, 525);
                            else hitbox.setCoord(215, 695);
                            break;
                        case 9:
                            if (side == 2) hitbox.setCoord(2844, 529);
                            else hitbox.setCoord(217, 693);
                            break;
                        case 10:
                            if (side == 2) hitbox.setCoord(2841, 533);
                            else hitbox.setCoord(217, 690);
                            break;
                        case 11:
                            if (side == 2) hitbox.setCoord(2838, 537);
                            else hitbox.setCoord(218, 685);
                            break;
                        case 12:
                            if (side == 2) hitbox.setCoord(2834, 541);
                            else hitbox.setCoord(218, 680);
                            break;
                        case 13:
                            if (side == 2) hitbox.setCoord(2830, 546);
                            else hitbox.setCoord(219, 676);
                            break;
                        case 14:
                            if (side == 2) hitbox.setCoord(2826, 552);
                            else hitbox.setCoord(221, 674);
                            break;
                        case 15:
                            if (side == 2) hitbox.setCoord(2822, 557);
                            else hitbox.setCoord(221, 673);
                            break;
                        case 16:
                            if (side == 2) hitbox.setCoord(2818, 561);
                            else hitbox.setCoord(222, 672);
                            break;
                        case 17:
                            if (side == 2) hitbox.setCoord(2815, 564);
                            else hitbox.setCoord(223, 672);
                            break;
                        case 18:
                            if (side == 2) hitbox.setCoord(2811, 567);
                            else hitbox.setCoord(223, 673);
                            break;
                        case 19:
                            if (side == 2) hitbox.setCoord(2808, 570);
                            else hitbox.setCoord(224, 673);
                            break;
                        case 20:
                            if (side == 2) hitbox.setCoord(2804, 573);
                            else hitbox.setCoord(224, 673);
                            break;
                        case 21:
                            if (side == 2) hitbox.setCoord(2802, 575);
                            else hitbox.setCoord(224, 673);
                            break;
                        case 22:
                            if (side == 2) hitbox.setCoord(2801, 576);
                            else hitbox.setCoord(224, 674);
                            break;
                        case 23:
                            if (side == 2) hitbox.setCoord(2798, 578);
                            else hitbox.setCoord(225, 675);
                            break;
                        case 24:
                            if (side == 2) hitbox.setCoord(2796, 580);
                            else hitbox.setCoord(225, 675);
                            break;
                        case 25:
                            if (side == 2) hitbox.setCoord(2794, 583);
                            else hitbox.setCoord(225, 675);
                            break;
                        case 26:
                            if (side == 2) hitbox.setCoord(2791, 585);
                            else hitbox.setCoord(226, 673);
                            break;
                        case 27:
                            if (side == 2) hitbox.setCoord(2789, 588);
                            else hitbox.setCoord(226, 670);
                            break;
                        case 28:
                            if (side == 2) hitbox.setCoord(2786, 591);
                            else hitbox.setCoord(226, 667);
                            break;
                        case 29:
                            if (side == 2) hitbox.setCoord(2783, 594);
                            else hitbox.setCoord(226, 662);
                            break;
                        case 30:
                            if (side == 2) hitbox.setCoord(2780, 596);
                            else hitbox.setCoord(226, 657);
                            break;
                        case 31:
                            if (side == 2) hitbox.setCoord(2778, 599);
                            else hitbox.setCoord(226, 654);
                            break;
                        case 32:
                            if (side == 2) hitbox.setCoord(2775, 601);
                            else hitbox.setCoord(226, 651);
                            break;
                        case 33:
                            if (side == 2) hitbox.setCoord(2772, 603);
                            else hitbox.setCoord(226, 648);
                            break;
                        case 34:
                            if (side == 2) hitbox.setCoord(2769, 605);
                            else hitbox.setCoord(226, 647);
                            break;
                        case 35:
                            if (side == 2) hitbox.setCoord(2766, 607);
                            else hitbox.setCoord(226, 646);
                            break;
                        case 36:
                            if (side == 2) hitbox.setCoord(2763, 609);
                            else hitbox.setCoord(226, 645);
                            break;
                        default:
                            hitbox.setCoord(0, 0);
                            break;
                    }
                }
                if (!bedSide.endState()) return twitching;
                if (type == 2){
                    if (side == 0) side = 2;
                    else side = 0;
                    bedSide.setDelay(1.5f);
                    bedSide.setRetreat();
                    changePath();
                    break;
                }
                if (rat != null && (rat.getState() == 2 || rat.getState() == 3)) {
                    if (rat.getSide() == 0) side = 2;
                    else side = 0;
                } else side = (byte) (random.nextInt(2) * 2);
                transitionState(rat, (byte) 2);
                if (type != 0) break;
                soundHandler.stop("catPulse");
                bedSide.setSoundLock();
                break;
            case 1:
                if (attack.getKillTimer() == 0 && attack.getReactionTimer() == 0 && !GameData.noJumpscares){
                    player.setJumpscare();
                    if (type == 0) Jumpscare.set("game/Cat/Jumpscare/room");
                    else if (type == 1) Jumpscare.set("game/Shadow Cat/Jumpscare", 0.75f);
                    else Jumpscare.set("game/Shadow Cat/HellJumpscare");
                    return twitching;
                }
                if (roomUpdate(soundHandler, rat, player, twitching) || attack.notMoved()){
                    if (attack.isSignal() && attack.notMoved()){
                        twitching = false;
                        attack.setSignal();
                    }
                    return twitching;
                }
                if (type == 0) {
                    if (side == 0) {
                        if (attack.getPosition() == 0) hitbox.setCoord(366, 741);
                        else if (attack.getPosition() == 1) hitbox.setCoord(290, 632);
                        else hitbox.setCoord(466, 616);
                    } else if (side == 1) {
                        if (attack.getPosition() == 0) hitbox.setCoord(1529, 724);
                        else if (attack.getPosition() == 1) hitbox.setCoord(1341, 583);
                        else hitbox.setCoord(1703, 605);
                    } else {
                        if (attack.getPosition() == 0) hitbox.setCoord(2559, 890);
                        else if (attack.getPosition() == 1) hitbox.setCoord(2395, 714);
                        else hitbox.setCoord(2748, 669);
                    }
                } else {
                    if (side == 0) {
                        if (attack.getPosition() == 0) hitbox.setCoord(381, 743);
                        else if (attack.getPosition() == 1) hitbox.setCoord(305, 630);
                        else if (attack.getPosition() == 2) hitbox.setCoord(475, 620);
                        else hitbox.setCoord(377, 548);
                    } else if (side == 1) {
                        if (attack.getPosition() == 0) hitbox.setCoord(1524, 709);
                        else if (attack.getPosition() == 1) hitbox.setCoord(1318, 577);
                        else if (attack.getPosition() == 2) hitbox.setCoord(1700, 602);
                        else hitbox.setCoord(1520, 521);
                    } else {
                        if (attack.getPosition() == 0) hitbox.setCoord(2559, 863);
                        else if (attack.getPosition() == 1) hitbox.setCoord(2401, 675);
                        else if (attack.getPosition() == 2) hitbox.setCoord(2712, 651);
                        else hitbox.setCoord(2544, 553);
                    }
                }
                if (side == 0) hitbox.setSize(75, GameData.hitboxMultiplier);
                else hitbox.setSize(100, GameData.hitboxMultiplier);
                attack.setMoved();
                break;
            case 2:
                if (bed.killTime() && !GameData.noJumpscares) {
                    player.setJumpscare();
                    if (type == 0) Jumpscare.set("game/Cat/Jumpscare/bed");
                    else if (type == 1) Jumpscare.set("game/Shadow Cat/Jumpscare", 0.75f);
                    else Jumpscare.set("game/Shadow Cat/HellJumpscare");
                    return twitching;
                }
                if (!bed.update(player, room)) return twitching;
                if (type == 2) {
                    state = 0;
                    bedSide.reset();
                    bedSide.setLimit(8);
                    bedSide.setDelay(1.5f);
                    if (rat.getSide() == 0) side = 2;
                    else side = 0;
                    break;
                }
                if (room.getState() == 0 && ((player.getSide() == 0 && side == 2) || (player.getSide() == 2 && side == 0))) {
                    transitionState((byte) 3);
                    soundHandler.play("peek");
                    break;
                }
                if (GameData.noJumpscares) break;
                player.setJumpscare();
                if (type == 0) Jumpscare.set("game/Cat/Jumpscare/bed");
                else if (type == 1) Jumpscare.set("game/Shadow Cat/Jumpscare", 0.75f);
                else Jumpscare.set("game/Shadow Cat/HellJumpscare");
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
                        if (rat.getSide() == 0) side = 2;
                        else side = 0;
                        player.setFreeze();
                        player.setBlacknessSpeed(1);
                        state = 2;
                        bed.reset(6, 1);
                        changePath();
                        break;
                    }
                    break;
                }
                if (!peek.update()) {
                    if (peek.notKillTime() || GameData.noJumpscares) return twitching;
                    player.setJumpscare();
                    if (type == 0) Jumpscare.set("game/Cat/Jumpscare/bed");
                    else if (type == 1) Jumpscare.set("game/Shadow Cat/Jumpscare", 0.75f);
                    else Jumpscare.set("game/Shadow Cat/HellJumpscare");
                    return twitching;
                }
                hitbox.setCoord(0, 0);
                player.setScared();
                player.setFreeze();
                boolean door = type != 0 || rat == null || rat.getDoor().getFrame() == 13 || rat.getSide() == side;
                if (side == 0) {
                    if (door) side = (byte) (1 + random.nextInt(2));
                    else if (rat.getSide() == 1) side = 2;
                    else side = 1;
                } else {
                    if (door) side = (byte) (random.nextInt(2));
                    else if (rat.getSide() == 1) side = 0;
                    else side = 1;
                }
                transitionState((byte) 1);
                break;
            case 4:
                if (!leave.update(player, side)) return twitching;
                transitionState((byte) 0);
                break;
        }
        return twitching;
    }

    public void changePath(){
        hitbox.setSize(100, GameData.hitboxMultiplier);
        if (state == 0) {
            if (side == 0) {
                if (type == 0) {
                    setPath("game/Cat/Retreat/Left");
                    append(String.valueOf(bedSide.getPhase()));
                    setX(0);
                    if (bedSide.getPhase() == 1) {
                        setY(339);
                        setWidth(371);
                    } else if (bedSide.getPhase() == 2) {
                        setY(274);
                        setWidth(400);
                    } else {
                        setY(233);
                        setWidth(378);
                    }
                } else {
                    setPath("game/Shadow Cat/Retreat/Left/");
                    if (bedSide.getFrame() < 8) append(String.valueOf(0));
                    setX(0);
                    setY(213);
                    setWidth(480);
                    setHeight(720);
                }
            } else {
                if (type == 0) {
                    setPath("game/Cat/Retreat/Right");
                    append(String.valueOf(bedSide.getPhase()));
                    setY(218);
                    if (bedSide.getPhase() == 1) {
                        setX(2761);
                        setWidth(274);
                    } else if (bedSide.getPhase() == 2) {
                        setX(2603);
                        setWidth(433);
                    } else {
                        setX(2537);
                        setWidth(497);
                    }
                } else {
                    setPath("game/Shadow Cat/Retreat/Right/");
                    setY(218);
                    if (bedSide.getFrame() < 8){
                        append(String.valueOf(0));
                        setX(2648);
                        setWidth(390);
                    } else {
                        setX(2483);
                        setWidth(555);
                    }
                }
            }
        } else if (state == 1){
            if (side == 0) {
                hitbox.setSize(75, GameData.hitboxMultiplier);
                setPath("game/" + name + "/Battle/Left");
                if (type == 0) {
                    setX(171);
                    setY(322);
                    setWidth(406);
                    setHeight(515);
                } else {
                    setX(174);
                    setY(242);
                    setWidth(464);
                    setHeight(612);
                }
            } else if (side == 1) {
                setPath("game/" + name + "/Battle/Middle");
                if (type == 0) {
                    setX(1215);
                    setY(78);
                    setWidth(664);
                    setHeight(791);
                } else {
                    setX(1178);
                    setY(52);
                    setWidth(735);
                    setHeight(861);
                }
            } else {
                setPath("game/" + name + "/Battle/Right");
                if (type == 0) {
                    setX(2229);
                    setY(196);
                    setWidth(711);
                    setHeight(828);
                } else {
                    setX(2134);
                    setY(192);
                    setWidth(870);
                    setHeight(832);
                }
            }
        } else if (state == 2){
            if (side == 0) {
                setPath("game/" + name + "/Bed/Left");
                setX(0);
                if (type == 0) {
                    setY(170);
                    setWidth(784);
                    setHeight(509);
                } else {
                    setY(214);
                    setWidth(890);
                    setHeight(446);
                }
            } else {
                setPath("game/" + name + "/Bed/Right");
                if (type == 0) {
                    setX(1100);
                    setY(141);
                    setWidth(948);
                    setHeight(538);
                } else {
                    setX(1408);
                    setY(204);
                    setWidth(640);
                    setHeight(420);
                }
            }
        } else if (state == 3){
            if (side == 0) {
                setPath("game/" + name + "/Peek/Left");
                if (type == 0) {
                    hitbox.setCoord(652, 440);
                    setX(515);
                    setY(231);
                    setWidth(274);
                    setHeight(345);
                } else {
                    hitbox.setCoord(648, 439);
                    setX(524);
                    setY(222);
                    setWidth(296);
                    setHeight(378);
                }
            } else {
                setPath("game/" + name + "/Peek/Right");
                if (type == 0) {
                    hitbox.setCoord(2349, 512);
                    setX(2156);
                    setY(194);
                    setWidth(557);
                    setHeight(525);
                } else {
                    hitbox.setCoord(2347, 509);
                    setX(2147);
                    setY(198);
                    setWidth(608);
                    setHeight(521);
                }
            }
        } else {
            hitbox.setCoord(0, 0);
            if (side == 0){
                setPath("game/" + name + "/Leaving/Left");
                if (type == 0){
                    setX(104);
                    setY(323);
                    setWidth(386);
                    setHeight(459);
                } else {
                    setX(109);
                    setY(337);
                    setWidth(378);
                    setHeight(445);
                }
            } else if (side == 2){
                setPath("game/" + name + "/Leaving/Right");
                if (type == 0){
                    setX(2491);
                    setY(259);
                    setWidth(459);
                    setHeight(589);
                } else {
                    setX(2494);
                    setY(272);
                    setWidth(492);
                    setHeight(582);
                }
            } else {
                setPath("game/" + name + "/Leaving/Middle");
                if (type == 0){
                    setX(1414);
                    setY(384);
                    setWidth(226);
                    setHeight(401);
                } else {
                    setX(1411);
                    setY(382);
                    setWidth(232);
                    setHeight(400);
                }
            }
        }
    }

    private boolean roomUpdate(SoundHandler soundHandler, Rat rat, Player player, boolean twitching){
        if (attack.update(soundHandler, random, type, player.isScared(), 0.01f)) {
            player.setBlacknessSpeed(6);
            if (attack.getTeleports() > 0) {
                if (type == 2) {
                    attack.setFlashTime(0.375f);
                    attack.setMoves(15);
                } else {
                    attack.setFlashTime(0.65f);
                    attack.setMoves(12);
                }
                side = attack.dodgeUpdate(soundHandler, random, rat, player, side, 2, type == 2);
                changePath();
            } else {
                if (type == 2){
                    player.setAttack();
                    player.setBlacknessTimes(1);
                    if (side == 0) side = 2;
                    else if (side == 2) side = 0;
                    else side = (byte) (random.nextInt(2) * 2);
                    attack.playDodge(soundHandler, player, side);
                    state = 3;
                    peek.reset(2, 2.5f, 0.75f);
                    changePath();
                } else {
                    transitionState((byte) 4);
                    player.setBlacknessTimes(2);
                    soundHandler.setSoundEffect(soundHandler.VOLUME, "cat", 0);
                    soundHandler.setSoundEffect(soundHandler.PITCH, "cat", 1);
                    attack.stopAudio(soundHandler);
                    soundHandler.play("thunder");
                    if (side != 1) soundHandler.play("leave");
                    player.setScared();
                    player.setAttack();
                }
            }
            return true;
        } else if (rat != null && rat.getDoor() != null
                && rat.getDoor().getCooldown() > rat.getDoor().getInitialCooldown() - 1
                && rat.getSide() != side && !twitching){
            attack.setKillTimer(Time.increaseTimeValue(attack.getKillTimer(), 2, 1));
        }
        if (attack.notLimitCheck(random, true, type)) return false;
        if (attack.getLimit() < 2) attack.increaseLimit();
        if (type == 0) attack.setFlashTime(0.25f + random.nextInt(3) * 0.125f);
        else if (type == 1) attack.setFlashTime(0.125f + random.nextInt(3) * 0.125f);
        else attack.setFlashTime(0.1f + random.nextInt(2) * 0.1f);
        return false;
    }

    public void transitionState(byte state){
        transitionState(null, state);
    }

    public void transitionState(Rat rat, byte state){
        this.state = state;
        if (state == 0){
            bedSide.reset();
            side = (byte) (random.nextInt(2) * 2);
        } else if (state == 1){
            if (type == 0) attack.reset(1.15f, 20, 2, (byte) 2, (byte) 0, (byte) 0, 22);
            else attack.reset(0.65f, 12, 2, (byte) 2, (byte) 1, (byte) 0, 24);
        } else if (state == 2){
            if (rat != null && (rat.getState() != 2 && rat.getState() != 3)) bed.reset(9, 1.15f);
            else bed.reset(13, 1.15f);
        } else if (state == 3) peek.reset(3, 2.25f + (0.25f * type), 0.75f);
        else if (state == 4) leave.reset();
        if (state != 0) changePath();
    }

    public byte getState() {
        return state;
    }

    public byte getType() {
        return type;
    }

    public byte getSide() {
        return side;
    }

    public int getTwitchFrame() {
        return twitch.getFrame();
    }

    public BedSide getBedSide() {
        return bedSide;
    }

    public Attack getAttack() {
        return attack;
    }

    public Hitbox getHitbox() {
        return hitbox;
    }

    public int getLeaveFrame(){
        return leave.getFrame();
    }
}