package winw.game.stock.analysis;

/**
 * 建议。
 * 
 * @author winw
 *
 */
@Deprecated
public class Advise {

	public enum Trading {
		/**
		 * 买入信号
		 */
		BUY_SIGNAL,
		/**
		 * 卖出信号
		 */
		SELL_SIGNAL
	}

	public enum Market {
		/**
		 * 多头市场
		 */
		BULL_MARKET,
		/**
		 * 空头市场
		 */
		BEAR_MARKET,

		/**
		 * 市场顶部
		 */
		MARKET_TOP,
		/**
		 * 市场底部
		 */
		MARKET_BOTTOM
	}

	/**
	 * <tt>null</tt>表示没有信号。
	 */
	private Trading trading;

	/**
	 * 市场行情。
	 */
	private Market market;

	/**
	 * 建议。
	 */
	private String advise;

	public Advise() {
		super();
	}

	public Advise(Trading trading) {
		super();
		this.trading = trading;
	}

	public Trading getSignal() {
		return trading;
	}

	public void setSignal(Trading trading) {
		this.trading = trading;
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

	public String getAdvise() {
		return advise;
	}

	public void setAdvise(String advise) {
		this.advise = advise;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Advise [signal=");
		builder.append(trading);
		builder.append(", advise=");
		builder.append(advise);
		builder.append(", market=");
		builder.append(market);
		builder.append("]");
		return builder.toString();
	}

}
