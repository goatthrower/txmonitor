package goatthrower.txmonitor.notification;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.bitcoinj.core.Address;

@Immutable
public class TxMessage implements Message {
	private static final long serialVersionUID = -8722893493252280337L;

	@Nonnull
	private final Set<Address> addresses;
	private final boolean received;
	private final boolean announced;
	
	private volatile String _message = null;
	
	public TxMessage(@Nonnull Set<Address> addresses, boolean received, boolean announced) {
		this.addresses = new HashSet<>(addresses);
		this.received = received;
		this.announced = announced;
	}
	
	@Nonnull
	public String getHeader() {
		String result = received ? "Incoming tx " : "Outgoing tx ";
		result += announced ? "announced" : "confirmed";
		return result;
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
