package griffib.shopdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.ImageButton;
import android.widget.LinearLayout;

public class EditOffer extends Activity {

  private EditText productName;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_offer);
    
    productName = (EditText) findViewById(R.id.product_name);
    
    Button confirmButton = (Button) findViewById(R.id.btn_done);
    Button cancelButton = (Button) findViewById(R.id.btn_discard);
 //   ImageButton addAttrButton = (ImageButton) findViewById(R.id.add_attr);
    
    
    /*
     * Programmatically finish building the UI
     */
    
    for (int i = 0; i<5; i++) {
      LinearLayout attrList = (LinearLayout) findViewById(R.id.attr_list);
      LayoutInflater inflater = getLayoutInflater();
      View row = inflater.inflate(R.layout.attr_row, null);
      LinearLayout extraAttr = (LinearLayout) row.findViewById(R.id.attr_row);
      
      ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                              ViewGroup.LayoutParams.FILL_PARENT,
                              ViewGroup.LayoutParams.FILL_PARENT);
      
      attrList.addView(extraAttr, i, params);
    }
    
    
    /*
     * Set up button listeners
     */
    
    confirmButton.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        
        bundle.putString(SDroidDb.KEY_OFFERS_PRODUCT_NAME,
                         productName.getText().toString());
        
        String[][] tags = new String[5][5];
        
        
        
        Intent i = new Intent();
        i.putExtras(bundle);
        setResult(RESULT_OK, i);
        finish();        
      }
    });
   
   cancelButton.setOnClickListener(new View.OnClickListener() {
    
    @Override
    public void onClick(View v) {
      setResult(RESULT_CANCELED);
      finish();      
    }
   });
   
//   addAttrButton.setOnClickListener(new View.OnClickListener() {
//    
//    @Override
//    public void onClick(View v) {
//      
//      LinearLayout attrList = (LinearLayout) findViewById(R.id.attr_list);
//      LayoutInflater inflater = getLayoutInflater();
//      View row = inflater.inflate(R.layout.attr_row, null);
//      LinearLayout extraAttr = (LinearLayout) row.findViewById(R.id.attr_row);
//      attrList.addView(extraAttr);
//      
//    }
//   });
   
    
  }
  
  

}
