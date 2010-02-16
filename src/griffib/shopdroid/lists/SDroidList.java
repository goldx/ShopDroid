package griffib.shopdroid.lists;

import griffib.shopdroid.SDroidDb;
import griffib.shopdroid.SDroidSettings;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

public class SDroidList extends ListActivity {
  
  public static final String SEARCH = "s";
  
  private SDroidDb dbHelper;
  
  private static final String DB_LOCAL_OFFERS = "Local_Offers";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
 // Get the local database
    dbHelper = new SDroidDb(this);
    dbHelper.open(DB_LOCAL_OFFERS);
  }
  
  public SDroidDb getDbHelper() {
    return dbHelper;
  }
  
  protected void openPrefs() {
    Intent i = new Intent(this, SDroidSettings.class);
    startActivity(i);
  }
  
  protected void sync() {
    
  }
  
}
