package com.lucasfreegames.castlewars.extras;

import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import com.lucasfreegames.castlewars.manager.ResourcesManager;

public class CustomMenuItem extends SpriteMenuItem {
	public CustomMenuItem(int pID, ITextureRegion region,
			VertexBufferObjectManager vbom, String text) {
		super(pID, region, vbom);
		final Text menuText= new Text(0, 0, ResourcesManager.getInstance().bigFont, text, vbom);
		menuText.setText(text);
		menuText.setPosition(menuText.getX()+(region.getWidth()/2),menuText.getY()+(region.getHeight()/2));
		attachChild(menuText);
	}

}
