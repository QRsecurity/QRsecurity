package beans;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

 
import javax.annotation.Resource;
import javax.faces.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.util.*;
import javax.faces.context.*;
import javax.faces.event.*;
import javax.faces.component.*;
import javax.servlet.http.*;



public class UsuarioBean implements Serializable{

	
	
	private ArrayList list = new ArrayList();
	private String nombre;
	private String password;
	private String imei;
	private String semilla;
	private String otp;
	private String respuesta;
	
	public String getRespuesta() {
		return respuesta;
	}
	public void setRespuesta(String respuesta) {
		
		this.respuesta = respuesta;
	}

	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
			
	public UsuarioBean() {
			
	}
	
	/**
	 * Lee valores del formulario y los guarda en la base de datos a través de la clase conection
	 * @return String segun el resultado de la operacion
	 * @throws Throwable
	 */
	public String doGuardar() throws Throwable{
		
		respuesta="repetido";
		Conection cnx=new Conection();
				
		if(cnx.consultarUsuario(nombre)==null){
			semilla=new GeneraSemilla().getSemilla();
			cnx.agregarusuario(new Usuario(nombre,password,imei,semilla));
			cnx.finalize();
			
			respuesta="guardado";		
		}
		return respuesta;
	}
	/**
	 * Consulta valores del formulario  a través de la clase conection
	 * @return
	 * @throws Throwable
	 */
	public String doConsultar() throws Throwable{
		
		respuesta="incorrecto";
		
		Conection cnx=new Conection();
		Usuario elUsuario= cnx.consultarUsuario(nombre);
		cnx.finalize();
		
		if(elUsuario==null){
			respuesta="el Usuario No existe";
			return respuesta;
		}
		
		if(validar(elUsuario)){
			respuesta="correcto";
		
		}			
			return respuesta;
		
	}
	
	/**
	 * Valida valores del formulario usando la clase TokenOtpServer
	 * @param usuario
	 * @return
	 */
	public boolean validar(Usuario usuario){
		if(usuario.getPassword().equals(password)==false){
			return false;
		}
		
		TokenOtpServer otpServer=new TokenOtpServer();
		boolean esOtp=otpServer.validarOTPs(otp, usuario.getSemilla(), usuario.getImei());
		
		if(esOtp==false){
			return false;
		}
		
		
		
		
		return true;
	}
		
	public void setList(ArrayList list) {

		this.list = list;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getSemilla() {
		return semilla;
	}
	public void setSemilla(String semilla) {
		this.semilla = semilla;
	}
}
