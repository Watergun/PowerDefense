import java.awt.Color;
import java.awt.image.BufferedImage;

public class ENEMY
{
	//Der Bezeichner
	private String name;
	// Position X
	private float posX;
	// Position Y
	private float posY;
	// Anzahl der Leben
	private int healthpoints;
	private int initialhealth;
	//Die Geschwindigkeit
	private double velocity;
	// Gold-Reward
	private int reward;
	//Das Bild
	private BufferedImage image;
	//Die letzte ausgefuehrte Bewegung (fuer BackToTrack())
	private int lastMovement;
	//Der Lebensstatus
	private boolean alive;
	//Das eigene Element
	private int element;
	//Standard-Konstruktor
	ENEMY()
	{
		name = "-";
		healthpoints = initialhealth = 0;
		posX = 190;
		posY = -10;
		velocity = 100.0d;
		lastMovement = 0;
		image = null;
		alive = true;
		element = -1;
	}
	//En Konstruktor, der einen anderen ENEMY als Vorlage nimmt
	ENEMY(ENEMY template)
	{
		name = template.ReturnName();
		healthpoints = initialhealth = template.ReturnInitialHealth();
		posX = template.ReturnPositionX();
		posY = template.ReturnPositionY();
		velocity = template.ReturnVelocity();
		lastMovement = 0;
		image = template.ReturnImage();
		alive = true;
		element = template.ReturnElement();
		reward = template.GetReward();
	}
	
	private int GetReward() {
		
		return reward;
	}
	//GET Methoden
	int ReturnPositionX()
	{
		return (int)posX;
	}
	int ReturnPositionY()
	{
		return (int)posY;
	}
	BufferedImage ReturnImage()
	{
		return image;
	}
	String ReturnName()
	{
		return name;
	}
	int ReturnHealth()
	{
		return healthpoints;
	}
	int ReturnInitialHealth()
	{
		return initialhealth;
	}
	double ReturnVelocity()
	{
		return velocity;
	}
	boolean isAlive()
	{
		return alive;
	}
	int ReturnElement()
	{
		return element;
	}
	
	//SET Methoden
	void SetName(String NewName)
	{
		name = NewName;
	}
	void SetVelocity(double NewVelocity)
	{
		velocity = NewVelocity;
	}
	void SetHealth(int NewHealth)
	{
		initialhealth = healthpoints = NewHealth;
	}
	void SetImage(BufferedImage NewImage)
	{
		image = NewImage;
	}
	void SetPosition(int x, int y)
	{
		posX = x;
		posY = y;
	}
	void SetAlive(boolean LivingFlag)
	{
		alive = LivingFlag;
	}
	void SetElement(int NewElement)
	{
		element = NewElement;
	}
	void kill()
	{
		healthpoints = 0;
		alive = false;
	}
	
