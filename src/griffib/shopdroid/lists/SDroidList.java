package griffib.shopdroid.lists;

import java.io.IOException;

import griffib.shopdroid.SDroidDb;
import griffib.shopdroid.SDroidSettings;
import griffib.shopdroid.comms.SDroidClient;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

public class SDroidList extends ListActivity {
  
  public static final String SEARCH = "s";
  public static final String EDIT = "e";
  

  protected static final int NEW_OFFER = 0;
  protected static final int EDIT_OFFER = 1;
  protected static final int NEW_PRODUCT = 2;
  protected static final int DIALOG_SQL_WARNING = 0;
  
  private SDroidDb dbHelper;
  private SDroidClient client;
  
  private static final String DB_LOCAL_OFFERS = "Local_Offers";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    // Get the local database
    dbHelper = new SDroidDb(this);
    dbHelper.open(DB_LOCAL_OFFERS);
    
    // Start SDroidClient
    client = new SDroidClient(dbHelper, this);
  }
  
  public SDroidDb getDbHelper() {
    return dbHelper;
  }
  
  public SDroidClient getClient() {
    return client;
  }
  
  protected void openPrefs() {
    Intent i = new Intent(this, SDroidSettings.class);
    startActivity(i);
  }
  
  protected void sync() {
    
  }
  
  protected void export() {
    try {
      client.exportOffers();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
}
