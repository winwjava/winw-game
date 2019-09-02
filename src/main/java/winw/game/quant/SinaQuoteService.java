package winw.game.quant;

import java.util.ArrayList;
import java.util.List;

/**
 * 新浪接口实现。
 * 
 * @author winw
 *
 */
public class SinaQuoteService implements QuoteService {

	// http://hq.sinajs.cn/list=sh600233
	protected String realtimeQuoteUrl = "http://hq.sinajs.cn/list=V_CODE";

	@Override
	public <T extends Quote> T get(Class<T> clazz, String code) throws Exception {
		String response = get(realtimeQuoteUrl.replaceFirst("V_CODE", code));
		String[] fileds = response.split("\"|,");
		if (fileds == null || fileds.length <= 1) {
			return null;
		}
		T quote = clazz.getDeclaredConstructor().newInstance();
		quote.setCode(code);
		quote.setName(fileds[1]);
		quote.setOpen(Double.parseDouble(fileds[2]));
		quote.setPreviousClose(Double.parseDouble(fileds[3]));
		quote.setPrice(Double.parseDouble(fileds[4]));
		quote.setHigh(Double.parseDouble(fileds[5]));
		quote.setLow(Double.parseDouble(fileds[6]));
		quote.setVolume(Long.parseLong(fileds[9]));
		quote.setAmount(Double.parseDouble(fileds[10]));
		quote.setDate(fileds[31]);
		quote.setTime(fileds[32]);
		// http://hq.sinajs.cn/list=[股票代码]
		// 返回结果：JSON实时数据，以逗号隔开相关数据，数据依次是
		// “股票名称、今日开盘价、昨日收盘价、当前价格、今日最高价、今日最低价、竞买价、竞卖价、成交股数、成交金额、
		// 买1手、买1报价、买2手、买2报价、…、买5报价、…、卖5报价、日期、时间”。
		return quote;
	}

	// 获取每日报价数据（前复权）
	protected String dailyQuoteUrl = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=V_CODE&scale=240&ma=no&datalen=V_NUM";

	// http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=sh600233&scale=240&ma=no&datalen=100
	// http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=[市场][股票代码]&scale=[周期]&ma=no&datalen=[长度]
	// 返回结果：获取5、10、30、60分钟JSON数据；day日期、open开盘价、high最高价、low最低价、close收盘价、volume成交量；向前复权的数据。注意，最多只能获取最近的1023个数据节点。
	// 例如，http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=sh600233&scale=60&ma=no&datalen=1023

	@Override
	public <T extends Quote> List<T> get(Class<T> clazz, String code, String from, String to, int num)
			throws Exception {
		String url = dailyQuoteUrl.replace("V_CODE", code).replace("V_NUM", String.valueOf(num));
		return parse(clazz, code, get(url), from, to);
	}

	private <T extends Quote> List<T> parse(Class<T> clazz, String code, String response, String from, String to)
			throws Exception {
		if (response.indexOf("[") == -1) {
			return null;
		}
		String data = response.substring(2, response.length() - 3);
		String[] lines = data.split("\\}\\,\\{");
		List<T> quoteList = new ArrayList<T>();
		for (int i = 0; i < lines.length; i++) {
			String[] fileds = lines[i].replaceAll("\"", "").split("\\,|:");
			if (fileds == null || fileds.length < 6) {
				continue;
			}
			// day:"2019-08-01",open:"13.170",high:"13.200",low:"12.650",close:"12.750",volume:"12621062"
			T quote = clazz.getDeclaredConstructor().newInstance();
			quote.setCode(code);
			quote.setDate(fileds[1]);
			quote.setOpen(Double.parseDouble(fileds[3]));
			quote.setHigh(Double.parseDouble(fileds[5]));
			quote.setLow(Double.parseDouble(fileds[7]));
			quote.setClose(Double.parseDouble(fileds[9]));
			quote.setVolume(Long.parseLong(fileds[11]));

			if (quote.getDate().compareTo(to) > 0) {
				break;
			}
			quoteList.add(quote);
		}
		return quoteList;
	}

	public static void main(String[] args) throws Exception {
		QuoteService quoteService = new SinaQuoteService();
		System.out.println(quoteService.get(Quote.class, "sz000651"));
		System.out.println(quoteService.get(Quote.class, "sh601318"));
		for (Quote quote : quoteService.get(Quote.class, "sh000300", Quote.today(), Quote.today())) {
			System.out.println(quote);
		}
	}

	// http://finance.sina.com.cn/realstock/company/[市场][股票代码]/[复权].js?d=[日期][复权]：qianfuquan-前复权；houfuquan-后复权。返回结果：股票日期的股价JSON数据。
	// 例如，http://finance.sina.com.cn/realstock/company/sz002095/qianfuquan.js?d=2015-06-16，获取深圳市场002095股票的前复权2015-06-16的数据。

	// 方法5：http://market.finance.sina.com.cn/downxls.php?date=[日期]&symbol=[市场][股票代码]
	// 返回数据：XLS文件；股票历史成交明细。
	// 例如，http://market.finance.sina.com.cn/downxls.php?date=2015-06-15&symbol=sz002095
	// 获取2015-06-15日期的深圳市长002095数据。
	// 方法6：http://market.finance.sina.com.cn/pricehis.php?symbol=[市场][股票代码]&startdate=[开始日期]&enddate=[结束日期]
	// 返回数据：HTML文本；指定日期范围内的股票分价表。
	// 例如，http://market.finance.sina.com.cn/pricehis.php?symbol=sh600900&startdate=2011-08-17&enddate=2011-08-19
	// 获取上证600900股票2011-08-17到2011-08-19日期的分价数据。

}
