package com.securitytoken.generateotp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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

public class GetSeed extends Activity {
	private EditText text;
	private Button buttonSeed;
		
	
	/**
	 * En este método se inicia la pantalla seed. Se realizan las validaciones necesarias de la semilla.
	 * Esta semilla es guardada en el dispositivo móvil de manera encriptada.
	 * 
	 */
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.seed);
	        
	        text=(EditText)findViewById(R.id.textSeed);
	        buttonSeed=(Button)findViewById(R.id.buttonSeed);
	        	        
	        buttonSeed.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
														
					if(text.getText().toString().equals("")){
						Toast.makeText(getBaseContext(), "Por favor escriba la semilla.",
									Toast.LENGTH_SHORT).show();		
						
					}else{
						if(text.getText().toString().length()!=64){
							Toast.makeText(getBaseContext(), "La semilla no tiene los 64 caracteres.",
									Toast.LENGTH_SHORT).show();	
							
						}else{		
							boolean correct = true;
							String x=text.getText().toString().toLowerCase();
												
							try{
								for(int c=0;c<x.length();c++){
									x.charAt(c);
									
									if(x.charAt(c)=='g'||x.charAt(c)=='h'||x.charAt(c)=='i'||x.charAt(c)=='j'||
											x.charAt(c)=='k'||x.charAt(c)=='l'||x.charAt(c)=='m'||
											x.charAt(c)=='n'||x.charAt(c)=='ñ'||x.charAt(c)=='o'||
											x.charAt(c)=='p'||x.charAt(c)=='q'||x.charAt(c)=='r'||
											x.charAt(c)=='s'||x.charAt(c)=='t'||x.charAt(c)=='u'||
											x.charAt(c)=='v'||x.charAt(c)=='w'||x.charAt(c)=='x'||
											x.charAt(c)=='y'||x.charAt(c)=='z'){
										correct=false;
										c=64;																		
									}								
								}								
							}catch(Exception e){
								correct=false;								
							}							
														
							if(correct==false){
								Toast.makeText(getBaseContext(), "La semilla tiene caracteres incorrectos.",
										Toast.LENGTH_SHORT).show();	
								
							}else{								
								SharedPreferences prefs = getSharedPreferences("MyPreferences",0);								
								
								String seed= encrypt(prefs.getInt("pin", 0), 
								text.getText().toString().toLowerCase());
																
								if(seed.equals("")){
									text.setText("");
									Intent k=new Intent(GetSeed.this, GetSeed.class);
									k.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
						        	startActivity(k);						        	
									
								}else{
									
								//	texto.setText(seed);
									SharedPreferences.Editor editor = prefs.edit();
									editor.putString("seed", seed);
									editor.commit();
									
									text.setText("");	
									Intent m=new Intent(GetSeed.this, GenerateOtp.class);
									m.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
						        	startActivity(m);						        									
								}
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
    		  Intent i= new Intent(GetSeed.this, SecurityTokenOtp.class);
		      i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		      startActivity(i);
    	  }
    	  
			 return super.onKeyDown(keyCode, event);
     }
	 
     /**
      * Este método permite encripar con Aes 128, la semilla dada.
      * 
      * @param a, la llave(PIN) que va a encriptar
      * @param b, el texto(semilla) que se va a encriptar
      * @return una cadena con la semilla encriptada
      */
     
	 private static String encrypt(int a, String b){
		 
		 try {
				byte[] theKey = null;
				byte[] theMsg = null;
				String numStr= a+"";				
				String all="0000000000000000000000000000"+numStr;
				
				theKey = hexToBytes(all);
				theMsg = hexToBytes(b);

				SecretKeySpec keySpec = new SecretKeySpec(theKey, "AES");
				Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
				cipher.init(Cipher.ENCRYPT_MODE, keySpec);
				byte[] theCph = cipher.doFinal(theMsg);
				
				return bytesToHex(theCph).toLowerCase();
				
		 }catch (Exception e) {
			 return "";
		 }	 
	 }
	 
	 /**
	  * Este método permite convertir una cadena en un arreglo de bytes.
	  * 
	  * @param str, la cadena que va a ser convertida en bytes
	  * @return el arreglo de bytes de la cadena enviada por parámetro
	  */
	 
	 private static byte[] hexToBytes(String str) {
		 		 
		 if (str == null) {
			 return null;
			 
		 } else if (str.length() < 2) {
				return null;
				
			} else {
				int length = str.length() / 2;
				byte[] buffer = new byte[length];
				
				for (int i = 0; i < length; i++) {
					buffer[i] = (byte) Integer.parseInt(str.substring(i * 2,
							i * 2 + 2), 16);
				}
				
				return buffer;
			}
		}

	 /**
	  * Este método permite convertir un arreglo de bytes a una cadena.
	  * 
	  * @param data, el arreglo de bytes que se va a convertir en un string
	  * @return la cadena correspondiente al arreglo de bytes dado
	  */
	 
	 private static String bytesToHex(byte[] data) {
		 
		 if (data == null) {
			 return null;
			
 		 } else {
 			 int tamano = data.length;
			 String str = "";
			
			 for (int i = 0; i < tamano; i++) {
			 	 if ((data[i] & 0xFF) < 16)
					 str = str + "0"
								+ java.lang.Integer.toHexString(data[i] & 0xFF);
					 else
						 str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
			 }
			 return str;
		 }
	 }
	 
}