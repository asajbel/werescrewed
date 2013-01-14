package com.blindtigergames.werescrewed.entity;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.blindtigergames.werescrewed.screens.GameScreen;





public class Player extends Entity {

	private World world;
	//public Body body = null;
	//Being has a Body body variable
	public Fixture playerPhysicsFixture;
	public Fixture playerSensorFixture;
	final static float MAX_VELOCITY = 300f;
	float stillTime = 0;
	long lastGroundTime = 0;
	static Texture player = new Texture(Gdx.files.internal("data/libgdx.png"));
	int prevKey;
	
	//private Camera cam;

	public enum PlayerState {
		Standing, Jumping, Falling
	}

	PlayerState playerState = PlayerState.Standing;

	//needs to be rewritten!!!!!!!!!!!!!!!!!!
	public Player(World world, float posX, float posY, String n, Texture tex)
	{
		super(n, new Vector2(posX, posY), tex);
		this.world = world;
		//createPlayerBody(posX, posY);
		createPlayerBodyOLD(posX, posY);
	}

	public Player(World world, Vector2 pos, String n, Texture tex)
	{
		super(n, pos, player);
		this.world = world;
		//createPlayerBody(posX, posY);
		createPlayerBodyOLD(pos.x, pos.y);
	}
	public Player(World world, Vector2 pos, String n)
	{
		super(n, pos, player);
		this.world = world;
		//createPlayerBody(posX, posY);
		createPlayerBodyOLD(pos.x, pos.y);
	}
	//I tried some weird stuff in this constructor
	private void createPlayerBody(float x, float y) {

		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyType.DynamicBody;
		playerBodyDef.position.set(x, y);
		body = world.createBody(playerBodyDef);
		
		PolygonShape poly = new PolygonShape();		
		poly.setAsBox(25f, 25f);
		playerPhysicsFixture = body.createFixture(poly, 1);
		poly.dispose();			
 
		CircleShape circle = new CircleShape();		
		circle.setRadius(25f);
		circle.setPosition(new Vector2(0, -25f));
		playerSensorFixture = body.createFixture(circle, 0);	

		circle.dispose();		
 
		body.setBullet(true);
		/*
		CircleShape playerfeetShape = new CircleShape();
		playerfeetShape.setRadius(7f);
		
		FixtureDef playerFixtureDef = new FixtureDef();
		//playerBody.createFixture(playerPolygonShape, 1.0f);
		playerFixtureDef.shape = playerfeetShape;
		playerFixtureDef.density = 0.9f;
		playerFixtureDef.friction = 0f;
		playerFixtureDef.restitution = 0.0f;
		playerBody.createFixture(playerFixtureDef);
		playerBody.setGravityScale(1f);
		playerBody.setFixedRotation(true);
		//playerBody.
		playerfeetShape.dispose();
		*/
	}
	
	//works normally
	private void createPlayerBodyOLD( float x, float y) {

		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyType.DynamicBody;
		playerBodyDef.position.set(x, y);
		body = world.createBody(playerBodyDef);
		CircleShape playerfeetShape = new CircleShape();
		playerfeetShape.setRadius(10f * GameScreen.PIXEL_TO_BOX);
		FixtureDef playerFixtureDef = new FixtureDef();
		//playerBody.createFixture(playerPolygonShape, 1.0f);
		playerFixtureDef.shape = playerfeetShape;
		playerFixtureDef.density = 9.9f;
		playerFixtureDef.friction = 0f;
		playerFixtureDef.restitution = 0.0f;
		body.createFixture(playerFixtureDef);
		body.setGravityScale(.1f);
		body.setFixedRotation(true);
		//playerBody.
		playerfeetShape.dispose();

	}
	
	public void moveRight()
	{
		if (body.getLinearVelocity().x<2.0f) {
			body.applyLinearImpulse(new Vector2(.004f, 0.0f), body.getWorldCenter());
		}
		//body.applyLinearImpulse(new Vector2(0.004f, 0.0f), 
		//		body.getWorldCenter());
		
		// Following three lines update the texture
		// doesn't belong here, I learned
		//Vector2 pos = playerBody.getPosition();
		//this.positionX = pos.x;
		//this.positionY = pos.y;
	}
	
	public void moveLeft()
	{
		if (body.getLinearVelocity().x>-2.0f) {
			body.applyLinearImpulse(new Vector2(-0.004f, 0.0f), body.getWorldCenter());
		}
		//body.applyLinearImpulse(new Vector2(-0.004f, 0.0f), 
		//		body.getWorldCenter());
		//Gdx.app.debug("Physics:", "Applying Left Impulse to player at "+playerBody.getWorldCenter());

	}
	
	public void jump()
	{
		if (Math.abs(body.getLinearVelocity().y) < 1e-5) {
			body.applyLinearImpulse(new Vector2(0.0f, 0.2f),
					body.getWorldCenter());
		}

	}
	
