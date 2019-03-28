package homework2.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail 
{
   public void Send(String user, String code) 
   {
        String email = getUserEmail(user);
        final String username = getUsername(user);
        final String password = getUserPass(user);	 

	Properties props = new Properties();

        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.host", "smtp.gmail.com");

        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        props.setProperty("mail.smtp.port", "587");	 

        Session session = Session.getDefaultInstance(props,

        new javax.mail.Authenticator() 
        {
	    protected PasswordAuthentication getPasswordAuthentication() 
            {
	        return new PasswordAuthentication(username,password);
	    }
	});

	try 
        {

	    Message message = new MimeMessage(session);
	    message.setFrom(new InternetAddress("sdev425chris@gmail.com"));
	    message.setRecipients(Message.RecipientType.TO,
	    InternetAddress.parse(email));

	    message.setSubject(user + " Auth - Multifactor Code");
	    message.setText("Hi," +
	    "Your Multifactor Auth Code: " + code);

	    Transport.send(message);
	    System.out.println("Mail sent succesfully!");

	} 
        catch (MessagingException e) 
        {
	     throw new RuntimeException(e);

        }
    }

    protected String getUserEmail(String user) 
    {
       if (user.equalsIgnoreCase("abc"))
       {
          return "consciouscoder7@gmail.com";
       }
       else if(user.equalsIgnoreCase("sdev425"))
       {
         return "sdev425chris@gmail.com";
       }
       else
       {
          System.out.println("No such email available terminate program.");
          System.exit(0);
       } 
       return "";
    }

    protected String getUserPass(String user) 
    {
       if (user.equalsIgnoreCase("abc"))
       {
          return "sdev425test";
       }
       else if(user.equalsIgnoreCase("sdev425"))
       {
         return "sdev425test";
       }
       return "";
    }

    protected String getUsername(String user) 
    {
       if (user.equalsIgnoreCase("abc"))
       {
          return "sdev425chris";
       }
       else if(user.equalsIgnoreCase("sdev425"))
       {
         return "sdev425test";
       }
       return "";        
    }
}
