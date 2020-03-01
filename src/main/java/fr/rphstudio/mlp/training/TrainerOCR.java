package fr.rphstudio.mlp.training;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class TrainerOCR implements ITraining {

    // camera focals
    public static final int THUMB_SIZE = 13;

    // private image buffer with default background color
    public Image[] inputImages;

    public TrainerOCR(){
        try {
            // Create image with default white color
            this.inputImages = new Image[this.getInputSize()];
            // create all pictures
            for(int i=0; i<this.inputImages.length; i++){
                this.inputImages[i] = new Image(TrainerOCR.THUMB_SIZE, TrainerOCR.THUMB_SIZE);
                String car = this.getCharFromDataSet(i);
                this.drawInput(car,this.inputImages[i]);
            }
        } catch(SlickException se){
            throw new Error("Impossible to create input images : "+se);
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
        return 150;
    }

    @Override
    public int getNbMaxBadDataSet() {
        return 10000000;
    }

    @Override
    public int getInputSize() {
        return TrainerOCR.THUMB_SIZE*TrainerOCR.THUMB_SIZE;
    }

    @Override
    public int getOutputSize() {
        // 26 letters, 10 numbers, 1 output for all other characters
        return 10+26+1; //26+10+1;
    }

    @Override
    public int getNbDataSet() {
        // 26 letters
        // 10 numbers
        // 1 unknown character
        return 10+26+1; //26+10;
    }

    public String getCharFromDataSet(int num){
        String car = "";
        if(num < 10){
            // number;
            num += '0';
        }
        else if(num<36){
            // letter
            num -=10;
            num += 'A';
        }
        else{
            num = '?';
        }
        car = Character.toString( (char)num );
        return car;
    }

    public void drawInput(String car, Image img){
        try {
            Graphics g = img.getGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, TrainerOCR.THUMB_SIZE, TrainerOCR.THUMB_SIZE);
            g.setColor(Color.black);
            g.drawString(car, 1, -3);
            g.flush();
        } catch(SlickException se){
            throw new Error("Impossible to get graphics : "+se);
        }
    }

    public double[] getInBufferFromImage(Image img){
        // input buffer
        double[] in = new double[TrainerOCR.THUMB_SIZE*TrainerOCR.THUMB_SIZE];
        for(int x=0;x<TrainerOCR.THUMB_SIZE;x++){
            for(int y=0;y<TrainerOCR.THUMB_SIZE;y++){
                Color clr = img.getColor(x,y);
                // only keep the RED component as we assume the color is grayscale
                in[x+y*TrainerOCR.THUMB_SIZE] = (clr.r*2)-1.0; // from 0/1 to -1/1
            }
        }
        return in;
    }

    @Override
    public double[] getInputDataSet(int num) {
        // Get input buffer from image
        double[] in = this.getInBufferFromImage(this.inputImages[num]);
        // return input buffer
        return in;
    }

    @Override
    public double[] getOutputDataSet(int num) {
        double[] out = new double[this.getOutputSize()];
        for(int i=0;i<out.length;i++){
            out[i] = -1;
        }
        out[num] = 1;
        return out;
    }

    @Override
    public String[] getInputLabels() {
        return null;
    }

        @Override
    public String[] getOutputLabels() {
        String[] out = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                         "A","B","C","D","E","F","G","H","I","J","K","L","M",
                         "N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                         "?"
                        };
        return out;
    }


}
