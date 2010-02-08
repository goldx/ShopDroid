package griffib.shopdroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SDroidSettings extends PreferenceActivity {

  /*
   * (non-Javadoc)
   * @see android.app.Activity#onCreate(android.os.Bundle)
   * 
   * Called when activity is first ran
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Open preference layout
    addPreferencesFromResource(R.xml.settings);    
  }
  
  

}
