package com.example.readnovel;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter{

	private List<String> mList;
	private Context mContext;
	public ListAdapter(Context context,List<String> list) {
		mContext = context;
		mList = list;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder ;
		if(convertView==null){
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_title,null);
			holder.title = (TextView) convertView.findViewById(R.id.item_tv_title);
			convertView.setTag(holder);
		}else {
			holder = (Holder) convertView.getTag();
		}
		String title = mList.get(position);
	
		title = title.replaceAll("\n", "");
		title = title.replaceAll("\r\n","");
		title = title.replaceAll("\r", "");
		
		if(title.contains("\n"))
			Log.e("log", "°üÀ¨");
		
		
		holder.title.setText(title);
		return convertView;
	}
	
	class Holder {
		TextView title;
	}

}
