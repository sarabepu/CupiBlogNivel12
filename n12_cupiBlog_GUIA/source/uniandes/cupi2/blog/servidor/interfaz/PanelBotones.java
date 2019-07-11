package uniandes.cupi2.blog.servidor.interfaz;

import java.awt.BorderLayout; 
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


public class PanelBotones extends JPanel implements ActionListener
{
    private static final String OPCION_1 = "OPCION_1";

    private static final String OPCION_2 = "OPCION_2";
   
    private JButton btnOpcion1;

    private JButton btnOpcion2; 
  
    private InterfazServidor principal;
    
	public PanelBotones(InterfazServidor Pprincipal)
	{
		 principal = Pprincipal;
		this.setBorder(new TitledBorder("Opciones"));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(700, 50));
		
		setLayout( new GridLayout( 1, 2 ) );

        btnOpcion1 = new JButton( "Opción 1" );
        btnOpcion1.setActionCommand( OPCION_1 );
        btnOpcion1.addActionListener( this );
        add( btnOpcion1 );

        btnOpcion2 = new JButton( "Opción 2" );
        btnOpcion2.setActionCommand( OPCION_2 );
        btnOpcion2.addActionListener( this );
        add( btnOpcion2 );
	}

	@Override
	public void actionPerformed(ActionEvent pEvento) {
		  String comando = pEvento.getActionCommand( );
	        if( OPCION_1.equals( comando ) )
	        {
	            principal.reqFuncOpcion1( );
	        }
	        else if( OPCION_2.equals( comando ) )
	        {
	            principal.reqFuncOpcion2( );
	        }
	}

}
