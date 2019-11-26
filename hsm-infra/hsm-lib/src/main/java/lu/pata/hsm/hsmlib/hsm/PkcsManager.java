package lu.pata.hsm.hsmlib.hsm;

import iaik.pkcs.pkcs11.wrapper.*;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;

public class PkcsManager {
    private Logger log= LoggerFactory.getLogger(PkcsManager.class);

    private PKCS11 pkcs11;
    private Long ses;

    private Hsm hsm;
    private Tools tool;

    private boolean hsmInitialized=false;

    public PkcsManager(Hsm hsm, Tools tool){
        this.hsm=hsm;
        this.tool=tool;
    }

    public boolean initHsm(long slot,char[] pin){
        try {
            hsm.initHsm(slot, pin);
            this.pkcs11 = hsm.getPkcs11();
            this.ses = hsm.getSes();
            hsmInitialized=true;
        } catch (PKCS11Exception | IOException e) {
            hsm.close();
            log.error("Error initializing HSM: "+e.getMessage());
        }

        return hsmInitialized;
    }

    public boolean isHsmInitialized() {
        return hsmInitialized;
    }

    public Long getObjByLabelAndClass(String objLabel,String objClass) throws PKCS11Exception {
        CK_ATTRIBUTE[] template=tool.getTemplate("CKA_LABEL","CKA_CLASS");
        template[0].pValue=objLabel.toCharArray();
        template[1].pValue=tool.getClassCode(objClass);

        pkcs11.C_FindObjectsInit(ses,template,false);
        long[] objs=pkcs11.C_FindObjects(ses,100);
        pkcs11.C_FindObjectsFinal(ses);

        if(objs.length>0)
            return objs[0];
        else
            return null;
    }

    public void listObjects() throws PKCS11Exception {
        pkcs11.C_FindObjectsInit(ses,null,false);
        long[] objs=pkcs11.C_FindObjects(ses,100);
        for(long o:objs) {
            String label=new String((char[])getAttribute(o,"CKA_LABEL"));
            log.info(o+":"+tool.getClass((Long) getAttribute(o,"CKA_CLASS"))+" - "+label);
        }
        pkcs11.C_FindObjectsFinal(ses);
    }

    public Object getAttribute(Long objectId,String attribute) throws PKCS11Exception {
        CK_ATTRIBUTE[] template=tool.getTemplate(attribute);
        pkcs11.C_GetAttributeValue(ses, objectId, template, false);
        return template[0].pValue;
    }

    public long[] generateKeyPair(String label) throws PKCS11Exception {
        Random rnd=new Random();
        byte[] id=new byte[10];
        rnd.nextBytes(id);

        CK_MECHANISM m=new CK_MECHANISM();
        m.mechanism= PKCS11Constants.CKM_RSA_PKCS_KEY_PAIR_GEN;

        CK_ATTRIBUTE a;
        CK_ATTRIBUTE[] privt=new CK_ATTRIBUTE[5];
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_LABEL;a.pValue=(label).toCharArray();privt[0]=a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_SENSITIVE;a.pValue=Boolean.valueOf(true);privt[1]=a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_EXTRACTABLE;a.pValue=Boolean.valueOf(true);privt[2]=a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_TOKEN;a.pValue=Boolean.valueOf(true);privt[3]=a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_ID;a.pValue=id;privt[4]=a;

        CK_ATTRIBUTE[] pubt=new CK_ATTRIBUTE[4];
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_LABEL;a.pValue=(label).toCharArray();pubt[0]=a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_MODULUS_BITS;a.pValue=Long.valueOf(2048);pubt[1]=a;
        //a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_ECDSA_PARAMS;a.pValue="secp192r1".toCharArray();pubt[1]=a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_TOKEN;a.pValue=Boolean.valueOf(true);pubt[2]=a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_ID;a.pValue=id;pubt[3]=a;

        return pkcs11.C_GenerateKeyPair(ses,m,pubt,privt,false);
    }

    public byte[] signData(Long key,byte[] data) throws PKCS11Exception {
        CK_MECHANISM m=new CK_MECHANISM();
        m.mechanism= PKCS11Constants.CKM_SHA256_RSA_PKCS;

        pkcs11.C_SignInit(ses,m,key,false);
        return pkcs11.C_Sign(ses,data);
    }

    public RSAPublicKey getPublicKey(int id) throws PKCS11Exception, NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger modulus=new BigInteger(1,(byte[])getAttribute((long)id,"CKA_MODULUS"));
        BigInteger exponent=new BigInteger(1,(byte[])getAttribute((long)id,"CKA_PUBLIC_EXPONENT"));

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, exponent);
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);

        return key;
    }

    public void uploadCertificate(X509Certificate cert, String label, String id) throws PKCS11Exception, CertificateEncodingException {
        CK_ATTRIBUTE[] certificate = new CK_ATTRIBUTE[10];

        CK_ATTRIBUTE a;

        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_CLASS;a.pValue=PKCS11Constants.CKO_CERTIFICATE; certificate[0] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_TOKEN;a.pValue=true; certificate[1] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_SUBJECT;a.pValue=cert.getSubjectX500Principal().getEncoded(); certificate[2] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_ID;a.pValue= Hex.decode(id); certificate[3] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_VALUE;a.pValue=cert.getEncoded(); certificate[4] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_PRIVATE;a.pValue=false; certificate[5] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_LABEL;a.pValue=label.toCharArray(); certificate[6] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_ISSUER;a.pValue=cert.getIssuerX500Principal().getEncoded(); certificate[7] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_SERIAL_NUMBER;a.pValue=cert.getSerialNumber().toByteArray(); certificate[8] = a;
        a=new CK_ATTRIBUTE();a.type=PKCS11Constants.CKA_CERTIFICATE_TYPE;a.pValue=PKCS11Constants.CKC_X_509; certificate[9] = a;

        pkcs11.C_CreateObject(ses, certificate,false);
    }
}

