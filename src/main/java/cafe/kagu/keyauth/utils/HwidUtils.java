/**
 * 
 */
package cafe.kagu.keyauth.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;

/**
 * @author DistastefulBannock
 *
 */
public class HwidUtils {
	
	/**
	 * Taken from hummus client. fuck you I own the rights it's mine to use
	 * @return The hwid for the current machine
	 */
	public static String getHwid() {
		String hwid = "";
		
		// Gets the computer's hardware data
		hwid = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL")
			+ System.getenv("NUMBER_OF_PROCESSORS") + System.getenv("PROCESSOR_ARCHITECTURE")
			+ System.getProperty("line.separator") + System.getProperty("os.arch")
			+ System.getProperty("file.encoding");
		
		// Removes spaces and end of line characters
		hwid = hwid.replaceAll(" ", "").replaceAll("(\\r|\\n)", "");
		
		// From github somewhere
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(hwid.getBytes());
	        StringBuffer hexString = new StringBuffer();
	        
	        byte byteData[] = md.digest();
	        
	        for (byte aByteData : byteData) {
	            String hex = Integer.toHexString(0xff & aByteData);
	            if (hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        hwid = hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// Return
		return hwid;
	}
	
}
