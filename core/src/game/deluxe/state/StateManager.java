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
    private int previousGameState;

    public StateManager(){
        game = new Game();
        menu = new Menu();
        gameData = new GameData();
        gameState = 0;
        previousGameState = -1;
    }

    public void update(Engine engine, SoundManager soundManager){
        if (gameState == 0){
            menu.update(engine, gameData, soundManager);
        } else {
            game.update(engine, gameData);
        }

        if (previousGameState != gameState){
            engine.getRequest().setNow(true);
            if (gameState == 0) menu.load(engine.getRequest());
            else if (gameState == 1) game.load(gameData, engine.getRequest());
            previousGameState = gameState;
            engine.getRequest().setStartLoading(true);
        }
    }

    public Game getGame() {
        return game;
    }

    public Menu getMenu() {
        return menu;
    }

    public int getPreviousGameState() {
        return previousGameState;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(byte gameState) {
        this.gameState = gameState;
    }
}
