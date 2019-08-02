import talib
from scipy import stats

# 初始化，context对象将会在任何方法之间做传递
def init(context):
    #沪深300指数、小盘指数和国债指数
    #510300 沪深
    #513600 恒生
    #513100 纳指
    context.csi300 = "510300.XSHG"
    context.shbond = "511010.XSHG"#000012.XSHG
    context.drawdown = 0.05 #回撤限制
    context.largest_value = {} #每个持仓期间最高市值
    # 止损后，限制购买
    #context.limit_order = {context.csi300 : 0, context.shbond: 0} 
    scheduler.run_daily(stoploss)

# 回撤止损
def stoploss(context, bar_dict):
    #for stock in context.limit_order.keys():
        #context.limit_order.update({stock : context.limit_order[stock] - 1})
    for stock in set(context.largest_value.keys()).difference(set(context.portfolio.positions)):
        del context.largest_value[stock]
    for stock in context.portfolio.positions:
        # 市值
        market_value = context.portfolio.positions[stock].market_value
        # 最高市值
        largest_value = max(context.largest_value.get(stock, 0), market_value)
        context.largest_value.update({stock : largest_value})
        drawdown = (1- market_value / largest_value)
        if drawdown > context.drawdown:
            order_target_percent(stock, 0)
            #context.limit_order.update({stock : 20})
            logger.info(str(stock) + ' drawdown ' + str(drawdown * 100) + '%')
            del context.largest_value[stock]
# 计算斜率
def get_slope(stock, num, bar_dict):
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
    
# 日线或分钟线更新将会触发
def handle_bar(context, bar_dict):
    #根据 EMA斜率判断当前的趋势
    csi300_ema5 = get_slope(context.csi300, 5, bar_dict)
    csi300_ema60 = get_slope(context.csi300, 60, bar_dict)
    shbond_ema60 = get_slope(context.shbond, 60, bar_dict)
    
    if csi300_ema60 > shbond_ema60 and csi300_ema5 > shbond_ema60:
        order(context, context.csi300)
    elif csi300_ema60 > shbond_ema60 and context.portfolio.positions[context.csi300].quantity > 0:
        return
    else:
        print(shbond_ema60)
        order(context, context.shbond)
# 下单，先卖出后买进
def order(context, stock):
    positions = context.portfolio.positions
    for temp in [context.csi300, context.shbond]:
        if temp != stock and positions[temp].quantity > 0:
            order_target_percent(temp, 0)
    for temp in [context.csi300, context.shbond]:
        if temp == stock and positions[temp].quantity == 0:
            order_target_percent(temp, 0.98)