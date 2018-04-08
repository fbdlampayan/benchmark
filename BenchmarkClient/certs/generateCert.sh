#!/bin/bash -e
set -x
# note: Script uses -batch and -subj, instead of interactive prompts.
rm -f ca.key ca.crt server.key server.csr server.crt client.key client.csr client.crt index.txt serial*
rm -rf certs crl newcerts

export SAN="DNS.1:daas-ems-daas-prototypes.espoo-apps.ilab.cloud, \
    DNS.2:c1-ems.edge.dac.nokia.com, \
    DNS.3:ems.edge.ci-dev.daaas.dynamic.nsn-net.net, \
    DNS.4:ems.ci-dev.daaas.dynamic.nsn-net.net, \
    DNS.5:ems.edge.dc1.dac.nokia.com, \
    DNS.6:ems.edge.ci-dev2.daaas.dynamic.nsn-net.net, \
    DNS.7:ems.edge.dac.nokia.com"


echo "Creating example CA, server cert/key..."

touch index.txt
echo 1000 > serial


openssl req \
	-x509 \
	-sha256 \
	-nodes \
	-newkey rsa:2048 \
	-days 365 \
	-reqexts san \
	-extensions san \
	-subj "/CN=edge.dac.nokia.com" \
	-config openssl.conf \
	-keyout serverkey.pem \
	-out servercert.pem


