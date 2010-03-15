package griffib.shopdroid.comms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import griffib.shopdroid.SDroidDb;
import griffib.shopdroid.comms.OffersProto.Offer;
import griffib.shopdroid.comms.OffersProto.Offers;
import griffib.shopdroid.comms.OffersProto.Offer.Attribute;




/**
 * Provides mapping for ShopDroid databases
 * 
 * @author Ben Griffiths
 *
 */
public class DbMapper {
  
  private Offers pendingOffers;
  private SDroidDb db;
  private Map<String, String> m = new HashMap<String, String>();
  
/**
 * 
 * @param o The Offers object which is going to be integrated
 * @param db The SDroidDb database that the offers will be integrated into
 */
  public DbMapper (Offers o, SDroidDb db) {
    pendingOffers = o;
    this.db = db;
    initMap();
  }
  
  /**
   * Call this function to perform the integration
   */
  public void integrate() throws SQLException {
    // iterate through all offers
    for (Offer offer: pendingOffers.getOfferList()) {
      // check for any mappings!
      String mappedName = checkForMappings(offer.getProduct());
      
      // If there were no mappings continue to add new product
      // else add offers to existing product.
      // This assumes mapped items exist in db already!
      // TODO mapping assumption may cause crashes
      long productId;
      if (mappedName==null) {
        productId = addProduct(offer.getProduct());
        if (productId == -1)
          throw new SQLException("There has been a contstraint problem");
      }
      else {
        productId = db.findProduct(mappedName);
      }
      long offerId = addOffer(productId, offer.getOfferSum());
      for (Attribute attr: offer.getAttributeList()) {
        addAttr(attr.getPredicate(), attr.getValue(), offerId);
      }
    }
  }
  
  private long addProduct(String name) {
    try { 
      return db.createProduct(name); 
    } catch (SQLiteConstraintException e) {
      // The product must already exist!
      long id = db.findProduct(name);
      if (id!=-1) 
        return id;
      else
        return -1;
    }
  }
  
  private long addOffer(long productId, String summary) {
    return db.createOffer(productId, summary);
  }
  
  private long addAttr(String pred, String val, long id) {
    return db.addAttribute(pred, val, id);
  }
  
  private void initMap() {
    String[] keys = new String[] { "usb stick", "nut tool", "sneakers" };
    String[] values = new String[] { "usb pen", "wiggle stick", "trainers" };
    
    for (int k=0,v=0; k<keys.length && v<values.length; k++, v++) {
      m.put(keys[k], values[v]);
    }
  }
  
  private String checkForMappings(String name) {
   
    Cursor c = db.fetchAllProducts();
    
    if (m.containsKey(name))
     return m.get(name);
    else if (LevenshteinCalc.validLevenshteinDistance(name, c)) {
     return LevenshteinCalc.getMatch(); 
    }
   
   return null;
  }
  
  protected static class LevenshteinCalc {
    
    private static final int MAX_DIS = 2;
    private static String match;
    
    protected static boolean validLevenshteinDistance(String name, Cursor c) {
      c.moveToFirst();
      int columnIndex = c.getColumnIndex(SDroidDb.KEY_PRODUCT_NAME);
      String productName;
      while (!c.isAfterLast()) {
        productName = c.getString(columnIndex);
        int dis = StringUtils.getLevenshteinDistance(productName, name);
        if (dis<=MAX_DIS) {
          match=productName;
          return true;
        }
        c.moveToNext();
      }
      return false;
    }
    
    protected static String getMatch() {
      return match;
    }
  }
  
}
