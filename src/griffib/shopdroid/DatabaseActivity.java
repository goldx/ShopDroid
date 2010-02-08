package griffib.shopdroid;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * A lot of influence from Google's Notepad Tutorial
 * 
 * @author Ben Griffiths
 *
 */
public class DatabaseActivity extends ListActivity {
  
  private static final int MENU_ADD = Menu.FIRST;
  private static final int DELETE_ID = Menu.FIRST + 1;
  private static final int EDIT_ID = Menu.FIRST + 2;
  private static final int SYNC_ID = Menu.FIRST + 3;
  private static final int SETTINGS_ID = Menu.FIRST + 4;
//  private static int offerNum = 0;
  
  private static final String DB_LOCAL_OFFERS = "Local_Offers";
  
  private static final int NEW_OFFER = 0;
  private static final int EDIT_OFFER = 1;
  
  private SDroidDb dbHelperLocal;
  private Cursor offerCurser;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.db_list);
    
    // Get the local database
    dbHelperLocal = new SDroidDb(this);
    dbHelperLocal.open(DB_LOCAL_OFFERS);
    
    // Populate the list
    fillData();
    
    // Setup a context menu
    registerForContextMenu(getListView());
    
    // Start server
    // The Server listens for incoming sync requests
    Intent startServer = new Intent(this, SDroidServer.class);
    this.startService(startServer);
  }

  /*
   * Options menu
   */
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean result = super.onCreateOptionsMenu(menu);
    menu.add(0, MENU_ADD, 0, R.string.menu_add)
        .setIcon(android.R.drawable.ic_menu_add);
    menu.add(0, SYNC_ID, 0, R.string.menu_sync)
        .setIcon(android.R.drawable.ic_menu_rotate);
    menu.add(0, SETTINGS_ID, 0, R.string.menu_settings)
        .setIcon(android.R.drawable.ic_menu_preferences);
    return result;
  }
  
  /**
   * Handle requests from the OptionsMenu
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case MENU_ADD:
      createNewOffer();
      return true;
    case SYNC_ID:
      syncOffers();
      return true;
    case SETTINGS_ID:
      Intent i = new Intent(this, SDroidSettings.class);
      startActivity(i);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  /*
   * END of Options menu
   */
  

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    menu.add(0, EDIT_ID, 0, R.string.menu_edit);
  }

  /**
   * Handles the context menu requests
   */
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    
    // First case - deleting an item from the Db
    case DELETE_ID:
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      dbHelperLocal.deleteOffer(info.id);
      fillData();
      return true;
      
    // Second case - editing an item from the Db
    case EDIT_ID:
      return true;
    }
    return super.onContextItemSelected(item);
  }
  
  /** Edit item on click */
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Cursor c = offerCurser;
    c.moveToPosition(position);
    Intent i = new Intent(this, EditOffer.class);
    i.putExtra(SDroidDb.KEY_OFFERS_ID, id);
    i.putExtra(SDroidDb.KEY_OFFERS_PRODUCT_NAME, c.getString(
        c.getColumnIndexOrThrow(SDroidDb.KEY_OFFERS_PRODUCT_NAME)));
    startActivityForResult(i, EDIT_OFFER);
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    
    if (resultCode != RESULT_CANCELED) {
      Bundle extras = data.getExtras();
      Long rowId;
      switch(requestCode) {
      case NEW_OFFER:
        String product = extras.getString(SDroidDb.KEY_OFFERS_PRODUCT_NAME);
        
        String[] tagPreds = extras.getStringArray(SDroidDb.KEY_TAGS_PREDICATE);
        String[] tagVals = extras.getStringArray(SDroidDb.KEY_TAGS_VALUE);
        
        rowId = dbHelperLocal.createOffer(product);
        
        for (int i=0; i<tagPreds.length; i++) {
          dbHelperLocal.addTag(tagPreds[i], tagVals[i], rowId);
        }
        
        fillData();
        break;
      case EDIT_OFFER:
        rowId = extras.getLong(SDroidDb.KEY_OFFERS_ID);
        if (rowId != null) {
          String newProdName = extras
              .getString(SDroidDb.KEY_OFFERS_PRODUCT_NAME);
          dbHelperLocal.updateOffer(newProdName, rowId);
        }
        fillData();
        break;
      }
    }
  }

  private void createNewOffer() {
    Intent i = new Intent(this, EditOffer.class);
    startActivityForResult(i, NEW_OFFER);
  }
  
  private void syncOffers() {
    
  }

  private void fillData() {
    offerCurser = dbHelperLocal.fetchAllOffers();
    startManagingCursor(offerCurser);
    
    String[] from = new String[] { SDroidDb.KEY_OFFERS_PRODUCT_NAME };
    int[] to = new int[] { R.id.row_text };
    
    SimpleCursorAdapter localOffers =
      new SimpleCursorAdapter(this, R.layout.db_row, offerCurser, from, to);
    setListAdapter(localOffers);
  }
}
