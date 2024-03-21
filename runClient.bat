@echo off

javac client/MsgSSLClientSocket.java

java "-Djavax.net.ssl.trustStore=C:\SSLStore\keystore.jks" "-Djavax.net.ssl.trustStorePassword=ciberheroes" client.MsgSSLClientSocket
