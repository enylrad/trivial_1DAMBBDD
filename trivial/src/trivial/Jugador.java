package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Statement;


public class Jugador extends Usuario {

	private int ganadas;
	private Quesito[] quesitos;

	public Jugador(){
		super();
		
		this.quesitos= new Quesito[6];
		this.ganadas = 0;
		
		for(int i=0; i<this.quesitos.length; i++){
			this.quesitos[i] = new Quesito(i+1);
		}
			
	}

	public Quesito[] getQuesitos() {
		return quesitos;
	}

	public void setQuesitos(Quesito[] quesitos) {
		this.quesitos = quesitos;
	}

	public int getGanadas() {
		return ganadas;
	}

	public void setGanadas(int ganadas) {
		this.ganadas = ganadas;
	}

	public boolean comprobarQuesitos(){
		
		int cont=0;
		
		for(int i=0; i<this.quesitos.length; i++)
			if(this.quesitos[i].isConseguido() == true)
				cont++;
		
		System.out.println("Tienes " + cont + " quesitos :).");
		
		if(cont == 6){
			
			System.out.println("\n******************************");
			System.out.println("*******¡¡Has ganado!!*********");
			System.out.println("******************************\n");
			return true;
			
		}else
			return false;

	}
	
	public void ganarQuesito(Quesito tema){
		
		for(int i=0; i<this.quesitos.length; i++)
			if(this.quesitos[i].equals(tema))
				this.quesitos[i].setConseguido(true);
		
	}
	
	public void perderQuesito(Quesito tema){
		
		for(int i=0; i<this.quesitos.length; i++)
			if(this.quesitos[i].equals(tema))
				this.quesitos[i].setConseguido(false);
		
	}
	
