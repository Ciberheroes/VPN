@echo off

javac server\MsgSSLServerSocket.java

java "-Djavax.net.ssl.keyStore=C:\SSLStore\keystore.jks" "-Djavax.net.ssl.keyStorePassword=ciberheroes" server.MsgSSLServerSocket
