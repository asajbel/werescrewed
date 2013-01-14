package com.blindtiger.werescrewed.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

//an Entity is anything that can exist, it has a position and a texture
public class Entity
{
	public String name;
	public Vector2 position;
	public Texture texture;
	public Body body;
	
	public Entity()
	{
		//none
	}
	
	public Entity(String n, Vector2 pos, Texture tex)
	{
		this.name = n;
		this.position = pos;
		this.texture = tex;
	}

    public void Move(Vector2 vector)
    {
        this.position.x += vector.x;
        this.position.y += vector.y;
    }
    
    public void draw(SpriteBatch batch)
    {
    	batch.draw(this.texture, this.position.x, this.position.y);
    }
    

	public void update()
	{
	}

}