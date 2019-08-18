package winw.game.quant.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpExecutor {

	public static final String CHARSET = "GBK";
	public static final int readTimeout = 10000;
	public static final int connectTimeout = 10000;

	public static String get(String url) throws IOException {
		URL request = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) request.openConnection();
		connection.setReadTimeout(readTimeout);
		connection.setConnectTimeout(connectTimeout);

		InputStreamReader is = new InputStreamReader(connection.getInputStream(), CHARSET);
		BufferedReader br = new BufferedReader(is);
		// br.readLine(); // skip the first line

		StringBuilder sb = new StringBuilder();

		for (String line = br.readLine(); line != null; line = br.readLine()) {
			sb.append(line);
		}

		if (sb.toString().length() == 0) {
			throw new IOException("response empty");
		}
		return sb.toString();
	}

	public static byte[] getBytes(String url) throws IOException {
		URL request = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) request.openConnection();
		connection.setReadTimeout(readTimeout);
		connection.setConnectTimeout(connectTimeout);

		InputStream in = connection.getInputStream();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int len = 0;
		byte[] buff = new byte[1024];
		while ((len = in.read(buff)) != -1) {
			os.write(buff, 0, len);
		}
		return os.toByteArray();
	}
}
