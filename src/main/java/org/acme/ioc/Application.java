package org.acme.ioc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

	static final Logger logger = LogManager.getLogger(Application.class);
	
	public static void main(String[] args) {
		logger.info("Chargement de l'application...");
		
		// Initialisation du contexte spring
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("/spring/beans.xml");
	
		// Récupération d'un bean et passage de la fenêtre en visible
		Window window = (Window) context.getBean("window");
		window.setVisible(true);
	}
	
}
