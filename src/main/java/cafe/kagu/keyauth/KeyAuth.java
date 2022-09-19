/**
 * 
 */
package cafe.kagu.keyauth;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.json.JSONObject;

import cafe.kagu.keyauth.utils.HashingUtils;
import cafe.kagu.keyauth.utils.HwidUtils;
import cafe.kagu.keyauth.utils.ResponseHandler;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author DistastefulBannock
 * This class is used to store data, make requests, and give a developer easy to use methods to interface with keyauths api
 */
public class KeyAuth {
	
	/**
	 * @param ownerId You can find out the owner id in the profile settings on keyauth.win
	 * @param appName Application name
	 * @param appSecret The app secret
	 * @param version Application version
	 */
	public KeyAuth(String ownerId, String appName, String appSecret, double version) {
		this(ownerId, appName, appSecret, version + "");
	}
	
	/**
	 * @param ownerId You can find out the owner id in the profile settings on keyauth.win
	 * @param appName Application name
	 * @param appSecret The app secret
	 * @param version Application version
	 */
	public KeyAuth(String ownerId, String appName, String appSecret, String version) {
		this.ownerId = ownerId;
		this.appName = appName;
		this.appSecret = appSecret;
		this.version = version;
	}

	private String ownerId, appName, appSecret, version, guid, session = null;
	private boolean loggedIn = false;
	public static final String KEYAUTH_ENDPOINT = "https://keyauth.win/api/1.2/";
	private final OkHttpClient client = new OkHttpClient();
	
	/**
	 * Initializes keyauth
	 * @param onFailedInit A ResponseHandler containing the code that runs if the response doesn't allow us to initialize. This could show an error message, close the app, log the error. Whatever you need it to do
	 * @param requestError A ResponseHandler containing the code that runs if there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if the response from the server is tampered with
	 */
	public void initialize(ResponseHandler onFailedInit, ResponseHandler requestError, ResponseHandler tamperedResponse) {
		
		// Create body for request
		guid = UUID.randomUUID().toString();
		FormBody formBody = new FormBody.Builder().add("type", "init").add("ver", version).add("name", appName)
				.add("ownerid", ownerId).add("enckey", guid).build();
		
		// Create and send the request
		Request request = new Request.Builder().url(KEYAUTH_ENDPOINT).post(formBody)
				.addHeader("Content-Type", "application/x-www-form-urlencoded").build();
		
		Call call = client.newCall(request);
		Response response = null;
		try {
			response = call.execute();
		} catch (IOException e) {
			if (response != null)
				response.close();
			e.printStackTrace();
			requestError.run("IOException");
			return;
		}
		
		// Docs say that the response can only ever be 200, if this isn't the case then something has gone wrong
		if (response.code() != 200) {
			requestError.run("Response Code " + response.code());
			response.close();
			return;
		}
		
		// Get response body
		String signature = response.header("signature"); // For later, getting before response is closed
		String jsonStr = null;
		try {
			jsonStr = response.body().string();
			response.close();
		} catch (IOException e) {
			response.close();
			e.printStackTrace();
			requestError.run("IOException");
			return;
		}
		
		// Verify the response isn't tampered with
		String hash = HashingUtils.hashHmacSha256(appSecret, jsonStr);
		if (!signature.equals(hash)) {
			tamperedResponse.run("Signature header \"" + signature + "\" didn't match \"" + hash + "\"");
			return;
		}
		
		// Parse and handle response
		JSONObject json = new JSONObject(jsonStr);
		if (json.getBoolean("success")) {
			session = json.getString("sessionid");
		}else {
			onFailedInit.run(json.getString("message"));
		}
	}
	
