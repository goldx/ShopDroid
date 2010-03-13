package griffib.shopdroid.comms;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import griffib.shopdroid.SDroidDb;
import griffib.shopdroid.comms.OffersProto.Offer;
import griffib.shopdroid.comms.OffersProto.Offers;
import griffib.shopdroid.comms.OffersProto.Offer.Attribute;

/**
 * Parts of this code are inspired by Google's Protocol Buffer java tutorial
 * available at http://code.google.com/apis/protocolbuffers/docs/javatutorial.html
 * @author Ben Griffiths
 *
 */
public class SDroidClient {
  
  private final static String MSG_FILE = "sdroidmsg";

  private SDroidDb db;
  private Context ctx;

  public SDroidClient(SDroidDb db, Context ctx) {
    this.db = db;
    this.ctx = ctx;
  }
  
  public void exportOffers() throws IOException {
    Cursor offersCursor = db.fetchAllOffers();
    offersCursor.moveToFirst();
    Offers.Builder offers = Offers.newBuilder();
    while (!offersCursor.isAfterLast()) {
      String summary = offersCursor.getString(offersCursor.getColumnIndex(SDroidDb.KEY_OFFER_SUM));
      long offerID = offersCursor.getLong(offersCursor.getColumnIndex(SDroidDb.KEY_ID));
      String product = db.getProductNameFromOffer(offerID);
      offers.addOffer(buildOffer(product, summary));
      offersCursor.moveToNext();
    }
    
    // We've finished with the cursor, close it
    offersCursor.close();
    
    // Write our message to file
    try {
      FileOutputStream out = ctx.openFileOutput(MSG_FILE, 0);
      offers.build().writeTo(out);
      out.close();
    } catch (FileNotFoundException e) {
      // It just hasn't been made yet... This shouldn't happen! openFileOutput
      // makes the file if it isn't there... but if it does
      Log.e("FileOutput", "File not found?!");
      Log.e("FileOutput", e.getMessage());
    } 
  }
  
  private Offer buildOffer(String product, String summary) {
    // Build the offer from given parameters
    Offer.Builder offer = Offer.newBuilder();
    offer.setProduct(product);
    offer.setOfferSum(summary);
    
    // Find the offers attributes and add them to the offer object
    Cursor attrCursor = db.fetchAttributes(summary);
    attrCursor.moveToFirst();
    while (!attrCursor.isAfterLast()) {
      String pred = attrCursor.getString(attrCursor.getColumnIndex(SDroidDb.KEY_ATTRIBUTES_PREDICATE));
      String val = attrCursor.getString(attrCursor.getColumnIndex(SDroidDb.KEY_ATTRIBUTES_VALUE));
      offer.addAttribute(buildAttribute(pred, val));
    }
    
    // We've finished with the cursor, close it
    attrCursor.close();
    
    return offer.build();
  }
  
  private Attribute buildAttribute(String pred, String val) {
    // Build an attribute from given parameters
    Attribute.Builder attribute = Attribute.newBuilder();
    attribute.setPredicate(pred);
    attribute.setValue(val);
    return attribute.build();
  }
  

}
