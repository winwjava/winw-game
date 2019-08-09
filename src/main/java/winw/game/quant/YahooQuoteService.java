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
 * 
 * '000300.SS' ，'香港恒生'=>'0011.HK', '道琼指数'=>'INDU', '纳斯达克'=>'^IXIC'
 * 
 * @author winw
 *
 */
public class YahooQuoteService extends QuoteService {

	@Override
	public Quote get(String code) throws Exception {
		yahoofinance.quotes.stock.StockQuote temp = YahooFinance.get(code).getQuote();
		Quote quote = new Quote();
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
	public List<Quote> get(String code, QuotePeriod quotePeriod, String from, String to) throws Exception {
		Calendar f = Calendar.getInstance();
		Calendar t = Calendar.getInstance();
		f.setTime(DateUtils.parseDate(from, DATE_PATTERN));
		t.setTime(DateUtils.parseDate(to, DATE_PATTERN));

		Stock s = YahooFinance.get(code);
		List<HistoricalQuote> h = s.getHistory(f, t, Interval.DAILY);

		List<Quote> quoteList = new ArrayList<Quote>();
		for (HistoricalQuote q : h) {
			try {
				Quote quote = new Quote();
				quote.setCode(code);
				quote.setQuotePeriod(quotePeriod);
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
