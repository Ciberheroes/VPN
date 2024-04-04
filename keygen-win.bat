@echo off

cd C:\
mkdir SSLStore
cd SSLStore

Keytool -genkey -keystore C:\SSLStore\keystore.jks -alias SSLCertificate -keyalg RSA

