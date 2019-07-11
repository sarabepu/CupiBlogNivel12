package uniandes.cupi2.blog.servidor.interfaz;

import java.awt.BorderLayout; 
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import uniandes.cupi2.blog.cliente.mundo.Usuario;
public class PanelUsuarios extends JPanel implements ActionListener, ListSelectionListener 
{
	private InterfazServidor principal;
	private JButton actlista;
	public final static String ACTUALIZAR = "ACTUALIZAR";

	private JList listaUsuarios;

	public PanelUsuarios(InterfazServidor pPrincipal)
	{
		setLayout(new BorderLayout());

		principal = pPrincipal;
		this.setBorder(new TitledBorder("Usuarios conectados"));
		setPreferredSize(new Dimension(350, 300));

		actlista = new JButton("Actualizar lista");
		actlista.setActionCommand(ACTUALIZAR);
		actlista.addActionListener(this);



		add(actlista, BorderLayout.SOUTH);

		JScrollPane scroll = new JScrollPane();
		scroll.setPreferredSize(new Dimension(500, 150));
		listaUsuarios = new JList();
		listaUsuarios.addListSelectionListener(this);

		scroll.getViewport().add(listaUsuarios);
		add(scroll, BorderLayout.CENTER);

	}

	public void valueChanged(ListSelectionEvent e)
	{
		if( listaUsuarios.getSelectedValue() != null)
		{
			String lacosa=  listaUsuarios.getSelectedValue().toString();
			String[] split= lacosa.split(":");
			principal.actualizarDatos(split[0]);
		}	
	}

	public void actualizarEncuentros(ArrayList lista) 
	{
		ArrayList nombres = new ArrayList();
		for (int i = 0; i < lista.size(); i++)
		{
			Usuario usuario = (Usuario) lista.get(i);
			if (usuario != null) 
			{
				String login = usuario.darLogin();
				String nombre = usuario.darNombre();
				String apellido = usuario.darApellido();

				String mezcla = login + ": " + apellido + "," + nombre;
				nombres.add(mezcla);
			}
		}
		listaUsuarios.setListData(nombres.toArray());
	}

	@Override
	public void actionPerformed(ActionEvent pEvento) 
	{
		if (pEvento.getActionCommand().equals(ACTUALIZAR))
		{
			principal.actualizarUsuarios();
		}
	}
}

