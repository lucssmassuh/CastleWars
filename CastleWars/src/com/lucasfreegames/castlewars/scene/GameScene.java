package com.lucasfreegames.castlewars.scene;

import java.io.IOException;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.lucasfreegames.castlewars.BaseScene;
import com.lucasfreegames.castlewars.extras.LevelCompleteWindow;
import com.lucasfreegames.castlewars.extras.LevelCompleteWindow.StarsCount;
import com.lucasfreegames.castlewars.manager.SceneManager;
import com.lucasfreegames.castlewars.manager.SceneManager.SceneType;
import com.lucasfreegames.castlewars.object.Player;
import com.lucasfreegames.castlewars.object.Projectile;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
/**
 * @author lamassuh
 *
 */
public class GameScene extends BaseScene implements IOnSceneTouchListener, IOnMenuItemClickListener
{
	private HUD gameHUD;
	private Text horizontalImpulseText;
	private Text verticalImpulseText;
	private Text availableShootsText;
	
	private PhysicsWorld physicsWorld;
	private LevelCompleteWindow levelCompleteWindow;
	
	private static final String TAG_LEVEL_PARAMETER="parameter";
	private static final String TAG_LEVEL_PARAMETER_NAME="name";
	private static final String TAG_LEVEL_PARAMETER_VALUE="value";
	//accepted values for params
	private static final String TAG_LEVEL_PARAMETER_VALUE_NUMBEROFSHOTS="shots";
	private static final String TAG_LEVEL_PARAMETER_VALUE_INSTRUCTIONS="instructions";
	private static final String TAG_LEVEL_PARAMETER_VALUE_PAR3="par3";
	private static final String TAG_LEVEL_PARAMETER_VALUE_PAR2="par2";
	private static final String TAG_LEVEL_PARAMETER_VALUE_GOAL="goal";
	
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	
	public static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM = "platform";
	public static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TARGET = "target";
	public static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE = "obstacle";
	public static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	public static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PROJECTILE = "projectile";
	private static final int MENU_NEXT = 0;
	private static final int MENU_RETRY = 1;
	private static final int MENU_MENU = 2;
	
 
	public static int currentLevel=1;
	private boolean levelOver;
	private Player playerCatapult;
	private Projectile projectileStone;
	private int numberOfShootsAvailable;
	private int levelPar2Stars;
	private int levelPar3Stars;			
	private int goal=0;
	private int maxX=0;
	private boolean goalAccomplished;
	private static String instructions= "Drag to shoot";
	
	public void setGoalAccomplished(boolean goalAccomplished) {
		this.goalAccomplished = goalAccomplished;
	}

	private String levelInstrucions;
	

	private int numberOfShootsFired=0;
	private Text gameOverText;
	private boolean levelEndDisplayed = false;
	
	float originalX=0,originalY=0, newX=0,newY=0, impulseX=0,impulseY=0;
	private MenuScene menuChildScene;

	
	private static float horizontalImpulse=0;
	private static float verticalImpulse=0;


	@Override
	public void createScene()
	{
		createBackground();
		createHUD();
		createPhysics();
		loadLevel(currentLevel);
		levelCompleteWindow = new LevelCompleteWindow(vbom);
		setOnSceneTouchListener(this);
		resetCounters();
		flashText(instructions, 1f,camera.getHUD());
	}
	

	public boolean isLastLevel(){
		return (resourcesManager.getNumberOfLevels() == currentLevel);
	}
	
	public void resetCounters(){
		numberOfShootsFired=0;
		levelOver=false;
		levelEndDisplayed=false;
		camera.setChaseEntity(playerCatapult.getBomb());
	}

