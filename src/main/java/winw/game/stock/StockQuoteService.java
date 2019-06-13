package winw.game.stock;

import java.util.List;

/**
 * 股票报价服务接口。
 * 
 * @author winw
 *
 */
public interface StockQuoteService {

	/**
	 * 返回实时报价。
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	StockQuote get(String code) throws Exception;

	/**
	 * 返回历史报价，向前复权（保持现有价位不变，将以前的价格缩减）。
	 * @param code
	 * @param quoteType
	 * @param from
	 * @param to
	 * @param num
	 * @return
	 * @throws Exception
	 */
	List<Quote> get(String code, QuoteType quoteType, String from, String to, int num) throws Exception;

}
