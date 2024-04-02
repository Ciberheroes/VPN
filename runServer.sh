#!/bin/bash

javac server/MsgSSLServerSocket.java

java -Djavax.net.ssl.keyStore=~/SSLStore/keystore.jks -Djavax.net.ssl.keyStorePassword=ciberheroes server.MsgSSLServerSocket