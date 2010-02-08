package griffib.shopdroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 
 * Provides database functions for ShopDroid.
 * Some code from this class has been influenced by code from Google's
 * Android Notepad tutorials available at
 * http://developer.android.com/guide/tutorials/notepad/index.html
 * 
 * @author Ben Griffiths
 *
 */
public class SDroidDb {
  
  public static final String KEY_OFFERS_ID = "_id";
  public static final String KEY_OFFERS_PRODUCT_NAME = "product_name";
  public static final String KEY_TAG_FOREIGN = "offer_id";
  public static final String KEY_TAG_ID = "_id";
  public static final String KEY_TAGS_PREDICATE = "predicate";
  public static final String KEY_TAGS_VALUE = "value";
  
  public static final String KEY_TAGS_ARRAY = "tags_array";
  
  
  private static final String TAG = "NotesDbAdapter";
  private DatabaseHelper mDbHelper;
  private SQLiteDatabase mDb;

  private final Context mCtx;
  
  // SQL for creating tables
  // Changes
  private static final String CREATE_OFFERS_TABLE = "create table Offers (" +
                                    "_id integer primary key autoincrement," +
                                    "product_name text not null" +
                                    ")";
  
  private static final String CREATE_TAGS_TABLE = "create table Tags (" +
                       "_id integer primary key autoincrement," +
                       "predicate text," +
                       "value text," +
                       "offer_id integer" +
                       ")";

  
  private static final String OFFERS_TABLE = "Offers";
  private static final String TAGS_TABLE = "Tags";
  private static final int DATABASE_VERSION = 2;
  
  /**
   * Database helper class
   * 
   * @author Ben Griffiths
   *
   */
  private static class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context, String dbName) {
      super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

      // Create tables
      db.execSQL(CREATE_OFFERS_TABLE);
      db.execSQL(CREATE_TAGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
              + newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS " + OFFERS_TABLE);
      db.execSQL("DROP TABLE IF EXISTS " + TAGS_TABLE);
      onCreate(db);
    }
  }
  
  /**
   * Constructor - takes the context to allow the database to be
   * opened/created
   * 
   * @param ctx the Context within which to work
   */
  public SDroidDb(Context ctx) {
    this.mCtx = ctx;
  }
  
  /**
   * Opens the database. If it cannot be opened, it tries to create a new one.
   * If a new one cannot be created then it throws an exception.
   * @return this
   * @throws SQLException if the database cannot be opened or created
   */
  public SDroidDb open(String dbName) throws SQLException {
    mDbHelper = new DatabaseHelper(mCtx, dbName);
    mDb = mDbHelper.getWritableDatabase();
    return this;
  }
  
  public void close() {
    mDbHelper.close();
  }
  
  public long createOffer(String productName) {
    // Create a set of values to insert into the database
    // android's SQLiteDatabase.insert requires these to be in the form
    // of ContentValues
	  ContentValues offerTableValues = new ContentValues();
	  offerTableValues.put(KEY_OFFERS_PRODUCT_NAME, productName);
	  
	  return mDb.insert(OFFERS_TABLE, null, offerTableValues);
  }
  
  public long addTag(String pred, String val, long offer) {
	  ContentValues tagTableValues = new ContentValues();
	  tagTableValues.put(KEY_TAGS_PREDICATE, pred);
	  tagTableValues.put(KEY_TAGS_VALUE, val);
	  tagTableValues.put(KEY_OFFERS_ID, offer);
	  
	  return mDb.insert(TAGS_TABLE, null, tagTableValues);
  }
  
  public boolean deleteOffer(long offer_id) {
    int del_tags = mDb.delete(TAGS_TABLE, KEY_TAG_FOREIGN + "=" + offer_id, null);
    int del_offer = mDb.delete(OFFERS_TABLE, KEY_OFFERS_ID + "=" + offer_id, null);
    
    if ((del_tags > 0) && (del_offer > 0)) 
      return true;
    else
      return false;
  }
  
  public void removeTag() {
    
  }
  
  /**
   * Update an offer. Offer to be updated is specified by the rowId.
   * @param newProdName 
   * @param rowId
   * @return true if there was a successful update, else false.
   */
  public boolean updateOffer(String newProdName, long rowId) {
    ContentValues UpdatedOfferVals = new ContentValues();
    UpdatedOfferVals.put(KEY_OFFERS_PRODUCT_NAME, newProdName);
    return mDb.update(OFFERS_TABLE, UpdatedOfferVals, 
        KEY_OFFERS_ID + "=" + rowId, null) > 0;
  }
  
  public void updateTag() {
    
  }
  
  public Cursor fetchAllOffers() {
    return mDb.query(OFFERS_TABLE, 
                     new String[] {KEY_OFFERS_ID, KEY_OFFERS_PRODUCT_NAME}, 
                     null, null, null, null, null);
  }
  
  public void fetchTags() {
    
  }
}
