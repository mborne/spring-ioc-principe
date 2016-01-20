package org.acme.ioc;

import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Consumer extends Thread {
	
	static final Logger logger = LogManager.getLogger(Consumer.class);
	
	private BlockingQueue<String> queue ;
	
	/**
	 * @return the queue
	 */
	public BlockingQueue<String> getQueue() {
		return queue;
	}

	/**
	 * @param queue the queue to set
	 */
	public void setQueue(BlockingQueue<String> queue) {
		this.queue = queue;
	}
	
	private void processMessage() throws InterruptedException{
		String message = queue.poll() ;
		if ( null != message ){
			logger.info("Consommation : {}",message);
		}else{
			logger.info("Aucun message en attente");
		}
		Thread.sleep(1000);
	}
	
	@Override
	public void run() {
		logger.info("Consommation des messages...");
		try {
			while ( true ){
				processMessage();
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
