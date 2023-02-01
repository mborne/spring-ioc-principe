package org.acme.ioc;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * Une fenêtre pour l'ajout de message sur la pile 
 * via une interface
 * 
 * @author MBorne
 *
 */
public class Window extends JFrame implements ActionListener {
	
	static final Logger logger = LogManager.getLogger(Window.class);
	
	private BlockingQueue<String> queue ;
	
	private JLabel labelMessage ;
	private JTextField textFieldMessage ;
	private JButton buttonSend;
	
	/**
	 * Construction de l'interface et référencement de la pile
	 * @param queue
	 */
	public Window( BlockingQueue<String> queue){
		super("Window!");
		this.queue = queue ;
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(400, 400);
		
		setLayout( new BorderLayout() ) ;
		labelMessage = new JLabel("Message");
		add(labelMessage, BorderLayout.NORTH);
		
		textFieldMessage = new JTextField();
		add(textFieldMessage,BorderLayout.CENTER);
		
		buttonSend = new JButton("Envoyer!");
		buttonSend.addActionListener(this);
		add(buttonSend,BorderLayout.SOUTH);
	}

	/**
	 * Gestion du clic sur le bouton "Envoyer!"
	 */
	public void actionPerformed(ActionEvent event) {
		String message = textFieldMessage.getText();
		logger.info("Envoi d'un message : {}",message);
		try {
			queue.add(message);
		}catch(IllegalStateException e){
			JOptionPane.showMessageDialog(this,e.getMessage());
		}
	}
	
	
	
}
