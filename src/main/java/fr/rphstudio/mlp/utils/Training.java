package fr.rphstudio.mlp.utils;

import fr.rphstudio.mlp.MLP;
import fr.rphstudio.mlp.except.TrainingFailureException;
import fr.rphstudio.mlp.training.ITraining;

public class Training {

    public static void trainMLP(MLP mlp, ITraining trainer) throws TrainingFailureException {
        Training.trainMLP(mlp,trainer,false);
    }

    public static void trainMLP(MLP mlp, ITraining trainer, boolean isMinErrorDisplayed) throws TrainingFailureException {
        // scramble the weights and bias
        mlp.scramble();

        // Train with ITraining interface
        double learningRate = trainer.getMaxLearningRate();
        double err    = 10000;
        double errMin = 10000;
        int countOK  = 0;
        int countBAD = 0;
        while(     countOK  < trainer.getNbMaxCorrectDataSet()
                && countBAD < trainer.getNbMaxBadDataSet()
                ){
            // get random data set number
            int r = (int)(Math.random()*trainer.getNbDataSet());
            // Get input and output arrays from random data set number
            double[] input  = trainer.getInputDataSet(r);
            double[] output = trainer.getOutputDataSet(r);
            // Set inputs and process forward
            mlp.setInputs(input);
            mlp.processForward();
            // Back propagation + retrieve error value
            err = mlp.backPropagation(output, learningRate);

            // Set learning rate according to error (in a specific range)
            learningRate = err/10;
            learningRate = Math.max(learningRate, trainer.getMinLearningRate());
            learningRate = Math.min(learningRate, trainer.getMaxLearningRate());
            // update minimal error
            if(errMin > err){
                errMin = err;
                if(isMinErrorDisplayed) {
                    System.out.println(errMin);
                }
            }
            // Update count
            if( err < trainer.getAllowedError() ){
                countOK ++;
                countBAD = 0;
            }
            else{
                countOK = 0;
                countBAD++;
            }
        }
        // Check if the training was a failure
        if( countBAD >= trainer.getNbMaxBadDataSet() ){
            throw new TrainingFailureException();
        }
    }

}
