package uniandes.cupi2.blog.servidor.mundo;

import java.io.BufferedReader;  
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import uniandes.cupi2.blog.cliente.mundo.Articulo;
import uniandes.cupi2.blog.cliente.mundo.Usuario;

public class ComunicadorCliente extends Thread
{
	private Socket socket;
	private String pLogin;
	private Usuario usuario;
	private AdministradorBaseDatos admin;
	private PrintWriter out;
	private BufferedReader in;
	private boolean fin;
	public ComunicadorCliente(Socket con, AdministradorBaseDatos acceso) throws IOException
	{
		socket = con;
		admin = acceso;
		out = new PrintWriter(con.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		fin = false;
	}

	public Usuario darUsuario()
	{
		return usuario;
	}
	public void registrar(String parametros) 
	{
		String[] param = parametros.split(Protocolo.SEPARADOR_PARAMETROS);
		try {
			admin.registrarUsuario(param);
			usuario= new Usuario(param[0], param[1], param[2]);
			out.println(Protocolo.REGISTRADO
					+Protocolo.SEPARADOR_COMANDO + param[0]);
		} 
		catch (SQLException e) 
		{
			out.println(Protocolo.ERROR
					+ Protocolo.SEPARADOR_COMANDO
					+ "Usuario existe");
		}
	}
	public void iniciarSesion(String parametros) 
	{
		String[] param = parametros
				.split(Protocolo.SEPARADOR_PARAMETROS);
		try {
			if (admin.darDatosUsuario(param[0]) != null) {
				out.println(Protocolo.LOGIN
						+ Protocolo.SEPARADOR_COMANDO
						+ admin.darDatosUsuario(param[0]).darLogin()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ admin.darDatosUsuario(param[0]).darNombre()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ admin.darDatosUsuario(param[0]).darApellido());
				pLogin = param[0];

				usuario= new Usuario(admin.darDatosUsuario(param[0]).darLogin(), 
						admin.darDatosUsuario(param[0]).darNombre(),
						admin.darDatosUsuario(param[0]).darApellido());
			} else
				out.println(Protocolo.ERROR
						+ Protocolo.SEPARADOR_COMANDO
						+ "Usuario no existe");

		} 
		catch (SQLException e) 
		{
			out.println(Protocolo.ERROR
					+ Protocolo.SEPARADOR_COMANDO
					+ "Usuario no existe");
		}
	}

	public void listaArt() throws ParseException{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");

			out.println(Protocolo.ARTICULOS
					+ Protocolo.SEPARADOR_COMANDO
					+ admin.consultarListaArticulos().size());
			for (int i = 0; i < admin.consultarListaArticulos().size(); i++) {
				Articulo art = admin.consultarListaArticulos().get(i);
				out.println(Protocolo.ARTICULO
						+ Protocolo.SEPARADOR_COMANDO
						+ art.darLoginUsuario()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darTitulo()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darCategoria()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darContenido()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ sdf.format(art.darFechaPublicacion())
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darCalificacionAcumulada()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darVecesCalificado());
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void publicar(String parametros) throws SQLException, ParseException {
		admin.registrarArticulo(parametros, pLogin);
		listaArt();
	}

	public void calificar(String param) throws SQLException, ParseException {
		admin.registrarCalificacion(param);
		listaArt();
	}

	public void estadisticas() throws SQLException {

		double[] nums = admin.retornarEstadisticas(pLogin);
		out.println(Protocolo.ESTADISTICAS
				+ Protocolo.SEPARADOR_COMANDO + (int)nums[0]
						+ Protocolo.SEPARADOR_PARAMETROS + nums[1]);
	}

	public void busquedaCategoria(String param) throws ParseException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm");

			out.println(Protocolo.ARTICULOS
					+ Protocolo.SEPARADOR_COMANDO
					+ admin.buscarPorCategoria(param).size());
			for (int i = 0; i < admin.buscarPorCategoria(param).size(); i++) {
				Articulo art = admin.buscarPorCategoria(param).get(i);
				out.println(Protocolo.ARTICULO
						+ Protocolo.SEPARADOR_COMANDO	
						+ art.darLoginUsuario()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darTitulo()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darCategoria()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darContenido()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ sdf.format(art.darFechaPublicacion())
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darCalificacionAcumulada()
						+ Protocolo.SEPARADOR_PARAMETROS
						+ art.darVecesCalificado());
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	public void run() {
		try {

			while (!fin)
			{
				String com = in.readLine();
				String[] linea = com.split(Protocolo.SEPARADOR_COMANDO);

				if (linea[0].equals(Protocolo.REGISTRAR))
				{
					registrar(linea[1]);
				} 
				else if (linea[0].equals(Protocolo.LOGIN))
				{

					iniciarSesion(linea[1]);

				} 
				else if (linea[0].equals(Protocolo.LOGOUT))
				{
					out.println(Protocolo.LOGOUT);
					socket.close();
					fin = true;
				} 
				else if (linea[0].equals(Protocolo.PUBLICAR_ARTICULO))
				{
					publicar(linea[1]);

				} 
				else if (linea[0].equals(Protocolo.ESTADISTICAS))
				{
					estadisticas();
				} 
				else if (linea[0].equals(Protocolo.LISTA_ARTICULOS)) 
				{
					listaArt();
				}
				else if (linea[0].equals(Protocolo.BUSQUEDA_CATEGORIA)) 
				{
					busquedaCategoria(linea[1]);

				} 
				else if (linea[0].equals(Protocolo.CALIFICAR)) 
				{

					calificar(linea[1]);
				}

			}

		} 
		catch (Exception e) {
			fin = true;
			e.printStackTrace();
		}
	}
}
