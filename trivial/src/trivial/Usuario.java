package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Statement;


public class Usuario {
	
	protected String nombre, login, pass, correo;
	protected int id;

	public Usuario(){
		
	}
	
	public Usuario(String nombre, String login, String correo) {
		
		this.nombre = nombre;
		this.login = login;
		this.correo = correo;
		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Usuario other = (Usuario) obj;
		
		if (login == null) {
			
			if (other.login != null)
				return false;
			
		} else if (!login.equals(other.login))
			return false;
		
		return true;
		
	}

	/**
	 * Metodo para creación de un usuario
	 */
	public void crearUsuario(){
		
		boolean valido=true;
		
		do{
			
			try{
			
				//Datos necesarios para la base de datos
				Class.forName("com.mysql.jdbc.Driver");
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
				Statement st = (Statement) conexion.createStatement();
			
				String nombre, login, email, pass, cpass; //variables de apoyo
				
				System.out.print("Introduce tu nombre: ");
				nombre = Main.registro.nextLine();
				
				System.out.print("Introduce el nombre de usuario: ");
				login = Main.registro.nextLine();
				//Mas tarde comprobar si esta en la base de datos MIRAR MAS TARDE
				
				do{
					
					System.out.print("Introduce tu correo electronico: ");
					email = Main.registro.nextLine();
					
					if(!Main.comprobarEmail(email))
						System.out.println("Correo no valido.");
					
				}while(!Main.comprobarEmail(email));
				
				//Mas tarde comprobar si esta en la base de datos MIRAR MAS TARDE
				do{
					
					if(!valido)
						System.out.println("Password no valido vuelva a introducirlo.");
					
					System.out.print("Introduce un password: ");
					pass = Main.registro.nextLine();
					
					System.out.print("Introduce de nuevo el password: ");
					cpass= Main.registro.nextLine();
				
					if(pass.equals(cpass) && Main.comprobarPass(pass))
						valido=true;
					else
						valido=false;
					
				}while(!valido);
				
				//Consulta para buscar coincidencias
				ResultSet resultado = st.executeQuery("SELECT id "
						+ "FROM usuario "
						+ "WHERE login LIKE '" + login + "'"
							+ "OR correo LIKE '" + email + "';");
				
				int id=-1;//inicializamos el id en -1
				
				while(resultado.next())
					id=resultado.getInt(1); //Si hay coincidencias se asignara a id
					
				resultado.close();
				
				if(id==-1){ //Si es -1 es que no se han encontrado coincidencias
				
					this.nombre=nombre;
					this.login=login;
					this.pass=pass;
					this.correo=email;
					
					st.executeUpdate("INSERT INTO usuario (nombre, login, pass, correo) "
							+ "VALUES('" + this.nombre + "','" + this.login + "', MD5('" + this.pass + "'),'" + this.correo + "');", Statement.RETURN_GENERATED_KEYS);
					
					ResultSet rs =st.getGeneratedKeys();
					if(rs.next())
						this.id=rs.getInt(1);
					
					valido=true;
					rs.close();
					st.close();
					conexion.close();
					
				}else{ //Si se encuentran coincidencias.
					
					System.out.println("El usuario ya existe, vuelva a introducir los datos");
					valido=false;
					
				}
				
			}catch(Exception n){
				
				System.out.println("Se produjo un error. Posiblemente la base de datos no este activa o disponible.");
				Main.menuPrincipal();
				
			}
			
		}while(!valido);
		
	}
		
}