package griffib.shopdroid.comms;

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
  public void integrate() {
    for (Offer offer: pendingOffers.getOfferList()) {
      long productId = addProduct(offer.getProduct());
      long offerId = addOffer(productId, offer.getOfferSum());
      for (Attribute attr: offer.getAttributeList()) {
        addAttr(attr.getPredicate(), attr.getValue(), offerId);
      }
    }
  }
  
  private long addProduct(String name) {
    return db.createProduct(name);
  }
  
  private long addOffer(long productId, String summary) {
    return db.createOffer(productId, summary);
  }
  
  private long addAttr(String pred, String val, long id) {
    return db.addAttribute(pred, val, id);
  }
}
