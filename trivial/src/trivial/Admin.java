package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.mysql.jdbc.Statement;

public class Admin extends Usuario {

	private Calendar ult_login;

	public Calendar getUlt_login() {
		return ult_login;
	}

	public void setUlt_login(Calendar ult_login) {
		this.ult_login = ult_login;
	}
	
	/**
	 * Metodo para identificar a los administradores
	 * @return
	 */
	public boolean logear(){
		
		boolean valido=true;
		
		this.ult_login = new GregorianCalendar();
		
		do{
			
			try{
				
				String usuario, pass, nombre="", correo="";
				int id=-1;//inicializamos el id en -1

				Class.forName("com.mysql.jdbc.Driver");
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
				Statement st = (Statement) conexion.createStatement();

				System.out.print("Usuario: ");
				usuario=Main.registro.nextLine();
				System.out.print("Password: ");
				pass=Main.registro.nextLine();
					
				ResultSet resultado = st.executeQuery("SELECT * "
						+ "FROM usuario u, admins a "
						+ "WHERE u.id = a.id "
							+ "AND login LIKE '" + usuario + "' "
							+ "AND pass LIKE MD5('" + pass + "');");
				
				while(resultado.next()){
						
					id=resultado.getInt(1); 
					nombre=resultado.getString(2);
					correo=resultado.getString(5);
						
				}
								
				st.executeUpdate("UPDATE admins "
								+ "SET ult_login = '" + ult_login.get(Calendar.YEAR) + "-" 
													  + (ult_login.get(Calendar.MONTH)+1) + "-"
													  + ult_login.get(Calendar.DAY_OF_MONTH) + "' "
								+ "WHERE id = " + id + ";");
				
				resultado.close();	
				st.close();
				conexion.close();
				
				if(id!=-1){ //Si es diferente de -1, los datos se han encontrado
				
					this.id=id;
					this.nombre=nombre;
					this.login=usuario;
					this.pass=pass;
					this.correo=correo;
					valido=true;
	
				}else{ //Si no se encuentran coincidencias.
					
					System.out.println("El administrador no se ha encontrado, vuelva a introducir los datos");
					st.close();
					conexion.close();
					valido=false;
					return false;
					
				}
				
			}catch(Exception n){
				
				System.out.println("Se produjo un error al buscar el administrador en la base de datos");
				Main.menuPrincipal();
				return false;
				
			}
			
		}while(!valido);
		
		return true;
	}
	
	/**
	 * Metodo para añadir un jugador
	 */
	public void añadirJug(){
		
		Jugador jug= new Jugador();
		jug.crearUsuario();
		
	}
	
	/**
	 * Metodo para eliminar un jugador
	 */
	public void eliminarJug(){
		
		int eli;
		
		try{
				
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			ResultSet resultado = st.executeQuery("SELECT u.id, u.nombre, u.login, u.correo "
												+ "FROM usuario u, jugador j "
												+ "WHERE u.id = j.id;");
					
			System.out.println("ID		Nombre		Login		Correo");
			
			while(resultado.next()){
					
				System.out.println(resultado.getInt(1) + "		" 
									+ resultado.getString(2) + "	" 
									+ resultado.getString(3) + "	" 
									+ resultado.getString(4));
					
			}
				
			System.out.println("¿Que ID de usuario quieres eliminar?: ");
			eli=Integer.parseInt(Main.registro.nextLine());
			
			Class.forName("com.mysql.jdbc.Driver");
						
			st.executeUpdate("DELETE FROM usuario "
							+ "WHERE id =" + eli + ";");
											
			resultado.close();	
			st.close();
			conexion.close();
				
		}catch(NumberFormatException n){
			
			System.out.println("Se esperaba un numero.");
			this.eliminarJug();
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar el usuario en la base de datos");
			Main.menuPrincipal();
			
		}
	}

