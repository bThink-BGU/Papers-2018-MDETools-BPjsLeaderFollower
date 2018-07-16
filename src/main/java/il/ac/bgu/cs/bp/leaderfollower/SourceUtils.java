package il.ac.bgu.cs.bp.leaderfollower;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 *
 * @author michael
 */
public class SourceUtils {
    
    public static String readResource( String resourceName ) throws IOException { 
        try ( InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
              BufferedReader rdr = new BufferedReader(new InputStreamReader(resource))) {
            if (resource == null) {
                throw new RuntimeException("Resource '" + resourceName + "' not found.");
            }
            return rdr.lines().collect( Collectors.joining("\n") );
            
        } catch (IOException ex) {
            throw new RuntimeException("Error reading resource: '" + resourceName +"': " + ex.getMessage(), ex);
        }
    }
}
