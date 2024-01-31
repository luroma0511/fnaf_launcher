package game.deluxe.state;

import game.engine.util.Engine;

public class StateManager {
    private final Game game;
    private final Menu menu;
    private byte gameState;

    public StateManager(){
        game = new Game();
        menu = new Menu();
        gameState = 0;
    }

    public void update(Engine engine){
        if (gameState == 0){
            menu.update(engine);
        } else {
            game.update(engine);
        }
    }

    public Game getGame() {
        return game;
    }

    public Menu getMenu() {
        return menu;
    }

    public byte getGameState() {
        return gameState;
    }

    public void setGameState(byte gameState) {
        this.gameState = gameState;
    }
}
