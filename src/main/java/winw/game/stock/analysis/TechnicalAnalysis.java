package winw.game.stock.analysis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import winw.game.stock.Stock;
import winw.game.stock.StockQuote;

/**
 * ���Ʒ�����
 * 
 * @author winw
 *
 */
public class TechnicalAnalysis {

	// ����MACD�ǻ����ƶ�ƽ���ߣ���˱��������ͺ�ָ�ꡣ
	// ��Ϊ�۸����Ƶ�ָ�꣬MACD���ڲ����ƣ���һ����Χ�ڽ��ף��������Բ��ȶ��ļ۸��ж����н��׵Ĺ�Ʊ��̫���á�

	public void analysis(Stock stock, List<StockQuote> quoteList) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String today = dateFormat.format(new Date());
		
		Calendar instance = Calendar.getInstance();
		instance.setTime(stock.getTime());
		// �����ǰʱ���ǽ���ʱ�䣬�򽫵���Ľ��ף�Ҳ���뵽��ʷ��¼�����
		if (instance.get(Calendar.HOUR_OF_DAY) < 15) {
			System.out.println("����ʱ�䣺" + stock.getTime());
			StockQuote quote = new StockQuote();
			quote.setDate(today);
			quote.setOpen(stock.getOpen());
			quote.setClose(stock.getPrice());// FIXME �õ�ǰ�ۣ��������̼�
			quote.setHigh(stock.getHigh());
			quote.setLow(stock.getLow());
			quote.setVolume(stock.getVolume());
			quoteList.add(quote);
		}
		// ���� MA MACD BOLL RSI KDJ ָ��
		analysisMACD(stock, Indicators.compute(quoteList));

		// TODO KDJ
	}

	/**
	 * �ź��߽��档
	 * <p>
	 * ��MACD��ƽ���߽���ʱ���������ź��߽��桱 Ҳ����˵�������磨����ͼ���ı����ʱ�� ���MACD�ߴ���ƽ���ߣ������ǡ����棩��
	 * �������������ƽ���ߣ�������������㣩�µ�������۴����¼��ı�׼���͡� ��Щ�¼�����Ϊ�ǹ�Ʊ���Ƽ����ڽ���ķ�����ٵļ���
	 * 
	 * <p>
	 * �㽻��
	 * <p>
	 * ��MACDϵ�иı����ʱ�����������㽻�桱�¼�����MACD�ߴ���ˮƽ���ᡣ���۸�ϵ�еĿ��ٺ�����EMA֮��û�в���ʱ���ͻᷢ�����������
	 * �����浽����MACD�ı仯������Ϊ�����������Ӹ�������Ϊ�����ǡ����㽻���ṩ�����Ʒ������仯��֤�ݣ����Ǳ��ź��߽�����ٵ�ȷ���䶯����
	 * 
	 * @param list
	 */
	protected void analysisMACD(Stock stock, List<Indicators> list) {// MACD�ź��߽���������㽻�������
		Indicators today = list.get(list.size() - 1);
		Indicators yesterday = list.get(list.size() - 2);
		StringBuilder result = new StringBuilder();

		// 1. MACD��棺DIFF ��������ͻ�� DEA��Ϊ�����źš�
		if (today.getDiff() > yesterday.getDiff() && today.getDea() < yesterday.getDea()) {
			result.append("1. MACD��棺DIFF ��������ͻ�� DEA��Ϊ�����źš�");
		}
		// 2. MACD���棺DIFF ��������ͻ�� DEA��Ϊ�����źš�
		if (today.getDiff() < yesterday.getDiff() && today.getDea() > yesterday.getDea()) {
			result.append("2. MACD���棺DIFF ��������ͻ�� DEA��Ϊ�����źš�");
		}
		// 3. MACD ��ת�죺MACD ֵ�ɸ��������г��ɿ�ͷתΪ��ͷ��
		if (today.getMacd() > 0 && yesterday.getMacd() < 0) {
			result.append("3. MACD ��ת�죺MACD ֵ�ɸ��������г��ɿ�ͷתΪ��ͷ��");
		}
		// 4. MACD ��ת�̣�MACD ֵ�����为���г��ɶ�ͷתΪ��ͷ��
		if (today.getMacd() < 0 && yesterday.getMacd() > 0) {
			result.append("4. MACD ��ת�̣�MACD ֵ�����为���г��ɶ�ͷתΪ��ͷ��");
		}

		// TODO ��ǰ�����ǽ׶Σ�����
		
		if (result.length() > 0) {
			System.out.println(today.getDate() + " " + stock.getCode() + " " + stock.getName() + " " + stock.getPrice()
					+ ": " + result.toString());
		}
	}

}
