package game.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.engine.util.Engine;
import game.engine.util.RenderManager;

public class Candys3Deluxe extends ApplicationAdapter {
	private RenderManager renderManager;
	private final ScheduledExecutorService engineTimer;
	private final Engine engine;

	public Candys3Deluxe(){
		engine = new Engine();
		engineTimer = Executors.newSingleThreadScheduledExecutor();
		engineTimer.scheduleAtFixedRate(engine::update, 0, 16, TimeUnit.MILLISECONDS);
	}

	@Override
	public void create(){
		renderManager = new RenderManager((short) Gdx.graphics.getWidth(), (short) Gdx.graphics.getHeight());
	}

	@Override
	public void render () {
		renderManager.requests(engine);
		renderManager.render(engine);
	}
	
	@Override
	public void dispose () {
		renderManager.dispose();
		engineTimer.shutdown();
	}
}
