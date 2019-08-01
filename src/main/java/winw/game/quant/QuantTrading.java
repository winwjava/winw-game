package winw.game.quant;

import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 量化交易。
 * 
 * @author winw
 *
 */
public abstract class QuantTrading {

	public final static String CSI_300 = "sh000300";// 沪深三百
	public final static String SH_BOND_ETF = "sh511010";// 上证国债ETF
	public final static String SH_GOLD_ETF = "sh518880";// 上证黄金ETF
	public final static String CSI_300_ETF = "sh510300";// 沪深三百ETF

	public final static String[] CSI_300_TOP = { // 沪深300十大权重股。市值大、流动性好。
			"sh601318", // 中国平安
			"sh600519", // 贵州茅台
			"sh600036", // 招商银行
			"sh601166", // 格力电器
			"sz000651", // 美的集团
			"sz000333", // 兴业银行
			"sz000858", // 五 粮 液
			"sh600276", // 伊利股份
			"sh600887", // 恒瑞医药
			"sh600030", // 中信证券
	};

	String currentDate;

	protected String getCurrentDate() {
		return currentDate;
	}

	/**
	 * @return 样本代码。
	 */
	public abstract String[] samples();

	/**
	 * 实现具体的交易。
	 * <p>
	 * getHistoryQuote方法可以获取到每个code的历史数据。
	 * 
	 * @param codes
	 */
	public abstract void trading(Portfolio portfolio);

	protected QuoteService quoteService = new TencentQuoteService();

	Map<String, List<QuantQuote>> quoteCache = new HashMap<String, List<QuantQuote>>();

	Map<String, List<QuantQuote>> historyQuote = new HashMap<String, List<QuantQuote>>();

	protected QuantQuote getCurrentQuote(String code) {// 收盘价上记录的是当前实时价格。
		List<QuantQuote> list = getHistoryQuote(code);
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.get(list.size() - 1);
	}

	/**
	 * 当前历史交易数据（从缓存中取）。
	 * 
	 * @param code
	 * @return
	 */
	protected List<QuantQuote> getHistoryQuote(String code) {
		return historyQuote.get(code);// FIXME 如果为空，则考虑用stockQuoteService查询一次。
	}

	protected List<QuantQuote> queryHistoryQuote(String from, String to, String code) throws Exception {
		Quote quoteDetail = quoteService.get(code);
		if (quoteDetail == null) {
			return null;
		}
		List<Quote> list = quoteService.get(code, QuotePeriod.DAILY, from, to);
		if (list == null || list.isEmpty()) {
			return null;
		}

		// 计算技术指标
		return QuantQuote.compute(list);
	}

	double getMarketValue(Collection<Position> positions, String to) throws Exception {
		double marketValue = 0;
		String from1 = addDays(to, -15);// 从to的15天前开始取数据，以获取to当天的收盘价
		for (Position position : positions) {
			List<Quote> temp = quoteService.get(position.getCode(), QuotePeriod.DAILY, from1, to);
			marketValue += position.getSize() * temp.get(temp.size() - 1).getClose();
		}
		return marketValue;
	}

	protected final int observation = -120;

	protected final static String datePattern = "yyyy-MM-dd";

	public String queryFrom(String from) throws ParseException {
		return addDays(from, observation * 7 / 5 - 11);
	}

	public String addDays(String date, int amount) throws ParseException {
		return DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(date, datePattern), amount), datePattern);
	}

	public QuoteService getStockQuoteService() {
		return quoteService;
	}

	public void setStockQuoteService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

}
