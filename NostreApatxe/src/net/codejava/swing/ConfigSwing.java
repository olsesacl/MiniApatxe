package net.codejava.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class ConfigSwing extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8159307523223584114L;
	private File configFile = new File("config.properties");
	private Properties configProps;
	
	private JLabel labelPath = new JLabel("Ruta: ");
	private JLabel labelPort = new JLabel("Puerto: ");
	private JLabel labelError = new JLabel("Archivo de error: ");
	
	private JTextField textPath = new JTextField(15);
	private JTextField textPort = new JTextField(15);
	private JTextField textError = new JTextField(15);
	
	private JButton buttonSave = new JButton("Guardar");
	
	private JButton buttonRun = new JButton("Iniciar");
	private JButton buttonStop = new JButton("Parar");
	private JButton buttonClear = new JButton("Limpiar log");
	
	private JTextArea textArea = new JTextArea(15, 35);
	JScrollPane scroll = new JScrollPane (textArea);
	//DefaultListModel model = new DefaultListModel();
	//private JList list = new JList(model);
    //private JScrollPane scrollPane = new JScrollPane(list);
    
    
	private NostreApatxe apache = null;
	private Thread hiloApache = null;
	
	public ConfigSwing() {
		super("Configura tu MiniApatxe");
		
		//list = new JList(model);

        //JScrollPane scrollPane = new JScrollPane(list);
        //scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //list.setSelectionModel((ListSelectionModel) new DisabledItemSelectionModel());
		
		//scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    //list.setSelectionModel((ListSelectionModel) new DisabledItemSelectionModel());        
        
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 5, 10);
		constraints.anchor = GridBagConstraints.WEST;
		
		add(labelPath, constraints);
		
		constraints.gridx = 1;
		constraints.gridwidth = 2;
		add(textPath, constraints);
		
		constraints.gridy = 1;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		add(labelPort, constraints);
		
		constraints.gridx = 1;
		add(textPort, constraints);

		constraints.gridy = 2;
		constraints.gridx = 0;
		add(labelError, constraints);
		
		constraints.gridx = 1;
		add(textError, constraints);
		
		constraints.gridy = 4;
		constraints.gridx = 0;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		add(buttonSave, constraints);
		
		constraints.gridx = 1;
		constraints.anchor = GridBagConstraints.CENTER;
		add(buttonRun, constraints);
		
		constraints.gridx = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		buttonStop.setEnabled(false);
		add(buttonStop, constraints);
		
		constraints.gridx = 3;
		constraints.anchor = GridBagConstraints.CENTER;
		add(buttonClear, constraints);
		
		
		constraints.gridy = 6;
		constraints.gridx = 0;
		constraints.gridwidth = 4;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, constraints);
        //add(scrollPane, constraints);
		
		buttonSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					saveProperties();
					JOptionPane.showMessageDialog(ConfigSwing.this, 
							"La configuracion se han guardado correctamente!");	
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(ConfigSwing.this, 
							"Error guardando el archivo de configuracion: " + ex.getMessage());		
				}
			}
		});
		
		buttonRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String localIP = displayInterfaceInformation();
					textArea.setText("Iniciando servidor en ip "+ localIP +" y puerto " + textPort.getText()+ "\n"+ textArea.getText());
					textArea.setText("Para verificar su funcionamiento escriba en su navegador:\n" + textArea.getText());
					textArea.setText(localIP + ":" + textPort.getText() + "/" + textError.getText() +"\n"+ textArea.getText());
					//model.addElement("Iniciando servidor");
					
					apache = new NostreApatxe(textPath.getText(), Integer.parseInt(textPort.getText()), textError.getText(), textArea);
					//verificamos si existe el fichero, sino creamos uno por defecto
					apache.checkErrorFile();
					
					
					//arrancamos el servidor en otro thread
					hiloApache = new Thread(apache);
					
					hiloApache.start();
					buttonStop.setEnabled(true);
					buttonRun.setEnabled(false);
					buttonSave.setEnabled(false);
					
					/*JOptionPane.showMessageDialog(ConfigSwing.this, 
							"La configuracion se han guardado correctamente!");*/
				} catch (Exception ex) {
					/*JOptionPane.showMessageDialog(ConfigSwing.this, 
							"Error al iniciar el servidor: " + ex.getMessage());*/	
					textArea.setText("Error al iniciar el servidor: " + ex.getMessage() + "\n"+ textArea.getText());
					
					//model.addElement("Error al iniciar el servidor: " + ex.getMessage());
				}
			}
		});
		
		buttonStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					textArea.setText("Parando servidor\n"+ textArea.getText());
					//model.addElement("Parando Servidor");
					
					hiloApache.interrupt();
					apache.cerrar();
					buttonStop.setEnabled(false);
					buttonRun.setEnabled(true);
					buttonSave.setEnabled(true);

				} catch (Exception ex) {
					textArea.setText("Error al cerrar el servidor: " + ex.getMessage() + "\n");
					
					//model.addElement("Error al cerrar el servidor: " + ex.getMessage());
				}
			}
		});
		
		buttonClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textArea.setText("");
			}
		});
		
		
		/*textArea.addCaretListener(new CaretListener() {

	        @Override
	        public void caretUpdate(CaretEvent e) {
	            System.out.println("text field have changed");

			}
	    });*/
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		try {
			loadProperties();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "El archivo de configuracion no existe, se cargaran los datos por defecto.");
		}
		textPath.setText(configProps.getProperty("PATH"));
		textPort.setText(configProps.getProperty("PORT"));
		textError.setText(configProps.getProperty("ERRORFILE"));
	}

	private void loadProperties() throws IOException {
		Properties defaultProps = new Properties();
		// sets default properties
		defaultProps.setProperty("PATH", "./");
		defaultProps.setProperty("PORT", "7000");
		defaultProps.setProperty("ERRORFILE", "error.html");
		
		configProps = new Properties(defaultProps);
		
		// loads properties from file
		InputStream inputStream = new FileInputStream(configFile);
		configProps.load(inputStream);
		inputStream.close();
	}
	
	private void saveProperties() throws IOException {
		configProps.setProperty("PATH", textPath.getText());
		configProps.setProperty("PORT", textPort.getText());
		configProps.setProperty("ERRORFILE", textError.getText());
		OutputStream outputStream = new FileOutputStream(configFile);
		configProps.store(outputStream, "CONFIGURACION MINIAPACHE");
		outputStream.close();
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ConfigSwing();
			}
		});
	}
	
	static String displayInterfaceInformation() throws SocketException {
		
		int check =0;
		
		Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)){
        	
		
	        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	        	
	        	if(!inetAddress.equals("127.0.0.1") && !inetAddress.equals("127.0.1.1") && check>0){
	        		String ip = String.valueOf(inetAddress);
	        		
	        		return ip.substring(1,ip.length());
	        	}
	        		
	        	
	        	check++;
	        }
        	/*for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                System.out.printf("InetAddress: %s\n", inetAddress);
            }*/
	        
        }
		return null;
	}
}