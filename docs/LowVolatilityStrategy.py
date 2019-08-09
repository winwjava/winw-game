import talib
import numpy as np
from scipy import stats

def init(context):
    context.drawdown = 0.05 #回撤限制
    context.largest_value = {} #每个持仓期间最高市值
    scheduler.run_daily(stoploss)
    scheduler.run_weekly(trade,tradingday=1,time_rule=market_open(minute=1))

# 回撤止损
def stoploss(context, bar_dict):
    for stock in set(context.largest_value.keys()).difference(set(context.portfolio.positions)):
        del context.largest_value[stock]
    for stock in context.portfolio.positions:
        # 市值
        market_value = context.portfolio.positions[stock].market_value
        bought_value = context.portfolio.positions[stock].bought_value
        # 最高市值
        largest_value = max(context.largest_value.get(stock, 0), market_value)
        context.largest_value.update({stock : largest_value})
        drawdown = (1- market_value / largest_value)
        if drawdown > context.drawdown:
            order_target_percent(stock, 0)
            #context.limit_order.update({stock : 20})
            logger.info(str(stock) + ' drawdown ' + str(drawdown * 100) + '%')
            del context.largest_value[stock]
            continue
        profit = (market_value - bought_value) / bought_value
        if profit < -0.02 : # 止损 1%
            order_target_percent(stock, 0)
            logger.info(str(stock) + ' stoploss ' + str(profit * 100) + '%')
            del context.largest_value[stock]
            continue
        if profit > 0.05 : # 止盈 5%
            order_target_percent(stock, 0)
            logger.info(str(stock) + ' stoploss ' + str(profit * 100) + '%')
            del context.largest_value[stock]
            continue
# 计算斜率
def get_slope(stock, num):
    # 获取历史收盘价序列，返回ndarray
    prices = history_bars(stock, num * 2, '1d', 'close')
    if len(prices) < num:
        return -10
    ema = talib.EMA(prices, num)
    # 模拟X轴，X点的间隔用Y点的0.005倍（y * 30% / 60）
    xPoints = [i * ema[-6] * 0.005 for i in range(0, 6)]
    # 用线性回归，计算 EMA60 最近时间段的斜率
    # (slope, b_s, r, tt, stderr)
    return stats.linregress(xPoints, ema[-6:])[0]
def trade(context, bar_dict):
    f = get_fundamentals(
        query(fundamentals.eod_derivative_indicator.market_cap
        ).order_by(fundamentals.eod_derivative_indicator.market_cap.asc()).limit(500)).columns
    #f = index_components('000300.XSHG') #000905.XSHG
    # 非ST，按照60天的收盘价的差异系数排序取前五，低波
    df = sorted([t for t in f if 1-is_st_stock(t)], key=lambda t:history_bars(t,60,'1d','close').std()/np.mean(history_bars(t,60,'1d','close')))[10:]
    # 将沪深300的5天收盘价做线性回归
    slope = get_slope('000300.XSHG', 60)
    for t in set(list(context.portfolio.positions) + df):
        # 最多持仓10只，如果不被选中则清仓，并且在大盘在向下时空仓
        order_target_percent(t,.99/ 10 * (t in df) * (slope > 0.04))