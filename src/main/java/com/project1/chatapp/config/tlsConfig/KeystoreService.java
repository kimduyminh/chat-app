package com.project1.chatapp.config.tlsConfig;

import lombok.Getter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Service

public class KeystoreService {
    @Getter
    private String keyPass;
    @Getter
    private String storePass;
    private Random rand = new Random();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public void generateKeystore() throws Exception {
        String[] randomList = {"nowdotheflashbangdance", "ez4enceenceence", "fortinaitielebabeje", "whatdouseebeforeitsover?", "cuzimsalty&sweetttt", "tellmewheretogowhattodooo?"};
        String alias = "creammjnkxD";
        keyPass = "nowdotheflashbangdance"; //randomList[rand.nextInt(randomList.length)];
        storePass = "ez4enceenceence"; //randomList[rand.nextInt(randomList.length)];
        String dname = "CN=localhost";
        String keystoreFile = "./src/main/resources/keystore.p12";

        System.out.println("Generated keyPass: " + keyPass);
        System.out.println("Generated storePass: " + storePass);

        // Generate key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(4096);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Create self-signed certificate
        X509Certificate certificate = generateSelfSignedCertificate(keyPair, dname, 3650);

        // Create keystore and set the key entry
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        keyStore.load(null, storePass.toCharArray());
        keyStore.setKeyEntry(alias, keyPair.getPrivate(), keyPass.toCharArray(), new java.security.cert.Certificate[]{certificate});

        // Store the keystore
        try (FileOutputStream fos = new FileOutputStream(keystoreFile)) {
            keyStore.store(fos, storePass.toCharArray());
            System.out.println("Keystore generated successfully!");
        } catch (Exception e) {
            System.err.println("Failed to generate keystore: " + e.getMessage());
            throw e; // Rethrow the exception or handle as appropriate
        }
    }

    private X509Certificate generateSelfSignedCertificate(KeyPair keyPair, String dname, int validityDays) throws Exception {
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + validityDays * 24L * 60L * 60L * 1000L);

        X500Name dnName = new X500Name(dname);
        BigInteger certSerialNumber = BigInteger.valueOf(now);

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                dnName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic()
        );

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA").build(keyPair.getPrivate());

        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certBuilder.build(contentSigner));
    }

    public static void main(String[] args) {
        KeystoreService keystoreService = new KeystoreService();
        try {
            keystoreService.generateKeystore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}