import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Vector;

import javax.imageio.ImageIO;
import java.util.Properties;

public class SQL 
{	
	private Connection conn;
	private Statement state;
	SQL()
	{
		conn = null;
		state = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("[SQL] CONNECTING TO DATABASE...");
			Properties info = new Properties();
			info.put("proxy_host", "192.168.1.251");
			info.put("proxy_port", "8080");
			info.put("proxy_user", "powerd");
			info.put("proxy_password", "powerd");
			info.put("user","root");
			info.put("password", "");
			
			conn = DriverManager.getConnection("jdbc:mysql://" + GAME.Servername + "/PowerDefense",
												info);
			System.out.println("[SQL] CONNECTION SUCCESSFUL !");
			//conn = DriverManager.getConnection("jdbc:mysql://192.168.2.200/PowerDefense?username=root&password=");
			System.out.println("[SQL] CREATING STATEMENT...");
			state = conn.createStatement();
			System.out.println("[SQL] STATEMENT SUCCESSFUL !");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
	//Diese Methoden geben die Daten direkt zurueck
	/*
	public Vector<ENEMY> GetEnemylibrary()
	{
		Vector<ENEMY> enemylib = new Vector<ENEMY>();
		ResultSet rs = null;
		try
		{
			String sql = "SELECT * FROM enemys";
			rs = state.executeQuery(sql);
			while(rs.next())
			{
				String name  = rs.getString("Name");
				int health = rs.getInt("Health");
				double velocity = rs.getDouble("Velocity");
				String image = rs.getString("Image");

				ENEMY e = new ENEMY();
				e.SetName(name);
				e.SetHealth(health);
				e.SetVelocity(velocity);
				e.SetImagesource(image);
				File img = new File("images/" + image);
				if(!img.exists())
					System.out.println("The file :" + image +" does not exist!");
				e.SetImage(ImageIO.read(img));
				enemylib.add(e);
			} 
			rs.close();
			return enemylib;
	    }
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	public Vector<TOWER> GetTowerlibrary()
	{
		Vector<TOWER> towerlib = new Vector<TOWER>();
		ResultSet rs;
		try
		{
			String sql = "SELECT * FROM towers";
			rs = state.executeQuery(sql);
			while(rs.next())
			{
				String name  = rs.getString("Name");
				int range = rs.getInt("Range");
				int firerate = rs.getInt("Firerate");
				int damage = rs.getInt("Damage");
				String image = rs.getString("Image");
				String shotimage = rs.getString("Shotimage");
				String up1 = rs.getString("Upgrade1");
				String up2 = rs.getString("Upgrade2");
				String up3 = rs.getString("Upgrade3");

				TOWER t = new TOWER();
				t.SetName(name);
				t.SetRange(range);
				t.SetFirerate(firerate);
				t.SetDamage(damage);
				t.SetImagesource(image);
				t.SetImage(ImageIO.read(new File("images/" + image)));
				t.SetShotimagesource(shotimage);
				t.SetShotimage(ImageIO.read(new File("images/" + shotimage)));
				t.SetUpgrade(0, new UPGRADE(up1));
				t.SetUpgrade(1, new UPGRADE(up2));
				t.SetUpgrade(2, new UPGRADE(up3));
				towerlib.add(t);
			} 
			rs.close();
			return towerlib;
	    }
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	public Vector<LEVEL> GetLevellist()
	{
		Vector<LEVEL> levellist = new Vector<LEVEL>();
		int row = 0;
		try
		{
			String sql = "SELECT * FROM levels";
			ResultSet rs = state.executeQuery(sql);
			while(rs.next())
			{
				//Die Position fuer spaeter merken
				row = rs.getRow();
				
				String name  = rs.getString("Name");
				int difficulty = rs.getInt("Difficulty");
				String colormap = rs.getString("Colormap");
				String background = rs.getString("Background");
				int startx = rs.getInt("StartpositionX");
				int starty = rs.getInt("StartpositionY");
				String waves = rs.getString("Waves");

				//Im Folgenden wird beim Laden ein neues ResultSet erzeugt weshalb dieses geschlossen werden muss
				rs.close();
				
				LEVEL l = new LEVEL();
				l.SetName(name);
				l.SetDifficulty(difficulty);
				l.LoadColormap("images/"+colormap);
				l.LoadBackground("images/"+background);
				l.LoadModifiedWaves(waves);
				l.SetEnemyStart(startx, starty);
				levellist.add(l);
				
				//An der alten Position wieder weitermachen
				rs = state.executeQuery(sql);
				rs.absolute(row);
			}
			//
			rs.close();
			return levellist;
	    }
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	public WAVE GetSpecificWave(String Identifier)
	{
		ResultSet rs = null;
		try
		{
			String sql = "SELECT * FROM waves";
			rs = state.executeQuery(sql);
			WAVE w = null;
			while(rs.next())
			{
				if(rs.getString("Identifier").equals(Identifier))
				{
					w = new WAVE();
					w.SetDifficulty(rs.getInt("Difficulty"));
					String full = rs.getString("Enemygroups");
					String[] enemygroups = full.split("ENEMYGROUP");
					//"", "ENEMYGROUP ...", "ENEMYGROUP ..." => 1. ueberfluessig
					for(int i = 1; i < enemygroups.length; i++)
					{
						String[] args = enemygroups[i].split(" ");
						if(args.length < 5)
							throw new Exception("ERROR: Enemygroup has not enough arguments: " + enemygroups[i]);
						ENEMY e = null;
						for(int j = 0; j < WAVE.enemylibrary.size(); j++)
						{
							if(WAVE.enemylibrary.elementAt(j).ReturnName().equals(args[1]))
								e = WAVE.enemylibrary.elementAt(j);
						}
						w.AddEnemygroup(e,
										Integer.parseInt(args[2]),
										Double.parseDouble(args[3]),
										Double.parseDouble(args[4]));
					}
				}
			}
			rs.close();
			return w;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	*/
	
	//Diese Methoden schreiben die gelesenen Daten in Textdateien
	public void UpdateData()
	{
		int index = 1;
		ResultSet rs = null;
		//Connecten und Daten in Dateien schreiben
		//Die ENEMYs:
		try
		{
			String sql = "SELECT * FROM enemys";
			rs = state.executeQuery(sql);
			while(rs.next())
			{
				String name  = rs.getString("Name");
				int element = rs.getInt("Element");
				int health = rs.getInt("Health");
				double velocity = rs.getDouble("Velocity");
				String image = rs.getString("Image");
				int reward = rs.getInt("Reward");

				File f = new File("enemys/Enemy" + index + ".txt");
				if(f.createNewFile() == false)
				{
					//Die Datei existiert bereits -> loeschen und erstellen
					f.delete();
					f.createNewFile();
				}
				//Jetzt schreiben
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write("NAME " + name + "\n");
				bw.write("ELEMENT " + element + "\n");
				bw.write("HEALTH " + health + "\n");
				bw.write("VELOCITY " + velocity + "\n");
				bw.write("REWARD " + reward + "\n");
				bw.write("IMAGE " + image);
				bw.close();
				index++;
			} 
			rs.close();
			index = 1;
			
			//Die TOWERs
			sql = "SELECT * FROM towers";
			rs = state.executeQuery(sql);
			while(rs.next())
			{
				String name  = rs.getString("Name");
				int element = rs.getInt("Element");
				int range = rs.getInt("Range");
				int firerate = rs.getInt("Firerate");
				int damage = rs.getInt("Damage");
				int cost = rs.getInt("Cost");
				String image = rs.getString("Image");
				String shotimage = rs.getString("Shotimage");
				String up1 = rs.getString("Upgrade1");
				String up2 = rs.getString("Upgrade2");
				String up3 = rs.getString("Upgrade3");

				File f = new File("towers/Tower" + index + ".txt");
				if(f.createNewFile() == false)
				{
					//Die Datei existiert bereits -> loeschen und erstellen
					f.delete();
					f.createNewFile();
				}
				//Jetzt schreiben
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write("NAME " + name + "\n");
				bw.write("ELEMENT " + element + "\n");
				bw.write("RANGE " + range + "\n");
				bw.write("FIRERATE " + firerate + "\n");
				bw.write("DAMAGE " + damage + "\n");
				bw.write("COST " + cost + "\n");
				bw.write("IMAGE " + image + "\n");
				bw.write("SHOTIMAGE " + shotimage + "\n");
				bw.write("UPGRADE 1 " + up1 + "\n"); 
				bw.write("UPGRADE 2 " + up2 + "\n"); 
				bw.write("UPGRADE 3 " + up3 + "\n"); 
				bw.close();
				index++;
			}
			rs.close();
			index = 1;
			
			//Die WAVEs
			sql = "SELECT * FROM waves";
			rs = state.executeQuery(sql);
			
			while(rs.next())
			{
				int difficulty = rs.getInt("Difficulty");
				String enemygroups = rs.getString("Enemygroups");
				
				File f = new File("waves/Wave" + index + ".txt");
				if(f.createNewFile() == false)
				{
					f.delete();
					f.createNewFile();
				}
				//Jetzt schreiben
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write("DIFFICULTY " + difficulty + "\n");
				bw.write(enemygroups);
				bw.close();
				index++;
			}
			rs.close();
			index = 1;
			
			//Die LEVELs
			sql = "SELECT * FROM levels";
			rs = state.executeQuery(sql);
			
			while(rs.next())
			{
				String name  = rs.getString("Name");
				int difficulty = rs.getInt("Difficulty");
				String colormap = rs.getString("Colormap");
				String background = rs.getString("Background");
				int startx = rs.getInt("StartpositionX");
				int starty = rs.getInt("StartpositionY");
				String waves = rs.getString("Waves");

				File f = new File("levels/Level" + index + ".txt");
				if(f.createNewFile() == false)
				{
					//Die Datei existiert bereits -> loeschen und erstellen
					f.delete();
					f.createNewFile();
				}
				//Jetzt schreiben
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write("NAME " + name + "\n");
				bw.write("DIFFICULTY " + difficulty + "\n");
				bw.write("COLORMAP " + colormap + "\n");
				bw.write("BACKGROUND " + background + "\n");
				bw.write("START " + startx + " " + starty + "\n");
				bw.write("POSTFIX .txt" + "\n");
				bw.write("WAVES " + waves);
				bw.close();
				index++;
			}
			rs.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}
	
	//Läd einen Spielstand in die Onlinehighscoreliste hoch
	public void CreateHighscoreEntry()
	{
		if(state == null)
		{
			System.out.println("[SQL] CAN'T ENTER HIGHSCORE IN ONLINE LIST");
			return;
		}
		try
		{
			String name = GAME.player.ReturnName();
			int points = GAME.player.ReturnPoints();
			int playtime = (int)(GAME.totaltime/1000);
			String sql = "INSERT INTO `PowerDefense`.`highscores` (`Name` ,`Points` , `Playingtime`) " +
					"VALUES ('" + name + "', '" + points + "', '" + playtime + "');";
			state.executeUpdate(sql);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}
	
	//Registriere einen neuen Benutzer
	public void Registration(String username, String password, String mailaddress)
	{
		if(state == null)
		{
			System.out.println("[SQL] REGISTRIERUNG FEHLGESCHLAGEN");
			return;
		}
		try
		{
			//Das Passwort wird mit MD5 gehasht und als 48-stellige hexadzimale Folge abgespeichert
			byte[] bytes = password.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(bytes);
			//Die Bytes muessen noch in das Textformat umgewandelt werden
			String digest_str = new String(digest);
			//Umwandlung in einen Hexadezimalen String
			String hex_hash = String.format("%040x", new BigInteger(1, digest_str.getBytes("UTF-8")));
			
			System.out.println(hex_hash);
			//Einspeichern
			String sql = "INSERT INTO `PowerDefense`.`highscores` "+
			"(`INDEX`,`Name`,`Points`, `Playingtime`,`Levelprogress`,`Waveprogress`,`Email`,`Password`) "+
			"VALUES ('"+GetHighscorelist().length+"', '"+username+"', '0', '0', '0', '0', '"+mailaddress+"', '"+hex_hash+"');";
			state.executeUpdate(sql);
			System.out.println("[SQL] REGISTRATION SUCCEEDED !");
			//
			MENU.registrationdialog.setVisible(false);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}
	
	//Gibt eine Mailaddress zurueck, falls es eine gibt
	public String ReturnMail(String username)
	{
		try
		{
			String sql = "SELECT * FROM highscores";
			ResultSet rs = state.executeQuery(sql);
			//Nach dem Benutzer suchen
			while(rs.next())
			{
				if(rs.getString("Name").equals(username))
				{
					return rs.getString("Email");
				}
			}
			
			//Der Benutzer wurde nicht gefunden
			return null;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	//Ueberprueft auf einen Passwortreset
	public boolean CheckPasswordReset(String username)
	{
		try
		{
			String sql = "SELECT * FROM highscores";
			ResultSet rs = state.executeQuery(sql);
			//
			boolean found = false;
			String password = "";
			while(rs.next())
			{
				if(rs.getString("Name").equals(username))
				{
					found = true;
					password = rs.getString("Password");
				}
			}
			if(!found)
				return false;
			//
			if(password.equals("RESET"))
				//Ein Passwort wurde tatsaechlich zurueckgesetzt
				return true;
			else
				return false;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	//Vergleicht die Hashs der Passwoerter des angebenen Users
	public boolean Login(String username, String password)
	{
		try
		{
			//Das lokale Passwort
			byte[] bytes = password.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(bytes);
			//Die Bytes muessen noch in das Textformat umgewandelt werden
			String digest_str = new String(digest);
			//Umwandlung in einen Hexadezimalen String
			String hex_hash = String.format("%040x", new BigInteger(1, digest_str.getBytes("UTF-8")));
		
			//Das globale Passwort
			String sql = "SELECT * FROM highscores";
			ResultSet rs = state.executeQuery(sql);
			//
			while(rs.next())
			{
				//den Benutzer suchen
				if(rs.getString("Name").equals(username))
				{
					//Benutzername gefunden
					
					//Das Passwort stimmt
					if(rs.getString("Password").equals(hex_hash))
					{
						//Login erfolgreich
						System.out.println("[SQL] LOGIN SUCCEEDED !");
						GAME.player = new PLAYER(username);
						//Kommt einer Set-Methode gleich
						GAME.player.AddPoints(rs.getInt("Points"));
						GAME.player.AddTime(rs.getInt("Playingtime"));
						GAME.levelindex = rs.getInt("Levelprogress");
						GAME.levellist.elementAt(GAME.levelindex).SetWave(rs.getInt("Waveprogress"));
						//GAME.player.SetGold(rs.getInt("Gold"));
						return true;
					}
					
					//Das Passwort war falsch
					else
					{
						System.out.println("[SQL] PASSWORD INCORRECT !");
						return false;
					}
				}
				//
			}
			//Der Benutzer wurde nicht gefunden
			System.out.println("[SQL] USERNAME NOT FOUND !\nRegistration is free !");
			return false;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	//Speichert deen Level- und Wellenfortschritt
	//Speichert den Level- und Wavestand
	public boolean SaveGamestate()
	{
		try
		{
			String sql = "SELECT * from highscores";
			ResultSet rs = state.executeQuery(sql);
			//
			int index = -1;
			while(rs.next())
			{
				if(rs.getString("Name").equals(GAME.player.ReturnName()))
				{
					index = rs.getInt("INDEX");
				}
			}
			//
			if(index == -1)
				return false;
			//Der Benutzername existiert
			sql = "UPDATE `PowerDefense`.`highscores` SET "+
					"`Levelprogress` = '" + GAME.levelindex + "', "+
					"`Waveprogress` = '" + GAME.levellist.elementAt(GAME.levelindex).ReturnWaveindex()+"' "+
					"WHERE `highscores`.`INDEX` ="+index+" ;";
			state.executeUpdate(sql);
			
			//Update fertig!
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	//Speichert ein neues passwort ein
	public boolean UpdatePassword(String username, String password)
	{
		try
		{
			String sql = "SELECT * FROM highscores";
			ResultSet rs= state.executeQuery(sql);
			//
			int index = -1;
			while(rs.next())
			{
				if(rs.getString("Name").equals(username))
				{
					index = rs.getInt("INDEX");
				}
			}
			//Benutzer nicht gefunden
			if(index == -1)
				return false;
			//Das Passwort hashen
			byte[] bytes = password.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(bytes);
			//Die Bytes muessen noch in das Textformat umgewandelt werden
			String digest_str = new String(digest);
			//Umwandlung in einen Hexadezimalen String
			String hex_hash = String.format("%040x", new BigInteger(1, digest_str.getBytes("UTF-8")));
			
			//
			sql = "UPDATE `PowerDefense`.`highscores` SET "+
					"`Password` = '" + hex_hash + "' "+
					"WHERE `highscores`.`INDEX` ="+index+";";
			state.executeUpdate(sql);
			//Hat funktioniert !
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	//Holt die Onlinehighscoreliste
	public PLAYER[] GetHighscorelist()
	{
		Vector<PLAYER>highscorelist= new Vector<PLAYER>();
		try
		{
			String sql = "SELECT * FROM highscores";
			ResultSet rs = state.executeQuery(sql);
			//
			while(rs.next())
			{
				PLAYER p = new PLAYER();
				p.SetName(rs.getString("Name"));
				p.AddPoints(rs.getInt("Points"));
				p.AddTime(rs.getInt("Playingtime"));
				//
				highscorelist.add(p);
			}
			
			PLAYER[] highscores = new PLAYER[highscorelist.size()];
			for(int i = 0; i < highscorelist.size(); i++)
			{
				highscores[i] = highscorelist.elementAt(i);
			}
			//
			return highscores;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	
	//Muss bei jedem Programmende aufgrufen werden
	public void Exit()
	{
		try
		{
			state.close();
			conn.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}
	
}
