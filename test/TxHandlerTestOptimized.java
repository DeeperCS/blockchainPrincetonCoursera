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

//lesson for next time we test this sort of thing: make a constructor for txs!
public class TxHandlerTestOptimized {

    static PublicKey CataPk, FraPk;
    static Transaction tx14, tx17, tx42, tx43, tx51, tx52, tx53, tx54;
    static UTXO utxo14_0, utxo17_0, utxo17_1, utxo51_0;

    @BeforeClass
    public static void setup() throws NoSuchProviderException, NoSuchAlgorithmException {
        SideTests st = new SideTests();
        CataPk = st.generatePublicKey();
        FraPk = st.generatePublicKey();

        //next up are valid txs

        //this is a CreateCoins tx
        tx14 = new Transaction();
        tx14.addOutput(2, CataPk);
        //BEWARE! must setHash()!
        tx14.setHash(new byte[8]);

        tx17 = new Transaction();
        tx17.addOutput(1, CataPk);
        tx17.addOutput(1, CataPk);

        tx17.addInput(tx14.getHash(), 0);
        //BEWARE! must sign each input
        byte[] tx17signature = new byte[4];
        tx17.addSignature(tx17signature, 0);
        tx17.setHash(new byte[8]);

        //next are invalid txs
        //this is CreateCoins tx (negative output)
        tx51 = new Transaction();
        tx51.addOutput(-1, CataPk);
        tx51.setHash(new byte[8]);

        //not all inputs are in current utxoPool
        tx52 = new Transaction();
        tx52.addOutput(1, CataPk);
        tx52.addInput(tx14.getHash(), 0);
        byte[] tx52signature = new byte[4];
        tx52.addSignature(tx52signature, 0);
        tx52.setHash(new byte[8]);

        //signatures on input are invalid
        tx53 = new Transaction();
        tx53.addOutput(1, CataPk);
        tx53.addInput(tx14.getHash(), 0);
        byte[] tx53signature = new byte[4];
        tx53.addSignature(tx53signature, 0);
        tx53.setHash(new byte[8]);

        //claims same input twice
        tx54 = new Transaction();
        tx54.addOutput(2, CataPk);
        tx54.addInput(tx14.getHash(), 0);
        tx54.addSignature(new byte[4], 0);
        tx54.addInput(tx14.getHash(), 0);
        tx54.addSignature(new byte[4], 1);
        tx54.setHash(new byte[8]);


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
    public void isValidWithFailingCondition1(){
        UTXOPool genesisUTXOPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(genesisUTXOPool);
        Assert.assertFalse(txHandler.isValidTx(tx52));
    }

    @Test
    public void isValidWithFailingCondition2(){
        UTXOPool utxoPoolAfterTx14 = new UTXOPool();
        utxoPoolAfterTx14.addUTXO(utxo14_0, tx14.getOutput(0));
        TxHandler txHandler = new TxHandler(utxoPoolAfterTx14);

        byte[] message = tx53.getRawDataToSign(0);
        PowerMockito.mockStatic(Crypto.class);
        when(Crypto.verifySignature(CataPk, message, tx53.getInput(0).signature )).thenReturn(false);

        Assert.assertFalse(txHandler.isValidTx(tx53));
    }

    @Test
    public void isValidWithFailingCondition3(){
        UTXOPool utxoPoolAfterTx14 = new UTXOPool();
        utxoPoolAfterTx14.addUTXO(utxo14_0, tx14.getOutput(0));

        TxHandler txHandler = new TxHandler(utxoPoolAfterTx14);

        byte[] message0 = tx54.getRawDataToSign(0);
        PowerMockito.mockStatic(Crypto.class);
        when(Crypto.verifySignature(CataPk, message0, tx54.getInput(0).signature )).thenReturn(true);

        byte[] message1 = tx54.getRawDataToSign(1);
        PowerMockito.mockStatic(Crypto.class);
        when(Crypto.verifySignature(CataPk, message1, tx54.getInput(1).signature )).thenReturn(true);

        Assert.assertFalse(txHandler.isValidTx(tx54));
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
        Assert.assertNotEquals(utxo17_0, utxo17_1);
        Assert.assertEquals(txHandler.utxoPool.getAllUTXO().size(), 2);

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
        Assert.assertTrue(txHandler.utxoPool.contains(utxo14_0));
        Assert.assertEquals(txHandler.utxoPool.getAllUTXO().size(), 1);
    }


    @Test
    public void handleBothValidAndInvalidTxs(){

        //next block is tx17 and tx52
        UTXOPool utxoPoolAfterTx14 = new UTXOPool();
        utxoPoolAfterTx14.addUTXO(utxo14_0, tx14.getOutput(0));
        TxHandler txHandler = new TxHandler(utxoPoolAfterTx14);

        //Next line is deceitful!
        //System.out.println(utxoPoolAfterTx14.getAllUTXO());

        Assert.assertTrue(utxoPoolAfterTx14.contains(utxo14_0));

        byte[] message17 = tx17.getRawDataToSign(0);
        PowerMockito.mockStatic(Crypto.class);
        when(Crypto.verifySignature(CataPk, message17, tx17.getInput(0).signature )).thenReturn(true);

        System.out.println("tx 17 is valid:" + txHandler.isValidTx(tx17));

        Transaction[] possibleTxs = new Transaction[2];
        possibleTxs[0] = tx17;
        possibleTxs[1] = tx52;


        Transaction[] validTransactions = txHandler.handleTxs(possibleTxs);
        //our block had only one valid tx, namely tx17
        Assert.assertEquals(validTransactions.length, 1);

        //next is sanity check on utxoPool after handling txs
        Assert.assertTrue(txHandler.utxoPool.contains(utxo17_0));
        Assert.assertTrue(txHandler.utxoPool.contains(utxo17_1));
        Assert.assertNotEquals(utxo17_0, utxo17_1);
        Assert.assertEquals(txHandler.utxoPool.getAllUTXO().size(), 2);
    }

}