	//Methode, die den ENEMY zurueck auf den Weg setzt, falls er vom Weg abgekommen ist
	void BackToTrack()
	{
		//Den Weg suchen
		boolean searching = true;
		
		while(searching)
		{
			//Die letzte Bewegung rueckgaengig machen
			switch(lastMovement)
			{
			case 1:
				posY += 1;
				break;
			case 2:
				posY -= 1;
				break;
			case 3:
				posX -= 1;
				break;
			case 4:
				posX += 1;
				break;
			default:
				return;
			}
			
			//Die derzeitige Pixelfarbe herausbekommen
			int RGB = GAME.levellist.elementAt(GAME.levelindex).ReturnMap().GetPixel((int)posX, (int)posY);
			Color c = new Color(RGB);
			
			//Wenn man am Weg angekommen ist
			if(c.getBlue() > 0 ||
				c.getRed() > 0 ||
				c.getGreen() > 0)
			{
				//In die Mitte der Track setzen
				switch(lastMovement)
				{
				case 1:
					posY += 10;
					break;
				case 2:
					posY -= 10;
					break;
				case 3:
					posX -= 10;
					break;
				case 4:
					posX += 10;
					break;
				}
				
				//Die Suche ist beendet
				searching = false;
			}
		}
	}
	//Die Bewegungsfunktion der Gegner, die abhaengig von der vergangenen Zeit ist
	//@Author: Leonhard Kurthen
	//@Edit:	David Katheder [21.04]
	boolean move(double dTime)
	{
		MAP map = GAME.levellist.elementAt(GAME.levelindex).ReturnMap();
		if(posX < map.ReturnWidth() && posX > 0
			&&posY < map.ReturnHeight() && posY > 0)
		{
			//Die Farbe des derzeitigen Pixels herausfinden
			int RGB = map.GetPixel((int)posX, (int)posY);
			Color c = new Color(RGB);
			
			//  { author dk
			// wenn die ENEMYs im ziel sind
			if(c.getRed()==100)
			{
				System.out.println("Gegner am Ziel");
				//Ein Leben abziehen
				PLAYER.GegnerAmZiel();
				System.out.println(PLAYER.lives+"  Leben");
				//Gegner entfernen...
				GAME.levellist.elementAt(GAME.levelindex).removeEnemy(this);
			}
			// }
			//Nach oben bewegen ist wenn: alle Farbwert sind groesser 0 z.B.(255,255,255)
			else if(c.getRed() > 0 && c.getBlue() > 0 && c.getGreen() > 0)
			{
				posY -= dTime*velocity;
				lastMovement = 1;
				//System.out.println("Nach oben");
				//GAME.gui.refresh();
				
			}
			
			//Nach unten bewegen ist wenn: der gruene Farbwert ist ueber 0 z.B.(0,255,0)
			else if(c.getGreen() > 0)
			{
				posY += dTime*velocity;
				lastMovement = 2;
			//	System.out.println("Nach unten");
				//GAME.gui.refresh();
			}
			
			//Nach rechts bewegen ist wenn: der blaue Farbwert ist ueber 0 z.B.(0,0,255)
			else if(c.getBlue() > 0)
			{
				posX += dTime*velocity;
				lastMovement = 3;
			//	System.out.println("Nach rechts");
				//GAME.gui.refresh();
			}
			
			//Nach links bewegen ist wenn: der rote Farbwert ist ueber 0 z.B.(255,0,0)
			else if(c.getRed() > 0)
			{
				posX -= dTime*velocity;
				lastMovement = 4;
				//System.out.println("Nach links");
				//GAME.gui.refresh();
			}
			
			else
				BackToTrack();
		}
		
		//Zu weit oben
		else if(posY < 0 && posX > 0 && posX < map.ReturnWidth())
		{
			//Nach unten bewegen
			posY += dTime*velocity;
			lastMovement = 2;
		}
				
		//Zu weit unten
		else if(posY > map.ReturnHeight() && posX > 0 && posX < map.ReturnWidth())
		{
			//Nach oben bewegen
			posY -= dTime*velocity;
			lastMovement = 1;
		}
				
		//Zu weit links
		else if(posX < 0 && posY > 0 && posY < map.ReturnHeight())
		{
			//Nach rechts bewegen
			posX += dTime*velocity;
			lastMovement = 3;
		}
				
		//Zu weit rechts
		else if(posX > map.ReturnWidth() && posY > 0 && posY < map.ReturnHeight())
		{
			//Nach links bewegen
			posX -= dTime*velocity;
			lastMovement = 4;
		}
				
		//Richtung nicht eindeutig
		else
		{
			return false;
		}
		//Alles lief richtig
		return true;
	}
	
	void Damage(int i)
	{
		/*
		 wenn der Gegner in der Reichweite des Turmes ist, ruft der Turm diese Methode 
		 des jeweiligen Gegners auf und gibt seinen Schaden als Parameter mit, 
		 der dann abgezogen wird.
		 wenn die Leben des Gegners kleiner gleich Null sind wird er aus der Wavelist entfernt.
		 author: david katheder
		 */
		healthpoints = healthpoints-i;
		if(healthpoints <= 0)
		{
			GAME.levellist.elementAt(GAME.levelindex).removeEnemy(this);
			GAME.player.AddPoints(reward);
			GAME.player.AddGold(reward);
		}
	}
	public void SetReward(int value) {
		
		reward=value;
	}
}

