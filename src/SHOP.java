//Die Shopklasse: Beinhaltet alle Towerinformationen (eine Art Bibliothek)
import java.util.Vector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;

public class SHOP
{
	//Die Liste, die alle Informationen jedes Towers beinhaltet
	public static Vector<TOWER> towerlibrary;
	
	//Konstruktor
	public SHOP()
	{
		towerlibrary = new Vector<TOWER>();
	}
	
	public boolean LoadFullTowerData()
	{
		try
		{
			String foldername = "towers/";
			File folder = new File(foldername);
			if(!folder.isDirectory())
				throw new Exception("The folder \"towers\" doesn't exist! Create one and place your towers in it");
		
			for(int i = 0; i < folder.listFiles().length; i++)
			{
				String filename = "towers/" + folder.listFiles()[i].getName();
				//Falls der tower nicht existiert soll nicht abgebrochen werden
				if(!LoadTowerData(filename))
					System.out.println("WARNING: \""+filename+"\" is apparently not a TOWER");
			}
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	//Die Informationen eines Towers aus einer Datei laden
	//@Author: Leonhard Kurthen
	//Anmerkung zur Aenderung: Windows-Textdatein haben als ENTER (Neue Zeile)
	// 						   die Zeichen 13 und 10 aus der ASCII Zeichentabelle
	//				Diese werden nun auch unterstuetzt
	//WICHTIG: Diese Methode ist im Grunde bei der Verwendung von SQL-Datenbanken ueberfluessig
	public static boolean LoadTowerData(String filename)
	{
		//Muss beim Hinzufuegen eines neuen Attributs erhoeht werden
		int attributes = 11;
		System.out.println(filename);
		/* Tower-Werte, die ausgelesen werden:
		 * 
		 * NAME 
		 * ELEMENT
		 * RANGE 
		 * FIRERATE  
		 * DAMAGE
		 * COST
		 * TOWERIMAGE
		 * SHOTIMAGE
		 * UPGRADE 1
		 * UPGRADE 2
		 * UPGRADE 3
		 */
		
		//Die Dateioperationen sind zum Teil unsicher. Deswegen muessen wir Exceptions abfangen
		try
		{
		//Zuerst ueberpruefen, ob die Datei exitiert
			File file = new File(filename);
			if(!file.isFile())
				throw new Exception("The file \""+filename+"\" does not exist!");
		
		//Den Filereader, der die Datei lesen kann, oeffnen
			FileReader rfile = new FileReader(file);
		
		//Das Array fuer die Zeilen
			String[] lines;
			lines = new String[attributes];
			
		//Die Datei in Zeilen aufteilen (4 Zeilen)
			for(int i = 0; i < attributes; i++)
			{
				int c = 0;
				lines[i] = new String("");
				
				//Die Zeile wird Zeichen fuer Zeichen ausgelesen
				boolean reading = true;
				while(reading)
				{
					//Ein Zeichen lesen
					c = rfile.read();
					
					//Wenn das Ende der Datei bereits erreicht wurde, die letzte Zeile aber noch
					//nicht erreicht wurde, ist die Datei ungueltig
					if(c == -1 && i < (attributes-1))
					{
						rfile.close();
						throw new Exception("The file \""+filename+"\" has not enough lines!");
					}
					
					//Wenn das Ende der letzten Zeile erreicht ist, oder man an einer neuen Zeile angelangt ist
					//muss die Schleife beendet werden
					//Das ASCII Zeichen mit der Nummer 10 entspricht einem 'ENTER' (in Linux)
					else if((c == -1 && i == (attributes -1)) || c == 10)
						break; //Oder: reading = false
					
					//In Windows wird die neue Zeile mit 13 und 10 dargestellt
					else if(c == 13)
					{
						//Die 10 wird geskippt
						rfile.skip(1);
						break;
					}
					
					//Wenn alles stimmt, wird das gelesene Zeichen dem String hinzugefuegt
					else
						lines[i] += (char)c;
				}
			
				//Zur Information
				//System.out.println(lines[i]); //DBG
			}
			
			//Schliessen
			rfile.close();
			
			//Der neue Tower, dessen Informationen gelesen wurden
			TOWER t = new TOWER();
			
			//Die gelesenen Zeilen separat interpretieren
			for(int i = 0; i < attributes; i++)
			{
				String attribute = "";
				
				//Das erste Leerzeichen(ASCII: 32) oder Tabulator(ASCII: 9) wird gesucht
				int space = lines[i].indexOf((char) 32);
				int tab = lines[i].indexOf((char) 9);
				int blank = 0;	//blank ist der platzhalter (kann entweder space oder tab sein)
				
				//Wenn space frueher als tab vorkommt und existiert
				if(space < tab && space != -1)
					blank = space;
				//Wenn tab frueher als space vorkommt und existiert
				else if(tab < space && tab != -1)
					blank = tab;
				//Wenn tab nicht existiert aber space schon
				else if(tab == -1 && space != -1)
					blank = space;
				//Wenn tab existiert aber space nicht
				else if(tab != -1 && space == -1)
					blank = tab;
				//Wenn keines von beiden existiert
				else
					throw new Exception("Invalid data format in file \""+filename+"\"");

				//Das Attribut lesen
				attribute = lines[i].substring(0, blank);
				
				//Der letzten Platzhalter vor dem Wert
				space = lines[i].lastIndexOf((char)32);
				tab = lines[i].lastIndexOf((char)9);
				//Wenn space spaeter als tab vorkommt
				if(space > tab)
					blank = space;
				//Wenn tab spaeter als space vorkommt
				else if(tab > space)
					blank = tab;
				//Eigentlich unmoeglich
				else
					throw new Exception("Impossible error");
				
				//Den Wert lesen
				String value = lines[i].substring(blank+1);
				
				//Das Attribut spezifisch unterscheiden
				switch(attribute)
				{
					case "NAME":
						t.SetName(value);
						break;
					case "ELEMENT":
						t.SetElement(Integer.parseInt(value));
						break;
					case "RANGE":
						t.SetRange(Integer.parseInt(value));
						break;
					case "FIRERATE":
						t.SetFirerate(Integer.parseInt(value));
						break;
					case "DAMAGE":
						t.SetDamage(Integer.parseInt(value));
						break;
					case "COST":
						t.SetCost(Integer.parseInt(value));
						break;
					case "IMAGE":
						//System.out.println("\"" + value + "\"");
						value = "images/" + value;
						File f = new File(value);
						if(!f.exists())
							throw new Exception("The file \""+value+"\" specified in \""+filename+"\"does not exist");
						t.SetImage(ImageIO.read(f));
						break;
					case "UPGRADE":
						space = lines[i].indexOf(" ");
						value = lines[i].substring(space+1);
						//NUMBER RANGE FIRERATE DAMAGE COST IMAGE SHOTIMAGE
						int upgradenumber = Integer.parseInt(value.substring(0, 1));
						UPGRADE up = new UPGRADE(value.substring(2));
						t.SetUpgrade(upgradenumber-1, up);
						break;
						
					case "SHOTIMAGE":
						try
						{
							//System.out.println(attribute);
							//System.out.println("\"" +value + "\"");
							BufferedImage shotimage = ImageIO.read(new File("images/"+value));
							t.SetShotimage(shotimage);
						}
						catch(Exception e)
						{
							e.printStackTrace();
							return false;
						}
						break;
					//Weitere Attribute...
						
					//Unbekanntes Attribut
					default:
						throw new Exception("Unknown attribute \""+attribute+"\" in file \""+filename+"\"");
				}
			}
			
			//Den frisch erstellten Tower in die Bibliothek einfuegen
			towerlibrary.add(t);
			
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
}
