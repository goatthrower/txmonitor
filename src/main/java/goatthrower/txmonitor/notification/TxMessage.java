package goatthrower.txmonitor.notification;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.bitcoinj.core.Address;

@Immutable
public class TxMessage implements Message {
	@Nonnull
	private final Set<Address> addresses;
	private final boolean received;
	
	private volatile String _message = null;
	
	public TxMessage(@Nonnull Set<Address> addresses, boolean received) {
		this.addresses = new HashSet<>(addresses);
		this.received = received;
	}
	
	@Nonnull
	public String getHeader() {
		return received ? "Incoming tx" : "Outgoing tx";
	}
	
	@Nonnull
	public String getMessage() {
		if (_message != null) {
			return _message;
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Activity on address(es) ");
		
		boolean first = true;
		for (Address a : addresses) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(a.toBase58());
			first = false;
		}
		
		_message = sb.toString();
		
		return _message;
	}
}
