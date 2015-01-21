package trivial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

public class Quesito {

	private int id;
	private String nombre;
	private ArrayList<Categoria> categorias;
	private boolean conseguido;
	private String color;

	/**
	 * Cosntructor para crear los quesitos con las diferentes categorias
	 * @param id de la categoria
	 */
	public Quesito(int id) {
	
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			ResultSet resultado = st.executeQuery("SELECT * "
					+ "FROM tema "
					+ "WHERE id = " + id + ";");
			
			while(resultado.next()){
				
				this.id=resultado.getInt(1); 
				this.nombre=resultado.getString(2);
				this.categorias=new ArrayList<>();				
				this.conseguido=false;
				this.color=resultado.getString(3);
				
			}
			
			resultado.close();
			st.close();
			conexion.close();
			
		}catch(Exception n){
			
			System.out.println("Se produjo un error al buscar el quesito/tema en la base de datos");
			
		}	
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost/trivial", "root", "");
			Statement st = (Statement) conexion.createStatement();
			
			ResultSet rs = st.executeQuery("SELECT * "
											+ "FROM categoria "
											+ "WHERE id_tema = " + this.id + ";");
			
			while(rs.next()){
				
				this.categorias.add(new Categoria(rs.getInt(1), rs.getString(2)));
				
			}
			rs.close();
			st.close();
			conexion.close();
			
		}catch(Exception e){
			
			System.out.println("Se produjo un error al buscar la categoria en la base de datos");
			
		}
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public ArrayList<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(ArrayList<Categoria> categorias) {
		this.categorias = categorias;
	}

	public boolean isConseguido() {
		return conseguido;
	}

	public void setConseguido(boolean conseguido) {
		this.conseguido = conseguido;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void quesitoConseguido() {
		
		this.conseguido=true;
	}
	
	public void quesitoPerido(){
		this.conseguido=false;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Quesito other = (Quesito) obj;
		
		if (color == null) {
			
			if (other.color != null)
				return false;
			
		} else if (!color.equals(other.color))
			return false;
		
		if (id != other.id)
			return false;
		
		if (nombre == null) {
			
			if (other.nombre != null)
				return false;
			
		} else if (!nombre.equals(other.nombre))
			return false;
		
		return true;
	}
	
	/***
	 * Metodo para buscar una categoria aleatroria;
	 * @return retorna la posición de la categoria.
	 */
	public int categoriaAle(){
		
		return (int) (Math.random()*this.categorias.size());
		
	}
			
}
