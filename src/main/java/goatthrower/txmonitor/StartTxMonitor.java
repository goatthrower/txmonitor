package goatthrower.txmonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartTxMonitor {
	private static Logger logger = LoggerFactory.getLogger(StartTxMonitor.class);
	
	public static void main(String[] args) {
		logger.info("Using network {}", Config.INSTANCE.getNetwork().getId());
		
		TxMonitor txm = new TxMonitor();
		txm.startup();
		
		logger.info("TxMonitor started");
	}
}
