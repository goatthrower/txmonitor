package goatthrower.txmonitor.notification.email;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import goatthrower.txmonitor.Config;
import goatthrower.txmonitor.notification.Message;
import goatthrower.txmonitor.notification.NotificationChannel;

public class SecureEmailNotificationChannel implements NotificationChannel {
	private static Logger logger = LoggerFactory.getLogger(SecureEmailNotificationChannel.class);
	
	private ScheduledExecutorService sched = Executors.newSingleThreadScheduledExecutor();
	
	public SecureEmailNotificationChannel() {
	}
	
	@Override
	public void notify(@Nonnull Message m) {
		sched.schedule(new NotificationTask(m), 0, TimeUnit.SECONDS);
	}
	
	private void doSend(@Nonnull Message m) throws EmailException {
		Email mail = new SimpleEmail();
		mail.setHostName(Config.INSTANCE.getSmtpHost());
		mail.setSmtpPort(Config.INSTANCE.getSmtpPort());
		mail.setSslSmtpPort(Config.INSTANCE.getSslSmtpPort());
		mail.setAuthentication(Config.INSTANCE.getSmtpUser(), Config.INSTANCE.getSmtpPasswd());
		mail.setSSLOnConnect(Config.INSTANCE.isSmtpSsl());
		mail.setStartTLSEnabled(Config.INSTANCE.isSmtpTls());
		
		mail.addTo(Config.INSTANCE.getSmtpTo());
		mail.setFrom(Config.INSTANCE.getSmtpFrom());
		
		mail.setSubject(m.getHeader());
		mail.setMsg(m.getMessage());
		
		mail.send();
	}
	
	private class NotificationTask implements Runnable {
		private final Message m;
		private volatile int retriesLeft = 2;
		
		private NotificationTask(@Nonnull Message m) {
			this.m = m;
		}
		
		@Override
		public void run() {
			try {
				doSend(m);
			} catch (EmailException ex) {
				logger.warn("Could not send message via mail", ex);
				if (retriesLeft > 0) {
					retriesLeft--;
					sched.schedule(this, 5, TimeUnit.MINUTES);
				} else {
					logger.warn("Skiping message '{} - {}' since too many delivery attempts failed!",
							m.getHeader(), m.getMessage());
				}
			}
		}
	}
}
