package winw.game.quant.analysis;

/**
 * 信号。
 * 
 * @author winw
 *
 */
@Deprecated
public enum Signal {
	/**
	 * 金叉（MACD）
	 */
	GOLDEN_CROSSOVER,
	/**
	 * 死叉（MACD）
	 */
	DEATH_CROSSOVER,
	/**
	 * 零交叉（MACD）
	 */
	ZERO_CROSSOVER,
	/**
	 * 成交量放大
	 */
	VOLUME_ENLARGE,
	/**
	 * 成交量收缩
	 */
	VOLUME_SHRINK,
}
