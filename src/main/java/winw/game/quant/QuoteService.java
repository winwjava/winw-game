package winw.game.quant;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 股票报价服务接口。
 * 
 * @author winw
 *
 */
public interface QuoteService {

	/**
	 * 
	 * @return 当前时间是否是交易时间。
	 */
	default boolean isTradingTime() {
		String hhmm = DateFormatUtils.format(new Date(), "HHmm");
		return hhmm.compareTo("0930") >= 0 && hhmm.compareTo("1500") <= 0;
	}

	/**
	 * @return 当前日期是否是交易日。
	 * @throws Exception
	 */
	default boolean isTradingDay() throws Exception {
		String today = DateFormatUtils.format(new Date(), Quote.DATE_PATTERN);
		// 先查询今天是否是交易日。
		Quote quote = get(Quote.class, "sh000300");
		return today.equals(quote.getDate());
	}

	/**
	 * 返回实时报价。
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	<T extends Quote> T get(Class<T> clazz, String code) throws Exception;

	/**
	 * 返回每日历史报价，向前复权（保持现有价位不变，将以前的价格缩减）。
	 * 
	 * <p>
	 * 包含当天正在交易的数据，当前价格作为收盘价。
	 * 
	 * @param code
	 * @param quotePeriod
	 * @param from
	 * @param to
	 * @return
	 * @throws Exception
	 */
	default <T extends Quote> List<T> get(Class<T> clazz, String code, String from, String to) throws Exception {
		// TODO 历史数据支持缓存，24小时内数据只取一次，次日更换。
		List<T> result = get(clazz, code, from, to, Quote.diff(from, to));

		String lastday = result.get(result.size() - 1).getDate();
		if (to.equals(lastday)) {
			return result;
		}
		T quote = get(clazz, code);
		quote.setClose(quote.getPrice());
		if (to.equals(quote.getDate())) {
			result.add(quote);
		}
		return result;
	}

	HttpClient client = HttpClient.newHttpClient();

	default String get(String url) throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
	}

	/**
	 * 返回每日历史报价，向前复权（保持现有价位不变，将以前的价格缩减），不包含今天正在交易的数据。
	 * 
	 * @param code
	 * @param quotePeriod
	 * @param from
	 * @param to
	 * @return
	 * @throws Exception
	 */
	<T extends Quote> List<T> get(Class<T> clazz, String code, String from, String to, int num) throws Exception;

	static QuoteService getDefault() {
		return new QuoteService() {

			private SinaQuoteService sinaQuoteService = new SinaQuoteService();
			private TencentQuoteService tencentQuoteService = new TencentQuoteService();

			@Override
			public <T extends Quote> T get(Class<T> clazz, String code) throws Exception {
				return sinaQuoteService.get(clazz, code);
			}

			@Override
			public <T extends Quote> List<T> get(Class<T> clazz, String code, String from, String to, int num)
					throws Exception {
				return tencentQuoteService.get(clazz, code, from, to, num);
			}
		};
	}

}
