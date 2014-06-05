package com.lucasfreegames.castlewars.extras;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.vbo.VertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.lucasfreegames.castlewars.manager.ResourcesManager;

public class LevelCompleteWindow extends Sprite
{
	private TiledSprite star1;
	private TiledSprite star2;
	private TiledSprite star3;
	private Text displayText;
	public Text getDisplayText() {
		return displayText;
	}

	VertexBufferObjectManager vbom;

	public enum StarsCount
	{
		ZERO,
		ONE,
		TWO,
		THREE
	}
	
	public LevelCompleteWindow(VertexBufferObjectManager pSpriteVertexBufferObject)
	{
		super(0, 0, 650, 400, ResourcesManager.getInstance().complete_window_region, pSpriteVertexBufferObject);
		vbom = pSpriteVertexBufferObject;
		displayText= new Text(300, 300,ResourcesManager.getInstance().bigFont, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", vbom);
		attachStars();
	}
	
	private void attachStars()
	{
		star1 = new TiledSprite(150, 200, ResourcesManager.getInstance().complete_stars_region, vbom);
		star2 = new TiledSprite(325, 200, ResourcesManager.getInstance().complete_stars_region, vbom);
		star3 = new TiledSprite(500, 200, ResourcesManager.getInstance().complete_stars_region, vbom);
		
		attachChild(star1);
		attachChild(star2);
		attachChild(star3);
	}
	
	/**
	 * Change star`s tile index, depends on stars count.
	 * @param starsCount
	 */
	public void display(StarsCount starsCount, Scene scene, Camera camera)
	{
		if(hasParent())return;
		// Change stars tile index, based on stars count (1-3)
		switch (starsCount)
		{
			case ZERO:
				star1.setCurrentTileIndex(1);
				star2.setCurrentTileIndex(1);
				star3.setCurrentTileIndex(1);
				displayText.setText("Try again rookie!");
				break;
			case ONE:
				star1.setCurrentTileIndex(0);
				star2.setCurrentTileIndex(1);
				star3.setCurrentTileIndex(1);
				displayText.setText("Not bad stonehead!");
				break;
			case TWO:
				displayText.setText("On the rocks!");
				star1.setCurrentTileIndex(0);
				star2.setCurrentTileIndex(0);
				star3.setCurrentTileIndex(1);
				break;
			case THREE:
				star1.setCurrentTileIndex(0);
				star2.setCurrentTileIndex(0);
				star3.setCurrentTileIndex(0);
				displayText.setText("Yeah! You ROCK!");
				break;
		}
		
		// Hide HUD
		camera.getHUD().setVisible(false);
		
		// Disable camera chase entity
		camera.setChaseEntity(null);
		
		// Attach our level complete panel in the middle of camera
		setPosition(camera.getCenterX(), camera.getCenterY());
		scene.attachChild(this);
		
	}

	

}