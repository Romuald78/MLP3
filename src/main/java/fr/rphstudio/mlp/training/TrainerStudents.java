package fr.rphstudio.mlp.training;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;


class InternalResults{
    public String   fullName;
    public double[] past;
    public double[] future;
}

public class TrainerStudents implements ITraining {

    // Private result object
    private List<InternalResults> results;

    private String inputNotes[];
    private String outputNotes[];

    // Constructor
    public TrainerStudents(String inputFileName){
        // Create empty list
        this.results = new ArrayList<>();
        // Prepare Local variables
        String line = null;
        int inputSize = 6;
        int outputSize = 3;
        // Open CSV filename
        try{
            BufferedReader br = new BufferedReader(new FileReader(inputFileName));
            // Read line per line
            line = br.readLine();
            if(line!=null){
                // Prepare labels
                String[] dataLine = line.split(";");
                this.inputNotes = new String[inputSize];
                this.outputNotes = new String[outputSize];
                for(int i=1;i<1+inputSize;i++){
                    this.inputNotes[i-1] = dataLine[i];
                }
                // Store output notes
                for(int i=1+inputSize;i<1+inputSize+outputSize;i++){
                    this.outputNotes[i-1-inputSize] = dataLine[i];
                }
            }
            do{
                line = br.readLine();
                if(line!=null){
                    // Split line and prepare data
                    String[] dataLine = line.split(";");
                    InternalResults data = new InternalResults();

                    // Store full name
                    data.fullName = dataLine[0];
                    // Store input notes
                    data.past = new double[inputSize];
                    for(int i=1;i<1+inputSize;i++){
                        data.past[i-1] = (Integer.parseInt(dataLine[i])-10)/10.0;
                    }
                    // Store output notes
                    data.future = new double[outputSize];
                    for(int i=1+inputSize;i<1+inputSize+outputSize;i++){
                        data.future[i-1-inputSize] = (Integer.parseInt(dataLine[i])-10)/10.0;
                    }
                    // Add student data to current list
                    this.results.add(data);
                }
            } while(line != null);
        }catch(IOException ioex){
            System.out.println("An error occured while trying to read input file '"+inputFileName+"'");
        }
    }

    @Override
    public double getMinLearningRate() {
        return 0.1;
    }

    @Override
    public double getMaxLearningRate() {
        return 0.5;
    }

    @Override
    public double getAllowedError() {
        return 0.01;
    }

    @Override
    public int getNbMaxCorrectDataSet() {
        return 100;
    }

    @Override
    public int getNbMaxBadDataSet() {
        return 10000000;
    }

    @Override
    public int getInputSize() {
        return this.results.get(0).past.length;
    }

    @Override
    public int getOutputSize() {
        return this.results.get(0).future.length;
    }

    @Override
    public int getNbDataSet() {
        return this.results.size();
    }

    @Override
    public double[] getInputDataSet(int num) {
        if(num < 0 || num >= this.getNbDataSet() ){
            throw new InputMismatchException("The number of the input data set is not correct : "+num);
        }
        return this.results.get(num).past;
    }

    @Override
    public double[] getOutputDataSet(int num) {
        if(num < 0 || num >= this.getNbDataSet() ){
            throw new InputMismatchException("The number of the input data set is not correct : "+num);
        }
        return this.results.get(num).future;
    }

    @Override
    public String[] getInputLabels() {
        return this.inputNotes;
    }


    @Override
    public String[] getOutputLabels() {
        return this.outputNotes;
    }

}
