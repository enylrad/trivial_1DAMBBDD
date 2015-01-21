package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.mysql.jdbc.Statement;

public class Dificultad {
	
	private int id;
	private String dificultad;

	public Dificultad() {
		
	}

	/**
	 * Cinstructor para asignar la dificultad de la partida, buscando en la base de datos.
	 * @param dificultad
	 */
	public Dificultad(String dificultad) {
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			ResultSet resultado = st.executeQuery("SELECT * "
					+ "FROM dificultad "
					+ "WHERE dificultad LIKE '" + dificultad + "';");
						
			while(resultado.next()){
				
				this.id=resultado.getInt(1);
				this.dificultad=resultado.getString(2);
				
			}
			
			resultado.close();
			st.close();
			conexion.close();
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar la dificultad");
			Main.menuPrincipal();
			
		}
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDificultad() {
		return dificultad;
	}

	public void setDificultad(String dificultad) {
		this.dificultad = dificultad;
	}
	
}
