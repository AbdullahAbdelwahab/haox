package org.apache.kerberos.kerb.crypto;

/**
 * Based on MIT krb5 enc_rc4.c
 */
public class Rc4 {

    private static byte[] L40 = "fortybits".getBytes();

    public static byte[] getSalt(int usage, boolean exportable) {
        int newUsage = convertUsage(usage);
        byte[] salt;

        if (exportable) {
            salt = new byte[14];
            System.arraycopy(L40, 0, salt, 0, 9);
            BytesUtil.int2bytes(newUsage, salt, 10, false);
        } else {
            salt = new byte[4];
            BytesUtil.int2bytes(newUsage, salt, 0, false);
        }

        return salt;
    }

    private static int convertUsage(int usage) {
        switch (usage) {
            case 1:  return 1;   /* AS-REQ PA-ENC-TIMESTAMP padata timestamp,  */
            case 2:  return 2;   /* ticket from kdc */
            case 3:  return 8;   /* as-rep encrypted part */
            case 4:  return 4;   /* tgs-req authz data */
            case 5:  return 5;   /* tgs-req authz data in subkey */
            case 6:  return 6;   /* tgs-req authenticator cksum */
            case 7:  return 7;   /* tgs-req authenticator */
            case 8:  return 8;
            case 9:  return 9;   /* tgs-rep encrypted with subkey */
            case 10: return 10;  /* ap-rep authentication cksum (never used by MS) */
            case 11: return 11;  /* app-req authenticator */
            case 12: return 12;  /* app-rep encrypted part */
            case 23: return 13;  /* sign wrap token*/
            default: return usage;
        }
    }
}
