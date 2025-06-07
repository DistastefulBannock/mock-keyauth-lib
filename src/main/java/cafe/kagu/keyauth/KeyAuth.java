/**
 * 
 */
package cafe.kagu.keyauth;

import java.io.File;

import cafe.kagu.keyauth.utils.ResponseHandler;

/**
 * @author DistastefulBannock This class is used to store data, make requests,
 *         and give a developer easy to use methods to interface with keyauths
 *         api
 */
public class KeyAuth {

	/**
	 * @param ownerId   You can find out the owner id in the profile settings on
	 *                  keyauth.win
	 * @param appName   Application name
	 * @param appSecret The app secret
	 * @param version   Application version
	 */
	public KeyAuth(String ownerId, String appName, String appSecret, double version) {
		this(ownerId, appName, appSecret, version + "");
	}

	/**
	 * @param ownerId   You can find out the owner id in the profile settings on
	 *                  keyauth.win
	 * @param appName   Application name
	 * @param appSecret The app secret
	 * @param version   Application version
	 */
	public KeyAuth(String ownerId, String appName, String appSecret, String version) {
		this.ownerId = ownerId;
		this.appName = appName;
		this.appSecret = appSecret;
		this.version = version;
	}

	private String ownerId, appName, appSecret, version, session = null;
	private boolean loggedIn = false;

	/**
	 * Initializes keyauth
	 * 
	 * @param onFailedInit     A ResponseHandler containing the code that runs if
	 *                         the response doesn't allow us to initialize. This
	 *                         could show an error message, close the app, log the
	 *                         error. Whatever you need it to do
	 * @param requestError     A ResponseHandler containing the code that runs if
	 *                         there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if
	 *                         the response from the server is tampered with
	 */
	public void initialize(ResponseHandler onFailedInit, ResponseHandler requestError,
			ResponseHandler tamperedResponse) {
		session = "0";
	}

	/**
	 * Registers a new account for the user
	 * 
	 * @param username                   Their username
	 * @param password                   Their password
	 * @param key                        Their license key
	 * @param requestError               A ResponseHandler containing the code that
	 *                                   runs if there is an error while sending the
	 *                                   request
	 * @param tamperedResponse           A ResponseHandler containing the code that
	 *                                   runs if the response from the server is
	 *                                   tampered with
	 * @param errorRegisteringAccount    A ResponseHandler containing the code that
	 *                                   runs if there is an issue while registering
	 *                                   an account
	 * @param successfullyCreatedAccount A ResponseHandler containing the code that
	 *                                   runs if the account is successfully created
	 */
	public void register(String username, String password, String key, ResponseHandler requestError,
			ResponseHandler tamperedResponse, ResponseHandler errorRegisteringAccount,
			ResponseHandler successfullyCreatedAccount) {
		if (session == null) {
			requestError.run("Not initialized");
			return;
		} else if (loggedIn) {
			requestError.run("Aleady logged in");
			return;
		}

		checkSession(requestError, tamperedResponse, errorRegisteringAccount);
		successfullyCreatedAccount.run("Registered and logged in!");

	}

	/**
	 * Logins into an account for the user
	 * 
	 * @param username             Their username
	 * @param password             Their password
	 * @param requestError         A ResponseHandler containing the code that runs
	 *                             if there is an error while sending the request
	 * @param tamperedResponse     A ResponseHandler containing the code that runs
	 *                             if the response from the server is tampered with
	 * @param errorLoggingIn       ResponseHandler containing the code that runs if
	 *                             there is an issue while logging into the account
	 * @param successfullyLoggedIn A ResponseHandler containing the code that runs
	 *                             if the user is successfully logged in
	 */
	public void login(String username, String password, ResponseHandler requestError, ResponseHandler tamperedResponse,
			ResponseHandler errorLoggingIn, ResponseHandler successfullyLoggedIn) {
		if (session == null) {
			requestError.run("Not initialized");
			return;
		} else if (loggedIn) {
			requestError.run("Aleady logged in");
			return;
		}

		checkSession(requestError, tamperedResponse, errorLoggingIn);
		successfullyLoggedIn.run("Logged in!");

	}

	/**
	 * Checks if the current session is that of a logged in user, if they are then
	 * it overwrites the current session used
	 * 
	 * @param requestError     A ResponseHandler containing the code that runs if
	 *                         there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if
	 *                         the response from the server is tampered with
	 * @param errorLoggingIn   ResponseHandler containing the code that runs if
	 *                         there is an issue while logging into the account
	 */
	public void checkSession(ResponseHandler requestError, ResponseHandler tamperedResponse,
			ResponseHandler errorLoggingIn) {
		if (session == null) {
			requestError.run("Not initialized");
			return;
		}
		loggedIn = true;
	}

	/**
	 * Checks if the hwid or the ip of the user is blacklisted
	 * 
	 * @param requestError     A ResponseHandler containing the code that runs if
	 *                         there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if
	 *                         the response from the server is tampered with
	 * @param blacklisted      ResponseHandler containing the code that runs if the
	 *                         user/hwid is blacklisted
	 */
	public void checkBlacklist(ResponseHandler requestError, ResponseHandler tamperedResponse,
			ResponseHandler blacklisted) {
		if (session == null) {
			requestError.run("Not initialized");
			return;
		}
	}

	/**
	 * Downloads a file from the file id
	 * @param fileId The id of the file to download
	 * @param downloadFile The file where the downloaded data should be stored
	 * @param requestError A ResponseHandler containing the code that runs if there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if the response from the server is tampered with
	 * @throws Exception Thrown when something goes wrong
	 */
	public void download(String fileId, File downloadFile, ResponseHandler requestError, ResponseHandler tamperedResponse) throws Exception {
		if (session == null) {
			requestError.run("Not initialized");
			return;
		}
		String error = "The mock keyauth lib version does not support the downloading of keyauth hosted files";
		requestError.run(error);
		throw new UnsupportedOperationException(error);
	}
	
	/**
	 * Bans the current logged in user, it will also blacklist their hwid and their current ip
	 * @param requestError A ResponseHandler containing the code that runs if there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if the response from the server is tampered with
	 */
	public void ban(ResponseHandler requestError, ResponseHandler tamperedResponse) {
		if (session == null) {
			requestError.run("Not initialized");
			return;
		} else if (!loggedIn) {
			requestError.run("Not logged in");
			return;
		}
	}
	
	/**
	 * Sends a log request to the auth server
	 * @param message The message to log
	 * @param requestError A ResponseHandler containing the code that runs if there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if the response from the server is tampered with
	 */
	public void log(String message, ResponseHandler requestError, ResponseHandler tamperedResponse) {
		if (session == null) {
			requestError.run("Not initialized");
			return;
		}
	}

	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}
	
	/**
	 * @return the ownerId
	 */
	public String getOwnerId() {
		return ownerId;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * @return the session
	 */
	public String getSession() {
		return session;
	}
	
}
