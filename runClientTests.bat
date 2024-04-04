@echo off
javac --release 21 --enable-preview -d . client/MsgSSLClientSocketTests.java

java "-Djavax.net.ssl.trustStore=C:\SSLStore\keystore.jks" "-Djavax.net.ssl.trustStorePassword=ciberheroes" client.MsgSSLClientSocketTests
