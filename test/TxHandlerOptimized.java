import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Random;


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
        tx14.setHash(new byte[8]);

        tx17 = new Transaction();
        tx17.addOutput(1, Cata);
        tx17.addOutput(1, Cata);
        tx17.addInput(tx14.getHash(), 0);
        tx17.setHash(new byte[8]);
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

    @Test
    public void isValidTxWithValidTx(){
        UTXOPool utxoPoolAfterTx14 = new UTXOPool();

        UTXO utxo14_0 = new UTXO(tx14.getHash(), 0);

        System.out.println("still good");
/*        //in
        tx17.getInput(0);

        UTXO utxo17_0 = new UTXO(tx17.getInput(0).prevTxHash, tx17.getInput(0).outputIndex);
        //UTXO utxo17_1 = new UTXO(tx17.getHash(), 1);
        Assert.assertNotNull(utxo17_0);*/

        //System.out.println(tx17.getHash());
        //UTXO utxo = new UTXO(tx17.getHash())


    }
}
