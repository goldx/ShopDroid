package griffib.shopdroid.tests;

import junit.framework.Assert;
import griffib.shopdroid.SDroidDb;
import griffib.shopdroid.lists.ProductsList;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

public class ProductsListTestCase extends
    ActivityInstrumentationTestCase2<ProductsList> {
  
  private SDroidDb mDb;
  private ListView mList = null;
  

  public ProductsListTestCase() {
    super("griffib.shopdroid", ProductsList.class);
  }

  protected void setUp() throws Exception {
    super.setUp();
    
    ProductsList activity = getActivity();
    mDb = activity.getDbHelper();
    mList = (ListView)activity.getListView();
  }
  
  // Tests that the activity has received and then displayed all
  // the products in the database
  public void testRetreivedAllProducts() {
    Cursor c = mDb.fetchAllProducts();
    Assert.assertEquals(c.getCount(), mList.getCount());
    c.close();
  }

}
