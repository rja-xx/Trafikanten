package no.knowit.trafikantenkiller.propertyutils;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public abstract class PropertyLoader
{

    public static Properties loadProperties (String name, ClassLoader loader)
    {
        if (name == null)
            throw new IllegalArgumentException ("null input: name");
        
        if (name.startsWith ("/"))
            name = name.substring (1);
            
        if (name.endsWith (SUFFIX))
            name = name.substring (0, name.length () - SUFFIX.length ());
        
        Properties result = null;
        
        InputStream in = null;
        try
        {
            if (loader == null) loader = ClassLoader.getSystemClassLoader ();
            
            if (LOAD_AS_RESOURCE_BUNDLE){    
                name = name.replace ('/', '.');
                final ResourceBundle rb = ResourceBundle.getBundle (name, Locale.getDefault (), loader);
                
                result = new Properties ();
                for (String key : rb.keySet()) {
                    result.put (key, rb.getString (key));
                } 
            }else{
                name = name.replace ('.', '/');
                
                if (! name.endsWith (SUFFIX))
                    name = name.concat (SUFFIX);
                                
                // Returns null on lookup failures:
                in = loader.getResourceAsStream (name);
                if (in != null)
                {
                    result = new Properties ();
                    result.load (in); // Can throw IOException
                }
            }
        }
        catch (Exception e)
        {
            result = null;
        }
        finally
        {
            if (in != null) try { in.close (); } catch (Throwable ignore) {}
        }
        
        if (THROW_ON_LOAD_FAILURE && (result == null))
        {
            throw new IllegalArgumentException ("could not load [" + name + "]"+
                " as a classloader resource");
        }
        
        return result;
    }
    
    /**
     * A convenience overload of {@link #loadProperties(String, ClassLoader)}
     * that uses the current thread's context classloader.
     */
    public static Properties loadProperties (final String name)
    {
        return loadProperties (name,
            Thread.currentThread ().getContextClassLoader ());
    }
        
    private static final boolean THROW_ON_LOAD_FAILURE = true;
    private static final boolean LOAD_AS_RESOURCE_BUNDLE = false;
    private static final String SUFFIX = ".properties";
} // End of class
