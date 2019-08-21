package winw.game.quant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class QuantTradingBase {

	public final static String CSI_300 = "sh000300";// 沪深三百
	public final static String SH_BOND = "sh000012";// 上证国债
	public final static String CSI_300_ETF = "sh510300";// 沪深三百ETF
	public final static String SH_BOND_ETF = "sh511010";// 上证国债ETF
	public final static String SH_GOLD_ETF = "sh518880";// 上证黄金ETF

	public final static String[] CSI_300_TOP = { // 沪深300十大权重股。市值大、流动性好。
			// 中国平安// 贵州茅台// 招商银行// 格力电器// 美的集团
			"sh601318", "sh600519", "sh600036", "sh601166", "sz000651",
			// 兴业银行// 五 粮 液// 伊利股份// 恒瑞医药// 中信证券
			"sz000333", "sz000858", "sh600276", "sh600887", "sh600030" };

	protected String currentDate;

	protected String getCurrentDate() {
		return currentDate;
	}

	protected QuoteService quoteService = QuoteService.getDefault();

	protected Map<String, List<QuoteIndex>> quoteCache = new HashMap<String, List<QuoteIndex>>();
	// 当前时间的历史数据。
	protected Map<String, List<QuoteIndex>> historyQuote = new HashMap<String, List<QuoteIndex>>();

	public List<QuoteIndex> compute(List<QuoteIndex> list) {
		QuoteIndex.computeMA(list);
		QuoteIndex.computeEMA(list);
		QuoteIndex.computeMACD(list);
		return list;
	}

	public void cache(String[] codes, String from, String to) throws Exception {
		String today = Quote.today();
		for (String temp : codes) {
			historyQuote.put(temp, queryHistoryQuote(from, to, temp));
			List<QuoteIndex> tempQuotes = getHistoryQuote(temp);
			String lastday = tempQuotes.get(tempQuotes.size() - 1).getDate();
			if (!today.equals(lastday)) {
				System.err.println("last quote(" + lastday + ") is not today!!!");
			}
		}
	}

	public QuoteIndex getCurrentQuote(String code) {// 收盘价上记录的是当前实时价格。
		List<QuoteIndex> list = getHistoryQuote(code);
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
	public List<QuoteIndex> getHistoryQuote(String code) {
		return historyQuote.get(code);// 如果为空，则考虑用stockQuoteService查询一次。
	}

	public List<QuoteIndex> queryHistoryQuote(String from, String to, String code) throws Exception {
		Quote quoteDetail = quoteService.get(QuoteIndex.class, code);
		if (quoteDetail == null) {
			return null;
		}
		List<QuoteIndex> list = quoteService.get(QuoteIndex.class, code, from, to);
		if (list == null || list.isEmpty()) {
			return null;
		}

		// 计算技术指标
		return compute(list);
	}

	public void updateHistoryQuote() {// 根据样本，及时同步。
		for (String code : quoteCache.keySet()) {
			List<QuoteIndex> list = quoteCache.get(code);
			if (list == null) {
				continue;
			}
			for (int i = 0; i < list.size(); i++) {
				String date = list.get(i).getDate();
				if (date.compareTo(currentDate) <= 0) {
					continue;
				}
				historyQuote.put(code, list.subList(0, i));
				break;
			}
		}
	}

	public Map<String, List<QuoteIndex>> getQuoteCache() {
		return quoteCache;
	}

}
