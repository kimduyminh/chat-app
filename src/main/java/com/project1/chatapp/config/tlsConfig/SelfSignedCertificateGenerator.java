package com.project1.chatapp.config.tlsConfig;

import sun.security.x509.*;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

public class SelfSignedCertificateGenerator {

    public static X509Certificate generate(KeyPair keyPair, String dname, int validityDays) throws Exception {
        PrivateKey privateKey = keyPair.getPrivate();

        X509CertInfo info = new X509CertInfo();
        Date from = new Date();
        Date to = new Date(from.getTime() + validityDays * 24L * 60L * 60L * 1000L);
        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger serialNumber = new BigInteger(64, new java.security.SecureRandom());
        X500Name owner = new X500Name(dname);

        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber));
        info.set(X509CertInfo.SUBJECT, owner);
        info.set(X509CertInfo.ISSUER, owner);
        info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        AlgorithmId algo = new AlgorithmId(AlgorithmId.SHA512_oid);
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

        // Sign the certificate to identify the algorithm that's used.
        X509CertImpl cert = new X509CertImpl(info);
        cert.sign(privateKey, "SHA256withRSA");

        // Update the algorithm, and resign.
        algo = (AlgorithmId) cert.get(X509CertImpl.SIG_ALG);
        info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
        cert = new X509CertImpl(info);
        cert.sign(privateKey, "SHA256withRSA");
        return cert;
    }
}