	/**
	 * Metodo para crear un jugador
	 */
	@Override
	public void crearUsuario(){
		super.crearUsuario();
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			//Lo introducimos en la base de datos de jugador
			st.executeUpdate("INSERT INTO jugador (id, ganadas) "
					+ "VALUES('" + this.id + "','" + this.ganadas + "');");
			
			st.close();
			conexion.close();
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al insertar el jugador en la base de datos");
			
		}
	
		
	}
	
	/**
	 * Metodo para logear un jugador
	 */
	public void logear(){
		
		boolean valido=true;
		
		do{
			
			try{
				
				String usuario, pass, nombre="", correo="";
				int id=-1, ganadas=0;//inicializamos el id en -1
				boolean existe;
				
				Class.forName("com.mysql.jdbc.Driver");
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
				Statement st = (Statement) conexion.createStatement();
				
				do{
					
					existe=false;
					
					//Pedimos los datos para idintificarse
					System.out.print("Usuario: ");
					usuario=Main.registro.nextLine();
					System.out.print("Password: ");
					pass=Main.registro.nextLine();
					
					//Hacemos la consulta
					ResultSet resultado = st.executeQuery("SELECT * "
							+ "FROM usuario u, jugador j "
							+ "WHERE u.id = j.id "
								+ "AND login LIKE '" + usuario + "' "
								+ "AND pass LIKE MD5('" + pass + "');");
	
					while(resultado.next()){
						//Asignamos los valores provisionales
						id=resultado.getInt(1); 
						nombre=resultado.getString(2);
						correo=resultado.getString(5);
						ganadas=resultado.getInt(7);
						
					}
					
					st.close();
					resultado.close();	
					conexion.close();
					
				}while(existe);
				
				if(id!=-1){ //Si es diferente de -1, los datos se han encontrado
				
					this.id=id;
					this.nombre=nombre;
					this.login=usuario;
					this.pass=pass;
					this.correo=correo;
					this.ganadas=ganadas;
					valido=true;
					
				}else{ //Si no se encuentran coincidencias.
					
					System.out.println("El jugador no se ha encontrado,"); //Lo indicamos
					
					if(Main.preguntarSiNo("¿Quieres seguir logeandote?(Si/No): ")) //Y preguntamos si quiere crear uno nuevo, por si no sabe la identificacion de uno  de los existenets.	
						valido=false;
					
					else{
						
						this.crearUsuario();
						valido=true;
						
					}	
						
				}
				
			}catch(Exception n){
				
				System.out.println("Se produjo un error al buscar el usuario en la base de datos");
				Main.menuPrincipal();
				
			}
			
		}while(!valido);
		
	}
	
	/**
	 * Metodo para logear con un usuario en una partida.
	 * @param part
	 */
	public void logear(Partida part){
		
		boolean valido=true;
		
		do{
			
			try{
				
				String usuario, pass, nombre="", correo="";
				int id=-1, ganadas=0;//inicializamos el id en -1
				boolean existe;
				
				Class.forName("com.mysql.jdbc.Driver");
				Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
				Statement st = (Statement) conexion.createStatement();
				
				do{
					existe=false;

					//Pedimos los datos para identificarse
					System.out.print("Usuario: ");
					usuario=Main.registro.nextLine();
					System.out.print("Password: ");
					pass=Main.registro.nextLine();
					
					//Realizamos la consulta
					ResultSet resultado = st.executeQuery("SELECT * "
							+ "FROM usuario u, jugador j "
							+ "WHERE u.id = j.id "
								+ "AND login LIKE '" + usuario + "' "
								+ "AND pass LIKE MD5('" + pass + "');");
				
					while(resultado.next()){
						
						id=resultado.getInt(1); 
						nombre=resultado.getString(2);
						correo=resultado.getString(5);
						ganadas=resultado.getInt(7);
						
					}
					
					resultado.close();
					
					//Si la consulta coincide con alguno de los que esta en el Array de la partida indicamos que no es posible seguir
					for(int i=0; i<part.getJugadores().size(); i++)
						if(id == part.getJugadores().get(i).getId()){
							
							existe=true;
							System.out.println("El usuario ya esta en la partida.");
							
						}
											
				}while(existe);
				
				st.close();
				conexion.close();
				
				if(id!=-1){ //Si es diferente de -1, los datos se han encontrado
				
					this.id=id;
					this.nombre=nombre;
					this.login=usuario;
					this.pass=pass;
					this.correo=correo;
					this.ganadas=ganadas;
					valido=true;
						
				}else{ //Si no se encuentran coincidencias. 
					
					System.out.println("El jugador no se ha encontrado,"); //Lo indicamos
					
					if(Main.preguntarSiNo("¿Quieres seguir logeandote?(Si/No): ")) //Y preguntamos si quiere crear uno nuevo, por si no sabe la identificacion de uno  de los existenets.	
						valido=false;	
					else{
						this.crearUsuario();
						valido=true;
					}
					
					st.close();
					conexion.close();
						
				}
				
			}catch(Exception n){
				
				System.out.println("Se produjo un error.");
				Main.menuPrincipal();
				
			}
			
		}while(!valido);
		
	}
	
	/**
	 * Metodo para la realización de las preguntas
	 * @param partida parametro de la partida que se esta jugando
	 * @param dif dificultad de la partida
	 */
	public void hacerPreguntas(Partida partida, String dif){
		
		int dados, tema=1;
		boolean seguir=false;
		
		System.out.println("\n¡Turno de " + this.getNombre() + "!");
			
		do{
			//Creamos la pregunta
			Pregunta preg= new Pregunta();
			preg.asignarDificultad(dif); //Asignamos su dificultad
			
			System.out.println("\n¡Tira los dados!");
			Main.registro.nextLine();
			
			dados=(int) (Math.random()*(6-1+1)+1); //Numero aleatorio para los dados
			
			System.out.println("¡Has sacado un " + dados + "!");
			
			//Mensaje segun los dados que hayamos sacado
			if(dados > 4) //Si son mayor de 4 nos dara quesito si respondemos bien, es 1 se nos restara
				System.out.println("¡Si respondes esta pregunta bien ganaras un quesito!");
			else if(dados == 1)
				System.out.println("¡Si respondes mal esta pregunta perderas un quesito!");
			
			tema=this.elegirTema();
			
			seguir=preg.mostrarPregunta(dados, this, tema, partida); //Mostramos la pregunta
			
			if(comprobarQuesitos()){ //Comprobamos los quesitos, si son 6 se gana la partida y se introducira en la base de datos.
				
					this.actualizarGP();
					Main.menuPrincipal();
				
			}
			
		}while(seguir);
		
	}
	
	/**
	 * Metodo para elegir un tema
	 * @return
	 */
	public int elegirTema(){
		
		String conseguido="";
		int opcion=-1, tema=1;
		int tema1=(int) (Math.random()*this.quesitos.length), tema2; //tema 1 sera elegido al azar y inicializamos el tema 
						
		System.out.println("\n¡TEMAS!");
				
		if(this.quesitos[tema1].isConseguido())
			conseguido=", !Ya tienes el quesito :)!";
				
		System.out.println("1-" + this.quesitos[tema1].getNombre() + conseguido);
				
		do{ //Comprobamos que el tema 2 no sea igual al tema 1
				
			tema2=(int)  (Math.random()*this.quesitos.length); //revisar
					
		}while(tema1 == tema2);
				
		conseguido="";
				
		if(this.quesitos[tema2].isConseguido())
			conseguido=", !Ya tienes el quesito :)!";
				
		System.out.println("2-" + this.quesitos[tema2].getNombre() + conseguido);
		
		do{	
			
			try{
				
				System.out.print("¿Que tematica quieres?: ");
				opcion=Integer.parseInt(Main.registro.nextLine());
				
				switch (opcion) {
				
					case 1:
						tema= tema1;
						break;
						
					case 2:
						tema= tema2;
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
		
		return tema;
		
	}
	
	/**
	 * Este metodo retorna el numero jugadas que ha jugado un jugador.
	 * @param jug
	 * @return
	 */
	public int preguntasJugadas(){
		
		try{
			
			int part=0;
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
	
			ResultSet resultado = st.executeQuery("SELECT COUNT(*) "
												+ "FROM jugador j, preguntajugada pj "
												+ "WHERE j.id = pj.id_jug "
												+ "AND j.id =" + this.id + ";");
	
			while(resultado.next()){
				
				part= resultado.getInt(1);
		
			}
				
			resultado.close();		
			st.close();
			conexion.close();
			return part;
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar las preguntas respondidas");
			Main.menuPrincipal();
			return 0;
		}
		
	}
	
	/**
	 * Metodo que muestra las preguntas correctas
	 * @param jug
	 * @return
	 */
	public int preguntasCorrectas(){
		
		try{
			
			int part=0;
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
	
			ResultSet resultado = st.executeQuery("SELECT COUNT(*) "
												+ "FROM jugador j, preguntajugada pj "
												+ "WHERE j.id = pj.id_jug "
												+ "AND j.id =" + this.id + " "
												+ "AND pj.correcta;");
	
			while(resultado.next()){
				
				part= resultado.getInt(1);
		
			}
				
			resultado.close();		
			st.close();
			conexion.close();
			
			return part;
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar las preguntas correctas");
			Main.menuPrincipal();
			return 0;
			
		}
		
	}
	
	/**
	 * Metodo que retorna las partidas jugadas
	 * @return
	 */
	public  int partidasJugadas(){
		
		try{
			
			int part=0;
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
	
			ResultSet resultado = st.executeQuery("SELECT COUNT(*) "
												+ "FROM partida "
												+ "WHERE id IN (SELECT id_par "
																+ "FROM preguntajugada "
																+ "WHERE id_jug ="+ this.id + ");");
				
			while(resultado.next()){
				
				part= resultado.getInt(1);
		
			}
				
			resultado.close();		
			st.close();
			conexion.close();

			return part;
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar las partidas jugadas");
			Main.menuPrincipal();
			return 0;
			
		}
		
	}
	
	/**
	 * Metodo para actualizar las partidas ganadas de un jugador
	 */
	public void actualizarGP(){
		
		try{
			
			//Datos necesarios para la base de datos
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			st.executeUpdate("UPDATE jugador "
							+ "SET ganadas=" + (this.ganadas+1) + " "
							+ "WHERE id=" + this.id + ";");
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al actualizar a ganada.");
			Main.menuPrincipal();
			
		}
		
	}
	
}
