package griffib.shopdroid.lists;

import griffib.shopdroid.R;
import griffib.shopdroid.SDroidDb;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class OffersList extends SDroidList {

  private static final int MENU_BACK = Menu.FIRST;
  private static final int MENU_VIEW_ALL = Menu.FIRST+1;
  private static final int MENU_SHARE = Menu.FIRST+2;
  private static final int MENU_SETTINGS = Menu.FIRST+3;
  
  private SDroidDb dbHelper;
  private Cursor cMain;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.db_list);
    
    dbHelper = getDbHelper();
    boolean search;
    
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      search = extras.getBoolean(SEARCH);
      if (search)
        fillData(extras.getLong(SDroidDb.KEY_PRODUCT_ID), search);
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
