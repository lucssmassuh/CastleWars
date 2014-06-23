package com.lucasfreegames.castlewars.manager;

import java.io.IOException;
import java.io.InputStream;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.lucasfreegames.castlewars.GameActivity;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class ResourcesManager
{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public GameActivity activity;
	public SmoothCamera camera;
	public VertexBufferObjectManager vbom;
	public int numberOfLevels=0;
	
	public Font bigFont;
	public Font smallFont;
	
	//---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	//---------------------------------------------
	
	public ITextureRegion splash_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion options_region;
	public ITextureRegion menu_next_region;
	public ITextureRegion menu_replay_region;
	public ITextureRegion menu_menu_region;
	public ITiledTextureRegion menu_level_region;

	
	// Game Texture
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	
	// Game Texture Regions
	public ITextureRegion plataform_region;
	public ITextureRegion obstacle_region;
	public ITextureRegion target_region;
	public ITextureRegion finDeAnio_region;
	public ITiledTextureRegion player_region;
	public ITiledTextureRegion projectile_region;
	
	private BitmapTextureAtlas splashTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	// Level Complete Window
	public ITextureRegion complete_window_region;
	public ITiledTextureRegion complete_stars_region;
	
	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------

	public void loadMenuResources()
	{
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}
	
	public void loadGameResources()
	{
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}
	
	private void loadMenuGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
        menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "frame.png");
        play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
        options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
        //menu_level_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "level.png");
        menu_level_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuTextureAtlas, activity, "level.png", 5, 1);
    	try 
    	{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} 
    	catch (final TextureAtlasBuilderException e)
    	{
			Debug.e(e);
		}
	}
	
	private void loadMenuAudio()
	{
		
	}
	
	private void loadMenuFonts()
	{
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 300, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture secondaryFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 300, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		bigFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
		bigFont.load();
		smallFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), secondaryFontTexture , activity.getAssets(), "font.ttf", 25, true, Color.GRAY, 2, Color.BLACK);
		smallFont.load();
	}

	private void loadGameGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        
       	plataform_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "base.png");
       	obstacle_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "stone.png");
       	target_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "target.png");
        player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "catapult.png", 7, 1);
        projectile_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "bomb.png", 4, 2);

        //LEVEL END MENU
        menu_next_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "next.png");
        menu_replay_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "retry.png");
        menu_menu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "menu.png");
        
        complete_window_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "levelCompleteWindow.png");
        complete_stars_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "star.png", 2, 1);

    	try 
    	{
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
		} 
    	catch (final TextureAtlasBuilderException e)
    	{
			Debug.e(e);
		}
	}
	
	private void loadGameFonts()
	{
		
	}
	
	private void loadGameAudio()
	{
		
	}
	
	public void unloadGameTextures()
	{
		// TODO (Since we did not create any textures for game scene yet)
	}
	
	public void loadSplashScreen()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
        splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
        splashTextureAtlas.load();	
	}
	
	public void unloadSplashScreen()
	{
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	public void unloadMenuTextures()
	{
		menuTextureAtlas.unload();
	}
	
	public void loadMenuTextures()
	{
		menuTextureAtlas.load();
	}
	
	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br><br>
	 * We use this method at beginning of game loading, to prepare Resources Manager properly,
	 * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, GameActivity activity, SmoothCamera camera, VertexBufferObjectManager vbom)
	{
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}
	
	//---------------------------------------------
	// GETTERS AND SETTERS
	//---------------------------------------------
	
	public int getNumberOfLevels() {
		return numberOfLevels;
	}

	public void setNumberOfLevels(int numberOfLevels) {
		this.numberOfLevels = numberOfLevels;
	}

	public static ResourcesManager getInstance()
	{
		return INSTANCE;
	}
	
}