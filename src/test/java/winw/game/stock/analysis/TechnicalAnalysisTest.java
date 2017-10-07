package winw.game.stock.analysis;

import java.text.DecimalFormat;
import java.util.List;

import org.junit.Test;

import winw.game.stock.Quote;
import winw.game.stock.QuoteType;
import winw.game.stock.StockList;
import winw.game.stock.StockQuote;
import winw.game.stock.StockQuoteService;
import winw.game.stock.TencentStockQuoteService;

public class TechnicalAnalysisTest {

	private StockQuoteService service = new TencentStockQuoteService();

	@Test
	public void testAll() throws Exception {
		for (String temp : StockList.SSE_50) {
			test(temp);
		}
	}

	@Test
	public void testOne() throws Exception {
		List<Indicator> indicatorList = test("sz000503");
		for (int i = 50; i < indicatorList.size(); i++) {
			Advise advise = TechnicalAnalysis.analysisMACD(indicatorList.subList(0, i));
			if (advise.getSignal() != null) {// Advise
				System.out.println(toString(indicatorList.get(i - 1)) + "\t" + advise.toString());
			}
			// Advise obv = TechnicalAnalysis.analysisOBV(indicatorList.subList(0, i));
			// System.out.println(toStringOBV(indicatorList.get(i)) + "\t" + obv.toString()
			// + "\t" + obv.toString());

			// Advise vpt = TechnicalAnalysis.analysisVPT(indicatorList.subList(0, i));
			// System.out.println(indicatorList.get(i).getDate()+ "\t"
			// +indicatorList.get(i).getVpt());
		}
	}

	public List<Indicator> test(String code) throws Exception {
		StockQuote stockQuote = service.get(code);
		if (stockQuote == null) {
			return null;// FIXME Warn
		}
		List<Quote> quoteList = service.get(stockQuote.getCode(), QuoteType.DAILY_QUOTE);

		List<Indicator> indicatorList = Indicator.compute(quoteList);
		Advise advise = TechnicalAnalysis.analysisMACD(indicatorList);
		System.out.println(stockQuote.getName() + "\t" + advise.toString());
		return indicatorList;
	}

	final DecimalFormat decimalFormat = new DecimalFormat("##0.000");

	protected String toString(Indicator indicator) {
		StringBuilder builder = new StringBuilder();
		builder.append("Indicator [date=");
		builder.append(indicator.getDate());
		builder.append(", close=");
		builder.append(indicator.getClose());
		builder.append(", diff=");
		builder.append(decimalFormat.format(indicator.getDiff()));
		builder.append(", dea=");
		builder.append(decimalFormat.format(indicator.getDea()));
		builder.append(", macd=");
		builder.append(decimalFormat.format(indicator.getMacd()));
		builder.append("]");
		return builder.toString();
	}

	protected String toStringOBV(Indicator indicator) {
		StringBuilder builder = new StringBuilder();
		builder.append("Indicator [date=");
		builder.append(indicator.getDate());
		builder.append(", volume=");
		builder.append(indicator.getVolume());
		builder.append(", obv=");
		builder.append(decimalFormat.format(indicator.getObv()));
		builder.append(", obvma=");
		builder.append(decimalFormat.format(indicator.getObvma()));
		builder.append("]");
		return builder.toString();
	}

	protected String toStringVPT(Indicator indicator) {
		StringBuilder builder = new StringBuilder();
		builder.append("Indicator [date=");
		builder.append(indicator.getDate());
		builder.append(", volume=");
		builder.append(indicator.getVolume());
		builder.append(", vpt=");
		builder.append(decimalFormat.format(indicator.getVpt()));
		builder.append("]");
		return builder.toString();
	}
}
