package winw.game.quant.dataview;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.util.Log;

import lombok.extern.slf4j.Slf4j;
import winw.game.quant.Quote;
import winw.game.quant.QuoteService;

/**
 * 根据每个标的走势，划分到一个主题里面。
 * 
 * @author winw
 *
 */
@Slf4j
@ManagedBean
public class SubjectService {

	@Resource
	SubjectRepository subjectRepository;
	@Resource
	SubjectEntryRepository subjectEntryRepository;

//	@Resource
//	private RedisTemplate<String, List<String>> redisTemplate;

	HashMap<String, List<Quote>> quoteCache = new HashMap<String, List<Quote>>();

	HashMap<String, Subject> codeSubjectMap = new HashMap<String, Subject>();

	private QuoteService quoteService = QuoteService.getDefault();

	@Transactional
//	@PostConstruct
//	@Scheduled(initialDelay = 2, fixedDelay = Integer.MAX_VALUE)
	public void init() throws Exception {
		for (String code : StockList.CSI_300) {// StockList.CSI_300   CSI_300_TOP
//			List<String> jsonList = redisTemplate.opsForValue().get(code);

			List<Quote> list = new ArrayList<Quote>();
//			for(String json: jsonList) {
//				list.add(JSONObject.parseObject(json, Quote.class));
//			}

//			List<Quote> list = JSONObject.parseArray(json, Quote.class);
			if (list != null && list.size() > 0) {
				log.info("use cache.");
				quoteCache.put(code, list);
				continue;
			}
			List<Quote> quoteList = quoteService.get(Quote.class, code, "2023-02-01", "2023-08-01");
			if (quoteList == null || quoteList.size() == 0) {
				log.warn("skip {}, quoteList is empty.", code);
				continue;
			}
//			redisTemplate.opsForValue().set(code, JSONObject.toJSONString(quoteList));
//			redisTemplate.opsForList().set(code, index, quoteList);

			quoteCache.put(code, quoteList);
		}

		for (String code : StockList.CSI_300) {
			if (subjectEntryRepository.findByCode(code).size() > 0) {// 已经存在
				continue;
			}
			Quote quote = quoteService.get(Quote.class, code);
			if (quote == null) {
				log.warn("skip {}, not found.", code);
				continue;
			}
			Subject subject = null;
			List<Quote> quoteList = quoteCache.get(code);

			HashMap<String, Double> codeCorrelationMap = new HashMap<String, Double>();
			for (String temp : quoteCache.keySet()) {
				if (temp.equals(code)) {
					continue;
				}
				double correlation = pearsonCorrelation(quoteList, quoteCache.get(temp));

				if (correlation > 0.85) {
					codeCorrelationMap.put(temp, correlation);
				}
			}
			if (codeCorrelationMap.size() > 0) {
				String correlationCode = codeCorrelationMap.entrySet().stream()
						.max(Comparator.comparing(Map.Entry::getValue)).get().getKey();

				List<SubjectEntry> entryList = subjectEntryRepository.findByCode(correlationCode);
				if (entryList.size() > 0) {
					Log.warn("subjectEntry has more then one.");
				}

				if (entryList.size() == 0) {// 都没入库；
					Quote correlationQuote = quoteService.get(Quote.class, correlationCode);
					subject = new Subject();
					subject.setName(correlationQuote.getName());
					subject = subjectRepository.save(subject);
					
					subjectEntryRepository
							.save(new SubjectEntry(subject.getId(), code, quote.getName(), correlationQuote.getMarketVal() / correlationQuote.getPrice()));// 这里用流通市值

//					double weight = correlationQuote.getMarketVal() / correlationQuote.getClose();// 流通股总数
					
					subjectEntryRepository.save(new SubjectEntry(subject.getId(), correlationCode,
							correlationQuote.getName(), correlationQuote.getMarketVal() / correlationQuote.getPrice() ));// 这里用流通股本

					codeSubjectMap.put(code, subject);
					codeSubjectMap.put(correlationCode, subject);
					log.info("{} {} join Subject {},  correlation: {}", quote.getCode(), quote.getName(),
							subject.getName(), codeCorrelationMap.get(correlationCode));
				} else {
					subject = subjectRepository.findById(entryList.get(0).getSubjectId()).get();
					log.info("{} {} join Subject {},  correlation: {}", quote.getCode(), quote.getName(),
							subject.getName(), codeCorrelationMap.get(correlationCode));

					subjectEntryRepository
							.save(new SubjectEntry(subject.getId(), code, quote.getName(), quote.getMarketVal() / quote.getPrice()));// 这里用流通市值

					subjectRepository.updateSubjectNum(subject.getId());
				}
			}

			if (subject == null) {
				codeSubjectMap.put(code, subject);
				subject = new Subject();
				subject.setName(quote.getName());
				subject = subjectRepository.save(subject);
				subjectEntryRepository
						.save(new SubjectEntry(subject.getId(), code, quote.getName(), quote.getMarketVal()));// 这里用流通市值

//				log.info("Create Subject {},  correlation: {}", subject.getName());
			}

			subjectRepository.flush();
			subjectEntryRepository.flush();
			quoteCache.put(code, quoteList);
		}

//		"sz000568", // 泸州老窖
//		quoteCache.put("sz000568", );
//		List<Quote> sz000568 = quoteService.get(Quote.class, "sz000568", "2023-07-01", "2023-08-01");
//		for (String code : quoteCache.keySet()) {
//			double correlation = pearsonCorrelation(sz000568, quoteCache.get(code));
//			System.out.println(code + " correlation: " + correlation);
//		}

//		System.out.println(pearsonCorrelation(list1, list2));
//		System.out.println(pearsonCorrelation(list2, list3));
//		System.out.println(pearsonCorrelation(list1, list3));
	}

