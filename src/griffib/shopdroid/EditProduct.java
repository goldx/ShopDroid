package griffib.shopdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class EditProduct extends Activity {

  private long productId = -1;
  
  EditText productField;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.edit_product);
    
    // Find interactable items
    productField = (EditText) findViewById(R.id.product_name);
    Button btnOk = (Button) findViewById(R.id.btn_done);
    Button btnDiscard = (Button) findViewById(R.id.btn_discard);
    
   
    
    Bundle extras = getIntent().getExtras();
    String productName = null;
    if (extras!=null) {
      // Then we must be editing!
      if((extras.getString(SDroidDb.KEY_PRODUCT_NAME)) != null)
        productName = extras.getString(SDroidDb.KEY_PRODUCT_NAME);
          
      productField.setText(productName);
      
      if((Long)(extras.getLong(SDroidDb.KEY_ID)) != null)
        productId = extras.getLong(SDroidDb.KEY_ID);
    }
    
    // Ok button
    btnOk.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        Bundle bundle = new Bundle();
        
        bundle.putString(SDroidDb.KEY_PRODUCT_NAME,
                         productField.getText().toString());
        bundle.putLong(SDroidDb.KEY_ID, productId);
        
        Intent i = new Intent();
        i.putExtras(bundle);
        setResult(RESULT_OK, i);
        finish();        
      }
    });
    
    btnDiscard.setOnClickListener(new View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        setResult(RESULT_CANCELED);
        finish();
      }
    });
  }


}
