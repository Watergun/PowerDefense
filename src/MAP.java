import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.Color;

import javax.imageio.ImageIO;

public class MAP
{
	//Die Spielfeldbreite
	private int 	width;
	//Die Spielfeldhoehe
	private int 	height;
	//
	private BufferedImage map;
	//
	private BufferedImage background;
	
	// Feld das �ber der Map liegt 
	// wenn �ber dem feld ein weg liegt ist der wert -2 (-> es kann kein turm eingespeichert werden)
	// wenn in dem feld ein Tower ist ist der wert der index des towers in der towerlist
	// wenn das feld leer ist ist der wert -1
	int[][] mapfield;
	
	MAP()
	{
		width = 0;
		height = 0;
		mapfield=null;
	}
	
	//GET Methoden
	int ReturnHeight()
	{
		return height;
	}
	int ReturnWidth()
	{
		return width;
	}
	BufferedImage ReturnMap()
	{
		return map;
	}
	BufferedImage ReturnBackground()
	{
		return background;
	}
	int GetPixel(int x, int y)
	{
		if(map == null || x < 0 || y < 0 || x > map.getWidth() || y > map.getHeight())
			return 0;
		return map.getRGB(x, y);
	}
	
	//Die Bewegungsmap (das was man nicht sieht) wird geladen
	boolean LoadMap(String Filename)
	{
		try
		{
			map = ImageIO.read(new File(Filename));
			if(map == null)
				throw(new Exception("Error: Die Datei \"" + Filename + "\" existiert nicht!"));
			
			height = map.getHeight();
			width = map.getWidth();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
		//{ author: dk
		int width=map.getWidth();
		int height=map.getHeight();
		// anzahl der felder anhand gr��e des bildes
		mapfield=new int[width/20][height/20];
				
		for(int u=0;u<width/20;u++)
		{
			for(int z=0;z<height/20;z++)
			{
				// setzen aller felder auf "leer"
				mapfield[u][z]=-1;
			}
		}
		for(int i = 0; i < width; i = i+20)
		{
			for(int o = 0; o < height; o = o+20)
			{	
				int RGB = map.getRGB(i, o);
				Color c = new Color(RGB);
				if(c.getBlue()>0 || c.getRed()>0 || c.getGreen()>0 )
				{
					// setzen der felder �ber denen ein Weg verl�uft auf "besetzt"
					mapfield[i/20][o/20]=-2;
				}
			}
		}
		return true;
	}
	
	//Der Hintergrund der Map (das was man sieht) wird geladen
	boolean LoadBackground(String Filename)
	{
		try
		{
			background = ImageIO.read(new File(Filename));
			if(background == null)
				throw(new Exception("Error: Die Datei \"" + Filename + "\" existiert nicht!"));
			return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
}

