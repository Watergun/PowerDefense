import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

//Die Klasse, die ein Level repraesentiert
public class LEVEL 
{
	//Der Name, der dem Spieler angezeigt wird
	private String name;
	//Die WAVEs, die dieses LEVEL besitzt
	private Vector<WAVE> wavelist;
	//Die Schwierigkeit, die dem Spieler angezeigt wird
	private int difficulty;
	//Ein Index, um die aktive WAVE aus der wavelist festzulegen
	private int waveindex;
	//Der Hintergrund
	private MAP map;
	//Der Startpunkt aller ENEMYs
	private Point enemystart;
	
	LEVEL()
	{
		map = new MAP();
		wavelist = new Vector<WAVE>();
		difficulty = 0;
		name = "";
		waveindex = -1;
		enemystart = new Point(0,0);
	}
	
	//GET Methoden
	String ReturnName()
	{
		return name;
	}
	BufferedImage ReturnBackground()
	{
		return map.ReturnBackground();
	}
	BufferedImage ReturnColormap()
	{
		return map.ReturnMap();
	}
	Vector<WAVE> ReturnWavelist()
	{
		return wavelist;
	}
	int ReturnDifficulty()
	{
		return difficulty;
	}
	int ReturnWaveindex()
	{
		return waveindex;
	}
	Point ReturnEnemyStart()
	{
		return enemystart;
	}
	WAVE ReturnCurrentWave()
	{
		if(waveindex > -1)
			return wavelist.elementAt(waveindex);
		else 
			return null;
	}
	MAP ReturnMap()
	{
		return map;
	}
	
	//SET Methoden
	void SetName(String NewName)
	{
		name = NewName;
	}
	boolean LoadBackground(String filename)
	{
		return(map.LoadBackground(filename));
	}
	boolean LoadColormap(String filename)
	{
		return(map.LoadMap(filename));
	}
	void SetWavelist(Vector<WAVE> NewWavelist)
	{
		wavelist = NewWavelist;
	}	
	void SetDifficulty(int NewDifficulty)
	{
		difficulty = NewDifficulty;
	}
	void SetEnemyStart(int NewX, int NewY)
	{
		enemystart = new Point(NewX,NewY);
		for(int i = 0; i < wavelist.size(); i++)
		{
			WAVE w = wavelist.elementAt(i);
			w.SetStartPosition(NewX, NewY);
		}
	}
	void SetMap(MAP NewMap)
	{
		map = NewMap;
	}
	//Mit Bedacht verwenden!
	void SetWave(int index)
	{
		waveindex = index;
	}
	
	//Diese Methode erhoeht den waveindex, falls es noch eine WAVE gibt
	void StartNewWave()
	{
		if((waveindex+1) < wavelist.size())
			waveindex++;
	}
	
	//Hilfsmethoden
	boolean isCurrentWaveEmpty()
	{
		if(waveindex == -1)
			return true;
		for(int i = 0; i < wavelist.elementAt(waveindex).ReturnEnemySize(); i++)
		{
			if(wavelist.elementAt(waveindex).IterateEnemygroups(i).isAlive())
				return false;
		}
		return true;
	}
	
	void removeEnemy(ENEMY e)
	{
		boolean found = false;
		//Den ENEMY entfernen
		WAVE w = wavelist.elementAt(waveindex);
		for(int i = 0; i < w.ReturnEnemySize(); i++)
		{
			if(w.IterateEnemygroups(i).equals(e))
			{
					w.IterateEnemygroups(i).SetAlive(false);
					w.IterateEnemygroups(i).SetPosition(enemystart.x, enemystart.y);
					found = true;
			//		wavelist.set(waveindex, w);
			}
		}
		if(found == false)
			System.out.println("FATAL ERROR: ENEMY FOR REMOVAL NOT FOUND! " + e.hashCode());
		
		//Wenn die WAVE zu Ende ist und sich kein Schuss mehr bewegt
		if(isCurrentWaveEmpty() && GAME.animationlist.size() == 0)
		{
			System.out.println("### Wave zu Ende ###");
			GAME.waverunning = false;
			GAME.gui.Startbutton.setEnabled(true);
		}
	}
	
