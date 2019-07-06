package winw.game.quant.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
	// public static final Logger logger =
	// Logger.getLogger(HttpUtils.class.getName());

	public static final String CHARSET = "utf-8";
	public static final int connectTimeout = 10000;
	public static final int readTimeout = 10000;

	public static String get(String url) throws IOException {
		URL request = new URL(url);

		HttpURLConnection connection = null;
		connection = (HttpURLConnection) request.openConnection();
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);

		InputStreamReader is = new InputStreamReader(
				connection.getInputStream(), "GBK");
		BufferedReader br = new BufferedReader(is);
		//br.readLine(); // skip the first line

		StringBuilder sb = new StringBuilder();

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			sb.append(line);
		}

		if (sb.toString().length() == 0) {
			throw new IOException("response empty");
		}
		return sb.toString();
	}

}
