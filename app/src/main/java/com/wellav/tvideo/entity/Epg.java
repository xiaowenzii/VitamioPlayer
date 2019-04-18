package com.wellav.tvideo.entity;

import java.io.Serializable;

public class Epg implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mStartTime;
	private String mEndTime;
	private String mName;
	public String getStartTime() {
		return mStartTime;
	}
	public void setStartTime(String startTime) {
		mStartTime = startTime;
	}
	public String getEndTime() {
		return mEndTime;
	}
	public void setEndTime(String endTime) {
		mEndTime = endTime;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	
	
	

}
