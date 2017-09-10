package goatthrower.txmonitor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static Logger logger = LoggerFactory.getLogger(Config.class);
	public static final File txmonHome = new File(System.getProperty("user.home"), ".txmon");
	private static final File config = new File(txmonHome, "var/txmonitor.properties");
	
	public static Config INSTANCE = new Config();
	
	private Properties properties;
	
	private Config() {
		loadPropertiesFromFile();
	}
	
	private void loadPropertiesFromFile() {
		Properties tmp = new Properties();
		
		try (FileReader fr = new FileReader(config)) {
			tmp.load(fr);
			properties = tmp;
		} catch (IOException ex) {
			logger.warn("Could not load properties file", ex);
			System.exit(-1);
		}
	}
	
	public NetworkParameters getNetwork() {
		String net = properties.getProperty("network", "main");
		switch (net) {
		case "main":
			return MainNetParams.get();
		case "test":
			return TestNet3Params.get();
		default:
			throw new IllegalArgumentException("Unknown network " + String.valueOf(net));
		}
	}
	
	public File getAddressesFile() {
		String filePath = properties.getProperty("addressesfile",
				txmonHome.getPath() + "/var/addresses.txt");
		return new File(filePath);
	}
	
	public boolean isEmailNotificationEnabled() {
		return "y".equals(properties.getProperty("smtp.enabled", "n"));
	}
	
	public String getSmtpHost() {
		return properties.getProperty("smtp.host");
	}
	
	public int getSmtpPort() {
		return Integer.parseInt(properties.getProperty("smtp.smtpPort"));
	}
	
	public String getSslSmtpPort() {
		return properties.getProperty("smtp.sslSmtpPort");
	}
	
	public String getSmtpUser() {
		return properties.getProperty("smtp.smtpUser");
	}
	
	public String getSmtpPasswd() {
		return properties.getProperty("smtp.smtpPasswd");
	}
	
	public boolean isSmtpSsl() {
		return "y".equals(properties.getProperty("smtp.ssl"));
	}
	
	public boolean isSmtpTls() {
		return "y".equals(properties.getProperty("smtp.tls"));
	}
	
	public String[] getSmtpTo() {
		String to = properties.getProperty("smtp.to");
		
		return to.split(";", -1);
	}
	
	public String getSmtpFrom() {
		return properties.getProperty("smtp.from");
	}
}
