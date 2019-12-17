/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.rphstudio.launcher;

import fr.rphstudio.mlp.MLP;
import fr.rphstudio.mlp.activation.ActivationFunction;
import fr.rphstudio.mlp.activation.TanH;
import fr.rphstudio.mlp.cost.CostFunction;
import fr.rphstudio.mlp.cost.Quadratic;
import fr.rphstudio.mlp.except.TrainingFailureException;
import fr.rphstudio.mlp.training.ITraining;
import fr.rphstudio.mlp.training.ITraining.*;
import fr.rphstudio.mlp.training.TrainerCamera2D;
import fr.rphstudio.mlp.training.TrainerLCD7;
import fr.rphstudio.mlp.utils.Training;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class State02Test extends BasicGameState {
    //------------------------------------------------
    // PUBLIC CONSTANTS
    //------------------------------------------------
    public static final int ID = 200;


    //------------------------------------------------
    // PRIVATE CONSTANTS
    //------------------------------------------------


    //------------------------------------------------
    // PRIVATE PROPERTIES
    //------------------------------------------------
    private StateBasedGame gameObject;
    private GameContainer container;
    private String version;

    private ITraining trainer;
    private MLP mlp;

    private long TIME_STEP = 0;
    private int dataSet = 0;
    private TrainResult result = TrainResult.MAX_ITERATION;

    private float targetH = 0;
    private boolean movingUp = false;
    private boolean movingDown = false;

    //------------------------------------------------
    // PRIVATE METHODS
    //------------------------------------------------
    // Get current program version string from file
    private void getVersion() {
        // Get display version
        BufferedReader br = null;
        try {
            this.version = "";
            br = new BufferedReader(new FileReader("info/version.txt"));
            String line;
            line = br.readLine();
            while (line != null) {
                this.version = this.version + line + "\n";
                line = br.readLine();
            }
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            throw new Error(e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                throw new Error(ex);
            }
        }
    }

    // Quit game
    private void quitGame() {
        this.container.exit();
    }


    //------------------------------------------------
    // CONSTRUCTOR
    //------------------------------------------------
    public State02Test() {
    }


    //------------------------------------------------
    // INIT METHOD
    //------------------------------------------------
    public void init(GameContainer container, StateBasedGame sbGame) throws SlickException {
        // Init fields
        this.container = container;
        this.gameObject = sbGame;

        // Get version string
        this.getVersion();

        // prepare H position
        this.targetH = 0;
        this.movingDown = false;
        this.movingUp = false;

        // Create trainer
        this.trainer = new TrainerCamera2D();

        // Create MLP data
        int[] sizes = {2, 16, 16, 1};
        ActivationFunction af = new TanH();
        CostFunction cf = new Quadratic();
        ActivationFunction[] afs = {af, af, af};

        // instanciate MLP
        this.mlp = new MLP(sizes, afs, cf);

        // Scramble weights and bias
        this.mlp.scramble();
    }


    //------------------------------------------------
    // RENDER METHOD
    //------------------------------------------------
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        // Fit Screen
        MainLauncher.fitScreen(container, g);

        // Get trainer
        TrainerCamera2D tr = ((TrainerCamera2D) this.trainer);

        // get values
        float W = (float) tr.totalW;
        float D = (float) tr.totalD;
        float H = (float) tr.totalH;
        float MID1 = (float) tr.mid1;
        float MID2 = (float) tr.mid2;
        float V1 = (float) tr.dv1;
        float V2 = (float) tr.dv2;
        float SCR1 = (float) tr.screenW1 / 2;
        float SCR2 = (float) tr.screenW2 / 2;
        float DY1 = (float) tr.dY1;
        float DY2 = (float) tr.dY2;
        float FOC1 = (float) tr.focal1;
        float FOC2 = (float) tr.focal2;
        float DCX = (float) tr.dcx;
        float DCY = (float) tr.dcy;

        // Set reference
        float refX = 25;
        float refX2 = refX + W + 50;
        float refY = 50;


        //----------------------------------------
        // TOP VIEW (W/D)
        //----------------------------------------
        // draw area
        g.setColor(Color.green);
        g.drawRect(refX, refY, W, D);
        // place cameras
        g.setColor(Color.red);
        g.drawLine(refX + MID1 - SCR1, refY + D + DY1, refX + MID1 + SCR1, refY + D + DY1);
        g.drawLine(refX + MID2 - SCR2, refY + D + DY2, refX + MID2 + SCR2, refY + D + DY2);
        // place focals
        g.setColor(Color.cyan);
        g.fillOval(refX + MID1 - 3, refY + D + DY1 + FOC1 - 3, 6, 6);
        g.fillOval(refX + MID2 - 3, refY + D + DY2 + FOC2 - 3, 6, 6);
        // trace focal limits
        g.drawLine(refX + MID1 - SCR1, refY + D + DY1, refX + MID1, refY + D + DY1 + FOC1);
        g.drawLine(refX + MID1 + SCR1, refY + D + DY1, refX + MID1, refY + D + DY1 + FOC1);
        g.drawLine(refX + MID2 - SCR2, refY + D + DY2, refX + MID2, refY + D + DY2 + FOC2);
        g.drawLine(refX + MID2 + SCR2, refY + D + DY2, refX + MID2, refY + D + DY2 + FOC2);

        //----------------------------------------
        // SIDE VIEW
        //----------------------------------------
        // draw area (H/D)
        g.setColor(Color.green);
        g.drawRect(refX2, refY, H, D);
        // place cameras
        g.setColor(Color.red);
        g.drawLine(refX2 + V1 - SCR1, refY + D + DY1, refX2 + V1 + SCR1, refY + D + DY1);
        g.drawLine(refX2 + V2 - SCR2, refY + D + DY2, refX2 + V2 + SCR2, refY + D + DY2);
        // place focals
        g.setColor(Color.cyan);
        g.fillOval(refX2 + V1 - 3, refY + D + DY1 + FOC1 - 3, 6, 6);
        g.fillOval(refX2 + V2 - 3, refY + D + DY2 + FOC2 - 3, 6, 6);
        // trace focal limits
        g.drawLine(refX2 + V1 - SCR1, refY + D + DY1, refX2 + V1, refY + D + DY1 + FOC1);
        g.drawLine(refX2 + V1 + SCR1, refY + D + DY1, refX2 + V1, refY + D + DY1 + FOC1);
        g.drawLine(refX2 + V2 - SCR2, refY + D + DY2, refX2 + V2, refY + D + DY2 + FOC2);
        g.drawLine(refX2 + V2 + SCR2, refY + D + DY2, refX2 + V2, refY + D + DY2 + FOC2);


        // get random data set
        if (this.TIME_STEP >= 500) {
            this.TIME_STEP -= 500;
            this.dataSet = (int) (Math.random() * (2 * (W + D) - 4));
        }

        // prepare x and y position
        float x = 0;
        float y = 0;
        this.targetH = Math.min(Math.max(0, this.targetH), H - 1);

        // Get mouse position and trace lines to point from cameras
        x = this.container.getInput().getMouseX() - refX;
        y = D - (this.container.getInput().getMouseY() - refY);
        x = Math.min(Math.max(0, x), W - 1);
        y = Math.min(Math.max(0, y), D - 1);

        // Compute dataset according to mouse position
        this.dataSet = (int) ((y * W) + x);

        // Get Screen position (IN) and cannon angle (OUT) according to number of dataset
        double[] in = tr.getInputDataSet(dataSet);
        double[] outTheoric = tr.getOutputDataSet(dataSet);
        double[] outReal = tr.getOutputDataSet(dataSet);

        // update real out using the trained mlp
        this.mlp.setInputs(in);
        this.mlp.processForward();
        outReal[0] = this.mlp.getOutput(3, 0);

        // Compute angle according to output
        float angleTheoric = (float) (outTheoric[0] * Math.PI); // -pi to +pi
        float angleReal = (float) (outReal[0] * Math.PI); // -pi to +pi

        // get position according to dataSet
        x = (float) (tr.getPositionUV(dataSet)[0]) + refX;
        y = D - (float) (tr.getPositionUV(dataSet)[1]) + refY;
        float z = this.targetH + refX2;

        // Compute error message
        int err = (int) (100 * 1000 * Math.abs(angleReal - angleTheoric) / Math.PI);
        double error = ((double) err) / 1000;


        //----------------------------------------
        // TOP VIEW (W/D)
        //----------------------------------------
        // Display target lines
        g.setColor(Color.darkGray);
        g.drawLine(refX + MID1, refY + D + DY1 + FOC1, x, y);
        g.drawLine(refX + MID2, refY + D + DY2 + FOC2, x, y);
        // Display target
        g.setColor(Color.yellow);
        g.fillOval(x - 3, y - 3, 6, 6);
        // display error text
        g.setColor(Color.yellow);
        g.drawString(Double.toString(error) + "%", x - 10, y - 25);

        // draw required input from cameras
        g.setColor(Color.yellow);
        g.drawOval(refX + MID1 + (float) in[0] * SCR1 - 3, refY + D + DY1 - 3, 6, 6);
        g.drawOval(refX + MID2 + (float) in[1] * SCR2 - 3, refY + D + DY2 - 3, 6, 6);

        // Draw cannon direction
        g.setColor(Color.cyan);
        g.fillOval(refX + W - DCX - 3, refY + D + DCY - 3, 6, 6);
        // Draw cannon fireline
        g.setColor(Color.blue);
        g.drawLine(refX + W - DCX, refY + D + DCY, refX + W - DCX + 2000 * (float) Math.cos(angleTheoric), refY + D + DCY - 2000 * (float) Math.sin(angleTheoric));
        g.setColor(Color.cyan);
        g.drawLine(refX + W - DCX, refY + D + DCY, refX + W - DCX + 2000 * (float) Math.cos(angleReal), refY + D + DCY - 2000 * (float) Math.sin(angleReal));

        //----------------------------------------
        // SIDE VIEW (W/D)
        //----------------------------------------
        // Display target lines
        g.setColor(Color.darkGray);
        g.drawLine(refX2 + V1, refY + D + DY1 + FOC1, z, y);
        g.drawLine(refX2 + V2, refY + D + DY2 + FOC2, z, y);
        // Display target
        g.setColor(Color.yellow);
        g.fillOval(z - 3, y - 3, 6, 6);


    }


    //------------------------------------------------
    // UPDATE METHOD
    //------------------------------------------------
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

        // move targetH
        if(this.movingDown){
            this.targetH -= 2.5f;
        }
        if(this.movingUp){
            this.targetH += 2.5f;
        }

        // Increase time step
        this.TIME_STEP += delta;

        // train MLP (only if it has to)
        if (this.result == TrainResult.MAX_ITERATION) {
            this.result = Training.trainMLP(this.mlp, this.trainer, false, 10000);
            // display if finished correctly
            if (this.result == TrainResult.LEVEL_OK) {
                System.out.println(this.mlp);
            }
        }

    }


    //------------------------------------------------
    // KEYBOARD METHODS
    //------------------------------------------------
    @Override
    public void keyPressed(int key, char c) {
        switch (key) {
            // toggle FPS
            case Input.KEY_F:
                this.container.setShowFPS(!this.container.isShowingFPS());
                break;
            // Quit game by pressing escape
            case Input.KEY_ESCAPE:
                this.quitGame();
                break;
            // Toggle full screen mode ON/OFF
            case Input.KEY_F11:
                try {
                    this.container.setFullscreen(!this.container.isFullscreen());
                } catch (SlickException se) {
                }
                break;
            case Input.KEY_ADD:
                this.movingUp = true;
                break;
            case Input.KEY_SUBTRACT:
                this.movingDown = true;
                break;
            // all other keys have no effect
            default:
                break;
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        switch (key) {
            case Input.KEY_ADD:
                this.movingUp = false;
                break;
            case Input.KEY_SUBTRACT:
                this.movingDown = false;
                break;
            // all other keys have no effect
            default:
                break;
        }
    }



    //------------------------------------------------
    // STATE ID METHOD
    //------------------------------------------------
    @Override
    public int getID()
    {
          return this.ID;
    }



    //------------------------------------------------
    // END OF STATE
    //------------------------------------------------
}