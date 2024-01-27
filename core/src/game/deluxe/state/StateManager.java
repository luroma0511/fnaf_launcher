package game.deluxe.state;

public class StateManager {
    private final Game game;
    private final Menu menu;
    private byte gameState;

    public StateManager(){
        game = new Game();
        menu = new Menu();
        gameState = 0;
    }

    public void update(){
        if (gameState == 0){
            menu.update();
        } else {
            game.update();
        }
    }

    public byte getGameState() {
        return gameState;
    }

    public void setGameState(byte gameState) {
        this.gameState = gameState;
    }
}
