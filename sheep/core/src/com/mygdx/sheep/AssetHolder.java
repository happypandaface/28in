package com.mygdx.sheep;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class AssetHolder
{
	private AssetManager assets;
	public Texture sheepTex;
	public Image sheepImg;
	public BitmapFont fontWhite;
	public BitmapFont fontRed;
	public Skin skin;
	public LabelStyle labelStyle;
	public TextButtonStyle buttonStyle;
	private String fontFile = "theGoodFont/goodFont.fnt";
	public TextureRegionDrawable backButtonTex;
	private String backButton = "140621-Menu-Button.png";
	public Texture sheepTex1;
	public Texture sheepTex2;
	public Texture sheepTex3;
	public Texture guardTex1;
	public Texture guardTex2;
	public Texture guardTex3;
	public Texture sheepVert1;
	public Texture sheepVert2;
	public Texture sheepVert3;
	public Texture startOverlay;
	public Texture endOverlay;
	public Texture redTex;
	public Texture greenTex;
	public Texture dog1;
	public Texture regTile1;
	public Texture boulder1;
	public Texture tallGrass1;
	public Texture white;
	private float fontSize = 1.0f/1500.0f;
	public LevelLoader levelLoader;
	public float buttonHeight = 0.07f;
	public float buttonWidth = 0.75f;
	
	public void startLoad()
	{
		assets = new AssetManager();
		assets.load(fontFile, BitmapFont.class);
		assets.load(backButton, Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Frame0.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Frame1.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Frame2.png", Texture.class);
		
		assets.load("140621-28Inches-Sprite-Guard-Frame-0.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Guard-Frame-1.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Guard-Frame-2.png", Texture.class);
		
		assets.load("140621-28Inches-Sprite-Sheep-Vertical-Frame0.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Vertical-Frame1.png", Texture.class);
		assets.load("140621-28Inches-Sprite-Sheep-Vertical-Frame2.png", Texture.class);
		
		assets.load("140616_Sheep RD1-BIG-sheep.png", Texture.class);
		assets.load("140616_Tile RD1-BIG.png", Texture.class);
		assets.load("140621-28Inches-Tile-Boulder.png", Texture.class);
		assets.load("140623-28Inches-Tile-Grass.png", Texture.class);
		
		assets.load("140621-28Inches-Button-Normal.png", Texture.class);
		assets.load("140621-28Inches-Button-Pressed.png", Texture.class);
		
		assets.load("140621-Starting-Area.png", Texture.class);
		assets.load("140621-Ending-Area-Overlay.png", Texture.class);
		assets.load("dog.png", Texture.class);
	}
	public void finishLoad()
	{
		levelLoader = new LevelLoader();
		skin = new Skin();
		regTile1 = assets.get("140616_Tile RD1-BIG.png", Texture.class);
		boulder1 = assets.get("140621-28Inches-Tile-Boulder.png", Texture.class);
		tallGrass1 = assets.get("140623-28Inches-Tile-Grass.png", Texture.class);
		sheepTex1 = assets.get("140621-28Inches-Sprite-Sheep-Frame0.png", Texture.class);
		sheepTex2 = assets.get("140621-28Inches-Sprite-Sheep-Frame1.png", Texture.class);
		sheepTex3 = assets.get("140621-28Inches-Sprite-Sheep-Frame2.png", Texture.class);
		guardTex1 = assets.get("140621-28Inches-Sprite-Guard-Frame-0.png", Texture.class);
		guardTex2 = assets.get("140621-28Inches-Sprite-Guard-Frame-1.png", Texture.class);
		guardTex3 = assets.get("140621-28Inches-Sprite-Guard-Frame-2.png", Texture.class);
		sheepVert1 = assets.get("140621-28Inches-Sprite-Sheep-Vertical-Frame0.png", Texture.class);
		sheepVert2 = assets.get("140621-28Inches-Sprite-Sheep-Vertical-Frame1.png", Texture.class);
		sheepVert3 = assets.get("140621-28Inches-Sprite-Sheep-Vertical-Frame2.png", Texture.class);
		startOverlay = assets.get("140621-Starting-Area.png", Texture.class);
		startOverlay.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		dog1 = assets.get("dog.png", Texture.class);
		endOverlay = assets.get("140621-Ending-Area-Overlay.png", Texture.class);
		endOverlay.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		{
			backButtonTex = new TextureRegionDrawable(new TextureRegion(assets.get(backButton, Texture.class)));
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.RED);
			pixmap.fill();
			redTex = new Texture(pixmap);
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.GREEN);
			pixmap.fill();
			greenTex = new Texture(pixmap);
		}
		{
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();
			white = new Texture(pixmap);
			skin.add("white", new Texture(pixmap));
		}
		sheepTex = new Texture(Gdx.files.internal("140616_Sheep RD1-BIG-sheep.png"));
		sheepImg = new Image(sheepTex);
		float fontScale = getPercentWidth(fontSize);
		fontWhite = assets.get(fontFile, BitmapFont.class);
		fontWhite.setColor(1, 1, 1, 1);
		fontWhite.setScale(fontScale);
		fontRed = assets.get(fontFile, BitmapFont.class);
		fontRed.setColor(1, 1, 1, 1);
		fontRed.setScale(fontScale);
		labelStyle = new LabelStyle();
		labelStyle.font = fontWhite;
		buttonStyle = new TextButtonStyle();
		//NinePatch upPatch = new NinePatch(assets.get("140621-28Inches-Button-Normal.png", Texture.class), 128, 128, 128, 128);
		//NinePatch downPatch = new NinePatch(assets.get("140621-28Inches-Button-Pressed.png", Texture.class), 128, 128, 128, 128);
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assets.get("140621-28Inches-Button-Normal.png", Texture.class)));
		//new NinePatchDrawable(upPatch);//skin.newDrawable("white", Color.valueOf("ffffff"));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assets.get("140621-28Inches-Button-Pressed.png", Texture.class)));
		//new NinePatchDrawable(downPatch);//skin.newDrawable("white", Color.valueOf("ff0000"));
		buttonStyle.font = fontRed;
		buttonStyle.fontColor = Color.valueOf("ff0000");
		buttonStyle.downFontColor = Color.valueOf("ffffff");
		buttonStyle.overFontColor = Color.valueOf("ff0000");
		buttonStyle.checkedFontColor = Color.valueOf("ff0000");
	}
	
	public float getPercentWidth(float per)
	{
		return ((float)Gdx.graphics.getWidth())*per;
	}
	
	public float getPercentHeight(float per)
	{
		return ((float)Gdx.graphics.getHeight())*per;
	}
	
	public int getPercentWidthInt(float per)
	{
		return (int)(((float)Gdx.graphics.getWidth())*per);
	}
	
	public int getPercentHeightInt(float per)
	{
		return (int)(((float)Gdx.graphics.getHeight())*per);
	}
	
	public float getProgress()
	{
		return assets.getProgress();
	}
	
	public boolean update()
	{
		return assets.update();
	}
}