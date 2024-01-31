package game.deluxe.enemy;

import game.engine.util.*;

public class Rat {

    public Rat(){

    }

    public void jumpscare(VideoManager videoManager){
        videoManager.setRequest("game/Rat/Jumpscare/room");
    }


    private final String[] textures = new String[]{
            "Battle/Left -> 464",
            "Battle/Middle -> 736",
            "Battle/Right -> 826",
            "Bed/LeftPeek -> 425",
            "Bed/RightPeek -> 589",
            "Leaving/Left -> 210",
            "Leaving/Right -> 311",
            "Tape/Tape -> 750",
            "Looking Away/Left -> 130",
            "Looking Away/Middle -> 232",
            "Looking Away/Right -> 105"
    };

    public void load(Engine engine){
        String prefix = "game/Rat/";
        String token = " -> ";
        for (String file: textures){
            String name = file.substring(0, file.indexOf(token));
            short width = Short.parseShort(file.substring(file.indexOf(token) + 4));
            engine.setSpriteRequest(engine.createSpriteRequest(engine.getSpriteRequest(), prefix + name, width));
        }
    }
}
