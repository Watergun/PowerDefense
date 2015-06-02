import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class UPGRADE 
{
	private int firerate;
	private int range;
	private int damage;
	private int cost;
	private BufferedImage towerlogo;
	private BufferedImage shotimage;
	
	UPGRADE(String unsplitted)
	{
		//RANGE FIRERATE DAMAGE COST IMAGE SHOTIMAGE
		try
		{
			String[] args = unsplitted.split(" ");
			if(args.length < 6)
				throw new Exception("ERROR: Upgrade has not enough arguments: " + unsplitted);
			range = Integer.parseInt(args[0]);
			firerate = Integer.parseInt(args[1]);
			damage = Integer.parseInt(args[2]);
			cost = Integer.parseInt(args[3]);
			towerlogo = ImageIO.read(new File("images/"+args[4]));
			shotimage = ImageIO.read(new File("images/"+args[5]));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}
	
	UPGRADE(int NewRange, int NewFirerate, int NewDamage, int NewCost, String towerpath, String shotpath)
	{
		try
		{
			towerlogo = ImageIO.read(new File("images/" + towerpath));
			if(towerlogo == null)
				throw new Exception("Error: The file: \""+towerpath +"\" does not exist!");
			shotimage = ImageIO.read(new File("images/" + shotpath));
			if(shotimage == null)
				throw new Exception("Error: The file: \""+shotpath+"\" does not exist!");
			firerate = NewFirerate;
			range = NewRange;
			damage = NewDamage;
			cost = NewCost;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}
	
	//GET
	int ReturnFirerate()
	{
		return firerate;
	}
	int ReturnDamage()
	{
		return damage;
	}
	int ReturnRange()
	{
		return range;
	}
	int ReturnCost()
	{
		return cost;
	}
	BufferedImage ReturnTowerlogo()
	{
		return towerlogo;
	}
	BufferedImage ReturnShotimage()
	{
		return shotimage;
	}
}
