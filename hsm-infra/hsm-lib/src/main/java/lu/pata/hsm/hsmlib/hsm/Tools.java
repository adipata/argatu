package lu.pata.hsm.hsmlib.hsm;

import iaik.pkcs.pkcs11.wrapper.CK_ATTRIBUTE;
import iaik.pkcs.pkcs11.wrapper.CK_DATE;
import iaik.pkcs.pkcs11.wrapper.PKCS11Constants;

import java.util.LinkedHashMap;
import java.util.Set;

public class Tools {
    private LinkedHashMap<String,Long> attributes=new LinkedHashMap<>();
    private LinkedHashMap<String,Long> classes=new LinkedHashMap<>();
    private LinkedHashMap<String,Long> keyType=new LinkedHashMap<>();

    public int getTemplateSize(){
        return attributes.size();
    }


    public Set<String> getAttributes(){
        return attributes.keySet();
    }

    public CK_ATTRIBUTE[] getTemplate(String... attrs){
        CK_ATTRIBUTE a;
        CK_ATTRIBUTE[] t=new CK_ATTRIBUTE[attrs.length];
        int i=0;
        for(String attr:attrs) {
            a = new CK_ATTRIBUTE();
            a.type = attributes.get(attr);
            t[i] = a;
            i++;
        }

        return t;
    }

    public String getClass(Long v){
        for(String k:classes.keySet()){
            if(classes.get(k).equals(v)) return k;
        }
        return "N/A";
    }

    public Long getClassCode(String objClass){
        return classes.get(objClass);
    }

    public String getKeyType(Long v){
        for(String k:keyType.keySet()){
            if(keyType.get(k).equals(v)) return k;
        }
        return "N/A";
    }

    public CK_ATTRIBUTE[] buildPublicKey(byte[] modulus, byte[] exponent, byte[] value){
        CK_ATTRIBUTE a;
        CK_ATTRIBUTE[] t=new CK_ATTRIBUTE[7];

        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_CLASS;a.pValue=PKCS11Constants.CKO_PUBLIC_KEY;t[0]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_KEY_TYPE;a.pValue=PKCS11Constants.CKK_RSA;t[1]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_WRAP;a.pValue=Boolean.valueOf(true);t[2]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_MODULUS;a.pValue=modulus;t[3]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_PUBLIC_EXPONENT;a.pValue=exponent;t[4]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_TOKEN;a.pValue=Boolean.valueOf(true);t[5]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_LABEL;a.pValue="GPub1_imported".toCharArray();t[6]=a;

        return t;
    }

    public CK_ATTRIBUTE[] buildPublicKey2(byte[] modulus, byte[] exponent, byte[] value){
        CK_ATTRIBUTE a;
        CK_ATTRIBUTE[] t=new CK_ATTRIBUTE[26];

        CK_DATE start=new CK_DATE();
        start.year="2019".toCharArray();
        start.month="01".toCharArray();
        start.day="01".toCharArray();
        CK_DATE end=new CK_DATE();
        end.year="2020".toCharArray();
        end.month="01".toCharArray();
        end.day="01".toCharArray();

        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_CLASS;a.pValue=PKCS11Constants.CKO_PUBLIC_KEY;t[0]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_DERIVE;a.pValue=Boolean.valueOf(false);t[1]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_ENCRYPT;a.pValue=Boolean.valueOf(true);t[2]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_KEY_TYPE;a.pValue=PKCS11Constants.CKK_RSA;t[3]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_LABEL;a.pValue="GPub1_imported".toCharArray();t[4]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_LOCAL;a.pValue=Boolean.valueOf(true);t[5]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_MODIFIABLE;a.pValue=Boolean.valueOf(true);t[6]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_MODULUS;a.pValue=modulus;t[7]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_MODULUS_BITS;a.pValue=Long.valueOf(2048);t[8]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_PRIVATE;a.pValue=Boolean.valueOf(true);t[9]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_PUBLIC_EXPONENT;a.pValue=exponent;t[10]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_TOKEN;a.pValue=Boolean.valueOf(true);t[11]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_TRUSTED;a.pValue=Boolean.valueOf(false);t[12]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_VERIFY;a.pValue=Boolean.valueOf(true);t[13]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_VERIFY_RECOVER;a.pValue=Boolean.valueOf(true);t[14]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_WRAP;a.pValue=Boolean.valueOf(true);t[15]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_START_DATE;a.pValue=start;t[16]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_END_DATE;a.pValue=end;t[17]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_ID;a.pValue=new byte[0];t[18]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_DECRYPT;a.pValue=Boolean.valueOf(false);t[19]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_UNWRAP;a.pValue=Boolean.valueOf(false);t[20]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_SIGN;a.pValue=Boolean.valueOf(false);t[21]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_VERIFY_RECOVER;a.pValue=Boolean.valueOf(true);t[22]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_EXTRACTABLE;a.pValue=Boolean.valueOf(false);t[23]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_KEY_GEN_MECHANISM;a.pValue=Long.valueOf(4294967295l);t[24]=a;
        a=new CK_ATTRIBUTE();a.type= PKCS11Constants.CKA_VALUE;a.pValue=value;t[25]=a;

        return t;
    }

