package goatthrower.txmonitor.notification;

import javax.annotation.Nonnull;

public interface NotificationChannel {
	public void notify(@Nonnull Message m);
}
