package game.deluxe;

import com.badlogic.gdx.ApplicationAdapter;

import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.deluxe.state.Engine;
import game.deluxe.util.RenderManager;

public class Candys3Deluxe extends ApplicationAdapter {
	RenderManager renderManager;
	ScheduledExecutorService engineTimer;

	@Override
	public void create () {
		//thread code
		final Engine engine = new Engine();
		engineTimer = Executors.newSingleThreadScheduledExecutor();
		engineTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				engine.updateDeltaTime();
				engine.update();
			}
		}, 0, 16, TimeUnit.MILLISECONDS);

		renderManager = new RenderManager();
	}

	@Override
	public void render () {
		renderManager.render();
	}
	
	@Override
	public void dispose () {
		renderManager.dispose();
		engineTimer.shutdown();
	}
}
