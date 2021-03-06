package goatthrower.txmonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Service;

import goatthrower.txmonitor.notification.TxNotifier;

public class TxMonitor {
	private Logger logger = LoggerFactory.getLogger(TxMonitor.class);
	
	private final ScheduledExecutorService sched = Executors.newSingleThreadScheduledExecutor();
	
	private WalletAppKit appkit;
	
	public TxMonitor() {
		NetworkParameters params = Config.INSTANCE.getNetwork();
		appkit = new WalletAppKit(params, new File(Config.txmonHome, "var/" + params.getId()), "txmon");
	}
	
	public void startup() {
		Service s = appkit.startAsync();
		s.awaitRunning();
		
		appkit.wallet().addCoinsReceivedEventListener((w, tx, pb, nb) -> handleTransaction(w, tx, true));
		appkit.chain().addNewBestBlockListener(b -> newBlock(b));
		
		sched.scheduleWithFixedDelay(() -> importAddresses(), 0, 1, TimeUnit.MINUTES);
	}
	
	private void newBlock(StoredBlock sb) {
		Peer p = appkit.peerGroup().getConnectedPeers().get(0);
		ListenableFuture<Block> lfb = p.getBlock(sb.getHeader().getHash());
		Block block;
		try {
			block = lfb.get();
			for (Transaction tx : block.getTransactions()) {
				handleTransaction(appkit.wallet(), tx, false);
			}
		} catch (InterruptedException ex) {
			logger.warn("Something went wrong here...", ex);
		} catch (ExecutionException ex) {
			logger.warn("Something went wrong here...", ex);
		}
	}
	
	private void handleTransaction(Wallet wallet, Transaction tx, boolean announced) {
		Set<Address> addresses = new HashSet<>();
		List<TransactionOutput> outputs = tx.getWalletOutputs(wallet);
		for (TransactionOutput o : outputs) {
			Address a = o.getAddressFromP2SH(Config.INSTANCE.getNetwork());
			if (a != null) {
				addresses.add(a);
			}
			
			a = o.getAddressFromP2PKHScript(Config.INSTANCE.getNetwork());
			if (a != null) {
				addresses.add(a);
			}

			try {
				byte[] raw = o.getScriptPubKey().getPubKeyHash();
				a = new Address(Config.INSTANCE.getNetwork(), raw);
				addresses.add(a);
			} catch (RuntimeException ex) {
			}
		}
		
		if (!addresses.isEmpty()) {
			TxNotifier.INSTANCE.sendNotifications(addresses, true, announced);
		}
	}
	
	private void importAddresses() {
		try (FileReader fr = new FileReader(Config.INSTANCE.getAddressesFile())) {
			try (BufferedReader br = new BufferedReader(fr)) {
				br.lines().forEach(s -> importAddressLine(s));
			}
			// finally we delete that file
			Config.INSTANCE.getAddressesFile().delete();
		} catch (FileNotFoundException ex) {
			// OK,  nothing to do
		} catch (IOException ex) {
			logger.warn("Failed to read addresses from import file", ex);
		}
	}
	
	private void importAddressLine(String line) {
		if (line.trim().isEmpty()) {
			return;
		}
		
		if (line.startsWith("xpub")) {
			// TODO xpub
		} else {
			importAddress(line);
		}
	}
	
	private void importAddress(String address) {
		try {
			Address a = Address.fromBase58(Config.INSTANCE.getNetwork(), address);
			appkit.wallet().addWatchedAddress(a);
			logger.info("Added address {} to watchlist", a.toBase58());
		} catch (AddressFormatException ex) {
			logger.warn("Could not import address " + String.valueOf(address), ex);
		}
	}
}
