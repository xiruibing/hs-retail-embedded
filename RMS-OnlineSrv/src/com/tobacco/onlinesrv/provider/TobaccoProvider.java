package com.tobacco.onlinesrv.provider;

import com.tobacco.onlinesrv.entities.Tobacco;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TobaccoProvider extends ContentProvider {

	public static final String CONTENT_URI = "com.tobacco.onlinesrv.provider.tobaccoProvider";

	private static final String TAG = "TobaccoProvider";
	private static final String DATABASE_NAME = "RMS_OnlineSrv.db";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_TABLE_NAME = "tobacco";

	private static final String DATABASE_CREATE = "create table if not exists "
			+ DATABASE_TABLE_NAME + "(" + Tobacco.KEY_ID
			+ " integer primary key autoincrement, " + Tobacco.KEY_NAME
			+ " varchar(20), " + Tobacco.KEY_PACKET_PRICE + " float ,"
			+ Tobacco.KEY_ITEM_PRICE + " float )";
	private DatabaseHelper tobaccoHelper = null;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		Log.i(TAG, "step into insert");
		SQLiteDatabase sqlDB = tobaccoHelper.getWritableDatabase();
		long rowId = sqlDB.insert(DATABASE_TABLE_NAME, "", values);
		Log.i("rowId is", rowId + "");
		if (rowId > 0) {
			Uri rowUri = ContentUris.appendId(Tobacco.CONTENT_URI.buildUpon(),
					rowId).build();
			getContext().getContentResolver().notifyChange(rowUri, null);
			// Cursor c = query(PreTobacco.CONTENT_URI, null, null, null, null);
			// Log.i("After insert size", c.getCount()+"");
			return rowUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Log.i(TAG, "step into onCreate");
		if(tobaccoHelper == null)
			tobaccoHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortTobacco) {
		// TODO Auto-generated method stub
		if(tobaccoHelper == null)
			tobaccoHelper = new DatabaseHelper(getContext());
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		SQLiteDatabase db = tobaccoHelper.getWritableDatabase();
		qb.setTables(DATABASE_TABLE_NAME);
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortTobacco);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		private SQLiteDatabase db = null;
		private Context ctx = null;

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			ctx = context;
			exist(DATABASE_NAME);
			db = openDatabase(DATABASE_NAME);
			createtable(db);
			// TODO Auto-generated constructor stub
		}

		private void createtable(SQLiteDatabase db) {
//			db.execSQL("drop table tobacco");
			db.execSQL(DATABASE_CREATE);
			Log.i(TAG, "Table created...");
//			initData(db);
			// db.execSQL("INSERT INTO " + DATABASE_TABLE_NAME
			// + " (id, brandcode,brandcount)"
			// + " VALUES ('2','def','1234')");
			// Log.i(TAG, "Init Data inserted...");
			// TODO Auto-generated method stub

		}
		private void initData(SQLiteDatabase db)
		{
			db.execSQL("INSERT INTO " + DATABASE_TABLE_NAME + " (name,packetprice,itemprice)"
					+ " VALUES ('中华','45','500')");
			db.execSQL("INSERT INTO " + DATABASE_TABLE_NAME + " (name,packetprice,itemprice)"
					+ " VALUES ('小熊猫','20','220')");
			db.execSQL("INSERT INTO " + DATABASE_TABLE_NAME + " (name,packetprice,itemprice)"
					+ " VALUES ('大熊猫','30','350')");
			db.execSQL("INSERT INTO " + DATABASE_TABLE_NAME + " (name,packetprice,itemprice)"
					+ " VALUES ('红双喜','5','40')");
			db.execSQL("INSERT INTO " + DATABASE_TABLE_NAME + " (name,packetprice,itemprice)"
					+ " VALUES ('七匹狼','7','80')");
			db.execSQL("INSERT INTO " + DATABASE_TABLE_NAME + " (name,packetprice,itemprice)"
					+ " VALUES ('玉溪','25','320')");
			db.execSQL("INSERT INTO " + DATABASE_TABLE_NAME + " (name,packetprice,itemprice)"
					+ " VALUES ('石狮','8','90')");
		}

		private SQLiteDatabase openDatabase(String databaseName) {
			db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
			return db;
		}

		private boolean exist(String databaseName) {
			// TODO Auto-generated method stub
			Log.i(TAG, "called fun : exist()");
			boolean flag = false;
			try {
				db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
				Log.i(TAG, "database/" + databaseName + " exist");
				flag = true;
			} finally {
				if (db != null)
					db.close();
				db = null;
			}
			return flag;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}
	}

}
