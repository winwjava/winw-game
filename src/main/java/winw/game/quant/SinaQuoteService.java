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

	// 分时线的查询：http://image.sinajs.cn/newchart/min/n/sh000001.gif
	// 日K线查询：http://image.sinajs.cn/newchart/daily/n/sh000001.gif
	// 周K线查询：http://image.sinajs.cn/newchart/weekly/n/sh000001.gif
	// 月K线查询：http://image.sinajs.cn/newchart/monthly/n/sh000001.gif

	// http://image.sinajs.cn/newchart/daily/n/sh601006.gif
	protected String dailyImage = "http://image.sinajs.cn/newchart/daily/n/V_CODE.gif";

	public List<Quote> image(String code, QuotePeriod quotePeriod, String from, String to) throws Exception {
		// byte[] bytes = HttpUtils.getBytes();

		JLabel lbl = new JLabel(new ImageIcon(dailyImage.replace("V_CODE", code)));

		JFrame frame = new JFrame("");
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		return null;
	}

	public static void main(String[] args) throws MalformedURLException {
		JFrame frame = new JFrame("");
		frame.setLayout(new FlowLayout());
		frame.add(new JPanel()
				.add(new JLabel(new ImageIcon(new URL("http://image.sinajs.cn/newchart/daily/n/sh601006.gif")))));
		frame.add(new JPanel()
				.add(new JLabel(new ImageIcon(new URL("http://image.sinajs.cn/newchart/daily/n/sh000001.gif")))));
		frame.add(new JPanel()
				.add(new JLabel(new ImageIcon(new URL("http://image.sinajs.cn/newchart/daily/n/sh000300.gif")))));
		frame.add(new JPanel()
				.add(new JLabel(new ImageIcon(new URL("http://image.sinajs.cn/newchart/daily/n/sz000333.gif")))));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
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
