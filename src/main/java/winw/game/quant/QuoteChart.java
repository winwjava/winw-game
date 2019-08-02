package winw.game.quant;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.commons.lang3.time.DateFormatUtils;

public class QuoteChart extends JPanel {
	private static final long serialVersionUID = 1L;

	private int width = 480;
	private int height = 320;

	private int fontH = 0;

	private int viewFrom = 0;
	private int viewLength = 75;
	private List<QuantQuote> quoteList;

	private double maxPrice, minPrice, maxVolume, priceRatio, volumeRatio;

	private float masterX, masterY, bottomH, masterW, deputyH, middleH, masterH, deputyY, quoteW;

	private Color riseColor = new Color(0xffFF5442, true);
	private Color fallColor = new Color(0xff2BB8AB, true);

	private Color ma5Color = Color.BLACK;
	private Color ma10Color = Color.BLUE;
	private Color ma60Color = new Color(0xffFF45A1, true);

	private List<Point2D> ma5Points = new ArrayList<Point2D>();
	private List<Point2D> ma10Points = new ArrayList<Point2D>();
	private List<Point2D> ma60Points = new ArrayList<Point2D>();

	private List<Point2D> v5Points = new ArrayList<Point2D>();
	private List<Point2D> v10Points = new ArrayList<Point2D>();

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		reset();
		g.setFont(new Font(null, 0, 11));
		fontH = g.getFontMetrics().getHeight();

