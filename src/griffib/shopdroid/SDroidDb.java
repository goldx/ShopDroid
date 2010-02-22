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
  
  public static final String KEY_ID = "_id";
  public static final String KEY_PRODUCT_ID = "product_id";
  public static final String KEY_PRODUCTS_PRODUCT_NAME = "product_name";
  public static final String KEY_OFFER_ID = "offer_id";
  public static final String KEY_ATTRIBUTES_PREDICATE = "predicate";
  public static final String KEY_ATTRIBUTES_VALUE = "value";
  public static final String KEY_OFFER_SUM = "offer_summary";
  public static final String KEY_TAGS_ARRAY = "tags_array";
  
  
  private static final String TAG = "NotesDbAdapter";
  private DatabaseHelper mDbHelper;
  private SQLiteDatabase mDb;

  private final Context mCtx;
  
  // SQL for creating tables
  // Changes
  private static final String CREATE_OFFERS_TABLE = 
                              "create table Offers (" +
                              "_id integer primary key autoincrement," +
                              "offer_summary text" +
                              ")";
  
  private static final String CREATE_PRODUCT_OFFERS = 
                              "create table Products_Offers (" +
                              "product_id integer," +
                              "offer_id integer)";
  
  private static final String CREATE_PRODUCTS_TABLE = 
                              "create table Products (" +
  		                        "_id integer primary key autoincrement," +
  		                        "product_name text," +
  		                        "UNIQUE (product_name) )";
  
  private static final String CREATE_ATTRIBUTES_TABLE = 
                              "create table Attributes (" +
                              "_id integer primary key autoincrement," +
                              "predicate text," +
                              "value text," +
                              "offer_id integer" +
                              ")";

  
  private static final String OFFERS_TABLE = "Offers";
  private static final String ATTRIBUTES_TABLE = "Attributes";
  private static final String PRODUCTS_TABLE = "Products";
  private static final String PRODUCTS_OFFERS = "Products_Offers";
  private static final int DATABASE_VERSION = 6;
  
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
      db.execSQL(CREATE_PRODUCTS_TABLE);
      db.execSQL(CREATE_ATTRIBUTES_TABLE);
      db.execSQL(CREATE_PRODUCT_OFFERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
              + newVersion + ", which will destroy all old data");
      db.execSQL("DROP TABLE IF EXISTS " + OFFERS_TABLE);
      db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE);
      db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_OFFERS);
      db.execSQL("DROP TABLE IF EXISTS " + ATTRIBUTES_TABLE);
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
  
  public long createProduct(String productName) {
    ContentValues cvs = new ContentValues();
    cvs.put(KEY_PRODUCTS_PRODUCT_NAME, productName);
    return mDb.insertOrThrow(PRODUCTS_TABLE, null, cvs);
  }
  
  public long createOffer(long productId, String summary) {
    // Create a set of values to insert into the database
    // android's SQLiteDatabase.insert requires these to be in the form
    // of ContentValues
	  ContentValues cvsOffers = new ContentValues();
	  ContentValues cvsContect = new ContentValues();
	  
	  cvsOffers.put(KEY_OFFER_SUM, summary);
	  long offerId = mDb.insert(OFFERS_TABLE, null, cvsOffers);
	  
	  cvsContect.put(KEY_PRODUCT_ID, productId);
	  cvsContect.put(KEY_OFFER_ID, offerId);
	  mDb.insert(PRODUCTS_OFFERS, null, cvsContect);
	  
	  return offerId;
  }
  
  public long addAttribute(String pred, String val, long offer) {
	  ContentValues tagTableValues = new ContentValues();
	  tagTableValues.put(KEY_ATTRIBUTES_PREDICATE, pred);
	  tagTableValues.put(KEY_ATTRIBUTES_VALUE, val);
	  tagTableValues.put(KEY_OFFER_ID, offer);
	  
	  return mDb.insert(ATTRIBUTES_TABLE, null, tagTableValues);
  }
  
  public boolean deleteOffer(long offer_id) {
    int del_tags = mDb.delete(ATTRIBUTES_TABLE, KEY_OFFER_ID + "=" + offer_id, null);
    int del_offer = mDb.delete(OFFERS_TABLE, KEY_OFFER_ID + "=" + offer_id, null);
    
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
    UpdatedOfferVals.put(KEY_PRODUCTS_PRODUCT_NAME, newProdName);
    return mDb.update(OFFERS_TABLE, UpdatedOfferVals, 
        KEY_OFFER_ID + "=" + rowId, null) > 0;
  }
  
  public void updateTag() {
    
  }
  
  public Cursor findOffers(Long productID) {
    String search = "SELECT " + SDroidDb.OFFERS_TABLE + ".*" +
                    " FROM " + SDroidDb.OFFERS_TABLE + ", " + SDroidDb.PRODUCTS_OFFERS + 
                    " WHERE " + SDroidDb.PRODUCTS_OFFERS + "." + SDroidDb.KEY_PRODUCT_ID + "=?" +
                    " AND " + SDroidDb.PRODUCTS_OFFERS + "." + SDroidDb.KEY_OFFER_ID +
                    "=" + SDroidDb.OFFERS_TABLE + "." + SDroidDb.KEY_ID;
    String[] args = new String[] { productID.toString() };
    return mDb.rawQuery(search, args);
  }
  
  public Cursor fetchAllOffers() {
    return mDb.query(OFFERS_TABLE, 
                     new String[] {KEY_ID, KEY_OFFER_SUM}, 
                     null, null, null, null, null);
  }
  
  public Cursor fetchAllProducts() {
    return mDb.query(PRODUCTS_TABLE, new String[] {KEY_ID, KEY_PRODUCTS_PRODUCT_NAME},
        null, null, null, null, null);
  }
  
  public Cursor fetchAttributes(Long offerID) {
    String search = "SELECT ;
    String[] selectionArgs = new String[] { offerID.toString() };
    return mDb.rawQuery(search, selectionArgs);
  }
}
