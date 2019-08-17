package winw.game.quant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

/**
 * 雅虎接口实现。
 * <p>
 * '000300.SS' ，'香港恒生'=>'0011.HK', '道琼指数'=>'INDU', '纳斯达克'=>'^IXIC'
 * 
 * @author winw
 *
 */
public class YahooQuoteService extends QuoteService {

	@Override
	public <T extends Quote> T get(Class<T> clazz, String code) throws Exception {
		yahoofinance.quotes.stock.StockQuote temp = YahooFinance.get(code).getQuote();
		T quote = clazz.newInstance();
		quote.setName(temp.getSymbol());
		quote.setCode(code);
		quote.setPrice(temp.getPrice().doubleValue());
		quote.setPreviousClose(temp.getPreviousClose().doubleValue());
		quote.setOpen(temp.getOpen().doubleValue());
		quote.setVolume(temp.getVolume().intValue());
		quote.setTime(temp.getLastTradeTime().getTime());
		quote.setHigh(temp.getDayHigh().doubleValue());
		quote.setLow(temp.getDayLow().doubleValue());
		return quote;
	}

	@Override
	public <T extends Quote> List<T> get(Class<T> clazz, String code, String from, String to) throws Exception {
		Calendar f = Calendar.getInstance();
		Calendar t = Calendar.getInstance();
		f.setTime(DateUtils.parseDate(from, Quote.DATE_PATTERN));
		t.setTime(DateUtils.parseDate(to, Quote.DATE_PATTERN));

		Stock s = YahooFinance.get(code);
		List<HistoricalQuote> h = s.getHistory(f, t, Interval.DAILY);

		List<T> quoteList = new ArrayList<T>();
		for (HistoricalQuote q : h) {
			try {
				T quote = clazz.newInstance();
				quote.setCode(code);
				quote.setDate(DateFormatUtils.format(q.getDate(), Quote.DATE_PATTERN));
				quote.setClose(q.getClose().doubleValue());
				quote.setOpen(q.getOpen().doubleValue());
				quote.setHigh(q.getHigh().doubleValue());
				quote.setLow(q.getLow().doubleValue());
				quote.setVolume(q.getVolume().intValue());
				quoteList.add(quote);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return quoteList;
	}

}
