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
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * This program demonstrates using java.util.Properties class to read and write
 * settings for Java application.
 * @author www.codejava.net
 *
 */
public class ConfigSwing extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8159307523223584114L;
	private File configFile = new File("config.properties");
	private Properties configProps;
	
	private JLabel labelPath = new JLabel("Ruta: ");
	private JLabel labelPort = new JLabel("Port number: ");
	private JLabel labelError = new JLabel("Archivo de error: ");
	
	private JTextField textPath = new JTextField(20);
	private JTextField textPort = new JTextField(20);
	private JTextField textError = new JTextField(20);
	
	private JButton buttonSave = new JButton("Save");
	
	public ConfigSwing() {
		super("Configura tu MiniApatxe");
		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 5, 10);
		constraints.anchor = GridBagConstraints.WEST;
		
		add(labelPath, constraints);
		
		constraints.gridx = 1;
		add(textPath, constraints);
		
		constraints.gridy = 1;
		constraints.gridx = 0;
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
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		add(buttonSave, constraints);
		
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
}