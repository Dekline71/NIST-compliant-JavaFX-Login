/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homework2.mail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// NEW NEW NEW ... these libraries are required for our audit date stamp
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

import homework2.mail.SendEmail;


/**
 *
 * @author jim Adopted from Oracle's Login Tutorial Application
 * https://docs.oracle.com/javafx/2/get_started/form.htm
 */

public class JavaMail extends Application 
{
   
// NEW NEW NEW ... setting up the variables so that we can call them for auditing purposes
    DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
// NEW NEW NEW ... setting a global integer for password attempts
    int attempts = 0;
    static int MAX_ATTEMPTS = 5;
   // private Attempts attempts = new Attempts();
    
// NEW NEW NEW ... this is needed for our audit logging    
    final String filename = "auditlog.txt";    
    FileWrite filewrite = new FileWrite();

      
    @Override
    public void start(Stage primaryStage) 
    {
        LogSystemStartUp();
            
        // Create Okay Button
        Button btn = new Button("Ok, I Acknowledge.");
        SystemUseNotification(primaryStage, btn);
       
        // Set the Action when button is clicked
        btn.setOnAction(new EventHandler<ActionEvent>() 
        {
           
            @Override
            public void handle(ActionEvent e) 
            {
                primaryStage.setTitle("SDEV425 Login");
                        
                // Grid Pane divides your window into grids
                GridPane grid = new GridPane();
                // Align to Center
                // Note Position is geometric object for alignment
                grid.setAlignment(Pos.CENTER);
                // Set gap between the components
                // Larger numbers mean bigger spaces
                grid.setHgap(10);
                grid.setVgap(10);

                // Create some text to place in the scene
                Text scenetitle = new Text("Welcome. Login to continue.");
                // Add text to grid 0,0 span 2 columns, 1 row
                grid.add(scenetitle, 0, 0, 2, 1);

                // Create Label
                Label userName = new Label("User Name:");
                // Add label to grid 0,1
                grid.add(userName, 0, 1);

                // Create Textfield
                TextField userTextField = new TextField();
                // Add textfield to grid 1,1
                 // NEW NEW NEW seeting a textbox name to help direct the users to enter their username      
                userTextField.setText("User Name");
                grid.add(userTextField, 1, 1);

                // we neede to use PW for printing our audit file, we need to rename this label
                // Create Label
                Label pwd = new Label("Password:");
                // Add label to grid 0,2
                grid.add(pwd, 0, 2);

                // Create Label
                int a = MAX_ATTEMPTS - attempts;
                Label attmp = new Label("Attempts Left: " + a);

                // Add label to grid 0,2
                //attmp.setText(attempts);
                grid.add(attmp, 0, 3);
              
                 // Create Passwordfield
                PasswordField pwBox = new PasswordField();
                // Add Password field to grid 1,2
                 // NEW NEW NEW seeting a password box **** to help direct the users to enter their username
                pwBox.setText("Password");
                grid.add(pwBox, 1, 2);

                // Create Login Button
                Button btn = new Button("Login");
                // Add button to grid 1,4
                grid.add(btn, 1, 4);

                final Text actiontarget = new Text();
                grid.add(actiontarget, 1, 6);

                // Set the Action when button is clicked
                btn.setOnAction(new EventHandler<ActionEvent>() 
                {
           
                    @Override
                    public void handle(ActionEvent e) 
                    {
                        /*the next 3 lines is needed to write the auditlog.txt file with 
                        file input and output we need a TRY and CATCH for errors*/
                        try 
                        {
                            // PrintWriter writer = new PrintWriter("auditlog.txt", "UTF-8");

                            //FileWriter fw = new FileWriter("auditlog.txt");
                            //PrintWriter pw = new PrintWriter(fw);
                            // new boolean expression to help with our login attempt management                
                            boolean isLoggedIn = false;
                            boolean isPasswordValid = authenticatePass(userTextField.getText(), pwBox.getText());
                            // If valid clear the grid and Welcome the user

                            //modified the IF statement to account for login attempts, in this case we are allowing 5 attempts           
                            if (attempts < 5 && isPasswordValid) 
                            {      
                                String code = generateRandomIntegers();
                             
                                SendEmail email = new SendEmail();
                                email.Send(userTextField.getText(), code);
                               grid.setVisible(false);

                               // Multifact auth user
                               GridPane auth = new GridPane();
                               
                               // Align to Center
                               // Note Position is geometric object for alignment
                               auth.setAlignment(Pos.CENTER);
                                // Set gap between the components
                               // Larger numbers mean bigger spaces
                               auth.setHgap(10);
                               auth.setVgap(10);
                               Text scenetitle = new Text("Authenitcate Code from " + userTextField.getText() + " email" );
                                // Create Textfield
                                TextField multifactorTextField = new TextField();
                                // Add textfield to grid 1,1
                                 // NEW NEW NEW seeting a textbox name to help direct the users to enter their username      
                                multifactorTextField.setText("Multifactor Code:");
                                auth.add(multifactorTextField, 1, 1);
                               // Add text to grid 0,0 span 2 columns, 1 row
                               auth.add(scenetitle, 0, 0, 2, 1);
                               
                                // Create Login Button
                                Button btn = new Button("Enter");
                                // Add button to grid 1,4
                                auth.add(btn, 1, 4);

                                final Text actiontarget = new Text();
                                auth.add(actiontarget, 1, 6);
                                
                                // Set the Action when button is clicked
                                btn.setOnAction(new EventHandler<ActionEvent>() 
                                {

                                    @Override
                                    public void handle(ActionEvent e) 
                                    {
                                       

                                        boolean isEmailCodeValid = authEmailCode(code, multifactorTextField.getText());
                                        if(isEmailCodeValid)
                                        {

                                            GridPane grid2 = new GridPane();
                                            auth.setVisible(false);


                                            // Align to Center
                                            // Note Position is geometric object for alignment
                                            grid2.setAlignment(Pos.CENTER);
                                             // Set gap between the components
                                            // Larger numbers mean bigger spaces
                                            grid2.setHgap(10);
                                            grid2.setVgap(10);
                                            Text scenetitle = new Text("Welcome " + userTextField.getText() + "!");
                                            // Add text to grid 0,0 span 2 columns, 1 row
                                            grid2.add(scenetitle, 0, 0, 2, 1);

                                            // Create Loginout Button
                                            Button btn = new Button("Logout");
                                            // Add button to grid 1,4
                                            grid2.add(btn, 2, 4);

                                            final Text actiontarget = new Text();
                                            grid2.add(actiontarget, 2, 6);
                                            Scene scene = new Scene(grid2, 500, 400);
                                            primaryStage.setScene(scene);
                                            primaryStage.show();

                                            // Set the Action when button is clicked
                                            btn.setOnAction(new EventHandler<ActionEvent>() 
                                            {
                                                @Override
                                                public void handle(ActionEvent e) 
                                                {
                                                    try
                                                    {
                                                        String s = "Success Login Out " + dtf.format(now) + " " + userTextField.getText() + "       UTC: " + Instant.now().toString()+"\n";
                                                        Files.write(Paths.get(filename), s.getBytes(), StandardOpenOption.APPEND);

                                                        grid2.setVisible(false);
                                                        GridPane logout = new GridPane();

                                                         // Align to Center
                                                        // Note Position is geometric object for alignment
                                                        logout.setAlignment(Pos.CENTER);
                                                         // Set gap between the components
                                                        // Larger numbers mean bigger spaces
                                                        logout.setHgap(10);
                                                        logout.setVgap(10);
                                                        Text scenetitle = new Text("Logging out " + userTextField.getText() + "!");
                                                        // Add text to grid 0,0 span 2 columns, 1 row
                                                        logout.add(scenetitle, 0, 0, 2, 1);
                                                        Scene scene = new Scene(logout, 500, 400);
                                                        primaryStage.setScene(scene);
                                                        primaryStage.show();
                                                        try
                                                        {
                                                           Thread.sleep(4000);
                                                        }
                                                        catch(InterruptedException ex)
                                                        {
                                                            Thread.currentThread().interrupt();
                                                        }

                                                        // add button
                                                        Button btn = new Button("Acknowlege disconnect and terminate.");
                                                        // Add button to grid 2,4
                                                        logout.add(btn, 2, 4);

                                                        final Text actiontarget = new Text();
                                                        logout.add(actiontarget, 2, 6);
                                                        // Set the Action when button is clicked
                                                        btn.setOnAction(new EventHandler<ActionEvent>() 
                                                        {
                                                            @Override
                                                            public void handle(ActionEvent e) 
                                                            {
                                                                System.exit(0);
                                                            }
                                                        });
                                                   }
                                                   catch(IOException ex)
                                                   {
                                                       Logger.getLogger(JavaMail.class.getName()).log(Level.SEVERE, null, ex);
                                                   }
                                                }
                                            });
                                        }

                                    }
                                });
                                
                                Scene scene = new Scene(auth, 500, 400);
                                primaryStage.setScene(scene);
                                primaryStage.show();
                                isLoggedIn = true;

                                // uncomment the line below if you would lie to print the number of attempts to your console, this will not display for the users                   
                                System.out.println("Accessed on " + dtf.format(now) +" " + userTextField.getText() + " UTC: " + Instant.now().toString());
                                String s = "Success Login " + dtf.format(now) +" " + userTextField.getText() + "\t\tUTC: " + Instant.now().toString()+"\n";
                                Files.write(Paths.get(filename), s.getBytes(), StandardOpenOption.APPEND);
                               
                            } 
                            else if (attempts != 4) 
                            { 
                            //enable the next line if you want to print to the console for testing                    
                               //System.out.println("Invalid Password " +attempts+ " attempts left");

                                Label notify = new Label("Please try again.");
                                // Add label to grid 0,2
                                grid.add(notify, 1, 6);
                                // uncomment the line below if you would lie to print the number of attempts to your console, this will not display for the users 
                                //System.out.println("Accessed on " + dtf.format(now) +" " + userTextField.getText());
                                System.out.println("Failed Login " + dtf.format(now) +" " + userTextField.getText());
                                String s = "Failed Login " + dtf.format(now) +" " + userTextField.getText() + "  UTC: " + Instant.now().toString()+"\n";
                               Files.write(Paths.get(filename), s.getBytes(), StandardOpenOption.APPEND);
                                //we need to incement the number of attempts so that we can exit the program when too many attempts have been reached                    
                                attempts++;
                                int a = MAX_ATTEMPTS - attempts;

                                attmp.setText("Attempts Left: " + a);
                            //System.out.println(remAttempts.get());

        // NEW NEW NEW, the next 4 lines are needed to exit the scene if the user failes too many login tries and the return is needed for the loop       

                            }
                            else
                            {
                                //pw.close();
                                Label l = new Label("You have exhuasted your attempts. Please wait 14 seconds.");
                                grid.add(l, 1,8);
                                    // enable the next line if you want to print to the console for testing                    
                                 //System.out.println("Invalid Password " +attempts+ " attempts left");

                                  // uncomment the line below if you would lie to print the number of attempts to your console, this will not display for the users 
                                  //System.out.println("Accessed on " + dtf.format(now) +" " + userTextField.getText());
                                  System.out.println("Exhausted login attempt " + dtf.format(now) + " " + userTextField.getText());
                                  String s = "Exhausted login attempt at " + dtf.format(now) + " by " + userTextField.getText() + " UTC: " + Instant.now().toString() +"\n";
                                  Files.write(Paths.get(filename), s.getBytes(), StandardOpenOption.APPEND);
                                  //we need to incement the number of attempts so that we can exit the program when too many attempts have been reached                    
                                  int a = MAX_ATTEMPTS - attempts;
                                  attmp.setText("Attempts Left: " + a);
                                  try
                                  {
                                      Thread.sleep(14000);
                                      attempts = 0;
                                  }
                                  catch(InterruptedException ex)
                                  {
                                    Thread.currentThread().interrupt();
                                  }
                          
                            }
                                return;        
                        }
                        /* the next 2 lines is needed to write the auditlog.txt file with 
                        file input and output we need a TRY and CATCH for errors*/
                        catch (IOException ex) 
                        {
                            Logger.getLogger(JavaMail.class.getName()).log(Level.SEVERE, null, ex);
                        }   
                    }       
                });
                // Set the size of Scene
                   Scene scene = new Scene(grid, 500, 400);
                   primaryStage.setScene(scene);
                   primaryStage.show();
        
            }
        
        });
        
        
            
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException
    {
        
        launch(args);
    }

    /**
     * @param user the username entered
     * @param pword the password entered
     * @return isValid true for authenticated
     */
    public boolean authenticatePass(String user, String pword) 
    {
        boolean isValid = false;
        if (user.equalsIgnoreCase("sdevadmin")
                && pword.equals("425!pass")) 
        {
            isValid = true;
        }
        else if (user.equalsIgnoreCase("abc")
                && pword.equals("abc123")) 
        {
            isValid = true;
        }

        return isValid;
    }
    
    
    public static String generateRandomIntegers()
    {
       double min = 0;
       double max = 9;
       double x1 = (int)(Math.random()*((max-min)+1))+min;
       double x2 = (int)(Math.random()*((max-min)+1))+min;
       double x3 = (int)(Math.random()*((max-min)+1))+min;
       double x4 = (int)(Math.random()*((max-min)+1))+min;
       
       String s = ""+x1+x2+x3+x4;

       System.out.println(s);
       return s;
    }
    
    private boolean authEmailCode(String code, String userInput) 
    {
        if(userInput.matches(code))
        {
            System.out.println("Match");
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private void SystemUseNotification(Stage primaryStage, Button btn)
    {
        primaryStage.setTitle("SYSTEM USE NOTIFICATION");
        // Grid Pane divides your window into grids
        GridPane SUnotice = new GridPane();
        // Align to Center
        // Note Position is geometric object for alignment
        SUnotice.setAlignment(Pos.CENTER);
        // Set gap between the components
        // Larger numbers mean bigger spaces
        SUnotice.setHgap(10);
        SUnotice.setVgap(10);

        // Create some text to place in the scene
        Text scenetitle = new Text("Notice:" );
        // Add text to grid 0,0 span 2 columns, 1 row
       SUnotice.add(scenetitle, 0, 0, 2, 1);

        // Create Label
        Label notice = new Label("1. Users are accessing a U.S. Government information system;\n"
                + "2. Information system usage may be monitored, recorded, and subject to aufit;\n"
                + "3. Unauthorized use of the information system is prohibited and subject to criminal and civil penalties;\n"
                + "4. Use of the information system indicates consent to monitoring and recording;" );
        // Add label to grid 0,1
        SUnotice.add(notice, 0, 1);
        
        Label unauth = new Label("Unauthorized use may be in the form of information tampering, stealing, impersonating users." );
        // Add label to grid 0,1
        SUnotice.add(unauth, 0, 2);
        
         // Add button to grid 1,4
        SUnotice.add(btn, 0, 6);

        final Text actiontarget = new Text();
        SUnotice.add(actiontarget, 1, 6);
        // Set the size of Scene
        Scene scene = new Scene(SUnotice, 700, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    private void LogSystemStartUp() 
    {
        // log program start up.
        try
        {
            String s = "System Start " + dtf.format(now) + " UTC: " + Instant.now().toString()+"\n";
            Files.write(Paths.get(filename), s.getBytes(), StandardOpenOption.APPEND);
        }
        catch(IOException ex)
        {
            Logger.getLogger(JavaMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}