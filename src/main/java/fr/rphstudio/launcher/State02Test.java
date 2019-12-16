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
import fr.rphstudio.mlp.training.TrainerCamera2D;
import fr.rphstudio.mlp.training.TrainerLCD7;
import fr.rphstudio.mlp.utils.Training;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class State02Test extends BasicGameState
{
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
    private GameContainer  container;
    private String         version;

    private ITraining      trainer;
    private MLP            mlp;




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
    public State02Test()
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

        // Create trainer
        this.trainer = new TrainerCamera2D();

        // Create MLP data
        int[] sizes = {2,16,16,1};
        ActivationFunction af = new TanH();
        CostFunction       cf = new Quadratic();
        ActivationFunction[] afs = {af,af,af};

        // instanciate MLP
        this.mlp = new MLP(sizes, afs, cf);

        // train MLP
        try {
            Training.trainMLP(this.mlp, this.trainer, true);
        }
        catch(TrainingFailureException tfe){
            System.out.println("The MLP has not reached the requirements during training !");
        }

    }



    //------------------------------------------------
    // RENDER METHOD
    //------------------------------------------------
    private long TIME_STEP = 0;
    private int dataSet = 0;
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException
    {
        // Fit Screen
        MainLauncher.fitScreen(container, g);

        // Get trainer
        TrainerCamera2D tr = ((TrainerCamera2D)this.trainer);
        // Set reference
        float refX = 500;
        float refY = 100;

        // get values
        float W = (float)tr.totalW;
        float H = (float)tr.totalW;
        float MID1 = (float)tr.mid1;
        float MID2 = (float)tr.mid2;
        float SCR1 = (float)tr.screenW1/2;
        float SCR2 = (float)tr.screenW2/2;
        float DY1 = (float)tr.dY1;
        float DY2 = (float)tr.dY2;
        float FOC1 = (float)tr.focal1;
        float FOC2 = (float)tr.focal2;
        float DCX = (float)tr.dcx;
        float DCY = (float)tr.dcy;

        // draw area
        g.setColor(Color.green);
        g.drawRect(refX,refY,W,H);
        // place cameras
        g.setColor(Color.red);
        g.drawLine(refX+MID1-SCR1, refY + H + DY1, refX+MID1+SCR1, refY + H + DY1);
        g.drawLine(refX+MID2-SCR2, refY + H + DY2, refX+MID2+SCR2, refY + H + DY2);
        // place focals
        g.setColor(Color.cyan);
        g.fillOval(refX+MID1-3, refY+H+DY1+FOC1-3, 6, 6);
        g.fillOval(refX+MID2-3, refY+H+DY2+FOC2-3, 6, 6);
        // trace focal limits
        g.drawLine(refX+MID1-SCR1, refY+H+DY1, refX+MID1, refY+H+DY1+FOC1);
        g.drawLine(refX+MID1+SCR1, refY+H+DY1, refX+MID1, refY+H+DY1+FOC1);
        g.drawLine(refX+MID2-SCR2, refY+H+DY2, refX+MID2, refY+H+DY2+FOC2);
        g.drawLine(refX+MID2+SCR2, refY+H+DY2, refX+MID2, refY+H+DY2+FOC2);

        // get random data set
        if(this.TIME_STEP >= 1000){
            this.TIME_STEP -= 1000;
            this.dataSet = (int)(Math.random()*W*H);
        }

        // prepare x and y position
        float x = 0 ;
        float y = 0;

        // Get mouse position and trace lines to point from cameras
        // x = this.container.getInput().getMouseX();
        // y = this.container.getInput().getMouseY();

        // Get Screen position (IN) and cannon angle (OUT) according to number of dataset
        double[] in  = tr.getInputDataSet(dataSet);
        double[] outTheoric = tr.getOutputDataSet(dataSet);
        double[] outReal    = tr.getOutputDataSet(dataSet);

        // update real out using the trained mlp
        this.mlp.setInputs(in);
        this.mlp.processForward();
        outReal[0] = this.mlp.getOutput(3,0);

        // Compute angle according to output
        float angleTheoric = (float)(outTheoric[0]*Math.PI); // -pi to +pi
        float angleReal    = (float)(outReal[0]   *Math.PI); // -pi to +pi

        // get position according to dataSet
        x = (float)(tr.getPositionUV(dataSet)[0])+refX;
        y = H-(float)(tr.getPositionUV(dataSet)[1])+refY;

        // Display position of the target
        g.setColor(Color.darkGray);
        g.drawLine(refX+MID1, refY+H+DY1+FOC1, x, y);
        g.drawLine(refX+MID2, refY+H+DY2+FOC2, x, y);
        g.setColor(Color.yellow);
        g.fillOval(x-3,y-3,6,6);

        // draw required input from cameras
        g.setColor(Color.yellow);
        g.drawOval(refX+MID1+(float)in[0]*SCR1-3, refY+H+DY1-3, 6, 6);
        g.drawOval(refX+MID2+(float)in[1]*SCR2-3, refY+H+DY2-3, 6, 6);

        // Draw cannon direction
        g.setColor(Color.blue);
        g.fillOval(refX+W-DCX-3, refY+H+DCY-3, 6, 6);
        // Draw cannon fireline
        g.setColor(Color.cyan);
        g.drawLine( refX+W-DCX, refY+H+DCY, refX+W-DCX+1000*(float)Math.cos(angleTheoric), refY+H+DCY-1000*(float)Math.sin(angleTheoric) );
        g.setColor(Color.blue);
        g.drawLine( refX+W-DCX, refY+H+DCY, refX+W-DCX+1000*(float)Math.cos(angleReal), refY+H+DCY-1000*(float)Math.sin(angleReal) );

    }

    
    //------------------------------------------------
    // UPDATE METHOD
    //------------------------------------------------
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
    {
        // Increase time step
        this.TIME_STEP += delta;


    }
    
    
    //------------------------------------------------
    // KEYBOARD METHODS
    //------------------------------------------------
    @Override
    public void keyPressed(int key, char c)
    {
        switch(key)
        {
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
                }catch(SlickException se){}
                break;
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