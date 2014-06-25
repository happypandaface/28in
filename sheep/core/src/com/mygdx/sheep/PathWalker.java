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

public class PathWalker extends Tile
{
	protected Array<Vector2> path;
	protected float speed = 1;
	protected float stepsThroughPath = 0;
	protected float offset = 0;
	protected Vector2 dir;
	protected Vector2 lastDir;
	
	public static class PathWalkerJson extends Tile.TileJson
	{
		public Array<Vector2> path;
		public float offset;
	}
	public TileJson continueJsonObject(Tile.TileJson tj)
	{
		super.continueJsonObject(tj);
		((PathWalkerJson)tj).path = path;
		((PathWalkerJson)tj).offset = offset;
		return tj;
	}
	public TileJson startJsonObject()
	{
		return new PathWalkerJson();
	}
	public String getTypeStr()
	{
		return "PathWalker";
	}
	public Tile makeFromJsonObject(TileJson tj)
	{
		super.makeFromJsonObject(tj);
		path = ((PathWalkerJson)tj).path;
		offset = ((PathWalkerJson)tj).offset;
		return this;
	}
	
	public PathWalker()
	{
		dir = new Vector2(0, 0);
		lastDir = new Vector2(0, 0);
		path = new Array<Vector2>();
	}
	
	public PathWalker addPath(int x, int y)
	{
		// makes sure that even if update is never called, the
		// pathwalker still is oriented correctly.
		//if (path.size == 0)
		//	pos.set(x, y);
		path.add(new Vector2(x, y));
		pos = getPosition(0);
		//if (path.size == 2)
		//	dir = path.get(1).cpy().sub(path.get(0));
		return this;
	}
	
	public PathWalker setPath(Array<Vector2> p)
	{
		path = p;
		pos = getPosition(0);
		return this;
	}
	
	public PathWalker setOffset(float o)
	{
		offset = o;
		pos = getPosition(0);
		return this;
	}
	
	public float getStepsThroughPath()
	{
		return stepsThroughPath;
	}
	
	public void resetSteps()
	{
		stepsThroughPath = 0;
	}
	
	public void update(float d)
	{
		stepsThroughPath += d*speed;
		pos = getPosition(stepsThroughPath);
	}
	
	public boolean getOnField()
	{
		float idxFlt = stepsThroughPath+offset;
		if (idxFlt > 0 && !getCompleted())
			return true;
		return false;
	}
	
	public boolean getCompleted()
	{
		float idxFlt = stepsThroughPath+offset;
		if (idxFlt > path.size-1 && path.size > 0)
			return true;
		return false;
	}
	
	public Vector2 getPosition(float steps)
	{
		if (path.size == 0)
			return new Vector2(0, 0);
		float idxFlt = steps+offset;
		while (idxFlt < 0)
			idxFlt += path.size;
		int idxTot = (int)Math.floor(idxFlt);
		float thisStep = idxFlt-idxTot;
		int idx = idxTot%path.size;
		int idxNext = (idxTot+1)%path.size;
		Vector2 rtn = path.get(idx).cpy();
		Vector2 dist = path.get(idxNext).cpy().sub(rtn);
		if (dist.x != dir.x && dist.y != dir.y)
			lastDir = dir.cpy();// this updates lastDir to the previous dir
		dir = dist.cpy();
		rtn.add(dist.scl(thisStep));
		return rtn;
	}
}