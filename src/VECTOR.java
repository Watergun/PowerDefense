//Stellt einen einfachen Vector dar
public class VECTOR 
{
	private double dx;
	private double dy;
	
	//CONSTRUCTOR
	//Standart
	public VECTOR()
	{
		dx = 0;
		dy = 0;
	}
	//Bei zwei vordefinierten Werten
	public VECTOR(double x, double y)
	{
		dx = x;
		dy = y;
	}
	//Bei zwei Punkten
	public VECTOR(double x1, double y1, double x2, double y2)
	{
		dx = x2-x1;
		dy = y2-y1;
	}
	//GET
	public double ReturnX()
	{
		return dx;
	}
	public double ReturnY()
	{
		return dy;
	}
	//SET
	public void SetXY(double NewX, double NewY)
	{
		dx = NewX;
		dy = NewY;
	}
	
	//OPERATOREN
	public VECTOR Add(VECTOR vec)
	{
		return new VECTOR(dx + vec.ReturnX(), dy + vec.ReturnY());
	}
	
	public VECTOR Sub(VECTOR vec)
	{
		return new VECTOR(dx - vec.ReturnX(), dy - vec.ReturnY());
	}
	
	public VECTOR Mul(double factor)
	{
		return new VECTOR(dx * factor, dy * factor);
	}
	
	public VECTOR Div(double factor)
	{
		return new VECTOR(dx / factor, dy / factor);
	}
	
	public double Scale(VECTOR vec)
	{
		return (dx*vec.ReturnX() + dy*vec.ReturnY());
	}
	
	//Normalisieren (Laenge 1)
	public void Normalize()
	{
		double length = Math.sqrt(dx*dx + dy*dy);
		dx /= length;
		dy /= length;
	}
}
