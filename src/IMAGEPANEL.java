import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

class IMAGEPANEL extends JPanel
{
	private BufferedImage image;
	private Point mouseposition;     
	
	public IMAGEPANEL()
	{
	    if(GAME.levelindex > -1 && GAME.levelindex < GAME.levellist.size())
	    {
	    	image = GAME.levellist.elementAt(GAME.levelindex).ReturnBackground();
		
			Dimension size = new Dimension(image.getWidth(), image.getHeight());
	  		setPreferredSize(size);
	    	setLayout(null);
	    }
	    
	    mouseposition = new Point(0,0);
	    
	    addMouseMotionListener(new MouseMotionListener()
	    {

			@Override
			public void mouseDragged(MouseEvent e)
			{}

			@Override
			public void mouseMoved(MouseEvent e) 
			{
				mouseposition = e.getPoint();
			}
	    });
	    addMouseListener(new MouseListener()
	    {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(GAME.gui.ReturnActiveTower()!=-1)
				{
					GAME.AddTower(e.getX(), e.getY(), GAME.gui.ReturnActiveTower());
				}
				else
				{
					int map_x = e.getX() / 20;
					int map_y = e.getY() / 20;
					if(map_x < 0 || map_x >= (image.getWidth()/20) ||
						map_y < 0 || map_y >= (image.getHeight()/20))
					{
						//Wenn ausserhalb der map geklickt wurde
						return;
					}
					if(GAME.levellist.elementAt(GAME.levelindex).ReturnMap().mapfield[map_x][map_y] > -1)
						//Dieser TOWER wird selektiert
						GUI.selectedtower = GAME.levellist.elementAt(GAME.levelindex).ReturnMap().mapfield[map_x][map_y];
					else
						//Der aktive TOWER wird deselektiert
						GUI.selectedtower = -1;		
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

	    }) ;
	    
	}
	      
	public void paintComponent(Graphics g) 
	{	
		//Die Zeichenflaeche leeren
		g.clearRect(0, 0, image.getWidth(), image.getHeight());
		
		//Das Hintergrundbild
		g.drawImage(image, 0, 0, null);
		
		//Die aktive WAVE (wenn es denn eine gibt)
		WAVE wave = GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave();
		if(wave != null)
		{
			
			//Jeden ENEMY rendern
			for(int i = 0; i < wave.ReturnEnemySize(); i++)
			{
				ENEMY e = wave.IterateEnemygroups(i);
				//Das Bild malen
				if(e.isAlive())
				{
					Image image = e.ReturnImage();
					g.drawImage(image, 
							e.ReturnPositionX() - (image.getWidth(null)/2),
							e.ReturnPositionY() - (image.getHeight(null)/2),
							null);
			
					//Der Gesundheitsbalken
					//Die Farbe soll sich von gruen ueber gelb zu rot aendern
					//ROT:  RGB(1,0,0)
					//GELB: RGB(1,1,0)
					//GRUEN:RGB(0,1,0)
					//Der Farbverlauf kann durch Funktionen beschrieben werden:
					//R(x) = m*x + t
					//x ist health
					//m ist -(2/inithealth)
					//t ist 2
					//Deshalb: R(health) = -2/inithealth * health + 2
					//Weil Werte ueber 1 rauskommen, muss nachkorrigiert werden
					float health = (float)e.ReturnHealth();
					float inithealth = (float)e.ReturnInitialHealth();
					int R = (int)(((-2/inithealth)*health + 2) * 255);
					if(R > 255)
						R = 255;
					//G(health) = 2/inithealth * health + 0
					int G = (int)((2/inithealth)*health * 255);
					if(G > 255)
						G = 255;
					else if(G < 0)
						G = 0;
					
					//Der B (Blau) Wert ist immer null
					Color healthbar = new Color(R,G,0);
			
					//Das Rechteck soll doppelt so breit sein wie der ENEMY
					//Und 3 Pixel darueber platziert sein
					//Die Breite des Rechtecks ist abhaengig von der Health des ENEMYs
					int x = e.ReturnPositionX() - (image.getWidth(null));
					int y = e.ReturnPositionY() - (image.getHeight(null)/2) - 3;
					int width = (int)(image.getWidth(null)*2);
					int height = 3;
					g.setColor(new Color(R,G,0));
					g.setColor(Color.black);
					g.fillRoundRect(x-1,y-1,width+2,height+2, 1, 1);
					g.setColor(healthbar);
					g.fillRect(x,y, (int)(width*(health/inithealth)), height);
				}
			}
		}
		
		for(int i= 0; i<GAME.animationlist.size();i++)
		{
			g.drawImage(GAME.animationlist.elementAt(i).ReturnImage(),
					    GAME.animationlist.elementAt(i).ReturnPositionX(),
					    GAME.animationlist.elementAt(i).ReturnPositionY(),
					    null);
		}
		//Alle TOWERs rendern
		for(int i = 0; i < GAME.towerlist.size(); i++)
		{
			g.drawImage(GAME.towerlist.elementAt(i).ReturnImage(),
						GAME.towerlist.elementAt(i).ReturnPositionX(),
						GAME.towerlist.elementAt(i).ReturnPositionY(),
						null);
		}

		//Den aktivierten TOWER rendern (Falls es einen gibt)
		if(GUI.activetower != -1)
			DrawActiveTower(g);
		
		//Den selektierten TOWER rendern (Falls es einen gibt)
		if(GUI.selectedtower != -1)
			DrawSelectedTower(g);
		
		g.dispose();
	}
	
	//Zeichnet den TOWER, der aus dem SHOP ausgewaehlt wurde
	void DrawActiveTower(Graphics g)
	{
		if(GUI.activetower >= SHOP.towerlibrary.size() || GUI.activetower == -1)
			return;
		if(mouseposition.x > image.getWidth() || mouseposition.y > image.getHeight())
			return;
		
	    int x = (mouseposition.x-1) / 20;
		x *= 20;
		int y = (mouseposition.y-1) / 20;
		y *= 20;
		g.drawImage(SHOP.towerlibrary.elementAt(GUI.activetower).ReturnImage(),
					x,
					y,
					null);
		
		g.setColor(Color.white);
		g.drawOval((x+10) - SHOP.towerlibrary.elementAt(GAME.gui.ReturnActiveTower()).ReturnRange(),
	    		(y+10) - SHOP.towerlibrary.elementAt(GAME.gui.ReturnActiveTower()).ReturnRange(),
	    		2*SHOP.towerlibrary.elementAt(GAME.gui.ReturnActiveTower()).ReturnRange(),
	    		2*SHOP.towerlibrary.elementAt(GAME.gui.ReturnActiveTower()).ReturnRange());
	}
	
	//Zeichnet den TOWER, der auf der MAP mit der Maus angeklickt wurde
	void DrawSelectedTower(Graphics g)
	{
		if(GUI.selectedtower == -1)
			return;
		if(GUI.selectedtower >= GAME.towerlist.size())
			return;
		
		int x = GAME.towerlist.elementAt(GUI.selectedtower).ReturnPositionX();
		int y = GAME.towerlist.elementAt(GUI.selectedtower).ReturnPositionY();
		int range = GAME.towerlist.elementAt(GUI.selectedtower).ReturnRange();
		
		g.setColor(Color.white);
		g.drawOval((x+10) - range,
	    		(y+10) - range,
	    		2*range,
	    		2*range);
	}
}
