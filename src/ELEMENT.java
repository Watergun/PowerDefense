import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class ELEMENT {

	static double[][] elementmatrix;
	
	public ELEMENT() 
	{
		elementmatrix = new double[5][5];
		SetMatrix();
		PrintMatrix();
	}
	
	void PrintMatrix()
	{
		System.out.println("Elementmatrix:");
		System.out.print("   ");
		
		for(int i = 0; i < 5; i++)
			System.out.print((i+1) + "    ");
		System.out.println("");
		for(int i = 0; i < 5; i++)
		{
			System.out.print((i+1) + "  ");
			for(int k = 0; k < 5; k++)
				System.out.print(elementmatrix[i][k] + " ");
			System.out.println("");
		}
	}
	
	void SetMatrix()
	{	
		FileReader fr = null;
		try 
		{
			fr = new FileReader("element.txt");
		} 
		catch (FileNotFoundException e) 
		{	
			System.out.println("ERROR: Element Datei ist nicht vorhanden!");
		}
         BufferedReader br = new BufferedReader(fr);
         String line = "";

         
        try
        {
        	for(int i = 0; i < 5; i++)
        	{
        		line = br.readLine() ;
        		// char[] linearray = new char[line.length()];
        		//linearray = line.toCharArray();
        		String[] numbers = line.split(" ");
        		System.out.println(numbers.length);
        		for(int o = 0; o < 5; o++)
        		{
        			elementmatrix[i][o] = Double.parseDouble(numbers[o]);
        		}
        	}
        	br.close();
        }
        catch (IOException e)
        {
        	e.printStackTrace();
        }
	}
}