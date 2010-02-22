package griffib.shopdroid;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SDroidSettings extends PreferenceActivity {

  private final static String KEY_TAG_LIST = "tag_list";
  /*
   * Called when activity is first ran
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Open preference layout
    addPreferencesFromResource(R.xml.settings);    
  }
  
  public String getTags(Context ctx) {
    return PreferenceManager.getDefaultSharedPreferences(ctx)
           .getString(KEY_TAG_LIST, null);
  }

}
