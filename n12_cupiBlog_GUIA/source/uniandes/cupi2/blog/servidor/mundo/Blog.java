package uniandes.cupi2.blog.servidor.mundo;

import java.io.FileInputStream; 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

public class Blog 
{

	private ServerSocket receptor;

	private Properties config;

	private AdministradorBaseDatos adminBlog;

	private Collection usuarios;

	public Blog(String archivo) throws SQLException, Exception
	{
		usuarios = new Vector();

		cargarConfiguracion(archivo);

		adminBlog = new AdministradorBaseDatos(config);
		adminBlog.conectarABD();
		adminBlog.crearTabla();
		verificarInvariante();
	}
	public Collection darGente()
	{

		return usuarios;
	}

	private void cargarConfiguracion(String archivo) throws Exception 
	{
		FileInputStream fis = new FileInputStream(archivo);
		config = new Properties();
		config.load(fis);
		fis.close();
	}

	public AdministradorBaseDatos darAdministradorResultados()
	{
		return adminBlog;
	}

	public void recibirConexiones() 
	{
		String aux = config.getProperty("servidor.puerto");
		int puerto = Integer.parseInt(aux);
		try 
		{
			receptor = new ServerSocket(puerto);
			while (true)
			{
				// Esperar una nueva conexión
				Socket socketNuevoCliente = receptor.accept();

				// Intentar iniciar un encuentro con el nuevo cliente
				conectar(socketNuevoCliente);
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			try 
			{
				receptor.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	public void conectar(Socket pSocket) 
	{
		Socket con = pSocket;

		try
		{
			ComunicadorCliente nom = new ComunicadorCliente(con, adminBlog);
			agregarNuevaConexion(nom);
		} catch (Exception e) 
		{
			try
			{
				con.close();

			}
			catch (Exception e2) 
			{
				e2.printStackTrace();
			}
			e.printStackTrace();

		}
	}

	public void agregarNuevaConexion(ComunicadorCliente ts) {
		usuarios.add(ts);
		ts.start();
	}

	private void verificarInvariante() {
		assert receptor != null : "Canal inválido";
		assert config != null : "Conjunto de propiedades inválidas";
		assert adminBlog != null : "El administrador del blog no debería ser null";
		assert usuarios != null : "La lista de artículos no debería ser null";
	}
	
	public String metodo1( )
	{
		return "Respuesta 1.";
	}

	public String metodo2( )
	{
		return "Respuesta 2.";
	}


}
