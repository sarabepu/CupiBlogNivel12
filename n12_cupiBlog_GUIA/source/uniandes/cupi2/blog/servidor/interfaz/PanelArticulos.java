package uniandes.cupi2.blog.servidor.interfaz;

import java.awt.Dimension;
import java.util.Collection;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class PanelArticulos extends JPanel{
	
	private InterfazServidor principal;
	private JList listaArticulos;

	public PanelArticulos(InterfazServidor pPrincipal){
		principal= pPrincipal;
		JScrollPane scroll = new JScrollPane( );
        listaArticulos = new JList( );
        scroll.getViewport( ).add( listaArticulos );
        scroll.setPreferredSize(new Dimension(330,580));
        add(scroll);  
		this.setBorder( new TitledBorder( "Lista artículos" ) );
		setPreferredSize(new Dimension(343,480));
	}
	
	
	public void actualizarArticulos(Collection lista)
	{
		listaArticulos.setListData( lista.toArray( ) );
	}


}
