package griffib.shopdroid.tests;

import junit.framework.Assert;
import griffib.shopdroid.SDroidDb;
import griffib.shopdroid.lists.OffersList;
import griffib.shopdroid.lists.SDroidList;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

public class OffersListTestCase1 extends
    ActivityInstrumentationTestCase2<OffersList> {
  
  private Context mCtx;
  private SDroidDb mDb;
  private ListView mList = null;
  
  public OffersListTestCase1() {
    super("griffib.shopdroid", OffersList.class);
  }
  
  @Override
  public void setUp() {
    
    mCtx = this.getInstrumentation().getContext();
    
    Intent i = new Intent(mCtx, OffersList.class);
    i.putExtra(SDroidList.SEARCH, false);
    
    OffersList activity = getActivity();
    
    mDb = activity.getDbHelper();
    mList = (ListView)activity.getListView();
  }
  
  // Test that all offers are found and displayed
  public void testAllOffers() {
    Cursor c = mDb.fetchAllOffers();
    Assert.assertEquals(c.getCount(), mList.getCount());
    c.close();
  }

}
