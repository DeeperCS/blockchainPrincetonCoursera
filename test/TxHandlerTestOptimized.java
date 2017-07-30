import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Crypto.class})

public class TxHandlerTestOptimized {

    static PublicKey CataPk, FraPk;
    static Transaction tx14, tx17, tx42, tx43, tx51;
    static UTXO utxo14_0, utxo17_0, utxo17_1;

    @BeforeClass
    public static void setup() throws NoSuchProviderException, NoSuchAlgorithmException {
        SideTests st = new SideTests();
        CataPk = st.generatePublicKey();
        FraPk = st.generatePublicKey();

        //this is a CreateCoins tx that is invalid as it has a negative input
        tx51 = new Transaction();
        tx51.addOutput(-1, CataPk);
        //BEWARE! must setHash()!
        tx51.setHash(new byte[8]);

        //this is a CreateCoins tx that is valid
        tx14 = new Transaction();
        tx14.addOutput(2, CataPk);
        tx14.setHash(new byte[8]);

        tx17 = new Transaction();
        tx17.addOutput(1, CataPk);
        tx17.addOutput(1, CataPk);

        tx17.addInput(tx14.getHash(), 0);
        //BEWARE! must sign each input
        byte[] tx17signature = new byte[4];
        tx17.addSignature(tx17signature, 0);
        tx17.setHash(new byte[8]);

        utxo14_0 = new UTXO(tx14.getHash(), 0);

        utxo17_0 = new UTXO(tx17.getHash(), 0);
        utxo17_1 = new UTXO(tx17.getHash(), 1);

    }

    @Test
    public void testSetup(){
        System.out.println("all good");
    }


    @Test
    public void isValidTxWithInvalidCreateCoinsTx(){
        //here I am checking the validity of tx51
        UTXOPool genesisUTXOPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(genesisUTXOPool);
        Assert.assertFalse(txHandler.isValidTx(tx51));
    }


    @Test
    public void isValidTxWithValidCreateCoinsTx(){
        //here I am checking the validity of tx14
        UTXOPool genesisUTXOPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(genesisUTXOPool);
        Assert.assertTrue(txHandler.isValidTx(tx14));
    }

    @Test
    public void handleTxsWithSingleValidTransaction(){
        //here I am evaluating whether tx17 gets handled correctly
        UTXOPool utxoPoolAfterTx14 = new UTXOPool();
        utxoPoolAfterTx14.addUTXO(utxo14_0, tx14.getOutput(0));
        TxHandler txHandler = new TxHandler(utxoPoolAfterTx14);

        //Next line is deceitful!
        //System.out.println(utxoPoolAfterTx14.getAllUTXO());

        Assert.assertTrue(utxoPoolAfterTx14.contains(utxo14_0));
        byte[] message = tx17.getRawDataToSign(0);

        PowerMockito.mockStatic(Crypto.class);
        when(Crypto.verifySignature(CataPk, message, tx17.getInput(0).signature )).thenReturn(true);

        System.out.println("tx 17 is valid:" + txHandler.isValidTx(tx17));

        Transaction[] possibleTxs = new Transaction[1];
        possibleTxs[0] = tx17;

        Transaction[] validTransactions = txHandler.handleTxs(possibleTxs);
        //our block had only one valid tx, namely tx17
        Assert.assertEquals(validTransactions.length, 1);

        //next is sanity check on utxoPool after handling txs
        Assert.assertTrue(txHandler.utxoPool.contains(utxo17_0));
        Assert.assertTrue(txHandler.utxoPool.contains(utxo17_1));
        Assert.assertFalse(utxo17_0.equals(utxo17_1));

    }

    @Test
    public void handleTxsWithSingleInvalidTransaction() {
        //say that tx14 (valid) happened before tx51
        UTXOPool utxoPoolAfterTx14 = new UTXOPool();
        utxoPoolAfterTx14.addUTXO(utxo14_0, tx14.getOutput(0));
        TxHandler txHandler = new TxHandler(utxoPoolAfterTx14);

        Transaction[] possibleTxs = new Transaction[1];
        possibleTxs[0] = tx51;

        Transaction[] validTransactions = txHandler.handleTxs(possibleTxs);
        //our block had only 0 valid tx, namely tx51
        Assert.assertEquals(validTransactions.length, 0);
        Assert.assertTrue(utxoPoolAfterTx14.contains(utxo14_0));
    }

}