	@Override
	public void onBackKeyPressed()
	{
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType()
	{
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene()
	{
		camera.setHUD(null);
		camera.setChaseEntity(null);
		camera.setCenterDirect(400, 240);
		unregisterUpdateHandler(physicsWorld);
	}
	
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent event) 
	{
		int myEventAction = event.getAction();
        float X = event.getX();
        float Y = event.getY();

        switch (myEventAction) {
           case MotionEvent.ACTION_DOWN:
        	
   			originalX= X;
   			originalY= Y;
        	break;
           case MotionEvent.ACTION_MOVE: {
   			newX= X;
   			newY= Y;
   			horizontalImpulse = originalX-newX;
   			verticalImpulse= originalY-newY;
     	    updateLaunchingParmsOnScreen();
            break;}
           case MotionEvent.ACTION_UP:
        	   projectileStone.updateCurrentVelocityParameters(impulseX, impulseY);
        	   if (playerCatapult.startShooting())
        	   {
        		   numberOfShootsFired++;
        		   if (refreshNumberOfShots()<=0){
        			   setLevelOver(true);
        		   }else{
        			   camera.setChaseEntity(projectileStone);
        		   }
        	   }
                break;
        }
		return false;
	}
	
	private void loadLevel(int levelID)
	{
		final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
		final FixtureDef BASIC_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0.1f, 0.1f, 0.5f);
		final FixtureDef PROJECTILE_FIXTURE_DEF_STONE = PhysicsFactory.createFixtureDef(1, 0.1f, 0.8f);
		
		//Load the root element, which is "level", a standardone on the engine, it has some useful 
		//constants for screen size
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
			{
				final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);

				camera.setBounds(0, 0, width, height); // here we set camera bounds
				camera.setBoundsEnabled(true);

				return GameScene.this;
			}
		});

		//Loads parameters related to a particular level (generic parameters for all levels shouldn't be added here)
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_LEVEL_PARAMETER)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
			{
				final String param_name = SAXUtils.getAttributeOrThrow(pAttributes, TAG_LEVEL_PARAMETER_NAME);
				final String param_value = SAXUtils.getAttributeOrThrow(pAttributes, TAG_LEVEL_PARAMETER_VALUE);
				
				if (param_name.equals(TAG_LEVEL_PARAMETER_VALUE_NUMBEROFSHOTS))
					{
					numberOfShootsAvailable = Integer.parseInt(param_value);
					refreshNumberOfShots();
					}
				if (param_name.equals(TAG_LEVEL_PARAMETER_VALUE_INSTRUCTIONS))
					{
						levelInstrucions = param_value;
					}
				if (param_name.equals(TAG_LEVEL_PARAMETER_VALUE_PAR3))
					{
						levelPar3Stars= Integer.parseInt(param_value);
					}
				if (param_name.equals(TAG_LEVEL_PARAMETER_VALUE_PAR2))
					{
						levelPar2Stars= Integer.parseInt(param_value);
					}
				if (param_name.equals(TAG_LEVEL_PARAMETER_VALUE_GOAL))
					{
						goal= Integer.parseInt(param_value);
					}

				return new Sprite(-200, -200, resourcesManager.plataform_region, vbom);
			}
		});

			//loads all level entities, everything you see on screen
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
			{
				final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
				final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
				final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
				
				final Sprite levelObject;
				
				if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM))
				{
					levelObject = new Sprite(x, y, resourcesManager.plataform_region, vbom)
					{
						@Override
						protected void onManagedUpdate(float pSecondsElapsed) 
						{
							super.onManagedUpdate(pSecondsElapsed);
/*							if (projectileStone.collidesWith(this))
							{
								projectileStone.startExploding();
								if (!procesImpactAndContinue()){
									displayGameOverText();
								}
							}*/
						}

					};

					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.StaticBody, BASIC_FIXTURE_DEF);
					body.setUserData(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM);
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));					
				} 
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE))
				{
					levelObject = new Sprite(x, y, resourcesManager.obstacle_region, vbom)
					{
						@Override
						protected void onManagedUpdate(float pSecondsElapsed) 
						{
							super.onManagedUpdate(pSecondsElapsed);
						}
					};
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.DynamicBody, PROJECTILE_FIXTURE_DEF_STONE);
					body.setUserData(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE);
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TARGET))
				{
					camera.setBoundsEnabled(true);
					camera.setBounds(camera.getBoundsXMin(), camera.getBoundsYMin(), x+camera.getWidth()/2, camera.getHeight()+100);
					levelObject = new Sprite(x, y, resourcesManager.target_region, vbom);
					final Body body = PhysicsFactory.createBoxBody(physicsWorld, levelObject, BodyType.DynamicBody, PROJECTILE_FIXTURE_DEF_STONE);
					body.setUserData(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TARGET);
					physicsWorld.registerPhysicsConnector(new PhysicsConnector(levelObject, body, true, false));
				}
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER))
				{
					playerCatapult = new Player(x, y, vbom, camera, physicsWorld)
					{
						@Override
						public void onDie()
						{
						}
					};
					playerCatapult.setBomb(projectileStone);
					levelObject = playerCatapult;
				}
				
				else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PROJECTILE))
				{
					projectileStone = new Projectile(x, y, vbom, camera, physicsWorld)
					{
						@Override
						public void onDie()
						{
							projectileStone.setVisible(false);
							projectileStone.setIgnoreUpdate(true);
						}
					};
					levelObject = projectileStone;
				}
				else
				{
					throw new IllegalArgumentException();
				}

				levelObject.setCullingEnabled(true);

				return levelObject;
			}
		});
		levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	}

	private int  refreshNumberOfShots(){
		int remainingShots= numberOfShootsAvailable-numberOfShootsFired;
		availableShootsText.setText("Shots: "+(remainingShots));
		return remainingShots;
	}
	
	/**
	* Shows a text for a specific period of time
	*
	* @author  Lucas Massuh
	* @version 1.0
	* @since   2015-05-25
	* @param textToFlash This is the text that is going to be displayed on screen
	* @param timeVisible This is the number of seconds the text is going to be visible for
	*/
	private void flashText(String textToFlash, float timeVisible){
		final Text flashText= new Text(0, 0, resourcesManager.bigFont, textToFlash, vbom);
		attachChild(flashText);
		flashText.setPosition(camera.getCenterX(), camera.getCenterY());
		engine.registerUpdateHandler(new TimerHandler(timeVisible, new ITimerCallback()
		{									
		    public void onTimePassed(final TimerHandler pTimerHandler)
		    {
		    	pTimerHandler.reset();
    			engine.unregisterUpdateHandler(pTimerHandler);
    			detachChild(flashText);
		    }
		}));
		
	}

	/**
	* Shows a text for a specific period of time on a specific HUD
	*
	* @author  Lucas Massuh
	* @version 1.0
	* @since   2015-05-25
	* @param textToFlash This is the text that is going to be displayed on screen
	* @param timeVisible This is the number of seconds the text is going to be visible for
	*/
	private void flashText(String textToFlash, float timeVisible, final HUD myHUD){
		final Text flashText= new Text(0, 0, resourcesManager.bigFont, textToFlash, vbom);
		flashText.setPosition(camera.getCenterX(), camera.getCenterY());
		myHUD.attachChild(flashText);
		engine.registerUpdateHandler(new TimerHandler(timeVisible, new ITimerCallback()
		{									
		    public void onTimePassed(final TimerHandler pTimerHandler)
		    {
		    	pTimerHandler.reset();
    			engine.unregisterUpdateHandler(pTimerHandler);
    			myHUD.detachChild(flashText);
		    }
		}));
		
	}

	
	//Permanently visible text with power, aim, and available shoots 
	private void createHUD()
	{
		gameHUD = new HUD();
		
		horizontalImpulseText = new Text(20, 420, resourcesManager.bigFont, "Power: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		horizontalImpulseText.setAnchorCenter(0, 0);	
		horizontalImpulseText.setText("Power: 0");
		
		verticalImpulseText = new Text(20, 380, resourcesManager.bigFont, "Elevation: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		verticalImpulseText.setAnchorCenter(0, 0);	
		verticalImpulseText.setText("Aim: 0");
		
		availableShootsText = new Text(20, 340, resourcesManager.bigFont, "Shots: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		availableShootsText.setAnchorCenter(0, 0);	
		availableShootsText.setText("Shots: 0");

		gameHUD.attachChild(horizontalImpulseText);
		gameHUD.attachChild(verticalImpulseText);
		gameHUD.attachChild(availableShootsText);
		camera.setHUD(gameHUD);
	}

	
	private boolean isGoalAccomplished(){
		return goalAccomplished;
	}
	
	private void updateLaunchingParmsOnScreen(){
		impulseX=  projectileStone.MAX_VELOCITY_X * (horizontalImpulse/ 800); 
		impulseY= projectileStone.MAX_VELOCITY_Y * (verticalImpulse/ 400);
		impulseX=(float)Math.round(impulseX *100)/100;
		impulseY=(float)Math.round(impulseY *100)/100;
		horizontalImpulseText.setText("Power: " + impulseX);
		verticalImpulseText.setText("Aim: " + impulseY);
	}
	
	private void createBackground()
	{
		setBackground(new Background(Color.BLUE));
	}
	
	private boolean isLevelOver(){
		return levelOver;
	}
	
	private void setLevelOver(boolean status){
		levelOver=status;
	}
	
	
	private boolean processImpactAndContinue(){
		if(!isLevelOver())
		{
			engine.registerUpdateHandler(new TimerHandler(3f, new ITimerCallback()
			{									
			    public void onTimePassed(final TimerHandler pTimerHandler)
			    {
			    	pTimerHandler.reset();
	    			engine.unregisterUpdateHandler(pTimerHandler);
	    			if(!isLevelOver())
	    				{
	    				camera.setChaseEntity(projectileStone);
	    				}
			    }
			}));
			return true;
		}else {
			projectileStone.setVisible(false);
			projectileStone.setIgnoreUpdate(true);
			engine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback()
			{									
			    public void onTimePassed(final TimerHandler pTimerHandler)
			    {
	    			engine.unregisterUpdateHandler(pTimerHandler);
	    			
	    			manageLevelEnding();
			    }
			}));
			return false;
		}
	}

	public boolean hasPlayerConsumedAllShots(){
		return (numberOfShootsAvailable<=numberOfShootsFired);
	}
	
	public StarsCount getStarsCount(){
		if (isGoalAccomplished()){
			if(numberOfShootsFired<=levelPar3Stars){
				return StarsCount.THREE;		
			}else if (numberOfShootsFired<=levelPar2Stars){
				return StarsCount.TWO;
			}else {
				return StarsCount.ONE;
			}
		}else{
			return StarsCount.ZERO;			
		}
	}

	public int getStarsCountInt(){
		if (isGoalAccomplished()){
			if(numberOfShootsFired<=levelPar3Stars){
				return 3;		
			}else if (numberOfShootsFired<=levelPar2Stars){
				return 2;
			}else {
				return 1;
			}
		}else{
			return 0;			
		}
	}

	public void manageLevelEnding(){
		if(levelEndDisplayed)return;
		instructions="";
		camera.setChaseEntity(null);
		levelCompleteWindow.display(getStarsCount(), GameScene.this, camera);
		displayText(levelCompleteWindow.getDisplayText(),camera.getCenterX(),camera.getCenterY()+120);
		//Only show next button on levelEndMenu if goal was accomplished and it is not the last level
		createMenuChildScene(!isLastLevel() && isGoalAccomplished());
		if(isGoalAccomplished()){
			MainMenuScene.handleLevelComplete(currentLevel, getStarsCountInt());
		}
		
	}
	
	public void displayText(Text t, float x, float y){
		if (t.hasParent())return;
		t.setPosition(x, y);
		attachChild(t);
		levelEndDisplayed =true;
	}
		
	private void createPhysics()
	{
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false); 
		physicsWorld.setContactListener(contactListener());
		registerUpdateHandler(physicsWorld);
	}
	
	
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
		switch(pMenuItem.getID())
		{
			//Load next level
			case MENU_NEXT:
				disposeScene();
				currentLevel++;
				SceneManager.getInstance().loadGameScene(engine);
				return true;
			//retry current level
			case MENU_RETRY:
				disposeScene();
				SceneManager.getInstance().loadGameScene(engine);
				return true;
			case MENU_MENU:
				((SmoothCamera)engine.getCamera()).setCenterDirect(400, 240);
				SceneManager.getInstance().loadMenuScene(engine);
			default:
				return false;
		}
	}
	
	private void createMenuChildScene(boolean showNext)
	{
		menuChildScene = new MenuScene(camera);
		
		//RETRY MENU HANDLING
		final IMenuItem retryMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RETRY, resourcesManager.menu_replay_region, vbom), 1.2f, 1);
		menuChildScene.addMenuItem(retryMenuItem);

		//MAIN MENU HANDLING
		final IMenuItem mainMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MENU, resourcesManager.menu_menu_region, vbom), 1.2f, 1);
		menuChildScene.addMenuItem(mainMenuItem);

		
		//NEXT MENU HANDLING
		if (showNext){
		final IMenuItem nextLevel = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_NEXT, resourcesManager.menu_next_region, vbom), 1.2f, 1);
		menuChildScene.addMenuItem(nextLevel);
		}
		menuChildScene.setOnMenuItemClickListener(this);
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		MainMenuScene.setMenuLayoutToHorizontal(menuChildScene, 100);
		menuChildScene.setPosition(-160, -170);
		setChildScene(menuChildScene);
	}
	
	
	
	// ---------------------------------------------
	// INTERNAL CLASSES
	// ---------------------------------------------
	
	private ContactListener contactListener()
	{
		ContactListener contactListener = new ContactListener()
		{
			public void beginContact(Contact contact)
			{
				Fixture x1 = contact.getFixtureA();
				Fixture x2 = contact.getFixtureB();
				
				// Start exploding the projectile if it hits the platform or the obstacles
				if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
				{
					//projectile hits the stone
					if ( (x1.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PROJECTILE) 
								&& (x2.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE)
									||x2.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM)) 
						   )||
						 (
						 (x1.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_OBSTACLE)
						 ||x1.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM)) 
						 && x2.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PROJECTILE))
					    )
							{
						camera.setChaseEntity(null);
						projectileStone.startExploding();
						processImpactAndContinue();
						
							}
					//Target hits the platform (goal accomplished)
					if (    x1.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TARGET) &&
							x2.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM)
							||
							x2.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TARGET) &&
							x1.getBody().getUserData().equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLATFORM)
						)
							{
								camera.setChaseEntity(null);
								setGoalAccomplished(true);
								setLevelOver(true);
								processImpactAndContinue();	
							}

				}
			}

			
			public void endContact(Contact contact)
			{
			}
			
			

			public void preSolve(Contact contact, Manifold oldManifold)
			{
			}

			public void postSolve(Contact contact, ContactImpulse impulse)
			{
			}
			
			public boolean checkColission(Contact contact, String n1, String n2){
				Fixture x1 = contact.getFixtureA();
				Fixture x2 = contact.getFixtureB();
				// Start exploding the projectile if it hits the platform or the obstacles
				if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
				{

					if (    x1.getBody().getUserData().equals(n1) &&
							x2.getBody().getUserData().equals(n2)
							||
							x2.getBody().getUserData().equals(n1) &&
							x1.getBody().getUserData().equals(n2)
						)
							{
								return true;	
							}
				}
				//return false if collission does not match conditions
				return false;
			}
		};
		return contactListener;
	}

}