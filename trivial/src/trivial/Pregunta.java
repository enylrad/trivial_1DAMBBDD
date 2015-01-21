package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Statement;

public class Pregunta {

	private int id;
	private String pregunta;
	private Respuesta resp;
	private Categoria cat;
	private Dificultad dif;
	
	public Pregunta(){
		
	}

	public Pregunta(String pregunta, Respuesta resp, Categoria cat, Dificultad dif) {
		
		this.pregunta = pregunta;
		this.resp = resp;
		this.cat = cat;
		this.dif = dif;
		
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	public Respuesta getResp() {
		return resp;
	}

	public void setResp(Respuesta resp) {
		this.resp = resp;
	}

	public Categoria getCat() {
		return cat;
	}

	public void setCat(Categoria cat) {
		this.cat = cat;
	}

	public Dificultad getDif() {
		return dif;
	}

	public void setDif(Dificultad dif) {
		this.dif = dif;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void asignarDificultad(String dif) {
		
		this.dif= new Dificultad(dif);
		this.cat= new Categoria();
		
	}

	/**
	 * Metodo para seleccionar la pregunta segun el tema y sus respuestas correspondientes.
	 * @param dados
	 * @param jug
	 * @param tema
	 * @return
	 */
	public boolean mostrarPregunta(int dados, Jugador jug, int tema, Partida part){
			
		int cat=jug.getQuesitos()[tema].categoriaAle(),//Elegr una categoria aleatoria
		    resp=0,
		    corr=(int) (Math.random()*(3-0+1)+0);
		
		this.buscarPreguntas(jug, tema, cat); //Buscar pregunta en base de datos con la categoria añadida falta desarrolar
		
		this.resp.buscarRespuestasF(jug, tema, cat); //Buscar respuestas falsas
		
		//Mostrara la preguna
		System.out.println("\nPregunta:" + this.pregunta);
				
		jug.getQuesitos()[tema].getCategorias().get(cat).getRespuestas().add(corr, this.resp);
				
		//Se mostrarán las respuestas
		for(int j=0; j<4; j++){
								
			System.out.println((j+1) + "-" + jug.getQuesitos()[tema].getCategorias().get(cat).getRespuestas().get(j).getRespuesta());
					
		}
				
		//Sumamos 1 a la correcta para que coincida con la opcion seleccionada.
		corr++;
		boolean valido=true;		
		
		do{
					
			try{
				valido=true;
				System.out.print("¿Cual es la respuesta correcta?: ");
				resp=Integer.parseInt(Main.registro.nextLine());
				
			}catch(NumberFormatException n){
				
				System.out.println("Se esperaba un número.");
				valido=false;
			}
		
		}while(!valido);
		
		for(int i=0; i<4; i++)
			jug.getQuesitos()[tema].getCategorias().get(cat).getRespuestas().remove(0); //Eliminamos las respuestas del array
		
		//POSIBLES RESPUETAS DEPENDIENDO SI SON CORRECTAS O NO
		if(dados > 4){
			
			if(corr == resp && jug.getQuesitos()[tema].isConseguido()){
				
				System.out.println("Ya tenias el quesito por lo que no tiene premio :P.");
				Main.resultadoBBDD(this, jug, part, true); //Actualizamos la base de datos
				return true;
				
			}else if(corr == resp){
				
				System.out.println("¡Enhorabuena has conseguido un quesito!");
				jug.ganarQuesito(jug.getQuesitos()[tema]);
				Main.resultadoBBDD(this, jug, part, true); //Actualizamos la base de datos
				return true;
				
			}else {
				
				System.out.println("La respuesta era " + this.resp.getRespuesta() + ", no has ganado un quesito :(");
				Main.resultadoBBDD(this, jug, part, false); //Actualizamos la base de datos
				return false;
			}
			
		}else if(dados == 1){
			
			if(corr != resp && !jug.getQuesitos()[tema].isConseguido()){
				
				System.out.println("No tenias el quesito, por lo que solo pierdes el turno :).");
				Main.resultadoBBDD(this, jug, part, false); //Actualizamos la base de datos
				return false;
				
			}else if(corr != resp){
				
				System.out.println("Ohh... has perdido un quesito :(");
				System.out.println("La respuesta era " + this.resp.getRespuesta() + " :(");
				jug.perderQuesito(jug.getQuesitos()[tema]);
				Main.resultadoBBDD(this, jug, part, false); //Actualizamos la base de datos
				return false;
				
			}else if(corr == resp && jug.getQuesitos()[tema].isConseguido()){
				
				System.out.println("¡Respuesta correcta puedes seguir jugando!, te has salvado de perder un quesito :)");
				Main.resultadoBBDD(this, jug, part, true); //Actualizamos la base de datos
				return true;
				
			}else{
				
				System.out.println("¡Respuesta correcta puedes seguir jugando!");
				Main.resultadoBBDD(this, jug, part, true); //Actualizamos la base de datos
				return true;
				
			}
			
		}else if(corr == resp){
			
			System.out.println("¡Respuesta correcta puedes seguir jugando!");
			Main.resultadoBBDD(this, jug, part, true); //Actualizamos la base de datos
			return true;
			
		}else{	
			
			System.out.println("La respuesta era " + this.resp.getRespuesta() + " :(");
			Main.resultadoBBDD(this, jug, part, true); //Actualizamos la base de datos
			return false;
			
		}
		
	}
	
	/**
	 * Metodo para buscar una pregunta para un jugador, con el tema y categoria seleccionada
	 * @param jug
	 * @param tema
	 * @param cat
	 */
	public void buscarPreguntas(Jugador jug, int tema, int cat){
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
									
			ResultSet resultado = st.executeQuery("SELECT p.id, r.id, p.preg, r.resp "
					+ "FROM pregunta p, respuesta r "
					+ "WHERE p.id_resp = r.id " 
						+ "AND p.id_cat =" + jug.getQuesitos()[tema].getCategorias().get(cat).getId() + " "
						+ "AND p.id_dif =" + this.dif.getId() + " "
					+ "ORDER BY RAND() LIMIT 1;");
			
			while(resultado.next()){
				
				//Asignamos la pregunta
				this.id=resultado.getInt(1);
				this.pregunta=resultado.getString(3);
				this.resp= new Respuesta(resultado.getInt(2), resultado.getString(4));				
				
			}
			
			resultado.close();
			st.close();
			conexion.close();
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar la pregunta en la base de datos");
			
		}	
		
	}	
	
}
