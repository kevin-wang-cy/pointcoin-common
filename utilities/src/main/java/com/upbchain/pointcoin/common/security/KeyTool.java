package com.upbchain.pointcoin.common.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.Base64;

/**
 *
 * @author kevin.wang.cy@gmail.com
 *
 */
public class KeyTool {
    private static final Logger LOG = LoggerFactory.getLogger(KeyTool.class);
    private final KeyStore keystore;

    protected KeyTool(@NotNull KeyStore keystore) {
        this.keystore = keystore;
    }

    public static KeyStore loadKeyStore(@NotNull InputStream keystoreIO, String keystorePass) {
        KeyStore ks = null;

        try {
            ks = KeyStore.getInstance("JCEKS");
            ks.load(keystoreIO, keystorePass.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
           LOG.error("Unable to load keystore", ex);
        }

        return ks;
    }

    public static KeyTool newInstance(@NotNull InputStream keystoreIO, String keystorePass) {
        KeyStore ks = KeyTool.loadKeyStore(keystoreIO, keystorePass);

        return new KeyTool(ks);
    }

    public String signMessage(@NotNull String plainText, @NotNull String signWithAlias, @NotNull String keyPass) {
        try {
            X509Certificate cert = (X509Certificate) this.keystore.getCertificate(signWithAlias);
            PrivateKey privateKey = (PrivateKey) this.keystore.getKey(signWithAlias, keyPass.toCharArray());

            Signature signature = Signature.getInstance((cert.getSigAlgName()));

            signature.initSign(privateKey);

            signature.update(plainText.getBytes("UTF-8"));

            return Base64.getEncoder().encodeToString(signature.sign());

        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | InvalidKeyException | UnsupportedEncodingException | SignatureException | NullPointerException ex) {
            LOG.error("Unable to sign message", ex);
        }

        return null;
    }

    public boolean verifyMessage(@NotNull String plainText, @NotNull String signedMessage, @NotNull String verifyWithAlias) {
        try {
            X509Certificate cert = (X509Certificate) this.keystore.getCertificate(verifyWithAlias);

            Signature signature = Signature.getInstance((cert.getSigAlgName()));

            signature.initVerify(cert.getPublicKey());

            signature.update(plainText.getBytes("UTF-8"));

            byte[] signedBytes = Base64.getDecoder().decode(signedMessage);

            return signature.verify(signedBytes);

        } catch (KeyStoreException | NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException | SignatureException | NullPointerException ex) {
            LOG.error("Unable to verify message", ex);
        }

        return false;
    }

    public boolean importX509Cert(@NotNull InputStream certIO, @NotNull String asAlias) {

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            DataInputStream dis = new DataInputStream(certIO);

            byte[] bytes = new byte[dis.available()];
            dis.readFully(bytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            java.security.cert.Certificate certs =  cf.generateCertificate(bais);

            keystore.setCertificateEntry(asAlias, certs);

            return true;
        }
        catch (IOException | CertificateException | KeyStoreException ex) {
            LOG.error("Unable to import certifcate", ex);

            return false;
        }
    }

    public boolean persistent(@NotNull OutputStream keystoreIO, @NotNull String storepass) throws CertificateException, NoSuchAlgorithmException, KeyStoreException {
        try {
            this.keystore.store(keystoreIO, storepass.toCharArray());

            return true;
        }
        catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException ex) {
            LOG.error("Unable to persistent keystore", ex);

            return false;
        }
    }
}
