package com.lucasfreegames.castlewars.extras;

import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.lucasfreegames.castlewars.extras.LevelCompleteWindow.StarsCount;
import com.lucasfreegames.castlewars.manager.ResourcesManager;
import com.lucasfreegames.castlewars.persistency.LevelContract;

public class CustomMenuItem extends AnimatedSpriteMenuItem{
	private boolean enabled;
	private Text levelName;
	private int starsRecord;
	public CustomMenuItem(int pID, ITiledTextureRegion region,
			VertexBufferObjectManager vbom, String levelName, int stars, boolean enabled) {
		super(pID, region, vbom);
		this.enabled=enabled;
		this.levelName = new Text(0, 0, ResourcesManager.getInstance().bigFont, levelName, vbom);
		 if (stars==0){
				if(enabled){
					setCurrentTileIndex(LevelContract.LEVEL_UNLOCKED);					
				}else{
					this.levelName.setVisible(false);
					setCurrentTileIndex(LevelContract.LEVEL_LOCKED);					
				}
		 }
		else{
			updateTileForStars(stars);
		}
		Debug.e ("enabled " +enabled +" level " + levelName + " stars "+ stars );
		this.levelName.setText(levelName);
		this.levelName.setPosition(this.levelName.getX()+(region.getWidth()/2),this.levelName.getY()+(region.getHeight()/2));
		attachChild(this.levelName);
	}
	public boolean isEnabled() {
		return enabled;
	}
	
	public void updateStars(int newStarsRecord){
		if (newStarsRecord > this.starsRecord){
			this.starsRecord = newStarsRecord;
			updateTileForStars(newStarsRecord);
			LevelContract.updateStarsRecord(Integer.parseInt(levelName.getText().toString()), newStarsRecord);
			Debug.e("Updating Stars for  Level" + levelName + " to "+newStarsRecord);
		}
	}
	
	public void enable() {
		this.enabled = true;
		setCurrentTileIndex(LevelContract.LEVEL_UNLOCKED);
		this.levelName.setVisible(true);
		Debug.e("Enabling Level" + Integer.parseInt(levelName.getText().toString()));
		LevelContract.unlockLevel(Integer.parseInt(levelName.getText().toString()));
		
	}
	
	public void updateTileForStars(int starsForTiles){
		if(starsForTiles==3){
			setCurrentTileIndex(LevelContract.LEVEL_COMPLETED_3STARS);	
		}else if (starsForTiles==2){
			setCurrentTileIndex(LevelContract.LEVEL_COMPLETED_2STARS);				
		}else if (starsForTiles==1){
			setCurrentTileIndex(LevelContract.LEVEL_COMPLETED_1STAR);	
		}
		
	}

}
