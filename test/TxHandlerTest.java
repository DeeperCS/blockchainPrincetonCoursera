import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;


import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class TxHandlerTest {

    PublicKey Cata;
    PublicKey Fra;

    Transaction txToStartTests, tx14, tx17, tx42, tx43;
    UTXO utxoToStartTests, utxo14_0, utxo17_0, utxo17_1;






    @Before
    public void setup(){






        Cata = Mockito.mock(PublicKey.class);
        Mockito.when(Cata.equals(null)).thenReturn(false);
        Fra = Mockito.mock(PublicKey.class);


        txToStartTests = new Transaction();
        txToStartTests.addOutput(8675309, Fra);

        utxoToStartTests = new UTXO(txToStartTests.getHash(), 0);


        //The following three are valid transactions

        //First block
        tx14 = new Transaction();
        tx14.addOutput(2, Cata);

        //Mockito.when(Crypto.verifySignature(Cata, tx14.getRawDataToSign(0), null)).thenReturn(true);


/*        tx17 = new Transaction();
        tx17.addOutput(1, Cata);
        tx17.addOutput(1, Cata);
        tx17.addInput(tx14.getHash(), 0);*/

       /* don't deal with this yet
        //Second block
        Transaction tx42 = new Transaction();
        tx42.addOutput(0.7, Fra);
        tx42.addOutput(0.3, Cata);
        tx42.addInput(tx17.getHash(), 1);

        //todo: make invalid transaction*/

/*
       //When processing the first block, first step should be gathering all these utxo's
        utxo14_0 = new UTXO(tx14.getHash(), 0);
        utxo17_0 = new UTXO(tx17.getHash(), 0);
        utxo17_1 = new UTXO(tx17.getHash(), 1);*/

    }

    @Test
    public void isValidTxWithValidTransactions(){

       UTXOPool genesisUTXOPool = new UTXOPool();
       genesisUTXOPool.addUTXO(utxoToStartTests, txToStartTests.getOutput(0));
       TxHandler txHandler = new TxHandler(genesisUTXOPool);



        System.out.println(txHandler.isValidTx(tx14));

    }




    //test isValidTx() with valid transactions



    //1. to make TxHandler

 /*   @Test
    public void testMethod(){
        UTXOPool utxoPool = new UTXOPool();
        //System.out.println(utxoPool);
        TxHandler txHandler = new TxHandler(utxoPool);

        Transaction tx = new Transaction();
        Transaction[] possibleTx = new Transaction[1];
        possibleTx[0]=tx;

        //System.out.println(txHandler.isValidTx(tx));
        txHandler.updateCandidatePool(tx);
        //System.out.println(txHandler.candidateUtxoPool);

        Transaction txNull = null;
        System.out.println(txHandler.isValidTx(txNull));

        System.out.println(txHandler.handleTxs(possibleTx));


    }*/


}
