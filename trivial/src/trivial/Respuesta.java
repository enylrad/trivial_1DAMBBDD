package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Statement;

public class Respuesta {

	private int id;
	private String respuesta;	
	
	public Respuesta() {
		
	}

	public Respuesta(int id, String respuesta) {
		
		this.id = id;
		this.respuesta = respuesta;
	}
	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	/**
	 * Metodo para buscar una respuesta para un jugador, con el tema y categoria seleccionada
	 * @param jug
	 * @param tema
	 * @param cat
	 */
	public void buscarRespuestasF(Jugador jug, int tema, int cat){
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
				
			ResultSet resultado = st.executeQuery("SELECT r.id, r.resp "
					+ "FROM respuesta r, categoria c "
					+ "WHERE r.id_cat = c.id "
						+ "AND c.id = " + jug.getQuesitos()[tema].getCategorias().get(cat).getId() + " "
						+ "AND r.id != " + this.id + " "
					+ "ORDER BY RAND() LIMIT 3;");
			
				
			while(resultado.next()){
					
				jug.getQuesitos()[tema].getCategorias().get(cat).getRespuestas().add(new Respuesta(resultado.getInt(1), resultado.getString(2))); 
								
			}
			
			resultado.close();
			st.close();
			conexion.close();
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar las respuestas en la base de datos");
			Main.menuPrincipal();
				
		}
		
	}
	
	

}
