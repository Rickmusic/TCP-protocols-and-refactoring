

public class UrlCache
{
	Catalog catalog;
    /**
     * Default constructor to initialize data structures used for caching/etc
	 * If the cache already exists then load it. If any errors then throw exception.
	 *
     * @throws UrlCacheException if encounters any errors/exceptions
     */
	public UrlCache() throws UrlCacheException
	{
		// Creation of data catalog // Loads cache if found // 
		catalog = new Catalog();
	}
	
    /**
     * Downloads the object specified by the parameter url if the local copy is out of date.
	 *
     * @param url	URL of the object to be downloaded. It is a fully qualified URL.
     * @throws UrlCacheException if encounters any errors/exceptions
     * @throws UnsupportedEncodingException 
     */
	public void getObject(String url) throws UrlCacheException
	{
		// Creation of new TCP client // 
		TCPClient client = new TCPClient(url);
		// Downloads the file specified by the url only if modified since the last time it was downloaded specified by the catalog // 
		client.conditionalDownloadURL(catalog.getTime(url));
		catalog.AddToCatalog(url, client.lastModified);
	}
	
    /**
     * Returns the Last-Modified time associated with the object specified by the parameter url.
	 *
     * @param url 	URL of the object 
	 * @return the Last-Modified time in millisecond as in Date.getTime()
     * @throws UrlCacheException if the specified url is not in the cache, or there are other errors/exceptions
     */
	public long getLastModified(String url) throws UrlCacheException
	{
		long milliseconds = catalog.getTime(url).getTime();
		return milliseconds;
	}

}