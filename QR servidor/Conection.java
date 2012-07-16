package beans;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
 
import javax.annotation.Resource;
import javax.faces.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * 
 * @author Lina Marce
 *
 */
public class Conection {
	
	String url= " ";
	String username = "root";
	String password = "root";
	String tabla = "usuario";
	
	Statement instruccion = null; 
	Connection conexion=null; 
	ResultSet resultado=null;
/**
 * Establece la conexion con base de datos mysql
 */
	public Conection() {
		
	url = "jdbc:mysql://localhost/otp"; 
	

	try { 
	Class.forName("com.mysql.jdbc.Driver"); 
	} catch (Exception e) { 
	System.out.println("Fallo al cargar el controlador JDBC/ODBC"); 
	return; 
	} 

	
	try { 
	conexion = DriverManager.getConnection( url,username, 	password); 
	instruccion = conexion.createStatement(); 
	} 
	catch (Exception e) { 
	System.err.println("problemas al conectar con "+url); 
	} 
	} 

	/**
	 * 
	 * @param usuario: usuario a agregar en bd
	 * @throws SQLException
	 */
	public void agregarusuario ( Usuario usuario ) throws SQLException 
	{ 
	try { 
		
	instruccion.executeUpdate( "INSERT INTO usuario (nombre,password,imei,semilla) VALUES " + 
	"('" + usuario.getNombre()+"', AES_ENCRYPT('" + usuario.getPassword() + 
	"','llave') , '" + usuario.getImei() + "', AES_ENCRYPT('" + usuario.getSemilla() + "','llave'))"); 

	System.out.println(usuario.getSemilla()); 
	} catch (Exception e) { 
	System.err.println("problemas con la sentencia SQL enviada a "+url+ 
	": "+e.getMessage()); 
	} 
	}
	
	/**
	 * 
	 * @param nombre: nombre del usuario a consultar
	 * @return Usuario consultado o null si no existe
	 * @throws SQLException
	 */
	public Usuario consultarUsuario(String nombre) throws SQLException{

		Usuario elUsuario=null;
		instruccion=conexion.createStatement();
		resultado=instruccion.executeQuery("select AES_DECRYPT(password,'llave'), imei, AES_DECRYPT(semilla, 'llave')  from usuario where nombre='"+nombre+"'");
		
		
		while(resultado.next()){
			 
			String password= new String(resultado.getBlob("AES_DECRYPT(password,'llave')").getBytes(1, (int)resultado.getBlob("AES_DECRYPT(password,'llave')").length()));
			
			String imei= resultado.getString("imei");
			String semilla= new String (resultado.getBlob("AES_DECRYPT(semilla, 'llave')").getBytes(1, (int)resultado.getBlob("AES_DECRYPT(semilla, 'llave')").length()));
			
			elUsuario=new Usuario (nombre,password,imei,semilla);
			
						
		}
		
		if(elUsuario!=null){
			System.out.println(elUsuario.getPassword());
			System.out.println(elUsuario.getSemilla());
			System.out.println(elUsuario.getImei());
			System.out.println(elUsuario.getNombre());
		}else{
			System.out.println("esta vacio el usuario");
		}
		
		
		
		return elUsuario;
		
		
	}
	

	// cerrar instrucciones y terminar la conexion a la base de datos 

	/**
	 * finaliza conexion con bd
	 */
	protected void finalize() throws Throwable 
	{ 
	super.finalize(); 

	// tratar de cerrar la conexión a la base de datos 
	try { 
	instruccion.close(); 
	conexion.close(); 
	} 

	// procesar posible excepcion SQLException en operacion de cierre 
	catch ( SQLException excepcionSQL ) { 
	excepcionSQL.printStackTrace(); 
	} 
	} 
	
	
	
	
	

}
