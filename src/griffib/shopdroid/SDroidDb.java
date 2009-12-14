package griffib.shopdroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 
 * Provides database functions for ShopDroid 
 * 
 * @author Ben Griffiths
 *
 */
public class SDroidDb {
  
  private static final String TAG = "NotesDbAdapter";
  private DatabaseHelper mDbHelper;
  private SQLiteDatabase mDb;

  private final Context mtx;
  
  // SQL for creating databases
  private static final String CREATE_OFFERS_TABLE = "create table Offers (" +
                                    "_id integer primary key autoincrement," +
                                    "product_name text not null," +
                                    "tag_id integer foreign key references Tag(_id)" +
                                    ")";
  
  private static final String CREATE_TAGS_TABLE = "create table Tags (" +
  		                               "_id integer primary key autoincrement," +
  		                               "namespace text not null" +
  		                               "predicate text not null" +
  		                               "value text not null" +
  		                               "offer_id foreign key references Offer(_id)" +
  		                               ")";
  
  
  private static final String DATABASE_NAME = "SDroid";
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

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

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
   * Constructor 
   * 
   * @param mtx
   */
  public SDroidDb(Context mtx) {
    this.mtx = mtx;
  }
}
