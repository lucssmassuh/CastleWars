package com.lucasfreegames.castlewars.object;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.lucasfreegames.castlewars.BaseScene;
import com.lucasfreegames.castlewars.manager.ResourcesManager;
import com.lucasfreegames.castlewars.manager.SceneManager;
import com.lucasfreegames.castlewars.scene.GameScene;

/**
 * @author Mateusz Mysliwiec
 * @author www.matim-dev.com
 * @version 1.0
 */
public class Projectile extends AnimatedSprite
{
	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------
	
	private Body body;
	private boolean falling =false;
	private boolean exploding=false;
	public static final int MAX_VELOCITY_X = 45;
	public static final int MAX_VELOCITY_Y = 25;
	public Vector2 currentVelocity= new Vector2(0, 0);
	final long[] STONE_EXPLODING = new long[] {100,200,300,1000};
	final long[] STONE_FLYING_ANIMATION = new long[] { 100,150,200,250,300,350,400,500};
	
	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------
	

	public boolean isExploding() {
		return exploding;
	}

	public void updateCurrentVelocityParameters(float x, float y) {
		currentVelocity = new Vector2(x, y);
	}
	
	public void setExploding(boolean exploding) {
		this.exploding = exploding;
	}

	public Projectile(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
	{
		super(pX, pY, ResourcesManager.getInstance().projectile_region, vbo);
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
	}
	
	// ---------------------------------------------
	// CLASS LOGIC
	// ---------------------------------------------
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{		
		body = PhysicsFactory.createCircleBody(physicsWorld, this, BodyType.KinematicBody, PhysicsFactory.createFixtureDef(1f, 0.1f, 20f));
		body.setUserData(GameScene.TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PROJECTILE);
		this.setVisible(false);
		body.isBullet();
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
		{
			@Override
	        public void onUpdate(float pSecondsElapsed)
	        {
				super.onUpdate(pSecondsElapsed);
					if (getY()<=0){
						reloadStone();
					}
	        }
		});
	}
	
	
	public void launch(float x, float y){
		if(isFalling()) return;
		if(isExploding()) return;
		setVisible(true);
		body.setType(BodyType.DynamicBody);
		body.setTransform(new Vector2(x/32,y/32), 0);
		body.setLinearVelocity(currentVelocity);
		animate(STONE_FLYING_ANIMATION, 0, 7,true); 
		body.setActive(true);
		setFalling(true);
		setExploding(false);
		}


	public void startExploding(){
		if(isExploding()) return;
		setExploding(true);
		animate(STONE_EXPLODING, 0, 3, false,  new IAnimationListener() {
	        public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
	                int pInitialLoopCount) {
	        }

	        public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
	                int pRemainingLoopCount, int pInitialLoopCount) {
	        }

	        public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
	                int pOldFrameIndex, int pNewFrameIndex) {
	        }

	        public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
	            reloadStone();
	        }
	    });

	}

	
	public void setFalling(boolean isfalling) {
		this.falling = isfalling;
	}

	public boolean isFalling() {
		return falling;
	}

	public void reloadStone(){
		body.setActive(false);
		body.setType(BodyType.StaticBody);
		setVisible(false);
		setExploding(false);
		setFalling(false);
		body.setTransform(new Vector2(0,0), 0);
	}
	
	public void onDie(){
		
	}
	
	
}