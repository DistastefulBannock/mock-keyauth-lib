/**
 * 
 */
package cafe.kagu.keyauth.utils;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * @author DistastefulBannock
 * Used for verification of responses from the auth server
 */
public class HashingUtils {
	
	/**
	 * @param key The key to hash with
	 * @param data The data to hash
	 * @return The hashed data, or just null if something has gone wrong
	 */
	public static String hashHmacSha256(String key, String data) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			mac.init(secret);
			return Hex.encodeHexString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			return null;
		}
	}
	
}
