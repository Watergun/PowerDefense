import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextPane;

public class CONSOLE extends JTextPane
{
	/*
		exit					//Beendet das Spiel
		pause					//Pausiert das Spiel
		continue				//Setzt das Spiel fort
		startwave [n]			//Startet eine Wave mit dem angegebenen Index
		loadwave [name]			//Laed eine Wave aus einer Datei und fuegt sie dem Level hinzu (temporaer)
		loadlevel [name]		//Laed ein Level aus einer Datei und fuegt sie in die Levelliste ein
		get [tower/enemy] [n]	//Gibt den aktuellen Status eines Objektes aus
		watch [tower/enemy] [n]	//Fuehrt 'get' in regelmaessigen Abstaenden aus (1 sek.)
		lib	[tower/enemy] [n]	//Gibt Informtionen ueber ein Objekt aus einer library aus
		resetgame				//Resettet das Spiel
		resetconsole			//Resettet die Konsole
		reset					//Resettet Spiel und Console
		kill [n]				//Toetet einen ENEMY mit dem angegebenen Index
	*/
	private static String content;
	private static int MAX_LENGTH = 1000;
	private static String InitString = "*** Console v.1.0 ***";
	//For the 'watch' command
	private static boolean watching = false;
	private static String watchedobject;
	static double watchtimer = 0;
	
