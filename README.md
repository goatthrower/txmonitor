# txmonitor

TxMonitor is a simple headless tool which notifies you when a bitcoin address is used on the network. TxMonitor makes use of the incredible [bitcoinj](https://github.com/bitcoinj/bitcoinj) library.

## Requirements

* Pretty much any kind of computer, for example a Raspberry Pi
* (obviously an) internet connection
* Java 8 JRE or JDK, for example openJDK

## Build from source

First you need to download and compile everything. An internet connection is mandatory.

1. Checkout
2. Execute `./gradlew build`
3. Check for distribution in `build/distributions`. You should see a tar and a zip file.

## Install from distribution tar / zip

1. Extract either the tar or zip, depending on your OS. zip will work on all OSes.
2. Go into the `bin` directory and start TxMonitor by executing the batch or shell script. TxMonitor start up fails and quits with an error message since config file is missing.
3. Go into the `var` directory. There is a template file called `txmonitor.properties`. Copy that file to `~/.txmon/var/txmonitor.properties` and edit that file according to your needs.
4. Start txmon again and it should run without any issues.
5. Optional setup some service, init-scripts, daemon stuff, ...

## Watch addresses

To watch new addresses just create a file called `addresses.txt` in the `var` directory. The file shall contain one address per line. You can watch normal addresses (like 1abcde...), segwit addresses (3abcde...) and deterministic wallets (xpub...).

TxMonitor checks once every minute for that file and imports all addresses and then deletes the file.

Please note that you cannot add private keys!

## Notification channels

At the moment there is only one notification channel available: email. Maybe support for jabber will be added, too.
