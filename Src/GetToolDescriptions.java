/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paintbackup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author G-sta
 */


public class GetToolDescriptions {
    
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    
    /**
     * Grabs tool descriptions from a txt file to be used in customToolBar
     */
    public GetToolDescriptions() {
        openFile();
        readFile();
        closeFile();
    }    
    
    /**
     * Returns an integer representation of the number of descriptions saved.
     * @return size.
     */
    public int getNumDescriptions(){
        return descriptions.size();
    }
    /**
     * Returns an String representation of the name of the tool at the given index.
     * @param i
     * @return 
     */
    public String getName(int i){
        return names.get(i);
    }
    /**
     * Returns a String representation of that will return the text to be put
     * into a popup for the tools.
     * @param i
     * @return 
     */
    public String getDescription(int i){
        if(descriptions.get(i).equals(" ")){
            return names.get(i);
        }else{
            return (names.get(i) + "\n" + descriptions.get(i));
        }
    }
    
    private Scanner x;
    
    /**
     * Opens the file to read tool descriptions from.
     */
    private void openFile(){
        try{
            x = new Scanner(new File("Tool_Descriptions.txt"));
        }
        catch(FileNotFoundException e){
            System.out.println("Could not find file");
            e.printStackTrace();
        }
        catch(Exception e){
            System.out.println("Could not find file");
        }
    }
    
    /**
     * Reads all of the content of the file to an array.
     */
    private void readFile(){
        while(x.hasNext()){ // while loop keeps going until it reaches the end of the file
            String line = x.nextLine();
            
            int sep = line.indexOf(":");
            String name = line.substring(0, sep);
            String desc;
            if(sep+1>=line.length()){
                desc = " ";
            }else{
                desc = line.substring(sep+1);
            }
            
            names.add(name);
            descriptions.add(desc);
        }
    }
    
    /**
     * Closes the file tool descriptions were read from.
     */
    private void closeFile(){
        x.close();
    }
}
