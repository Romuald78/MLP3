package fr.rphstudio.mlp.training;

import java.util.InputMismatchException;

public class TrainerCamera2D implements ITraining {

    // camera focals
    public final double focal1   = 20;
    public final double focal2   = 30;
    // camera screen width
    public final double screenW1 = 90;
    public final double screenW2 = 110;
    // total useful area size
    public final int totalW   = 600;
    public final int totalH   = 400;
    public final int totalD   = 500;

    // camera shifts (from the area borders)
    public final double du1      = 200;
    public final double du2      = 200;
    public final double dv1      = 150;
    public final double dv2      = 250;

    // camera screen middle positions
    public double mid1;
    public double mid2;
    // camera dY distances
    public double dY1;
    public double dY2;

    // cannon shift (from area right border)
    public double dcx = 300;
    public double dcy = 150;

    // Constructor : place the two cameras randomly
    public TrainerCamera2D(){
        // middle screen X points
        this.mid1 = this.du1;
        this.mid2 = this.totalW - this.du2;
        // dY distances
        this.dY1 = Math.max( (2*this.focal1*(this.dv1            )/this.screenW1)-this.focal1,
                             (2*this.focal1*(this.totalW-this.du1)/this.screenW1)-this.focal1);
        this.dY2 = Math.max( (2*this.focal2*(this.dv2            )/this.screenW2)-this.focal2,
                             (2*this.focal2*(this.mid2           )/this.screenW2)-this.focal2);
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
        return 0.00001;
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
        return 2;
    }

    @Override
    public int getOutputSize() {
        return 1;
    }

    @Override
    public int getNbDataSet() {
        return (int)(this.totalW*this.totalD + 0.999);
    }

    public double[] getPositionUV(int num){
        // compute u and v from num
        double u = num % this.totalW;
        double v = (num-u)/this.totalW;
        double[] uv = {u,v};
        return uv;
    }

    @Override
    public double[] getInputDataSet(int num) {
        if(num < 0 || num >= this.getNbDataSet() ){
            throw new InputMismatchException("The number of the input data set is not correct : "+num);
        }
        // Get UV from num
        double[] uv = this.getPositionUV(num);
        double u = uv[0];
        double v = uv[1];
        return this.getInputDataSet(u,v);
    }

    public double[] getInputDataSet(double u, double v){
        // compute x1
        double dx1 = u-this.mid1;
        double x1 = (this.focal1*dx1)/(this.focal1+this.dY1+v);
        x1 /= this.screenW1/2;
        // compute x2
        double dx2 = u-this.mid2;
        double x2 = (this.focal2*dx2)/(this.focal2+this.dY2+v);
        x2 /= this.screenW2/2;
        double[] in = {x1,x2};
        return in;
    }

    @Override
    public double[] getOutputDataSet(int num) {
        if(num < 0 || num >= this.getNbDataSet() ){
            throw new InputMismatchException("The number of the output data set is not correct : "+num);
        }
        // Get UV from num
        double[] uv = this.getPositionUV(num);
        double u = uv[0];
        double v = uv[1];
        return this.getOutputDataSet(u,v);
    }

    public double[] getOutputDataSet(double u, double v){
        // Compute output
        double deltaX = u-(this.totalW-this.dcx);
        double deltaY = v+this.dcy;
        double alpha = Math.atan2(deltaY,deltaX);
        double[] out = {alpha/Math.PI};
        return out;

    }

}
