package com.smart.mirrorer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.smart.mirrorer.bean.PushQuestionBean;
import com.smart.mirrorer.util.mUtil.L;
import com.videorecorder.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MatchQuestionProviderService {

	private final String TABLE_NAME = "question_match";
	//获得数据库操作实例
	private DBOpenHelper dbOpenHelper;
	//引入上下文对象
	public MatchQuestionProviderService(Context context) {
		dbOpenHelper = DBOpenHelper.getInstance(context);
	}
	
	public List<PushQuestionBean> getAllDate() {
		List<PushQuestionBean> persons = new ArrayList<PushQuestionBean>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" order by time desc", null);
		while(cursor.moveToNext()) {
			String qid = cursor.getString(cursor.getColumnIndex("qid"));
			String sourceId = cursor.getString(cursor.getColumnIndex("source"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String headurl = cursor.getString(cursor.getColumnIndex("headurl"));//headurl,qcount
			int qcount = cursor.getInt(cursor.getColumnIndex("qcount"));
			String name = cursor.getString(cursor.getColumnIndex("name"));



			PushQuestionBean findData = new PushQuestionBean();
			findData.setName(name);
			findData.setHeadImgUrl(headurl);
			findData.setqCount(qcount);
			findData.setQid(qid);
			findData.setSource(sourceId);
			findData.setQuestion(content);
			findData.setTs(Long.parseLong(time));

			persons.add(findData);
		}
		cursor.close();
		return persons;
	}

	/**
	 * 插入
	 * @param sourceData
	 * @param keyword
     */
	public void saveOrUpdateHistory(PushQuestionBean sourceData,String keyword) {
		PushQuestionBean dbData = find(keyword);
		if (dbData == null) {
			saveSource(sourceData);
		} else {
//			dbData.time = System.currentTimeMillis()+"";
			update(sourceData);
		}
	}

	/**
	 *更新数据库
	 * @param data
	 */
	private void update(PushQuestionBean data) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("time", data.getTs()+"");
		String[] args = {data.getQid()};
		db.update(TABLE_NAME, cv, "qid=?",args);
		Log.i("lzm","更新question_match表的数据是:"+data);
//		db.execSQL("update "+TABLE_NAME+" set time=? where =?",
//				new Object[]{data.getTime(), data.getQid()});
	}

	/**
	 * 保存数据到数据库
	 * @param data 传入的数据对象
	 */
	//start varchar(30),name varchar(50))";
	private void saveSource(PushQuestionBean data) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Log.i("lzm","存入question_match表的数据是:"+data);
		db.execSQL("insert into "+TABLE_NAME+"(qid,source,content,time,headurl,qcount,start,name) values(?,?,?,?,?,?,?,?)",
				new Object[]{data.getQid(),data.getSource(), data.getQuestion(), data.getTs()+"",data.getHeadImgUrl(),data.getqCount()+"",data.getStar(),data.getName()});
	}

	/**
	 * 查询数据库中的的指定数据
	 * @return
	 */
	public PushQuestionBean find(String qid) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where qid=?", new String[]{qid});
		if(cursor.moveToFirst()) {
			String q_id = cursor.getString(cursor.getColumnIndex("qid"));
			String sourceId = cursor.getString(cursor.getColumnIndex("source"));
			String content = cursor.getString(cursor.getColumnIndex("content"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			cursor.close();

			PushQuestionBean findData = new PushQuestionBean();
			findData.setQid(q_id);
			findData.setSource(sourceId);
			findData.setQuestion(content);
			findData.setTs(Long.parseLong(time));

			return findData;
		}
		return null;
	}

	/**
	 * 删除问题
	 * @param key
     */
	public void deleteHistorysWithQid(String key) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		String sql = "delete from " + TABLE_NAME + " where qid=?";
		L.i("取消问题 questionData sql = "+sql);
		db.execSQL(sql, new Object[]{key});
	}

	public void clearHistorys() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from "+TABLE_NAME);
	}

	/**
	 * 获取总记录数
	 * @return
	 */
	public long getCount() {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "+TABLE_NAME, null);
		cursor.moveToFirst();
		long count = cursor.getLong(0);
		cursor.close();
		return count;
	}
}