		drawTickMark((Graphics2D) g);// 刻度
		drawQuoteList((Graphics2D) g);// 指标
		drawTextLabels((Graphics2D) g);// 标签
	}

	private void reset() {
		ma5Points.clear();
		ma10Points.clear();
		ma60Points.clear();
		v5Points.clear();
		v10Points.clear();

		maxPrice = 0;
		maxVolume = 0;

		width = getWidth() - 1;
		height = getHeight() - 1;
		masterX = width / 40;// 左边空白
		masterY = height / 10;// 顶部空白，放交易代码及其他信息
		bottomH = masterX;// 底部高，等于左边空白宽
		masterW = width - masterX - width / 20; // 主图宽度，占9/10，右边留空占1/10，放刻度

		deputyH = (height - masterY - bottomH) / 3; // 辅图的高度，占1/4
		middleH = (height - masterY - bottomH) / 12; // 主图与副图中间，放时间戳
		masterH = height - masterY - bottomH - deputyH - middleH; // 价格图的高度，占1/2
		deputyY = masterY + masterH + middleH;// 辅图
		quoteW = (masterW - viewLength) / viewLength;
	}

	// 刻度线
	private void drawTickMark(Graphics2D g) {
		g.setColor(Color.GRAY);
		// g.drawRect(0, 0, width, height);
		drawRect(g, masterX, masterY, masterW, masterH);
		drawRect(g, masterX, deputyY, masterW, deputyH);
		// 虚线
		g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, new float[] { 1.4f, 1.4f },
				0.0f));
		// 水平刻度线
		for (int i = 1; i < 7; i++) {// 主图
			drawLine(g, masterX, masterY + masterH / 7 * i, masterX + masterW, masterY + masterH / 7 * i);
		}
		for (int i = 1; i < 5; i++) {// 副图
			drawLine(g, masterX, deputyY + deputyH / 5 * i, masterX + masterW, deputyY + deputyH / 5 * i);
		}
		// 垂直刻度线
		g.setStroke(
				new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, new float[] { 4, 4 }, 0.0f)); // 设置新的画刷
		// 每隔10个画一个
		for (int i = 10; i < viewLength; i += 10) {
			double lineX = masterX + (quoteW + 1) * i;
			// 主图
			drawLine(g, lineX, masterY, lineX, masterY + masterH);
			// 副图
			drawLine(g, lineX, deputyY, lineX, height - bottomH);
		}
		g.setStroke(new BasicStroke());// 还原
	}

	// 主副图蜡烛图
	private void drawQuoteList(Graphics2D g) {
		minPrice = quoteList.get(viewFrom).getLow();
		for (int i = viewFrom; i < viewFrom + viewLength; i++) {
			maxPrice = Math.max(maxPrice, quoteList.get(i).getHigh());
			maxPrice = Math.max(maxPrice, quoteList.get(i).getMa60());
			minPrice = Math.min(minPrice, quoteList.get(i).getLow());
			minPrice = Math.min(minPrice, quoteList.get(i).getMa60());
			maxVolume = Math.max(maxVolume, quoteList.get(i).getVolume());
			maxVolume = Math.max(maxVolume, quoteList.get(i).getVolumeMa5());
			maxVolume = Math.max(maxVolume, quoteList.get(i).getVolumeMa10());
		}
		double diffPrice = maxPrice - minPrice;
		maxPrice = maxPrice + diffPrice * 0.07;
		minPrice = minPrice - diffPrice * 0.07;
		maxVolume = maxVolume + maxVolume * 0.14;
		volumeRatio = deputyH / maxVolume;// 成交与高度的比例
		priceRatio = masterH / (maxPrice - minPrice);// 价格与高度的比例

		for (int i = viewFrom; i < quoteList.size(); i++) {
			double openPrice = quoteList.get(i).getOpen();
			double closedPrice = quoteList.get(i).getClose();
			double higherPrice;
			double lowerPrice;
			if (openPrice >= closedPrice) {
				higherPrice = openPrice;
				lowerPrice = closedPrice;
				g.setColor(fallColor);
			} else {
				higherPrice = closedPrice;
				lowerPrice = openPrice;
				g.setColor(riseColor);
			}
			// 线本身占1个像素
			double rectX = masterX + (quoteW + 1) * (i - viewFrom) + 1;
			double lineX = masterX + (quoteW + 1) * (i - viewFrom) + quoteW / 2 - 0.5 + 1;
			double baseY = masterY + masterH;
			// K线，开盘价收盘价
			fillRect(g, rectX, baseY - (higherPrice - minPrice) * priceRatio, quoteW,
					(higherPrice - lowerPrice) * priceRatio);
			// K线，最高价最低价
			drawLine(g, lineX, baseY - (quoteList.get(i).getHigh() - minPrice) * priceRatio, lineX,
					baseY - (quoteList.get(i).getLow() - minPrice) * priceRatio);
			ma5Points.add(new Point2D.Double(lineX, baseY - (quoteList.get(i).getMa5() - minPrice) * priceRatio));
			ma10Points.add(new Point2D.Double(lineX, baseY - (quoteList.get(i).getMa10() - minPrice) * priceRatio));
			ma60Points.add(new Point2D.Double(lineX, baseY - (quoteList.get(i).getMa60() - minPrice) * priceRatio));
			// 成交量
			baseY = height - bottomH;
			fillRect(g, rectX, baseY - quoteList.get(i).getVolume() * volumeRatio, quoteW,
					quoteList.get(i).getVolume() * volumeRatio);
			v5Points.add(new Point2D.Double(lineX, baseY - quoteList.get(i).getVolumeMa5() * volumeRatio));
			v10Points.add(new Point2D.Double(lineX, baseY - quoteList.get(i).getVolumeMa10() * volumeRatio));
		}
		// 均线，贝塞尔曲线
		drawBezierCurve(g, ma5Points, ma5Color);
		drawBezierCurve(g, ma10Points, ma10Color);
		drawBezierCurve(g, ma60Points, ma60Color);
		drawBezierCurve(g, v5Points, ma5Color);
		drawBezierCurve(g, v10Points, ma10Color);
	}

	private static final float BEZIER_RATIO = 0.16f;

	private void drawBezierCurve(Graphics2D g, List<Point2D> points, Color color) {
		Double path = new Path2D.Double();
		path.moveTo(points.get(0).getX(), points.get(0).getY());
		Point leftPoint = new Point();
		Point rightPoint = new Point();
		for (int i = 0; i < points.size() - 1; i++) {
			if (i == 0 && points.size() > 2) {
				leftPoint.setLocation(
						points.get(i).getX() + BEZIER_RATIO * (points.get(i + 1).getX() - points.get(0).getX()),
						points.get(i).getY() + BEZIER_RATIO * (points.get(i + 1).getY() - points.get(0).getY()));
				rightPoint.setLocation(
						points.get(i + 1).getX() - BEZIER_RATIO * (points.get(i + 2).getX() - points.get(i).getX()),
						points.get(i + 1).getY() - BEZIER_RATIO * (points.get(i + 2).getY() - points.get(i).getY()));
			} else if (i == points.size() - 2 && i > 1) {
				leftPoint.setLocation(
						points.get(i).getX() + BEZIER_RATIO * (points.get(i + 1).getX() - points.get(i - 1).getX()),
						points.get(i).getY() + BEZIER_RATIO * (points.get(i + 1).getY() - points.get(i - 1).getY()));
				rightPoint.setLocation(
						points.get(i + 1).getX() - BEZIER_RATIO * (points.get(i + 1).getX() - points.get(i).getX()),
						points.get(i + 1).getY() - BEZIER_RATIO * (points.get(i + 1).getY() - points.get(i).getY()));
			} else if (i > 0 && i < points.size() - 2) {
				leftPoint.setLocation(
						points.get(i).getX() + BEZIER_RATIO * (points.get(i + 1).getX() - points.get(i - 1).getX()),
						points.get(i).getY() + BEZIER_RATIO * (points.get(i + 1).getY() - points.get(i - 1).getY()));
				rightPoint.setLocation(
						points.get(i + 1).getX() - BEZIER_RATIO * (points.get(i + 2).getX() - points.get(i).getX()),
						points.get(i + 1).getY() - BEZIER_RATIO * (points.get(i + 2).getY() - points.get(i).getY()));
			}
			path.curveTo(leftPoint.getX(), leftPoint.getY(), rightPoint.getX(), rightPoint.getY(),
					points.get(i + 1).getX(), points.get(i + 1).getY());
		}
		g.setColor(color);
		g.draw(path);
	}

	// 头部写code
	private String header = "code Daily from .. to ..";
	// 底部写策略名，及收益。
	private String footer = "Daily from .. to ..";

	private Map<String, Order> orders = new HashMap<>();

	private void drawTextLabels(Graphics2D g) {
		QuantQuote lastView = quoteList.get(viewFrom + viewLength - 1);
		String ma5 = "MA5:" + format(lastView.getMa5());
		String ma10 = "MA10:" + format(lastView.getMa10());
		String ma60 = "MA60:" + format(lastView.getMa60());

		int ma5Wight = g.getFontMetrics().stringWidth(ma5);
		int ma10Wight = g.getFontMetrics().stringWidth(ma10);

		g.setColor(ma5Color);
		g.drawString(ma5, masterX + 3, masterY + fontH * 0.8f);
		g.setColor(ma10Color);
		g.drawString(ma10, masterX + 3 + ma5Wight * 1.1f, masterY + fontH * 0.8f);
		g.setColor(ma60Color);
		g.drawString(ma60, masterX + 3 + (ma5Wight + ma10Wight) * 1.1f, masterY + fontH * 0.8f);

		String v5 = "MA5:" + format(lastView.getVolumeMa5());
		String v10 = "MA10:" + format(lastView.getVolumeMa10());
		int ma5Width = g.getFontMetrics().stringWidth(v5);
		g.setColor(ma5Color);
		g.drawString(v5, masterX + 3, deputyY + fontH * 0.8f);
		g.setColor(ma10Color);
		g.drawString(v10, masterX + 3 + ma5Width * 1.1f, deputyY + fontH * 0.8f);

		// 在右上角写收盘价和成交量。颜色用红色或绿色。
		// g.setColor(lastView.getClose() > lastView.getOpen() ? riseColor : fallColor);
		// String price = "Price:" + format(lastView.getClose());
		// int priceWidth = g.getFontMetrics().stringWidth(price);
		// g.drawString(price, masterX + masterW - priceWidth, masterY + fontH * 0.8f);
		// String vol = "Volume:" + format(lastView.getVolume());
		// int volWidth = g.getFontMetrics().stringWidth(vol);
		// g.drawString(vol, masterX + masterW - volWidth, deputyY + fontH * 0.8f);

		// 右边写刻度
		g.setColor(Color.GRAY);
		double scale = (maxPrice - minPrice) / 7;
		for (int i = 0; i <= 7; i++) {
			g.drawString(format(minPrice + scale * i), masterX + masterW + 3,
					(float) (masterY + masterH - scale * i * priceRatio + fontH * 0.25));
		}
		scale = maxVolume / 5;
		for (int i = 0; i <= 5; i++) {
			g.drawString(format(0 + scale * i), masterX + masterW + 3,
					(float) (height - bottomH - scale * i * volumeRatio + fontH * 0.25));
		}
		// 中间写日期，每隔10天写一个
		String year = "1000-";
		g.setColor(Color.BLUE);
		for (int i = 10; i < viewLength; i += 10) {
			String timestamp = quoteList.get(i).getDate();
			if (timestamp.startsWith(year)) {
				timestamp = timestamp.substring(5);
			} else {
				year = timestamp.substring(0, 5);
			}
			int timestampW = g.getFontMetrics().stringWidth(timestamp);
			g.drawString(timestamp, masterX + (quoteW + 1) * i - timestampW / 2, deputyY - middleH / 3);
		}
		// 买卖记录。
		for (int i = viewFrom; i < viewLength; i++) {
			QuantQuote quote = quoteList.get(i);
			Order order = orders.get(quote.getDate());
			if (order == null) {
				continue;
			}
			// 在最低价下面画 + 或 -
			double rectX = masterX + (quoteW + 1) * (i - viewFrom) + 1;
			double lineX = masterX + (quoteW + 1) * (i - viewFrom) + quoteW / 2 - 0.5 + 1;
			double rectY = (masterY + masterH - (quote.getLow() - minPrice) * priceRatio) + quoteW - 1;
			g.setColor(Color.BLUE);
			g.setFont(new Font(null, 0, 11));
			g.drawString(String.valueOf(order.getPrice()), (float) rectX,
					(float) (masterY + masterH - (quote.getHigh() - minPrice) * priceRatio) - fontH);
			fillRect(g, rectX - 1, rectY - 1, quoteW + 3, quoteW + 3);

			g.setColor(Color.WHITE);
			// 横杠
			drawLine(g, rectX, rectY + quoteW / 2, rectX + quoteW, rectY + quoteW / 2);
			if (order.getSize() > 0) {// 竖杠
				drawLine(g, lineX, rectY, lineX, rectY + quoteW);
			}
		}

		// 头部和底部
		g.setColor(Color.BLACK);
		g.setFont(new Font(null, Font.BOLD, 12));
		g.drawString(footer, masterX + 3, height - fontH * 0.2f);
		g.drawString(header, masterX + 3, masterY - fontH * 0.2f);
		String date = quoteList.get(viewFrom + viewLength - 1).getDate();
		int dateW = g.getFontMetrics().stringWidth(date);
		g.drawString(date, masterX + masterW - dateW, masterY - fontH * 0.2f);
	}

	private void drawRect(Graphics2D g, double x, double y, double w, double h) {
		g.draw(new Rectangle2D.Double(x, y, w, h));
	}

	private void fillRect(Graphics2D g, double x, double y, double w, double h) {
		if (h < 1) {
			h = 1;
		}
		g.fill(new Rectangle2D.Double(x, y, w, h));
	}

	private void drawLine(Graphics2D g, double x1, double y1, double x2, double y2) {
		Double line = new Path2D.Double();
		line.moveTo(x1, y1);
		line.lineTo(x2, y2);
		g.draw(line);
	}

	private String format(double num, int scale) {
		BigDecimal bigDecimal = new BigDecimal(num);
		return bigDecimal.setScale(scale, BigDecimal.ROUND_DOWN).toPlainString();
	}

	/**
	 * 按量级格式化价格
	 */
	private String format(double num) {
		if (num == 0) {
			return "0";
		} else if (num < 1) {
			return format(num, 3);
		} else if (num < 100) {
			return format(num, 2);
		} else if (num < 10000) {
			return format(num, 0);
		} else if (num < 100000000) {
			return format(num / 1000, 2) + "K";
		} else {
			return format(num / 1000000, 2) + "M";
		}
	}

	public QuoteChart(List<QuantQuote> quoteList, int viewFrom, int viewLength, String header, String footer,
			List<Order> orderList) {
		super();
		this.viewFrom = viewFrom;
		this.viewLength = viewLength;
		this.quoteList = quoteList;
		this.header = header;
		this.footer = footer;
		if (orderList != null) {
			for (Order order : orderList) {
				this.orders.put(order.getDate(), order);
			}
		}
	}

	public QuoteChart(List<QuantQuote> quoteList, String from, String to, String header, String footer,
			List<Order> orderList) {
		this(quoteList, 0, 0, header, footer, orderList);

		for (int i = 0; i < quoteList.size(); i++) {
			if (viewFrom < 0 && quoteList.get(i).getDate().compareTo(from) >= 0) {
				viewFrom = i;
				System.out.println("from " + viewFrom);
			}
			if (quoteList.get(i).getDate().compareTo(to) <= 0) {
				viewLength = i - viewFrom + 1;
			}
		}
	}

	public static void show(List<QuoteChart> views) {
		// TODO Logo W
		// TODO 幅图支持显示MACD，z-score
		JFrame frame = new JFrame("winw-game");
		frame.setVisible(true);
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		for (QuoteChart quoteChart : views) {
			quoteChart.setPreferredSize(new Dimension(2500, 320));
			// quoteChart.setBorder(BorderFactory.createLineBorder(Color.WHITE));
			container.add(quoteChart);
		}
		frame.add(new JScrollPane(container));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void show(Portfolio portfolio, String from, String to) throws Exception {
		Map<String, List<Order>> orders = new HashMap<String, List<Order>>();
		for (Order order : portfolio.getOrderList()) {
			if (!orders.containsKey(order.getCode())) {
				orders.put(order.getCode(), new ArrayList<>());
			}
			orders.get(order.getCode()).add(order);
		}

		List<QuoteChart> charts = new ArrayList<>();
		QuoteService service = new TencentQuoteService();
		for (String code : orders.keySet()) {
			List<QuantQuote> quotes = QuantQuote.compute(service.get(code, QuotePeriod.DAILY, from, to));
			charts.add(new QuoteChart(quotes, from, to, code + " Daily", "", orders.get(code)));
		}
		show(charts);
	}

	private static QuoteChart newChart(String code) throws Exception {
		QuoteService service = new TencentQuoteService();
		String today = DateFormatUtils.format(new Date(), QuoteService.DATE_PATTERN);
		List<Quote> dailyQuote = service.get(code, QuotePeriod.DAILY, "2018-06-01", today);
		QuoteChart chart = new QuoteChart(QuantQuote.compute(dailyQuote), 70, dailyQuote.size() - 70, code + " Daily",
				"", null);
		chart.setLayout(new FlowLayout());
		return chart;
	}

	public static void main(String[] args) throws Exception {
		show(Arrays.asList(newChart("sh000001"), newChart("sh000300"), newChart("sz000333"), newChart("sz002352")));
	}

}