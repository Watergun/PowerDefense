import java.awt.image.BufferedImage;

public class TOWER
{
	//Die Koordinaten des Turmes (Die Mitte des Turmes)
	private int posX;
	private int posY;
	//Kontrollvariable
	boolean running;
	// Reichweite des Turmes
	private int range;
	// Schaden des Turmes
	private int damage;
	// Feuerrate
	private int firerate;
	//Bezeichner
	public String name;
	int upgradeindex=-1;
	//
	private BufferedImage image;
	private BufferedImage shotimage;
	private double shootingtimer = 1.0d;
	//Element
	private int element;
	private UPGRADE[] upgrades;
	//Preis
	private int cost;
	
	public TOWER()
	{
		posX = 0;
		posY = 0;
		range = 0;
		damage = 0;
		running = true;
		firerate = 0; 
		upgrades= new UPGRADE[3];
		shotimage = null;
		cost = 0;
	}
	
	public TOWER(int positionX, int positionY)
	{	
		posX	=	positionX;
		posY	=	positionY;
		range	=	200;
		running	=	true;
		damage	=	100;
		firerate=	1;
		upgrades= new UPGRADE[3];
		shotimage = null;
		cost = 0;
	}
	public TOWER(int positionX, int positionY,BufferedImage img)
	{	
		posX	=	positionX;
		posY	=	positionY;
		range	=	200;
		running	=	true;
		damage	=	100;
		firerate=	1;
		image=img;
		upgrades= new UPGRADE[3];
		shotimage = null;
		cost = 0;
	}
	public TOWER(int positionX, int positionY, TOWER template)
	{
		posX = positionX;
		posY = positionY;
		name = template.ReturnName();
		range = template.ReturnRange();
		running = true;
		damage = template.ReturnDamage();
		firerate = template.ReturnFirerate();
		image = template.ReturnImage();
		upgrades= new UPGRADE[3];
		upgrades=template.ReturnUpgrades();
		element = template.ReturnElement();
		cost = template.ReturnCost();
	}

	//SET-Methoden
	void SetRange(int NewRange)
	{
		range = NewRange;
	}
	void SetDamage(int NewDamage)
	{
		damage = NewDamage;
	}
	void SetFirerate(int NewFirerate)
	{
		firerate = NewFirerate;
	}
	void SetElement(int NewElement)
	{
		element = NewElement;
	}
	void SetPosition(int NewPosX, int NewPosY)
	{
		posX = NewPosX;
		posY = NewPosY;
	}
	void SetName(String NewName)
	{
		name = NewName;
	}
	void SetUpgrade(int index, UPGRADE up)
	{
		 upgrades[index] = up;
	}
	void SetCost(int NewCost)
	{
		cost = NewCost;
	}
	
	//GET-Methoden
	int ReturnPositionX()
	{
		return posX;
	}
	int ReturnPositionY()
	{
		return posY;
	}
	void SetImage(BufferedImage NewImage)
	{
		image = NewImage;
	}
	void SetShotimage(BufferedImage NewImage)
	{
		shotimage = NewImage;
	}
	BufferedImage ReturnImage()
	{
		return image;
	}
	int ReturnRange()
	{
		return range;
	}
	int ReturnFirerate()
	{
		return firerate;
	}
	int ReturnDamage()
	{
		return damage;
	}
	int ReturnElement()
	{
		return element;
	}
	String ReturnName()
	{
		return name;
	}
	UPGRADE[] ReturnUpgrades()
	{
		return upgrades;
	}
	int ReturnCost()
	{
		return cost;
	}
	
	//Upgrade
	void TowerUpgrade()
	{
		if(upgradeindex<2)
		{	
			if(GAME.player.gold>=upgrades[upgradeindex+1].ReturnCost())
			{
			UPGRADE currentUpgrade= upgrades[upgradeindex+1];
			range=currentUpgrade.ReturnRange();
			damage=currentUpgrade.ReturnDamage();
			firerate=currentUpgrade.ReturnFirerate();
			shotimage=currentUpgrade.ReturnShotimage();
			image=currentUpgrade.ReturnTowerlogo();
			GAME.player.gold-=upgrades[upgradeindex+1].ReturnCost();
			upgradeindex++;
			}
			else
			{
				System.out.println("Du hast nicht genügend Gold! Dir fehlen noch "+( upgrades[upgradeindex+1].ReturnCost()-GAME.player.gold) +" Goldstücke");
			}
		}
		else{
			System.out.print("Der Turm ist schon maximal ausgebaut!");
		}
	}
	//SchieÃŸmethode
	void Shoot(double dTime)
	{
		/* die wave list wird druchlaufen 
		 die Differenz der Korrdinaten des Turmes und des Gegners wird als double gespeichert.
		 mithilfe des Pythagoras ( Methode Math.hypot(double, double)) wird die Entfernung zwischen 
		 beiden berechnet.
		 wenn diese kleiner gleich der Reichweite ist, ruft der Turm die Methode Damage(int i) des Feindes auf
		 und gibt seinen Damagewert mit 
		 wenn die enemylist leer ist wird running auf false gesetzt 
		 author: david katheder
		 */
		
		//Der Timer, der den Abstand zwischen den Schuessen steuert
		shootingtimer += dTime;
		//firerate soll die Anzahl Schuesse pro Minute angeben
		if(shootingtimer < (60.0d/(double)firerate))
		{
			//Dieser TOWER muss noch laenger warten
			return;
		}
		//Wenn die Zeit reif ist, darf geschossen werden
		//(und der timer muss zurueckgesetzt werden)
		
		if(GAME.levellist.elementAt(GAME.levelindex).isCurrentWaveEmpty())
		{
			return;
		}
		
		WAVE w = GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave();
			
		for(int i = 0; i< w.ReturnEnemySize(); i++)
		{
			ENEMY e = w.IterateEnemygroups(i);
			if(e.ReturnPositionX() >= 0 && e.ReturnPositionY() >= 0 && e.isAlive())
			{
				//Die X- und Y-Differenz
				//Die +10 wird verwendet, damit von der Mitte des TOWERs aus berechnet wird
				double posXe = e.ReturnPositionX()-(posX+10);
				double posYe = e.ReturnPositionY()-(posY+10);
			
				//Wenn Gegner in Reichweite des Turmes
				if(Math.hypot(posXe, posYe) <= range)
				{	
					GAME.animationlist.add(new ANIMATION(e,this));
					//Dann wird dem ersten Gegner Schaden zugefuegt
					
					//numberY--;	
					shootingtimer = 0;
					break;
				}	
			}
		}
	}
}
		
	

		
	

