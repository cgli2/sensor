package com.cnjson.sensor.db.entity;

import java.io.Serializable;

import com.cnjson.sensor.db.annotation.FieldMeta;

/**
 * 基础类型
 * @author cgli
 *
 * @param <T>
 */
public abstract class BaseEntity<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2741958602919739853L;
	
	@FieldMeta(isPrimay=true,name="id")
	private String id;
	
	public BaseEntity(){
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

 


	

}
