import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Crypto.class})

public class TxHandlerOptimized {

    static PublicKey CataPk, FraPk;
    static Transaction tx14, tx17, tx42, tx43;
    static UTXO utxo14_0;

    @BeforeClass
    public static void setup() throws NoSuchProviderException, NoSuchAlgorithmException {
        SideTests st = new SideTests();
        CataPk = st.generatePublicKey();
        FraPk = st.generatePublicKey();

        tx14 = new Transaction();
        tx14.addOutput(2, CataPk);
        //BEWARE! must setHash()!
        tx14.setHash(new byte[8]);

        tx17 = new Transaction();
        tx17.addOutput(1, CataPk);
        tx17.addOutput(1, CataPk);

        tx17.addInput(tx14.getHash(), 0);
        //BEWARE! must sign each input
        tx17.addSignature(new byte[4], 0);
        tx17.setHash(new byte[8]);

        utxo14_0 = new UTXO(tx14.getHash(), 0);

    }

    @Test
    public void testSetup(){
        System.out.println("all good");
    }

    @Test
    public void isValidTxWithNoInputs(){
        //here I am checking the validity of tx14
        UTXOPool genesisUTXOPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(genesisUTXOPool);
        Assert.assertTrue(txHandler.isValidTx(tx14));
    }

    @Test
    public void isValidTxWithValidTx(){
        //here I am checking the validity of tx17
        UTXOPool utxoPoolAfterTx14 = new UTXOPool();
        utxoPoolAfterTx14.addUTXO(utxo14_0, tx14.getOutput(0));
        TxHandler txHandler = new TxHandler(utxoPoolAfterTx14);
        //so far this has no problems

        System.out.println(tx17.getRawDataToSign(0));

        PowerMockito.mockStatic(Crypto.class);




        /*
        Crypto crypto = Mockito.mock(Crypto.class);
        Mockito.when(crypto.v)


        txHandler.isValidTx(tx17);
*/




    }
}
