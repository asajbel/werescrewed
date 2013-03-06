package com.blindtigergames.werescrewed.util;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

public class ArrayHash {
	protected HashMap<String, Array<String>> data;
	
	public ArrayHash(){
		data = new HashMap<String, Array<String>>();
	}
	
	public void set(String key, int index, String value){
		if (!data.containsKey( key )){
			data.put( key, new Array<String>() );
		}
		data.get( key ).set( index, value );
	}
	
	public void add(String key, String value){
		if (!data.containsKey( key )){
			data.put( key, new Array<String>() );
		}
		data.get( key ).add( value );
	}
	
	public boolean containsKey( String key){return data.containsKey( key );}
	
	public boolean containsIndex( String key, int index){
		if (data.containsKey (key)){
			if ( index >= 0 && index < data.get(key).size ){
				return true;
			}
		}
		return false;
	}
	
	public String get(String key, int index){
		if (data.containsKey( key ) ){
			Array<String> values = data.get( key );
			if (values.size > 0){
				return values.get( fixIndex(values.size, index) );
			}
		}
		return null;
	}
	
	public Array<String> getAll(String key){
		if (data.containsKey( key )){
			return data.get( key );
		}
		return null;
	}
			
	public String get(String key){return get(key,0);}
	public String getLast(String key){return get(key,-1);}
	
	public HashMap<String,String> toHash(){
		HashMap<String,String> out = new HashMap<String,String>();
		for (String key : data.keySet( )){
			out.put( key, get(key) );
		}
		return out;
	}
	
	
	protected int fixIndex(int size, int index){
		int out = index;
		if (size > 0){
			while (index < 0){
				out += size; //Negative indecies wrap to back of list
			}
			if (index >= size){
				index = size-1;
			}
		}
		return out;
	}
}