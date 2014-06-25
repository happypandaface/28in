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

public class Enemy extends PathWalker
{
	public void update(float delta)
	{
		super.update(delta);
		
		if (sheepGame != null)
			for (int i = 0; i < sheepGame.tiles.size; ++i)
			{
				Tile t = sheepGame.tiles.get(i);
				if (t instanceof SheepObj)
				{
					SheepObj so = (SheepObj)t;
					if (so.getOnField())
					{
						Vector2 dist = so.getPos().cpy().sub(getPos());
						if (Math.abs(dist.x) < 0.375f &&
							Math.abs(dist.y) < 0.375f)
							so.gitRekt();
						/*
						Vector2 distLight = getPos().cpy();
						distLight.add(dir).sub(so.getPos());
						if (Math.abs(distLight.x) < 0.5f &&
							Math.abs(distLight.y) < 0.5f)
							so.gitRekt();*/
					}
				}
			}
	}
	public String getTypeStr()
	{
		return "Enemy";
	}
}