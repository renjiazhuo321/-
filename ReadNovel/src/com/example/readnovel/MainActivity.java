package com.example.readnovel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.db.BookDAO;
import com.example.util.BookPageFactory;

public class MainActivity extends Activity implements OnClickListener,OnItemClickListener{

	private DrawerLayout mDrawerlayout;
	private TextView mTextContent;
	private Button mBtn;
	private ListView mListView;
	private BookPageFactory factory;
	private BookDAO bookDAO;
	private ProgressDialog dialog;
	private List<String> mTitleList;
	float   downX;
	float   downY;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			dialog.cancel();
			initListView();
			toggle();
			
		};
	};
	private SharedPreferences mPreferences;
	private int position;
	private ScrollView scrollView;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initVariable();
		initView();
		initListener();
		initContent();
	}
	private void initContent() {
		mPreferences = getSharedPreferences("position", Context.MODE_PRIVATE);
		position = mPreferences.getInt("position", 1);
		
		showContent();
		mPreferences.edit().putInt("position", position).commit();
	}
	public void showContent(){
		String content=	bookDAO.getContent(position);
		mTextContent.setText(content);
	}
	private void initVariable(){
		mTitleList = new ArrayList<String>();
		bookDAO = new BookDAO(this);
	    factory = new BookPageFactory(this);
	    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/book.txt";
	    try {
			factory.openbook(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    dialog = new ProgressDialog(this);
	    dialog.setMessage("智能分章中...");
		
	}
	private void initListener() {
		mBtn.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}
	private void initView() {
		mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mTextContent = (TextView) findViewById(R.id.tv_content);
		mBtn = (Button) findViewById(R.id.btn);
		mListView = (ListView) findViewById(R.id.listview_left_drawer);
		scrollView = (ScrollView) findViewById(R.id.contentSv);
		mTextContent.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 String action = "";
			        //在触发时回去到起始坐标
			        float x= event.getX();
			        float y = event.getY();
			        switch (event.getAction()){
			            case MotionEvent.ACTION_DOWN:
			                //将按下时的坐标存储
			            	   downX = x;
			            	   downY = y;
			                break;
			            case MotionEvent.ACTION_UP:

			                //获取到距离差
			                float dx= x-downX;
			                float dy = y-downY;
			                //防止是按下也判断
			                if (Math.abs(dx)>8&&Math.abs(dy)>8) {
			                    //通过距离差判断方向
			                    int orientation = getOrientation(dx, dy);
			                    switch (orientation) {
			                        case 'r':
			                            action = "右";
			                            
			                            position= position-1;
			                            if(position<1){
			                            	position=1;
			                            	return true;
			                            }
			                            showContent();
			                            mPreferences.edit().putInt("position",position );
			                            scrollView.scrollTo(0, 0);
			                            break;
			                        case 'l':
			                            action = "左";
			                            
			                            position= position+1;
			                            mPreferences.edit().putInt("position",position );
			                            showContent();
			                            scrollView.scrollTo(0, 0);
			                            break;
			                        case 't':
			                            action = "上";
			                            break;
			                        case 'b':
			                            action = "下";
			                            break;
			                    }
			                }
			                break;
			            case MotionEvent.ACTION_CANCEL:

			                //获取到距离差
			                 dx= x-downX;
			                 dy = y-downY;
			                //防止是按下也判断
			                if (Math.abs(dx)>8&&Math.abs(dy)>8) {
			                    //通过距离差判断方向
			                    int orientation = getOrientation(dx, dy);
			                    switch (orientation) {
			                        case 'r':
			                            action = "右";
			                            
			                            position= position-1;
			                            if(position<1){
			                            	position=1;
			                            	return true;
			                            }
			                            showContent();
			                            mPreferences.edit().putInt("position",position );
			                            break;
			                        case 'l':
			                            action = "左";
			                            
			                            position= position+1;
			                            mPreferences.edit().putInt("position",position );
			                            showContent();
			                            break;
			                        case 't':
			                            action = "上";
			                            break;
			                        case 'b':
			                            action = "下";
			                            break;
			                    }
			                }
			            	break;
			        }
				return true;
			}
		});
		initListView();
	}
	
	private void initListView() {
		int count = bookDAO.getCount();
		if(count>0){
			for(int i=0;i<count;i++){
				String title = bookDAO.getTitle(i+1);
				mTitleList.add(title);
			}
			ListAdapter adapter = new ListAdapter(this, mTitleList);
			mListView.setAdapter(adapter);
		}
	}
	@Override
	public void onClick(View v){
		if(v.getId()==R.id.btn){
			String content = bookDAO.getContent(1);
			if(content!=null&&content.length()>100)
				toggle();
			else{
				 dialog.show();
				new Thread(new Runnable(){
						@Override
						public void run(){
							factory.showGraph();
							mHandler.sendEmptyMessage(0);
						}
				}).start();
			}
		}
	}
	private void toggle(){
		boolean isOpen = mDrawerlayout.isDrawerOpen(mListView);
		if(isOpen)
			mDrawerlayout.closeDrawer(mListView);
		else
			mDrawerlayout.openDrawer(mListView);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String content = bookDAO.getContent(position+1);
		mTextContent.setText(content);
		toggle();
		
	}
	/**
     * 根据距离差判断 滑动方向
     * @param dx X轴的距离差
     * @param dy Y轴的距离差
     * @return 滑动的方向
     */
    private int getOrientation(float dx, float dy) {
        if (Math.abs(dx)>Math.abs(dy)){
            //X轴移动
            return dx>0?'r':'l';
        }else{
            //Y轴移动
            return dy>0?'b':'t';
        }
    }
}
