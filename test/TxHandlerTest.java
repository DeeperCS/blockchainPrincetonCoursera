import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.*;


import java.security.PublicKey;


public class TxHandlerTest {

    PublicKey Cata;
    PublicKey Fra;

    Transaction tx14, tx17, tx42, tx43;

    UTXO utxo14_0, utxo17_0, utxo17_1;



    @Before
    public void setup(){
        Cata = Mockito.mock(PublicKey.class);
        Fra = Mockito.mock(PublicKey.class);

        //The following three are valid transactions

        //First block
        tx14 = new Transaction();
        tx14.addOutput(2, Cata);

        tx17 = new Transaction();
        tx17.addOutput(1, Cata);
        tx17.addOutput(1, Cata);
        tx17.addInput(tx14.getHash(), 0);

       /* don't deal with this yet
        //Second block
        Transaction tx42 = new Transaction();
        tx42.addOutput(0.7, Fra);
        tx42.addOutput(0.3, Cata);
        tx42.addInput(tx17.getHash(), 1);

        //todo: make invalid transaction*/


       //When processing the first block, first step should be gathering all these utxo's
        utxo14_0 = new UTXO(tx14.getHash(), 0);
        utxo17_0 = new UTXO(tx17.getHash(), 0);
        utxo17_1 = new UTXO(tx17.getHash(), 1);

    }

    @Test
    public void isValidTxWithValidTransactions(){

       //At genesis I haven't processed any transactions so UTXOPool is empty
       UTXOPool genesisUTXOPool = new UTXOPool();

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
