#!/bin/bash

javac client/MsgSSLClientSocket.java

java -Djavax.net.ssl.trustStore=~/SSLStore/keystore.jks -Djavax.net.ssl.trustStorePassword=ciberheroes client.MsgSSLClientSocket