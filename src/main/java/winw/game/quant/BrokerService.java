package winw.game.quant;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.PreDestroy;

import winw.game.QuantConfig;

/**
 * 券商接口。用 <tt>Robot</tt> 模拟操作同花顺实现。
 * <p>
 * 只支持一个交易账户。
 * <p>
 * 参考https://github.com/nladuo/THSTrader实现（Python控制能力更强）。
 * 
 * <p>
 * 另外有代理服务器实现：https://github.com/928675268/alphaquant.git和开源实现：https://github.com/waditu/tushare.git，
 * 但可靠性很低，且难以维护更新。
 * 
 * @author winw
 *
 */
@ManagedBean("generalBrokerService")
public class BrokerService {

	private Robot robot;
	protected Process client;

	private static Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

	public BrokerService() {
		super();
	}

	private void startBrokerClient(QuantConfig config) throws AWTException, IOException, UnsupportedFlavorException {
		if (config.getBroker() == null || config.getBroker().isEmpty()) {
			return;
		}
		robot = new Robot();
		robot.setAutoDelay(50);
		for (int i = 0; i <= 5; i++) {
			key(KeyEvent.VK_ALT, KeyEvent.VK_SPACE, KeyEvent.VK_N);
		}
		// click(dimension.width, dimension.height);
		client = Runtime.getRuntime().exec(config.getBrokerExe());
		for (long cpuDuration = 0, lastDuration = -100; cpuDuration - lastDuration >= 2000 * 5 / 100;) {
			robot.delay(2000);
			lastDuration = cpuDuration;
			cpuDuration = ProcessHandle.of(client.pid()).get().info().totalCpuDuration().get().toMillis();
		}

		key(KeyEvent.VK_ESCAPE);
		key(KeyEvent.VK_ALT, KeyEvent.VK_SPACE, KeyEvent.VK_X);
		key(KeyEvent.VK_ESCAPE);
		robot.delay(300);
		// Login
		// 自动申购新股。
	}

	@PreDestroy
	public void destroy() {
		if (client != null && client.isAlive()) {
			client.destroy();
		}
	}

	public Portfolio getPortfolio(QuantConfig config) throws Exception {
		startBrokerClient(config);

		Portfolio portfolio = new Portfolio(config.getPortfolio(), 300000, config.getMaxPosition(),
				config.getDrawdownLimit(), config.getStoplossLimit());
		Double balance = getBalance(portfolio);
		if (balance == null) {// 重新启动
			destroy();
			return getPortfolio(config);
		}
		portfolio.setCash(balance);

		// 根据初始资金和持仓计算资金余额。
		// 前一交易日结算后的余额
		portfolio.getPositions().putAll(getPositions());
		return portfolio;
	}

	public void delegate(Portfolio portfolio, Order order) {
		key(KeyEvent.VK_F4);// 还原F1和F2的光标位置
		key(order.getSize() > 0 ? KeyEvent.VK_F1 : KeyEvent.VK_F2);

		key(delPrefix(order.getCode()));
		key(KeyEvent.VK_TAB);

		robot.delay(100);
		key(KeyEvent.VK_BACK_SPACE, 10);// 删除自动输入的价格
		key(String.valueOf(order.getCurrentPrice()));
		key(KeyEvent.VK_TAB);

		key(String.valueOf(Math.abs(order.getSize())));
		key(KeyEvent.VK_TAB);

		for (int i = 0; i < 5; i++) {// 确保提交。
			key(KeyEvent.VK_ENTER);
			robot.delay(100);
		}
	}

	/**
	 * 全部撤单。
	 * 
	 * @param portfolio
	 */
	public void withdrawal(Portfolio portfolio) {
		key(KeyEvent.VK_F1);
		key(KeyEvent.VK_SLASH);
	}

