package winw.game.quant;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SinaQuoteService extends QuoteService {

	@Override
	public Quote get(String code) throws Exception {
		// http://hq.sinajs.cn/list=[股票代码]
		// 返回结果：JSON实时数据，以逗号隔开相关数据，数据依次是
		// “股票名称、今日开盘价、昨日收盘价、当前价格、今日最高价、今日最低价、竞买价、竞卖价、成交股数、成交金额、
		// 买1手、买1报价、买2手、买2报价、…、买5报价、…、卖5报价、日期、时间”。
		return null;
	}

	@Override
	public List<Quote> get(String code, QuotePeriod quotePeriod, String from, String to) throws Exception {
		// http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=[市场][股票代码]&scale=[周期]&ma=no&datalen=[长度]
		// 返回结果：获取5、10、30、60分钟JSON数据；day日期、open开盘价、high最高价、low最低价、close收盘价、volume成交量；向前复权的数据。注意，最多只能获取最近的1023个数据节点。
		// 例如，http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=sz002095&scale=60&ma=no&datalen=1023
		return null;
	}

}
