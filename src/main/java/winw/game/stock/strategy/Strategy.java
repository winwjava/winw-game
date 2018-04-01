package winw.game.stock.strategy;

import java.util.List;

import winw.game.stock.analysis.Advise;
import winw.game.stock.analysis.Indicator;

/**
 * 投资策略。
 * 
 * <p>
 * 技术分析必须与基本分析结合，提高准确度。
 * <p>多种技术分析方法的综合研判。
 * 
 * @author winw
 *
 */
public interface Strategy {

	/**
	 * 分析并返回 Advise。
	 * 
	 * @param indicator
	 * @return Advise
	 */
	Advise analysis(List<Indicator> indicator);

}