	/**
	 * 当日交易记录。
	 * <p>
	 * 注意：佣金无法获取。
	 * 
	 * @throws IOException
	 * @throws UnsupportedFlavorException
	 */
	public List<Order> getTradings(Portfolio portfolio) throws Exception {
		key(KeyEvent.VK_F4);
		click(100, dimension.height - 100);
		key(KeyEvent.VK_DOWN);
		robot.delay(500);
		key(KeyEvent.VK_DOWN);
		key(KeyEvent.VK_DOWN);
		keyWithCtrl(KeyEvent.VK_C);
		String tradings = getClipboardString();
		if (!tradings.startsWith("成交时间")) {
			return null;
		}
		List<Order> result = new ArrayList<Order>();
		for (String line : tradings.split("\n")) {
			if (line.startsWith("成交时间")) {
				continue;
			}
			String[] fileds = line.split("\t");
			Order order = new Order();
			order.setTime(fileds[0]);
			order.setCode(addPrefix(fileds[1]));
			order.setSize(("证券卖出".equals(fileds[3]) ? -1 : 1) * Integer.valueOf(fileds[4]));
			order.setCurrentPrice(Double.valueOf(fileds[5]));

			order.setCommission(portfolio.commission(order.getCurrentPrice() * order.getSize()));
			order.setAmount(new BigDecimal(order.getCurrentPrice() * order.getSize())
					.add(new BigDecimal(order.getCommission())).setScale(2, RoundingMode.DOWN).doubleValue());
			result.add(order);
		}
		return result;
	}

	/**
	 * 前一交易日结算后的余额。
	 * 
	 * @return
	 * @throws Exception
	 */
	private Double getBalance(Portfolio portfolio) throws Exception {
		key(KeyEvent.VK_F4);
		for (int i = 0; i <= 5; i++) {
			click(100, dimension.height - 100);
			key(KeyEvent.VK_DOWN);
		}
		robot.delay(500);
		key(KeyEvent.VK_DOWN);
		key(KeyEvent.VK_DOWN);
		keyWithCtrl(KeyEvent.VK_C);
		String lastday = getClipboardString();

		if (lastday == null || !lastday.startsWith("成交日期")) {
			return null;
		}
		String[] lines = lastday.split("\n");
		String[] fileds = lines[lines.length - 1].split("\t");

		Double balance = Double.valueOf(fileds[11]);
		for (Order order : getTradings(portfolio)) {// 假设没有委托。
			balance -= order.getAmount();
		}
		return (double) balance.intValue();
	}

	/**
	 * 当前持仓。
	 * 
	 * @return
	 * @throws UnsupportedFlavorException
	 * @throws IOException
	 */
	private Map<String, Position> getPositions() throws UnsupportedFlavorException, IOException {
		key(KeyEvent.VK_F4);
		key(KeyEvent.VK_DOWN);
		key(KeyEvent.VK_DOWN);
		keyWithCtrl(KeyEvent.VK_C);
		String positions = getClipboardString();
		if (!positions.startsWith("明细")) {
			return null;
		}
		HashMap<String, Position> results = new HashMap<String, Position>();
		for (String line : positions.split("\n")) {
			if (line.startsWith("明细")) {
				continue;
			}
			String[] fileds = line.split("\t");
			Position position = new Position();
			position.setCode(addPrefix(fileds[1]));
			position.setName(fileds[2]);
			position.setSize(Integer.valueOf(fileds[3]));
			position.setSellable(Integer.valueOf(fileds[4]));
			position.setHoldingPrice(Double.valueOf(fileds[5]));
			position.setCurrentPrice(Double.valueOf(fileds[6]));
			// position.setWeight(Double.valueOf(fileds[11]) * 0.01);
			results.put(position.getCode(), position);
		}
		return results;
	}

