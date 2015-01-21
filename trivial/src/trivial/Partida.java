package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

public class Partida {

	private int id;
	private String nombre;
	private boolean ganada;
	private ArrayList<Jugador> jugadores;
	private final int jug_max=6, jug_min=2;
	
	public Partida() {
	
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isGanada() {
		return ganada;
	}

	public void setGanada(boolean ganada) {
		this.ganada = ganada;
	}

	public ArrayList<Jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(ArrayList<Jugador> jugadores) {
		this.jugadores = jugadores;
	}

	public int getJug_max() {
		return jug_max;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Metodo que crea una partida
	 */
	public void nuevaPartida(){ //Revisar
	
		String dif="";
		int turno, 
			num_j=jug_min, 
			opcion=-1;

		System.out.print("Nombre de la partida: ");
		this.nombre=Main.registro.nextLine();
		
		num_j=this.numeroJugadores(num_j);
		
		this.jugadores= new ArrayList<Jugador>();
		
		this.crearJugadores(num_j);
			
		do{
			
			try{
				
				System.out.println("\nDIFICULTAD");
				System.out.println("1.-Facil");
				System.out.println("2.-Normal");
				System.out.print("Introduce una opción: ");
				opcion=Integer.parseInt(Main.registro.nextLine());
				
				switch (opcion) {
			
					case 1:
						dif="facil";
						break;
					
					case 2:
						dif ="facil"; //Puesto en facil pq no hay mas dificultades actualmente
						break;
	
					default:
						System.out.println("Opción no valida.");
						break;
						
				}
				
			}catch(NumberFormatException e){
				
				System.out.println("Se esperaba un número.");
				opcion=-1;
				
			}
			
		}while(opcion < 1 || opcion > 2);
		
		this.id=añadirPartBD();
		
		turno=(int) (Math.random()*this.jugadores.size());
		
		do{
			
			if(turno>=this.jugadores.size())
				turno=0;
			
			this.jugadores.get(turno).hacerPreguntas(this, dif);
			turno++;
			
		}while(this.isGanada() == false);
	
	}
	
	/**
	 * Metodo para asignar el numero de juadores.
	 * @param num_j
	 * @return
	 */
	public int numeroJugadores(int num_j){
		
		boolean correcto=true;
		
		do{
			
			try{
				
				if(!correcto)
					System.out.println("Introduzca el dato de nuevo por favor...");
				
				System.out.print("Numero de jugadores: ");
				num_j=Integer.parseInt(Main.registro.nextLine());
				
				if(num_j > jug_max){
					
					System.out.println("¡El máximo de jugadores es " + jug_max + "!");
					correcto=false;
					
				}
				
				if(num_j <= jug_min-1){
					
					System.out.println("¡El mínimo es " + jug_min +"!");
					correcto=false;
					
				}else
					correcto=true;
				
			}catch(NumberFormatException e){
				
				System.out.println("Se esperaba un número.");	
				correcto=false;
	
			}
				
		}while(!correcto);
		
		return num_j;
		
	}
	
	/**
	 * Metodo para crear los Jugadores de la partida.
	 * @param num_j
	 */
	public void crearJugadores(int num_j){
		
		for(int i=0; i<num_j; i++){
			
			System.out.println("JUGADOR " + (i+1));
			
			if(this.comprobarJugadoresLog()){ //Comprobamos que no esten todos los jugadores de la base de datos en la partida
				
				Jugador jug = new Jugador(); //Inicializamos el jugador
				jug.crearUsuario();	//Utilizamos el metodo para crear el usuario
				this.jugadores.add(jug); //Lo añadimos a la partida
				
			}else if(Main.preguntarSiNo("¿Jugador nuevo?(Si/No): ")){ //Preguntamos si es un jugador nuevo no existente
				
				Jugador jug = new Jugador();
				jug.crearUsuario();
				this.jugadores.add(jug);
				
			}else{
				
				Jugador jug = new Jugador();
				jug.logear(this); //Nos identificamos en la partida
				this.jugadores.add(jug);
			}
				
		}
		
	}
	
	/**
	 * Metodo para añadir la partida a la base de datos
	 * @return devolvera el id de la partida.
	 */
	public int añadirPartBD(){
		
		int id_part=-1;
		
		try{
			
			//Datos necesarios para la base de datos
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
		
		
			 st.executeUpdate("INSERT INTO partida (nombre) "
						+ "VALUES('" + this.nombre + "');", Statement.RETURN_GENERATED_KEYS);
			
			ResultSet rs =st.getGeneratedKeys();
			
			if(rs.next())
				id_part= rs.getInt(1);
			
			st.close();
			conexion.close();
			
			return id_part;
				
		}catch(Exception n){
			
			System.out.println("Se produjo un error al insertar la partida a la BD.");
			return id_part;
			
		}
		
	}
	
	/**
	 * Metodo para comprobar que no se han identificado ya todos los usuarios de la base de datos
	 * @return si ya estan identificados todos los usuarios de la base de datos o no
	 */
	public boolean comprobarJugadoresLog(){
		
		ArrayList<Usuario> jugadores = new ArrayList<>();
		int cont=0;
		
		try{
					
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			ResultSet resultado = st.executeQuery("SELECT u.id, u.nombre, u.login, u.correo "
												+ "FROM usuario u, jugador j "
												+ "WHERE u.id = j.id;");
				
			while(resultado.next()){
					
				//Añadimos a un array todos los jugadores de la base de datos
				jugadores.add(new Usuario(resultado.getString(2),resultado.getString(3), resultado.getString(4)));
					
			}
				
			resultado.close();			
			
		
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar el usuario en la base de datos");
			Main.menuPrincipal();
			
		}
		
		for(int i=0; i<this.jugadores.size(); i++)
			for(int j=0; j<jugadores.size(); j++)
				if(this.jugadores.get(i).getLogin().equals(jugadores.get(j).getLogin())) //Comparamos los jugadores de la base de datos con los de la partida
					cont++;
				
		if(cont == 0 && this.jugadores.size()>0){ //Si no se han encontrado coincidencias y el tamaño de la partida es mayor de 0
			
			System.out.println("No hay jugadores en la base de datos, debe crear uno nuevo."); //No hay jugadores disponibles en la base de datos, se debera crear uno nuevo
			return true;
			
		}else if(cont == jugadores.size()){
			
			System.out.println("Todos los usuarios registrados estan en la partida, debes crear uno nuevo"); //Todos los jugadores que estan en la partida estan en la basse de datos, se deberan crear nuevos
			return true;
			
		}else
			return false; //Sino todo correcto y retorna false
					
	}
	
}
