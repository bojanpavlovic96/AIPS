package app.resource_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.io.Resources;

public class BrokerConfig {

	private static final String CONFIG_PATH = "config/broker-config.json";
	private static final String STAGE_FIELD = "stage";

	private static BrokerConfig configCache;

	public static BrokerConfig loadConfig() {

		if (BrokerConfig.configCache == null) {

			// com.google.guava
			URL url = Resources.getResource(CONFIG_PATH);
			String txtConfig = "";
			try {
				txtConfig = Resources.toString(url, StandardCharsets.UTF_8);
			} catch (IOException e1) {
				System.out.println("Error in loading configuration ... ");
				e1.printStackTrace();
				return null;
			}

			if (txtConfig != null && !txtConfig.isEmpty()) {

				JsonMapper mapper = new JsonMapper();
				try {

					JsonNode rawJsonConfig = mapper.readTree(txtConfig);

					String targetStage = rawJsonConfig
							.get(BrokerConfig.STAGE_FIELD)
							.asText();

					JsonNode stageConfig = rawJsonConfig.get(targetStage);
					BrokerConfig.configCache = mapper.treeToValue(stageConfig, BrokerConfig.class);

					BrokerConfig.configCache.stage = targetStage;

				} catch (JsonProcessingException e) {
					System.out.println("Error in parsing broker config file ... ");
					System.out.println("EXC: " + e.toString());
					e.printStackTrace();
					return null;
				}

			} else {
				System.out.println("ERROR: Found empty config file on path:" +
						BrokerConfig.CONFIG_PATH + " ... ");
				return null;
			}

		}

		return BrokerConfig.configCache;
	}

	// json mapped configuration fields

	public String stage; // development || production

	public String address;
	public int port;
	public String username;
	public String password;
	public String vhost;

	public QueuesConfig queues;

	public String toString() {

		String txtObj = "";
		try {
			JsonMapper mapper = new JsonMapper();
			txtObj = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return txtObj;
	}

	public String constructUri() {
		return "amqp://" + this.username + ":" + password
				+ "@" + address + ":" + this.port
				+ "/" + vhost;
	}

}
