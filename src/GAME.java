import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class GAME implements MouseListener
{
	//Der Servername
	public static String Servername = "leonhardk.noip.me";
	//Alle Tower im Spielfeld
	public static  Vector<TOWER>	towerlist;
	//Die aktiven Schuesse
	public static  Vector<ANIMATION>	animationlist;
	//Der Spieler
	static PLAYER 	player;
	//Der Shop, der Towerinformationen beinhaltet
	static SHOP			shop;
	//SQL-Datenbankverbindung
	static SQL sql;
	//
	static HTTP http;
	//
	static MAIL mail;
	// grafische Oberfl�che
	static GUI gui;
	//ALle Levels, die das Spiel besitzt
	static Vector<LEVEL> levellist;
	static int levelindex;
	//Running the main loop
	static boolean running;
	static boolean wait = true;
	//index to control the release of enemys into the map
	static long totaltime;
	static boolean waverunning;
	static ELEMENT element;
	static public  boolean online;
	
	GAME()
	{
		//Attribute initialisieren
		totaltime = 0;
		//player = new PLAYER("NO USER");
		towerlist = new Vector<TOWER>();
		levellist = new Vector<LEVEL>();
		animationlist = new Vector<ANIMATION>();
			
		//Die Ordner erstellen, falls sie noch nicht existieren			
		CreateStructure();				
		
		//Nicht-Datenbank-Daten aktualisieren (Bilder, Elementdatei und jar-Datei)
		HTTP http = new HTTP();
		http.CheckUpdateList();
		http.UpdateFiles();
				
		//Die Elementedatei laden
		element = new ELEMENT();
				
		//Die Verbindung zur Datenbank aufbauen
		sql = new SQL();
		//Alle Daten aktualisieren (Level, Waves, Enemys und Towers)
		sql.UpdateData();
			
		//Der Shop
		shop = new SHOP();
				
		//Alle Daten einlesen
		if(!shop.LoadFullTowerData())
			System.out.println("Error while loading towerdata from the \"towers\" folder!");
		if(!WAVE.LoadFullEnemyData())
			System.out.println("Error while loading enemydata from the \"enemys\" folder!");
		if(!LoadFullLevelData())
			System.out.println("Error while loading leveldata from the \"levels\" folder!");
		
		gui = null;
		running = true;
		waverunning = false;
		element = null;
	}
	
	//Einen Tower hinzufuegen (Pixelkoordinaten) 
	public static void AddTower(int posX,int posY,int shopnumber)
	{	
		//Die Pixelkoordinaten in das Map-Koordinatensystem umwandeln
		int x = posX/20;
		int y = posY/20;
		// Falls genuegend Gold vorhanden ist
		if(player.gold >= SHOP.towerlibrary.elementAt(shopnumber).ReturnCost())
		{
		// Falls das Feld frei ist 
		if(levellist.elementAt(levelindex).ReturnMap().mapfield[x][y]==-1)
		{
			//TOWER t= new TOWER(x*20+10,y*20+10,SHOP.towerlibrary.elementAt(shopnumber).ReturnImage());
			TOWER t = new TOWER(x*20,y*20,SHOP.towerlibrary.elementAt(shopnumber));
			towerlist.add(t);
			// index des towers wird in das Array eingespeichert 
			levellist.elementAt(levelindex).ReturnMap().mapfield[x][y] = towerlist.indexOf(t);
			gui.ResetActiveTower();
			player.gold-=shop.towerlibrary.elementAt(shopnumber).ReturnCost();
		}
		}
		else
		{
			System.out.println("Du hast nicht gen�gend Gold! Dir fehlen noch " + (shop.towerlibrary.elementAt(shopnumber).ReturnCost()-player.gold) + " Goldst�cke");
		}
	}
	
	//Ein TOWER wird entfernt
	void RemoveTower(int towerindex)
	{
		if(towerindex>=0 && towerindex <towerlist.size())
		{
			towerlist.remove(towerindex);
		}
	}
 
	//Erzeugt die Grundstruktur für das Spiel
	public static void CreateStructure()
	{
		String[] foldernames = {"images", "levels", "waves", "enemys", "towers"};
		for(int i = 0; i < foldernames.length; i++)
		{
			File folder = new File(foldernames[i]);
			if(!folder.isDirectory())
				folder.mkdir();
		} 
	}
	
	//Die Hauptmethode des Spiels
	public static void main(String[] args)
	{
		//Attribute initialisieren
		totaltime = 0;
		player = new PLAYER("Leonhard");
		towerlist = new Vector<TOWER>();
		levellist = new Vector<LEVEL>();
		animationlist = new Vector<ANIMATION>();
		
		
		
		//Die Ordner erstellen, falls sie noch nicht existieren
		CreateStructure();
		
		//Nicht-Datenbank-Daten aktualisieren (Bilder, Elementdatei und jar-Datei)
		HTTP http = new HTTP();
		http.CheckUpdateList();
		http.UpdateFiles();
		
		//Die Elementedatei laden
		element = new ELEMENT();
		
		//Die Verbindung zur Datenbank aufbauen
		sql = new SQL();
		//Alle Daten aktualisieren (Level, Waves, Enemys und Towers)
		sql.UpdateData();
		
		//Der Shop
		shop = new SHOP();
		
		//Alle Daten einlesen
		 if(!shop.LoadFullTowerData())
             System.out.println("Error while loading towerdata from the \"towers\" folder!");
		 if(!WAVE.LoadFullEnemyData())
             System.out.println("Error while loading enemydata from the \"enemys\" folder!");
		 if(!LoadFullLevelData())
             System.out.println("Error while loading leveldata from the \"levels\" folder!");
		
		//Die GUI erzeugen
		gui=new GUI();
		running = true;
		
		long lnStartTime = 0;
		long lnLastTime = System.currentTimeMillis();
		
		System.out.println("[GAME] GAME IS RUNNING !");
//######HAUPTSCHLEIFE
		while(running)
		{
//############MOVE

			lnStartTime = System.currentTimeMillis();
			long timeDifference = lnStartTime - lnLastTime;
			double dTime = timeDifference/1000d;
			//double dTime = 0.0001d;
			totaltime += timeDifference;
			lnLastTime = lnStartTime;
			
			if(waverunning)
			{
				levellist.elementAt(levelindex).MoveCurrentWave(dTime);
			
				//Alle Tower sollen schießen
				for(int i = 0; i < towerlist.size(); i++)
				{
					towerlist.elementAt(i).Shoot(dTime);
				}
				for(int i = 0; i < animationlist.size(); i++)
				{
					animationlist.elementAt(i).MoveShot(dTime);
				}
				//
				if(CONSOLE.IsWatching())
					gui.console.Watch(dTime);
			}
			
//########  RENDER
			
			gui.refresh();
		}
		sql.CreateHighscoreEntry();
		sql.Exit();
		System.exit(0);
		return;
	}

	public void Start()
	{
		gui = new GUI();
	}
	
	public void MainLoop()
	{	
		running = true;
		
		long lnStartTime = 0;
		long lnLastTime = System.currentTimeMillis();
		
		System.out.println("[GAME] GAME IS RUNNING ! ");
//######HAUPTSCHLEIFE
		while(running)
		{
			if(wait)
			{
				System.out.println("{ RUNNING }");
				wait = false;
			}
			
//############MOVE
			lnStartTime = System.currentTimeMillis();
			long timeDifference = lnStartTime - lnLastTime;
			double dTime = timeDifference/1000d;
			totaltime += timeDifference;
			lnLastTime = lnStartTime;
			
			if(waverunning)
			{
				levellist.elementAt(levelindex).MoveCurrentWave(dTime);
			
				//Alle Tower sollen schießen
				for(int i = 0; i < towerlist.size(); i++)
				{
					towerlist.elementAt(i).Shoot(dTime);
				}
				for(int i = 0; i < animationlist.size(); i++)
				{
					animationlist.elementAt(i).MoveShot(dTime);
				}
				//
				if(CONSOLE.IsWatching())
					gui.console.Watch(dTime);
			}
			
//########  RENDER
			//System.out.println(guicreated);
			if(gui != null)
			{
				gui.refresh();
			}
		}
		System.out.println("[GAME] EXITING GAME...");
		
		sql.CreateHighscoreEntry();
		sql.Exit();
		System.exit(0);
		return;
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		System.out.print("1");
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	public static boolean LoadFullLevelData()
	{
		if(levellist == null)
			levellist = new Vector<LEVEL>();
		try
		{
			File folder = new File("levels/");
			if(!folder.isDirectory())
				throw new Exception("The folder \"levels\" doesn't exist! Create one and place your levels in it");
		
			for(int i = 0; i < folder.listFiles().length; i++)
			{
				String filename = "levels/" + folder.listFiles()[i].getName();
				//Falls der enemy nicht existiert, soll nicht abgebrochen werden
				if(!LoadLevelData(filename))
					System.out.println("WARNING: \""+filename+"\" is apparently not an LEVEL ");
			}
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public static boolean LoadLevelData(String filename)
	{
		System.out.println(filename);
		/*// Eine Leveldatei schaut wie folgt aus:
		 * --------------------------------
		 * NAME Grasplateau     //-> Der Name, der angezeigt wird
		 * DIFFICULTY 1			//-> Die Schwierigkeit, die angezeigt wird (kein effekt)
		 * COLORMAP	ColorMap2.png //-> Die Farbkarte
		 * BACKGROUND background.png //-> Das Hintergrundbild
		 * START 190 -10		//-> Die Startposition der Gegner
		 * POSTFIX .txt			//-> Die Dateiendung fuer die Wavedateien
		 * WAVES wave1 wave1*2 wave1*2/2 
		 * //-> Die Waves haben Modifikatoren (+,-,*,/) fuer die Anzahl oder die Zeitdifferenz,
		 * //Damit man keine neue Wave erstellen muss
		 * --------------------------------
		 * 
		 */
		int attributes = 7;
		try
		{
			File f = new File(filename);
			if(!f.isFile())
				throw new Exception("ERROR: The file \""+filename+"\" does not exist!");
			BufferedReader br = new BufferedReader(new FileReader(f));
			String[] lines = new String[attributes];
			for(int i = 0; i < attributes; i++)
			{
				lines[i] = br.readLine();
				if(lines[i] == "" || lines[i] == null)
				{
					br.close();
					throw new Exception("ERROR: The file \""+filename+"\" has not enough attributes!");
				}
			}
			
			br.close();
			LEVEL l = new LEVEL();
			String postfix = "";
			
			for(int i = 0; i < attributes; i++)
			{
				int space = lines[i].indexOf((char)32);
				if(space < 0)
					throw new Exception("ERROR: Invalid fileformat in file \""+filename+"\" !");
				String attribute = lines[i].substring(0, space);
				if(attribute == null || attribute == "")
					throw new Exception("ERROR: Invalid fileformat in file \""+filename+"\" !");
				
				switch(attribute)
				{
				case "NAME":
					String name = lines[i].substring(space+1);
					l.SetName(name);
					break;
					
				case "DIFFICULTY":
					int difficulty = Integer.parseInt(lines[i].substring(space+1));
					l.SetDifficulty(difficulty);
					break;
				
				case "COLORMAP":
					l.LoadColormap("images/" + lines[i].substring(space+1));
					break;
					
				case "BACKGROUND":
					l.LoadBackground("images/" + lines[i].substring(space+1));
					break;
					
				case "START":
					int space2 = lines[i].indexOf((char)32, space+1);
					if(space2 == -1)
						throw new Exception("ERROR: Invalid Attribute \""+lines[i]+"\" in file \""+filename+"\"");
					String coordinateX = lines[i].substring(space+1, space2);
					String coordinateY = lines[i].substring(space2+1);
					int x = Integer.parseInt(coordinateX);
					int y = Integer.parseInt(coordinateY);
					l.SetEnemyStart(x,y);
					break;
					
				case "POSTFIX":
					postfix = lines[i].substring(space+1);
					break;
						
				case "WAVES":
					boolean endofline = false;
					Vector<WAVE> wavelist;
					while(!endofline)
					{
						int nextspace = lines[i].indexOf((char)32, space+1);
						if(nextspace == -1)
						{
							endofline = true;
							nextspace = lines[i].length();
						}
						String wave = lines[i].substring(space+1,nextspace);
						space = nextspace;
						int[] operators = new int[4];
						operators[0] = wave.indexOf('+');
						operators[1] = wave.indexOf('-');
						operators[2] = wave.indexOf('*');
						operators[3] = wave.indexOf('/');
						//
						int firstoperatorpos = -1;
						int firstoperator = -1;
						int secondoperatorpos = -1;
						int secondoperator = -1;
						for(int k = 0; k < 4; k++)
						{
							if(operators[k] > -1 && (operators[k] < firstoperatorpos || firstoperator == -1))
							{
								firstoperator = k;
								firstoperatorpos = operators[k];
							}
						}
						for(int k = 0; k < 4; k++)
						{
							if(operators[k] > firstoperatorpos)
							{
								secondoperator = k;
								secondoperatorpos = operators[k];
							}
						}
						//
						if(firstoperator == -1)
						{
							if(!l.LoadWaveData("waves/" + wave + postfix))
								throw new Exception("ERROR: The file \""+wave+"\" specified in \""+filename+"\" is invalid!");
						}
						else
						{
							if(!l.LoadWaveData("waves/" + wave.substring(0, firstoperatorpos) + postfix))
								throw new Exception("ERROR: The file \""+wave.substring(0, firstoperatorpos)+
													"\" specified in \""+filename+"\" is invalid!");
							WAVE w = l.ReturnWavelist().lastElement();
							if(secondoperator == -1)
							{
								double firstoperand = Double.parseDouble(wave.substring(firstoperatorpos+1));
								w.ModifyTotalEnemyNumber(wave.substring(firstoperatorpos, firstoperatorpos+1).charAt(0),
														firstoperand);
							}
							else
							{
								double firstoperand = Double.parseDouble(wave.substring(firstoperatorpos+1,secondoperatorpos));
								double secondoperand = Double.parseDouble(wave.substring(secondoperatorpos+1));
								w.ModifyTotalEnemyNumber(wave.substring(firstoperatorpos,firstoperatorpos+1).charAt(0),
														firstoperand);
								w.ModifiyTotalReleaseTimes(wave.substring(secondoperatorpos, secondoperatorpos+1).charAt(0),
															secondoperand);
							}
							int last = l.ReturnWavelist().size();
							wavelist = l.ReturnWavelist();
							wavelist.remove(last-1);
							wavelist.add(w);
							l.SetWavelist(wavelist);
						}
						
					}
					break;
				//endof: case "WAVE"
					
				default:
					throw new Exception("ERROR: Invalid attribute \""+attribute+"\" in file \""+filename+"\" !");
				}
			}
			
			levellist.add(l);
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
}
	
	
