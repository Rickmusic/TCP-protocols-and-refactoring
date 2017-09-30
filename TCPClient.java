import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TCPClient
{
	// url comes in form "domain:optionalPortNumber/path/path.type"
	// example "people.ucalgary.ca:80/~mghaderi/test/test.html"
	String domain; 
	String path;
	String filename; //"path.type" //
	String directory;//"/path/"
	int portNumber = 80; // default port number 80 unless otherwise specified // 
	Date lastModified = null; // Received from HTTP protocol at time of request // 
	
	
	public TCPClient(String url)
	{	
		// Parsing URl string into components // 
		stringParser(url);
	}
	
	// Returns an open connection to the url // 
	// Uses conditional get based on last modified time // 
	public Socket openConnection(Date getTime)
	{
		Socket socket = null;
		// Attempts to open a connection to the domain path and portnumber specified // 
		try
		{
			//Initializing Socket and print writer //  
			socket = new Socket(domain, portNumber);
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))); 
			// Requesting information // 
			// If we have a time get information conditionally // 
			if (getTime != null)
			{
				String [] date = getTime.toString().split(" ");
				out.println("GET " + path + " HTTP/1.0");
				out.println("If-Unmodified-Since: " + date[0] + ", " + date[2] + " " + date[1] + " " + date[5] + " " + date[3] + " "+ "GMT");
			}
			// Else get the information // 
			else 
			{
				out.println("GET " + path + " HTTP/1.0"); 
			}
			out.println(); 
			out.flush(); 
			// Notifying the user // 
			System.out.println("Connected to web address: " + domain + path );
			return socket;
		}
		catch (Exception e)
		{
			System.out.println("Error Opening Socket Connection to " + domain + path);
			e.printStackTrace();
		}
		return socket;
	}
	
	// Attempts to close all the streams and the "open" socket given as a parameter // 
	// Notifies user of activity // 
	public void CloseConnection(Socket socket)
	{
		try 
		{
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
			System.out.println("Connection closed to " + domain + path);
		} 
		catch (IOException e)
		{
			System.out.println("Error Closing Socket Connection to " + domain + path);
			e.printStackTrace();
		}	
	}
	
	// Downloads and delegates information from the url // 
	// Notifies user of happenings // 
	public void conditionalDownloadURL(Date getTime)
	{

			// Flag to determine if data included with HTTP message or not // 
			boolean flag = false;
			// Opens socket for url // 
			Socket socket = openConnection(getTime);
			
			// Retrieval of socket input Stream // 
			InputStream inToo;	
			// Used to read HTTP header and data //  
			BufferedReader in;
			// Updates the lastmodified time to the time given by the cache// 
			lastModified = getTime;
			
			// Try to read the input provided by the socket connection // 
			try 
			{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		        String inputLine;
		        // Reading HTTP Header and saving lastModified time// 
		        // Last modified time saved as type data using SimpleDateFormat // 
		        // Also sets flag if the message includes data // 
				while((inputLine = in.readLine()) != null && !inputLine.contains("Content-Type:"))
				{
					if (inputLine.contains("Last"))
					{
						String [] temp = inputLine.split(" ");
						SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz");
						lastModified = sdf.parse(temp[1] + " " +  temp[2] + " " + temp[3] + " "  + temp[4] + " " + temp[5] + " " + temp[6]);
						flag = true;
					}
				}
				
				// If we have data create directories if they don't already exist and save it// 
				if (flag == true)
				{	
					// Used as buffer for incoming data streams // 
					byte[] data = new byte[10 * 1024];
					// Creates directory structure in working directory // 
					File location = new File(directory);
					location.mkdirs();
					// // 
					System.out.println("Saving File");
					FileOutputStream fileOut = new FileOutputStream(directory + "/" + filename);
					inToo = socket.getInputStream();
					// Writes data to file // 
					int x;
					while((x = inToo.read(data)) != -1)
					{
						fileOut.write(data, 0, x);
					}	
					fileOut.close();
				}
				// No changes to data then notify the user of this // 
				else
				{
					System.out.println("No changes to saved content. Files not changed");
				}
				// Always close your connections // 
				CloseConnection(socket);
				in.close();	
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				System.out.println("Date Capture failure");
				e.printStackTrace();
			} 
	}
	
	// Divides the url into it's individual components// 
	private void stringParser(String url)
	{
		path = "";
		directory = "";
		String[] tokens = url.split("/");
		filename = tokens[(tokens.length) - 1 ];
		for(int x = 1; x < tokens.length; x++)
		{
			if (x != (tokens.length -1))
			{
				directory = directory.concat("/");
				directory = directory.concat(tokens[x]);
				
			}
			path = path.concat("/");
			path = path.concat(tokens[x]);	
		}
		
		
		tokens = tokens[0].split(":");
		if(tokens.length == 2)
		{
			portNumber = Integer.parseInt(tokens[1]);
		}
		domain = tokens[0];
		directory = domain.concat(directory);
	}
	
	// returns last modified data associated with url // Only works when connection has been opened or time provided by cache // 
	public Date getLastModified()
	{
		return lastModified;
	}
}

