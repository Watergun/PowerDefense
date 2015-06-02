import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class ANIMATION {
	double posX,posY;
	VECTOR vector;
	ENEMY aim;
	TOWER tower;
	private BufferedImage image;
	double velocity;
	
	ANIMATION(ENEMY e,TOWER t)
	{	
		tower=t;
		posX=t.ReturnPositionX();
		posY=t.ReturnPositionY();
		aim=e;
		vector=new VECTOR(t.ReturnPositionX(),t.ReturnPositionY(),aim.ReturnPositionX(),aim.ReturnPositionY());
		
		try {
			image=ImageIO.read(new File("images/bullet_black.gif"));
		} catch (IOException ex) {
			
			ex.printStackTrace();
		}
		velocity = 300;
	}
	
	void MoveShot(double dTime)
	{
		double tolerance = 4;
		if((posX <= (aim.ReturnPositionX()+tolerance) && posX >= (aim.ReturnPositionX()-tolerance)
		&&  posY <= (aim.ReturnPositionY()+tolerance) && posY >= (aim.ReturnPositionY()-tolerance)))
		{
			//Den Schaden berechnen in Abhaengigkeit des Elements
			int damage = (int) ((double)tower.ReturnDamage() * ELEMENT.elementmatrix[tower.ReturnElement()-1][aim.ReturnElement()-1]);
			aim.Damage(damage);
			GAME.animationlist.remove(this);
			return;
		}
		//Der Schuss ist ausserhalb der Map (Kann auftreten, wenn aim zerstoert wurde
		else if(posX < 0 || posX > GAME.levellist.elementAt(GAME.levelindex).ReturnBackground().getWidth()
			 || posY < 0 || posY > GAME.levellist.elementAt(GAME.levelindex).ReturnBackground().getHeight())
		{
			GAME.animationlist.remove(this);
			if(GAME.levellist.elementAt(GAME.levelindex).isCurrentWaveEmpty() && GAME.animationlist.size() == 0)
			{
				GAME.waverunning = false;
				GAME.gui.Startbutton.setEnabled(true);
				System.out.println("# Wave zu Ende #");
			}
			return;
		}
		//Wenn das Ziel noch am Leben ist, muss der Vektor neu berechnet werden
		if(aim.isAlive())
		{
			vector = new VECTOR(posX,posY,aim.ReturnPositionX(),aim.ReturnPositionY());
			vector.Normalize();
		}
		posX += vector.ReturnX()*velocity*dTime;
		posY += vector.ReturnY()*velocity*dTime;	
	}
	
	public BufferedImage ReturnImage() {
	
		return image;
	}

	public int ReturnPositionY() {
		
		return (int)posY;
	}

	public int ReturnPositionX() {
		
		return (int) posX;
	}
}
