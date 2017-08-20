package winw.game.stock;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import winw.game.stock.util.HttpUtils;

/**
 * @author winw
 *
 */
public class TencentStockQuoteService implements StockQuoteService {

	protected String realtimeQuoteUrl = "http://qt.gtimg.cn/q=V_CODE";

	// 获取前复权的数据
	protected String dailyQuoteUrl = "http://web.ifzq.gtimg.cn/appstock/app/fqkline/get?_var=kline_dayqfq&param=V_CODE,day,,,120,qfq";

	// protected String historicalQuoteUrl =
	// "http://data.gtimg.cn/flashdata/hushen/latest/daily/sh600233.js";

	// http://qt.gtimg.cn/q=s_sh600233

	// 下面的接口是不复权接口
	// http://data.gtimg.cn/flashdata/hushen/latest/daily/sh600233.js
	//
	// http://data.gtimg.cn/flashdata/hushen/daily/17/sh600233.js
	// http://data.gtimg.cn/flashdata/hushen/weekly/sh600233.js

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	public StockQuote get(String code) throws IOException, ParseException {
		String response = HttpUtils.get(realtimeQuoteUrl.replaceFirst("V_CODE", code));
		String[] split = response.split("~");

		StockQuote quote = new StockQuote();

		// v_sh600233="1~圆通速递~600233~18.07~18.11~18.10~17984~7724~10260~18.07~158~18.06~703~18.05~453~18.04~552~18.03~426~18.08~210~18.09~61~18.10~414~18.11~15~18.12~111~15:00:02/18.07/5/S/9035/23371|15:00:00/18.07/1/S/1807/23368|14:59:57/18.07/1/S/1807/23364|14:59:50/18.07/17/S/30724/23355|14:59:44/18.07/7/S/12655/23349|14:59:44/18.07/1/S/1807/23346~20170804150557~-0.04~-0.22~18.14~18.03~18.07/17978/32487500~17984~3250~0.54~31.11~~18.14~18.03~0.61~59.63~509.80~6.01~19.92~16.30~0.83";
		// 看返回数据是以 ~ 分割字符串中内容，下标从0开始，依次为
		// 0: 未知
		// 1: 股票名字
		quote.setName(split[1]);
		// 2: 股票代码
		quote.setCode(code);
		// 3: 当前价格
		quote.setPrice(Double.parseDouble(split[3]));
		// 4: 昨收
		quote.setPreviousClose(Double.parseDouble(split[4]));
		// 5: 今开
		quote.setOpen(Double.parseDouble(split[5]));
		// 6: 成交量（手）
		quote.setVolume(Integer.parseInt(split[6]));
		// 7: 外盘
		// 8: 内盘
		// 9: 买一
		// 10: 买一量（手）
		// 11-18: 买二 买五
		// 19: 卖一
		// 20: 卖一量
		// 21-28: 卖二 卖五
		// 29: 最近逐笔成交
		// 30: 时间
		// 20170804150557
		quote.setTime(dateFormat.parse(split[30]));
		// 31: 涨跌
		// 32: 涨跌%
		// 33: 最高
		// 34: 最低
		// 35: 价格/成交量（手）/成交额
		// 36: 成交量（手）
		// 37: 成交额（万）
		// 38: 换手率
		// 39: 市盈率
		// TODO 市盈率
		// quote.setPe(Double.parseDouble(split[39]));
		// 40:
		// 41: 最高
		quote.setHigh(Double.parseDouble(split[41]));
		// 42: 最低
		quote.setLow(Double.parseDouble(split[42]));
		// 43: 振幅
		// 44: 流通市值
		// 45: 总市值
		// 46: 市净率
		// 47: 涨停价
		// 48: 跌停价
		return quote;
	}

	public List<StockQuote> get(String code, QuoteType quoteType) throws IOException {
		String response = HttpUtils.get(dailyQuoteUrl.replaceFirst("V_CODE", code));
		// latest_daily_data="\n\
		// num:100 total:4024 start:000608 00:140 01:237 02:236 03:240 04:242
		// 05:218 06:240 07:240 08:245 09:243 10:241 11:242 12:242 13:238 14:245
		// 15:236 16:155 17:144\n\
		// 170804 18.10 18.07 18.14 18.03 17984\n\
		// ";

		// date open close high low volume
		String data = response.substring(response.indexOf("qfqday") + 10, response.indexOf("\"]],"));
		String[] lines = data.split("\\]\\,\\[");// "],["

		List<StockQuote> quoteList = new ArrayList<StockQuote>();
		for (int i = 2; i < lines.length; i++) {
			String[] fileds = lines[i].replaceAll("\"", "").split("\\,");
			if (fileds == null || fileds.length < 6) {
				continue;
			}

			StockQuote quote = new StockQuote();
			quote.setDate(fileds[0]);
			quote.setOpen(Double.parseDouble(fileds[1]));
			quote.setClose(Double.parseDouble(fileds[2]));
			quote.setHigh(Double.parseDouble(fileds[3]));
			quote.setLow(Double.parseDouble(fileds[4]));
			quote.setVolume((int) Double.parseDouble(fileds[5]));
			quoteList.add(quote);
		}
		return quoteList;
	}

}
