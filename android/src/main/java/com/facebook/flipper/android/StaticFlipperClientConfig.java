package com.facebook.flipper.android;

import java.io.ByteArrayInputStream;

import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import java.util.Base64;

import com.facebook.flipper.core.FlipperObject;

public class StaticFlipperClientConfig {

    public static final String USE_STATIC_CONFIG = "USE_STATIC_CONFIG";

    private StaticFlipperClientConfig() {
    }

    public static boolean enabled = false;
    public static String clientId = "UNKNOWN";
    public static String caCertifcate = null;
    public static String clientAlias = null;
    public static String clientCertificate = null;
    public static String clientKey = null;
    public static String clientKeyPassword = null;
    public static String clientKeyType = null;

    public static boolean isEnabled() {
        return enabled;
    }

    public static FlipperObject getAuthenticationObject() {
        return new FlipperObject.Builder()
                // TODO: add deviuceid
                .put("certificates_ca_path",
                        USE_STATIC_CONFIG)
                // .put("certificates_ca_certificate",
                // serverCertifcate)
                .put("certificates_client_path",
                        USE_STATIC_CONFIG)
                // .put("certificates_client_certificate",
                // clientCertificate)
                // .put("certificates_client_key",
                // clientKey)
                // .put("certificates_client_key_type",
                // clientKeyType)
                .put("certificates_client_pass",
                        clientKeyPassword)
                .build();
    }

    public static void loadClientKeyStore(KeyStore ks) {
        try {
            ks.load(null, null);

            X509Certificate clientCert = null;
            RSAPrivateKey clientKey = null;

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyFactory keyFactory = KeyFactory.getInstance(clientKeyType != null ? clientKeyType : "RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
                    Base64.getDecoder().decode(StaticFlipperClientConfig.clientKey));
            clientKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

            try (ByteArrayInputStream certStream = new ByteArrayInputStream(
                    Base64.getDecoder().decode(StaticFlipperClientConfig.clientCertificate))) {
                clientCert = (X509Certificate) certificateFactory.generateCertificate(certStream);
            }
            ks.setKeyEntry(clientAlias, clientKey, clientKeyPassword.toCharArray(),
                    new Certificate[] { clientCert });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Certificate getCaCertificate() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            try (ByteArrayInputStream caInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(
                    caCertifcate))) {
                return certificateFactory.generateCertificate(caInputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // private byte[] generateCSR(String sigAlg, KeyPair keyPair) {
    // ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    // PrintStream printStream = new PrintStream(outStream);

    // try {
    // X500Name x500Name = new X500Name("CN=EXAMPLE.COM");

    // Signature sig = Signature.getInstance(sigAlg);

    // sig.initSign(keyPair.getPrivate());

    // PKCS10 pkcs10 = new PKCS10(keyPair.getPublic());
    // // pkcs10.encodeAndSign(new X500Signer(sig, x500Name)); // For Java 6
    // pkcs10.encodeAndSign(x500Name, sig); // For Java 7 and Java 8
    // pkcs10.print(printStream);

    // byte[] csrBytes = outStream.toByteArray();

    // return csrBytes;
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // } finally {
    // if (null != outStream) {
    // try {
    // outStream.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // if (null != printStream) {
    // printStream.close();
    // }
    // }

    // return new byte[0];
    // }

}
