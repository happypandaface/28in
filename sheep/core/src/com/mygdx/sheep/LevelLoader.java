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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpParametersUtils;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class LevelLoader
{
	public int currentLevel = 0;
	public String currentLevelStr = "none";
	private Array<String> levelLink;
	private AssetHolder assetHolder;
	private boolean loading;
	private LoadLevelListener lll;
	private SpriteBatch batch;
	
	public LevelLoader()
	{
		batch = new SpriteBatch();
		levelLink = new Array<String>();
		levelLink.add("Basics");
		levelLink.add("Guards!");
		levelLink.add("Two birds");
		levelLink.add("Yardwork");
		levelLink.add("Landscaping");
		levelLink.add("Landscaping 2");
		levelLink.add("Roundabout");
		levelLink.add("Two paths");
		levelLink.add("Raptors");
	}
	
	public void setAssetHolder(AssetHolder as)
	{
		assetHolder = as;
	}
	public void loadLevelNet(SheepGame sheepGame, String playerName, String levelName)
	{
		HttpRequest httpGet = new HttpRequest(HttpMethods.POST);
		httpGet.setUrl("http://scottgriffy.com/sheep/echo.php");
		httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpGet.setContent("test=test1");
		//httpGet.setContent(HttpParametersUtils.convertHttpParameters(parameters));
		//...
		Gdx.net.sendHttpRequest (httpGet, new HttpResponseListener() {
			private String levelName;
			public void handleHttpResponse(HttpResponse httpResponse) {
				Gdx.app.log("httpreq", httpResponse.getResultAsString());
				lll.levelLoaded(levelName);
				//do stuff here based on response
			}

			public void failed(Throwable t) {
				Gdx.app.log("httpreq", t.getMessage());
				//do stuff here based on the failed attempt
			}

			public void cancelled() {
				Gdx.app.log("httpreq", "cancelled");
				//do stuff here based on the failed attempt
			}
			public HttpResponseListener setLevelName(String s)
			{
				levelName = s;
				return this;
			}
		}.setLevelName(levelName));
	}
	public void loadLevel(SheepGame sheepGame, String levelName, boolean showHelp)
	{
		currentLevelStr = levelName;
		sheepGame.reset();
		if (levelName.equals("Roundabout"))
		{
			sheepGame.addTile(new Boulder().set(0, 3));
			sheepGame.addTile(new Boulder().set(2, 3));
			sheepGame.addTile(new Boulder().set(3, 3));
			sheepGame.addTile(new Boulder().set(1, 5));
			sheepGame.addTile(new Boulder().set(2, 5));
			sheepGame.addTile(new Boulder().set(4, 5));
			
			sheepGame.addTile(new Guard()
				.addPath(0, 6)
				.addPath(1, 6)
				.addPath(2, 6)
				.addPath(3, 6)
				.addPath(3, 5)
				.addPath(3, 4)
				.addPath(2, 4)
				.addPath(1, 4)
				.addPath(0, 4)
				.addPath(0, 5)
				);
			
			sheepGame.addTile(new Guard()
				.addPath(4, 4)
				.addPath(3, 4)
				.addPath(2, 4)
				.addPath(1, 4)
				.addPath(1, 3)
				.addPath(1, 2)
				.addPath(2, 2)
				.addPath(3, 2)
				.addPath(4, 2)
				.addPath(4, 3)
				);
			
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
			
			Json json = new Json();
			Array<Tile.TileJson> objs = new Array<Tile.TileJson>();
			for (int i = 0; i < sheepGame.tiles.size; ++i)
				objs.add(sheepGame.tiles.get(i).getJsonObj());
			String jsonStr = json.toJson(objs, Array.class);
			
			sheepGame.reset();
			Array<Tile.TileJson> tjArray = json.fromJson(Array.class, jsonStr);
			for (int i = 0; i < tjArray.size; ++i)
			{
				Tile.TileJson tj = tjArray.get(i);
				Tile t = null;
				Gdx.app.log("json", tj.type);
				if (tj.type.equals("Guard"))
					t = new Guard().makeFromJsonObject(tj);
				else
				if (tj.type.equals("Sheep"))
					t = new SheepObj().makeFromJsonObject(tj);
				else
				if (tj.type.equals("Dog"))
					t = new SheepObj().makeFromJsonObject(tj);
				else
				if (tj.type.equals("Boulder"))
					t = new Boulder().makeFromJsonObject(tj);
				else
				if (tj.type.equals("TallGrass"))
					t = new Boulder().makeFromJsonObject(tj);
				
				sheepGame.addTile(t);
			}
		}else
		if (levelName.equals("Two birds"))
		{
			if (showHelp)
				sheepGame.addMessage(new SheepMessage("Your sheep move\nat the same time", 0.3f));
			
			for (int x = 0; x < 5; ++x)
				if (x != 1)
					sheepGame.addTile(new Boulder().set(x, 2));
			
			// bottom right corner
			sheepGame.addTile(new Boulder().set(3, 3));
			sheepGame.addTile(new Boulder().set(4, 3));
			sheepGame.addTile(new Boulder().set(4, 4));
			// top left corner
			sheepGame.addTile(new Boulder().set(0, 5));
			sheepGame.addTile(new Boulder().set(1, 5));
			sheepGame.addTile(new Boulder().set(0, 4));
			
			sheepGame.addTile(new Boulder().set(2, 6));
			
			sheepGame.addTile(new Guard()
				.addPath(0, 3)
				.addPath(1, 3)
				.addPath(2, 3)
				.addPath(1, 3)
				);
			sheepGame.addTile(new Guard()
				.addPath(2, 5)
				.addPath(3, 5)
				.addPath(4, 5)
				.addPath(3, 5)
				);
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
		}else
		if (levelName.equals("Landscaping"))
		{
			if (showHelp)
				sheepGame.addMessage(new SheepMessage("But sometimes grass\ncan be helpful...", 0.3f));
			
			for (int x = 0; x < 5; ++x)
				if (x != 1)
					sheepGame.addTile(new Boulder().set(x, 2));
			
			// bottom right corner
			sheepGame.addTile(new Boulder().set(4, 3));
			sheepGame.addTile(new Boulder().set(4, 4));
			// top left corner
			sheepGame.addTile(new Boulder().set(0, 5));
			sheepGame.addTile(new Boulder().set(1, 6));
			sheepGame.addTile(new Boulder().set(0, 4));
			
			sheepGame.addTile(new Boulder().set(2, 6));
			
			sheepGame.addTile(new TallGrass().set(1, 2));
			sheepGame.addTile(new TallGrass().set(2, 4));
			sheepGame.addTile(new TallGrass().set(1, 4));
			
			WalkPath path1 = new WalkPath();
			path1.add(0, 3);
			path1.add(1, 3);
			path1.add(2, 3);
			path1.add(3, 3);
			path1.add(2, 3);
			path1.add(1, 3);
			sheepGame.addTile(new Guard()
				.setPath(path1)
				.setOffset(1)
				);
			sheepGame.addTile(new Dog()
				.setPath(path1)
				.setOffset(2)
				);
			WalkPath path2 = new WalkPath();
			path2.add(1, 5);
			path2.add(2, 5);
			path2.add(3, 5);
			path2.add(4, 5);
			path2.add(3, 5);
			path2.add(2, 5);
			sheepGame.addTile(new Guard()
				.setPath(path2)
				.setOffset(0)
				);
			sheepGame.addTile(new Dog()
				.setPath(path2)
				.setOffset(1)
				);
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
		}else
		if (levelName.equals("Landscaping 2"))
		{
			if (showHelp)
				sheepGame.addMessage(new SheepMessage("Grass can be used\nto group sheep\ntogether", 0.3f));
			
			for (int x = 0; x < 5; ++x)
				if (x != 1)
					sheepGame.addTile(new Boulder().set(x, 2));
			
			// bottom right corner
			sheepGame.addTile(new Boulder().set(3, 3));
			sheepGame.addTile(new Boulder().set(4, 3));
			sheepGame.addTile(new Boulder().set(4, 4));
			// top left corner
			sheepGame.addTile(new Boulder().set(0, 5));
			sheepGame.addTile(new Boulder().set(1, 5));
			sheepGame.addTile(new Boulder().set(0, 4));
			
			sheepGame.addTile(new Boulder().set(2, 6));
			
			sheepGame.addTile(new TallGrass().set(1, 1));
			sheepGame.addTile(new TallGrass().set(3, 1));
			
			sheepGame.addTile(new Guard()
				.addPath(0, 3)
				.addPath(1, 3)
				.addPath(2, 3)
				.addPath(1, 3)
				.setOffset(1)
				);
			sheepGame.addTile(new Guard()
				.addPath(2, 5)
				.addPath(3, 5)
				.addPath(4, 5)
				.addPath(3, 5)
				);
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
		}else
		if (levelName.equals("Yardwork"))
		{
			if (showHelp)
			{
				sheepGame.addMessage(new SheepMessage("Tall grass slows\nyou down", 0.3f));
				sheepGame.addMessage(new SheepMessage("You can cut \ntall grass \nby tapping on it!", 0.3f));
			}
			
			sheepGame.addTile(new TallGrass().set(2, 4));
			
			sheepGame.addTile(new Boulder().set(0, 3));
			sheepGame.addTile(new Boulder().set(1, 3));
			sheepGame.addTile(new Boulder().set(3, 3));
			sheepGame.addTile(new Boulder().set(4, 3));
			sheepGame.addTile(new Boulder().set(4, 4));
			sheepGame.addTile(new Boulder().set(0, 4));
			sheepGame.addTile(new Boulder().set(0, 5));
			sheepGame.addTile(new Boulder().set(1, 5));
			sheepGame.addTile(new Boulder().set(3, 5));
			sheepGame.addTile(new Boulder().set(4, 5));
			
			sheepGame.addTile(new Guard()
				.addPath(1, 4)
				.addPath(2, 4)
				.addPath(3, 4)
				.addPath(2, 4)
				);
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
		}else
		if (levelName.equals("Guards!"))
		{
			if (showHelp)
			{
				sheepGame.addMessage(new SheepMessage("Don't get seen\nby guards!", 0.3f));
				sheepGame.addMessage(new SheepMessage("They can only see one\nspace in front of them", 0.3f));
			}
			WalkPath path = new WalkPath();
			path.add(4, 3);
			path.add(3, 3);
			path.add(2, 3);
			path.add(1, 3);
			path.add(0, 3);
			path.add(1, 3);
			path.add(2, 3);
			path.add(3, 3);
			sheepGame.addTile(new Guard()
				.setPath(path)
				);
			
			sheepGame.addTile(new Dog()
				.setPath(path)
				.setOffset(1)
				);
			
			sheepGame.addTile(new Boulder().set(3, 5));
			sheepGame.addTile(new Boulder().set(4, 1));
			
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
			
		}else
		if (levelName.equals("Basics"))
		{
			if (showHelp)
			{
				sheepGame.addMessage(new SheepMessage("Draw a path from\nthe bottom to the top", 0.3f));
				sheepGame.addMessage(new SheepMessage("Then tap and hold\nanywhere to move sheep\nalong the path", 0.3f));
			}
			
			sheepGame.addTile(new Boulder().set(1, 4));
			sheepGame.addTile(new Boulder().set(3, 2));
			sheepGame.addTile(new Boulder().set(4, 2));
			sheepGame.addTile(new Boulder().set(4, 5));
			sheepGame.addTile(new Boulder().set(2, 3));
			sheepGame.addTile(new Boulder().set(4, 1));
			sheepGame.addTile(new Boulder().set(0, 6));
			sheepGame.addTile(new Boulder().set(0, 1));
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
			
		}else
		if (levelName.equals("Two paths"))
		{
				
			for (int x = 0; x < 5; ++x)
				if (x != 3)
					sheepGame.addTile(new Boulder().set(x, 7));
			for (int x = 1; x < 5; ++x)
				sheepGame.addTile(new Boulder().set(x, 5));
			for (int x = 1; x < 4; ++x)
				sheepGame.addTile(new Boulder().set(x, 2));
			
			sheepGame.addTile(new TallGrass().set(3, 3));
			sheepGame.addTile(new TallGrass().set(2, 4));
			sheepGame.addTile(new TallGrass().set(0, 1));
			
			sheepGame.addTile(new Guard()
				.addPath(0, 6)
				.addPath(0, 6)
				.addPath(1, 6)
				.addPath(2, 6)
				.addPath(3, 6)
				.addPath(4, 6)
				.addPath(4, 6)
				.addPath(3, 6)
				.addPath(2, 6)
				.addPath(1, 6)
				);
			
			sheepGame.addTile(new Guard()
				.addPath(4, 4)
				.addPath(3, 4)
				.addPath(2, 4)
				.addPath(1, 4)
				.addPath(0, 4)
				.addPath(0, 3)
				.addPath(1, 3)
				.addPath(2, 3)
				.addPath(3, 3)
				.addPath(4, 3)
				);
			sheepGame.addTile(new Guard()
				.addPath(4, 4)
				.addPath(3, 4)
				.addPath(2, 4)
				.addPath(1, 4)
				.addPath(0, 4)
				.addPath(0, 3)
				.addPath(1, 3)
				.addPath(2, 3)
				.addPath(3, 3)
				.addPath(4, 3)
				.setOffset(8)
				);
			
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
		}else
		if (levelName.equals("Raptors"))
		{
			for (int x = 0; x < 5; ++x)
				if (x != 3)
					sheepGame.addTile(new Boulder().set(x, 7));
			for (int x = 1; x < 5; ++x)
				sheepGame.addTile(new Boulder().set(x, 4));
			for (int x = 0; x < 4; ++x)
				sheepGame.addTile(new Boulder().set(x, 1));
			
			sheepGame.addTile(new TallGrass().set(3, 2));
			
			for (int off = 3; off < 12; off += 4)
				sheepGame.addTile(new Guard()
					.addPath(0, 5)
					.addPath(0, 6)
					.addPath(1, 6)
					.addPath(2, 6)
					.addPath(3, 6)
					.addPath(4, 6)
					.addPath(4, 5)
					.addPath(3, 5)
					.addPath(2, 5)
					.addPath(1, 5)
					.setOffset(off)
					);
			
			for (int off = 0; off < 10; off += 4)
				sheepGame.addTile(new Guard()
					.addPath(4, 3)
					.addPath(3, 3)
					.addPath(2, 3)
					.addPath(1, 3)
					.addPath(0, 3)
					.addPath(0, 2)
					.addPath(1, 2)
					.addPath(2, 2)
					.addPath(3, 2)
					.addPath(4, 2)
					.setOffset(off)
					);
			
			
			sheepGame.addTile(new SheepObj().setOffset(0));
			sheepGame.addTile(new SheepObj().setOffset(-4));
		}
		lll.levelLoaded(levelName);
	}
	public boolean areMoveLevels()
	{
		if (currentLevel >= levelLink.size-1)
			return false;
		return true;
	}
	public int getMaxLevels()
	{
		return levelLink.size;
	}
	public void reloadLevel(SheepGame sheepGame, boolean showHelp)
	{
		loadLevel(sheepGame, currentLevel, showHelp);
	}
	public void nextLevel(SheepGame sheepGame, boolean showHelp)
	{
		loadLevel(sheepGame, currentLevel+1, showHelp);
	}
	public void loadLevel(SheepGame sheepGame, int l)
	{
		loadLevel(sheepGame, l, true);
	}
	public boolean currentLevelHasHelp()
	{
		if (currentLevelStr.equals("Guards!"))
			return true;
		if (currentLevelStr.equals("Yardwork"))
			return true;
		if (currentLevelStr.equals("Landscaping 2"))
			return true;
		if (currentLevelStr.equals("Landscaping"))
			return true;
		if (currentLevelStr.equals("Basics"))
			return true;
		if (currentLevelStr.equals("Two birds"))
			return true;
		return false;
	}
	public void loadLevel(SheepGame sheepGame, int l, boolean showHelp)
	{
		currentLevel = l;
		if (l < levelLink.size)
			loadLevel(sheepGame, levelLink.get(l), showHelp);
	}
	public void setLoadLevelListener(LoadLevelListener lll)
	{
		this.lll = lll;
	}
	public boolean isLoading()
	{
		return loading;
	}
	public void render()
	{
		batch.begin();
		//batch.draw(
		batch.end();
	}
}