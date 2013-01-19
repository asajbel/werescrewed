package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.mover.IMover;
import com.blindtigergames.werescrewed.screens.GameScreen;

//an Entity is anything that can exist, it has a position and a texture
public class Entity
{
	public String name;
	public EntityDef type;
	public Sprite sprite;
	public Vector2 offset;
	public Body body;
	protected World world;
	public IMover mover;
	
	public Entity(){
		type = null;
		sprite = null;
		body = null;
		world = null;
		offset = new Vector2(0.0f,0.0f);
		name = "I AM ERROR.";
	}
	
	public Entity(String n, EntityDef d, World w, Vector2 pos, float rot, Vector2 sca)
	{
		this();
		name = n;
		type = d;
		world = w;
		constructSprite();
		constructBody(pos.x, pos.y, sca.x, sca.y);
	}
	
	public Entity(String n, Sprite spr, Body bod)
	{
		this();
		name = n;
		sprite = spr;
		body = bod;
		if (bod != null){
			world = bod.getWorld();
			sprite.setScale(GameScreen.PIXEL_TO_BOX);
		}

	}
	
	public Entity(String n, Vector2 pos, Texture tex, Body bod)
	{
		this(n, tex == null ? null: generateSprite(tex), bod);
		setPosition(pos);
	}
	
	
	public void setPosition(float x, float y){
		if (body != null){
			body.setTransform(x, y, body.getAngle());
		} else if (sprite != null){
			sprite.setPosition(x, y);
		}
	}
	
	public void setPosition(Vector2 pos){
		setPosition(pos.x,pos.y);
	}
	
	public Vector2 getPosition(){
		return body.getPosition();
	}
	
    public void Move(Vector2 vector)
    {
    	Vector2 pos = body.getPosition().add(vector);
    	setPosition(pos);
    }
    
    public void draw(SpriteBatch batch)
    {
    	if (sprite != null)
    		sprite.draw(batch);
    }
    

	public void update()
	{
		if (body != null && sprite != null){
			Vector2 bodyPos = body.getPosition();
			Vector2 spritePos = bodyPos.mul(GameScreen.BOX_TO_PIXEL).add(offset);
			sprite.setPosition(spritePos.x, spritePos.y);
			if(mover != null)
				mover.move(body);
		}
	}

	protected String generateName(){
		return type.name;
	}
	
	protected static Sprite generateSprite(Texture tex){
		Sprite out = new Sprite(tex);
		out.setOrigin(tex.getWidth()/2, tex.getHeight()/2);
		return out;
	}
	
	protected void constructSprite(){
		if (type != null && type.texture != null)
			sprite = new Sprite(type.texture);
	}
	
	protected void constructBody( float x, float y, float width, float height ){
		if (type != null){
			body = world.createBody(type.bodyDef);
			for (FixtureDef fix : type.fixtureDefs){
				body.createFixture(fix);
				Gdx.app.log( "Entity", fix.toString());
			}
			setPosition(x,y);
		}
	}
}