package com.wellav.tvideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wellav.tvideo.R;
import com.wellav.tvideo.entity.Channels;

public class ChannelListAdapter extends BaseAdapter {
	private Context mContext;
	private Channels mChannels;

	public ChannelListAdapter(Context ct,Channels channellist) {
		mContext = ct;
		mChannels = channellist;
	}
	
	public void refresh(Channels channellist){
		mChannels = channellist;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mChannels.getContentList().size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_channel, null);
			
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.item_channelname);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.item_channelcontent);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		String name = mChannels.getContentList().get(position).getName();
		//String content = mChannels.getContentList().get(position).getCurrentEpg().getName();
		viewHolder.name.setText(name);
		//viewHolder.content.setText(content);
		if(mChannels.getCode() == position){
			viewHolder.name.setSelected(true);
			viewHolder.content.setSelected(true);
		}else{
			viewHolder.name.setSelected(false);
			viewHolder.content.setSelected(false);
		}
		return convertView;
	}
	
	private class ViewHolder{
		private TextView name;
		private TextView content;
	}

}
