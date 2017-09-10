package goatthrower.txmonitor.notification;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public interface Message extends Serializable {
	@Nonnull
	public String getHeader();

	@Nonnull
	public String getMessage();
}
