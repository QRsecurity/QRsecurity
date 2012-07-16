package com.securitytoken.generateotp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author Nathaly Ramirez, Lina Quintero.
 *
 */

public class ChangePin extends Activity{
	
	private int pin;
	private String seed;
	private EditText oldPin;
	private EditText newPin;
	private Button button;
	private SharedPreferences prefs;
	
	/**
	 * En este método se inicia la pantalla newpin. Se realizan las validaciones necesarias para cambiar el
	 * PIN por uno nuevo. Este PIN nuevo es guardado en el dispositivo móvil.
	 * 
	 */
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newpin);  
        
        prefs =  getSharedPreferences("MyPreferences", 0 );
        pin=prefs.getInt("pin", 0);
        seed=prefs.getString("seed", null);	
        
        oldPin= (EditText)findViewById(R.id.oldPin);
        newPin=(EditText)findViewById(R.id.newPin);
        button=(Button)findViewById(R.id.accept);
        
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if(oldPin.getText().toString().equals("")||newPin.getText().toString().equals("")){
					
					Toast.makeText(getBaseContext(), "Por favor llene los campos.", Toast.LENGTH_SHORT).show();
					
				}else{
					
					if(oldPin.getText().toString().length()!=4||newPin.getText().toString().length()!=4){
						
						Toast.makeText(getBaseContext(), "Por favor llene los campos.", 
								Toast.LENGTH_SHORT).show();
					}else{
						
						if(pin==Integer.parseInt(oldPin.getText().toString())){
							
							int lastPin=Integer.parseInt(newPin.getText().toString());						 
							SharedPreferences.Editor editor = prefs.edit();
							editor.putInt("pin", lastPin);
							editor.commit();	
							
							oldPin.setText("");
							newPin.setText("");
							
							if(seed==null){
								Intent i=new Intent(ChangePin.this, GetSeed.class);
								i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
								startActivity(i);

							}else{
								Intent j=new Intent(ChangePin.this, GenerateOtp.class);
								j.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
								startActivity(j);
								
							}
						}else{
							Toast.makeText(getBaseContext(), "El PIN anterior no es correcto. " +
									"Ingreselo otra vez.", Toast.LENGTH_SHORT).show();
						
						}
					}
				}
			}
        });
	}
	
	/**
	 * Este método permite ir a la pantalla incial cuando se ha presionado el botón atrás.
	 */
	
    public boolean onKeyDown (int keyCode, KeyEvent event){
		 
		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			 Intent i= new Intent(ChangePin.this, SecurityTokenOtp.class);
			 i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
	         startActivity(i);
		 }
		 
		    return super.onKeyDown(keyCode, event);
	 }
}