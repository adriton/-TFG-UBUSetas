package basedatossql;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

import org.apache.log4j.Logger;

import dbpedia.DBpedia;
import traductor.Translator;

/**
 * Clase que crea la base de datos a partir de BDsql y DBpedia
 * 
 * @name CreadorBD
 * @author Adrian Anton Garcia
 * @category class
 */

public class CreadorBD {
	// logger del creadorBD

	private final static Logger logger = Logger.getLogger(CreadorBD.class);
	// clase de la web semantica
	private DBpedia dp = null;
	// clase de la base de datos
	private BDsql bd = null;

	// urls para crear la base de datos
	private String urlFichero = "src\\main\\java\\nombresSetas.txt";
	private String urlBaseDatos = "jdbc:sqlite:C:\\sqlite\\DBsetas\\DBsetas.db";
	// String connectionUrl
	// ="jdbc:sqlserver://localhost:1433;databaseName=DBsetas;user=sa;password=adrian1";
	// Nombres de las tablas
	String nombreTablaDescripciones = "TablaDescripciones";
	String nombreTablaGeneros = "TablaGeneros";
	String nombreTablaEnlaces = "TablaEnlaces";
	String nombreTablaComestible = "TablaComestible";
	String nombreTablaClaves = "TablaClaves";

	/**
	 * Constructor que inicializa la clase CreadorBD
	 * 
	 * @name CreadorBD
	 * @author Adrian Anton Garcia
	 * @category constructor
	 */

	public CreadorBD() {
		dp = new DBpedia();
		bd = new BDsql();
	}

	/**
	 * método que devuelve la url donde se encuentra el fichero con los nombres
	 * de las setas
	 * 
	 * @name getUrlFichero
	 * @author Adrian Anton Garcia
	 * @category método
	 * @return String, instancia de la base de datos
	 */

	public String getUrlFichero() {
		return this.urlFichero;
	}

	/**
	 * método que devuelve la url donde se encuentra la base de datos
	 * 
	 * @name getUrlBaseDatos
	 * @author Adrian Anton Garcia
	 * @category método
	 * @return String, la url de la base de datos
	 */

	public String getUrlBaseDatos() {
		return this.urlBaseDatos;
	}

	/**
	 * método que devuelve la instancia de la base de datos usada
	 * 
	 * @name getBaseDatos
	 * @author Adrian Anton Garcia
	 * @category método
	 * @return BDsql, instancia de la base de datos
	 */

	public BDsql getBaseDatos() {
		return bd;
	}

	/**
	 * método que devuelve la instancia de la web semantica usada
	 * 
	 * @name getWebSemantica
	 * @author Adrian Anton Garcia
	 * @category método
	 * @return BDsql, instancia de la web semantica
	 */

	public DBpedia getWebSemantica() {
		return dp;
	}

	/**
	 * @name crearBaseDatos
	 * @author Adrian Anton Garcia
	 * @category procedimiento
	 * @Description Procedimiento que crea la base de datos creando las tablas y
	 *              rellenandolas mediante la web semántica
	 */

	public void crearBaseDatos() {
		// Lectura nombres de setas

		List<String> resultados;
		resultados = this.leerFichero(urlFichero);

		// Conexión con la base de datos

		String connectionUrl = urlBaseDatos;
		bd.conectarseBaseDatos(connectionUrl);

		// Creacion de las tablas

		bd.borrarTablaSetas(nombreTablaDescripciones);
		bd.crearTablaDescripciones(nombreTablaDescripciones);

		bd.borrarTablaSetas(nombreTablaGeneros);
		bd.crearTablaEspecies(nombreTablaGeneros);

		bd.borrarTablaSetas(nombreTablaEnlaces);
		bd.crearTablaEnlaces(nombreTablaEnlaces);

		bd.borrarTablaSetas(nombreTablaComestible);
		bd.crearTablaComestible(nombreTablaComestible);

		System.out.println(resultados.toString());

		// Inserción de las filas

		for (String nombreSeta : resultados) {
			this.insertarDescripciones(nombreSeta, nombreTablaDescripciones);
			this.insertarGeneros(nombreSeta, nombreTablaGeneros);
			this.insertarLinks(nombreSeta, nombreTablaEnlaces);
			this.insertarComestibilidad(nombreSeta, nombreTablaComestible);
		}

		// Cierre de la base de datos
		bd.close();

	}

