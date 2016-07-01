package com.smart.mirrorer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.smart.mirrorer.bean.DBHistoryData;

import java.util.ArrayList;
import java.util.List;
/**
 * video_path varchar(100),
 * quesion_desc varchar(100),
 * question_id varchar(50),
 * tutor_head varchar(100),
 * tutor_nick varchar(50),
 * price integer,
 * time_format varchar(50),
 * time integer null)");
 */
public class VideoHistoryProviderService {
	//获得数据库操作实例
	private DBOpenHelper dbOpenHelper;
	//引入上下文对象
	public VideoHistoryProviderService(Context context) {
		dbOpenHelper = DBOpenHelper.getInstance(context);
	}
	
	public List<DBHistoryData> getAllDate() {
		List<DBHistoryData> videos = new ArrayList<DBHistoryData>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from video_history order by time desc", null);
		Log.i("lzm","getAllDate size = "+cursor.getCount());
		while(cursor.moveToNext()) {
			String desc = cursor.getString(cursor.getColumnIndex("quesion_desc"));
			String tutorName = cursor.getString(cursor.getColumnIndex("tutor_nick"));
			String tutorHead = cursor.getString(cursor.getColumnIndex("tutor_head"));
			String videoPath = cursor.getString(cursor.getColumnIndex("video_path"));
			float price = cursor.getFloat(cursor.getColumnIndex("price"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String timeFormat = cursor.getString(cursor.getColumnIndex("time_format"));

			DBHistoryData findData = new DBHistoryData();
			findData.questionDesc = desc;
			findData.tutorHead = tutorHead;
			findData.tutorNick = tutorName;
			findData.videoPath = videoPath;
			findData.price = price;
			findData.time = time;
			findData.timeFormat = timeFormat;

			videos.add(findData);
		}
		cursor.close();
		return videos;
	}

	public void saveOrUpdateHistory(DBHistoryData sourceData,String keyword) {
		Log.i("lzm","保存录像信息到表");
		DBHistoryData dbData = find(keyword);
		if (dbData == null) {
			Log.i("lzm","保存:uri="+sourceData.getVideoPath());
			saveSource(sourceData);
		} else {
//			dbData.time = System.currentTimeMillis()+"";
			Log.i("lzm","更新");
			update(sourceData);
		}
	}

	/**
	 *更新数据库
	 * @param data
	 */
	public void update(DBHistoryData data) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update video_history set time=? price=? time_format=? where question_id=?",
				new Object[]{data.time, data.questionId,data.timeFormat});
	}

	/**
	 * 保存数据到数据库
	 * @param data 传入的数据对象
	 */
	public void saveSource(DBHistoryData data) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("insert into video_history(time,video_path,quesion_desc,question_id,tutor_head," +
				"tutor_nick,time_format,price) values(?,?,?,?,?,?,?,?)",
				new Object[]{data.time,data.videoPath, data.questionDesc,data.questionId,data.tutorHead,
						data.tutorNick,data.timeFormat,data.price});
	}
	/**
	 * 查询数据库中的的指定数据
	 * @return
	 */
	public DBHistoryData find(String keyName) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from video_history where question_id=?", new String[]{keyName});
		if(cursor.moveToFirst()) {
			String desc = cursor.getString(cursor.getColumnIndex("quesion_desc"));
			String tutorName = cursor.getString(cursor.getColumnIndex("tutor_nick"));
			String tutorHead = cursor.getString(cursor.getColumnIndex("tutor_head"));
			String videoPath = cursor.getString(cursor.getColumnIndex("video_path"));
			String timeFormat = cursor.getString(cursor.getColumnIndex("time_format"));
			int price = cursor.getInt(cursor.getColumnIndex("price"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			Log.e("lzm","time = "+time);
			cursor.close();

			DBHistoryData findData = new DBHistoryData();
			findData.questionDesc = desc;
			findData.tutorHead = tutorHead;
			findData.tutorNick = tutorName;
			findData.videoPath = videoPath;
			findData.price = price;
			findData.time = time;
			findData.timeFormat = timeFormat;

			return findData;
		}
		return null;
	}


	public void deleteHistorysWithName(String key) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from video_history where question_id=?", new Object[]{key});
	}

	public void clearHistorys() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from video_history");
	}


	/**
	 * 分页查询数据
	 * @param offset 跳过的记录数
	 * @param maxSize 显示的最大记录数
	 * @return
	 */
	public List<DBHistoryData> getPagingDate(int offset, int maxSize) {
		List<DBHistoryData> persons = new ArrayList<DBHistoryData>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from video_history order by id asc limit ?,?",
				new String[]{String.valueOf(offset), String.valueOf(maxSize)});
		while(cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String isbn = cursor.getString(cursor.getColumnIndex("isbn"));
//			int account = cursor.getInt(cursor.getColumnIndex("account"));

			DBHistoryData findData = new DBHistoryData();
//			findData.bookName = name;
//			findData.isbn = isbn;

			persons.add(findData);
		}
		cursor.close();
		return persons;
	}
	
	public Cursor getCursorScrollData(int offset, int maxSize) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		return db.rawQuery("select id as _id,name,phone,account from video_history order by id asc limit ?,?",
				new String[]{String.valueOf(offset), String.valueOf(maxSize)});
		
	}
	
	/**
	 * 获取总记录数
	 * @return
	 */
	public long getCount() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from video_history", null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}
}
