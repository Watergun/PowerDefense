import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import javax.imageio.ImageIO;

public class WAVE
{
	//Die Bibliothek, die alle Daten zu allen ENEMYs umfasst
	public static Vector<ENEMY>	enemylibrary;
	//
	//Alle enemygroups,die diese WAVE besitzt
	private Vector<Vector<ENEMY>>enemygroups;
	//Der jeweilige offset der enemygroups
	private Vector<Double> groupoffsets;
	//Die jeweilige Zeit zwischen den Startpunkten der ENEMYs der enemygroups
	private Vector<Double> groupreleasetimes;
	//Ein jeweiliger groupindex fuer die enemygroups, um das Starten zu steuern
	private Vector<Integer> groupindices;
	
	//Die gesamte Laufzeit der WAVE
	private double wavetime;
	
	//Ein Attribut, das nur dem Spieler sagt, mit welcher Schwierigkeit er es zu tun hat
	private int difficulty;

	//Der Konstruktor
	WAVE()
	{	
		difficulty = 0;
		wavetime = 0;
		groupindices = new Vector<Integer>();
		groupoffsets = new Vector<Double>();
		groupreleasetimes = new Vector<Double>();
		enemygroups = new Vector<Vector<ENEMY>>();
	}
	
	//GET Methoden
	Vector<Vector<ENEMY>> ReturnEnemygroups()
	{
		return enemygroups;
	}
	Vector<Double> ReturnOffsets()
	{
		return groupoffsets;
	}
	Vector<Double> ReturnReleasetimes()
	{
		return groupreleasetimes;
	}
	Vector<Integer>ReturnIndices()
	{
		return groupindices;
	}
	int ReturnEnemySize()
	{
		//Die Zaehlvariable
		int count = 0;
		//Alle enemygroups durchgehen
		for(int i = 0; i < enemygroups.size(); i++)
		{
			//Und die ENEMYs zaehlen
			for(int k = 0; k < enemygroups.elementAt(i).size(); k++)
			{
				count++;
			}
		}
		//Die Groesse zurueckgeben
		return count;
	}
	int ReturnDifficulty()
	{
		return difficulty;
	}
	
	//SET Methoden
	void SetDifficulty(int NewDifficulty)
	{
		difficulty = NewDifficulty;
	}
	//Modifikatoren fuer alle ENEMYs
	public void ModifyTotalEnemyNumber(char operator, double value)
	{
		//Veraendert die Anzahl an ENEMYs in allen enemygroups
		for(int i = 0; i < enemygroups.size(); i++)
		{
			if(enemygroups.elementAt(i).isEmpty())
				return;
			//Den ersten ENEMY abspeichern
			ENEMY e = enemygroups.elementAt(i).elementAt(0);
			int number = enemygroups.elementAt(i).size();
			int newnumber = 0;
			//+ - * / werden bisher unterstuetzt
			switch(operator)
			{
			case '+':
				newnumber = number + (int)value;
				break;
				case '-':
				newnumber = number - (int)value;
				break;
			case '*':
				newnumber = (int)(((double)number)*value);
				break;
			case '/':
				newnumber = (int)(((double)number)/value);
				break;
			default:
				newnumber = number;
			}
			//
			enemygroups.elementAt(i).clear();
			//
			for(int l = 0; l < newnumber; l++)
			{
				enemygroups.elementAt(i).add(new ENEMY(e));
			}
		}
	}
	public void ModifiyTotalReleaseTimes(char operator, double value)
	{
		for(int i = 0; i < enemygroups.size(); i++)
		{
			double time = groupreleasetimes.elementAt(i);
			double newtime = 0.0d;
			switch(operator)
			{
			case '+':
				newtime = time + value;
				break;
			case '-':
				newtime = time - value;
				break;
			case '*':
				newtime = time * value;
				break;
			case '/':
				newtime = time / value;
				break;
			default:
				newtime = time;
			}
			groupreleasetimes.set(i, newtime);
		}
	}
	public void SetStartPosition(int x, int y)
	{
		//Setzt fuer jeden ENEMY diese Startposition
		for(int i = 0; i < ReturnEnemySize(); i++)
		{
			IterateEnemygroups(i).SetPosition(x, y);
		}
	}
	public void Revive()
	{
		for(int i = 0; i < ReturnEnemySize(); i++)
		{
			IterateEnemygroups(i).SetAlive(true);
		}
	}
	public void ResetIndices()
	{
		wavetime = 0;
		for(int i = 0; i < groupindices.size(); i++)
		{
			groupindices.setElementAt(0, i);
		}
	}
	//Fuegt eine neue enemygroup hinzu
	void AddEnemygroup(ENEMY enemy, int count, double releasetime, double offset)
	{
		Vector<ENEMY> enemylist = new Vector<ENEMY>();
		for(int i = 0; i < count; i++)
		{
			enemylist.add(new ENEMY(enemy));
		}
		enemygroups.add(enemylist);
		groupreleasetimes.add(releasetime);
		groupoffsets.add(offset);
		groupindices.add(-1);
	}
	
	//Eine Hilfmethode, die das Durchlaufen der enemygroups leichter machen soll
	ENEMY IterateEnemygroups(int i)
	{
		if(i < 0 || i > ReturnEnemySize())
		{
			return null;
		}
		
		int count = -1;
		for(int j = 0; j < enemygroups.size(); j++)
		{
			for(int k = 0; k < enemygroups.elementAt(j).size(); k++)
			{
				count++;
				if(count == i)
					return enemygroups.elementAt(j).elementAt(k);
			}
		}
		return null;
	}
	
