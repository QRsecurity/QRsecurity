package beans;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GeneraSemilla {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 */
		
	private String semilla;
	
	public String getSemilla() throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		 SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		 
		 // byte[] bt = random.generateSeed(32);
		 //String mostrar=toHexadecimal(bt);
		 //System.out.println(mostrar);
		 
		 String uno=toHexadecimal(random.generateSeed(32));
			  
		return uno;
	}

	public void setSemilla(String semilla) {
		this.semilla = semilla;
	}


	
	 public GeneraSemilla() {
			
		
		 
	}
	 
	

	private static String toHexadecimal(byte[] digest){
		   String hash = "";
		 
		         for(byte aux : digest) {
		
		             int b = aux & 0xff;
		 
		             if (Integer.toHexString(b).length() == 1) hash += "0";
		
		             hash += Integer.toHexString(b);
		
		         }
		 
		     return hash;
		
		     }
	 
	 


}
