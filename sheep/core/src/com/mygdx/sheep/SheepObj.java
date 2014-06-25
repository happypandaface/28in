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

public class SheepObj extends PathWalker
{
	protected boolean dead = false;
	private boolean moving = false;
	
	public SheepObj()
	{
		speed = 2.6f;
	}
	public String getTypeStr()
	{
		return "Sheep";
	}
	
	@Override
	public boolean getOnField()
	{
		// commented out so you can see the sheep that got caught
		//if (dead)
		//	return false;
		return super.getOnField();
	}
	
	public void gitRekt()
	{
		dead = true;
	}
	
	public boolean isDead()
	{
		return dead;
	}
	
	@Override
	public void update(float delta)
	{
		float moveSpeed = 1;
		for (int i = 0; i < sheepGame.tiles.size; ++i)
		{
			Tile t = sheepGame.tiles.get(i);
			if (t instanceof TallGrass)
			{
				TallGrass tg = (TallGrass)t;
				Vector2 dist = tg.getRndPos().cpy().sub(getRndPos());
				if (tg.checkOverlap(this) &&
					tg.getExists())
				{
					moveSpeed = .5f;
				}
			}
		}
		if (sheepGame.getSheepVel() > 0)
		{
			moving = true;
			stepsThroughPath += delta*speed*moveSpeed*sheepGame.getSheepVel();
		}else
		{
			moving = false;
		}
		pos = getPosition(stepsThroughPath);
	}
	
	public Texture getTex()
	{
		if (dir.y == 0)
		{
			if (!moving)
				return assetHolder.sheepTex1;
			
			int steps = ((int)(getStepsThroughPath()*3.0f));
			if (steps%2 == 0)
				return assetHolder.sheepTex2;
			else
				return assetHolder.sheepTex3;
		}else
		if (dir.x == 0)
		{
			if (!moving)
				return assetHolder.sheepVert1;
			
			int steps = ((int)(getStepsThroughPath()*3.0f));
			if (steps%2 == 0)
				return assetHolder.sheepVert2;
			else
				return assetHolder.sheepVert3;
		}else
		{
			return assetHolder.sheepTex1;
		}
	}
	
	public static Texture getTex(AssetHolder assetHolder)
	{
		return assetHolder.sheepTex1;
	}
	
	@Override
	public void draw(SpriteBatch batch, float delta)
	{
		boolean flipX = false;
		if (dir.x == -1 || (dir.x == 0 && lastDir.x == -1))
			flipX = true;
		if (getExists() && getOnField() && !getCompleted())
			drawSprite(batch, getTex(), pos.x, pos.y, flipX);
	}
}