package winw.game.quant;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 股票报价服务接口。
 * 
 * @author winw
 *
 */
public abstract class QuoteService {

	/**
	 * 返回实时报价。
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public abstract Quote get(String code) throws Exception;

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
	public abstract List<Quote> get(String code, String from, String to) throws Exception;

	/**
	 * 
	 * @return 当前时间是否是交易时间。
	 */
	public boolean isTradingTime() {
		String hhmm = DateFormatUtils.format(new Date(), "HHmm");
		return hhmm.compareTo("0930") >= 0 && hhmm.compareTo("1500") <= 0;
	}

	/**
	 * @return 当前日期是否是交易日。
	 * @throws Exception
	 */
	public boolean isTradingDay() throws Exception {
		String today = DateFormatUtils.format(new Date(), Quote.DATE_PATTERN);
		// 先查询今天是否是交易日。
		Quote quote = get("sh000300");
		return today.equals(quote.getDate());
	}

	public static QuoteService getDefault() {
		return new SinaQuoteService();
	}

}
