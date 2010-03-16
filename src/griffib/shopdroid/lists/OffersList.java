package griffib.shopdroid.lists;

import griffib.shopdroid.EditOffer;
import griffib.shopdroid.R;
import griffib.shopdroid.SDroidDb;
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

public class OffersList extends SDroidList {

  public static final String SUMMARY = "s";
  
  private static final int MENU_BACK = Menu.FIRST;
  private static final int MENU_VIEW_ALL = Menu.FIRST+1;
  private static final int MENU_SHARE = Menu.FIRST+2;
  private static final int MENU_SETTINGS = Menu.FIRST+3;
  private static final int MENU_EDIT = Menu.FIRST+4;
  
  private SDroidDb dbHelper;
  private Cursor cMain;
  
  private Long productID = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.db_list);
    
    dbHelper = getDbHelper();
    boolean search;
    
 // Setup a context menu
    registerForContextMenu(getListView());
    
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      search = extras.getBoolean(SEARCH);
      if (search) {
        productID = extras.getLong(SDroidDb.KEY_PRODUCT_ID);
        fillData(productID, search);
      }
      else
        fillData(null, search);
    }
  }
  
  /*
   * Options Menu
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    boolean result = super.onCreateOptionsMenu(menu);
    menu.add(0, MENU_BACK, 0, R.string.menu_back);
    menu.add(0, MENU_VIEW_ALL, 0, R.string.menu_view_all);
    menu.add(0, MENU_SHARE, 0, R.string.menu_sync);
    menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings);
    return result;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case MENU_BACK:
      finish();
      return true;
    case MENU_VIEW_ALL:
      fillData(null, false);
      return true;
    case MENU_SHARE:
      return true;
    case MENU_SETTINGS:
      openPrefs();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  /*
   * Context Menu
   */
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, MENU_EDIT, 0, R.string.menu_edit);
  }
 
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    Intent i;
    
    switch (item.getItemId()) {
      case MENU_EDIT:
        i = new Intent(this, EditOffer.class);
        i.putExtra(SDroidDb.KEY_ID, info.id);
        i.putExtra(SDroidDb.KEY_PRODUCT_ID, productID);
        i.putExtra(SDroidList.EDIT, true);
        startActivityForResult(i, EDIT_OFFER);
      break;
    }
    return super.onContextItemSelected(item);
  }
  
  

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Cursor c = cMain;
    c.moveToPosition(position);
    Intent i = new Intent(this, EditOffer.class);
    i.putExtra(SDroidDb.KEY_OFFER_ID, id);
    int columnIndex = c.getColumnIndex(SDroidDb.KEY_OFFER_SUM);
    i.putExtra(SUMMARY, c.getString(columnIndex));
    i.putExtra(SDroidDb.KEY_ATTRIBUTES_PREDICATE, dbHelper.getAttributePred(id));
    i.putExtra(SDroidDb.KEY_ATTRIBUTES_VALUE, dbHelper.getAttributeVals(id));
    i.putExtra(EDIT, true);
    
    startActivityForResult(i, EDIT_OFFER);
  }
  
  

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode!=RESULT_CANCELED) {
      Bundle extras = data.getExtras();
      
      switch(requestCode) {
      case EDIT_OFFER:
        break;
      }
    }
  }

  private void fillData(Long productID, boolean search) {
    String[] from = new String[] { SDroidDb.KEY_OFFER_SUM };;
    
    if (search)
      cMain = dbHelper.findOffers(productID);
    else
      cMain = dbHelper.fetchAllOffers();
    
    startManagingCursor(cMain);
    
    int[] to = new int[] { R.id.row_text };
    
    SimpleCursorAdapter localOffers =
      new SimpleCursorAdapter(this, R.layout.db_row, cMain, from, to);
    setListAdapter(localOffers);
  }

  

  

}
