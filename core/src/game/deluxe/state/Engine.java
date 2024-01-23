package game.deluxe.state;

public class Engine {
    private byte engineState;
    private Game game;
    private Menu menu;
    private float deltaTime;
    private long previousTime;

    public Engine(){
        game = new Game();
        menu = new Menu();
        engineState = 0;
    }

    public void update(){
        System.out.println("Working: " + deltaTime);
        if (engineState == 0){
            menu.update();
        } else if (engineState == 1){
            game.update();
        }

        previousTime = System.nanoTime();
    }

    public void updateDeltaTime(){
        deltaTime = (float) (System.nanoTime() - previousTime) / 100_000_000;
    }
}