	public final static String[] CSI_300_TOP = { // 沪深300十大权重股。市值大、流动性好。
			"sh600519", // 贵州茅台
			"sh601318", // 中国平安
			"sz000858", // 五 粮 液
			"sh600036", // 招商银行
			"sz000333", // 美的集团
			"sh600276", // 恒瑞医药
			"sh601166", // 兴业银行
			"sz000651", // 格力电器
			"sh601888", // 中国中免
			"sh600887", // 伊利股份
			"sh600030", // 中信证券
			"sh601012", // 隆基股份
			"sz002475", // 立讯精密
			"sz300059", // 东方财富
			"sh600031", // 三一重工
			"sz000002", // 万科A
			"sh603288", // 海天味业
			"sz000001", // 平安银行
			"sz002415", // 海康威视
			"sz002594", // 比亚迪
//			"sh600900",  // 长江电力
//			"sh601398",  // 工商银行
			"sh603259", // 药明康德
			"sz000568", // 泸州老窖
			"sz000725", // 京东方A
			"sz002352", // 顺丰控股
			"sz002714", // 牧原股份
			"sh600309", // 万华化学
			"sz002304" // 洋河股份
	};

	public static void main(String[] args) throws Exception {
	}

	/**
	 * Pearson Correlation
	 * 皮尔逊相关系数相关系数是由卡尔.皮尔逊从弗朗西斯.高尔顿在19世纪80年代提出的一个相似但稍有不同的想法演变来的，用于度量两个变量之间的线性相关程度，值介于-1和1之间。两个变量的pearson相关系数用它们的协方差与方差的商表示。
	 * 
	 * @return
	 * 
	 */
	public static double pearsonCorrelation(List<Quote> list1, List<Quote> list2) {
		if (list1 == null || list2 == null) {
			return 0;
		}

		int l = Math.min(list1.size(), list2.size());
		if (l < 3) {
			return 0;
		}
		SimpleRegression regression = new SimpleRegression();
		for (int i = 0; i < l; i++) {
			regression.addData(list1.get(i).getClose(), list2.get(i).getClose());
		}
		return regression.getR();
	}

}
