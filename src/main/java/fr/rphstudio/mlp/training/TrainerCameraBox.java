package fr.rphstudio.mlp.training;

import java.util.InputMismatchException;

public class TrainerCameraBox implements ITraining {

    // camera focals
    public final double focal1   = 20;
    public final double focal2   = 30;
    // camera screen width
    public final double screenW1 = 90;
    public final double screenW2 = 150;
    // total useful area size
    public final int totalW   = 600;
    public final int totalH   = 400;
    public final int totalD   = 700;

    // camera shifts (from the area borders)
    public final double du1      = 100;
    public final double du2      = 100;
    public final double dw1      = 100;
    public final double dw2      = 300;

    // camera screen middle positions
    public double mid1;
    public double mid2;
    // camera dY distances
    public double dY1;
    public double dY2;

    // cannon shift (from area right border)
    public double dcx = 300;
    public double dcy = 150;
    public double dcz = 200;

    // Constructor : place the two cameras randomly
    public TrainerCameraBox(){
        // middle screen X points
        this.mid1 = this.du1;
        this.mid2 = this.totalW - this.du2;
        // dY distances
        this.dY1 = Math.max( (2*this.focal1*(this.dw1)/this.screenW1)-this.focal1,
                             (2*this.focal1*(this.totalW-this.du1)/this.screenW1)-this.focal1);
        this.dY2 = Math.max( (2*this.focal2*(this.dw2)/this.screenW2)-this.focal2,
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
        return 0.000001;
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
        return 4;
    }

    @Override
    public int getOutputSize() {
        return 2;
    }

    @Override
    public int getNbDataSet() {
        return (int)(this.totalW*this.totalD*this.totalH + 0.999);
    }

    public double[] getPositionUVW(int num){
        // compute u and v from num
        // num = w*(W*D) + v*W + u
        double u = num % this.totalW;
        num -= u;
        num /= this.totalW;
        double v = num%this.totalD;
        num -= v;
        num /= this.totalD;
        double w = num%this.totalH;

        double[] uvw = {u,v,w};
        return uvw;
    }

    @Override
    public double[] getInputDataSet(int num) {
        if(num < 0 || num >= this.getNbDataSet() ){
            throw new InputMismatchException("The number of the input data set is not correct : "+num);
        }
        // Get UV from num
        double[] uvw = this.getPositionUVW(num);
        double u = uvw[0];
        double v = uvw[1];
        double w = uvw[2];
        return this.getInputDataSet(u,v,w);
    }

    public double[] getInputDataSet(double u, double v, double w){
        // compute x1
        double dx1 = u-this.mid1;
        double x1 = (this.focal1*dx1)/(this.focal1+this.dY1+v);
        x1 /= this.screenW1/2;
        // compute y1
        double dy1 = w-this.dw1;
        double y1 = (this.focal1*dy1)/(this.focal1+this.dY1+v);
        y1 /= this.screenW1/2;
        // compute x2
        double dx2 = u-this.mid2;
        double x2 = (this.focal2*dx2)/(this.focal2+this.dY2+v);
        x2 /= this.screenW2/2;
        // compute y2
        double dy2 = v-this.dw2;
        double y2 = (this.focal2*dy2)/(this.focal2+this.dY2+v);
        y2 /= this.screenW2/2;
        double[] in = {x1,y1,x2,y2};
        return in;
    }

    @Override
    public double[] getOutputDataSet(int num) {
        if(num < 0 || num >= this.getNbDataSet() ){
            throw new InputMismatchException("The number of the output data set is not correct : "+num);
        }
        // Get UV from num
        double[] uvw = this.getPositionUVW(num);
        double u = uvw[0];
        double v = uvw[1];
        double w = uvw[2];
        return this.getOutputDataSet(u,v,w);
    }

    @Override
    public String[] getInputLabels() {
        String[] out = { "X1", "Y1", "X2", "Y2" };
        return out;
    }

    @Override
    public String[] getOutputLabels() {
        String[] out = { "alpha", "beta" };
        return out;
    }

    public double[] getOutputDataSet(double u, double v, double w){
        // Compute output
        double deltaX = u-(this.totalW-this.dcx);
        double deltaY = v+this.dcy;
        double deltaZ = w-this.dcz;
        double alpha = Math.atan2(deltaY,deltaX);
        double beta  = Math.atan2(deltaY,deltaZ);
        double[] out = {alpha/Math.PI, beta/Math.PI};
        return out;
    }

}
