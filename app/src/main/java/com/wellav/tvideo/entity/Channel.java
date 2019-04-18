package com.wellav.tvideo.entity;

import java.io.Serializable;

public class Channel implements Serializable{

	private static final long serialVersionUID = 1L;
	private String mProvider;
	private String mGuid;
	private String mLevel;
	private String mType;
	private int mHits;
	private int mChannelId;
	private int mCatchupDays;
	private boolean mAllowRecord;
	private String mProtocol;
	private String mName;
	private String mPlayUrl;
	private String mPic;
	private Epg mCurrentEpg;
	private Epg mNextEpg;
	public String getProvider() {
		return mProvider;
	}
	public void setProvider(String provider) {
		mProvider = provider;
	}
	public String getGuid() {
		return mGuid;
	}
	public void setGuid(String guid) {
		mGuid = guid;
	}
	public String getLevel() {
		return mLevel;
	}
	public void setLevel(String level) {
		mLevel = level;
	}
	public String getType() {
		return mType;
	}
	public void setType(String type) {
		mType = type;
	}
	public int getHits() {
		return mHits;
	}
	public void setHits(int hits) {
		mHits = hits;
	}
	public int getChannelId() {
		return mChannelId;
	}
	public void setChannelId(int channelId) {
		mChannelId = channelId;
	}
	public int getCatchupDays() {
		return mCatchupDays;
	}
	public void setCatchupDays(int catchupDays) {
		mCatchupDays = catchupDays;
	}
	public boolean isAllowRecord() {
		return mAllowRecord;
	}
	public void setAllowRecord(boolean allowRecord) {
		mAllowRecord = allowRecord;
	}
	public String getProtocol() {
		return mProtocol;
	}
	public void setProtocol(String protocol) {
		mProtocol = protocol;
	}
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		mName = name;
	}
	public String getPlayUrl() {
		return mPlayUrl;
	}
	public void setPlayUrl(String playUrl) {
		mPlayUrl = playUrl;
	}
	public String getPic() {
		return mPic;
	}
	public void setPic(String pic) {
		mPic = pic;
	}
	public Epg getCurrentEpg() {
		return mCurrentEpg;
	}
	public void setCurrentEpg(Epg currentEpg) {
		mCurrentEpg = currentEpg;
	}
	public Epg getNextEpg() {
		return mNextEpg;
	}
	public void setNextEpg(Epg nextEpg) {
		mNextEpg = nextEpg;
	}
	
	
	
}
