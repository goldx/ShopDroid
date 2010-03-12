package griffib.shopdroid.comms;

import android.database.Cursor;
import griffib.shopdroid.SDroidDb;
import griffib.shopdroid.comms.OffersProto.Offer;

public class DbOutWriter {

  private SDroidDb db;
  
  public DbOutWriter(SDroidDb db) {
    this.db = db;
  }
  
  public void export() {
    Cursor c = db.fetchAllOffers();
  }
}
