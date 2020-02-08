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
import fr.rphstudio.mlp.training.ITraining;
import fr.rphstudio.mlp.training.TrainerLCD7;
import fr.rphstudio.mlp.training.TrainerXOR;
import fr.rphstudio.mlp.utils.SlickDisplayMLP;
import fr.rphstudio.mlp.utils.Training;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainTestMLP extends BasicGameState
{
    //------------------------------------------------
    // PRIVATE STRUCTURES
    //------------------------------------------------
    private class LayerStruct{
        private LayerStruct(int sz, ActivationFunction a){
            this.layerSize = sz;
            this.af = a;
        }
        private int                layerSize;
        private ActivationFunction af;
    }


    //------------------------------------------------
    // PUBLIC CONSTANTS
    //------------------------------------------------
    public static final int ID = 100;



    //------------------------------------------------
    // PRIVATE CONSTANTS
    //------------------------------------------------
    private static final int REF_X = 150;
    private static final int MID_Y = 1080/2;

    /*

    private static final int CIRCLE_WIDTH  = 18;
    private static final int CIRCLE_HEIGHT = CIRCLE_WIDTH;
    private static final int DZ  = 25;
    private static final int DAF = 25;
    private static final int NEURON_WIDTH  = DZ+DAF*2;
    private static final int NEURON_HEIGHT = NEURON_WIDTH/2;

    private static final int OUT_WIDTH  = 13;
    private static final int OUT_BOX    = 30;       // out box is the hit box used when mouse is over
    private static final int OUT_HEIGHT = OUT_WIDTH;

    private static final int LAYER_SPACE = 10;
    private static final int LAYER_WIDTH = 2*LAYER_SPACE + NEURON_WIDTH;
    private static final int LAYER_INTER = 150;
    //*/

    //------------------------------------------------
    // PRIVATE PROPERTIES
    //------------------------------------------------
    private StateBasedGame gameObject;
    private GameContainer  container;
    private String         version;

    private List<LayerStruct> layers;
    private CostFunction cf;
    private MLP               mlp;
    private ITraining         trainer;

    private long              timeWatch;
    private boolean           displayHelp;




    //------------------------------------------------
    // PRIVATE METHODS
    //------------------------------------------------
    // Get current program version string from file
    private void getVersion()
    {
        // Get display version
        BufferedReader br = null;
        try
        {
            this.version = "";
            br = new BufferedReader(new FileReader("info/version.txt"));
            String line;
            line = br.readLine();
            while(line != null)
            {
                this.version = this.version + line + "\n";
                line = br.readLine();
            }
            if (br != null)
            {
                br.close();
            }
        }
        catch (IOException e)
        {
            throw new Error(e);
        }
        finally
        {
            try
            {
                if (br != null)
                {
                    br.close();
                }
            }
            catch (IOException ex)
            {
                throw new Error(ex);
            }
        }
    }

    // Quit game
    private void quitGame()
    {
        this.container.exit();
    }









    
    //------------------------------------------------
    // CONSTRUCTOR
    //------------------------------------------------
    public MainTestMLP()
    {
    }
    
    
    //------------------------------------------------
    // INIT METHOD
    //------------------------------------------------
    public void init(GameContainer container, StateBasedGame sbGame) throws SlickException
    {
        // Init fields
        this.container  = container;
        this.gameObject = sbGame;
        
        // Get version string
        this.getVersion();

        // initializes the layer sizes
        this.layers = new ArrayList<>();
        // Init cost function
        this.cf = new Quadratic();

        /* ========== SCARECAT ==========
        // Create trainer
        this.trainer = new TrainerCameraBox();
        // Create layers (size + activation functions)
        this.layers.add( new LayerStruct(this.trainer.getInputSize() , null ) ); // no activation function : input layer
        this.layers.add( new LayerStruct(16, new TanH() ) );
        this.layers.add( new LayerStruct(16, new TanH() ) );
        this.layers.add( new LayerStruct(this.trainer.getOutputSize(), new TanH() ) );
        //*/

        //* ========== LCD 7 ==========
        // Create trainer
        this.trainer = new TrainerLCD7();
        // Create layers (size + activation functions)
        this.layers.add( new LayerStruct(this.trainer.getInputSize() , null ) ); // no activation function : input layer
        this.layers.add( new LayerStruct(3, new TanH() ) );
        this.layers.add( new LayerStruct(this.trainer.getOutputSize(), new TanH() ) );
        //*/

        /* ========== XOR ==========
        // Create trainer
        this.trainer = new TrainerXOR();
        // Create layers (size + activation functions)
        this.layers.add( new LayerStruct(this.trainer.getInputSize() , null ) ); // no activation function : input layer
        this.layers.add( new LayerStruct(2, new TanH() ) );
        this.layers.add( new LayerStruct(this.trainer.getOutputSize(), new TanH() ) );
        //*/


        // Init the application
        this.init();

        // init timewatch
        this.timeWatch = 0;
        // init help display
        this.displayHelp = false;
    }

    public void init()
    {
        // Create array of sizes and activation functions
        int[] sizes = new int[this.layers.size()];
        ActivationFunction[] afs = new ActivationFunction[this.layers.size()-1];

        // fill arrays
        for(int i=0;i<this.layers.size();i++){
            sizes[i] = this.layers.get(i).layerSize;
            if(i>0){
                afs[i-1] = this.layers.get(i).af;
            }
        }

        // instanciate MLP
        this.mlp = new MLP( sizes, afs, this.cf );

        // Scramble before train
        this.mlp.scramble();

        // train MLP
        Training.trainMLP(this.mlp, this.trainer);

        // display final MLP configuration
        System.out.println(this.mlp);

        // set display size
        SlickDisplayMLP.setSize(45);
    }

    //------------------------------------------------
    // RENDER METHOD
    //------------------------------------------------
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException
    {
        // Fit Screen
        MainLauncher.fitScreen(container, g);

        // Get MLP information and display neurons according to
        SlickDisplayMLP.displayMLP(this.mlp,g,this.trainer,REF_X,MID_Y);

        // Get mouse position
        float x = container.getInput().getMouseX();
        float y = container.getInput().getMouseY();
        // Display message according to position
        String msg = SlickDisplayMLP.getMessage(this.mlp, x,y,this.displayHelp, REF_X, MID_Y);
        if(msg != null){
            int h  = 50;
            int dy = -55;
            if(this.displayHelp){
                h = 125;
            }
            g.setColor(new Color(0,128,0,192));
            g.fillRect(x-55,y+dy,120,h);
            g.setColor(Color.white);
            g.drawString(msg,x-50,y+dy+5);
        }

        // Render version number
        g.setColor(Color.white);
        g.drawString(this.version, 1920-500, 1080-30);
    }

    
    //------------------------------------------------
    // UPDATE METHOD
    //------------------------------------------------
    private static final int TIME_STEP = 1000;
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
    {
        // measure time
        this.timeWatch += delta;

        // check when we have to perform operation
        if(this.timeWatch > TIME_STEP){
            this.timeWatch -= TIME_STEP;

            // Get data set value according to current time
            int r = (int)((System.currentTimeMillis()/TIME_STEP)%this.trainer.getNbDataSet());

            // Get input array according to data set number
            double[] input = this.trainer.getInputDataSet(r);

            // process forward
            this.mlp.setInputs(input);
            this.mlp.processForward();
        }
    }
    
    
    //------------------------------------------------
    // KEYBOARD METHODS
    //------------------------------------------------
    @Override
    public void keyPressed(int key, char c)
    {
        switch(key)
        {
            // Quit game by pressing escape
            case Input.KEY_ESCAPE:
                this.quitGame();
                break;
            // Toggle full screen mode ON/OFF
            case Input.KEY_F11:
                try {
                    this.container.setFullscreen(!this.container.isFullscreen());
                }catch(SlickException se){}
                break;
            // Display details (DEBUG)
            case Input.KEY_H:
                this.displayHelp = !this.displayHelp;
            // go to game
            // all other keys have no effect
            default :     
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