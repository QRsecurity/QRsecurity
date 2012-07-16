package com.securitytoken.generateotp;

import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Basado en:
 * 	- RFC6238: TOTP, Time-Based One-Time Password Algorithm
 * 
 */

public class GenerateOtp extends Activity{
	
	private Button button;
	private TextView value;
	private TextView text;
	private TextView textTime;
	private TextView time;
	private long dateSeconds;
	private SharedPreferences prefs;
	
	/**
	 * En este método se toma la fecha actual del dispositivo móvil, el IMEI (identificador del dispositivo) y 
	 * la semilla guardada en el dispositivo para enviarlos al método que generar el OTP.  
	 */
		
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.otp);
	        
	        button=(Button)findViewById(R.id.generateOtp);
	        value=(TextView)findViewById(R.id.otp);
	        text=(TextView)findViewById(R.id.textValueOtp);
	        textTime=(TextView)findViewById(R.id.textTime);
	        time=(TextView)findViewById(R.id.time);
	        	        
	        prefs =  getSharedPreferences("MyPreferences", 0 );
	        value.setText(prefs.getString("otp1", null));
	        final SharedPreferences.Editor editor = prefs.edit();
			
			if(prefs.getString("otp1", null)!=null){
				
				Date dH = Calendar.getInstance().getTime();
				long dMilli= dH.getTime();			
				long dSeconds=dMilli/1000;        	
				long total=dSeconds-Long.parseLong(prefs.getString("date", "0"));        	
				long t=total*1000;
				
				text.setText("El OTP generado es: ");
				textTime.setText("Tiene validez hasta los próximos ");
				time.setText(String.valueOf(total));        	
				button.setEnabled(false);
				
				new CountDownTimer(30000-t, 1000) {

					public void onTick(long millisUntilFinished) {							    	 
			    	 
						time.setText(millisUntilFinished/1000+" segundos");
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						button.setEnabled(true);
						text.setText(" ");
						textTime.setText(" ");
						value.setText(" ");
						time.setText(" ");
						editor.putString("otp1", null );
						editor.putString("date", "0");
						editor.commit();
						
							Intent a= new Intent(GenerateOtp.this, SecurityTokenOtp.class);
							a.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
					        startActivity(a);
					
						
						
					}
				}.start();				
			}
	        
	        button.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					byte[] end = null;
					
					try {
						final SharedPreferences.Editor editor = prefs.edit();
						String steps="0";										
						
						int pin=prefs.getInt("pin", 0);
						String pinFinal="0000000000000000000000000000"+pin;
						String seed=prefs.getString("seed", null);
						    
						byte[] theKey = hexToBytes(pinFinal);				     
						byte[] seed1=hexToBytes(seed);
				     				     
						end=decrypt(seed1, theKey);
						bytesToHex(end).toLowerCase();					
										
						TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						String imei = tm.getDeviceId();
				
						Date dateHour = Calendar.getInstance().getTime();
						long dateMilliSeconds= dateHour.getTime();
						dateSeconds=dateMilliSeconds/1000;
						
						editor.putString("date",String.valueOf(dateSeconds));
						editor.commit();
				     	 
						steps = Long.toHexString(dateSeconds).toUpperCase();
						String other=stringToHex(imei).toUpperCase();
						steps+=other;
						
						button.setEnabled(false);
					 	text.setText("El OTP generado es: ");
						textTime.setText("Tiene validez hasta los próximos ");
					
						String otp="";
						
				        otp=generateTOTP(bytesToHex(end).toLowerCase(), steps);
				        text.setText("El OTP generado es:");
				        value.setText(otp);				        	
				        	
						editor.putString("otp1", otp );
						editor.commit();
							
						new CountDownTimer(30000, 1000) {

						     public void onTick(long millisUntilFinished) {							    	 
							    	 
						    	 time.setText(millisUntilFinished/1000+" segundos");
						     }

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								editor.putString("otp1", null );
						   	    editor.commit();
							//	button.setEnabled(true);
								text.setText(" ");
								textTime.setText(" ");
								value.setText(" ");
								time.setText(" ");		
								
								Intent b= new Intent(GenerateOtp.this, SecurityTokenOtp.class);
								b.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
						        startActivity(b);
							}

						  }.start();	
					}catch (Exception e){
						value.setText(e.toString());
					}				     
				}
	        });
	 }	
	  
	 /**
	  * Este método permite ir a la pantalla inicial cuando se ha presionado el botón atrás.
	  */
	 
	 public boolean onKeyDown (int keyCode, KeyEvent event){
		 
		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			 Intent i= new Intent(GenerateOtp.this, SecurityTokenOtp.class);
			 i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
	         startActivity(i);
		 }
		 
		 return super.onKeyDown(keyCode, event);
	 }

	 /**
	  * Este método permite desencriptar con Aes-128, a partir de los arreglos de bytes dados por parámetro.
	  * 
	  * @param encrypted, el texto en bytes encriptado
	  * @param key, la llave para desencriptar
	  * @return el arreglo de bytes desencriptado
	  * @throws Exception
	  */
	 
	 private static byte[] decrypt(byte[] encrypted, byte[] key) throws Exception {
			
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");		
				
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decrypted= cipher.doFinal(encrypted);
			
			return decrypted;
		}
		
	 /**
	  * Este método permite convertir un hexadecimal en un arreglo de bytes.
	  * 
	  * @param str, el hexadecimal que va a ser convertido en bytes
	  * @return el arreglo de bytes del hexadecimal enviado por parámetro
	  */
	 
	 private static byte[] hexToBytes(String str) {
		 
		 if (str == null) {
			 return null;
			 
		 }else if (str.length() < 2) {
			 return null;
			 
		 }else {
			 int length = str.length() / 2;
			 byte[] buffer = new byte[length];
			 
			 for (int i = 0; i < length; i++) {
				 buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
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
			 
		 }else {
			 int length = data.length;
			 String str = "";
				
			 for (int i = 0; i < length; i++) {
				 
				 if ((data[i] & 0xFF) < 16)
					 str = str + "0"
				 + java.lang.Integer.toHexString(data[i] & 0xFF);
				 else
					 str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
			 }
			 
			return str;
		 }
	 }
	
	 /**
	  * Este método permite generar OTPs de acuerdo a los valores dados por parámetro.
	  * 
	  * @param key, semilla desencriptada
	  * @param time, fecha y hora actual concatenada con el IMEI
	  * @return cadena con el OTP generado
	  */
	 
	 private static String generateTOTP(String key, String time){
		 
		 String result = null;

	     byte[] msg = hexStringToBytes(time);
	     byte[] k = hexStringToBytes(key);
	     byte[] hash = hmac_sha(k, msg);
	     int offset = hash[hash.length - 1] & 0xf;

	     int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) |
	    		 ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
	        
	     int otp = binary % 100000000;

	     result = Integer.toString(otp);
	        
	     while (result.length() < 8) {
	    	 result = "0" + result;
	     }
	        
	     return result;
	 }
	
	 /**
	  * Este método permite convertir una cadena string en hexadecimal.
	  * 
	  * @param base, el string que va a convertirse en hexadecimal
	  * @return el hexadecimal de la cadena dada
	  */
	 
	 private static String stringToHex(String base){
		 
		 StringBuffer buffer = new StringBuffer();
		 int value;
		     
	     for(int x = 0; x < base.length(); x++){
	    	 int cursor = 0;
	         value = base.charAt(x);
	         String binaryChar = new String(Integer.toBinaryString(base.charAt(x)));
		         
	         for(int i = 0; i < binaryChar.length(); i++){
		        	 
	             if(binaryChar.charAt(i) == '1'){
	                 cursor += 1;
	             }
	         }
		         
	         if((cursor % 2) > 0){
	             value += 128;
	         }
		         
	         buffer.append(Integer.toHexString(value));
	     }
		     
	     return buffer.toString();     
		}		
		
	 /**
	  * Este método permite calcular un HMAC a partir de SHA256 y de los parámetros dados.
	  * 
	  * @param key, la llave usada para HmacSHA256
	  * @param text, el texto que va a ser autenticado
	  * @return el texto autenticado
	  */
	 
	 private static byte[] hmac_sha(byte[] key, byte[] text){
		 
		 try {
			 Mac hmac;
             hmac = Mac.getInstance("HmacSHA256");
             SecretKeySpec macKey = new SecretKeySpec(key, "RAW");
             hmac.init(macKey);
             return hmac.doFinal(text);
	            
         } catch (GeneralSecurityException gse) {
        	 throw new UndeclaredThrowableException(gse);
         }
     }
	
	 /**
	  * Este método permite convertir una cadena en un arreglo de bytes.
	  * 
	  * @param str, la cadena que va a ser convertida en bytes
	  * @return el arreglo de bytes de la cadena enviada por parámetro
	  */
	 
	 private static byte[] hexStringToBytes(String str){
		 		 
		 byte[] bArray = new BigInteger("10" + str,16).toByteArray();	       
         byte[] array = new byte[bArray.length - 1];
         
         for (int i = 0; i < array.length; i++)
        	 array[i] = bArray[i+1];
         
         return array;
	 }
		 
}