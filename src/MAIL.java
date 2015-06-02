import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//Diese Klasse kann eine Email versenden
//(bei bestehender Internetverbindung)

public class MAIL
{
	//Kein Nutzen
	MAIL()
	{}
	
	//Versendet eine Reset-Mail an die Mailaddresse 
	public static boolean SendResetRequest(String mailaddress, String username, String Key)
	{
		try
	    {
			//Einstellungen fuer den Sendeserver (GMX-Mailserver)
			Properties props = new Properties();
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.host", "mail.gmx.net");
	        props.put("mail.smtp.port", "587");

	        //Beim Server authentifizieren
	        Session session = Session.getInstance(props,
	          new javax.mail.Authenticator()
	          {
	            protected PasswordAuthentication getPasswordAuthentication() 
	            {
	            	//Der Versender und das Passwort
	                return new PasswordAuthentication("powerdefense.authenticator@gmx.de","powerdefense");
	            }
	          });
	        
	        //Die Nachricht
	        Message msg = new MimeMessage(session);
	        //Der Versender
	        msg.setFrom(new InternetAddress("powerdefense.authenticator@gmx.de","PowerDefense Authenticator"));
	        //Der Empfaenger
	        msg.addRecipient(Message.RecipientType.TO,
	                         new InternetAddress(mailaddress, username));
	        
	        //Der Titel der Mail
	        msg.setSubject("Verifizierung der Passwortentfernung");
	        //Die Nachricht
	        msg.setContent("<b>"+username+"</b>,<br>"+
	        			"Es wurde beantragt, das Passwort Ihres Accounts auf Power-Defense<br>"+
	                    "zur&uuml;ckzusetzen.<br>"+
	                    "Wenn Sie dies angefordert haben, klicken Sie bitte hier:<br>"+
	                    "<a href=\"http://"+GAME.Servername+"/cgi-bin/passwordreset.cgi?Name="+username+"&Key="+Key+"\">PasswortReset</a><br>"+
	                    "Wenn Sie die Anforderung nicht gesendet haben, k&ouml;nnen Sie diese Email<br>"+
	                    "ignorieren! An Ihrem Account wird nichts ver&auml;ndert.<br>"+
	                    "Das Power-Defense-Team",
	                    "text/html");
	       
	        //Die Email versenden
	        Transport.send(msg);
	       
	        //Das Versenden war erfolgreich
	        System.out.println("[MAIL] RESET-REQUEST SUCCESSFULLY SENT TO: "+mailaddress);
	        System.out.println("[MAIL] NOW VERIFY THE PASSWORDRESET IN THIS EMAIL !");
	        System.out.println("[MAIL] AFTER THAT, LOGIN NORMALLY WITHOUT PASSWORD");
	        
	        return true;
	    }
		catch(Exception e)
		{
			//Etwas hat nicht funktioniert!
			//Gruende: - Keine Internetanbindung
			//		   - Emailaddress existiert nicht
			//		   - Mailserver ist down (unwahrscheinlich)
			System.out.println(e.getMessage());
			System.out.println("[MAIL] SENDING FAILED !");
			return false;
		}
	}
}