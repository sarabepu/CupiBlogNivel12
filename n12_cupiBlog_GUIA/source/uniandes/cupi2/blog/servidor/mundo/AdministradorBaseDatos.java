package uniandes.cupi2.blog.servidor.mundo;

import java.io.File; 
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import uniandes.cupi2.blog.cliente.mundo.Articulo;
import uniandes.cupi2.blog.cliente.mundo.Usuario;

public class AdministradorBaseDatos 
{
	private String url;
	private String driver;
	private String shutdown;
	private String path;
	private Connection conexion;
	private Properties configuracion;

	public AdministradorBaseDatos(Properties config) {
		configuracion = config;
	}

	public void conectarABD() throws Exception {

		url = configuracion.getProperty("admin.db.url");
		driver = configuracion.getProperty("admin.db.driver");
		shutdown = configuracion.getProperty("admin.db.shutdown");
		path = configuracion.getProperty("admin.db.path");
		File data = new File(path);

		System.setProperty("derby.system.home", data.getAbsolutePath());
		Class.forName(driver).newInstance();
		conexion = DriverManager.getConnection(url);
	}

	public void desconectarse() throws Exception {
		conexion.close();
		try {
			DriverManager.getConnection(shutdown);
		} catch (Exception e) {

		}

	}

	public void crearTabla() throws SQLException {
		boolean crear = false;

		Statement statement = conexion.createStatement();

		try {
			statement.executeQuery("SELECT * FROM usuarios WHERE 1=2");
		} catch (SQLException e) {
			crear = true;
		}

		if (crear) {
			String sql = "CREATE TABLE usuarios (login varchar(32), nombre varchar(50), apellido varchar(50), articulos_publicados Integer, PRIMARY KEY (login))";
			statement.execute(sql);
		}
		
		crear = false;

		try {
			statement.executeQuery("SELECT * FROM articulos WHERE 1=2");
		} catch (SQLException E) {
			crear = true;
		}
		if (crear) {
			String sql2 = "CREATE TABLE articulos (login_usuario varchar(32), titulo varchar(50), categoria varchar(50), contenido varchar(10000), fecha_publicacion varchar(20), calificacion_acumulada Integer, veces_calificado Integer, PRIMARY KEY (login_usuario, titulo))";
			statement.execute(sql2);
		}
		statement.close();

	}

	public Usuario darDatosUsuario(String pLogin) throws SQLException {
		Statement s = conexion.createStatement();
		Usuario usu = null;

		String sql = "SELECT * FROM usuarios WHERE login ='" + pLogin
				+ "'";
		ResultSet rs = s.executeQuery(sql);
		while (rs.next())

		{
			String login = rs.getString(1);
			String nombre = rs.getString(2);
			String apellido = rs.getString(3);

			usu = new Usuario(login, nombre, apellido);
		}

		rs.close();
		return usu;
	}

	public void registrarUsuario(String[] param) throws SQLException {
		Statement s = conexion.createStatement();

		String sql = "INSERT INTO usuarios(login, nombre, apellido) VALUES('" 
		+ param[0] + "','" + param[1] + "','"
				+ param[2] + "')";
		s.execute(sql);
		s.close();
	}

	public ArrayList<Articulo> consultarListaArticulos() throws SQLException,ParseException 
	
	{
		ArrayList<Articulo> lista = new ArrayList<Articulo>();

		String sql = "SELECT * FROM articulos";

		Statement st = conexion.createStatement();
		ResultSet resultado = st.executeQuery(sql);

		while (resultado.next())
		{
			String pLoginUsuario = resultado.getString(1);
			String pTitulo = resultado.getString(2);
			String pCategoria = resultado.getString(3);
			String pContenido = resultado.getString(4);
			String pFechaPublicacion = resultado.getString(5);
			int pCalificacionAcumulada = resultado.getInt(6);
			int pVecesCalificado = resultado.getInt(7);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");

			Articulo art = new Articulo(pLoginUsuario, pTitulo, pCategoria,
					pContenido, sdf.parse(pFechaPublicacion),
					pCalificacionAcumulada, pVecesCalificado);
			lista.add(art);
		}

		resultado.close();
		st.close();

		return lista;
	}

	public void registrarArticulo(String info, String pLogin)
			throws SQLException {
		String[] division = info
				.split(Protocolo.SEPARADOR_PARAMETROS);

		Statement s = conexion.createStatement();

		Calendar cal = Calendar.getInstance();
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
		String sd = sdf.format(d);

		String sql = "INSERT INTO articulos VALUES ('" + pLogin + "','"
				+ division[0] + "','" + division[1] + "','" + division[2]
						+ "','" + sd + "',0,0)";
		s.execute(sql);
		s.close();

	}


	public Collection darArticulosUsuario(String login) throws SQLException {

		Collection articulos = new ArrayList();
		String sql = "SELECT titulo FROM articulos WHERE login_usuario='"
				+ login + "'";

		Statement st = conexion.createStatement();
		ResultSet resultado = st.executeQuery(sql);

		while (resultado.next()) {

			String pTitulo = resultado.getString(1);
			articulos.add(pTitulo);

		}
		resultado.close();
		st.close();

		return articulos;
	}

	public void registrarCalificacion(String param) throws SQLException {

		String[] parama = param
				.split(Protocolo.SEPARADOR_PARAMETROS);
		Statement s = conexion.createStatement();

		String sql1 = "UPDATE articulos SET calificacion_acumulada = calificacion_acumulada + '"
				+ parama[2] + "'" + "WHERE titulo = " + "'" + parama[1] + "'";

		String sql2 = "UPDATE articulos SET veces_calificado = veces_calificado + 1 WHERE titulo = "
				+ "'" + parama[1] + "'";

		s.executeUpdate(sql1);

		s.executeUpdate(sql2);

		s.close();

	}

	public double[] retornarEstadisticas(String login) throws SQLException {
		double[] resp = new double[2];
		int tamaño = 0;
		double suma = 0;
		double veces = 0;

		Statement s = conexion.createStatement();

		String sql = "SELECT * FROM articulos WHERE login_usuario ='" + login
				+ "'";

		ResultSet rs = s.executeQuery(sql);

		while (rs.next())
		{
			tamaño++;
			suma += rs.getInt(6);
			veces += rs.getInt(7);

		}

		if (tamaño != 0) {
			resp[0] = tamaño;
			resp[1] = (suma / veces);
		}
		s.close();
		return resp;
	}

	public ArrayList<Articulo> buscarPorCategoria(String param)
			throws SQLException, ParseException {
		ArrayList<Articulo> lista = new ArrayList<Articulo>();

		String sql = "SELECT * FROM articulos WHERE " + "categoria" + " = '"
				+ param + "'";

		Statement st = conexion.createStatement();
		ResultSet resultado = st.executeQuery(sql);

		while (resultado.next()) {
			String pLoginUsuario = resultado.getString(1);
			String pTitulo = resultado.getString(2);
			String pCategoria = resultado.getString(3);
			String pContenido = resultado.getString(4);
			String pFechaPublicacion = resultado.getString(5);
			int pCalificacionAcumulada = resultado.getInt(6);
			int pVecesCalificado = resultado.getInt(7);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");
			Articulo art = new Articulo(pLoginUsuario, pTitulo, pCategoria,
					pContenido, sdf.parse(pFechaPublicacion),
					pCalificacionAcumulada, pVecesCalificado);
			lista.add(art);
		}

		resultado.close();
		st.close();

		return lista;
	}

}