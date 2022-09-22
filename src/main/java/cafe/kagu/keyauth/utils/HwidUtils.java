/**
 * 
 */
package cafe.kagu.keyauth.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor.ProcessorIdentifier;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HWDiskStore;

/**
 * @author DistastefulBannock
 *
 */
public class HwidUtils {
	
	/**
	 * @return The hwid for the current machine
	 */
	public static String getHwid() {
		String hwid = "";
		
		SystemInfo systemInfo = new SystemInfo();
		for (GraphicsCard graphicsCard : systemInfo.getHardware().getGraphicsCards()) {
			hwid += graphicsCard.getDeviceId() + graphicsCard.getName() + graphicsCard.getVendor() + graphicsCard.getVRam();
		}
		for (HWDiskStore hwDiskStore : systemInfo.getHardware().getDiskStores()) {
			hwid += hwDiskStore.getSerial();
		}
		ProcessorIdentifier processorIdentifier = systemInfo.getHardware().getProcessor().getProcessorIdentifier();
		hwid += processorIdentifier.getFamily() + processorIdentifier.getIdentifier() + processorIdentifier.getVendor() + processorIdentifier.getMicroarchitecture();
		
		// From github somewhere
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(hwid.getBytes(StandardCharsets.UTF_8));
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
