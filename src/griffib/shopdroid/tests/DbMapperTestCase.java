package griffib.shopdroid.tests;

import griffib.shopdroid.comms.DbMapper;
import junit.framework.Assert;
import junit.framework.TestCase;

public class DbMapperTestCase extends TestCase {
  public void setUp() {
    // set up
  }
  
  // Test case
  public void testFooMapper() {
    Assert.assertEquals("foo", DbMapper.FooMapper());
  }
}
