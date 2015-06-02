import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Vector;

public class HTTP 
{
	private static URL address;
	private boolean newerversion;

	HTTP()
	{
		address = null;
		newerversion = false;
	}
	
	public boolean DoesNewerVersionExist()
	{
		return newerversion;
	}
	
	public void CheckUpdateList()
	{
		System.out.println("[GAME] CHECKING FOR NEWER VERSIONS... ");
		try
		{
			address = new URL("http://" + GAME.Servername + "/updatelist.txt");
			ReadableByteChannel rbc = Channels.newChannel(address.openStream());
			FileOutputStream fos = new FileOutputStream("updatelist.txt");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			//
			BufferedReader br = new BufferedReader(new FileReader("updatelist.txt"));
			String versionline = br.readLine();
			int space = versionline.indexOf(" ");
			double onlineversion = Double.parseDouble(versionline.substring(space+1));
			br.close();
			//
			File f = new File("currentversion.txt");
			if(!f.exists())
			{
				newerversion = true;
			}
			//
			br = new BufferedReader(new FileReader("currentversion.txt"));
			versionline = br.readLine();
			space = versionline.indexOf(" ");
			double currentversion = Double.parseDouble(versionline.substring(space+1));
			br.close();
			//
			//
			if(onlineversion > currentversion)
				newerversion = true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return;
		}
	}
	
	public void UpdateFiles()
	{
		if(!newerversion)
		{
			System.out.println("[GAME] IS ALREADY NEWEST VERSION !");
			return;
		}
		System.out.println("[GAME] NEWER VERSION WAS FOUND !");
		try
		{
			System.out.println("[GAME] UPDATING TO NEWER VERSION...");
			Vector<String> lines_v = new Vector<String>();
			BufferedReader br = new BufferedReader(new FileReader("updatelist.txt"));
			String line = null;
			while((line = br.readLine()) != null)
				lines_v.add(line);
			br.close();
			String[] lines = new String[lines_v.size()];
			for(int i = 0; i < lines.length; i++)
				lines[i] = lines_v.elementAt(i);
			//
			int space = lines[1].indexOf(" ");
			String jarfile = lines[1].substring(space+1);
			//
			address = new URL("http://" + GAME.Servername + "/jars/" + jarfile);
			ReadableByteChannel rbc = Channels.newChannel(address.openStream());
			FileOutputStream fos = new FileOutputStream("PowerDefense.jar");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
			
			//Die Elementedatei
			address = new URL("http://" + GAME.Servername + "/element.txt");
			rbc = Channels.newChannel(address.openStream());
			fos = new FileOutputStream("element.txt");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
			
			//Die Spielanleitung
			address = new URL("http://" + GAME.Servername + "/spielanleitung.txt");
			rbc = Channels.newChannel(address.openStream());
			fos = new FileOutputStream("spielanleitung.txt");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			rbc.close();
			fos.close();
			//lines[2] = "IMAGES:"
			//Jetzt die Images
			for(int i = 3; i < lines.length; i++)
			{
				address = new URL("http://" + GAME.Servername + "/images/" + lines[i]);
				rbc = Channels.newChannel(address.openStream());
				fos = new FileOutputStream("images/" + lines[i]);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
				rbc.close();
			}
			//
			File f = new File("currentversion.txt");
			f.delete();
			f = new File("updatelist.txt");
			f.renameTo(new File("currentversion.txt"));
			
			System.out.println("[GAME] UPDATED !");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			System.out.println("[GAME] UPDATE FAILED !");
			return;
		}
	}
	
	public static String CreateNewAuthorization()
	{
		try
		{
			address = new URL("http://"+GAME.Servername+"/cgi-bin/createauthorization.cgi");
			HttpURLConnection connection = (HttpURLConnection)address.openConnection();
			connection.setRequestMethod("GET");
			//
			InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            String response = "";
            while((line = rd.readLine()) != null)
            {
            	response = response + line;
            }
            //
			return response;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}
}
