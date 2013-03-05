package com.blindtigergames.werescrewed.entity.builders;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.action.IAction;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;

public class EventTriggerBuilder extends
		GenericEntityBuilder< EventTriggerBuilder > {

	// name and pos in super
	private boolean rectangle;
	private boolean circle;
	private float radius;
	private float width, height;
	private boolean offsetAbove, offsetBelow, offsetRight, offsetLeft;
	private boolean attachedToEntity;
	@SuppressWarnings( "unused" )
	private float attachedEntityHeight, attachedEntityWidth;
	private boolean repeatableAction;
	private boolean twoPlayersToActivate;
	private boolean twoPlayersToDeactive;
	private IAction beginAction;
	private IAction endAction;
	private ArrayList< Entity > entitiesToAdd;

	public EventTriggerBuilder( World world ) {
		super( );
		reset( );
		super.world( world );
		entitiesToAdd = new ArrayList< Entity >( );
	}

	@Override
	public EventTriggerBuilder reset( ) {
		super.resetInternal( );
		this.rectangle = false;
		this.circle = true;
		this.radius = 100f;
		this.width = 100f;
		this.height = 100f;
		this.attachedEntityHeight = 0.0f;
		this.attachedEntityWidth = 0.0f;
		this.endAction = null;
		this.beginAction = null;
		this.repeatableAction = false;
		this.twoPlayersToActivate = false;
		this.twoPlayersToDeactive = false;
		this.offsetAbove = false;
		this.offsetBelow = false;
		this.offsetRight = false;
		this.offsetLeft = false;
		this.attachedToEntity = false;
		return this;
	}

	public EventTriggerBuilder radius( float radius ) {
		this.radius = radius;
		return this;
	}

	public EventTriggerBuilder height( float height ) {
		this.height = height;
		return this;
	}

	public EventTriggerBuilder width( float width ) {
		this.width = width;
		return this;
	}

	public EventTriggerBuilder circle( ) {
		this.circle = true;
		this.rectangle = false;
		return this;
	}

	public EventTriggerBuilder rectangle( ) {
		this.rectangle = true;
		this.circle = false;
		return this;
	}

	public EventTriggerBuilder setPositionToEntity( Entity entity ) {
		this.attachedToEntity = true;
		this.pos = entity.getPositionPixel( );
		return this;
	}

	public EventTriggerBuilder offsetAbove( ) {
		this.offsetAbove = true;
		this.offsetBelow = false;
		return this;
	}

	public EventTriggerBuilder offsetBelow( ) {
		this.offsetBelow = true;
		this.offsetAbove = false;
		return this;
	}

	public EventTriggerBuilder offsetRight( ) {
		this.offsetRight = true;
		this.offsetLeft = false;
		return this;
	}

	public EventTriggerBuilder offsetLeft( ) {
		this.offsetLeft = true;
		this.offsetRight = false;
		return this;
	}

	public EventTriggerBuilder repeatable( ) {
		this.repeatableAction = true;
		return this;
	}

	public EventTriggerBuilder twoPlayersToActivate( ) {
		this.twoPlayersToActivate = true;
		return this;
	}

	public EventTriggerBuilder twoPlayersToDeactivate( ) {
		this.twoPlayersToDeactive = true;
		return this;
	}

	public EventTriggerBuilder beginAction( IAction begin ) {
		this.beginAction = begin;
		return this;
	}

	public EventTriggerBuilder endAction( IAction end ) {
		this.endAction = end;
		return this;
	}

	public EventTriggerBuilder addEntity( Entity entity ) {
		entitiesToAdd.add( entity );
		return this;
	}

	public EventTrigger build( ) {
		EventTrigger et = new EventTrigger( this.name, this.world );

		if ( this.circle ) {
			if ( this.attachedToEntity ) {
				if ( offsetAbove ) {
					this.pos = new Vector2( this.pos.x, this.pos.y );
				} else if ( offsetBelow ) {
					this.pos = new Vector2( this.pos.x, this.pos.y );
				} else if ( offsetRight ) {
					this.pos = new Vector2( this.pos.x, this.pos.y );
				} else if ( offsetLeft ) {
					this.pos = new Vector2( this.pos.x, this.pos.y );
				}
			}
			et.constructCircleBody( this.radius, this.pos );
		} else if ( this.rectangle ) {
			if ( this.attachedToEntity ) {
				if ( offsetAbove ) {
					this.pos = new Vector2( this.pos.x, this.pos.y );
				} else if ( offsetBelow ) {
					this.pos = new Vector2( this.pos.x, this.pos.y );
				} else if ( offsetRight ) {
					this.pos = new Vector2( this.pos.x, this.pos.y );
				} else if ( offsetLeft ) {
					this.pos = new Vector2( this.pos.x, this.pos.y );
				}
			}
			et.contructRectangleBody( this.height, this.width, this.pos );
		}

		et.setRepeatable( this.repeatableAction );
		et.setTwoPlayersToActivate( this.twoPlayersToActivate );
		et.setTwoPlayersToDeactivate( this.twoPlayersToDeactive );
		et.addBeginIAction( this.beginAction );
		et.addEndIAction( this.endAction );

		for ( Entity e : entitiesToAdd ) {
			et.addEntityToTrigger( e );
		}
		return et;
	}
}