	protected void click(int x, int y) {
		robot.mouseMove(x, y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	protected void key(int key) {
		robot.keyPress(key);
		robot.keyRelease(key);
	}

	protected void key(int key, int num) {
		robot.setAutoDelay(1);
		for (int i = 0; i < num; i++) {
			robot.keyPress(key);
			robot.keyRelease(key);
		}
		robot.setAutoDelay(20);
	}

	protected void keyWithCtrl(int key) {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(key);
		robot.keyRelease(key);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}

	protected void key(int... keys) {
		for (int key : keys) {
			robot.keyPress(key);
		}
//		for (int key : keys) {
//			robot.keyRelease(key);
//		}
		for (int i = keys.length - 1; i >= 0; i--) {
			robot.keyRelease(keys[i]);
		}
	}

//	public void fullScreen() {
//		robot.keyPress(KeyEvent.VK_ALT);
//		robot.keyPress(KeyEvent.VK_SPACE);
//		robot.keyPress(KeyEvent.VK_X);
//		robot.keyRelease(KeyEvent.VK_ALT);
//		robot.keyRelease(KeyEvent.VK_SPACE);
//		robot.keyRelease(KeyEvent.VK_X);
//	}

	public void key(final String text) {
		robot.setAutoDelay(1);
		for (int i = 0; i < text.length(); i++) {
			final char ch = text.charAt(i);
			final boolean upperCase = Character.isUpperCase(ch);
			final int keyCode = KeyEvent.getExtendedKeyCodeForChar(ch);
			if (upperCase) {
				robot.keyPress(KeyEvent.VK_SHIFT);
			}
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
			if (upperCase) {
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
		}
		robot.setAutoDelay(20);
	}

	/**
	 * 把文本设置到剪贴板（复制）
	 */
	public static void setClipboardString(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(text), null);
	}

	/**
	 * 从剪贴板中获取文本（粘贴）
	 * 
	 * @throws IOException
	 * @throws UnsupportedFlavorException
	 */
	public static String getClipboardString() throws UnsupportedFlavorException, IOException {
		Transferable trans = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return (String) trans.getTransferData(DataFlavor.stringFlavor);
		}
		return null;
	}

	public static String addPrefix(String code) {
		if (code.startsWith("0") || code.startsWith("1")) {
			return "sz" + code;
		}
		if (code.startsWith("6") || code.startsWith("5")) {
			return "sh" + code;
		}
		return code;
	}

	public static String delPrefix(String code) {
		String prefix = code.substring(0, 2);
		if (prefix.equalsIgnoreCase("sh") || prefix.equalsIgnoreCase("sz")) {
			return code.substring(2);
		}
		return code;
	}

	public static void main0(String[] args) {
		System.out.println(addPrefix("600333"));
		System.out.println(addPrefix("000333"));
		System.out.println(delPrefix("sz000333"));
		System.out.println(delPrefix("sh600333"));
	}

	public static void main(String[] args) throws Exception {
		QuantConfig config = new QuantConfig();
		config.setBrokerExe("C:\\同花顺软件\\同花顺\\xiadan.exe");
		config.setBroker("test");
		config.setInitAssets(1000000d);
		config.setPortfolio("test");
		config.setMaxPosition(1);
		config.setDrawdownLimit(0.1);
		config.setStoplossLimit(0.1);

		Order order = new Order();
		order.setCode("sh600519");
		order.setSize(1000);
		order.setCurrentPrice(1000);

		BrokerService service = new BrokerService();
		Portfolio portfolio = service.getPortfolio(config);
		System.out.println("Balance: " + portfolio.getCash());
		for (Position position : portfolio.getPositions().values()) {
			System.out.println(position);
		}
		for (Order temp : service.getTradings(portfolio)) {
			System.out.println(temp);
		}
//		service.delegate(portfolio, order);
//		service.delegate(portfolio, order);
//		service.delegate(portfolio, order);
//		order.setSize(-1000);
//		service.delegate(portfolio, order);
//		service.delegate(portfolio, order);
//		service.delegate(portfolio, order);
		service.destroy();
	}

}
