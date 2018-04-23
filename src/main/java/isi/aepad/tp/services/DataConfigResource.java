package isi.aepad.tp.services;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Map;

import isi.aepad.tp.modelo.Categoria;
import isi.aepad.tp.modelo.Producto;
import isi.aepad.tp.util.GeneradorDatos;
import isi.aepad.tp.util.InterceptorAcceso;

@Singleton
@Path("dataconfig")
@Dependent
@Interceptors(InterceptorAcceso.class)
public class DataConfigResource {

	@Inject
	private GeneradorDatos generador;
	
	@EJB
	private ProductoResource productoEJB;
	
	public static final String[] CATEGORIAS = { "autos", "electronica", "hogar", "tecnologia", "indumentaria",
			"deportes", "computacion", "audio", "camping", "pesca", "respuestos", "libros", "peliculas", "videos",
			"entretenimiento", "blanco", "ferreteria", "vinos", "bebidas", "video", "tv", "internet", "redes" };

	private List<Categoria> entidadesCategorias= new ArrayList<>();
	
	@PersistenceContext(unitName="AEPAD_PU")
	private EntityManager em;

	@GET
	@Path("inicializar1")
	@Produces(MediaType.APPLICATION_JSON)
	public Response inicializar1() {
		long millisInicio = System.currentTimeMillis();
		this.crearCategorias();
		this.crearProductos(500);
		long millisFin= System.currentTimeMillis();
		JsonObject model = Json.createObjectBuilder()
				   .add("MILLIS_INICIO", millisInicio)
				   .add("MILLIS_FIN", millisFin)
				   .add("Duracion", (millisFin-millisInicio))
				   .build();
		return Response.ok(model.toString()).build();
	}

	@GET
	@Path("inicializar2")
	@Produces(MediaType.APPLICATION_JSON)
	public Response inicializar2() {
		long millisInicio = System.currentTimeMillis();
		List<Categoria> cats= em.createQuery("SELECT c FROM Categoria c").getResultList();
		
		JsonObjectBuilder builderObj= Json.createObjectBuilder();
		builderObj.add("MILLIS_INICIO", millisInicio);
		
		JsonArrayBuilder builderArr = Json.createArrayBuilder();
		for(int i = 0;i<500;i++) {
			long millisAntes = System.currentTimeMillis();
			productoEJB.crearProductoRandom(cats);
			long millisDespues= System.currentTimeMillis();
			builderArr.add((millisDespues-millisAntes));
		}		
		long millisFin= System.currentTimeMillis();
		
		builderObj.add("MILLIS_FIN", millisFin);
		builderObj.add("Duracion", (millisFin-millisInicio));
		builderObj.add("detalle", builderArr.build());				   
		return Response.ok(builderObj.build().toString()).build();
	}

	@GET
	@Path("drop")
	@Produces(MediaType.APPLICATION_JSON)
	public Response destruir() {
		int productosBorrados = em.createQuery("DELETE FROM Producto p").executeUpdate();
		int categoriasBorradas = em.createQuery("DELETE FROM Categoria c").executeUpdate();
		JsonObject model = Json.createObjectBuilder()
				   .add("productosBorrados", productosBorrados)
				   .add("categoriasBorradas", categoriasBorradas)				   
				   .build();
		return Response.ok(model.toString()).build();
	}

	public Response backup() {
		return Response.ok().build();
	}

	private void crearCategorias() {		
//		Random r = new Random();
//		int indice = r.nextInt(categorias.length);
		for(String catName : CATEGORIAS) {
			Categoria cat = new Categoria();
			cat.setNombre(catName);
			em.persist(cat);
			entidadesCategorias.add(cat);
		}
	}

	private void crearProductos(Integer n) {		
		Random r = new Random();
		int maxCat = 1+r.nextInt(3);
		for(int i=0;i<n;i++) {
			HashSet<Integer> catGeneradas = new HashSet<>();
			while(catGeneradas.size()<maxCat) {
				catGeneradas.add(r.nextInt(entidadesCategorias.size()));
			}
			Producto p = new Producto();
			p.setPrecio(0.0);
			p.setDescripcion(generador.generateRandomWords(3));
			List<Categoria> aux = new ArrayList<>();
			for(Integer idxcat:catGeneradas) {
				aux.add(this.entidadesCategorias.get(idxcat));
			}
			p.setCategoria(aux);
			em.persist(p);
		}
	}

	
}