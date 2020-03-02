/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.rphstudio.launcher;

import fr.rphstudio.mlp.MLP;
import fr.rphstudio.mlp.activation.ActivationFunction;
import fr.rphstudio.mlp.activation.SoftMax;
import fr.rphstudio.mlp.activation.TanH;
import fr.rphstudio.mlp.cost.CostFunction;
import fr.rphstudio.mlp.cost.Quadratic;
import fr.rphstudio.mlp.training.ITraining;
import fr.rphstudio.mlp.training.ITraining.TrainResult;
import fr.rphstudio.mlp.training.TrainerOCR;
import fr.rphstudio.mlp.utils.SaveRestore;
import fr.rphstudio.mlp.utils.SlickDisplayMLP;
import fr.rphstudio.mlp.utils.Training;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainTestOCR extends BasicGameState {
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
    private Image charImg;
    private String saveFileName;


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
    public MainTestOCR() {
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

        // Set filename for save/restore
        this.saveFileName = "OCR2_softmax_0_1.mlp";

        // Create trainer
        this.trainer = new TrainerOCR();

        // Create MLP data
        int[] sizes = { this.trainer.getInputSize(), 48, this.trainer.getOutputSize() };
        ActivationFunction af  = new TanH();
        ActivationFunction af2 = new SoftMax();

        CostFunction cf = new Quadratic();
        ActivationFunction[] afs = {af, af2};

        // instanciate MLP
        this.mlp = new MLP(sizes, afs, cf);

        // Scramble weights and bias
        this.mlp.scramble();

        // Restore if possible
        MLP loadedMLP = SaveRestore.restore(this.saveFileName);
        if(loadedMLP != null){
            this.mlp = loadedMLP;
            this.result = TrainResult.LEVEL_OK;
        }

        // create image
        this.charImg = new Image(TrainerOCR.THUMB_SIZE,TrainerOCR.THUMB_SIZE);

        // Set size of MLP display
        SlickDisplayMLP.setSize(11);
    }


    //------------------------------------------------
    // RENDER METHOD
    //------------------------------------------------
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        // Fit Screen
        MainLauncher.fitScreen(container, g);

        // Get trainer
        TrainerOCR tr = (TrainerOCR)this.trainer;

        // Set reference
        float refX = 1650;
        float refY = 290;

        // prepare x and y position
        float x = refX-(4-this.mlp.getNbLayers())*175;
        float y = refY + (this.dataSet * 16);

        // Get Screen position (IN) and cannon angle (OUT) according to number of dataset
        double[] in  = tr.getInputDataSet(dataSet);
        double[] out = tr.getOutputDataSet(dataSet);

        // update real out using the trained mlp
        this.mlp.setInputs(in);
        this.mlp.processForward();

        // render image according to input dataset
        ((TrainerOCR) this.trainer).drawInput(
                ((TrainerOCR) this.trainer).getCharFromDataSet(this.dataSet),
                 this.charImg
                );
        if(this.result != TrainResult.LEVEL_OK){
            g.drawImage(this.charImg, x+100,y-250,Color.red);
            g.drawImage(this.charImg.getScaledCopy(30), 400, 300,Color.red);
        }
        else{
            g.drawImage(this.charImg, x+100,y-250,Color.white);
            g.drawImage(this.charImg.getScaledCopy(30), 400, 300,Color.white);
        }

        // Render the MLP structure
        SlickDisplayMLP.displayMLP(this.mlp, g, this.trainer, 1150, 540);
    }


    //------------------------------------------------
    // UPDATE METHOD
    //------------------------------------------------
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

        // train MLP (only if it has to)
        if (this.result == TrainResult.MAX_ITERATION) {
            this.result = Training.trainMLP(this.mlp, this.trainer, false, 8000, true);
            // save and display if finished correctly
            if(this.result == TrainResult.LEVEL_OK){
                // Save LCD
                SaveRestore.save(this.mlp,"./"+this.saveFileName);
                // display MLP
                System.out.println(this.mlp);
            }
        }

        // Increase time step and update data set for rendering
        this.TIME_STEP += delta;
        int value = 100;
        if(this.result == TrainResult.LEVEL_OK) {
            value = 750;
        }
        if(this.TIME_STEP >= value){
            this.TIME_STEP -= value;
            this.dataSet = (this.dataSet+1)%this.trainer.getNbDataSet();
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
            // all other keys have no effect
            default:
                break;
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        switch (key) {
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