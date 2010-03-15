package griffib.shopdroid.comms;

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
  
/**
 * 
 * @param o The Offers object which is going to be integrated
 * @param db The SDroidDb database that the offers will be integrated into
 */
  public DbMapper (Offers o, SDroidDb db) {
    pendingOffers = o;
    this.db = db;
  }
  
  /**
   * Call this function to perform the integration
   */
  public void integrate() throws SQLException {
    for (Offer offer: pendingOffers.getOfferList()) {
      long productId = addProduct(offer.getProduct());
      if (productId==-1)
        throw new SQLException("There has been a contstraint problem");
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
  
  public class MapperHelper {
    
  }
}
