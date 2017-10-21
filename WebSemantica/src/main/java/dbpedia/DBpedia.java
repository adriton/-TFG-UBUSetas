package dbpedia;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.log4j.Logger;


/**
 * @name DBpedia
 * @author Adrian Anton Garcia
 * @category class
 * @Description Clase que contiene los métodos necesarios realizar consultas a
 *              la web semantica dbpedia
 */

public class DBpedia {

	// logger del BDsql
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(DBpedia.class);
	
	QueryExecution lanzadorQuery = null;

	/**
	 * @name DBpedia
	 * @author Adrian Anton Garcia
	 * @category constructor
	 * @Description Constructor que inicializa la clase DBpedia
	 */

	public DBpedia() {

	}

	/**
	 * @name lanzarConsulta
	 * @author Adrian Anton Garcia
	 * @category metodo
	 * @Description metodo que lanza la consulta pasada por parametro a la
	 *              dbpedia
	 * @param String,
	 *            consulta a lanzar
	 * @return ResultSet, resultset que contiene el resultado de la consulta
	 */

	public ResultSet lanzarConsulta(String query) {
		lanzadorQuery = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		ResultSet resultados = lanzadorQuery.execSelect();
		return resultados;

	}

	/**
	 * @name getDataQueryGenero
	 * @author Adrian Anton Garcia
	 * @category metodo
	 * @Description metodo que obtiene la consulta para conseguir el genero de una seta llamando a la
	 *              dbpedia con su nombre completo
	 * @param String,
	 *            seta a consultar
	 * @return String, Consulta para obtener el genero de la web Semántica
	 */

	public String getDataQueryGeneroSeta(String nombreEspecie) {
		return "PREFIX dbpediaResource: <http://dbpedia.org/resource/>" + "PREFIX dbo:<http://dbpedia.org/ontology/>"
				+ "PREFIX dbp:<http://dbpedia.org/property/>" + "SELECT ?genero ?gen " + "WHERE { " + "dbpediaResource:"
				+ nombreEspecie + " dbo:genus ?genero." + "?genero dbp:genus ?gen" + "}";
	}

	/**
	 * @name getDataQueryGenero
	 * @author Adrian Anton Garcia
	 * @category metodo
	 * @Description metodo que obtiene la consulta para conseguir el genero de una seta llamando a la
	 *              debepedia solo con la primeta parte del nombre
	 * @param String,
	 *            seta a consultar
	 * @return String, Genero conseguido de la web Semántica
	 */

	public String getDataQueryGenero(String nombreGenero) {
		return "PREFIX dbpediaResource:<http://dbpedia.org/resource/>" + "PREFIX dbo:<http://dbpedia.org/ontology/>"
				+ "PREFIX dbp:<http://dbpedia.org/property/>" + "SELECT ?genero " + "WHERE { " + "dbpediaResource:"
				+ nombreGenero + " dbp:genus ?genero." + "}";
	}

	/**
	 * @name getDataQueryEnlace
	 * @author Adrian Anton Garcia
	 * @category metodo
	 * @Description metodo que obtiene la consulta para conseguir el genero de
	 *              una seta
	 * @param String,
	 *            seta a consultar
	 * @return String, Consulta para obtener el genero de la seta
	 */

	public String getDataQueryEnlace(String nombreEspecie) {
		return "PREFIX dbpediaResource:<http://dbpedia.org/resource/>" + "PREFIX dbo:<http://dbpedia.org/ontology/>"
				+ "PREFIX dbp:<http://dbpedia.org/property/>" + "SELECT ?enlace " + "WHERE { " + "dbpediaResource:"
				+ nombreEspecie + " dbo:wikiPageID ?enlace." + "}";
	}

	/**
	 * @name getDataQueryComestible
	 * @author Adrian Anton Garcia
	 * @category metodo
	 * @Description metodo obtiene la consulta para conseguir la
	 *              comestibilidad de una seta
	 * @param String,
	 *            seta a consultar
	 * @return String, consulta de la comestibilidad de la seta
	 */

	public String getDataQueryComestible(String nombreEspecie) {
		return "PREFIX dbpediaResource:<http://dbpedia.org/resource/>" + "PREFIX dbp:<http://dbpedia.org/property/>"
				+ "SELECT  ?especie " + "WHERE { " + "dbpediaResource:" + nombreEspecie + " dbp:howedible ?especie."
				+ "}";
	}

	/**
	 * @name getDataQueryDescriptionEsp
	 * @author Adrian Anton Garcia
	 * @category metodo
	 * @Description metodo que consigue la descripcion en español de la seta
	 *              introducida como parametro
	 * @param String,
	 *            seta a consultar
	 * @return String, Consulta de la web Semántica
	 */

	public String getDataQueryDescriptionEsp(String nombreEspecie) {
		return "PREFIX dbpediaResource: <http://dbpedia.org/resource/>" + "PREFIX dbo:<http://dbpedia.org/ontology/>"
				+ "SELECT ?descripcion " + "WHERE { " + "dbpediaResource:" + nombreEspecie
				+ " dbo:abstract ?descripcion." + "FILTER ( lang(?descripcion)=\"es\" )." // ||
				// lang(?descripcion)=\"en\"
				+ "}";
	}

	/**
	 * @name getDataQueryDescriptionEn
	 * @author Adrian Anton Garcia
	 * @category metodo
	 * @Description metodo que obtiene la consulta para conseguir la descripcion en ingles de la seta
	 *              introducida como parametro
	 * @param String,
	 *            seta a consultar
	 * @return String, Consulta de la web Semántica
	 */

	public String getDataQueryDescriptionEn(String nombreEspecie) {
		return "PREFIX dbpediaResource: <http://dbpedia.org/resource/>" + "PREFIX dbo:<http://dbpedia.org/ontology/>"
				+ "SELECT ?descripcion " + "WHERE { " + "dbpediaResource:" + nombreEspecie
				+ " dbo:abstract ?descripcion." + "FILTER ( lang(?descripcion)=\"en\" )." // ||
																							// lang(?descripcion)=\"es\"
				+ "}";
	}

	/**
	 * @name close
	 * @author Adrian Anton Garcia
	 * @category procedimiento
	 * @Description Procedimiento que cierra la conexion con la web semantica
	 */

	public void close() {
		lanzadorQuery.close();
	}
}