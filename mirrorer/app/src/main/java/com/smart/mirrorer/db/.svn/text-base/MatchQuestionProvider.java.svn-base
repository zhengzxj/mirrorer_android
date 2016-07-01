package com.smart.mirrorer.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MatchQuestionProvider extends ContentProvider {
	private DBOpenHelper dbOpenHelper;
	private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int SEARCHQUESTIONS = 1;
	private static final int SEARCHQUESTION = 2;

	private static final String TABLE_NAME = "question_match";

	static {
		matcher.addURI("com.smart.mirrorer.providers.matchquestionprovider", TABLE_NAME, SEARCHQUESTIONS);
		matcher.addURI("com.smart.mirrorer.providers.matchquestionprovider", TABLE_NAME+"/#", SEARCHQUESTION);
	}

	@Override
	public boolean onCreate() {
		dbOpenHelper = DBOpenHelper.getInstance(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		switch (matcher.match(uri)) {
		case SEARCHQUESTIONS:
			return db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		case SEARCHQUESTION:
			long id = ContentUris.parseId(uri);
			
			String where = "id="+id;
			if(selection!=null && !"".equals(selection.trim())) {
				where = where + " and "+selection;
			}
			return db.query(TABLE_NAME, projection, where, selectionArgs, null, null, sortOrder);
		default:
			throw new IllegalArgumentException("Unknow Uri:"+uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		switch(matcher.match(uri)) {
		case SEARCHQUESTIONS:
			return "vnd.android.cursor.dir/"+TABLE_NAME;
		case SEARCHQUESTION:
			return "vnd.android.cursor.item/"+TABLE_NAME;
		default:
			throw new IllegalArgumentException("Unknow Uri:"+uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		switch(matcher.match(uri)) {
		case SEARCHQUESTIONS:
			long rowid = db.insert(TABLE_NAME, "id", values);//新增记录的id
			getContext().getContentResolver().notifyChange(uri, null);//发送数据变化通知֪
			return ContentUris.withAppendedId(uri, rowid);
		default :
			throw new IllegalArgumentException("Unknow Uri:"+uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int num = 0;
		switch(matcher.match(uri)) {
		case SEARCHQUESTIONS:
			num = db.delete(TABLE_NAME, selection, selectionArgs);
			break;
		case SEARCHQUESTION:
			long id = ContentUris.parseId(uri);
			String where = "id="+id;
			if(selection!=null && !"".equals(selection.trim())) {
				where = where + " and "+selection;
			}
			num = db.delete(TABLE_NAME, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknow Uri:"+uri);
		}
		return num;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int num = 0;
		switch(matcher.match(uri)) {
		case SEARCHQUESTIONS:
			num = db.update(TABLE_NAME, values, selection, selectionArgs);
			break;
		case SEARCHQUESTION:
			long id = ContentUris.parseId(uri);
			String where = "id="+id;
			if(selection!=null && !"".equals(selection.trim())) {
				where = where + " and "+selection;
			}
			num = db.update(TABLE_NAME, values, where, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknow Uri:"+uri);
		}
		return num;
	}

}
