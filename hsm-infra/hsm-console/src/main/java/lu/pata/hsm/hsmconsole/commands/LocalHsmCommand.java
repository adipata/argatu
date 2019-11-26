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

import java.io.IOException;
import java.math.BigInteger;

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

    @ShellMethod("Generate local RSA 2048 client key on HSM. Used to access the server.")
    public void genkey(@ShellOption(help = "Key label") String label) throws PKCS11Exception {
        if(!pkcsManager.isHsmInitialized()) if(!tryHsmInit()) return;
        pkcsManager.generateKeyPair(label);
    }

    @ShellMethod("List all the keys on HSM")
    public void list() throws PKCS11Exception {
        if(!pkcsManager.isHsmInitialized()) if(!tryHsmInit()) return;
        pkcsManager.listObjects();
    }

    @ShellMethod("Create CSR")
    public void csrTest() throws PKCS11Exception, IOException {
        if(!pkcsManager.isHsmInitialized()) if(!tryHsmInit()) return;
        Long o=pkcsManager.getObjByLabelAndClass("test","CKO_PUBLIC_KEY");
        BigInteger modulus=new BigInteger(1,(byte[])pkcsManager.getAttribute(o,"CKA_MODULUS"));
        BigInteger exponent=new BigInteger(1,(byte[])pkcsManager.getAttribute(o,"CKA_PUBLIC_EXPONENT"));
        CSR csr=new CSR("test",modulus,exponent);
        byte[] dataToSign=csr.getCertificationRequestInfo();
        csr.setSignature(pkcsManager.signData(3l,dataToSign));
        csr.export("test_adi.csr");
    }

    private boolean tryHsmInit(){
        out.print("Login to HSM slot "+hsmSlot);
        String pin=in.prompt("Enter PIN","",false);
        return pkcsManager.initHsm(Long.parseLong(hsmSlot),pin.toCharArray());
    }
}
