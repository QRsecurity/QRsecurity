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
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Nathaly Ramirez, Lina Quintero.
 *
 */

public class FirstPin extends Activity{
	
	private EditText text;
	private TextView textNote; 
	private Button button;
	private Button buttonChange;
	private int pin=0;
	private int pinPrefs=0;
	private String seed=null;
	
	/**
	 * En este método se inicia la pantalla pin1. Aquí se realizan las validaciones necesarias del PIN para 
	 * permitir el acceso a la aplicación. Este PIN es guardado en el dispositivo móvil.
	 * 
	 */
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin1);
        
        text=(EditText)findViewById(R.id.pin1);
        button=(Button)findViewById(R.id.buttonPin1);
        textNote=(TextView)findViewById(R.id.textNote);
        buttonChange=(Button)findViewById(R.id.buttonChange);
      
        
        SharedPreferences prefs =  getSharedPreferences("MyPreferences", 0 );
        pinPrefs=prefs.getInt("pin", 0);  
        
        if(pinPrefs==0){
        	
        	textNote.setVisibility(View.VISIBLE);
        	buttonChange.setVisibility(View.INVISIBLE);
        	
        }else{
        	
        	buttonChange.setVisibility(View.VISIBLE);
        	textNote.setVisibility(View.INVISIBLE);
        	
        }  
        
        
                       
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				
				if(text.getText().toString().equals("")){
					Toast.makeText(getBaseContext(), "Por favor escriba el PIN.",
							Toast.LENGTH_SHORT).show();	
				}else{
					if(text.getText().toString().length()==4){
						
						String textoPin=text.getText().toString();	
						pin=Integer.parseInt(textoPin);
						
						SharedPreferences prefs = getSharedPreferences("MyPreferences",0);						 
						SharedPreferences.Editor editor = prefs.edit();
						seed=prefs.getString("seed", null);
						
						if(pinPrefs==0){
							editor.putInt("pin", pin);
							editor.commit();
							
							if(seed==null){	
								text.setText("");
								Intent i= new Intent(FirstPin.this, GetSeed.class);
								i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
					            startActivity(i); 				            
					            
							}else{
								text.setText("");
								Intent j= new Intent(FirstPin.this, GenerateOtp.class);
								j.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
					            startActivity(j); 
					            
							}
							
						}else{
							
							if(pin==pinPrefs){
								
								if(seed==null){	
									text.setText("");
									Intent i= new Intent(FirstPin.this, GetSeed.class);
									i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
						            startActivity(i); 				            
						            
								}else{
									text.setText("");
									Intent j= new Intent(FirstPin.this, GenerateOtp.class);
									j.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
						            startActivity(j); 
						            
								}
							}else{								
								Toast.makeText(getBaseContext(), "El PIN no es correcto.",
										Toast.LENGTH_SHORT).show();	
							}
						}												
												
					}else{
						Toast.makeText(getBaseContext(), "El PIN debe tener 4 caracteres númericos.",
								Toast.LENGTH_SHORT).show();							
					}
				}
			}
        });
        
        buttonChange.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				text.setText("");
				Intent k= new Intent(FirstPin.this, ChangePin.class);
				k.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
	            startActivity(k); 
				
			}        	
        });
        
	}
	
	/**
	 * Este método permite ir a la pantalla incial cuando se ha presionado el botón atrás.
	 */
	
    public boolean onKeyDown (int keyCode, KeyEvent event){
		 
		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			 Intent i= new Intent(FirstPin.this, SecurityTokenOtp.class);
			 i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
	         startActivity(i);
		 }
		 
		    return super.onKeyDown(keyCode, event);
	 }
}