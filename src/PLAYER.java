//Die Klasse PLAYER, die Informationen ueber den momentanen Spieler beinhaltet
public class PLAYER
{
	//Der Geldstand
	int gold;
	//Anzahl Leben
	static int lives;
	//Der Spielername
	String playername;
	//Der Punktestand
	int points;
	//
	int playtime;
	
	//Leerer Konstruktor: Die Werte werden auf 0 gesetzt
	PLAYER()
	{	
		gold = 0;
		lives = 0;
		points = 0;
	}
	
	//Alternativer Konstruktor: Mit Spielername und Anfangswerten
	PLAYER(String Playername)
	{
		gold = 100;
		lives = 20;
		playername = Playername;
		points = 0;
	}
	//GET
	String ReturnName()
	{
		return playername;
	}
	int ReturnGold()
	{
		return gold;
	}
	int ReturnLives()
	{
		return lives;
	}
	int ReturnPoints()
	{
		return points;
	}
	int ReturnPlaytime()
	{
		return playtime;
	}

	//SET
	void SetName(String NewName)
	{
		playername = NewName;
	}
	void SetGold(int NewGold)
	{
		gold = NewGold;
	}
	void SetLives(int NewLives)
	{
		lives = NewLives;
	}
	void AddPoints(int add)
	{
		points += add;
	}
	void AddGold (int add)
	{
		gold+=add;
	}
	void AddTime(int seconds)
	{
		playtime += seconds;
	}
	
	//
	static void GegnerAmZiel()
	{
		lives--;
	}
}
