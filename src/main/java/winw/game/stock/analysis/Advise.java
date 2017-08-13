package winw.game.stock.analysis;

/**
 * 建议。
 * 
 * @author winw
 *
 */
public class Advise {

	public enum Signal {
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
		BEAR_MARKET
	}

	/**
	 * <tt>null</tt>表示没有信号。
	 */
	private Signal signal;

	/**
	 * 市场行情。
	 */
	private Market market;

	/**
	 * 建议。
	 */
	private String advise;

	public Signal getSignal() {
		return signal;
	}

	public void setSignal(Signal signal) {
		this.signal = signal;
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
		builder.append(signal);
		builder.append(", advise=");
		builder.append(advise);
		builder.append("]");
		return builder.toString();
	}

}
