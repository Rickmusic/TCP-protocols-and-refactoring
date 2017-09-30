import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Catalog class
 * 
 * @author 	Austin Wattling
 *
 * Class handles all the IO to the Cache File
 */


public class Catalog
{
	
	HashMap<String, Date> catalog = new HashMap<String, Date>(); // Saves the URL along with the last modified time retrieved with URL Request
	@SuppressWarnings("unchecked")
	public Catalog()
	{
		// Try block attempts to open the cache saved by the last session // 
		// If unable to do so notifies user and uses an empty hashmap instead. //
		try
		{
			
	        File toRead=new File("Cache");
	        FileInputStream fis=new FileInputStream(toRead);
	        ObjectInputStream ois=new ObjectInputStream(fis);

			catalog =(HashMap<String,Date>)ois.readObject();

	        ois.close();
	        fis.close();

	    }
	    
		catch(Exception e)
		{
			System.out.println("No Current Catalog found");			
		}
	}
	
	// returns the last modified time of the url and returns null if url is not found. // 
	public Date getTime(String url)
	{
		return catalog.get(url);
	}
	
	// Adds a new entry into the current hashmap and saves the entire current hashmap to "cache" file // 
	public void AddToCatalog(String URL, Date LastModified)
	{
		catalog.put(URL, LastModified);
		try
		{
		    File fileOne=new File("Cache");
		    FileOutputStream fos=new FileOutputStream(fileOne);
		        ObjectOutputStream oos=new ObjectOutputStream(fos);

		        oos.writeObject(catalog);
		        oos.flush();
		        oos.close();
		        fos.close();
		    }
		catch(Exception e){}
	}
	
	
	// testing Method used to print out the current Hashmap/cache. // 
	public void printCatalog()
	{
		System.out.println(Arrays.asList(catalog));
	}
	
}
