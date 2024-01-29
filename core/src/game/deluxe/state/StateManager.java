package game.deluxe.state;

import java.util.Map;

public class StateManager {
    private final Game game;
    private final Menu menu;
    private byte gameState;

    public StateManager(){
        game = new Game();
        menu = new Menu();
        gameState = 0;
    }

    public void update(Map<String, String> requests){
        if (gameState == 0){
            menu.update(requests);
        } else {
            game.update(requests);
        }
    }

    public byte getGameState() {
        return gameState;
    }

    public void setGameState(byte gameState) {
        this.gameState = gameState;
    }
}
