package com.lucasfreegames.castlewars.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.util.GLState;
import org.andengine.util.debug.Debug;

import android.database.Cursor;
import android.view.Window;
import android.view.WindowManager;

import com.lucasfreegames.castlewars.BaseScene;
import com.lucasfreegames.castlewars.extras.CustomMenuItem;
import com.lucasfreegames.castlewars.manager.ResourcesManager;
import com.lucasfreegames.castlewars.manager.SceneManager;
import com.lucasfreegames.castlewars.manager.SceneManager.SceneType;
import com.lucasfreegames.castlewars.persistency.LevelContract;
import com.lucasfreegames.castlewars.persistency.LevelHelper;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener, IScrollDetectorListener, IOnSceneTouchListener
{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	
	private static final int MENU_ITEM_PADDING = 20;
	private MenuScene menuChildScene;
	private HUD menuHUD;
	private SurfaceScrollDetector mScrollDetector;
	private int higestLevel;
	private static final float  menuOffsetX=-250;
	private static final float menuOffsetY=-50;
	private static float menuLeftLimit;
	private static float menuWidth;
	private static float menuRightLimit=menuOffsetX;
	
	//---------------------------------------------
	// METHODS FROM SUPERCLASS
	//---------------------------------------------

	@Override
	public void createScene()
	{
		createBackground();
		createMenuChildScene();
		refreshHUD();
		setOnSceneTouchListener(this);
		this.mScrollDetector = new SurfaceScrollDetector(this);
		registerTouchArea(this);

	}

	@Override
	public void onBackKeyPressed()
	{
		System.exit(0);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_MENU;
	}
	

	@Override
	public void disposeScene()
	{
		menuHUD.detachChildren();
		menuHUD.detachSelf();
		menuHUD.dispose();
		menuChildScene.detachChildren();
		menuChildScene.detachSelf();
		menuChildScene.dispose();
	}
	
	
	public void refreshHUD(){
 		camera.setHUD(menuHUD);
		setChildScene(menuChildScene);
	}
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
			if( canPlayLevel(pMenuItem.getID())){
				GameScene.currentLevel=  pMenuItem.getID();
				launchGame();
			}
			return true;
	}
	
	public boolean canPlayLevel(int level){
		return (level<=higestLevel);
	};
	
	public void launchGame(){
		SceneManager.getInstance().loadGameScene(engine);
	}
	
	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------
	
	private void createBackground()
	{
		menuHUD = new HUD();
		menuHUD.attachChild(new Sprite(400, 240, resourcesManager.menu_background_region, vbom)
		{
    		@Override
            protected void preDraw(GLState pGLState, Camera pCamera) 
    		{
                super.preDraw(pGLState, pCamera);
                pGLState.enableDither();
            }
		});
	}
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(camera);
		LevelHelper dbHelper = new LevelHelper(ResourcesManager.getInstance().activity);
		Cursor c= dbHelper.getLevels();
		boolean isLevelUnlocked;
		int currentLevel;
		while(c.moveToNext()){
			isLevelUnlocked= c.getString(c.getColumnIndex(LevelContract.LevelEntry.COLUMN_NAME_LEVEL_LOCK)).equals(LevelContract.LevelEntry.VALUE_LEVEL_LOCK_UNLOCKED);
			currentLevel=c.getInt(c.getColumnIndex(LevelContract.LevelEntry.COLUMN_NAME_LEVEL_ID));
			menuChildScene.addMenuItem(
						new ScaleMenuItemDecorator(
								new CustomMenuItem(
									currentLevel, 
									resourcesManager.menu_level_region, 
									vbom, 
									currentLevel+"",
									c.getInt(c.getColumnIndex(LevelContract.LevelEntry.COLUMN_NAME_LEVEL_STARS)),
									isLevelUnlocked)
							, 1.2f, 1) 
						);
			if(isLevelUnlocked)this.higestLevel= currentLevel;
		}
		menuChildScene.buildAnimations();
		menuChildScene.setOnMenuItemClickListener(this);
		setMenuLayoutToHorizontal(menuChildScene, MENU_ITEM_PADDING);
		menuChildScene.setCamera(camera);
		//Left limit shouldn't be less than the menu width minus the ofsset  
		menuLeftLimit=-menuWidth-menuOffsetX;
		menuChildScene.setPosition(menuOffsetX,-(menuChildScene.getChildCount()* menuChildScene.getChildByIndex(0).getHeight()/2.2f)+menuOffsetY);
		setChildScene(menuChildScene);
		//Debug.e( menuChildScene.getY() +"  "+ menuChildScene.getX()+ "  " + camera.getSurfaceWidth() ); 
		}
	
	/**
	* Sets menu items layout to horizontal 
	* @author  Lucas Massuh
	* @version 1.0
	* @since   2015-05-28
	* @return returns the "horizontalised" menu, just in case. 
	* @param menu This should be the menu scene containing all the Menu Item children to be set horizontal 
	* @param padding This is the horizontal padding between each Menu Item
	*/	public static MenuScene setMenuLayoutToHorizontal(MenuScene menu, int padding){
		if (menu.getChildCount()<=1) return menu;
		// Starts at 1 since child[0] position won't be changed
		for(int i=1; i<menu.getChildCount();i++){
			menu.getChildByIndex(i).setPosition(
					//it can be optimized by pre-calculating item's width if they are all equal
					menu.getChildByIndex(i-1).getX()+menu.getChildByIndex(i).getWidth()+padding,
                    menu.getChildByIndex(0).getY());
			menuWidth+=menu.getChildByIndex(i).getWidth()+padding;
		}
		return menu;
	}


	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		
	}

	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
			float newX = menuChildScene.getX()+pDistanceX;
			if (newX<menuLeftLimit){
				newX= menuLeftLimit;
			}
			if (newX>menuRightLimit){
				newX= menuRightLimit;
			}
		//Debug.e("MENU Y: "+menuChildScene.getY()+ "X: "+menuChildScene.getX()+" MENU MIN X"+menuLeftLimit);
		menuChildScene.setPosition(newX, menuChildScene.getY());
	}

	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		return false;
	}
	
}