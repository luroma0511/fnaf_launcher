package game.deluxe.state;

import game.deluxe.data.GameData;
import game.deluxe.state.Game.Game;
import game.deluxe.state.Menu.Menu;
import game.engine.util.Engine;
import game.engine.util.SoundManager;

public class StateManager {
    private final Game game;
    private final Menu menu;
    private final GameData gameData;
    private int gameState;

    public StateManager(){
        game = new Game();
        menu = new Menu();
        gameData = new GameData();
        gameState = 0;
    }

    public void update(Engine engine, SoundManager soundManager){
        if (gameState == 0){
            menu.update(engine, soundManager);
        } else {
            game.update(engine, gameData);
        }
    }

    public Game getGame() {
        return game;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(byte gameState) {
        this.gameState = gameState;
    }
}
