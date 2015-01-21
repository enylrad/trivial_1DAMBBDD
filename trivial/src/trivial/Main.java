package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mysql.jdbc.Statement;

public class Main {
	
	public static Scanner registro = new Scanner(System.in); //Para registrar lo que escribimos en el programa

	public static void main(String[] args) {
		
		menuPrincipal(); //Llamamos al menu principal
		
	}
	
	/**
	 * Menu Principal con todas las opciones del menu para nuestro juego.
	 */
	public static void menuPrincipal(){
			
		int opcion=-1;
		
		do{
			
			try{
				System.out.println("\n===========");
				System.out.println("= TRIVIAL =");
				System.out.println("===========");
				System.out.println("1.-Jugar");
				System.out.println("2.-Estadisticas");
				System.out.println("3.-Ranking");
				System.out.println("4.-Admin");
				System.out.println("0.-Salir");
				System.out.println("===========");
				System.out.print("Introduce una opción: ");
				opcion=Integer.parseInt(registro.nextLine());
				
				switch (opcion) {
				
					case 1:
						Jugar(); //Metodo para empezar una partida nueva
						break;
						
					case 2:
						estadisticasJugador(); //Metodo para mostrar las estadisticas de un jugador en los diferentes temas
						break;
						
					case 3:
						rankingJugadores(); //Ranking de partidas jugadas
						break;
						
					case 4:
						menuAdmin(); //Menu del administrador donde administrar el juego
						break;
						
					case 0:
						System.out.println("Salio del programa.");
						break;
	
					default:
						System.out.println("Opción no valida.");
						break;
				}
				
			}catch(NumberFormatException e){
				
				System.out.println("Se esperaba un número.");
				opcion=-1;
			}
			
		}while(opcion!=0);
		
	}
	
	/**
	 * Menu donde iniciaremos una partida nueva.
	 */
	public static void Jugar(){
		
		Partida partida = new Partida(); //Creamos la partida
		partida.nuevaPartida(); //Vamos al metodo para iniciar la partida
		
	}
	
	/**
	 * Metodo para mostrar las estadisticas de un jugador
	 */
	public static void estadisticasJugador(){
		
		Jugador jug = new Jugador(); //Iniciamos el jugador
		jug.logear(); //Logeamos
		
		//Assiganamos los valores con los metodos corresponditentes
		int jugadas=jug.partidasJugadas(), 
			ganadas=jug.getGanadas(), 
			respondidas=jug.preguntasJugadas(), 
			correctas=jug.preguntasCorrectas();
		
		//Diferentes estadisticas
		System.out.println("\nESTADISTICAS");
		System.out.println("Nombre: " + jug.getNombre());
		System.out.println("Correo: " + jug.getCorreo());
		System.out.println("Jugadas: " + jugadas);		
		System.out.println("Ganadas: " + ganadas + "(" + ganadasPorcentaje(ganadas, jugadas) + "%)");
		System.out.println("Perdidas: " + (jugadas-ganadas) + "(" + perdidasPorcentaje(ganadas, jugadas) + "%)");
		System.out.println("Preguntas respondidas: " + respondidas);
		System.out.println("Preguntas correctas: " + correctas + "(" + ganadasPorcentaje(correctas, respondidas) + "%)");
		System.out.println("Preguntas incorrectas: " + (respondidas-correctas) + "(" + perdidasPorcentaje(correctas, respondidas) + "%)");
		System.out.println();
		menuPrincipal();
		
	}	
	