    public Tools(){
        attributes.put("CKA_CLASS",0L);
        attributes.put("CKA_TOKEN",1L);
        attributes.put("CKA_PRIVATE",2L);
        attributes.put("CKA_LABEL",3L);
        attributes.put("CKA_APPLICATION",16L);
        attributes.put("CKA_VALUE",17L);
        attributes.put("CKA_OBJECT_ID",18L);
        attributes.put("CKA_CERTIFICATE_TYPE",128L);
        attributes.put("CKA_ISSUER",129L);
        attributes.put("CKA_SERIAL_NUMBER",130L);
        attributes.put("CKA_AC_ISSUER",131L);
        attributes.put("CKA_OWNER",132L);
        attributes.put("CKA_ATTR_TYPES",133L);
        attributes.put("CKA_TRUSTED",134L);
        attributes.put("CKA_CERTIFICATE_CATEGORY",135L);
        attributes.put("CKA_JAVA_MIDP_SECURITY_DOMAIN",136L);
        attributes.put("CKA_URL",137L);
        attributes.put("CKA_HASH_OF_SUBJECT_PUBLIC_KEY",138L);
        attributes.put("CKA_HASH_OF_ISSUER_PUBLIC_KEY",139L);
        attributes.put("CKA_CHECK_VALUE",144L);
        attributes.put("CKA_KEY_TYPE",256L);
        attributes.put("CKA_SUBJECT",257L);
        attributes.put("CKA_ID",258L);
        attributes.put("CKA_SENSITIVE",259L);
        attributes.put("CKA_ENCRYPT",260L);
        attributes.put("CKA_DECRYPT",261L);
        attributes.put("CKA_WRAP",262L);
        attributes.put("CKA_UNWRAP",263L);
        attributes.put("CKA_SIGN",264L);
        attributes.put("CKA_SIGN_RECOVER",265L);
        attributes.put("CKA_VERIFY",266L);
        attributes.put("CKA_VERIFY_RECOVER",267L);
        attributes.put("CKA_DERIVE",268L);
        attributes.put("CKA_START_DATE",272L);
        attributes.put("CKA_END_DATE",273L);
        attributes.put("CKA_MODULUS",288L);
        attributes.put("CKA_MODULUS_BITS",289L);
        attributes.put("CKA_PUBLIC_EXPONENT",290L);
        attributes.put("CKA_PRIVATE_EXPONENT",291L);
        attributes.put("CKA_PRIME_1",292L);
        attributes.put("CKA_PRIME_2",293L);
        attributes.put("CKA_EXPONENT_1",294L);
        attributes.put("CKA_EXPONENT_2",295L);
        attributes.put("CKA_COEFFICIENT",296L);
        attributes.put("CKA_PRIME",304L);
        attributes.put("CKA_SUBPRIME",305L);
        attributes.put("CKA_BASE",306L);
        attributes.put("CKA_PRIME_BITS",307L);
        attributes.put("CKA_SUB_PRIME_BITS",308L);
        attributes.put("CKA_VALUE_BITS",352L);
        attributes.put("CKA_VALUE_LEN",353L);
        attributes.put("CKA_EXTRACTABLE",354L);
        attributes.put("CKA_LOCAL",355L);
        attributes.put("CKA_NEVER_EXTRACTABLE",356L);
        attributes.put("CKA_ALWAYS_SENSITIVE",357L);
        attributes.put("CKA_KEY_GEN_MECHANISM",358L);
        attributes.put("CKA_MODIFIABLE",368L);
        attributes.put("CKA_ECDSA_PARAMS",384L);
        attributes.put("CKA_EC_PARAMS",384L);
        attributes.put("CKA_EC_POINT",385L);
        attributes.put("CKA_SECONDARY_AUTH",512L);
        attributes.put("CKA_AUTH_PIN_FLAGS",513L);
        attributes.put("CKA_ALWAYS_AUTHENTICATE",514L);
        attributes.put("CKA_WRAP_WITH_TRUSTED",528L);
        attributes.put("CKA_WRAP_TEMPLATE",1073742353L);
        attributes.put("CKA_UNWRAP_TEMPLATE",1073742354L);
        attributes.put("CKA_HW_FEATURE_TYPE",768L);
        attributes.put("CKA_RESET_ON_INIT",769L);
        attributes.put("CKA_HAS_RESET",770L);
        attributes.put("CKA_PIXEL_X",1024L);
        attributes.put("CKA_PIXEL_Y",1025L);
        attributes.put("CKA_RESOLUTION",1026L);
        attributes.put("CKA_CHAR_ROWS",1027L);
        attributes.put("CKA_CHAR_COLUMNS",1028L);
        attributes.put("CKA_COLOR",1029L);
        attributes.put("CKA_BITS_PER_PIXEL",1030L);
        attributes.put("CKA_CHAR_SETS",1152L);
        attributes.put("CKA_ENCODING_METHODS",1153L);
        attributes.put("CKA_MIME_TYPES",1154L);
        attributes.put("CKA_MECHANISM_TYPE",1280L);
        attributes.put("CKA_REQUIRED_CMS_ATTRIBUTES",1281L);
        attributes.put("CKA_DEFAULT_CMS_ATTRIBUTES",1282L);
        attributes.put("CKA_SUPPORTED_CMS_ATTRIBUTES",1283L);
        attributes.put("CKA_ALLOWED_MECHANISMS",1073743360L);
        attributes.put("CKA_VENDOR_DEFINED",2147483648L);

        classes.put("CKO_DATA",0L);
        classes.put("CKO_CERTIFICATE",1L);
        classes.put("CKO_PUBLIC_KEY",2L);
        classes.put("CKO_PRIVATE_KEY",3L);
        classes.put("CKO_SECRET_KEY",4L);
        classes.put("CKO_HW_FEATURE",5L);
        classes.put("CKO_DOMAIN_PARAMETERS",6L);
        classes.put("CKO_MECHANISM",7L);
        classes.put("CKO_VENDOR_DEFINED",2147483648L);

        keyType.put("CKK_RSA",0L);
        keyType.put("CKK_DSA",1L);
        keyType.put("CKK_DH",2L);
        keyType.put("CKK_ECDSA",3L);
        keyType.put("CKK_EC",3L);
        keyType.put("CKK_X9_42_DH",4L);
        keyType.put("CKK_KEA",5L);
        keyType.put("CKK_GENERIC_SECRET",16L);
        keyType.put("CKK_RC2",17L);
        keyType.put("CKK_RC4",18L);
        keyType.put("CKK_DES",19L);
        keyType.put("CKK_DES2",20L);
        keyType.put("CKK_DES3",21L);
        keyType.put("CKK_CAST",22L);
        keyType.put("CKK_CAST3",23L);
        keyType.put("CKK_CAST5",24L);
        keyType.put("CKK_CAST128",24L);
        keyType.put("CKK_RC5",25L);
        keyType.put("CKK_IDEA",26L);
        keyType.put("CKK_SKIPJACK",27L);
        keyType.put("CKK_BATON",28L);
        keyType.put("CKK_JUNIPER",29L);
        keyType.put("CKK_CDMF",30L);
        keyType.put("CKK_AES",31L);
        keyType.put("CKK_BLOWFISH",32L);
        keyType.put("CKK_TWOFISH",33L);
        keyType.put("CKK_VENDOR_DEFINED",2147483648L);
    }
}

