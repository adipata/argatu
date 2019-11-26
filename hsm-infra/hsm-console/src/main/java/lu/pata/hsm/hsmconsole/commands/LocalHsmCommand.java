package lu.pata.hsm.hsmconsole.commands;

import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;
import lu.pata.hsm.hsmconsole.config.InputReader;
import lu.pata.hsm.hsmconsole.config.OutputPrinter;
import lu.pata.hsm.hsmlib.cert.CSR;
import lu.pata.hsm.hsmlib.hsm.PkcsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.naming.InvalidNameException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;

@ShellComponent
public class LocalHsmCommand {
    private Logger log= LoggerFactory.getLogger(LocalHsmCommand.class);

    @Autowired
    private PkcsManager pkcsManager;

    @Autowired
    private OutputPrinter out;
    @Autowired
    private InputReader in;

    @Value("${hsm.slot}")
    private String hsmSlot;
    @ShellMethod("Initialize the connection with the local HSM.")
    public void initHsm(){
        tryHsmInit();
    }

    @ShellMethod("Generate local RSA 2048 client key on HSM. Generate a PKCS10 request.")
    public void genkey(@ShellOption(help = "Key label") String label) throws PKCS11Exception, IOException {
        if(!pkcsManager.isHsmInitialized()) if(!tryHsmInit()) return;
        pkcsManager.generateKeyPair(label);

        //Generate CSR
        Long pubKey=pkcsManager.getObjByLabelAndClass(label,"CKO_PUBLIC_KEY");
        Long privKey=pkcsManager.getObjByLabelAndClass(label,"CKO_PRIVATE_KEY");
        BigInteger modulus=new BigInteger(1,(byte[])pkcsManager.getAttribute(pubKey,"CKA_MODULUS"));
        BigInteger exponent=new BigInteger(1,(byte[])pkcsManager.getAttribute(pubKey,"CKA_PUBLIC_EXPONENT"));
        CSR csr=new CSR(label,modulus,exponent);
        byte[] dataToSign=csr.getCertificationRequestInfo();
        csr.setSignature(pkcsManager.signData(privKey,dataToSign));
        csr.export(label+".csr");
        out.print(label+".csr exported");
    }

    @ShellMethod("Import PKCS7")
    public void importCert(
            @ShellOption(help = "PKCS7 response file") String fileName,
            @ShellOption(help = "Certificate to upload",defaultValue = "na") String cn) throws FileNotFoundException, CertificateException, InvalidNameException, PKCS11Exception {
        FileInputStream is = new FileInputStream( fileName );
        CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
        Iterator it = cf.generateCertificates( is ).iterator();
        while ( it.hasNext() )
        {
            X509Certificate c = (X509Certificate)it.next();
            if(cn.equals("na")) {
                out.print(c.getSubjectX500Principal().getName());
            } else {
                if(c.getSubjectX500Principal().getName().equals("CN="+cn)){
                    //LdapName dn = new LdapName(c.getSubjectX500Principal().getName());
                    //String label=dn.getRdn(0).getValue().toString(); //TODO: better parsing for CN
                    if(!pkcsManager.isHsmInitialized()) if(!tryHsmInit()) return;
                    Long pubKey=pkcsManager.getObjByLabelAndClass(cn,"CKO_PUBLIC_KEY");
                    byte[] id=(byte[])pkcsManager.getAttribute(pubKey,"CKA_ID");
                    pkcsManager.uploadCertificate(c,cn,id);
                    out.print("Certificate imported");
                    break;
                }
            }
        }
    }

    @ShellMethod("List all the keys on HSM")
    public void list() throws PKCS11Exception {
        if(!pkcsManager.isHsmInitialized()) if(!tryHsmInit()) return;
        pkcsManager.listObjects();
    }

    @ShellMethod("Test PKCS11 java")
    public void jcert() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        Provider prototype = Security.getProvider("SunPKCS11");
        Provider provider = prototype.configure("pkcs11.txt");

        KeyStore ks=KeyStore.getInstance("PKCS11",provider);
        ks.load(null, "123456".toCharArray());
        for (Iterator<String> it = ks.aliases().asIterator(); it.hasNext(); ) {
            String a = it.next();
            out.print(a);
        }

    }

    private boolean tryHsmInit(){
        out.print("Login to HSM slot "+hsmSlot);
        String pin=in.prompt("Enter PIN","",false);
        return pkcsManager.initHsm(Long.parseLong(hsmSlot),pin.toCharArray());
    }
}
