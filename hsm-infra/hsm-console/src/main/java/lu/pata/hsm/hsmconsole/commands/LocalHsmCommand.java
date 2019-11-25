package lu.pata.hsm.hsmconsole.commands;

import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;
import lu.pata.hsm.hsmconsole.config.InputReader;
import lu.pata.hsm.hsmconsole.config.OutputPrinter;
import lu.pata.hsm.hsmlib.hsm.PkcsManager;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

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
    public void genkey(@ShellOption(help = "Key label") String label,@ShellOption(help = "Key ID as HEX byte array") String ids) throws PKCS11Exception {
        if(!pkcsManager.isHsmInitialized()) if(!tryHsmInit()) return;

        byte[] id;

        try {
            id=Hex.decodeHex(ids);
        } catch (DecoderException e) {
            log.error("ID is not a HEX byte array (ex. AABB0033) : "+e.getMessage());
            return;
        }

        pkcsManager.generateKeyPair(label,id);
    }

    private boolean tryHsmInit(){
        out.print("Login to HSM slot "+hsmSlot);
        String pin=in.prompt("Enter PIN","",false);
        return pkcsManager.initHsm(Long.parseLong(hsmSlot),pin.toCharArray());
    }
}
