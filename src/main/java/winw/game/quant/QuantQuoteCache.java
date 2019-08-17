package winw.game.quant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuantQuoteCache {

	protected String currentDate;

	protected String getCurrentDate() {
		return currentDate;
	}

	protected QuoteService quoteService = QuoteService.getDefault();

	protected Map<String, List<QuantQuote>> quoteCache = new HashMap<String, List<QuantQuote>>();

	protected Map<String, List<QuantQuote>> historyQuote = new HashMap<String, List<QuantQuote>>();

	public List<QuantQuote> compute(List<QuantQuote> list) {
		QuantQuote.computeMA(list);
		QuantQuote.computeEMA(list);
		QuantQuote.computeMACD(list);
		return list;
	}

	public void cache(String[] codes, String from, String to) throws Exception {
		String today = Quote.today();
		for (String temp : codes) {
			historyQuote.put(temp, queryHistoryQuote(from, to, temp));
			List<QuantQuote> tempQuotes = getHistoryQuote(temp);
			String lastday = tempQuotes.get(tempQuotes.size() - 1).getDate();
			if (!today.equals(lastday)) {
				System.err.println("last quote(" + lastday + ") is not today!!!");
			}
		}
	}

	public QuantQuote getCurrentQuote(String code) {// 收盘价上记录的是当前实时价格。
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
	public List<QuantQuote> getHistoryQuote(String code) {
		return historyQuote.get(code);// 如果为空，则考虑用stockQuoteService查询一次。
	}

	public List<QuantQuote> queryHistoryQuote(String from, String to, String code) throws Exception {
		Quote quoteDetail = quoteService.get(QuantQuote.class, code);
		if (quoteDetail == null) {
			return null;
		}
		List<QuantQuote> list = quoteService.get(QuantQuote.class, code, from, to);
		if (list == null || list.isEmpty()) {
			return null;
		}

		// 计算技术指标
		return compute(list);
	}

	public void updateHistoryQuote() {// 根据样本，及时同步。
		for (String code : quoteCache.keySet()) {
			List<QuantQuote> list = quoteCache.get(code);
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

	public Map<String, List<QuantQuote>> getQuoteCache() {
		return quoteCache;
	}

}
