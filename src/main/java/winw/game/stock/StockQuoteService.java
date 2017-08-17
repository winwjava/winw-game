package winw.game.stock;

import java.util.List;

public interface StockQuoteService {

	Stock getStock(String code) throws Exception;

	/**
	 * 获取历史数据，向前复权（保持现有价位不变，将以前的价格缩减）。
	 * @param code
	 * @return
	 * @throws Exception
	 */
	List<StockQuote> getHistoricalQuote(String code) throws Exception;

}
