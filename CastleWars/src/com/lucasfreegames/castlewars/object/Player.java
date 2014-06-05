package com.lucasfreegames.castlewars.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.lucasfreegames.castlewars.manager.ResourcesManager;
import com.lucasfreegames.castlewars.scene.GameScene;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public abstract class Player extends AnimatedSprite
{
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------
	
	protected static final int PROJECTILE_OFFSET_X = 50;
	protected static final int PROJECTILE_OFFSET_Y = 45;
	private static final long[] ANIMATION_LAUNCH = new long[] { 100,100,100,100,100};

	private Body body;
	private Projectile bomb;
	private boolean shooting;

	// ---------------------------------------------
	// GETTERS AND SETTERS
	// ---------------------------------------------
	public Projectile getBomb() {
		return bomb;
	}

	public void setBomb(Projectile bomb) {
		this.bomb = bomb;
	}

	
	public void setShooting(boolean shooting) {
		this.shooting = shooting;
	}

	protected boolean isShooting(){
			return shooting;
	}
	
	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
	{
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		createPhysics(camera, physicsWorld);
		setShooting(false);
	}
	
	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{		
		//Create the phiscs body so we can animate it, move it, etc...
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		//Add a tag so we can identify it when get loaded by the level loader on GameScene
		body.setUserData(GameScene.TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER);
				
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);
				if (getY() <= 0)
				{					
					onDie();
				}
	        }
		});
	}
		
	public boolean startShooting()
	{
		// Don't do the shooting animation if is still shooting or the bomb is active
		if (isShooting()||bomb.isExploding()|| bomb.isFalling())
			return false;
		//animate the shooting and listen for its end
		animate(ANIMATION_LAUNCH, 0,ANIMATION_LAUNCH.length-1,false,  new IAnimationListener() {
	        public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
	                int pInitialLoopCount) {
	        }

	        public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
	                int pRemainingLoopCount, int pInitialLoopCount) {
	        }

	        public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
	                int pOldFrameIndex, int pNewFrameIndex) {
	        	//before it ends we throw the projectile, but we let it continue so it it's more realistic
	        	if (pNewFrameIndex== (ANIMATION_LAUNCH.length-3)) bomb.launch(getX()+PROJECTILE_OFFSET_X, getY()+PROJECTILE_OFFSET_Y);
	        }

	        public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	           //Bring back the element to the original position once the animation has ended
	        	stop();
	        }
	    });
		return true;
	}
	
	public void stop(){
		body.setLinearVelocity(0f, 0f);
		body.setType(BodyType.StaticBody);
		setShooting(false);
		stopAnimation(0);
	}
	
	public abstract void onDie();
}