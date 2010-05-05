package griffib.shopdroid.tests;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

public class MyInstrumentationTestRunner extends InstrumentationTestRunner {
  @Override
  public TestSuite getAllTests() {
    InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
    
    //must add all tests here
    suite.addTestSuite(DbMapperTestCase.class);
    suite.addTestSuite(ProductsListTestCase.class);
    suite.addTestSuite(OffersListTestCase1.class);
    
    return suite;
  }
  
  @Override
  public ClassLoader getLoader() {
    return MyInstrumentationTestRunner.class.getClassLoader();
  }
}
