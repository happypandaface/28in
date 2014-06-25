package com.mygdx.sheep;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
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
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;

public class EndlessInches extends SheepGame
{
	private Pen startPen;
	private Pen endPen;
	private Array<Vector2> oldPath = null;
	
	private float oldPathFade = 0f;
	private float oldPathFadeMax = 1f;
	
	protected float scrollHeight;
	protected float scrollWidth;
	protected float scrollSpeed = 0.4f;
	
	public void playEndless()
	{
		startPen = new Pen();
		startPen.set(1, 3);
		endPen = new Pen();
		endPen.set(3, 7);
		addTile(startPen);
		addTile(endPen);
		addTile(new SheepObj().setOffset(0));
		addTile(new SheepObj().setOffset(-4));
		playingEndless = true;
	}
	public void reset()
	{
		scrollHeight = 0;
		startPen = null;
		endPen = null;
		super.reset();
	}
	public void render()
	{
		float delta = Gdx.graphics.getDeltaTime();
		if (gameOverlay.isPaused())
			delta = 0;
		
		if (!doneWithPath())// for redrawing paths in endless
		{
			sheepVel = 0;
			canDirectSheep = false;
			sheepGo = false;
		}
		
		scrollHeight += delta*scrollSpeed;
		super.render();
	}
	public boolean checkIsStartingPiece(Vector2 add)
	{
		if (startPen.getPos().x == add.x &&
			startPen.getPos().y == add.y)
			return true;
		return false;
	}
	public float getOffsetX()
	{
		return 0;
	}
	public float getOffsetY()
	{
		return -scrollHeight*getTileHeight();
	}
	public float getOffsetTileX()
	{
		return 0;
	}
	public float getOffsetTileY()
	{
		return -scrollHeight;
	}
	public void drawSheepPath(SpriteBatch batch, float delta)
	{
		if (oldPath != null && (oldPathFade-delta)/oldPathFadeMax > 0)
		{
			oldPathFade -= delta;
			float color = 0.2f;
			float tint = 0.3f*oldPathFade/oldPathFadeMax;
			drawAPath(batch, oldPath, color, tint, delta);
		}
		super.drawSheepPath(batch, delta);
	}
	
	public boolean doneWithPath()
	{
		if (sheepPath.size == 0)
			return false;
		Vector2 pos = sheepPath.get(sheepPath.size-1);
		if (endPen.getPos().x == pos.x &&
			endPen.getPos().y == pos.y)
			return true;
		return false;
	}
	public void removeTile(Tile t)
	{
		tiles.removeValue(t, true);
	}
	public void winTheGame()
	{
		//removeTile(startPen);
		startPen = endPen;
		oldPath = new Array<Vector2>(sheepPath.toArray());
		oldPathFade = oldPathFadeMax;
		sheepPath.clear();
		endPen = new Pen();
		endPen.set(3, 3+startPen.getPos().y);
		addTile(endPen);
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof SheepObj)
				((SheepObj)t).resetSteps();
		}
		sheepVel = 0;
		flashPath = flashPathTime;
		//winning = true;
		//gameOverlay.newMessage();
		//messages.add(new SheepMessage("You Win!", .5f).setColor("green"));
	}
	public int greenOverlayY()
	{
		float tileH = getTileHeight();
		return (int)(startPen.getPos().y*tileH+getOffsetY());
	}
	public int startGreenOverlay()
	{
		return (int)(startPen.getPos().x+getOffsetTileX());
	}
	public int endGreenOverlay()
	{
		return (int)(startPen.getPos().x+getOffsetTileX()+1);
	}
	public int redOverlayY()
	{
		float tileH = getTileHeight();
		return (int)(endPen.getPos().y*tileH+getOffsetY());
	}
	public int startRedOverlay()
	{
		return (int)(endPen.getPos().x+getOffsetTileX());
	}
	public int endRedOverlay()
	{
		return (int)(endPen.getPos().x+getOffsetTileX()+1);
	}
}