	/**
	 * Metodo para modificar un usuario
	 */
	public void modificarJug(){
		
		int eli;
		String nombre, pass, cpass;
		boolean valido = true;
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			ResultSet resultado = st.executeQuery("SELECT u.id, u.nombre, u.login, u.correo "
												+ "FROM usuario u, jugador j "
												+ "WHERE u.id = j.id;");
					
			System.out.println("ID		Nombre		Login		Correo");
			
			while(resultado.next()){
					
				System.out.println(resultado.getInt(1) + "		" 
									+ resultado.getString(2) + "	" 
									+ resultado.getString(3) + "	" 
									+ resultado.getString(4));
					
			}
				
			System.out.print("¿Que usuario quieres modificar?: ");
			eli=Integer.parseInt(Main.registro.nextLine());
			
			System.out.print("Nombre nuevo: ");
			nombre = Main.registro.nextLine();
			
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
			
			st.executeUpdate("UPDATE usuario "
							+ "SET nombre = '" + nombre + "', "
							+ "pass = MD5('" + pass + "') "
							+ "WHERE id = " + eli + ";");
					
			resultado.close();
			st.close();
			conexion.close();
				
		}catch(NumberFormatException n){
			
			System.out.println("Se esperaba un numero.");
			this.eliminarJug();
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar el usuario en la base de datos");
			Main.menuPrincipal();
			
		}
	
	}
	
	/*<SUSPENDIDO
	
	public void añadirPreg(){
		
		try{
			
			int tema, categ, dif;
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			
			//TEMAS
			ResultSet resultado = st.executeQuery("SELECT * "
												+ "FROM tema;");
		
			System.out.println("\nTEMAS DISPONIBLES");
			while(resultado.next()){
					
				System.out.println("-ID: " + resultado.getInt(1) + "Tema: " + resultado.getString(2));
				
			}
				
			resultado.close();	
			System.out.println("=================================");
			System.out.print("¿Que tema será la pregunta?: ");
			tema=Integer.parseInt(Main.registro.nextLine());			
			
			resultado = st.executeQuery("SELECT id, categoria "
									  + "FROM categoria "
									  + "WHERE id_tema=" + tema + ";");
			
			
			//CATEGORIAS
			System.out.println("\nCATEGORIAS DISPONIBLES");
			while(resultado.next()){
				
				System.out.println("-ID: " + resultado.getInt(1) + "Categoria: " + resultado.getString(2));
				
			}
			System.out.println("0-Nueva Categoria");
			System.out.println("=================================");
			System.out.print("¿Que categoria será la pregunta?: ");
			categ=Integer.parseInt(Main.registro.nextLine());
			System.out.println();
			
			resultado = st.executeQuery("SELECT * "
					  + "FROM dificultad;");
			
			
			//DIFICULTAD
			System.out.println("\nDIFICULTAD");
			
			while(resultado.next()){
				
				System.out.println("-ID: " + resultado.getInt(1) + "Dificultad: " + resultado.getString(2));
				
			}
			System.out.println("=================================");
			System.out.print("¿Que dificultad tendrá la pregunta?: ");
			dif=Integer.parseInt(Main.registro.nextLine());
			
			
			
			if(categ == 0){
				
				
				String categoria;
				int veces;
				
				System.out.print("¿Que categoría desea introducir?: ");
				categoria=Main.registro.nextLine();
				
				st.executeUpdate("INSERT INTO categoria (categoria, id_tema) "
							   + "VALUES ('" + categoria + ", " + tema + ");", Statement.RETURN_GENERATED_KEYS);
				
				resultado = st.getGeneratedKeys();
				
				while(resultado.next())
					categ=resultado.getInt(1);
				
				System.out.println("Minimo tiene que introducir 4 respuestas, ¿cuantas quiere introduri?: ");
				veces=Integer.parseInt(Main.registro.nextLine());
				System.out.println();
				for(int i=0; i<veces; i++)
					
				
				
			}else{
				
				introducirPreg(categ, dif);
				
			}
			
				
		}catch(NumberFormatException n){
			
			System.out.println("Se esperaba un numero.");
			this.eliminarJug();
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar el usuario en la base de datos");
			Main.menuPrincipal();
			
		}
	}
	
	public void eliminarPreg(){
		System.out.println("En desarrollo.");
	}
	
	public void modificarPreg(){
		System.out.println("En desarrollo.");
	}
	
	public static void introducirPreg(int categ, int dif){
		
		String pregunta, respuesta;
		int id_resp=0;
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			System.out.println("Pregunta que deseas introducir: ");
			pregunta=Main.registro.nextLine();
			System.out.println("Respuesta de la pregunta introducida: ");
			respuesta=Main.registro.nextLine();
			
			st.executeUpdate("INSERT INTO respuesta (resp, id_cat) "
							+ "VALUES ('" + respuesta + "', " + categ + ");", Statement.RETURN_GENERATED_KEYS);
			
			ResultSet resultado = st.getGeneratedKeys();
			
			while(resultado.next())
				id_resp=resultado.getInt(1);
			
			st.executeUpdate("INSERT INTO pregunta (preg, id_resp, id_cat, id_dif) "
							+ "VALUES ('" + pregunta + "', " + id_resp + ", " + categ + ", " + dif + ");");
			
		}catch(Exception e){
			
			System.out.println("Error al introducir pregunta");
			
		};
		
	}
	
	public static void introducirRespuesta(int cat)
	
	SUSPENDIDO>
	*/
	
}
