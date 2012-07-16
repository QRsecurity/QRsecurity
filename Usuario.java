package beans;
/**
 * 
 * @author Lina Marce
 * Clase que modela un usuario a autenticar
 *
 */
public class Usuario {

	private String nombre;
	private String password;
	private String imei;
	private String semilla;
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
	
	/**
	 * 
	 * @param nombre: id de usuario
	 * @param password: password de usuario
	 * @param imei: numero imei dispositivo movil
	 * @param semilla: semilla aleatoria
	 */
	public Usuario(String nombre, String password, String imei, String semilla) {
		super();
		this.nombre = nombre;
		this.password = password;
		this.imei = imei;
		this.semilla = semilla;
	}
	
	public Usuario() {
	
	}
	

}
