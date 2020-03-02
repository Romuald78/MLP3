package fr.rphstudio.mlp.utils;

import fr.rphstudio.mlp.MLP;
import fr.rphstudio.mlp.except.TrainingFailureException;
import fr.rphstudio.mlp.training.ITraining;
import fr.rphstudio.mlp.training.ITraining.*;

public class Training {

    public static TrainResult trainMLP(MLP mlp, ITraining trainer) {
        return Training.trainMLP(mlp,trainer,false,1000000000);
    }

    public static TrainResult trainMLP(MLP mlp, ITraining trainer, boolean display) {
        return Training.trainMLP(mlp,trainer,display,1000000000);
    }

    public static TrainResult trainMLP(MLP mlp, ITraining trainer, int maxT) {
        return Training.trainMLP(mlp,trainer,false, maxT);
    }

    public static TrainResult trainMLP(MLP mlp, ITraining trainer, boolean isMinErrorDisplayed, int maxTrainings) {
        return Training.trainMLP(mlp, trainer, isMinErrorDisplayed, maxTrainings, false);
    }

    public static TrainResult trainMLP(MLP mlp, ITraining trainer, boolean isMinErrorDisplayed, int maxTrainings, boolean isOrdered) {
        // Train with ITraining interface
        double learningRate = trainer.getMaxLearningRate();
        double err    = 10000;
        double errMin = 10000;
        int countOK   = 0;
        int countBAD  = 0;
        int nbTrains  = 0;
        int r         = 0;
        while(     countOK  < trainer.getNbMaxCorrectDataSet()
                && countBAD < trainer.getNbMaxBadDataSet()
                && nbTrains < maxTrainings
                ){
            // increase nbtrains
            nbTrains++;
            // get random data set number
            if(isOrdered){
                r = (r+1)%trainer.getNbDataSet();
            }
            else{
                r = (int)(Math.random()*trainer.getNbDataSet());
            }
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
                    System.out.println(">>> "+ errMin);
                }
            }
            /*
            if(err > trainer.getAllowedError()){
                System.out.println("!!! "+ err);
            }
            */
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
        // display MLP
        if(isMinErrorDisplayed) {
            System.out.println(mlp);
        }
        // Check if the training was a failure
        if( countBAD >= trainer.getNbMaxBadDataSet() ){
            return TrainResult.MAX_ERROR;
        }
        else if(countOK >= trainer.getNbMaxCorrectDataSet() ){
            return TrainResult.LEVEL_OK;
        }
        else{
            return TrainResult.MAX_ITERATION;
        }
    }

}
