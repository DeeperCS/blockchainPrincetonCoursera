import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;


public class TxHandlerOptimized {

    static PublicKey Cata;
    static PublicKey Fra;

    Transaction tx14, tx17, tx42, tx43;

    @BeforeClass
    public static void setup() throws NoSuchProviderException, NoSuchAlgorithmException {
        SideTests st = new SideTests();
        Cata = st.generatePublicKey();
        Fra = st.generatePublicKey();
    }

    @Test
    public void testSetup(){
        System.out.println("all good");
    }
}
