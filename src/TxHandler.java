import java.util.ArrayList;
import java.util.List;

public class TxHandler {

    UTXOPool candidateUtxoPool;


    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        //first step is to make a "defensive" copy of utxoPool:
        candidateUtxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid,
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     * values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {

        double totalValueOfInputs = 0;
        double totalValueOfOutputs = 0;

        for (int index = 0; index < tx.numInputs(); index++){
            Transaction.Input input = tx.getInput(index);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            Transaction.Output previousOutput;
            //condition (1)
            if (candidateUtxoPool.contains(utxo)) {
                previousOutput = candidateUtxoPool.getTxOutput(utxo);
                //condition (2)
                if (Crypto.verifySignature(previousOutput.address, tx.getRawDataToSign(index), input.signature)){
                    totalValueOfInputs =+ previousOutput.value;}
                else return false;
            }
            else return false;
        }

        for (int index = 0; index < tx.numOutputs(); index ++){
            Transaction.Output output = tx.getOutput(index);
            totalValueOfOutputs =+ output.value;
        }

        //condition (5)
        return (totalValueOfInputs >= totalValueOfOutputs);



    /*    Damn it! Can't use forEach as you need indexes for everything!

        //Loop through all inputs
        tx.getInputs().forEach(
                input -> {  UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
                            Transaction.Output output;
                            //condition (1)
                            if (candidateUtxoPool.contains(utxo)) {
                                output = candidateUtxoPool.getTxOutput(utxo);
                                //condition (2)
                                Crypto.verifySignature(output.address, tx.getRawDataToSign() , input.signature);

                                sumValueOfInput(tx);

                            }
                            else return false;
                }
        );
*/
    }


    public boolean checkValidityConditions(Transaction.Input input){

        //Condition (1)
        UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
        if (candidateUtxoPool.contains(utxo) ) return true;

        //Condition (2)
        Crypto.verifySignature()
        else return false;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        /* LOGIC:
        1. loop through all transactions and enlarge candidate pool
        2. loop through all transactions again checking each for validity
            if valid update candidate pool
        */

        List<Transaction> validTransactions = new ArrayList<>();
        Transaction[] validTransactionsAsArray = (Transaction[]) validTransactions.toArray();

        candidateUtxoPool = makeCandidatePool(possibleTxs);

        for (Transaction tx : possibleTxs){
            if (isValidTx(tx)) {
                validTransactions.add(tx);
                updateCandidatePool(tx);
            }
        }
        return validTransactionsAsArray;
    }


    public UTXOPool makeCandidatePool(Transaction[] possibleTxs) {
        for (Transaction tx : possibleTxs) {
            int numberOfOutputs = tx.numOutputs();
            for (int i = 0; i < numberOfOutputs; i++) {
                UTXO utxo = new UTXO(tx.getHash(), i);
                Transaction.Output output = tx.getOutput(i);
                candidateUtxoPool.addUTXO(utxo, output);
            }
        }
        return candidateUtxoPool;
    }

    public UTXOPool updateCandidatePool(Transaction tx){

    }
}
