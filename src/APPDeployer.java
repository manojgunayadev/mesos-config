import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class APPDeployer {

	static String HOST_NAME = "localhost";
	static String PORT = "8080";
	static String MESOS_APP_URL = "/v2/apps";
	static String MESOS_DNS_CONF_FILENAME = "mesos-dns.json";
	static String MESOS_MYSQL_CONF_FILENAME = "mysql.json";
	static String MESOS_APIM_CONF_FILENAEME = "apim.json";
	static String MESOS_MYSQL_CLIENT_CONF_FILENAME = "mysqlclient.json";
	static String MESOS_CONFIGUTAION_ID = "id";
	static String MESOS_DNS_SERVICE_STATUS_URL = "/v1/services/";
	static String MESOS_APP_STATUS_PORT = "8123";
	static String MESOS_DNS_DOMAIN_SUFFIX = "_tcp.marathon.mesos";

	public static void main(String[] args) throws Exception {

		String marathonAppDeployerUrl = "http:" + "//" + HOST_NAME + ":" + PORT
				+ MESOS_APP_URL;

		String mesosDNSConf = readFile(
				"/home/manoj/support/verizone/workspace/Verizone/conf/"
						+ MESOS_DNS_CONF_FILENAME, Charset.defaultCharset());
		deploy(mesosDNSConf, marathonAppDeployerUrl);

		String mesosMySqlConf = readFile(
				"/home/manoj/support/verizone/workspace/Verizone/conf/"
						+ MESOS_MYSQL_CONF_FILENAME, Charset.defaultCharset());
		deploy(mesosMySqlConf, marathonAppDeployerUrl);

		String serviceStatusResult;
		String appId = getServiceId(mesosMySqlConf);
		String servicePort = "";
		String serviceHost = "";

		do {
			serviceStatusResult = checkStatusOfService(appId);
			if (serviceStatusResult != null && serviceStatusResult.length() > 0) {
				servicePort = getServiceDetails("host", serviceStatusResult);
				serviceHost = getServiceDetails("port", serviceStatusResult);
				if (serviceStatusResult != null
						&& serviceStatusResult.length() > 0) {
					if (servicePort != null && servicePort.length() > 0
							&& serviceHost != null && serviceHost.length() > 0) {
						break;
					}
				}
			}
			System.out.println("waiting...");
			Thread.sleep(30 * 1000);
		} while (true);

		String mesosMySqlClientConf = readFile(
				"/home/manoj/support/verizone/workspace/Verizone/conf/"
						+ MESOS_MYSQL_CLIENT_CONF_FILENAME,
				Charset.defaultCharset());
		deploy(mesosMySqlClientConf, marathonAppDeployerUrl);

		appId = getServiceId(mesosMySqlClientConf);

		do {
			serviceStatusResult = checkStatusOfService(appId);
			if (serviceStatusResult != null && serviceStatusResult.length() > 0) {

				servicePort = getServiceDetails("host", serviceStatusResult);
				serviceHost = getServiceDetails("port", serviceStatusResult);

				if (servicePort != null && servicePort.length() > 0
						&& serviceHost != null && serviceHost.length() > 0) {
					break;
				}
			}
			System.out.println("waiting...");
			Thread.sleep(30 * 1000);
		} while (true);
		
		deleteApp(appId);
		
	 String mesosapimConf = readFile(
				"/home/manoj/support/verizone/workspace/Verizone/conf/"
						+ MESOS_APIM_CONF_FILENAEME, Charset.defaultCharset());
	 deploy(mesosapimConf, marathonAppDeployerUrl);

	}

	private static void deleteApp(String appId) {

	//	/v2/apps/my-app/tasks?host=mesos.vm&scale=false
		URL url;
		try {
			url = new URL("http://" + HOST_NAME +":" + PORT + MESOS_APP_URL +"/" + appId);
			
			
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("DELETE");
			httpCon.connect(); 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	private static String getServiceDetails(String elementName,
			String serviceStatusResult) {

		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(serviceStatusResult).getAsJsonArray();
		Gson googleJson = new Gson();
		JsonObject jsonObj = array.get(0).getAsJsonObject();

		String configId = jsonObj.get(elementName).getAsString();
		return configId;
	}

	private static String checkStatusOfService(String appId) {

		StringBuffer response = new StringBuffer();

		try {

			// http://127.0.0.1:8123/v1/services/_mysql777._tcp.marathon.mesos
			URL url = new URL("http://" + HOST_NAME + ":"
					+ MESOS_APP_STATUS_PORT + MESOS_DNS_SERVICE_STATUS_URL
					+ "_" + appId + "." + MESOS_DNS_DOMAIN_SUFFIX);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			con.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
		return response.toString();
	}

	private static String getServiceId(String messoConfigurations) {

		JsonParser parser = new JsonParser();
		JsonObject obj = parser.parse(messoConfigurations).getAsJsonObject();
		String configId = obj.get(MESOS_CONFIGUTAION_ID).getAsString();

		return configId;
	}

	private static void deploy(String confJson, String Url) {

		try {
			URL url = new URL("http://" + HOST_NAME + ":" + PORT
					+ MESOS_APP_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			OutputStream os = conn.getOutputStream();
			os.write(confJson.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	private static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
