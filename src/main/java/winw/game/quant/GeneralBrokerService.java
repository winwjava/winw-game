package winw.game.quant;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import winw.game.TradingConfig;

/**
 * 通用券商接口。用 <tt>Robot</tt> 模拟操作同花顺实现。
 * <p>
 * 参考https://github.com/nladuo/THSTrader实现（Python控制能力更强）。
 * 
 * @author winw
 *
 */
@ManagedBean("generalBrokerService")
public class GeneralBrokerService extends BrokerService {

	private Robot robot;
	protected Process client;

	@Resource
	protected TradingConfig config;

	public GeneralBrokerService() {
		super();
	}

	public GeneralBrokerService(String brokerExe) throws AWTException, IOException {
		super();
		config = new TradingConfig();
		config.setBrokerExe(brokerExe);
		init();
	}

	@PostConstruct
	public void init() throws AWTException, IOException {
		if (config.getBroker() == null || config.getBroker().isEmpty()) {
			return;
		}
		client = Runtime.getRuntime().exec(config.getBrokerExe());
		robot = new Robot();
		robot.delay(15000);
		robot.setAutoDelay(20);

		// TODO login
	}

	@PreDestroy
	public void destroy() {
		if (client != null && client.isAlive()) {
			client.destroy();
		}
	}

	public Portfolio find(String name) throws Exception {
		Map<String, Position> positions = getPositions();
		// 资金余额，设置初始值，然后根据持仓计算即可。
		// 当前持仓
		// TODO 当日成交 = 当天第一次order时先更新持仓。然后与当前持仓做对比即可。
		double cash = config.getInitAssets();
		for (Position position : positions.values()) {
			cash -= position.getHoldingPrice() * position.getSize();
		}
		return new Portfolio(name, cash, config.getMaxPosition(), config.getDrawdownLimit(), config.getStoplossLimit());
	}

	public Map<String, Position> getPositions() throws UnsupportedFlavorException, IOException {
		key(KeyEvent.VK_F4);
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
			position.setCode(fileds[1]);
			position.setName(fileds[2]);
			position.setSize(Integer.valueOf(fileds[3]));
			position.setSellable(Integer.valueOf(fileds[4]));
			position.setHoldingPrice(Double.valueOf(fileds[5]));
			position.setCurrentPrice(Double.valueOf(fileds[6]));
			System.out.println(position);
			results.put(position.getCode(), position);
		}
		return results;
	}

	public void order(Order order) {
		long t0 = System.currentTimeMillis();
		key(KeyEvent.VK_F4);// 还原F1和F2的光标位置
		key(order.getSize() > 0 ? KeyEvent.VK_F1 : KeyEvent.VK_F2);

		keyboardString(order.getCode().substring(2));
		key(KeyEvent.VK_TAB);

		robot.delay(100);
		key(KeyEvent.VK_BACK_SPACE, 10);// 删除自动输入的价格
		keyboardString(String.valueOf(order.getPrice()));
		key(KeyEvent.VK_TAB);

		keyboardString(String.valueOf(Math.abs(order.getSize())));
		key(KeyEvent.VK_TAB);

		for (int i = 0; i < 5; i++) {// 确保提交。
			key(KeyEvent.VK_ENTER);
			robot.delay(100);
		}
		long t1 = System.currentTimeMillis();
		System.out.println("order cost: " + (t1 - t0));
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

	public void keyboardString(final String text) {
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

	public static void main(String[] args)
			throws IOException, AWTException, UnsupportedFlavorException, InterruptedException {
		Order order = new Order();
		order.setCode("sh600519");
		order.setSize(1000);
		order.setPrice(1000);

		GeneralBrokerService service = new GeneralBrokerService("C:\\同花顺软件\\同花顺\\xiadan.exe");
		service.getPositions();
		service.order(order);
		service.order(order);
		service.order(order);

		order.setSize(-1000);
		service.order(order);
		service.order(order);
		service.order(order);
		service.getPositions();
		Thread.sleep(5000);
		service.destroy();
	}

}
