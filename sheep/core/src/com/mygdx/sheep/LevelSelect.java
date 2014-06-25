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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class LevelSelect
{
	private AssetHolder assetHolder;
	private Stage stage;
	public boolean loaded = false;
	private sheep sheep;
	private SheepGame sheepGame;
	private Table levels;
	private int currentPanel;
	private int levelsPerPage = 4;
	
	public LevelSelect()
	{
	}
	
	public void setSheepMain(sheep s)
	{
		sheep = s;
	}
	
	public void setGame(SheepGame sg)
	{
		sheepGame = sg;
	}
	
	public void load()
	{
		stage = new Stage();
		float buttonHeight = 0.07f;
		float buttonWidth = 0.75f;
		
		Table topMenu = new Table();
		topMenu.setFillParent(true);
		topMenu.top();
		topMenu.left();
		ImageButton back = new ImageButton(assetHolder.backButtonTex);
		back.addListener(new InputListener(){
			private LevelSelect lSelect;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				lSelect.goBack();
			}
			public InputListener setSceneChanger(LevelSelect s)
			{
				this.lSelect = s;
				return this;
			}
		}.setSceneChanger(this));
		topMenu.add(back).width(sheepGame.getTileWidth()).height(sheepGame.getTileHeight());
		stage.addActor(topMenu);
		
		Table bottomMenu = new Table();
		bottomMenu.setFillParent(true);
		bottomMenu.bottom();
		TextButton backLvl = new TextButton("Previous Levels", assetHolder.buttonStyle);
		backLvl.addListener(new InputListener(){
			private LevelSelect lSelect;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				lSelect.backLevels();
			}
			public InputListener setSceneChanger(LevelSelect s)
			{
				this.lSelect = s;
				return this;
			}
		}.setSceneChanger(this));
		TextButton more = new TextButton("More Levels", assetHolder.buttonStyle);
		more.addListener(new InputListener(){
			private LevelSelect lSelect;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				lSelect.moreLevels();
			}
			public InputListener setSceneChanger(LevelSelect s)
			{
				this.lSelect = s;
				return this;
			}
		}.setSceneChanger(this));
		bottomMenu.add(more).height(assetHolder.getPercentHeightInt(buttonHeight)).width(assetHolder.getPercentWidthInt(buttonWidth)).pad(10);
		bottomMenu.row();
		bottomMenu.add(backLvl).height(assetHolder.getPercentHeightInt(buttonHeight)).width(assetHolder.getPercentWidthInt(buttonWidth)).pad(10);
		stage.addActor(bottomMenu);
		
		levels = new Table();
		levels.setFillParent(true);
		setLevelPanel(1);
		stage.addActor(levels);
		
		loaded = true;
	}
	
	public void setLevelPanel(int p)
	{
		levels.clearChildren();
		currentPanel = p;
		for (int i = (p-1)*levelsPerPage; i < (p)*levelsPerPage && i < assetHolder.levelLoader.getMaxLevels(); ++i)
		{
			TextButton lvl1 = new TextButton("Level "+(i+1), assetHolder.buttonStyle);
			levels.add(lvl1).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
			levels.row();
			lvl1.addListener(new InputListener(){
				private LevelSelect lSelect;
				private int level;
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
				{
					return true;
				}
				public void touchUp(InputEvent event, float x, float y, int pointer, int button)
				{
					lSelect.doLevel(this.level);
				}
				public InputListener setup(LevelSelect s, int i)
				{
					this.level = i;
					this.lSelect = s;
					return this;
				}
			}.setup(this, i));
		}
	}
	
	public int maxPanels()
	{
		return (int)Math.ceil((float)assetHolder.levelLoader.getMaxLevels()/(float)levelsPerPage);
	}
	
	public void moreLevels()
	{
		if (currentPanel < maxPanels())
			setLevelPanel(currentPanel+1);
	}
	
	public void backLevels()
	{
		if (currentPanel > 1)
			setLevelPanel(currentPanel-1);
	}
	
	public void doLevel(int l)
	{
		assetHolder.levelLoader.setLoadLevelListener(new LoadLevelListener()
		{
			private LevelSelect levelSelect;
			public void levelLoaded(String levelName)
			{
				levelSelect.loaded(levelName);
			}
			public LoadLevelListener setLevelSelect(LevelSelect ls)
			{
				levelSelect = ls;
				return this;
			}
		}.setLevelSelect(this));
		assetHolder.levelLoader.loadLevel(sheepGame, l);
	}
	
	public void loaded(String levelName)
	{
		sheep.gotoMenu("game");
	}
	
	public void goBack()
	{
		sheep.gotoMenu("main");
	}
	
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
	}
	
	public void render ()
	{
		if (assetHolder.levelLoader.isLoading())
			assetHolder.levelLoader.render();
		else
		{
			Gdx.gl.glClearColor(1, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act(Gdx.graphics.getDeltaTime());
			stage.draw();
		}
	}
	
	public void switchTo()
	{
		Gdx.input.setInputProcessor(stage);
	}
}