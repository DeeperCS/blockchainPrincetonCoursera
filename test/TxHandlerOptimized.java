import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;


public class TxHandlerOptimized {

    static PublicKey Cata, Fra;
    static Transaction tx14, tx17, tx42, tx43;

    @BeforeClass
    public static void setup() throws NoSuchProviderException, NoSuchAlgorithmException {
        SideTests st = new SideTests();
        Cata = st.generatePublicKey();
        Fra = st.generatePublicKey();

        tx14 = new Transaction();
        tx14.addOutput(2, Cata);
    }

    @Test
    public void testSetup(){
        System.out.println("all good");
    }

    @Test
    public void isValidTxWithNoInputs(){
        UTXOPool genesisUTXOPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(genesisUTXOPool);
        Assert.assertTrue(txHandler.isValidTx(tx14));
    }
}
