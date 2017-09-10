package goatthrower.txmonitor.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.bitcoinj.core.Address;

import goatthrower.txmonitor.Config;
import goatthrower.txmonitor.notification.email.SecureEmailNotificationChannel;

public class TxNotifier {
	public static TxNotifier INSTANCE = new TxNotifier();
	
	private final List<NotificationChannel> channels;

	private TxNotifier() {
		channels = Collections.unmodifiableList(initializeChannels());
	}
	
	@Nonnull
	private List<NotificationChannel> initializeChannels() {
		List<NotificationChannel> result = new ArrayList<>();
		if (Config.INSTANCE.isEmailNotificationEnabled()) {
			result.add(new SecureEmailNotificationChannel());
		}
		return result;
	}
	
	public void sendNotifications(@Nonnull Set<Address> addresses,
			boolean received, boolean announced) {
		Message m = new TxMessage(addresses, received, announced);
		for (NotificationChannel c : channels) {
			c.notify(m);
		}
	}
}
