/**
 * 
 */
package cafe.kagu.keyauth;

/**
 * @author lavaflowglow
 *
 */
public class Example {

	/**
	 * Program entry point
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Example keyauth object
		KeyAuth keyAuth = new KeyAuth("ZvEPlLo1aX", "KaguTest",
				"f1bb6072a0c259c5f628555b52837da2ae917b22201aab57f59c00a29b1f5ad5", 1.0);
		
		// Init keyauth
		keyAuth.initialize(msg -> {
			System.out.println("Failed " + msg);
		}, msg -> {
			System.out.println("Error " + msg);
		}, msg -> {
			System.out.println("Tampered response " + msg);
		});
		
		// Register new user account
		keyAuth.register("Example", "Password", "YLCZ0P-J556P9-NERKL9-YCWAM9-C6I4U2-QXXRAV", msg -> {
			System.out.println("Error " + msg);
		}, msg -> {
			System.out.println("Response tampered " + msg);
		}, msg -> {
			System.out.println("Error registering account " + msg);
		});
		
	}

}
