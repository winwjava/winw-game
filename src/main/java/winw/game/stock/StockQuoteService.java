package winw.game.stock;

import java.util.List;

public interface StockQuoteService {

	Stock getStock(String code) throws Exception;

	List<StockQuote> getHistoricalQuote(String code) throws Exception;

}
