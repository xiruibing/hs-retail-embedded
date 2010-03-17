package com.tobacco.pos.contentProvider;

import static android.provider.BaseColumns._ID;

import java.util.Date;
 
import com.tobacco.pos.entity.AllTables;

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

public class PurchaseBillCPer extends ContentProvider {
		private SQLiteDatabase     sqlDB;
	    private DatabaseHelper    dbHelper;
	    private static final String  DATABASE_NAME     = "AllTables.db";
	    private static final int        DATABASE_VERSION         = 1;
	    private static final String TABLE_NAME   = "PurchaseBill";
	    private static Context ct = null;

	    private static class DatabaseHelper extends SQLiteOpenHelper {
	    	
	    	private SQLiteDatabase db = null;
	    

			private Context ctx = null;

			public DatabaseHelper(Context context) {
					super(context, DATABASE_NAME, null, DATABASE_VERSION);
				ctx = context;
				ct = context;
				
				db = openDatabase(DATABASE_NAME);
			
				createtable(db);
			
			
			}

			private SQLiteDatabase openDatabase(String databaseName) {
				db = ctx.openOrCreateDatabase(DATABASE_NAME, 1, null);
				return db;
			}

			private void createtable(SQLiteDatabase db) {
				try {
					db.query(TABLE_NAME, null, null, null, null, null, null);
				} catch (Exception e) {
				db.execSQL("create table if not exists " + TABLE_NAME + " ( " + _ID
						+ " integer primary key autoincrement,"
						+ " pBillNum varchar(20) unique not null, "
						+ " operId integer references UserInfo ( " + _ID + " ),"
						+ " time date not null ," 
						+ " comment varchar(255))");
				initPurchaseBill(db);
				}
			}


	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	            db.execSQL("drop table if exists " + TABLE_NAME);
	            onCreate(db);
	        }

			@Override
			public void onCreate(SQLiteDatabase db) {
				
			}
			private boolean initPurchaseBill(SQLiteDatabase db) {
				
				ContentValues value = new ContentValues();
				
				value.clear();
				value.put("pBillNum", "P1");
				value.put("operId", 1);
				Date d1 = new Date();
				value.put("time", d1.toLocaleString());
				value.put("comment", "第一张进货单");
				db.insertOrThrow(TABLE_NAME, null, value);
				
				value.clear();
				value.put("pBillNum", "P2");
				value.put("operId", 1);
				Date d2 = new Date();
				value.put("time", d2.toLocaleString());
				value.put("comment", "第二张进货单");
				db.insertOrThrow(TABLE_NAME, null, value);
				
				return true;
				
			}
	    } 

	    @Override
	    public int delete(Uri uri, String s, String[] as) {
	        return 0;
	    } 

	    @Override
	    public String getType(Uri uri) {
	        return null;
	    } 

	    @Override
	    public Uri insert(Uri uri, ContentValues contentvalues) {
	        sqlDB = dbHelper.getWritableDatabase();
	        long rowId = sqlDB.insert(TABLE_NAME, "", contentvalues);
	        if (rowId > 0) {
	            Uri rowUri = ContentUris.appendId(AllTables.Unit.CONTENT_URI.buildUpon(), rowId).build();
	            ct.getContentResolver().notifyChange(rowUri, null);
	            return rowUri;
	        }
	        throw new SQLException("Failed to insert row into " + uri);
	    } 

	    @Override
	    public boolean onCreate() {
	        dbHelper = new DatabaseHelper(getContext());
	        return (dbHelper == null) ? false : true;
	    } 

	    @Override
	    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
	
	    	dbHelper = new DatabaseHelper(ct);
	    	SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	        SQLiteDatabase db = dbHelper.getWritableDatabase();
	        qb.setTables(TABLE_NAME);
	        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
	        c.setNotificationUri(ct.getContentResolver(), uri);
	        return c;
	    } 

	    @Override
	    public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
	        return 0;
	    }
	    
	    public String getMaxPBillNum(){
	    	Cursor c = this.query(AllTables.PurchaseBill.CONTENT_URI, null, null, null, " time ");
	    	if(c.getCount()>0){
	    		c.moveToLast();
	    		String lastPBillNum = c.getString(1);
	    		return lastPBillNum.substring(1);
	    	}
	    	else
	    		return "";
	    }
	    
	    public boolean addPBill(String pBillNum, int operId, String time, String comment){
	    	ContentValues value = new ContentValues();
			
			value.clear();
			value.put("pBillNum", pBillNum);
			value.put("operId", operId);
			value.put("time", time);
			value.put("comment", comment);
		
	    	Uri uri = this.insert(AllTables.PurchaseBill.CONTENT_URI, value);
	    	if(uri == null)
	    		return false;
	    	else
	    		return true;
	    }
	    public String[] getAllPBill(){//获取所有的进货单，时间最晚的排在最前，格式是：进货单号+时间
	    	String str [];
	    	Cursor c = this.query(AllTables.PurchaseBill.CONTENT_URI, null, null, null, " time ");
			
			str = new String[c.getCount()];
			c.moveToLast();
			for(int i=0;i<c.getCount();i++){
				str[i] = c.getString(1)+":"+c.getString(3);
				c.moveToPrevious();
			}
			return str;
	    }
	    
	    public int getPBillIdByPBillNum(String pBillNum){
	    	Cursor c = this.query(AllTables.PurchaseBill.CONTENT_URI, null, " pBillNum = ? ", new String[]{pBillNum}, null);
	    	if(c.getCount()>0){
	    		c.moveToFirst();
	    		return c.getInt(0);
	    	}
	    	return -1;
	    }

}