	public CONSOLE()
	{
		content = InitString+"\n>";
		setText(content);
		this.setBackground(Color.black);
		this.setForeground(Color.white);
		this.addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent ke) 
			{
				if(ke.getKeyChar() == 8)
				{
					UpdateContent();
					if(content.charAt(content.length()-1) == '\n')
						UpdateContent(">");
				}
				else if(ke.getKeyChar() == 27)
				{
					watching = false;
					UpdateContent("\n>");
				}
				else if(ke.getKeyChar() == 13 || ke.getKeyChar() == 10)
				{
					//Get what the user typed in
					UpdateContent();
					int begin = content.lastIndexOf('>');
					//System.out.println("BEG:"+begin);
					//System.out.println("LEN:"+content.length());
					if(begin < 0 || begin >= (content.length()-2))
					{
						UpdateContent(">");
						return;
					}
					String entered = content.substring(begin+1);
					if(entered == "" || entered == "\n")
						return;
					entered = entered.substring(0, entered.length()-1);
					//System.out.println(entered);
					//Search for the space
					int space = entered.indexOf(' ');
					String Params = "";
					String Command = "";
					if(space < 0)
						space = entered.length();
					else 
						Params = entered.substring(space+1);
					Command = entered.substring(0, space);
					//System.out.println("CMD:"+Command);
					//System.out.println("PRM:"+Params);
					//Befehl verarbeiten
					InterpretCommand(Command, Params);
					//
					UpdateContent("\n>");
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent arg0) {}
		});
		
	}
	
	//Fuer das 'content'-Attribut
	public void UpdateContent(String Add)
	{
		content = getText();
		content += Add;
		//Nicht zu viel Text
		if(content.length() > MAX_LENGTH)
		{
			int newline = content.indexOf('\n');
			content = content.substring(newline+1);
		}
		setText(content);
	}
	public void UpdateContent()
	{
		content = getText();
	}
	public static boolean IsWatching()
	{
		return watching;
	}
	
	//Das Herzstueck der Konsole
	public void InterpretCommand(String cmd, String params)
	{
		//System.out.println("PRM:\""+params+"\"");
		if(cmd == null || cmd == "")
			return;
		switch(cmd)
		{
		case "exit":
			GAME.running = false;
			break;
			//
		case "pause":
			GAME.waverunning = false;
			break;
			//
		case "continue":
			GAME.waverunning = true;
			break;
			//
		case "startwave":
			if(params == "" || params == null)
			{
				UpdateContent("Expected parameters!");
				return;
			}
			int waveindex = Integer.parseInt(params);
			if(GAME.levellist.elementAt(GAME.levelindex).ReturnWavelist().size() <= waveindex || waveindex < 0)
			{
				UpdateContent("Invalid index!");
				return;
			}
			GAME.levellist.elementAt(GAME.levelindex).SetWave(waveindex);
			GAME.waverunning = true;
			GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().Revive();
			GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().ResetIndices();
			Point startpos = GAME.levellist.elementAt(GAME.levelindex).ReturnEnemyStart();
			GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().SetStartPosition(startpos.x, startpos.y);
			break;
			//
		case "startlevel":
			int levelindex = Integer.parseInt(params);
			if(GAME.levellist.size() >= levelindex || levelindex < 0)
			{
				UpdateContent("Invalid index!");
				return;
			}
			GAME.levelindex = levelindex;
			break;
			//
		case "loadwave":
			if(!GAME.levellist.elementAt(GAME.levelindex).LoadWaveData(params))
			{
				UpdateContent("Loading failed! See other console output for more information.");
				return;
			}
			break;
			//
		case "loadlevel":
			if(!GAME.LoadLevelData(params))
			{
				UpdateContent("Loading failed! See other console for more information!");
				return;
			}
			break;
			//
		case "get":
			int space = params.indexOf(' ');
			if(space < 0)
			{
				UpdateContent("Invalid parameters!");
				return;
			}
			int index = Integer.parseInt(params.substring(space+1));
			if(params.startsWith("enemy"))
			{
				if(index >= GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().ReturnEnemySize() || index < 0)
				{
					UpdateContent("Invalid index!");
					return;
				}
				ENEMY e = GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().IterateEnemygroups(index);
				UpdateContent("\nName: " + e.ReturnName());
				UpdateContent("\nHealth: " + e.ReturnHealth());
				UpdateContent("\nVelocity: " + e.ReturnVelocity());
				UpdateContent("\nPosition: " + e.ReturnPositionX() + ", " + e.ReturnPositionY());
			}
			else if(params.startsWith("tower"))
			{
				if(index >= GAME.towerlist.size() || index < 0)
				{
					UpdateContent("Invalid index!");
					return;
				}
				TOWER t = GAME.towerlist.elementAt(index);
				UpdateContent("\nName: " + t.ReturnName());
				UpdateContent("\nDamage: " + t.ReturnDamage());
				UpdateContent("\nRange: " + t.ReturnRange());
				UpdateContent("\nFirerate: " + t.ReturnFirerate());
				UpdateContent("\nPosition: " + t.ReturnPositionX() + ", " + t.ReturnPositionY());
				GUI.selectedtower = index;
			}
			else
			{
				UpdateContent("Unknown object: "+params);
				return;
			}
			break;
			//
		case "watch":
			space = params.indexOf(' ');
			if(space < 0)
			{
				UpdateContent("Invalid parameters!");
				return;
			}
			index = Integer.parseInt(params.substring(space+1));
			if(index < 0)
			{
				UpdateContent("Invalid index!");
				return;
			}
			//
			watchedobject = params;
			watching = true;
			break;
			//
		case "set":
			space = params.indexOf(' ');
			if(space < 0)
			{
				UpdateContent("Invalid parameters!");
				return;
			}
			String value = params.substring(space+1);
			if(params.startsWith("playername"))
				GAME.player.SetName(value);
			else if(params.startsWith("playergold"))
				GAME.player.SetGold(Integer.parseInt(value));
			else if(params.startsWith("playerlives"))
				GAME.player.SetLives(Integer.parseInt(value));
			//else if(params.startsWith("..."))
			//...
			break;
			//
		case "lib":
			space = params.indexOf(' ');
			if(space < 0)
			{
				UpdateContent("Invalid paramters!");
				return;
			}
			index = Integer.parseInt(params.substring(space+1));
			if(params.startsWith("enemy"))
			{
				if(index < 0 || index >= WAVE.enemylibrary.size())
				{
					UpdateContent("Invalid index!");
					return;
				}
				ENEMY e = WAVE.enemylibrary.elementAt(index);
				UpdateContent("\nName: " + e.ReturnName());
				UpdateContent("\nHealth: " + e.ReturnHealth());
				UpdateContent("\nVelocity: " + e.ReturnVelocity());
			}
			else if(params.startsWith("tower"))
			{
				if(index < 0 || index >= SHOP.towerlibrary.size())
				{
					UpdateContent("Invalid index!");
					return;
				}
				TOWER t = SHOP.towerlibrary.elementAt(index);
				UpdateContent("\nName: " + t.ReturnName());
				UpdateContent("\nDamage: " + t.ReturnDamage());
				UpdateContent("\nRange: " + t.ReturnRange());
				UpdateContent("\nFirerate: " + t.ReturnFirerate());
				UPGRADE[] upgrades = t.ReturnUpgrades();
				for(int i = 0; i < 3; i++)
				{
					UpdateContent("\nUpgrade " + (i+1) +":\nD:" + upgrades[i].ReturnDamage() +
							  " R:" + upgrades[i].ReturnRange() + 
							  " FR:" + upgrades[i].ReturnFirerate());
				}
			}
			else
			{
				UpdateContent("Unknown object: " + params);
				return;
			}
			break;
			//
		case "resetgame":
			UpdateContent("#Resetting Game#");
			GAME.levelindex = 0;
			for(int i = 0; i < GAME.levellist.elementAt(GAME.levelindex).ReturnWavelist().size(); i++)
			{
				GAME.levellist.elementAt(GAME.levelindex).SetWave(i);
				GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().Revive();
				startpos = GAME.levellist.elementAt(GAME.levelindex).ReturnEnemyStart();
				GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().SetStartPosition(startpos.x,  startpos.y);
				GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().ResetIndices();
			}
			break;
			//
		case "resetconsole":
			setText(InitString);
			UpdateContent();
			break;
		case "reset":
			InterpretCommand("resetgame","");
			InterpretCommand("resetconsole", "");
			break;
		case "kill":
			if(params == "")
			{
				UpdateContent("Expected parameters!");
				return;
			}
			index = Integer.parseInt(params);
			if(index < 0 || index > GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().ReturnEnemySize())
			{
				UpdateContent("Invalid index!");
				return;
			}
			GAME.levellist.elementAt(GAME.levelindex).removeEnemy(
					GAME.levellist.elementAt(GAME.levelindex).ReturnCurrentWave().IterateEnemygroups(index)
																);
			break;
		default:
			UpdateContent("Unknown Command: \""+cmd+"\"");
			//UpdateContent("\n>");
		}
		//
		return;
	}
	
	//Laesst einfach nur bis zu einer bestimmten Zeit laufen 
	public void Watch(double dTime)
	{
		watchtimer += dTime;
		if(watchtimer >= 1)
		{
			InterpretCommand("get", watchedobject);
			UpdateContent("------\n");
			watchtimer = 0;
		}
	}
}
