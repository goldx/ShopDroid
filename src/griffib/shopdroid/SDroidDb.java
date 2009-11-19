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
  
  private static final String DATABASE_CREATE =
    "create table notes (_id integer primary key autoincrement, "
            + "title text not null, body text not null);";
  
  private static final String DATABASE_NAME = "SDroid";
  private static final String DATABASE_TABLE = "test_table";
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

        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS notes");
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
