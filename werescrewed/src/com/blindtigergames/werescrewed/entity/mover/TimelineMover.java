package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class TimelineMover implements IMover {

    // kinematic can't collide with kinematic or static bodies
    // kinematic bodies have infinite mass

    boolean loop;
    SteeringOutput movement;

    public TimelineMover() {
        movement = new SteeringOutput( new Vector2( 0, -0.01f ), 0 );
    }

    @Override
    public void move( float deltaTime, Body body ) {
        // TODO Auto-generated method stub
        movement.applySteering( body );
    }

    @Override
    public void move( float deltaTime, Body body, SteeringOutput steering ) {
        // TODO Auto-generated method stub
        movement.add( steering ).applySteering( body );
        body.setAngularVelocity( movement.rotation );
        body.setLinearVelocity( movement.velocity );
    }
}
