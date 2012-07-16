package com.securitytoken.generateotp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @author Nathaly Ramirez, Lina Quintero.
 *
 */

public class SecurityTokenOtp extends Activity {
   	
	private Button access;	
	
    /**
	 * En este método se inicia la pantalla main.
	 * 
	 */
	
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);      
             
        access=(Button)findViewById(R.id.buttonAccess);
               
        access.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent k=new Intent(SecurityTokenOtp.this, FirstPin.class);
			    k.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			    startActivity(k);
      
			}
		});               	       	
    }
}