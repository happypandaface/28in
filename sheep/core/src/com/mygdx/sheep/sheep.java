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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class sheep extends ApplicationAdapter
{
	SpriteBatch batch;
	Texture img;
	private Stage stage;
    TextButton button;
	private boolean inPlayMenu = false;
	private boolean inLevelSelect = false;
	private boolean inEndless = false;
	private PuzzleMode puzzleMode;
	private EndlessInches endlessInches;
	private LevelSelect levelSelect;
	private boolean loading = false;
	private boolean loaded = false;
	private AssetHolder assetHolder;
	private ShapeRenderer shapeRenderer;
	
	public sheep()
	{
		loading = true;
	}
	
	@Override
	public void create ()
	{
		shapeRenderer = new ShapeRenderer();
		
		assetHolder = new AssetHolder();
		assetHolder.startLoad();
		
		
		puzzleMode = new PuzzleMode();
		puzzleMode.setAssetHolder(assetHolder);
		puzzleMode.setSheepMain(this);
		
		endlessInches = new EndlessInches();
		endlessInches.setAssetHolder(assetHolder);
		endlessInches.setSheepMain(this);
		
		levelSelect = new LevelSelect();
		levelSelect.setAssetHolder(assetHolder);
		levelSelect.setSheepMain(this);
		levelSelect.setGame(puzzleMode);
	}
	
	public void load ()
	{
		// this has to come first
		assetHolder.finishLoad();
		
		
		levelSelect.load();
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		puzzleMode.create();
		endlessInches.create();
		button = new TextButton("Play", assetHolder.buttonStyle);
		button.addListener(new InputListener(){
			private sheep sheep;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				sheep.play();
			}
			public InputListener setSceneChanger(sheep s)
			{
				this.sheep = s;
				return this;
			}
		}.setSceneChanger(this));
		Label nameLabel = new Label("28 INCHES", assetHolder.labelStyle);
		nameLabel.setWrap(false);
		TextButton button2 = new TextButton("How", assetHolder.buttonStyle);
		button2.addListener(new InputListener(){
			private sheep sheep;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				sheep.how();
			}
			public InputListener setSceneChanger(sheep s)
			{
				this.sheep = s;
				return this;
			}
		}.setSceneChanger(this));
		TextButton endless = new TextButton("Endless Inches", assetHolder.buttonStyle);
		endless.addListener(new InputListener(){
			private sheep sheep;
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				sheep.doEndless();
			}
			public InputListener setSceneChanger(sheep s)
			{
				this.sheep = s;
				return this;
			}
		}.setSceneChanger(this));
		Table topTable = new Table();
		topTable.setFillParent(true);
		topTable.top();
		topTable.add(assetHolder.sheepImg).width(200).height(200).pad(50);
		Table table = new Table();
		table.setFillParent(true);
		table.add(nameLabel).pad(30);
		table.row();
		table.add(button).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
		table.add(button2).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		table.row();
		table.add(endless).height(assetHolder.getPercentHeightInt(assetHolder.buttonHeight)).width(assetHolder.getPercentWidthInt(assetHolder.buttonWidth)).pad(10);
		Label studioLabel = new Label("STUDIO NAME FPO", assetHolder.labelStyle);
		studioLabel.setWrap(false);
		//table.bottom();
		//table.add(studioLabel);
		Table bottomTable = new Table();
		bottomTable.setFillParent(true);
		bottomTable.bottom();
		bottomTable.add(studioLabel);
		stage.addActor(table);
		stage.addActor(topTable);
		stage.addActor(bottomTable);
		batch = new SpriteBatch();
		loaded = true;
	}
	
	public void doEndless()
	{
		endlessInches.reset();
		endlessInches.playEndless();
		gotoMenu("endless");
	}
	
	public void play()
	{
		gotoMenu("level");
		//Gdx.input.setInputProcessor(sheepGame);
	}
	
	public void how()
	{
		assetHolder.levelLoader.setLoadLevelListener(new LoadLevelListener()
		{
			private sheep sheep;
			public void levelLoaded(String levelName)
			{
				sheep.gotoMenu("game");
			}
			public LoadLevelListener setLevelSelect(sheep ls)
			{
				sheep = ls;
				return this;
			}
		}.setLevelSelect(this));
		assetHolder.levelLoader.loadLevel(puzzleMode, 0);
	}
	
	public void gotoMenu(String s)
	{
		if (s.equals("main"))
		{
			inPlayMenu = false;
			inEndless = false;
			inLevelSelect = false;
			Gdx.input.setInputProcessor(stage);
		}else
		if (s.equals("game"))
		{
			inPlayMenu = true;
			inEndless = false;
			inLevelSelect = false;
			puzzleMode.switchTo();
		}else
		if (s.equals("endless"))
		{
			inPlayMenu = false;
			inEndless = true;
			inLevelSelect = false;
			endlessInches.switchTo();
		}else
		if (s.equals("level"))
		{
			inPlayMenu = false;
			inEndless = false;
			inLevelSelect = true;
			levelSelect.switchTo();
		}
	}

	@Override
	public void render () {
		if (loading && assetHolder.update())
		{
			load();
			loading = false;
		}else if (loading)
		{
			float per = assetHolder.getProgress();
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(1, 0, 0, 1);
			shapeRenderer.rect(assetHolder.getPercentWidth(.2f), assetHolder.getPercentHeight(.45f), assetHolder.getPercentWidth(.6f), assetHolder.getPercentHeight(.1f));
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(1, 0, 0, 1);
			shapeRenderer.rect(assetHolder.getPercentWidth(.2f), assetHolder.getPercentHeight(.45f), assetHolder.getPercentWidth(.6f)*per, assetHolder.getPercentHeight(.1f));
			shapeRenderer.end();
		}
		if (loaded)
		{
			if (inPlayMenu)
			{
				puzzleMode.render();
			}else
			if (inEndless)
			{
				endlessInches.render();
			}else
			if (inLevelSelect)
			{
				levelSelect.render();
			}else
			{
				Gdx.gl.glClearColor(1, 0, 0, 1);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				stage.act(Gdx.graphics.getDeltaTime());
				stage.draw();
				//batch.begin();
				//batch.draw(img, 0, 0);
				//font.draw(batch, "Hello", 100, 100);
				//batch.end();
			}
		}
	}
}
