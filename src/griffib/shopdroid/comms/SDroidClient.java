package griffib.shopdroid.comms;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
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
  
  private final static boolean EDGE = true;
  private final static String MSG_FILE = "sdroidmsg";
  private final static String OCTECT_STEAM = "application/octect-steam";

  private SDroidDb db;
  private Context ctx;

  public SDroidClient(SDroidDb db, Context ctx) {
    this.db = db;
    this.ctx = ctx;
  }
  
  public void importOffers() {
    try {
      Offers offers;
      if (!EDGE) {
        offers = Offers.parseFrom(ctx.openFileInput(MSG_FILE));
      } else {
        HttpClient client = new DefaultHttpClient();
        String url = "http://192.168.1.69:8888/sdroidmarshal";
        HttpGet getRequest = new HttpGet(url);
        
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String proto = client.execute(getRequest, responseHandler);
        offers = null;
      }
      DbMapper map = new DbMapper(offers, db);
      map.integrate();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
      if (!EDGE) {
        FileOutputStream out = ctx.openFileOutput(MSG_FILE, 0);
        offers.build().writeTo(out);
        out.close();
      } else {
        
        // Set up the HttpClient
        HttpClient client = new DefaultHttpClient();
        String url = "http://192.168.2.103:8888/sdroidmarshal";
        HttpPost postRequest = new HttpPost(url);
        
        // Create the content for the message
        AbstractContentBody[] parts = new AbstractContentBody[1];
        InputStream ins = new ByteArrayInputStream(offers.build().toByteArray());
        parts[0] = new InputStreamBody(ins, OCTECT_STEAM, "sdroidmsg");
        //parts[1] = new StringBody("uid");
        
        // Add the content to the message
        MultipartEntity requestContent = new MultipartEntity();
        requestContent.addPart("message", parts[0]);
        //requestContent.addPart("ID", parts[1]);
        
        // Send!
        postRequest.setEntity(requestContent);
        client.execute(postRequest);
        ins.close();
        
        try {
          ResponseHandler<String> responseHandler = new BasicResponseHandler();
          String responseBody = client.execute(postRequest, responseHandler);
          
        } catch (Throwable t) {
          
        }
      }
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
      attrCursor.moveToNext();
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
