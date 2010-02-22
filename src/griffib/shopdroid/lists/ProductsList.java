package griffib.shopdroid.lists;

import griffib.shopdroid.EditOffer;
import griffib.shopdroid.EditProduct;
import griffib.shopdroid.R;
import griffib.shopdroid.SDroidDb;
import griffib.shopdroid.comms.SDroidServer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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
public class ProductsList extends SDroidList {
  
  
  public static final String KEY_ATTR_NUM = "attr_num";
  
  // Menu items
  private static final int MENU_ADD = Menu.FIRST;
  private static final int DELETE_ID = Menu.FIRST + 1;
  private static final int EDIT_ID = Menu.FIRST + 2;
  private static final int SYNC_ID = Menu.FIRST + 3;
  private static final int DISPLAY_ID = Menu.FIRST + 4;
  private static final int SETTINGS_ID = Menu.FIRST + 5;
  private static final int MENU_ADD_PRODUCT = Menu.FIRST + 6;
  private static final int FIND_OFFERS = Menu.FIRST + 7;
  private static final int EXPORT_ID = Menu.FIRST +8;
  
  
  private SDroidDb dbHelper;
  private Cursor cMain;
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.db_list);
    
    // Get the local database
    
    dbHelper = getDbHelper();
    
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
    menu.add(0, MENU_ADD_PRODUCT, 0, R.string.menu_add_product)
        .setIcon(android.R.drawable.ic_menu_add);
    menu.add(0, SYNC_ID, 0, R.string.menu_sync)
        .setIcon(android.R.drawable.ic_menu_share);
    menu.add(Menu.NONE, EXPORT_ID, Menu.NONE, R.string.menu_export)
        .setIcon(android.R.drawable.ic_menu_save);
    menu.add(0, DISPLAY_ID, 0, R.string.menu_change_list)
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
    case MENU_ADD_PRODUCT:
      createNewProduct();
    case SYNC_ID:
      sync();
      return true;
    case EXPORT_ID:
      export();
      return true;
    case DISPLAY_ID:
      Intent i = new Intent(this, OffersList.class);
      i.putExtra(SEARCH, false);
      startActivity(i);
      return true;
    case SETTINGS_ID:
      openPrefs();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  /*
   * END of Options menu
   */
  
  /*
   * Context Menu
   */

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, MENU_ADD, 0, R.string.menu_add);
    menu.add(0, FIND_OFFERS, 0, R.string.menu_find);
    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    menu.add(0, EDIT_ID, 0, R.string.menu_edit);
  }

  /**
   * Handles the context menu requests
   */
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    Intent i;
    
    switch (item.getItemId()) {
    
    case MENU_ADD:
      i = new Intent(this, EditOffer.class);
      i.putExtra(SDroidDb.KEY_ID, info.id);
      startActivityForResult(i, NEW_OFFER);
      return true;
      
      
    case FIND_OFFERS:
      i = new Intent(this, OffersList.class);
      i.putExtra(SDroidDb.KEY_PRODUCT_ID, info.id);
      i.putExtra(SEARCH, true);
      startActivity(i);
      return true;
    
    // Deleting an item from the Db
    case DELETE_ID:
      dbHelper.deleteOffer(info.id);
      fillData();
      return true;
      
    // Editing an item from the Db
    case EDIT_ID:
      return true;
    }
    return super.onContextItemSelected(item);
  }
  
  /*
   * END of context menu
   */
  
  /** Edit item on click */
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Cursor c = cMain;
    c.moveToPosition(position);
    Intent i = new Intent(this, EditOffer.class);
    i.putExtra(SDroidDb.KEY_ID, id);
    i.putExtra(SDroidDb.KEY_PRODUCTS_PRODUCT_NAME, c.getString(
        c.getColumnIndexOrThrow(SDroidDb.KEY_PRODUCTS_PRODUCT_NAME)));
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
        Long productId = extras.getLong(SDroidDb.KEY_ID);
        String summary = extras.getString(SDroidDb.KEY_OFFER_SUM);
        
        String[] tagPreds = extras.getStringArray(SDroidDb.KEY_ATTRIBUTES_PREDICATE);
        String[] tagVals = extras.getStringArray(SDroidDb.KEY_ATTRIBUTES_VALUE);
        
        
        rowId = dbHelper.createOffer(productId, summary);
       
        for (int i=0; i<tagPreds.length; i++) {
          dbHelper.addAttribute(tagPreds[i], tagVals[i], rowId);
        }
        
        fillData();
        break;
      case EDIT_OFFER:
        rowId = extras.getLong(SDroidDb.KEY_OFFER_ID);
        if (rowId != null) {
          String newProdName = extras
              .getString(SDroidDb.KEY_PRODUCTS_PRODUCT_NAME);
          dbHelper.updateOffer(newProdName, rowId);
        }
        fillData();
        break;
      case NEW_PRODUCT:
        String productName = extras.getString(SDroidDb.KEY_PRODUCTS_PRODUCT_NAME);
        try {
        dbHelper.createProduct(productName);
        } catch (SQLiteConstraintException e) {
          showDialog(DIALOG_SQL_WARNING);
        }
        break;
      }
    }
  }
  
  /*
   * Create dialogs
   */
  protected Dialog onCreateDialog(int id) {
    AlertDialog msgSentDialog;
    switch(id) {
      case DIALOG_SQL_WARNING:
         AlertDialog.Builder buildMsgSent = new AlertDialog.Builder(this);
         buildMsgSent.setMessage(R.string.sql_warning)
                     .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                      
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();                        
                      }
                    });
         msgSentDialog = buildMsgSent.create();
         
        break;
      default:
        msgSentDialog = null;
    }
    return msgSentDialog;
  }
  
  
  ///// Private Methods /////

  private void createNewOffer() {
    Intent i = new Intent(this, EditOffer.class);
    startActivityForResult(i, NEW_OFFER);
  }
  
  private void createNewProduct() {
    Intent i = new Intent(this, EditProduct.class);
    startActivityForResult(i, NEW_PRODUCT);
  }

  private void fillData() {
          
    cMain = dbHelper.fetchAllProducts();
    String[] from = new String[] { SDroidDb.KEY_PRODUCTS_PRODUCT_NAME };
        
    startManagingCursor(cMain);
        
    int[] to = new int[] { R.id.row_text };
    
    SimpleCursorAdapter localOffers =
      new SimpleCursorAdapter(this, R.layout.db_row, cMain, from, to);
    setListAdapter(localOffers);
  }
}
