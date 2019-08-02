import talib
import numpy as np
from scipy import stats

# 初始化，context对象将会在任何方法之间做传递
def init(context):
    #沪深300指数、小盘指数和国债指数
    context.csi300 = "000300.XSHG"
    context.drawdown = 0.05 #回撤限制
    context.largest_value = {} #每个持仓期间最高市值
    scheduler.run_daily(stoploss)

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
    
def volume_enlarge(stock, num):
    volumes = history_bars(stock, num + 1, '1d', 'volume')
    return volumes[-1] / np.mean(volumes[- num - 2 : -2])
    
def macd_increase(stock):
    prices = history_bars(stock, 50, '1d', 'close')
    # 计算MACD，返回dif, dea 和 macd的时间序列数组，，MACD没有乘2
    dif, dea, macd = talib.MACD(prices, 12, 26, 9)
    return macd[-1] > macd[-2]
def get_box(stock, num):
    prices = history_bars(stock, num + 1, '1d', 'close')
    high_price = history_bars(stock, num + 1, '1d', 'high')
    low_price = history_bars(stock, num + 1, '1d', 'low')
    if len(high_price) < num :
        return 1
    h = np.max(high_price[0 : -2])
    l = np.min(low_price[0 : -2])
    
    #振幅
    va = (h-l) / np.mean(prices[0 : -2])
    # 判断今天收盘价突破箱体
    if prices[-1] > np.max(prices[0 :-2]):
        return va
    return 1
def handle_bar(context, bar_dict):
    f = index_components('000905.XSHG') #000905.XSHG
    stocks = [t for t in f if get_slope(t, 60) > 0.5 and macd_increase(t) and volume_enlarge(t, 20) > 1.5 and t not in context.portfolio.positions]
    stocks = sorted([t for t in stocks], key=lambda t : get_box(t, 25))
    if len(stocks) <= 0: return
    # 建仓
    for t in stocks[0:5 - len(context.portfolio.positions)]:
        # 最多持仓10只，如果不被选中则清仓，并且在大盘在向下时空仓
        order_target_percent(t,.99/ 5)
        
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
        if largest_value == 0 :
            continue
        context.largest_value.update({stock : largest_value})
        drawdown = (1- market_value / largest_value)
        if drawdown > context.drawdown:
            order_target_percent(stock, 0)
            logger.info(str(stock) + ' drawdown ' + str(drawdown * 100) + '%')
            del context.largest_value[stock]
            continue
        
        profit = (market_value - bought_value) / bought_value
        if profit < -0.02 : # 止损 1%
            order_target_percent(stock, 0)
            logger.info(str(stock) + ' stoploss ' + str(profit * 100) + '%')
            del context.largest_value[stock]
            continue
        if profit > 1 : # 止盈 5%
            order_target_percent(stock, 0)
            logger.info(str(stock) + ' stoploss ' + str(profit * 100) + '%')
            del context.largest_value[stock]
            continue