	/**
	 * Procedimiento que hace uso de la web semántica para consultar la
	 * comestibilidad de la seta que se pasa por parametro e inserta una fila de
	 * esa seta con su comestiblidad.
	 * 
	 * @name insertarComestibilidad
	 * @author Adrian Anton Garcia
	 * @category procedimiento
	 * @param String,
	 *            nombre de la seta a insertar la comestibilidad
	 */

	public void insertarComestibilidad(String nombreSeta, String nombreTabla) {

		String consulta = dp.getDataQueryComestible(nombreSeta);
		String resultado = ResultSetFormatter.asText(dp.lanzarConsulta(consulta));
		// elimino los caracteres innecesarios
		resultado = resultado.replaceAll("-", "").replaceAll("=", "").replaceAll("\\|", "").substring(10).trim();
		// creo un array con cada comestibilidad obtenida de las seta
		String[] arrayComestibles = resultado.split("\"");
		// creo una lista que sera lo que se inserte en la tabla
		List<String> comes = new ArrayList<String>();
		String res = null;
		for (int i = 0; i < arrayComestibles.length; i++) {
			// diferencio entre el contenido de la consulta que es basura y lo
			// que es la comestibilidad de la seta
			res = arrayComestibles[i].split("\\^")[0];
			// si es la comestibilidad la agrego a la lista
			if (res.length() > 0) {
				comes.add(res);
			}
		}
		nombreSeta = nombreSeta.toLowerCase().replaceAll("_", " ").trim();
		Translator traductor = new Translator();
		if (comes.size() > 0) {
			String comestibleEn = "";
			String comestibleEs = "";
			for (String c : comes) {
				comestibleEn = comestibleEn + " " + c;
				comestibleEn = comestibleEn.trim();
				comestibleEs = comestibleEs + " " + traductor.translateType(c);
				comestibleEs = comestibleEs.trim();
			}

			bd.insertarFilaComestible(nombreTablaComestible, nombreSeta, comestibleEn, comestibleEs);
		} else {
			bd.insertarFilaComestible(nombreTablaComestible, nombreSeta, "unknown", "desconocido");
		}
	}

	/**
	 * Procedimiento que hace uso de la web semántica para consultar el link de
	 * la seta que se pasa por parametro e inserta una fila de esa seta con el
	 * link.
	 * 
	 * @name insertarLinks
	 * @author Adrian Anton Garcia
	 * @category procedimiento
	 * @param String,
	 *            nombre de la seta a insertar el link
	 */

	public void insertarLinks(String nombreSeta, String nombreTabla) {

		String queryEng = dp.getDataQueryEnlace(nombreSeta);
		ResultSet ResultSetresultados = dp.lanzarConsulta(queryEng);
		String resultado = ResultSetFormatter.asText(ResultSetresultados);

		resultado = resultado.replaceAll("-", "").replaceAll("=", "").replaceAll("\\|", "").substring(16).trim();
		// resultado1=lanzador.filterRemoveQuotes(resultado1);
		if (resultado.length() > 0) {
			resultado = "https://wikipedia.org/wiki?curid=" + resultado;
		} else {
			queryEng = dp.getDataQueryEnlace(nombreSeta.split("_")[0]);
			ResultSetresultados = dp.lanzarConsulta(queryEng);
			resultado = ResultSetFormatter.asText(ResultSetresultados);
			resultado = resultado.replaceAll("-", "").replaceAll("=", "").replaceAll("\\|", "").substring(16).trim();
			resultado = "https://wikipedia.org/wiki?curid=" + resultado;
		}
		nombreSeta = nombreSeta.toLowerCase().replaceAll("_", " ").trim();
		bd.insertarFilaEnlace(nombreTabla, nombreSeta, resultado);
	}

	/**
	 * Procedimiento que hace uso de la web semántica para consultar el genero
	 * de la seta que se pasa por parametro e inserta una fila de esa seta con
	 * la especie.
	 * 
	 * @name insertarGeneros
	 * @author Adrian Anton Garcia
	 * @category procedimiento
	 * @param String,
	 *            nombre de la seta a insertar la especie
	 */

