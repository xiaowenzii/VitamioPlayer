package com.wellav.tvideo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Channels implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mCode;
	private List<Channel> mContentList = new ArrayList<Channel>();
	public int getCode() {
		return mCode;
	}
	public void setCode(int code) {
		mCode = code;
	}
	public List<Channel> getContentList() {
		return mContentList;
	}
	public void setContentList(List<Channel> channelList) {
		mContentList = channelList;
	}
	
	

}