	//Bewegt die aktive WAVE
	void MoveCurrentWave(double dTime)
	{
		if(waveindex > -1)
			wavelist.elementAt(waveindex).moveWave(dTime);
	}
	
	
	//Diese Methode ist nur zu Debuggingzwecken gedacht
	void PRINT_ALL_COORDS()
	{
		for(int i = 0; i < wavelist.elementAt(waveindex).ReturnEnemygroups().size(); i++)
		{
			for(int k = 0; k < wavelist.elementAt(waveindex).ReturnEnemygroups().elementAt(i).size(); k++)
				System.out.println("["+i+"]["+k+"]: "+
									wavelist.elementAt(waveindex).ReturnEnemygroups().elementAt(i).elementAt(k).ReturnPositionX()+
									" - "+ wavelist.elementAt(waveindex).ReturnEnemygroups().elementAt(i).elementAt(k).ReturnPositionY()+
									" "+wavelist.elementAt(waveindex).ReturnEnemygroups().elementAt(i).elementAt(k).isAlive()+
									" "+wavelist.elementAt(waveindex).ReturnEnemygroups().elementAt(i).elementAt(k).hashCode());
		}
	}
	//Laed eine bestimmte WAVE und fuegt sie ẃavelist hinzu
	public boolean LoadWaveData(String filename)
	{
		System.out.println(filename);
		/*Eine Wavedatei besteht aus:
		 * 
		 * DIFFICULTY 1
		 * ENEMYGROUP Schaf 10 2 0
		 * ENEMYGROUP Schaf 5 2 1
		 * ...
		 * 
		 * Es koennen beliebig viele ENEMYGROUPs folgen
		 */
		try
		{
			File f = new File(filename);
			if(!f.isFile())
				throw new Exception("ERROR: The file \""+filename+"\" does not exist!");
			BufferedReader br = new BufferedReader(new FileReader(f));
			Vector<String> lines = new Vector<String>();
			boolean reading = true;
			while(reading)
			{
				String line = br.readLine();
				if(line == null || line == "")
					break;
				lines.add(line);
			}
			br.close();
			WAVE w = new WAVE();
			
			for(int i = 0; i < lines.size(); i++)
			{
				int space = lines.elementAt(i).indexOf((char)32);
				if(space == -1)
					throw new Exception("ERROR: The file \""+filename+"\" is invalid!");
				String attribute = lines.elementAt(i).substring(0, space);
				String value = lines.elementAt(i).substring(space+1);
				
				switch(attribute)
				{
				case "DIFFICULTY":
					w.SetDifficulty(Integer.parseInt(value));
					break;
					
				case "ENEMYGROUP":
					//NAME ANZAHL RELEASE OFFSET
					int space1 = value.indexOf((char)32);
					int space2 = value.indexOf((char)32, space1+1);
					int space3 = value.indexOf((char)32, space2+1);
					if(space1 == -1 || space2 == -1 || space3 == -1)
						throw new Exception("ERROR: The file \""+filename+"\" has an invalid line: \""+attribute+value+"\"");
					
					String name = value.substring(0, space1);
					int number = Integer.parseInt(value.substring(space1+1, space2));
					double release = Double.parseDouble(value.substring(space2+1, space3));
					double offset = Double.parseDouble(value.substring(space3+1));
					
					int libindex = -1;
					for(int k = 0; k < WAVE.enemylibrary.size(); k++)
					{
						if(WAVE.enemylibrary.elementAt(k).ReturnName().equals(name))
							libindex = k;
					}
					if(libindex == -1)
						throw new Exception("ERROR: The ENEMY \""+name+"\" specified in file \""+filename+"\" does not exist!");
					
					ENEMY e = new ENEMY(WAVE.enemylibrary.elementAt(libindex));
					e.SetPosition(enemystart.x, enemystart.y);
					w.AddEnemygroup(e, number, release, offset);
					break;
					
				default:
					throw new Exception("ERROR: Invalid attribute \""+attribute+"\" in file \""+filename+"\"");	
				}
			}
			wavelist.add(w);
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	/*public boolean LoadModifiedWaves(String allwaves)
	{
		//wave1 wave2 wave3 wave1*10 wave2*10/2
		boolean endofline = false;
		int space = -1;
		while(!endofline)
		{
			int nextspace = allwaves.indexOf(" ", space+1);
			if(nextspace == -1)
			{
				endofline = true;
				nextspace = allwaves.length();
			}
			String wave = allwaves.substring(space+1,nextspace);
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
				WAVE w = GAME.sql.GetSpecificWave(wave);
				if(w == null)
				{
					System.out.println("ERROR: The Databasemanager could not find: "+wave);
					return false;
				}
				wavelist.add(w);
			}
			else
			{
				WAVE w = GAME.sql.GetSpecificWave(wave.substring(0, firstoperatorpos));
				if(w == null)
				{
					System.out.println("ERROR: The Databasemanager could not find: "+
										wave.substring(0, firstoperatorpos));
					return false;
				}
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
				wavelist.add(w);
			}
		}
		return true;
	}
	*/
}
