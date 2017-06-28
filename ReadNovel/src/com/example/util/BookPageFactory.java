
package com.example.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.db.BookDAO;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * 
 * @author JcMan bolg--http://blog.csdn.net/programchangesworld
 *
 */
public class BookPageFactory {
	
	private File book_file = null;
	private int mStartPos = 0;
	private MappedByteBuffer m_mbBuf = null;
	private int m_mbBufLen = 0;
	private String m_strCharsetName = "GBK";
	private BookDAO mBookDAO;
	private Context mContext;
	private FileChannel fc;
	
	public BookPageFactory(Context context) {
		mContext = context;
	}
	
	@SuppressWarnings("resource")
	public void openbook(String strFilePath) throws IOException {
		book_file = new File(strFilePath);
		long lLen = book_file.length();
		m_mbBufLen = (int) lLen;
		m_mbBuf = new RandomAccessFile(book_file, "rw").getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, lLen);
		  fc = new RandomAccessFile(book_file, "rw").getChannel();  
        //注意，文件通道的可读可写要建立在文件流本身可读写的基础之上  
         m_mbBuf = fc.map(FileChannel.MapMode.READ_WRITE, 0, lLen);  
		
	}
	// 读取一段落
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// 根据编码格式判断换行
		if (m_strCharsetName.equals("UTF-16LE")){
			while (i < m_mbBufLen - 1){
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00){
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")){
			while (i < m_mbBufLen - 1){
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a){
					break;
				}
			}
		} else {
			while (i < m_mbBufLen){
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a){
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++){
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		try {
			fc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf;
	}
	private String getNextString(){
		byte[] buf = readParagraphForward(mStartPos);
		mStartPos+=buf.length;
		String s = "";
		try {
			s = new String(buf,m_strCharsetName);
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return s;
	}
	
	public void showGraph(){
		mBookDAO = new BookDAO(mContext);
		int flag = 0;
		String strLine = "";
		String content = "";
		String title = "";
		String regex = "第.{1,8}章.{0,}\r\n";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(strLine);
		while(mStartPos<m_mbBufLen-1){
			while(strLine.length()<1000&&mStartPos<m_mbBufLen-1)
				strLine+=getNextString();
			matcher = pattern.matcher(strLine);
			if(matcher.find()) {
				if(flag==0){
					content  = strLine;
					title = matcher.group();
					flag=1;
				}else{
					content+=strLine.substring(0, matcher.start());
					mBookDAO.addContent(title, content);
					title = matcher.group();
					content = strLine.substring(matcher.start(),strLine.length());
				}
			}else
				content+=strLine;
			strLine="";
		} 
	}
}
