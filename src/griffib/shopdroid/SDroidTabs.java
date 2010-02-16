package griffib.shopdroid;

import griffib.shopdroid.lists.ProductsList;

import com.google.android.maps.Projection;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;


/**
 * Containers for various activities which are access via tabs.
 * 
 * @author Ben Griffiths
 *
 */
public class SDroidTabs extends TabActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    TabHost tabs = getTabHost();
    tabs.addTab(tabs.newTabSpec("Tab1").setIndicator("Database").
                setContent(new Intent(this, ProductsList.class)));
    tabs.addTab(tabs.newTabSpec("Tab2").setIndicator("Messaging").
                setContent(new Intent(this, ShopDroid.class)));
    
  }

}