	private void stop()
	{
		float velocity = body.getLinearVelocity().x;
		
		if (velocity != 0.0f) {
			if (velocity < - 0.1f) 
				body.applyLinearImpulse(new Vector2(0.010f, 0.0f), body.getWorldCenter());
			else if (velocity > 0.1f)
				body.applyLinearImpulse(new Vector2(-0.010f, 0.0f), body.getWorldCenter());
			else if (velocity > -0.1 && velocity < 0.1f)
				body.setLinearVelocity(0.0f, 0.0f);
		}
		
		
	}
	
	public void draw(SpriteBatch batch)
	{

		batch.draw(this.texture, this.position.x, this.position.y);
	
	}
	
	public void update()
	{
		Vector2 pos = body.getPosition();
		Vector2 vel = body.getLinearVelocity();
		

		this.position = pos;
		
		
		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.SPACE))
		{
			jump();
		}
		if (Gdx.input.isKeyPressed(Keys.A))
		{
			moveLeft();
			prevKey = Keys.A;
		}

		if (Gdx.input.isKeyPressed(Keys.D))
		{
			moveRight();
			prevKey = Keys.D;
		}
		if(Gdx.input.isKeyPressed(Keys.S))
		{
			stop();
		}
		
		if ((!Gdx.input.isKeyPressed(Keys.D) && !Gdx.input.isKeyPressed(Keys.A)) 
				&& (prevKey == Keys.D || prevKey == Keys.A)) {
			stop(); 
		}
		
		if (Gdx.input.isTouched()) {
			jump();
		} 
		
/*		
 * This example is found at a blog, i couldn't get it to work right away
		boolean grounded = isPlayerGrounded(Gdx.graphics.getDeltaTime());
		if(grounded) {
			lastGroundTime = System.nanoTime();
		} else {
			if(System.nanoTime() - lastGroundTime < 100000000) {
				grounded = true;
			}
		}
 
		// cap max velocity on x		
		if(Math.abs(vel.x) > MAX_VELOCITY) {			
			vel.x = Math.signum(vel.x) * MAX_VELOCITY;
			playerBody.setLinearVelocity(vel.x, vel.y);
		}
 
		// calculate stilltime & damp
		if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D)) {			
			stillTime += Gdx.graphics.getDeltaTime();
			playerBody.setLinearVelocity(vel.x * 0.9f, vel.y);
		}
		else { 
			stillTime = 0;
		}			
 
		// disable friction while jumping
		if(!grounded) {			
			playerPhysicsFixture.setFriction(0f);
			playerSensorFixture.setFriction(0f);			
		} else {
			if(!Gdx.input.isKeyPressed(Keys.A) && !Gdx.input.isKeyPressed(Keys.D) && stillTime > 0.2) {
				playerPhysicsFixture.setFriction(100f);
				playerSensorFixture.setFriction(100f);
			}
			else {
				playerPhysicsFixture.setFriction(0.2f);
				playerSensorFixture.setFriction(0.2f);
			}
 
			//if(groundedPlatform != null && groundedPlatform.dist == 0) {
			//	playerBody.applyLinearImpulse(0, -24, pos.x, pos.y);				
			//}
		}		
 
		// apply left impulse, but only if max velocity is not reached yet
		
		if(Gdx.input.isKeyPressed(Keys.A) && vel.x > -MAX_VELOCITY) {
			//playerBody.applyLinearImpulse(-2f, 0, pos.x, pos.y);
			moveLeft();
		}
 
		// apply right impulse, but only if max velocity is not reached yet
		if(Gdx.input.isKeyPressed(Keys.D) && vel.x < MAX_VELOCITY) {
			//playerBody.applyLinearImpulse(2f, 0, pos.x, pos.y);
			moveRight();
		}
 
		// jump, but only when grounded
		if(Gdx.input.isKeyPressed(Keys.W)) {			
			//jump = false;
			if(grounded) {
				playerBody.setLinearVelocity(vel.x, 0);			
				//System.out.println("jump before: " + player.getLinearVelocity());
				playerBody.setTransform(pos.x, pos.y + 0.01f, 0);
				playerBody.applyLinearImpulse(0, 30, pos.x, pos.y);			
				//System.out.println("jump, " + player.getLinearVelocity());				
			}
		}
		*/	
	}
	
	//adapted from example code, doesn't work yet
	private boolean isPlayerGrounded(float deltaTime) {				

		List<Contact> contactList = world.getContactList();
		for(int i = 0; i < contactList.size(); i++) {
			Contact contact = contactList.get(i);
			if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
			   contact.getFixtureB() == playerSensorFixture)) {				
 
				Vector2 pos = body.getPosition();
				WorldManifold manifold = contact.getWorldManifold();
				boolean below = true;
				for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below &= (manifold.getPoints()[j].y < pos.y - 1.5f);
				}
				if(below) {										
					return true;
				}
 
				return false;
			}
		}
		return false;
	}
	
	
}