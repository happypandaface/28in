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

public class SheepGame implements InputProcessor
{
	SpriteBatch batch;
	protected float numTilesX = 5;
	protected float numTilesY = 8;
	protected Array<Vector2> sheepPath;
	public Array<Tile> tiles;
	public Array<SheepMessage> messages;
	public ArrayMap<String, Texture> texLink;
	protected boolean sheepGo;
	protected boolean canDirectSheep;
	protected AssetHolder assetHolder;
	protected sheep sheep;
	protected InputMultiplexer inMux;
	protected Tile touchedTile = null;
	protected float overlayOffset = 0;
	protected boolean losing = false;
	protected boolean winning = false;
	protected boolean skipTap = false;
	protected float sheepVel = 0;
	protected float sheepAccel = 4.0f;
	protected float sheepDeccel = 6.0f;
	protected float flashPath;
	protected float flashPathTime = 1f;
	protected GameOverlay gameOverlay;
	
	protected boolean playingEndless;
	
	public SheepGame()
	{
		inMux = new InputMultiplexer();
		inMux.addProcessor(this);
		gameOverlay = new GameOverlay();
		gameOverlay.setSheepGame(this);
		inMux.addProcessor(gameOverlay.inMux);
	}
	
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
		gameOverlay.setAssetHolder(assetHolder);
	}
	
	public void setSheepMain(sheep s)
	{
		sheep = s;
		gameOverlay.setSheepMain(s);
	}
	
	public void create()
	{
		gameOverlay.create();
		inMux.addProcessor(this);
		
		sheepPath = new Array<Vector2>();
		texLink = new ArrayMap<String, Texture>();
		tiles = new Array<Tile>();
		messages = new Array<SheepMessage>();
		batch = new SpriteBatch();
		texLink.put("sheep", new Texture(Gdx.files.internal("140616_Sheep RD1-BIG-sheep.png")));
		texLink.put("grass", new Texture(Gdx.files.internal("140616_Tile RD1-BIG.png")));
		texLink.put("boulder", new Texture(Gdx.files.internal("140621-28Inches-Tile-Boulder.png")));
		texLink.put("tallGrass", new Texture(Gdx.files.internal("140623-28Inches-Tile-Grass.png")));
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(new Color(1f, 1f, 1f, 1f));
			pixmap.fill();
			texLink.put("path", new Texture(pixmap));
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(new Color(0f, 0f, 0f, 1f));
			pixmap.fill();
			texLink.put("guard", new Texture(pixmap));
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(new Color(1f, 1f, 0f, .3f));
			pixmap.fill();
			texLink.put("light", new Texture(pixmap));
		}
		
		
	}
	
	public void reset()
	{
		playingEndless = false;
		gameOverlay.reset();
		flashPath = flashPathTime;
		tiles.clear();
		messages.clear();
		//shownBottomMenu = false;
		sheepVel = 0;
		skipTap = false;
		losing = false;
		winning = false;
		sheepPath.clear();
		sheepGo = false;
		canDirectSheep = false;
	}
	
	public void switchTo()
	{
		Gdx.input.setInputProcessor(inMux);
	}
	
	public void addMessage(SheepMessage msg)
	{
		messages.add(msg);
	}
	public SheepMessage getMessage()
	{
		return messages.get(0);
	}
	public boolean hasMessage()
	{
		return messages.size > 0;
	}
	public boolean lastMessage()
	{
		return messages.size == 1;
	}
	public void removeOneMessage()
	{
		messages.removeIndex(0);
	}
	
	public void addTile(Tile t)
	{
		t.setAssetHolder(assetHolder);
		if (t instanceof SheepObj)
		{
			((SheepObj)t).setPath(sheepPath);
		}
		t.create(this);
		tiles.add(t);
	}
	
	public void render()
	{
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float realDelta = Gdx.graphics.getDeltaTime();// for all effects
		float delta = realDelta;// for game effects
		if (gameOverlay.isPaused())
			delta = 0;
		if (sheepGo)
			sheepVel += delta*sheepAccel;
		else
			sheepVel -= delta*sheepDeccel;
		if (sheepVel > 1)
			sheepVel = 1;
		if (sheepVel < 0)
			sheepVel = 0;
		
		float tileW = getTileWidth();
		float tileH = getTileHeight();
		float startX = getStartX();
		float startY = getStartY();
		
		if (!gameOverlay.isPaused() && messages.size == 0)
		{
			for (int i = 0; i < tiles.size; ++i)
			{
				Tile t = tiles.get(i);
				t.update(delta);
			}
		}
		//batch.enableBlending();
		batch.begin();
		if (playingEndless)
		{
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			for (int x = -1; x <= getNumTilesX()+1; ++x)
				for (int y = -1; y <= getNumTilesY()+1; ++y)
				{
					batch.draw(texLink.get("grass"), startX+x*tileW+getOffsetX()%getTileWidth(), startY+y*tileH+getOffsetY()%getTileHeight(), tileW, tileH);
				}
		}else
		{
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			for (int x = 0; x < getNumTilesX(); ++x)
				for (int y = 0; y < getNumTilesY(); ++y)
				{
					batch.draw(texLink.get("grass"), startX+x*tileW+getOffsetX(), startY+y*tileH+getOffsetY(), tileW, tileH);
				}
		}
		
		// these are for win/loss conditions
		int numSheep = 0;
		int doneSheep = 0;
		int deadSheep = 0;
		// draw everything but guards and sheep
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (!(t instanceof Guard) && !(t instanceof SheepObj))
			{
				t.draw(batch, delta);
			}
		}
		drawSheepPath(batch, delta);
		batch.setColor(1, 1, 1, 1);
		// draw guards
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof Guard)
			{
				t.draw(batch, delta);
			}
		}
		// draw and count sheep
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof SheepObj)
			{
				++numSheep;
				t.draw(batch, delta);
			}
		}
		for (int i = 0; i < tiles.size; ++i)
		{
			Tile t = tiles.get(i);
			if (t instanceof SheepObj && ((SheepObj)t).getCompleted())
				++doneSheep;
			if (t instanceof SheepObj && ((SheepObj)t).isDead())
				++deadSheep;
		}
		if (doneSheep == numSheep && !winning)
			winTheGame();
		if (deadSheep > 0 && !losing)
			loseTheGame();
		if (sheepPath.size == 0)
		{
			overlayOffset += delta;
			if (overlayOffset > 1)
				overlayOffset -= 1;
			for (int x = startGreenOverlay(); x < endGreenOverlay(); ++x)
				batch.draw(assetHolder.startOverlay, x*tileW, greenOverlayY(), tileW, tileH, overlayOffset, overlayOffset, overlayOffset+1, overlayOffset+1);
		}else
		if (!doneWithPath())
		{
			overlayOffset += delta;
			if (overlayOffset > 1)
				overlayOffset -= 1;
			for (int x = startRedOverlay(); x < endRedOverlay(); ++x)
				batch.draw(assetHolder.endOverlay, x*tileW, redOverlayY(), tileW, tileH, overlayOffset, overlayOffset, overlayOffset+1, overlayOffset+1);
		}
		batch.end();
		gameOverlay.render(realDelta);
	}
	public int greenOverlayY()
	{
		return 0;
	}
	public int startGreenOverlay()
	{
		return 0;
	}
	public int endGreenOverlay()
	{
		return (int)numTilesX+1;
	}
	public int redOverlayY()
	{
		float tileH = getTileHeight();
		return (int)((numTilesY-1)*tileH);
	}
	public int startRedOverlay()
	{
		return 0;
	}
	public int endRedOverlay()
	{
		return (int)numTilesX+1;
	}
	public void drawSheepPath(SpriteBatch batch, float delta)
	{
		float color = 0.2f+0.8f*flashPath/flashPathTime;
		float tint = 0.3f+0.7f*flashPath/flashPathTime;
		if (!canDirectSheep)
		{
			color = 0.2f;
			tint = 0.3f;
		}
		if (canDirectSheep)
		{
			flashPath -= delta;
			if (flashPath < 0)
				flashPath = 0;
		}
		drawAPath(batch, sheepPath, color, tint, delta);
	}
	public void drawAPath(SpriteBatch batch, Array<Vector2> path, float color, float tint, float delta)
	{
		float tileW = getTileWidth();
		float tileH = getTileHeight();
		float startX = getStartX();
		float startY = getStartY();
		
		batch.setColor(color, color, color, tint);
		// the path you draw
		for (int i = 0; i < path.size; ++i)
		{
			Vector2 p = path.get(i);
			// make it flash after created
			batch.draw(texLink.get("path"), startX+p.x*tileW+getOffsetX(), startY+p.y*tileH+getOffsetY(), tileW, tileH);
		}
		
	}
	
	public void loseTheGame()
	{
		losing = true;
		gameOverlay.newMessage();
		messages.add(new SheepMessage("You Lose!", .5f));
	}
	
	public void winTheGame()
	{
		winning = true;
		gameOverlay.newMessage();
		messages.add(new SheepMessage("You Win!", .5f).setColor("green"));
	}
	
	public boolean doneWithPath()
	{
		if (sheepPath.size == 0)
			return false;
		if (sheepPath.get(sheepPath.size-1).y == numTilesY-1)
			return true;
		return false;
	}
	
	public boolean getSheepGo()
	{
		return sheepGo;
	}
	
	public float getSheepVel()
	{
		return sheepVel;
	}
	
	public float getStartX()
	{
		return 0;
	}
	
	public float getStartY()
	{
		return 0;
	}
	
	public float getNumTilesX()
	{
		return numTilesX;
	}
	
	public float getNumTilesY()
	{
		return numTilesY;
	}
	
	public float getTileWidth()
	{
		return Gdx.graphics.getWidth()/numTilesX;
	}
	
	public float getTileHeight()
	{
		return Gdx.graphics.getHeight()/(numTilesY+1);
	}
	
	public boolean keyDown(int keycode)
	{
		return false;
	}
	public boolean keyTyped(char character)
	{
		return false;
	}
	public boolean keyUp(int keycode)
	{
		return false;
	}
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}
	public boolean scrolled(int amount)
	{
		return false;
	}
	public boolean isWinning()
	{
		return winning;
	}
	public boolean isLosing()
	{
		return losing;
	}
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (losing || winning || messages.size > 0)
		{
			skipTap = true;
		}
		if (!gameOverlay.isPaused())
		{
			boolean tileTouch = false;
			int x = screenX;
			int y = (Gdx.graphics.getHeight()-screenY);
			int tileX = (int)Math.floor((float)x/(float)getTileWidth()-getOffsetTileX());
			int tileY = (int)Math.floor((float)y/(float)getTileHeight()-getOffsetTileY());
			if (canDirectSheep)
			{
				for (int i = 0; i < tiles.size; ++i)
				{
					Tile t = tiles.get(i);
					Vector2 currPos = t.getFlrPos();
					if (currPos.x == tileX &&
						currPos.y == tileY)
					{
						if (t.touchedDown())
						{
							touchedTile = t;
							tileTouch = true;
							break;
						}
					}
				}
			}
			if (!tileTouch && canDirectSheep)
				sheepGo = true;
		}
		return touchDragged(screenX, screenY, pointer);
	}
	public boolean checkIfCanPathOver(Tile t)
	{
		if (t instanceof Boulder)
		{
			return false;
		}
		return true;
	}
	public boolean checkIsStartingPiece(Vector2 add)
	{
		return add.y == 0;
	}
	public float getOffsetX()
	{
		return 0;
	}
	public float getOffsetY()
	{
		return 0;
	}
	public float getOffsetTileX()
	{
		return 0;
	}
	public float getOffsetTileY()
	{
		return 0;
	}
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if (!gameOverlay.isPaused())
		{
			if (!doneWithPath() && messages.size == 0)
			{
				int x = screenX;
				int y = (Gdx.graphics.getHeight()-screenY);
				float tileX = (float)x/(float)getTileWidth()-getOffsetTileX();
				float tileY = (float)y/(float)getTileHeight()-getOffsetTileY();
				int tileXFlr = (int)Math.floor(tileX);
				int tileYFlr = (int)Math.floor(tileY);
				Vector2 add = new Vector2(tileXFlr, tileYFlr);
				boolean pieceWorks = true;
				for (int i = 0; i < sheepPath.size; ++i)
				{
					Vector2 p = sheepPath.get(i);
					if (p.x == add.x && p.y == add.y)
						pieceWorks = false;
				}
				for (int i = 0; i < tiles.size; ++i)
				{
					Tile t = tiles.get(i);
					if (!checkIfCanPathOver(t))
					{
						if (t.checkOverlap(add))
							pieceWorks = false;
					}
				}
				if (sheepPath.size == 0 && !checkIsStartingPiece(add))
					pieceWorks = false;
				if (sheepPath.size != 0 && sheepPath.get(sheepPath.size-1).cpy().sub(add).len() != 1)
					pieceWorks = false;
				if (pieceWorks)
					sheepPath.add(add);
				else
				{
					// if it's on the path, remove all beyond that one.
					for (int i = 0; i < sheepPath.size-1; ++i)// minus 1 form size (can't remove beyond the last one
					{
						Vector2 curr = sheepPath.get(i);
						if (curr.x == add.x &&
							curr.y == add.y)
						{
							sheepPath.removeRange(i+1, sheepPath.size-1);
							break;
						}
					}
				}
			}else
			{
				
			}
		}
		return false;
	}
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (skipTap && (losing || winning || messages.size > 0))
		{
			gameOverlay.trySkip();
		}
		skipTap = false;
		if (!gameOverlay.isPaused())
		{
			int x = screenX;
			int y = (Gdx.graphics.getHeight()-screenY);
			int tileX = (int)Math.floor((float)x/(float)getTileWidth()-getOffsetTileX());
			int tileY = (int)Math.floor((float)y/(float)getTileHeight()-getOffsetTileY());
			for (int i = 0; i < tiles.size; ++i)
			{
				Tile t = tiles.get(i);
				Vector2 currPos = t.getFlrPos();
				if (currPos.x == tileX &&
					currPos.y == tileY)
				{
					if (t == touchedTile)
						touchedTile.touchedUp();
				}
			}
			if (doneWithPath())
				canDirectSheep = true;
			if (canDirectSheep)
				sheepGo = false;
		}
		return false;
	}
}