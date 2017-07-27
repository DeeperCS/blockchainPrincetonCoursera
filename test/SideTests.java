import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * 27-7-2017.
 */
public class SideTests {

    //generate key following documentation at  https://docs.oracle.com/javase/tutorial/security/apisign/step2.html

    public PublicKey generatePublicKey() throws NoSuchProviderException, NoSuchAlgorithmException{
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();
        return pub;
    }

    @Test
    public void testPublicKeyGen() throws NoSuchProviderException, NoSuchAlgorithmException {
        System.out.println(generatePublicKey());
    }

}
