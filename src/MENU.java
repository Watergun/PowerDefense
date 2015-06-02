import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.security.MessageDigest;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;



public class MENU extends JFrame 
{	
	//Referanzattribut auf GAME
	private static GAME game;
	//Array aller Buttons in MENU
	private JButton[] buttons;
	//Referenz auf sich selbst 
	public JFrame frame = this;
	//Textfeld 
	private JTextField userfield;
	private JTextField userfield1;
	private JPasswordField passwordfield;
	private JPasswordField passwordfield1;
	private JPasswordField passwordfield2;
	private JPasswordField newpasswordfield;
	private JPasswordField newpasswordfield2;
	private JTextField mailfield;
	private JLabel username;
	private JLabel username1;
	private JLabel password;
	private JLabel password1;
	private JLabel password2;
	private JLabel maillabel;
	private JLabel passwordreset;
	//
	public static JDialog registrationdialog;
	MENU()
	{
		//Das JFrame erstellen
		super("Power Defense");
		
		//Das Spiel  vorbereiten
		game = new GAME();
		
		//Das Hauptmenue
		Container c = new Container();
		setResizable(true);
		setVisible(true);
		userfield = new JTextField();
		userfield.setSize(100,50);
		userfield.setBackground(Color.LIGHT_GRAY);
		userfield1 = new JTextField();
		userfield1.setSize(100,50);
		userfield1.setBackground(Color.LIGHT_GRAY);
		passwordfield = new JPasswordField();
		passwordfield.setSize(100,50);
		passwordfield.setBackground(Color.LIGHT_GRAY);
		passwordfield1 = new JPasswordField();
		passwordfield1.setSize(100,50);
		passwordfield1.setBackground(Color.LIGHT_GRAY);
		passwordfield2 = new JPasswordField();
		passwordfield2.setSize(100,50);
		passwordfield2.setBackground(Color.LIGHT_GRAY);
		mailfield = new JTextField();
		mailfield.setSize(100,50);
		mailfield.setBackground(Color.LIGHT_GRAY);
		username = new JLabel("Username:");
		password = new JLabel("Passwort:");
		username1 = new JLabel("Username:");
		password1 = new JLabel("Passwort:");
		password2 = new JLabel("Passwort wiederholen:");
		maillabel = new JLabel("Email: (optional) ");
		passwordreset = new JLabel("(Email erforderlich für Passwortreset)");
		//Die Startbuttons und zusaetzliche Dialogbuttons
		buttons = new JButton[8];
		for(int i = 0; i <8; i++)
		{
			buttons[i] = new JButton();
		}
		buttons[0].setText("Anmelden");
		buttons[1].setText("Registrieren");
		buttons[2].setText("Spielanleitung");
		buttons[3].setText("Highscore");
		buttons[4].setText("Registrierung abschliessen");
		buttons[5].setText("Passwort speichern");
		buttons[6].setText("Passwort-Reset Anfrage");
		buttons[7].setText("Offlinemodus");
		buttons[0].setSize(100, 50);
		buttons[1].setSize(100, 50);
		buttons[2].setSize(100, 50);
		buttons[3].setSize(100, 50);
		buttons[4].setSize(100, 50);
		buttons[5].setSize(100, 50);
		buttons[6].setSize(100, 50);
		buttons[7].setSize(100, 50);
		setSize(500,490);
		JPanel panel = new JPanel()
		{
			public void paintComponent(Graphics g)
			{
				BufferedImage image = null;
				try
				{
					image = ImageIO.read(new File("images/td.jpg"));
					g.drawImage(image,0,0,null);
				}
				catch(IOException e)
				{
					System.out.println(e);
				}
				setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
				//setSize(new Dimension(image.getWidth(),image.getHeight()));
			}
		};
		
		c = new Container();
		buttons[0].addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent arg0)
			{	
				String password = new String(passwordfield.getPassword());
				
				System.out.println("\""+password+"\"");
				//Kein Passwort eingetragen -> User hat vllt sein Passwort zurueckgesetzt
				if(password.equals(""))
				{
					
					if(GAME.sql.CheckPasswordReset(userfield.getText()))
					{
						JDialog newpassworddialog = new JDialog(frame, "Neues Passwort",true);
						newpassworddialog.setSize(200, 200);
						newpassworddialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
						JPanel c = new JPanel();
						c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
						//Erstellen
						JLabel newpasswordlabel = new JLabel("Neues Passwort:");
						newpasswordfield = new JPasswordField();
						newpasswordfield.setSize(100,50);
						newpasswordfield.setBackground(Color.LIGHT_GRAY);
						JLabel newpasswordlabel2 = new JLabel("Passwort wiederholen:");
						newpasswordfield2 = new JPasswordField();
						newpasswordfield2.setSize(100,50);
						newpasswordfield2.setBackground(Color.LIGHT_GRAY);
						//Adden
						c.add(newpasswordlabel);
						c.add(newpasswordfield);
						c.add(newpasswordlabel2);
						c.add(newpasswordfield2);
						c.add(buttons[5]);
						//
						newpassworddialog.add(c);
						newpassworddialog.setVisible(true);
					}
					else
						System.out.println("[LOGIN] USERNAME DOES NOT EXIST !");
					
				}
				//Anmelden
				if(GAME.sql.Login(userfield.getText(), password))
				{
					GAME.online = true;
					game.Start();
				}
			}
		});

		//Registrierung
		buttons[1].addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent arg0)
			{	
				registrationdialog = new JDialog(frame,"Registrierung",true);
				registrationdialog.setSize(300,300);
				registrationdialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
				JPanel c = new JPanel();
				c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
				c.add(username1);
				username1.setAlignmentX(Component.LEFT_ALIGNMENT);
				c.add(userfield1);
				c.add(password1);
				password1.setAlignmentX(Component.LEFT_ALIGNMENT);
				c.add(passwordfield1);
				c.add(password2);
				password2.setAlignmentX(Component.LEFT_ALIGNMENT);
				c.add(passwordfield2);
				maillabel.setAlignmentX(Component.LEFT_ALIGNMENT);
				c.add(maillabel);
				c.add(mailfield);
				c.add(passwordreset);
				c.add(buttons[4]);
				registrationdialog.add(c);
				registrationdialog.setVisible(true);	
			}
		});
		
		//Spielanleitung
		buttons[2].addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{	
				BufferedReader br = null;
				String text = "Spielanleitung:";
				JDialog d = new JDialog(frame, "Spielanleitung",true);
				File spielanleitung = new File("spielanleitung.txt");
				try 
				{
					br = new BufferedReader(new FileReader(spielanleitung));
					String s;
					while((s = br.readLine()) != null)
						text = text + "\n" + s;	

				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
				JTextArea textarea = new JTextArea(text);
				textarea.setEditable(false);
				d.setDefaultCloseOperation(HIDE_ON_CLOSE);
				d.add(textarea);
				d.pack();
				d.setVisible(true);
			}
		});
		
		//Highscores
		buttons[3].addActionListener(new ActionListener()
		{	
			@Override
			public void actionPerformed(ActionEvent arg0)
			{		
				JDialog d = new JDialog(frame, "Highscore",true);
				PLAYER[] playerarray = GAME.sql.GetHighscorelist();
				String[] columnNames = {"Name","Points", "Playingtime"};
				String[][] rowData = new String[playerarray.length][3];
				for(int i = 0; i < playerarray.length;i++)
				{
					rowData[i][0] = playerarray[i].ReturnName();
					rowData[i][1] = Integer.toString(playerarray[i].ReturnPoints());
					rowData[i][2] = Integer.toString(playerarray[i].ReturnPlaytime());
				}
				JTable highscore = new JTable(rowData, columnNames);
				highscore.setAutoCreateRowSorter(true);
				highscore.setDragEnabled(false);
				highscore.setEnabled(false);
				
				JScrollPane JSP = new JScrollPane(highscore);
				
				d.add(JSP);
				d.setDefaultCloseOperation(HIDE_ON_CLOSE);
				d.pack();
				d.setVisible(true);
			}
		});
		
		//Registrierung abschliessen
		buttons[4].addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				String s1 = new String(passwordfield1.getPassword());
				String s2 = new String(passwordfield2.getPassword());
				if(userfield1.getText().length() <= 0 ||
				   s1.length() < 0	||
				   s2.length() < 0)
				{
					System.out.println("[MENU] Bitte Angaben vervollständigen!");		
				}
				else
				{
					PLAYER[] spieler = GAME.sql.GetHighscorelist();
					boolean verfuegbar = true;
					for(int i = 0; i<spieler.length;i++)
					{
						if(spieler[i].ReturnName().equals(userfield1.getText()))
						{
							verfuegbar = false;
						}	
					}
					if(verfuegbar)
					{
						if(s1.equals(s2))
						{
							//
							String mail = mailfield.getText();
							if(mail.equals(""))
								mail = "not registered";
							GAME.sql.Registration(userfield1.getText(), s1, mail);
							GAME.player = new PLAYER(userfield1.getText());
							game.Start();
						}
						else
						{
							System.out.println("Die Passwörter stimmen nicht überein!");
						}
					}
					else
					{
						System.out.println("Der Benutzername ist schon vergeben!");
					}
				}
			}
		});
		
		//Neues Passwort speichern
		buttons[5].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String password = new String(newpasswordfield.getPassword());
				String password2 = new String(newpasswordfield2.getPassword());
				String username = userfield.getText();
				if(password.equals(password2))
				{
					if(!GAME.sql.UpdatePassword(username, password))
						System.out.println("[LOGIN] PASSWORD UPDATE FAILED !");
				}
				else
				{
					System.out.println("[LOGIN] PASSWORDS DON'T MATCH !");
				}
			}
		});
		
		buttons[6].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//Schritt 1: Beim Server eine Authorisation erstellen (Einfach nur eine Nummer)
				String key = HTTP.CreateNewAuthorization();
				if(key == null)
				{
					System.out.println("[LOGIN] FAILED SENDING RESET REQUEST!");
					return;
				}
				
				//Schritt 2: Nach einer Email suchen
				String mail = GAME.sql.ReturnMail(userfield.getText());
				if(mail == null)
				{
					System.out.println("[LOGIN] USERNAME NOT FOUND !");
					return;
				}
				else if(mail.equals("not registered"))
				{
					System.out.println("[LOGIN] NO REGISTERED MAIL FOUND ! SORRY...");
					return;
				}
				
				//Schritt 3: Anfrageemail versenden
				if(!MAIL.SendResetRequest(mail, userfield.getText(), key))
				{
					System.out.println("[LOGIN] FAILED TO SEND EMAIL !");
					return;
				}
				
				//Fertig !
			}
		});
		
		//Offlinemodus
		buttons[7].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				GAME.player = new PLAYER("Offline");
				game.online = false;
				game.Start();
			}
		});
		
		c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		c.add(username);
		username.setAlignmentX(Component.CENTER_ALIGNMENT);
		c.add(userfield);
		c.add(password);
		c.add(passwordfield);
		password.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		for(int i = 0; i < 4; i++)
		{
			c.add(buttons[i]);
			buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		c.add(buttons[6]);
		c.add(buttons[7]);
		buttons[6].setAlignmentX(Component.CENTER_ALIGNMENT);
		buttons[7].setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(c);
		add(panel);
		
		//Fertig stellen
		pack();
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args)
	{
		
		System.setProperty("http.proxyHost", "192.168.1.251");
		System.setProperty("socksProxyHost", "192.168.1.251");
		System.setProperty("http.proxyPort", "8080");
		System.setProperty("socksProxyPort", "8080");
		System.setProperty("java.net.socks.username","powerd");
		System.setProperty("java.net.socks.password", "powerd");
		
		Authenticator.setDefault(new Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication("powerd","powerd".toCharArray());
			}
		});
		
		@SuppressWarnings("unused")
		MENU menu = new MENU();
		
		//Die Hauptschleife
		game.MainLoop();
	}
}
