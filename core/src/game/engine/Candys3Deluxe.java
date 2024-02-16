package game.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import game.engine.util.Engine;
import game.engine.util.InputManager;
import game.engine.util.RenderManager;
import game.engine.util.SoundManager;

public class Candys3Deluxe extends ApplicationAdapter {
	private RenderManager renderManager;
	private SoundManager soundManager;
	private ScheduledExecutorService engineTimer;
	private Engine engine;
	public static int width;
	public static int height;

	public Candys3Deluxe(int width, int height){
		Candys3Deluxe.width = width;
		Candys3Deluxe.height = height;
	}

	@Override
	public void create(){
		InputManager inputManager = new InputManager();
		Gdx.input.setInputProcessor(inputManager);
		renderManager = new RenderManager(width, height);
		renderManager.setInputManager(inputManager);
		soundManager = new SoundManager();
		engine = new Engine();
		engine.setInputManager(inputManager);
		engine.getRequest().addImageRequest("Static/Static");
		engine.getRequest().addImageRequest("menu/button");
		engine.getRequest().addImageRequest("menu/window");
		engine.getRequest().addImageRequest("menu/option");
		engine.getRequest().addImageRequest("menu/scroll_bar");
		engine.getRequest().addImageRequest("menu/shadow_rat");
		engine.getRequest().addImageRequest("menu/shadow_cat");
		engine.getRequest().addSoundRequest("menu");
		engineTimer = Executors.newSingleThreadScheduledExecutor();
		engineTimer.scheduleAtFixedRate(() -> engine.update(soundManager), 0, 16, TimeUnit.MILLISECONDS);
	}

	@Override
	public void resize(int width, int height){
		if (renderManager.getCameraManager().getViewport() != null){
			renderManager.getCameraManager().getViewport().update(width, height);
			renderManager.getCameraManager().getViewport().apply();
		}
	}

	@Override
	public void render () {
		renderManager.requests(soundManager, engine.getRequest());
		renderManager.viewportAdjust();
		renderManager.render(engine);
	}
	
	@Override
	public void dispose () {
		engineTimer.shutdown();
		soundManager.dispose();
		renderManager.dispose();
	}
}
