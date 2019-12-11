package lu.pata.hsm.hsmconsole.remote;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

public class RestTest {

    static void test() throws Exception{
        var keyManagerFactory = buildKeyManagerFactory();

        var trustManagerFactory = buildTrustManagerFactory();

        var sslContext = buildSslContext(keyManagerFactory, trustManagerFactory);

        var url = new URL("https://localhost:8443/cmd");
        var urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

        try (var urlInput = urlConnection.getInputStream()) {
            System.out.println(new String(urlInput.readAllBytes()));
        }
    }

    static KeyManagerFactory buildKeyManagerFactory() throws UnrecoverableKeyException,
            NoSuchAlgorithmException,
            KeyStoreException,
            IOException,
            CertificateException {
        Provider prototype = Security.getProvider("SunPKCS11");
        Provider provider = prototype.configure("pkcs11.txt");
        Security.addProvider(provider);
        KeyStore clientStore=KeyStore.getInstance("PKCS11");
        clientStore.load(null, "123456".toCharArray());

        var keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm()
        );

        var keyPass = "".toCharArray();
        keyManagerFactory.init(clientStore, keyPass);

        return keyManagerFactory;
    }


    static TrustManagerFactory buildTrustManagerFactory() throws KeyStoreException,
            IOException,
            NoSuchAlgorithmException,
            CertificateException {
        var storeType = "jks";
        var trustStore = KeyStore.getInstance(storeType);

        var storePath = "data/trusted.jks";
        try (var fis = new FileInputStream(storePath)) {
            trustStore.load(fis, null);
        }

        var trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
        );

        trustManagerFactory.init(trustStore);

        return trustManagerFactory;
    }

    static SSLContext buildSslContext(
            KeyManagerFactory keyManagerFactory,
            TrustManagerFactory trustManagerFactory) throws KeyManagementException,
            NoSuchAlgorithmException {
        var sslContext = SSLContext.getInstance("TLSv1.3");

        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                null
        );

        return sslContext;
    }
}
