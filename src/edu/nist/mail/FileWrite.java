/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nist.mail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @description This program will write text to a file and save the file in the
 * project's root directory.
 * @author Eric
 */
public class FileWrite 
{
    public FileWrite()
    {
    }
    /**
     * @param args the command line arguments
     */
    private void Write(String txt)
    {
        // declaring variables of text and initializing the buffered writer
        //String txt = "Hello World.";
        BufferedWriter writer = null;
        
        // write the text variable using the bufferedwriter to testing.txt
        try 
        {
            writer = new BufferedWriter(new FileWriter("testing.txt"));
            writer.write(txt);
        }
        // print error message if there is one
        catch (IOException io) {
            System.out.println("File IO Exception" + io.getMessage());
        }
        //close the file
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            }
            //print error message if there is one
            catch (IOException io) {
                System.out.println("Issue closing the File." + io.getMessage());
            }
        }
    }
    
}