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
  private static int offerNum = 0;
  
  private static final int NEW_OFFER = 0;
  private static final int EDIT_OFFER = 1;
  
  private SDroidDb dbHelper;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.db_list);
    
    // Get the local database
    dbHelper = new SDroidDb(this);
    dbHelper.open();
    
    // Populate the list
    fillData();
    
    // Setup a context menu
    registerForContextMenu(getListView());
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean result = super.onCreateOptionsMenu(menu);
    menu.add(0, MENU_ADD, 0, R.string.menu_add);
    return result;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case MENU_ADD:
      createNewOffer();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case DELETE_ID:
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      dbHelper.deleteOffer(info.id);
      fillData();
      return true;
    }
    return super.onContextItemSelected(item);
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Bundle extras = data.getExtras();
    
    switch(requestCode) {
    case NEW_OFFER:
      String product = extras.getString("product_name");
      String namesp1 = extras.getString("namesp1");
      String val1 = extras.getString("val1");
      String namesp2 = extras.getString("namesp2");
      String val2 = extras.getString("val2");
      dbHelper.createOffer(product);
      //dbHelper.addTag(namesp, pred, val,)
      fillData();
      break;
    }
  }

  private void createNewOffer() {
    Intent i = new Intent(this, EditOffer.class);
    startActivityForResult(i, NEW_OFFER);
  }

  private void fillData() {
    Cursor c = dbHelper.fetchAllOffers();
    startManagingCursor(c);
    
    String[] from = new String[] { SDroidDb.KEY_OFFERS_PRODUCT_NAME };
    int[] to = new int[] { R.id.row_text };
    
    SimpleCursorAdapter offers =
      new SimpleCursorAdapter(this, R.layout.db_row, c, from, to);
    setListAdapter(offers);
  }
}
