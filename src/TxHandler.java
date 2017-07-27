import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * (1) all outputs (my note: do they mean inputs?) claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid,
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     * values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {

        //todo: figure out this value!
        if (tx == null) return false;

        double totalValueOfInputs = 0;
        double totalValueOfOutputs = 0;
        Set<UTXO> spentUTXO = new HashSet<>();

        //todo: first figure out what to do with createCoin transactions (have no input)
        /*
        * Conditions (1), (2), (3) and (5) don't apply
        */
        if (tx.numInputs()==0) return true;


        for (int index = 0; index < tx.numInputs(); index++) {
            Transaction.Input input = tx.getInput(index);
            UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            spentUTXO.add(utxo);
            Transaction.Output previousOutput;
            //condition (1)
            if (candidateUtxoPool.contains(utxo)) {
                previousOutput = candidateUtxoPool.getTxOutput(utxo);
                //condition (2)
                if (Crypto.verifySignature(previousOutput.address, tx.getRawDataToSign(index), input.signature)) {
                    totalValueOfInputs = +previousOutput.value;
                } else return false;
            } else return false;
        }

        for (int index = 0; index < tx.numOutputs(); index++) {
            Transaction.Output output = tx.getOutput(index);
            //condition (4)
            if (output.value < 0) return false;
            totalValueOfOutputs = +output.value;
        }

        //condition (3)
        if (spentUTXO.size() < tx.numInputs()) return false;

        //condition (5)
        return (totalValueOfInputs >= totalValueOfOutputs);
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

        int numberOfPossibleTransactions = possibleTxs.length;

        Transaction[] validTransactions = new Transaction[numberOfPossibleTransactions];
        //List<Transaction> validTransactions = new ArrayList<>();

        addAllUTXOtoCandidatePool(possibleTxs);

        for (int i = 0; i < numberOfPossibleTransactions ; i++){
            Transaction tx = possibleTxs[i];
            if (isValidTx(tx)){
                validTransactions[i] = tx;          //should I care if there are nulls in some spaces?
                updateCandidatePool(tx);
            }
        }

        return validTransactions;

    }

/*    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        *//* LOGIC:
        1. loop through all transactions and enlarge candidate pool
        2. loop through all transactions again checking each for validity
        if valid update candidate pool
        *//*

        Transaction[] validTransactions = new Transaction[1];



        addAllUTXOtoCandidatePool(possibleTxs);

        for (Transaction tx : possibleTxs){
            if (isValidTx(tx)) {
                validTransactions[0] = tx;
                updateCandidatePool(tx);
            }
        }
        return validTransactions;

    }*/






    //this method adds ALL UTXOs consumed in possibleTxs to candidateUtxoPool, whether the tx is valid or not
    public void addAllUTXOtoCandidatePool(Transaction[] possibleTxs) {
        for (Transaction tx : possibleTxs) {
            int numberOfOutputs = tx.numOutputs();
            for (int i = 0; i < numberOfOutputs; i++) {
                UTXO utxo = new UTXO(tx.getHash(), i);
                Transaction.Output output = tx.getOutput(i);
                candidateUtxoPool.addUTXO(utxo, output);
            }
        }
    }


    public void updateCandidatePool(Transaction tx){
        //since tx is valid, as you find an utxo you can go ahead and remove it from the candidatePool
        tx.getInputs().forEach(input -> {UTXO utxo = new UTXO(input.prevTxHash, input.outputIndex);
            candidateUtxoPool.removeUTXO(utxo);});
    }
}
