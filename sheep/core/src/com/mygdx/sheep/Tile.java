package com.mygdx.sheep;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;


public class Tile
{
	public Vector2 pos;
	protected SheepGame sheepGame;
	protected AssetHolder assetHolder;
	protected boolean exists = true;
	public static Texture getTex(AssetHolder assetHolder)
	{
		return null;
	}
	public static class TileJson
	{
		public String type;
		public Vector2 pos;
	}
	public TileJson getJsonObj()
	{
		TileJson tj = startJsonObject();
		continueJsonObject(tj);
		return tj;
	}
	public Tile makeFromJsonObject(TileJson tj)
	{
		pos = tj.pos;
		return this;
	}
	public TileJson startJsonObject()
	{
		return new TileJson();
	}
	public TileJson continueJsonObject(TileJson tj)
	{
		tj.type = getTypeStr();
		tj.pos = pos;
		return tj;
	}
	public String getTypeStr()
	{
		return "Tile";
	}
	
	public void create(SheepGame sheepGame)
	{
		this.sheepGame = sheepGame;
	}
		
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
	}
	
	public Tile()
	{
		pos = new Vector2();
	}
	
	public Tile set(float x, float y)
	{
		pos.x = x;
		pos.y = y;
		return this;
	}
	
	public Vector2 getPos()
	{
		return pos;
	}
	
	public boolean getExists()
	{
		return exists;
	}
	
	public boolean touchedDown()
	{
		return false;
	}
	
	public boolean touchedUp()
	{
		return false;
	}
	
	public Vector2 getFlrPos()
	{
		Vector2 rtn = pos.cpy();
		rtn.x = (int)Math.floor(rtn.x);
		rtn.y = (int)Math.floor(rtn.y);
		return rtn;
	}
	
	public Vector2 getRndPos()
	{
		Vector2 rtn = pos.cpy();
		rtn.x = (int)Math.round(rtn.x);
		rtn.y = (int)Math.round(rtn.y);
		return rtn;
	}
	
	public boolean checkOverlap(Vector2 v)
	{
		Vector2 dist = getRndPos().cpy().sub(v);
		if (dist.x == 0 &&
			dist.y == 0)
		{
			return true;
		}
		return false;
	}
	
	public boolean checkOverlap(Tile t)
	{
		Vector2 dist = t.getRndPos().cpy().sub(getRndPos());
		if (dist.x == 0 &&
			dist.y == 0)
		{
			return true;
		}
		return false;
	}
	
	public Texture getTex()
	{
		return null;
	}
	
	public void draw(SpriteBatch batch, float delta)
	{
		if (exists)
		{
			drawSprite(batch, getTex(), pos.x, pos.y);
		}
	}
	
	public void drawSprite(SpriteBatch batch, Texture tex, float x, float y)
	{
		drawSprite(batch, tex, x, y, false);
	}
	
	public void drawSprite(SpriteBatch batch, Texture tex, float x, float y, boolean flipX)
	{
		drawSprite(batch, tex, x, y, flipX, 1.0f);
	}
	
	public void drawSprite(SpriteBatch batch, Texture tex, float x, float y, float alpha)
	{
		drawSprite(batch, tex, x, y, false, alpha);
	}
	
	public void drawSprite(SpriteBatch batch, Texture tex, float x, float y, boolean flipX, float alpha)
	{
		batch.setColor(1.0f, 1.0f, 1.0f, alpha);
		
		float tileW = sheepGame.getTileWidth();
		float tileH = sheepGame.getTileHeight();
		float startX = sheepGame.getStartX();
		float startY = sheepGame.getStartY();
		
		float flip = 1;
		if (flipX)
			flip = -1;
		
		batch.draw(tex, startX+(x+(flip==-1?1:0))*tileW+sheepGame.getOffsetX(), startY+y*tileH+sheepGame.getOffsetY(), tileW*flip, tileH);
	}
	
	public void update(float delta)
	{
		
	}
}