package lu.pata.hsm.hsmlib.cert;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.asn1.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class CSR {
    private BigInteger modulus;
    private BigInteger exponent;
    private String subject;
    private byte[] signature;

    public CSR(String subject,BigInteger modulus,BigInteger exponent){
        this.subject=subject;
        this.modulus=modulus;
        this.exponent=exponent;
    }

    public void export(String fileName) throws IOException{
        ByteArrayOutputStream s = new ByteArrayOutputStream ();
        DERSequenceGenerator seq00 = new DERSequenceGenerator (s);

        seq00.addObject(certificationRequestInfo());
        seq00.addObject(signatureAlgorithm());
        seq00.addObject(signature());
        seq00.close();
        FileUtils.writeByteArrayToFile(new File(fileName),s.toByteArray());
    }

    /**
     * Data for signing
     * @return
     * @throws IOException
     */
    public byte[] getCertificationRequestInfo() throws IOException {
        return certificationRequestInfo().getEncoded();
    }

    public void setSignature(byte[] sig){
        this.signature=sig;
    }

    private DERSequence certificationRequestInfo() throws IOException {
        DERInteger ver=new DERInteger(0);
        byte[] attributes={(byte)0xA0,(byte)0x00}; //CONTEXT SPECIFIC
        return new DERSequence(seqASN1Encodables(ver, getSubject(),subjectPKInfo(),ASN1Primitive.fromByteArray(attributes)));
    }

    private DERSequence getSubject(){
        DERObjectIdentifier cn_oid=new DERObjectIdentifier("2.5.4.3"); //CN
        DERUTF8String cn=new DERUTF8String(subject);
        DERSequence cn_seq=new DERSequence(seqASN1Encodables(cn_oid,cn));
        DERSet cn_set=new DERSet(cn_seq);
        return new DERSequence(cn_set);
    }

    private DERSequence subjectPKInfo(){
        DERObjectIdentifier pki_oid=new DERObjectIdentifier("1.2.840.113549.1.1.1"); //RSA
        DERNull der_null=new DERNull();
        DERSequence algorithm=new DERSequence(seqASN1Encodables(pki_oid,der_null));

        DERInteger modulus_der=new DERInteger(modulus);
        DERInteger exponent_der=new DERInteger(exponent);

        DERBitString subjectPublicKey=new DERBitString(new DERSequence(seqASN1Encodables(modulus_der,exponent_der)));

        return new DERSequence(seqASN1Encodables(algorithm,subjectPublicKey));
    }

    private DERSequence signatureAlgorithm(){
        DERObjectIdentifier sig_oid=new DERObjectIdentifier("1.2.840.113549.1.1.11"); //sha256RSA
        DERNull der_null=new DERNull();
        return new DERSequence(seqASN1Encodables(sig_oid,der_null));
    }

    private DERBitString signature(){
        return new DERBitString(signature);
    }

    private ASN1EncodableVector seqASN1Encodables(ASN1Encodable... el){
        ASN1EncodableVector v=new ASN1EncodableVector();
        for(ASN1Encodable e:el) v.add(e);
        return v;
    }
}
