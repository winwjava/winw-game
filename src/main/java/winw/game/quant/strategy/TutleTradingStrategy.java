package winw.game.quant.strategy;

/**
 * 海龟交易策略。
 * 
 * <p>
 * 唐奇安通道，上线=Max（前N个交易日的最高价），下线=Min（前N个交易日的最低价），中线=（上线+下线）/2，每个交易日结束之后更新当天的数据。这里N一般默认取20。那么唐奇安通道就是这个上线和下线所形成的走势区间。
 * 
 * 
 * <p>按分钟回测，可以及时发现价格突破唐奇安通道。怎么证明是有效突破？
 * @author winw
 *
 */
public abstract class TutleTradingStrategy {

}
