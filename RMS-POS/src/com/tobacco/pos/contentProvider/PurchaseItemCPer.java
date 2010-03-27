package com.tobacco.pos.contentProvider;

import static android.provider.BaseColumns._ID;
 
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

public class PurchaseItemCPer extends ContentProvider {

	  	private SQLiteDatabase     sqlDB;
	    private DatabaseHelper    dbHelper;
	    private static final String  DATABASE_NAME     = "AllTables.db";
	    private static final int        DATABASE_VERSION         = 1;
	    private static final String TABLE_NAME   = "PurchaseItem";
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
				db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
				return db;
			}

			private void createtable(SQLiteDatabase db) {
				try {
					db.query(TABLE_NAME, null, null, null, null, null, null);
				} catch (Exception e) {
				db.execSQL("create table if not exists " + TABLE_NAME + " ( " + _ID
						+ " integer primary key autoincrement,"
						+ " purchaseBillId integer references PurchaseBill ( " + _ID + " ), "
						+ "pGoodsNum integer not null ,"
					    + "pPriceId integer references GoodsPrice ( " + _ID + " ))");
				initPurchaseItem(db);
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
			private boolean initPurchaseItem(SQLiteDatabase db) {
				
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
	    	dbHelper = new DatabaseHelper(ct);
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
	    	dbHelper = new DatabaseHelper(ct);
	    	 SQLiteDatabase db = dbHelper.getWritableDatabase();
	         int count;
	       
	         count = db.update(TABLE_NAME, contentvalues, s, as);
	     
	         ct.getContentResolver().notifyChange(uri, null);
	         return count;
	       
	    }

	    public boolean addPItem(int pBillId, int priceId, int count){
	    	Cursor c = this.query(AllTables.PurchaseItem.CONTENT_URI, null, " purchaseBillId = ? and pPriceId = ? ", new String[]{pBillId+"", priceId+""}, null);
	    	if(c.getCount() == 0){
	    	
	    		ContentValues value = new ContentValues();
	    		value.put("purchaseBillId", pBillId);
	    		value.put("pGoodsNum", count);
	    		value.put("pPriceId", priceId);
	    	
	    		Uri uri = this.insert(AllTables.PurchaseItem.CONTENT_URI, value);
	    		if(uri!=null)
	    			return true;
	    		return false;
	    	}
	    	else{
	    		c.moveToFirst();
	    		int originalCount = c.getInt(2);
	    		ContentValues value = new ContentValues();
	    		value.put("purchaseBillId", pBillId);
	    		value.put("pGoodsNum", originalCount + count);
	    		value.put("pPriceId", priceId);
	    		int updateCount = this.update(AllTables.PurchaseItem.CONTENT_URI, value, " purchaseBillId = ? and pPriceId = ? ", new String[]{pBillId+"", priceId+""});
	    		if(updateCount>0) 
	    			return true;
	    		 	    		
	    		else 
	    			return false;
	    		 
	    			
	    	}
	    }

}
