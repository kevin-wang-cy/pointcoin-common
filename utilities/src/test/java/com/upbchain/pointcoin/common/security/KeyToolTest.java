package com.upbchain.pointcoin.common.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

import static org.junit.Assert.*;

/**
 * @author kevin.wang.cy@gmail.com
 */
public class KeyToolTest {
    private KeyTool walletKeyTool = null;
    private KeyTool xupkeyTool = null;

    @Before
    public void setup() throws Exception {
        walletKeyTool = KeyTool.newInstance(KeyToolTest.class.getResourceAsStream("/com/upbchain/pointcoin/common/security/wallet.keystore"), "walletstorepass");
        xupkeyTool = KeyTool.newInstance(KeyToolTest.class.getResourceAsStream("/com/upbchain/pointcoin/common/security/xup.keystore"), "xupstorepass");

        assertNotNull(walletKeyTool);
        assertNotNull(xupkeyTool);
    }

    @After
    public void cleanup() throws Exception {
        walletKeyTool = null;
        xupkeyTool = null;
    }

    @Test
    public void singAndVerifyTest() throws Exception {
        String[] plainTexts = {"", "xxxxx", "a=b&c=3&e=123456&f=2012-12111&id=xxxx-xxxuuu-uuu&n=3.14490000",
                  "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
                          + "dfafasdfasdfasdfasfasffdsasaafdfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
        };

        String signedMessage = null;
        for (String plainText : plainTexts) {
            signedMessage = walletKeyTool.signMessage(plainText, "wallet-default-test", "wallletkeypass");
            System.out.println(signedMessage);
            assertNotNull(signedMessage);
            assertEquals(344, signedMessage.length());
            assertTrue(xupkeyTool.verifyMessage(plainText,  signedMessage, "wallet-default-test"));

            signedMessage = walletKeyTool.signMessage(plainText, "wallet-ec-test", "wallletkeypass");
            System.out.println(signedMessage);
            assertNotNull(signedMessage);
            assertEquals(96, signedMessage.length());
            assertTrue(xupkeyTool.verifyMessage(plainText,  signedMessage, "wallet-ec-test"));

            signedMessage = xupkeyTool.signMessage(plainText, "xup-default-test", "xupkeypass");
            System.out.println(signedMessage);
            assertNotNull(signedMessage);
            assertEquals(344, signedMessage.length());
            assertTrue(walletKeyTool.verifyMessage(plainText,  signedMessage, "xup-default-test"));

            // no private key for signing
            signedMessage = walletKeyTool.signMessage(plainText, "xup-zhuhai-test", "wallletkeypass");
            assertTrue(signedMessage == null);

            signedMessage = xupkeyTool.signMessage(plainText, "wallet-zhuhai-test", "xupkeypass");
            assertTrue(signedMessage == null);

            // incorrect key pass
            signedMessage = walletKeyTool.signMessage(plainText, "wallet-zhuhai-test", "in-correct-password");
            assertTrue(signedMessage == null);

            // no public key for verifying
            signedMessage = walletKeyTool.signMessage(plainText, "wallet-zhuhai-test", "wallletkeypass");
            assertNotNull(signedMessage);
            assertFalse(xupkeyTool.verifyMessage(plainText,  signedMessage, "wallet-zhuhai-test"));

            // incorrect public key for verifying
            signedMessage = walletKeyTool.signMessage(plainText, "wallet-ec-test", "wallletkeypass");
            assertNotNull(signedMessage);
            assertFalse(xupkeyTool.verifyMessage(plainText,  signedMessage, "wallet-default-test"));
        }


    }


    @Test
    public void importAndPersistentTest() throws Exception {
        InputStream certIO = KeyToolTest.class.getResourceAsStream("/com/upbchain/pointcoin/common/security/wallet-zhuhai-test.cert");

        assertTrue(xupkeyTool.importX509Cert(certIO, "imported-wallet-zhuhai-test"));

        String plainText = "any message here";
        String signedMessage = walletKeyTool.signMessage(plainText, "wallet-zhuhai-test", "wallletkeypass");

        assertTrue(xupkeyTool.verifyMessage(plainText,  signedMessage, "imported-wallet-zhuhai-test"));

        Path tmpFile = Files.createTempFile("xx-", "-xx");

        try (OutputStream tmpIO = Files.newOutputStream(tmpFile)) {
            assertTrue(xupkeyTool.persistent(tmpIO, "tmpstorepass"));
        }

        KeyTool tmpxupkeytool = KeyTool.newInstance(Files.newInputStream(tmpFile), "tmpstorepass");

        assertNotNull(tmpxupkeytool);

        assertTrue(tmpxupkeytool.verifyMessage(plainText,  signedMessage, "imported-wallet-zhuhai-test"));

        signedMessage = tmpxupkeytool.signMessage(plainText, "xup-default-test", "xupkeypass");
        System.out.println(signedMessage);
        assertNotNull(signedMessage);
        assertEquals(344, signedMessage.length());
        assertTrue(walletKeyTool.verifyMessage(plainText,  signedMessage, "xup-default-test"));
    }
}