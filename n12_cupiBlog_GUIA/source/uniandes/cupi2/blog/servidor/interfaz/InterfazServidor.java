package uniandes.cupi2.blog.servidor.interfaz;

import java.awt.BorderLayout; 
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import uniandes.cupi2.blog.servidor.mundo.Blog;
import uniandes.cupi2.blog.servidor.mundo.ComunicadorCliente;

public class InterfazServidor extends JFrame
{
	private Blog conexiones;
	private PanelBotones panelBotones;
	private PanelArticulos panelArticulos;
	private PanelUsuarios panelUsuarios;

	public InterfazServidor(Blog conexion)
	{
		conexiones = conexion;
		setLayout(new BorderLayout());
		setSize(700, 700);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);

		panelBotones = new PanelBotones(this);
		add( panelBotones, BorderLayout.SOUTH ); 
		panelArticulos = new PanelArticulos(this);
		add( panelArticulos, BorderLayout.EAST );
		panelUsuarios = new PanelUsuarios(this);
		add( panelUsuarios, BorderLayout.WEST );
	}

	public void actualizarDatos(String login)
	{
		try 
		{

			Collection articulos = conexiones.darAdministradorResultados().darArticulosUsuario(login);
			ArrayList array= new ArrayList();
			Iterator itr = articulos.iterator();
			while(itr.hasNext()) 
			{

				String element = (String) itr.next();
				String mezcla= element + "( Por: " + login + ")";
				array.add(mezcla);
			}

			panelArticulos.actualizarArticulos(array);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public void actualizarUsuarios( )
	{
		Collection jugadores = conexiones.darGente();
		ArrayList lista= new ArrayList();

		Iterator itr = jugadores.iterator();
		while(itr.hasNext()) 
		{
			ComunicadorCliente element = (ComunicadorCliente) itr.next();
			if(element.darUsuario()!=null)
			{
				System.out.println(element.darUsuario());
				lista.add(element.darUsuario());
			}
		}
		panelUsuarios.actualizarEncuentros(lista);

	}

	public static void main( String[] args )
	{
		try
		{
			String archivoPropiedades = "./data/servidor.properties";
			Blog mundo = new Blog( archivoPropiedades );

			InterfazServidor interfaz = new InterfazServidor( mundo );
			interfaz.setVisible( true );

			mundo.recibirConexiones( );
		}
		catch( Exception e )
		{            
			e.printStackTrace( );
		}
	}

	public void reqFuncOpcion1( )
	{
		String resultado = conexiones.metodo1( );
		JOptionPane.showMessageDialog( this, resultado, "Respuesta", JOptionPane.INFORMATION_MESSAGE );
	}

	public void reqFuncOpcion2( )
	{
		String resultado = conexiones.metodo2( );
		JOptionPane.showMessageDialog( this, resultado, "Respuesta", JOptionPane.INFORMATION_MESSAGE );
	}
}