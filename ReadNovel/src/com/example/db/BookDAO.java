package com.example.db;

import java.net.ContentHandler;

import com.example.util.MyConfig;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Config;

public class BookDAO {
	
	private SQLiteDatabase mDatabase;
	private Context mContext;
	private SharedPreferences mPreferences;
	
	public BookDAO(Context context){
		mContext = context;
		mPreferences = context.getSharedPreferences("book", Context.MODE_PRIVATE);
		openOrCreate();
	}
	
	private void openOrCreate(){
		mDatabase = mContext.openOrCreateDatabase(MyConfig.DB_NAME, Context.MODE_PRIVATE, null); 
		boolean isFirst = mPreferences.getBoolean("first", false);
		if(!isFirst){
			mDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+MyConfig.TABLE_NAME+
					"(_id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, content INTEGER)");
			Editor editor = mPreferences.edit();
			editor.putBoolean("first", true);
			editor.commit();
		}
		
	}
	public long addContent(String title,String content) {
		ContentValues values = new ContentValues();
		values.put("title", title);
		values.put("content", content);
		long id = mDatabase.insert(MyConfig.TABLE_NAME, null, values);
		return id;
	}
	
	public String  getContent(int id) {
		String content = "";
		String sql = "Select * from "+MyConfig.TABLE_NAME+" Where _id= "+id;
		Cursor cursor = mDatabase.rawQuery(sql,null);
		while(cursor.moveToNext()){
			content = cursor.getString(cursor.getColumnIndex("content"));
		}
		return content;
	}
	
	public String  getTitle(int id) {
		String title = "";
		String sql = "Select * from "+MyConfig.TABLE_NAME+" Where _id= "+id;
		Cursor cursor = mDatabase.rawQuery(sql,null);
		while(cursor.moveToNext()){
			title = cursor.getString(cursor.getColumnIndex("title"));
		}
		return title;
	}
	
	public int getCount() {
		int count = 0;
		String sql = "Select * from "+MyConfig.TABLE_NAME;
		Cursor cursor = mDatabase.rawQuery(sql, null);
		count = cursor.getCount();
		return count;
		
	}

}
