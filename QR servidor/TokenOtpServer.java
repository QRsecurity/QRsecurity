package beans;

import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TokenOtpServer {

	
	private static long T0=0;
	private static long X = 30;
	private static String steps = "0";
	
	/**
	 * @param args
	 */
	
	public TokenOtpServer() {
		
	}
	
	public boolean validarOTPs (String otpUsuario, String semillaBD, String imei) {
		
		    boolean retorno=false;
	         Date fechaHora = Calendar.getInstance().getTime();
	         System.out.println("Fecha y Hora: "+fechaHora);
	        
	         DateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	         String fechaFormato=formato.format(fechaHora).toString();
	         System.out.println(fechaFormato);
	         
	         String[] arrayFechaHora= fechaFormato.split(":");
	         String segundos=arrayFechaHora[2];
	    
             long fechaMiliSegundos= fechaHora.getTime();
             System.out.println("Fecha en Milisegundos: "+fechaMiliSegundos);
             long fechaSegundos=fechaMiliSegundos/1000;
             System.out.println(fechaSegundos);
             System.out.println();
             
       
	       for(int i=-30; i<=30; i++){
	    	   long T = (fechaSegundos -i);
              // System.out.println("T: "+T);
               steps = Long.toHexString(T).toUpperCase();
               String otro=stringToHex(imei).toUpperCase();
               steps+=otro;
               System.out.println("los steps: "+ steps);
               String otpserver=generateTOTP(semillaBD, steps);
               
               System.out.println("el del servidor es: "+ otpserver +" y  el del usuario es: "+ otpUsuario);
               
               if(otpUsuario.equals(otpserver)){
            	   System.out.println(i);
            	   System.out.println(T);
            	   System.out.println(otpserver);
            	   
            	   int numSegundos= Integer.parseInt(segundos);
            	   System.out.println(i+"   ,   "+numSegundos);
            	   
            	   if(numSegundos>i){
            		   int segundosOtpCelular= numSegundos-i;
            		   System.out.println("El Otp fue creado en el segundo: "+segundosOtpCelular);
            	   }else{
            		   int segundosOtpCelular=numSegundos-i;
            		   System.out.println("El Otp fue creado en el segundo: "+(60+segundosOtpCelular));
            		   
            	   }
            	   i=30;
            	   
            	   return true;
           	   }else{
           		   if(i==30){
           			System.out.println("El tiempo fue generado en un rango mayor a 30 Segundos " +
           					"o ya se cumplio el tiempo de vida del OTP");
           			   
           		   }
           		   
           		   
           	   }
               
               
            		  
            		  
           }
	       
	       return false;
                  
	     }
	
	public static String stringToHex(String base)
    {
     StringBuffer buffer = new StringBuffer();
     int intValue;
     for(int x = 0; x < base.length(); x++)
         {
         int cursor = 0;
         intValue = base.charAt(x);
         String binaryChar = new String(Integer.toBinaryString(base.charAt(x)));
         for(int i = 0; i < binaryChar.length(); i++)
             {
             if(binaryChar.charAt(i) == '1')
                 {
                 cursor += 1;
             }
         }
         if((cursor % 2) > 0)
             {
             intValue += 128;
         }
         buffer.append(Integer.toHexString(intValue));
     }
     return buffer.toString();
}
	
        /**
     * 
     * HMAC calcula un mensaje para autenticacion 
     *
     * @param crypto: el algoritmo criptografico
     * @param keyBytes: el numero de bytes para la llave HMAC
     * @param text: el mensaje que va a ser autenticado
     */

    private static byte[] hmac_sha(byte[] keyBytes, byte[] text){
        try {
            Mac hmac;
            hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec macKey =
                new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }

    /**
     *  Convierte un valor HEX en un arreglo de bytes
     *
     * @param hex: HEX string
     *
     * @return:  byte array
     */

    private static byte[] hexStr2Bytes(String hex){
        // Adding one byte to get the right conversion
        // Values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex,16).toByteArray();

        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        for (int i = 0; i < ret.length; i++)
            ret[i] = bArray[i+1];
        return ret;
    }

    /**
     * Este metodo genera el TOTP para los parametros determinados.
     *
     * @param key: secreto compartido
     * @param time: valor de la ventana de tiempo.
     * @param returnDigits: numero de digitos para retornar.
     * @param crypto: la funcion criptografica para usar.
     *
     * @return: totp
     *              
     */

    public static String generateTOTP(String key, String time){
        int codeDigits = 8;
        String result = null;

        // Using the counter
        // First 8 bytes are for the movingFactor
        // Compliant with base RFC 4226 (HOTP)
       
        // Get the HEX in a Byte[]
        byte[] msg = hexStr2Bytes(time);
      //  System.out.println("Este es el mensaje: "+msg);
        
        byte[] k = hexStr2Bytes(key);
     //   System.out.println("Este es la key: "+k);


        byte[] hash = hmac_sha(k, msg);

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;
        
      //  System.out.println("este es el offset: "+offset);

        int binary =
            ((hash[offset] & 0x7f) << 24) |
            ((hash[offset + 1] & 0xff) << 16) |
            ((hash[offset + 2] & 0xff) << 8) |
            (hash[offset + 3] & 0xff);
        
    //   System.out.println("este es binary "+binary);
        
        int otp = binary % 100000000;

        result = Integer.toString(otp);
        while (result.length() < codeDigits) {
            result = "0" + result;
        }
        return result;
    }


}