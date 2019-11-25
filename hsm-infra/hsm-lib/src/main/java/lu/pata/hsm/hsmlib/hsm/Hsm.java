package lu.pata.hsm.hsmlib.hsm;

import iaik.pkcs.pkcs11.wrapper.PKCS11;
import iaik.pkcs.pkcs11.wrapper.PKCS11Connector;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;
import iaik.pkcs.pkcs11.wrapper.PKCS11Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Hsm {
    private Logger log=LoggerFactory.getLogger(Hsm.class);

    private PKCS11 pkcs11;
    private Long ses;

    private String libHsm;
    private String libWrapper;

    public Hsm(String libHsm,String libWrapper) {
        this.libHsm=libHsm;
        this.libWrapper=libWrapper;
    }

    public void initHsm(long slot,char[] pin) throws IOException, PKCS11Exception {
        pkcs11 = PKCS11Connector.connectToPKCS11Module(libHsm, libWrapper);
        pkcs11.C_Initialize(null, false);
        ses = pkcs11.C_OpenSession(slot, PKCS11Constants.CKF_SERIAL_SESSION | PKCS11Constants.CKF_RW_SESSION, null, null);
        pkcs11.C_Login(ses, PKCS11Constants.CKU_USER, pin, false);
        log.info("HSM initialized");
    }

    public PKCS11 getPkcs11() {
        return pkcs11;
    }

    public Long getSes() {
        return ses;
    }

    public void close() {
        if(ses!=null)
            try {
                pkcs11.C_Logout(ses);
                pkcs11.C_CloseSession(ses);
                pkcs11.C_Finalize(null);
                log.info("HSM closed");
            } catch (PKCS11Exception e) {
                log.error("Error closing HSM: "+e.getMessage());
            }
    }
}