	//Bewegt alle Gegner, die in den enemygroups sind und von den groupindices erfasst sind
	void moveWave(double dTime)
	{
		//Die Laufzeit erhoehen
		wavetime += dTime;
		
		//Jetzt muss jede enemygroup durchgegangen werden
		for(int i = 0; i < enemygroups.size(); i++)
		{	
			//Die zeiten und den index raussuchen
			double offset = groupoffsets.elementAt(i);
			double releasetime = groupreleasetimes.elementAt(i);
			int groupindex = groupindices.elementAt(i);
			
			//Falls es an der Zeit ist, einen neuen ENEMY zu starten
			//"wavetime - offset" ist die Startposition der enemygroup
			//groupindex*releasetime zeigt die Zeit an, bei der immer ein neuer ENEMY gestartet wird
			if((wavetime-offset) >= ((groupindex+1)*releasetime) && groupindex < enemygroups.elementAt(i).size())
			{
				//Ein ENEMY kommt hinzu
				groupindex++;
				groupindices.setElementAt(groupindex, i	);
			}
		}

		//Jetzt muessen alle aktiven ENEMYs bewegt werden
		//Wenn der groupindex groesser als die Anzahl ENEMYs in der enemygroup ist, tritt eine NPE auf
		//-> Deshalb die Bedingung
		for(int i = 0; i < enemygroups.size(); i++)
		{
			for(int k = 0; k < enemygroups.elementAt(i).size(); k++)
			{
				if(k <= groupindices.elementAt(i) && enemygroups.elementAt(i).elementAt(k).isAlive()) 
					enemygroups.elementAt(i).elementAt(k).move(dTime);
			}
		}
	}
	
	//Laed die enemylibrary aus dem Ordner enemys
	//Liest die ENEMY-Daten einer bestimmten Datei ein
	//Diese Methode existiert in kommentierter Form (beinahe) analog in SHOP 
	//Der einzige Unterschied sind die Parameter der Datei
	//WICHTIG: Diese Methode ist bei der Verwendung von SQL ueberfluessig
	public static boolean LoadFullEnemyData()
	{
		if(enemylibrary == null)
			enemylibrary = new Vector<ENEMY>();
		try
		{
			File folder = new File("enemys/");
			if(!folder.isDirectory())
				throw new Exception("The folder \"enemys\" doesn't exist! Create one and place your enemys in it");
		
			for(int i = 0; i < folder.listFiles().length; i++)
			{
				String filename = "enemys/" + folder.listFiles()[i].getName();
				//Falls der enemy nicht existiert, soll nicht abgebrochen werden
				if(!LoadEnemyData(filename))
					System.out.println("WARNING: \""+filename+"\" is apparently not an ENEMY ");
			}
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	public static boolean LoadEnemyData(String filename)
	{
		//Muss beim Hinzufuegen eines neuen Attributs erhoeht werden
		int attributes = 6;
		System.out.println(filename);
		/* Enemy-Werte, die ausgelesen werden:
		 * 
		 * NAME 
		 * ELEMENT
		 * VELOCITY 
		 * HEALTH  
		 * REWARD
		 * IMAGE
	     */
		try
		{
			File file = new File(filename);
			if(!file.isFile())
				throw new Exception("The file \""+filename+"\" does not exist!");
				
			FileReader rfile = new FileReader(file);
			String[] lines;
			lines = new String[attributes];
			for(int i = 0; i < attributes; i++)
			{
				int c = 0;
				lines[i] = new String("");
				boolean reading = true;
				while(reading)
				{
					c = rfile.read();
					if(c == -1 && i < (attributes-1))
					{
						rfile.close();
						throw new Exception("The file \""+filename+"\" has not enough lines!");
					}
					else if((c == -1 && i == (attributes -1)) || c == 10)
						break;
					else if(c == 13)
					{
						rfile.skip(1);
						break;
					}
					else
						lines[i] += (char)c;
				}
				//System.out.println(lines[i]); //DBG
			}
			rfile.close();
			ENEMY e = new ENEMY();
			for(int i = 0; i < attributes; i++)
			{
				String attribute = "";
				int space = lines[i].indexOf((char) 32);
				int tab = lines[i].indexOf((char) 9);
				int blank = 0;	
				if(space < tab && space != -1)
					blank = space;
				else if(tab < space && tab != -1)
					blank = tab;
				else if(tab == -1 && space != -1)
					blank = space;
				else if(tab != -1 && space == -1)
					blank = tab;
				else
					throw new Exception("Invalid data format in file \""+filename+"\"");
				attribute = lines[i].substring(0, blank);
				space = lines[i].lastIndexOf((char)32);
				tab = lines[i].lastIndexOf((char)9);
				if(space > tab)
					blank = space;
				else if(tab > space)
					blank = tab;
				else
					throw new Exception("Impossible error");
				
				String value = lines[i].substring(blank+1);
				switch(attribute)
				{
					case "NAME":
						e.SetName(value);
						break;
					case "ELEMENT":
						e.SetElement(Integer.parseInt(value));
						break;
					case "VELOCITY":
						e.SetVelocity(Double.parseDouble(value));
						break;
					case "HEALTH":
						e.SetHealth(Integer.parseInt(value));
						break;
					case "REWARD":
						e.SetReward(Integer.parseInt(value));
						break;
					case "IMAGE":
						value = "images/" + value;
						File f = new File(value);
						if(!f.exists())
							throw new Exception("The file \""+value+"\" specified in \""+filename+"\"does not exist");
						e.SetImage(ImageIO.read(f));
						break;
					default:
						throw new Exception("Unknown attribute \""+attribute+"\" in file \""+filename+"\"");
				}
			}
			enemylibrary.add(e);		
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
}