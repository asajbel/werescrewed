package com.blindtigergames.werescrewed.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.utils.Array;

/**
 * A HashMap with buckets at each key. Each bucket is a badlogic Array.
 * 
 * @author Kevin / stew
 *
 * @param <K>
 * @param <V>
 */
public class ArrayHash< K, V > {
	protected HashMap< K, Array< V >> data;

	public ArrayHash( ) {
		data = new HashMap< K, Array< V >>( );
	}

	public void set( K key, int index, V value ) {
		Array<V> array;
		if ( !data.containsKey( key ) ) {
			array = new Array<V>();
			data.put( key, array );
		} else {
			array = data.get( key );
		}
		if ( index >= array.size ) {
			array.ensureCapacity( index+1 );
		}
		array.set( index, value );
	}

	public V put( K key, V value ) {
		if ( !data.containsKey( key ) ) {
			data.put( key, new Array< V >( ) );
		}
		data.get( key ).add( value );
		return value;
	}

	public boolean containsKey( K key ) {
		return data.containsKey( key );
	}

	public boolean containsIndex( K key, int index ) {
		if ( data.containsKey( key ) ) {
			if ( index >= 0 && index < data.get( key ).size ) {
				return true;
			}
		}
		return false;
	}

	public V get( K key, int index ) {
		if ( data.containsKey( key ) ) {
			Array< V > values = data.get( key );
			if ( values.size > 0 ) {
				return values.get( fixIndex( values.size, index ) );
			}
		}
		return null;
	}

	public Array< V > getAll( K key ) {
		if ( data.containsKey( key ) ) {
			return data.get( key );
		}
		return null;
	}

	public V get( K key ) {
		return get( key, 0 );
	}

	public V getLast( K key ) {
		return get( key, -1 );
	}

	public HashMap< K, V > toHash( ) {
		HashMap< K, V > out = new HashMap< K, V >( );
		for ( K key : data.keySet( ) ) {
			out.put( key, get( key ) );
		}
		return out;
	}

	protected int fixIndex( int size, int index ) {
		int out = index;
		if ( size > 0 ) {
			while ( index < 0 ) {
				out += size; // Negative indecies wrap to back of list
			}
			if ( index >= size ) {
				index = size - 1;
			}
		}
		return out;
	}

	public Collection< Array< V > > arrays( ) {
		return data.values( );
	}

	public Set< K > keySet( ) {
		return data.keySet( );
	}

	public int size( ) {
		return data.size( );
	}
	
	public void remove(String key, int index){
		if ( data.containsKey( key ) ) {
			Array< V > values = data.get( key );
			if ( values.size > 0 && index < values.size) {
				values.removeIndex( index );
			}
		}
	}
	
	public void remove(String key, V value){
		if ( data.containsKey( key ) ) {
			Array< V > values = data.get( key );
			if ( values.size > 0 ) {
				values.removeValue( value, false );
			}
		}
	}
}