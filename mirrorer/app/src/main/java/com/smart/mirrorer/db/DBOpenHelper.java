package com.smart.mirrorer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smart.mirrorer.util.mUtil.L;

public class DBOpenHelper extends SQLiteOpenHelper {

	private String CREATE_BOOK = "create table if not exists question_match(_id integer primary key autoincrement,qid varchar(50),source varchar(50),content varchar(100),time varchar(50),headurl varchar(120),qcount varchar(30),start varchar(30),name varchar(50))";
	private String CREATE_TEMP_BOOK = "alter table question_match rename to _temp_question_match";
	private String INSERT_DATA = "insert into question_match select *,'' from _temp_question_match";
	private String DROP_BOOK = "drop table _temp_question_match";
	private static DBOpenHelper dbhelper = null;

	public static synchronized DBOpenHelper getInstance(Context context) {
		if (dbhelper == null) {
			dbhelper = new DBOpenHelper(context);
		}
		return dbhelper;
	}

	private DBOpenHelper(Context context) {
		super(context, "mirrorer.db", null, 11);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		L.i("DB-onCreate");
		db.execSQL("create table if not exists video_history(_id integer primary key autoincrement,video_path varchar(100),quesion_desc varchar(100),question_id varchar(50),tutor_head varchar(100),tutor_nick varchar(50),price integer,time_format varchar(50),time integer null)");
		db.execSQL(CREATE_BOOK);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL("alter table video_history add time integer null");
//		if(oldVersion == newVersion)return;
//		switch (newVersion) {
//			case 11:
//				L.i("DB-onUpgrade oldVersion = "+oldVersion+",newVersion = "+newVersion);
//				db.execSQL("alter table question_match add headurl varchar(120)");
//				db.execSQL("alter table question_match add qcount varchar(30)");
//				db.execSQL("alter table question_match add start varchar(30)");
//				db.execSQL("alter table question_match add name varchar(50)");
//				L.i("onUpgrade oldVersion = "+oldVersion+",newVersion = "+newVersion);
//				break;
//		}

	}

}
