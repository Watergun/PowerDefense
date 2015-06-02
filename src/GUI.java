import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class GUI extends JFrame  {

	static int activetower = -1;
	static int selectedtower = -1;
	JButton Startbutton;
	JLabel[] playerlabels;
	IMAGEPANEL imagepanel;
	CONSOLE console;
	JTable highscore;
	JLabel[] towerlabels;
	JButton UpgradeButton;
	GUI()
	{	
		super("PowerDefense");
		setLayout(new BorderLayout());
		imagepanel= new IMAGEPANEL();
		Container container=new Container() ;
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		PLAYER[] playerarray = null;
		if(GAME.online)
		{
			playerarray = GAME.sql.GetHighscorelist();
			String[] columnNames = {"Name","Points", "Playingtime"};
			String[][] rowData = new String[playerarray.length][3];
			for(int i = 0; i < playerarray.length;i++)
			{
				rowData[i][0] = playerarray[i].ReturnName();
				rowData[i][1] = Integer.toString(playerarray[i].ReturnPoints());
				rowData[i][2] = Integer.toString(playerarray[i].ReturnPlaytime());
			}
			highscore = new JTable(rowData, columnNames);
			highscore.setAutoCreateRowSorter(true);
			highscore.setDragEnabled(false);
			highscore.setEnabled(false);
			
			JButton HighscoreButton = new JButton("HIGHSCORE");
			HighscoreButton.setPreferredSize(new Dimension(150, 50));
			HighscoreButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					JDialog d = new JDialog(GAME.gui,"Highscore",true);
					JScrollPane JSP = new JScrollPane(highscore);
			
					d.add(JSP);
					d.setDefaultCloseOperation(HIDE_ON_CLOSE);
					d.pack();
					d.setVisible(true);
				}
			
			});
			container.add(HighscoreButton);
		}
	    setVisible(true);
		for(int i=0;i<SHOP.towerlibrary.size();i++)
		{
		//JButton towerbutton= new JButton(String.valueOf(i),new ImageIcon(SHOP.towerlibrary.elementAt(i).ReturnImage()));
		JButton towerbutton = new JButton(new ImageIcon(SHOP.towerlibrary.elementAt(i).ReturnImage()));
		towerbutton.putClientProperty("IDENTIFIER", i);
		towerbutton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(activetower != -1)
				{
					ResetActiveTower();
					return;	
				}
				
				JButton b=(JButton) e.getSource();
				//activetower=Integer.parseInt(b.getText());
				activetower = (int)b.getClientProperty("IDENTIFIER");
			}
		});
		
		container.add(towerbutton);
		}
		
		add(container,BorderLayout.SOUTH);
		
		System.out.println("[GUI] TOWERS CREATED !");
		//Der Button der die WAVE starten soll
		Startbutton = new JButton("Start Wave");
		Startbutton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Startbutton.setEnabled(false);
				GAME.waverunning = true;
				GAME.levellist.elementAt(GAME.levelindex).StartNewWave();
			}
		});
		Startbutton.setPreferredSize(new Dimension(160, 50));
		Container east = new Container();
		east.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		east.add(Startbutton, gbc);
		console = new CONSOLE();
		//con.setPreferredSize(new Dimension(160, 450));
		JScrollPane scroll = new JScrollPane(console);
		scroll.setPreferredSize(new Dimension(160, 450));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		gbc.gridy = 1;
		gbc.gridx = 0;
		east.add(scroll, gbc);
		add(east,BorderLayout.EAST);		
		System.out.println("[GUI] CONSOLE CREATED !");
		
		add(imagepanel,BorderLayout.CENTER);
		
		playerlabels = new JLabel[9];
		playerlabels[0] = new JLabel("SPIELER: " + GAME.player.ReturnName());
		playerlabels[1] = new JLabel("GOLD: " + GAME.player.ReturnGold());
		playerlabels[2] = new JLabel("LEBEN: " + GAME.player.ReturnLives());
		playerlabels[3] = new JLabel("PUNKTE: "+ GAME.player.ReturnPoints());
		playerlabels[4] = new JLabel("Ausgewaehlter Tower: ");
		playerlabels[5] = new JLabel();
		playerlabels[6] = new JLabel();
		playerlabels[7] = new JLabel();
		playerlabels[8] = new JLabel();

		
		UpgradeButton = new JButton();
		UpgradeButton.setVisible(false);
		UpgradeButton.setPreferredSize(new Dimension(150, 50));
		UpgradeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if(selectedtower>-1)
				{
					GAME.towerlist.elementAt(selectedtower).TowerUpgrade();
				}
				else
				{
					System.out.print("Bitte Tower auswaehlen");
				}
			}
		});
		Container c = new Container();
		c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
		c.setPreferredSize(new Dimension(180, 400));
		for(int i = 0; i < 9; i++)
			c.add(playerlabels[i]);
		c.add(UpgradeButton);
		
		towerlabels = new JLabel[5];
		for(int i = 0; i<5; i++)
		{
			towerlabels[i] = new JLabel();
			c.add(towerlabels[i]);
		}
		
		
		add(c,BorderLayout.WEST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pack();
		setResizable(true);
		
		System.out.println("[GAME] GUI CREATED !");
		
		this.addWindowListener(new WindowListener()
		{
			public void windowClosing(WindowEvent e)
			{
				GAME.running = false;
			}
			public void windowOpened(WindowEvent e)
			{
				refresh();
			}
			public void windowClosed(WindowEvent e)
			{
				GAME.running = false;
			}
			public void windowActivated(WindowEvent e)
			{
				
			}
			public void windowDeactivated(WindowEvent e)
			{}
			public void windowIconified(WindowEvent e)
			{
				GAME.waverunning = false;
			}
			public void windowDeiconified(WindowEvent e)
			{
			}
		});
	}
			
		void refresh()
		{
			playerlabels[0].setText("NAME: " + GAME.player.ReturnName());
			playerlabels[1].setText("GOLD: " + GAME.player.ReturnGold());
			playerlabels[2].setText("LEBEN: " +GAME.player.ReturnLives());
			playerlabels[3].setText("PUNKTE: "+ GAME.player.ReturnPoints());
			if(selectedtower > -1)
			{
				//System.out.println(selectedtower);
				TOWER t = GAME.towerlist.elementAt(selectedtower);
				UPGRADE[] upg = t.ReturnUpgrades();
				playerlabels[4].setVisible(true);
				playerlabels[5].setText("Name: " + t.ReturnName());
				playerlabels[6].setText("Range: " + t.ReturnRange());
				playerlabels[7].setText("Firerate: " + t.ReturnFirerate());
				playerlabels[8].setText("Damage: " + t.ReturnDamage());
				if(t.upgradeindex<2)
				{UpgradeButton.setText("Upgrade: "+upg[t.upgradeindex+1].ReturnCost() + " Gold");
				UpgradeButton.setVisible(true);
				}else
				UpgradeButton.setVisible(false);
				
				
			}
			else
			{
				playerlabels[4].setVisible(false);
				playerlabels[5].setText("");
				playerlabels[6].setText("");
				playerlabels[7].setText("");
				playerlabels[8].setText("");
				UpgradeButton.setVisible(false);
			}
			if(activetower>-1)
			{
				TOWER t = SHOP.towerlibrary.elementAt(activetower);
				towerlabels[0].setText("Name: " + t.ReturnName());
				towerlabels[1].setText("Range: " + t.ReturnRange());
				towerlabels[2].setText("Firerate: " + t.ReturnFirerate());
				towerlabels[3].setText("Damage: " + t.ReturnDamage());
				towerlabels[4].setText("Preis: " + t.ReturnCost());
				
			}
			else
			{
				towerlabels[0].setText("");
				towerlabels[1].setText("");
				towerlabels[2].setText("");
				towerlabels[3].setText("");
				towerlabels[4].setText("");
			}
			repaint();
		}

		int ReturnActiveTower()
		{ 
			return activetower;
		}

		void ResetActiveTower()
		{ 
			 activetower=-1;
		}
	
		int ReturnSelectedTower()
		{
			return selectedtower;
		}

}
