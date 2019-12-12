package lu.pata.hsm.hsmconsole.remote;

import lu.pata.hsm.hsmlib.ServerCommand;
import lu.pata.hsm.hsmlib.ServerCommandResponse;
import org.apache.http.conn.ssl.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RestDispatcher implements CommandDispatcher {
    private Logger log=LoggerFactory.getLogger(RestDispatcher.class);
    private String user;
    private String pass;
    private RestTemplate rt;


    @Override
    public void setUser(String user, String password) {
        this.user=user;
        this.pass=password;
    }

    public ServerCommandResponse cmd_tmp(ServerCommand command) {
        ServerCommandResponse resp=null;
        try {
            RestTest.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

    @Override
    public ServerCommandResponse cmd(ServerCommand command) {
        ServerCommandResponse resp;
        try {
            if(rt==null) rt=restTemplate(user,pass);
            resp=rt.postForObject("https://localhost:8443/cmd",command,ServerCommandResponse.class);
        } catch (KeyStoreException | CertificateException | KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException | IOException e) {
            resp=new ServerCommandResponse();
            resp.setIsError(true);
            resp.setErrorMessage(e.getMessage());
            log.error(e.getMessage());
        }

        return resp;
    }

    private RestTemplate restTemplate(String user,String pass) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        if(user==null || pass==null) throw new KeyManagementException("Cannot load keys for remote access. Please provide username and password.");

        //KeyStore clientStore=KeyStore.getInstance("JKS");
        //clientStore.load(new FileInputStream("data/users.jks"),"".toCharArray());

        //PKCS11Provider padi=new PKCS11Provider("e:\\kit\\SecurityServerEvaluation-V4.30.1.2\\Software\\Windows\\x86-64\\Crypto_APIs\\PKCS11_R2\\lib\\cs_pkcs11_R2.dll");
        //Security.addProvider(padi);

        Provider prototype = Security.getProvider("SunPKCS11");
        Provider provider = prototype.configure("pkcs11.txt");
        Security.addProvider(provider);

        KeyStore clientStore=KeyStore.getInstance("PKCS11",provider);
        clientStore.load(null, "418214".toCharArray());

        KeyStore trusted=KeyStore.getInstance("JKS");
        trusted.load(new FileInputStream("data/trusted.jks"),"".toCharArray());

        SSLContextBuilder sslContextBuilder=new SSLContextBuilder();
        sslContextBuilder.useProtocol("TLS");
        sslContextBuilder.loadKeyMaterial(clientStore,pass.toCharArray(),new SelectByAlias(user));
        sslContextBuilder.loadTrustMaterial(trusted);

        SSLConnectionSocketFactory sslConnectionSocketFactory=new SSLConnectionSocketFactory(sslContextBuilder.build());
        CloseableHttpClient httpClient= HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        HttpComponentsClientHttpRequestFactory requestFactory=new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(10000);
        return new RestTemplate(requestFactory);
    }

    private static final class SelectByAlias implements PrivateKeyStrategy {
        private Set<String> keyAliases = new HashSet<>();

        public SelectByAlias(String[] keyAliases) {
            for (String k : keyAliases) {
                this.keyAliases.add(k);
            }
        }

        public SelectByAlias(String keyAlias) {
            this.keyAliases.add(keyAlias);
        }

        @Override
        public String chooseAlias(Map<String, PrivateKeyDetails> aliases, Socket socket) {
            for (String k : aliases.keySet()) {
                if (keyAliases.contains(k)) {
                    return k;
                }
            }
            return null;
        }
    }
}

