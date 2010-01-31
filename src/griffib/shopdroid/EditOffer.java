package griffib.shopdroid;

import griffib.shopdroid.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    ImageButton addAttrButton = (ImageButton) findViewById(R.id.add_attr);
    
    
    confirmButton.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        
        bundle.putString(SDroidDb.KEY_OFFERS_PRODUCT_NAME,
                         productName.getText().toString());
        
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
   
   addAttrButton.setOnClickListener(new View.OnClickListener() {
    
    @Override
    public void onClick(View v) {
      
      LinearLayout attrList = (LinearLayout) findViewById(R.id.attr_list);
      LayoutInflater inflater = getLayoutInflater();
      View row = inflater.inflate(R.layout.attr_row, null);
      LinearLayout extraAttr = (LinearLayout) row.findViewById(R.id.attr_row);
      attrList.addView(extraAttr);
      
    }
   });
   
    
  }
  
  

}
