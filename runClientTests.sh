#!/bin/bash

javac client/MsgSSLClientSocketTests.java

java -Djavax.net.ssl.trustStore=$HOME/SSLStore/keystore.jks -Djavax.net.ssl.trustStorePassword=ciberheroes client.MsgSSLClientSocket