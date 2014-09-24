package org.haox.kerb.crypto;

import org.haox.asn1.type.Asn1Type;
import org.haox.kerb.codec.KrbCodec;
import org.haox.kerb.crypto.enc.*;
import org.haox.kerb.spec.KrbException;
import org.haox.kerb.spec.type.common.*;

import java.util.ArrayList;
import java.util.List;

public class EncryptionHandler {

    public static EncryptionType getBestEncryptionType(List<EncryptionType> requestedTypes,
                                                       List<EncryptionType> configuredTypes) {
        for (EncryptionType encryptionType : configuredTypes) {
            if (requestedTypes.contains(encryptionType)) {
                return encryptionType;
            }
        }

        return null;
    }

    public static EncTypeHandler getEncHandler(String eType) throws KrbException {
        EncryptionType eTypeEnum = EncryptionType.fromName(eType);
        return getEncHandler(eTypeEnum);
    }

    public static EncTypeHandler getEncHandler(int eType) throws KrbException {
        EncryptionType eTypeEnum = EncryptionType.fromValue(eType);
        return getEncHandler(eTypeEnum);
    }

    public static EncTypeHandler getEncHandler(EncryptionType eType) throws KrbException {
        return getEncHandler(eType, false);
    }

    private static EncTypeHandler getEncHandler(EncryptionType eType, boolean check) throws KrbException {
        EncTypeHandler encHandler = null;
        switch (eType) {
            case DES_CBC_CRC:
                encHandler = new DesCbcCrcEnc();
                break;

            case DES_CBC_MD5:
            case DES:
                encHandler = new DesCbcMd5Enc();
                break;

            case DES_CBC_MD4:
                encHandler = new DesCbcMd4Enc();
                break;

            case DES3_CBC_SHA1:
            case DES3_CBC_SHA1_KD:
            case DES3_HMAC_SHA1:
                encHandler = new Des3CbcSha1Enc();
                break;

            case AES128_CTS_HMAC_SHA1_96:
            case AES128_CTS:
                encHandler = new Aes128CtsHmacSha1Enc();
                break;

            case AES256_CTS_HMAC_SHA1_96:
            case AES256_CTS:
                encHandler = new Aes256CtsHmacSha1Enc();
                break;

            case CAMELLIA128_CTS_CMAC:
            case CAMELLIA128_CTS:
                encHandler = new Camellia128CtsCmacEnc();
                break;

            case CAMELLIA256_CTS_CMAC:
            case CAMELLIA256_CTS:
                encHandler = new Camellia256CtsCmacEnc();
                break;

            case RC4_HMAC:
            case ARCFOUR_HMAC:
            case ARCFOUR_HMAC_MD5:
                encHandler = new Rc4HmacEnc();
                break;

            case RC4_HMAC_EXP:
            case ARCFOUR_HMAC_EXP:
            case ARCFOUR_HMAC_MD5_EXP:
                encHandler = new Rc4HmacExpEnc();
                break;

            case NONE:
            default:
                break;
        }

        if (encHandler == null && ! check) {
            String message = "Unsupported encryption type: " + eType.name();
            throw new KrbException(KrbErrorCode.KDC_ERR_ETYPE_NOSUPP, message);
        }

        return encHandler;
    }

    public static EncryptedData seal(Asn1Type message, EncryptionKey key, KeyUsage usage) throws KrbException {
        byte[] encoded = KrbCodec.encode(message);
        return encrypt(encoded, key, usage);
    }


    public static EncryptedData encrypt(byte[] plainText, EncryptionKey key, KeyUsage usage) throws KrbException {
        EncTypeHandler handler = getEncHandler(key.getKeyType());
        byte[] cipher = handler.encrypt(plainText, key.getKeyData(), usage.getValue());

        EncryptedData ed = new EncryptedData();
        ed.setCipher(cipher);
        ed.setEType(key.getKeyType());
        ed.setKvno(key.getKvno());

        return ed;
    }

    public static byte[] decrypt(byte[] data, EncryptionKey key, KeyUsage usage) throws KrbException {
        EncTypeHandler handler = getEncHandler(key.getKeyType());

        byte[] plainData = handler.decrypt(data, key.getKeyData(), usage.getValue());
        return plainData;
    }

    public static byte[] decrypt(EncryptedData data, EncryptionKey key, KeyUsage usage) throws KrbException {
        EncTypeHandler handler = getEncHandler(key.getKeyType());

        byte[] plainData = handler.decrypt(data.getCipher(), key.getKeyData(), usage.getValue());
        return plainData;
    }

    public static EncryptionType[] getSupportedEncTypes() {
        return new EncryptionType[0];
    }

    public static List<EncryptionKey> makeEncryptionKeys(
            String principalName, String passPhrase) throws KrbException {
        return makeEncryptionKeys(principalName, passPhrase, getSupportedEncTypes());
    }

    public static List<EncryptionKey> makeEncryptionKeys(
            String principalName, String passPhrase, EncryptionType[] ciphers) throws KrbException {
        List<EncryptionKey> resultKeys = new ArrayList<EncryptionKey>();
        for (EncryptionType encryptionType : ciphers) {
            resultKeys.add(string2Key(principalName, passPhrase, encryptionType));
        }

        return resultKeys;
    }


    public static List<EncryptionKey> makeRandomKeys() throws KrbException {
        return null;
    }

    public static EncryptionKey makeRandomKey(EncryptionType encryptionType) throws KrbException {
        return null;
    }

    public static boolean isImplemented(EncryptionType eType) {
        EncTypeHandler handler = null;
        try {
            handler = getEncHandler(eType, true);
        } catch (KrbException e) {
            return false;
        }
        return  handler != null;
    }

    public static EncryptionKey string2Key(String principalName,
          String passPhrase, EncryptionType eType) throws KrbException {
        PrincipalName principal = new PrincipalName(principalName);
        byte[] keyBytes = stringToKey(passPhrase,
                PrincipalName.makeSalt(principal), null, eType);
        return new EncryptionKey(eType, keyBytes);
    }

    public static byte[] stringToKey(String string, String salt,
                   byte[] s2kparams, EncryptionType eType) throws KrbException {
        EncTypeHandler handler = getEncHandler(eType);
        byte[] keyBytes = handler.str2key(string, salt, s2kparams);
        return keyBytes;
    }

    public static boolean isSupported(EncryptionType eType) {
        EncryptionType[] supportedTypes = getSupportedEncTypes();
        for (int i = 0; i < supportedTypes.length; i++) {
            if (eType == supportedTypes[i]) {
                return true;
            }
        }
        return false;
    }
}