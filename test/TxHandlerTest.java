import org.junit.Test;
import org.junit.Assert;



public class TxHandlerTest {

    //1. to make TxHandler

    @Test
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


    }


}