	/**
	 * Metodo para mostrar el Ranking de jugadores
	 */
	public static void rankingJugadores(){
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
	
			//Consulta para buscar los jugadores y ordenarlos de mayor a menor teniendo en cuenta sus partidas ganadas
			ResultSet resultado = st.executeQuery("SELECT u.nombre, j.ganadas "
												+ "FROM jugador j, usuario u "
												+ "WHERE u.id = j.id "
												+ "ORDER BY ganadas DESC LIMIT 10;");
			
			//Mostramos los jugadores
			System.out.println("\nRanking:");
			System.out.println("Jugador				Ganadas");
			
			while(resultado.next()){
				
				System.out.println(resultado.getString(1) + "				" + resultado.getInt(2));
		
			}
			System.out.println();
				
			resultado.close();		
			st.close();
			conexion.close();
	
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar el ranking");
			Main.menuPrincipal();

		}
	}
	
	/**
	 * Menu para el Administrador
	 */
	public static void menuAdmin(){
		
		Admin adm = new Admin(); //Creamos el administrador
		
		if(adm.logear()){ //Logemoas, si lo conseguimos entraremos al menu
			
			int opcion=-1;
			
			do{
				
				try{
					
					System.out.println("\nMENU ADMINISTRACIÓN");
					System.out.println("1-Añadir jugador.");
					System.out.println("2-Borrar jugador.");
					System.out.println("3-Editar jugador.");
					/* SUSPENDIDO
					System.out.println("4-Añadir pregunta.");
					System.out.println("5-Eliminar pregutna.");
					System.out.println("6-Editar pregunta.");
					*/
					System.out.println("0-Volver al Menu");
					System.out.print("Introduce una opción: ");
					opcion=Integer.parseInt(registro.nextLine());
					
					switch (opcion) {
						case 1:
							adm.añadirJug(); //Metodo para añadir un jugador
							break;
						case 2:
							adm.eliminarJug(); //Metodo para eliminar un jugador
							break;
						case 3:
							adm.modificarJug(); //Metodo para modificar un jugador
							break;
						/* <SUSPENDIDO
						case 4:
							adm.añadirPreg(); //Metodo para añadir preguntas
							break;
						case 5:
							adm.eliminarPreg(); //Metodo para eliminar preguntas
							break;
						case 6:
							adm.modificarPreg(); //Metodo para modificar preguntas
							break;
						 */
						case 0:
							menuPrincipal(); //Volvemos al menu principal
							break;
		
						default:
							System.out.println("Opción no valida.");
							break;
					}
					
				}catch(NumberFormatException e){
					
					System.out.println("Se esperaba un número.");
					opcion=-1;
					
				}
				
			}while(opcion!=0);
			
		}else{ //Sino nos indicara que no es un usuario valido y volveremos al menu
			
			System.out.println("Usuario no valido.");
			menuPrincipal();
			
		}
				
	}
	
	/**
	 * Metodo para agregar los resultados de una pregunta a la base de datos
	 * @param preg Pregunta de la base de datos
	 * @param jug Jugador en la base de datos
	 * @param part Partida en la base de datos
	 * @param resul Si fue correcta o incorrecta la pregunta
	 */
	public static void resultadoBBDD(Pregunta preg, Jugador jug, Partida part, boolean resul){
		
		try{
			
			//Datos necesarios para la base de datos
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			int resultado;
			
			//Si es correcta la asignara el valor 1, sino el valor 0
			if(resul)
				resultado=1;
		
			else
				resultado=0;
		
			//Insertamos los datos
			st.executeUpdate("INSERT INTO preguntajugada (id_jug, id_par, id_preg, correcta) "
						+ "VALUES (" + jug.getId() + ", " + part.getId() + ", " + preg.getId() + ", " + resultado + ");"); 
			
			st.close();
			conexion.close();
		
		}catch(Exception n){
			
			System.out.println("Se produjo un errora al insertar los datos de partidajugada");
			Main.menuPrincipal();
			
		}
		
	}
	
	/**
	 * Metodo para respuesta si/no
	 * @param pregunta pregunta que realizaremos
	 * @return devolveremos si es correcta o no.
	 */
	public static boolean preguntarSiNo(String pregunta){
		
		boolean correcto=true;
		String respuesta;
		
		do{
			
			System.out.print(pregunta);
			respuesta=registro.nextLine();
		
			if(Character.toString(respuesta.charAt(0)).equalsIgnoreCase("S"))
				return true;
				
			else if(Character.toString(respuesta.charAt(0)).equalsIgnoreCase("N"))
				return false;
			
			else{
				
				System.out.println("Respuesta no válida, responda de nuevo por favor: ");
				correcto=false;
				
			}
			
		}while(!correcto);//Sino es una respuesta valida volvera a preguntar
		
		return true;
		
	}
	
	private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/**
	 * Metodo para comprobar Email, buscado en internet.
	 * @param email
	 * @return
	 */
	public static boolean comprobarEmail(String email){
		
		 // Compiles the given regular expression into a pattern.
	    Pattern pattern = Pattern.compile(PATTERN_EMAIL);
	
	    // Match the given input against this pattern
	    Matcher matcher = pattern.matcher(email);
	    return matcher.matches();
		
	}

	/**
	 * Metodo para comprobar si la contraseña es correcta
	 * @param pass
	 * @return
	 */
	public static boolean comprobarPass(String pass){
		
		int contMay=0, contMin=0, contNum=0, contSim=0;
		
		if(pass.length() >= 8 && pass.length() <= 22){
			
			//For para recorrer la palabra
			for(int i=0; i<pass.length(); i++){
				
				//Comprobamos si tiene simbolor muy raros.
				if(pass.charAt(i) > '\u00A0'){
					System.out.println("La contraseña contiene algun caracter no valido.");
					return false;
				}
				
				//Contamos los numeros
				if(pass.charAt(i) > '\u002F' && pass.charAt(i) < '\u003A')
					contNum++;
				
				//Contamos las mayusculas
				if(pass.charAt(i) > '\u0040' && pass.charAt(i) < '\u005B')
					contMay++;
				
				//Contamos las minusculas
				if(pass.charAt(i) > '\u0060' && pass.charAt(i) < '\u007B')
					contMin++;
				
				//Contamos los simbolos
				if(pass.charAt(i) > '\u0020' && pass.charAt(i) < '\u0030'
						|| pass.charAt(i) > '\u0039' && pass.charAt(i) < '\u0041'
						|| pass.charAt(i) > '\u005A' && pass.charAt(i) < '\u0061'
						|| pass.charAt(i) > '\u007A' && pass.charAt(i) < '\u007F')
					contSim++;
					
			}
			
			if(contMay>0 && contMin>0 && contNum > 0 && contSim > 0)
				return true;
			
			else{
				
				System.out.println("La contraseña no tiene:");
				
				if(contMay == 0 )
					System.out.println("-Mayusculas");
				if(contMin == 0 )
					System.out.println("-Minusculas");
				if(contNum == 0 )
					System.out.println("-Numeros");
				if(contSim == 0)
					System.out.println("-Simbolos");

				return false;				
			}
				
		}else if(pass.length() < 8){
			
			System.out.println("La contraseña es demasiado pequeña.");
			return false;
			
		}else if(pass.length() > 22){
			
			System.out.println("La contraseña es demasiado grande.");
			return false;
		}

		System.out.println("No es correcta.");
		return false;
		
	}
	
	public static int ganadasPorcentaje(int bien, int total){
		
		try{
			
			return (bien*100)/total;
			
		}catch(ArithmeticException ex){
			
			return 0;
		}
		
	}
	
	public static int perdidasPorcentaje(int bien, int total){
		
		try{
			
			return ((total-bien)*100)/total;
			
		}catch(ArithmeticException ex){
			
			return 0;
		}
		
	}
	
}
