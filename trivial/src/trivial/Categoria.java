package trivial;

import java.util.ArrayList;

public class Categoria {

	private int id;
	private String nombre;
	private ArrayList<Respuesta> respuestas;
	
	public Categoria() {
		
		this.respuestas=new ArrayList<>();
	}	
	
	public Categoria(int id, String nombre){
		
		this.id=id;
		this.nombre=nombre;
		this.respuestas = new ArrayList<Respuesta>();
	}
	
	public Categoria(String nombre, ArrayList<Respuesta> respuestas) {
		
		this.nombre = nombre;
		this.respuestas = respuestas;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public ArrayList<Respuesta> getRespuestas() {
		return respuestas;
	}
	
	public void setRespuestas(ArrayList<Respuesta> respuestas) {
		this.respuestas = respuestas;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