	public void insertarGeneros(String nombreSeta, String nombreTabla) {

		String consulta = dp.getDataQueryGeneroSeta(nombreSeta);

		String resultado = ResultSetFormatter.asText(dp.lanzarConsulta(consulta));

		if (resultado.length() > 73) {
			resultado = resultado.replaceAll("-", "").replaceAll("=", "").replaceAll("\\|", "").substring(23).trim();
			resultado = resultado.split("\"")[1].split("\"")[0];
		} else {
			consulta = dp.getDataQueryGenero(nombreSeta.split("_")[0]);
			resultado = ResultSetFormatter.asText(dp.lanzarConsulta(consulta));

			if (resultado.length() > 48) {
				resultado = resultado.replaceAll("-", "").replaceAll("=", "").replaceAll("\\|", "").substring(15)
						.trim();
				resultado = resultado.split("\"")[1].split("\"")[0];
			} else {
				resultado = nombreSeta.split("_")[0];

			}
		}
		// formateo el nombre para adecuarlo a la estructura de Android
		nombreSeta = nombreSeta.toLowerCase().replaceAll("_", " ").trim();
		bd.insertarFilaGeneros(nombreTabla, nombreSeta, resultado);
	}

	/**
	 * Procedimiento que hace uso de la web semántica para consultar las
	 * descripciones de la seta que se pasa por parametro e inserta una fila de
	 * esa seta con la descripción.
	 * 
	 * @name insertarDescripciones
	 * @author Adrian Anton Garcia
	 * @category procedimiento
	 * @param String,
	 *            nombre de la seta a insertar las descripciones
	 */

	public void insertarDescripciones(String nombreSeta, String nombreTabla) {

		ResultSet ResultSetresultados = null;
		int EsGenero = 0;
		// Descripcion en ingles
		String queryEng = dp.getDataQueryDescriptionEn(nombreSeta);
		ResultSetresultados = dp.lanzarConsulta(queryEng);
		String descripcionEng = ResultSetFormatter.asText(ResultSetresultados);
		descripcionEng = descripcionEng.replaceAll("-", "").replaceAll("=", "").replaceAll("\\|", "").substring(14)
				.trim();
		// inserto la fila
		if (descripcionEng.length() == 0) {
			EsGenero = 1;
			String nombreSetaPadre = nombreSeta.split("_")[0];
			queryEng = dp.getDataQueryDescriptionEn(nombreSetaPadre);
			ResultSetresultados = dp.lanzarConsulta(queryEng);
			descripcionEng = ResultSetFormatter.asText(ResultSetresultados);
			descripcionEng = descripcionEng.replaceAll("-", "").replaceAll("=", "").replaceAll("\\|", "").substring(14)
					.trim();
		}

		// Descripcion en español
		/*
		 * String queryEsp = dp.getDataQueryDescriptionEsp(nombreSeta);
		 * ResultSetresultados = dp.lanzarConsulta(queryEsp); String
		 * descripcionEsp = ResultSetFormatter.asText(ResultSetresultados);
		 * descripcionEsp = descripcionEsp.replaceAll("-", "").replaceAll("=",
		 * "").replaceAll("\\|", "").substring(14) .trim(); //si no esta en
		 * español la inserto en ingles if(descripcionEsp.length()==0){ String
		 * nombreSetaPadre=nombreSeta.split("_")[0]; queryEsp =
		 * dp.getDataQueryDescriptionEn(nombreSetaPadre); ResultSetresultados =
		 * dp.lanzarConsulta(queryEsp); descripcionEsp =
		 * ResultSetFormatter.asText(ResultSetresultados); descripcionEsp =
		 * descripcionEsp.replaceAll("-", "").replaceAll("=",
		 * "").replaceAll("\\|", "").substring(14).trim();
		 * if(descripcionEsp.length()==0){ descripcionEsp=descripcionEng; } }
		 */
		Translator traductor = new Translator();

		String descripcionEsp = "";
		try {
			descripcionEsp = traductor.callUrlAndParseResult("en", "es", descripcionEng);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		// formateo el nombre para adecuarlo a la estructura de Android
		nombreSeta = nombreSeta.toLowerCase().replaceAll("_", " ").trim();
		bd.insertarFilaDescripciones(nombreTabla, nombreSeta, descripcionEsp, descripcionEng, EsGenero);
	}

	/**
	 * método que lee las setas del fichero y devuelve una lista con todos los
	 * nombres de las setas contenidas en ese fichero
	 * 
	 * @name leeFichero
	 * @author Adrian Anton Garcia
	 * @category método
	 * @param String,
	 *            nombre del fichero a leer
	 * @return List<String>, lista de nombres de las setas leidas del fichero
	 */

	public List<String> leerFichero(String nombre) {
		List<String> nombreSetas = null;

		try {
			nombreSetas = new ArrayList<String>();
			String cadena;
			FileReader f = new FileReader(nombre);
			BufferedReader b = new BufferedReader(f);
			while ((cadena = b.readLine()) != null) {
				nombreSetas.add(cadena);
			}
			b.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return nombreSetas;
	}
}
