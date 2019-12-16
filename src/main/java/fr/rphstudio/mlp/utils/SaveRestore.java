package fr.rphstudio.mlp.utils;

import fr.rphstudio.mlp.MLP;

import java.io.*;

public class SaveRestore {

    private static final String DEFAULT_PATH = "./test.mlp";

    public static void save(MLP mlp){
        SaveRestore.save(mlp, DEFAULT_PATH);
    }

    public static void save(MLP mlp, String path){
        try {
            // open connection to file
            FileOutputStream   fos = new FileOutputStream(new File(path));
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            // Write objects to file
            oos.writeObject(mlp);

            // close connection
            oos.close();
            fos.close();
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("File not found");
        }
        catch (IOException ioe) {
            System.out.println("Error initializing stream");
        }
    }

    public static MLP restore(){
        return SaveRestore.restore(DEFAULT_PATH);
    }

    public static MLP restore(String path){
        try {
            // open connection to file
            FileInputStream fis = new FileInputStream(new File(path));
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Read objects
            MLP mlp = (MLP) ois.readObject();
            // Close connection
            ois.close();
            fis.close();
            // display MLP object
            System.out.println(mlp);
            return mlp;
        }
        catch (FileNotFoundException fnfe) {
            System.out.println("File not found");
            return null;
        }
        catch (IOException ioe) {
            System.out.println("Error initializing stream");
            return null;
        }
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            return null;
        }
    }

}