	/**
	 * Registers a new account for the user
	 * @param username Their username
	 * @param password Their password
	 * @param key Their license key
	 * @param requestError A ResponseHandler containing the code that runs if there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if the response from the server is tampered with
	 * @param errorRegisteringAccount A ResponseHandler containing the code that runs if there is an issue while registering an account
	 */
	public void register(String username, String password, String key, ResponseHandler requestError, ResponseHandler tamperedResponse, ResponseHandler errorRegisteringAccount) {
		if (session == null) {
			requestError.run("NotInitialized");
			return;
		}
		else if (loggedIn) {
			requestError.run("AleadyLoggedIn");
			return;
		}
		
		FormBody formBody = new FormBody.Builder().add("type", "register").add("username", username).add("pass", password)
				.add("key", key).add("hwid", HwidUtils.getHwid()).add("sessionid", session).add("name", appName).add("ownerid", ownerId).build();
		
		String jsonStr = makeRequest(formBody);
		switch (jsonStr) {
			case "IOException":
			case "NON200":{
				requestError.run(jsonStr);
			}break;
			case "Tampered":{
				tamperedResponse.run(jsonStr);
			}break;
			default:{
				JSONObject json = new JSONObject(jsonStr);
				if (json.getBoolean("success")){
					errorRegisteringAccount.run(json.getString("message"));
				}else {
					loggedIn = true;
				}
			}break;
		}
		
	}
	
	/**
	 * Logins into an account account for the user
	 * @param username Their username
	 * @param password Their password
	 * @param requestError A ResponseHandler containing the code that runs if there is an error while sending the request
	 * @param tamperedResponse A ResponseHandler containing the code that runs if the response from the server is tampered with
	 * @param errorLoggingIn ResponseHandler containing the code that runs if there is an issue while logging into the account
	 */
	public void login(String username, String password, ResponseHandler requestError, ResponseHandler tamperedResponse, ResponseHandler errorLoggingIn) {
		if (session == null) {
			requestError.run("NotInitialized");
			return;
		}
		else if (loggedIn) {
			requestError.run("AleadyLoggedIn");
			return;
		}
		
		FormBody formBody = new FormBody.Builder().add("type", "login").add("username", username).add("pass", password)
				.add("hwid", HwidUtils.getHwid()).add("sessionid", session).add("name", appName).add("ownerid", ownerId).build();
		
		String jsonStr = makeRequest(formBody);
		switch (jsonStr) {
			case "IOException":
			case "NON200":{
				requestError.run(jsonStr);
			}break;
			case "Tampered":{
				tamperedResponse.run(jsonStr);
			}break;
			default:{
				JSONObject json = new JSONObject(jsonStr);
				if (json.getBoolean("success")){
					errorLoggingIn.run(json.getString("message"));
				}else {
					loggedIn = true;
				}
			}break;
		}
		
	}
	
	/**
	 * Makes a request to the auth server and checks it for tampering, the initialize method doesn't use this because the tamper check is slightly different for that response
	 * @param requestBody The request payload to send
	 * @return The response json
	 */
	private String makeRequest(RequestBody requestBody) {
		// Create and send the request
		Request request = new Request.Builder().url(KEYAUTH_ENDPOINT).post(requestBody)
				.addHeader("Content-Type", "application/x-www-form-urlencoded").build();

		Call call = client.newCall(request);
		Response response = null;
		try {
			response = call.execute();
		} catch (IOException e) {
			if (response != null)
				response.close();
			e.printStackTrace();
			return "IOException";
		}

		// Docs say that the response can only ever be 200, if this isn't the case then
		// something has gone wrong
		if (response.code() != 200) {
			response.close();
			return "NON200";
		}

		// Get response body
		String signature = response.header("signature"); // For later, getting before response is closed
		String jsonStr = null;
		try {
			jsonStr = response.body().string();
			response.close();
		} catch (IOException e) {
			response.close();
			e.printStackTrace();
			return "IOException";
		}

		// Verify the response isn't tampered with
		String hash = HashingUtils.hashHmacSha256(guid + "-" + appSecret, jsonStr);
		if (!signature.equals(hash)) {
			return "Tampered";
		}
		
		// Return
		return jsonStr;
	}
	
	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
}
