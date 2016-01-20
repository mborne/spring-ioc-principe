package org.acme.ioc;

import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Producer extends Thread {
	
	static final Logger logger = LogManager.getLogger(Producer.class);
	
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
	
	@Override
	public void run() {
		for ( int i = 0; i < 20; i++ ){
			String message = "#"+i;
			logger.info("Production : {}",message);
			try {
				queue.put(message);
			} catch (InterruptedException e) {
				logger.error("Interruption du producteur!");
				throw new RuntimeException(e);
			}
		}
	}
}
