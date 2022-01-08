package winw.game.quant.dataview;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@ManagedBean
public class SubjectIndexService {

	@Resource
	IndexQuoteRepository indexQuoteRepository;
//	@Resource
//	CategoryRepository categoryRepository;
	@Resource
	SubjectIndexRepository subjectIndexRepository;

//	@PostConstruct
//	public void setCategoryIndex() throws Exception {
//		
//		List<Category> list = new ArrayList<Category>();
//		list.add(new Category());
//		categoryRepository.saveAll(list);
//	}

	private Map<String, String> pivotalMap = new HashMap<String, String>();

	{ // 市盈率越高，说明市场越看好，未来更有前景或短期看好
		// 第一产业：农牧业（农林牧渔）
		pivotalMap.put("930707", "中证畜牧");// 牧原股份
		pivotalMap.put("931778", "农牧主题");
		// 第二产业：采矿业和制造业（煤炭、石油、有色金属、黄金、食品、饮料、纺织、服装、家具、水泥、造纸、化工、医药、钢铁、冶炼、机械、建筑、汽车、航空航天、船舶铁路、电器、计算机、通信电子）
		pivotalMap.put("H30090", "能源红利");// 能源
		pivotalMap.put("H11057", "石化产业");// 石化
		pivotalMap.put("H30034", "300有色");// 有色
		pivotalMap.put("931012", "CS黄金");// 黄金
		pivotalMap.put("399997", "中证白酒");// 白酒，国庆春节，消费
		pivotalMap.put("H11051", "钢铁产业");// 钢铁
		pivotalMap.put("930706", "中证水泥");// 水泥
		pivotalMap.put("000950", "300基建");// 基建
		pivotalMap.put("930598", "稀土产业");// 稀土
		pivotalMap.put("H30206", "中证造纸");// 造纸

		pivotalMap.put("930698", "中证水利");// 水利水电（长江电力、中国电建、中国能建）
		
		// 第三产业：服务业（银行、证券、保险、房地产、旅游、餐饮酒店、信息技术、物流、教育、文体娱乐、水电气热）
		pivotalMap.put("000951", "300银行");// 银行
		pivotalMap.put("931169", "证券30");// 券商，与大盘同起同落
		pivotalMap.put("930618", "中证保险");// 保险
		pivotalMap.put("930633", "中证旅游");// 旅游，假期
		pivotalMap.put("H11060", "中证物流");// 物流，9月中旬快递会涨价，出现行情。  顺丰控股 中远海控 韵达股份
		pivotalMap.put("399812", "养老产业");// 养老
		pivotalMap.put("H30095", "中证医药");// 医药 新华制药 药明康德
//		pivotalMap.put("H50026", "上证医药");// 医药 千金药业 浙江医药
		pivotalMap.put("930641", "中证中药");// 以岭药业
		
		pivotalMap.put("930781", "中证影视");// 万达电影
		pivotalMap.put("H30365", "中证文娱");// 	三七互娱

		pivotalMap.put("L11618", "300海运");// 300海运
		pivotalMap.put("000952", "300地产（万科、保利）");// 300地产
		pivotalMap.put("930697", "中证家电");// 家电

		// 热点概念
		pivotalMap.put("931672", "风电产业");// 风电
		pivotalMap.put("931151", "光伏产业"); // 光伏
		pivotalMap.put("931746", "储能产业");// 储能，宁德时代、比亚迪、亿纬锂能、阳光电源、天赐材料、恩捷股份
		pivotalMap.put("931071", "人工智能");// 人工智能
		pivotalMap.put("931144", "通信技术");// 通信
		pivotalMap.put("H30007", "芯片产业");// 芯片
//		pivotalMap.put("930721", "CS智能汽车");
//		pivotalMap.put("930771", "CS新能源");
		pivotalMap.put("399959", "军工指数");// 军工

//		pivotalMap.put("930875", "空天军工");
//		pivotalMap.put("399973", "中证国防");
//		pivotalMap.put("H50043", "上证养老");// 养老

		// 2021年人口停止增长，老龄化人口持续增加，1962-1973大约三亿人口（70%是农民或农民工）在未来10年开始退休养老。
		// 到2030年，60岁以上人口将超过4亿，占人口33%左右，0-14岁1.4亿，占10%左右，劳动人口只有55%左右。
		// 到2035年，15-65人口将降至55%以下，每对夫妻需要养2-3个老人（父母、祖父母），1-2个小孩。社会养老负担加重。
		// 直接影响房地产、家电行业、汽车行业，间接影响钢铁、石油，未来预期下降，竞争加剧，利润下滑。大量工人失业。
		// 间接导致GDP增速下滑，维持在5%左右，居民收入增长低于通货膨胀（通胀大于增长），实际收入下降，居民消费下降。
		// 80后达到50岁，90后达到40岁，这群廉价劳动力逐步退出市场，依靠制造业和外贸的国家经济一片萧条，国际竞争力大幅下降。
		// 导致制造业和资本转移到其他发展中国家（东南亚和印度），人民币贬值，
		// 长三角外贸城市（上海、杭州、苏州、宁波、南京、无锡、常州、绍兴、嘉兴） 1亿人口
		// 珠三角外贸城市（广州、深圳、香港、佛山、东莞、惠州、珠海） 1亿人口
		// 国家中心城市：北京、天津、重庆、成都、武汉、郑州、西安、厦门、青岛，1亿人口

		// 人口不再增长：粮食和肉类（需求和价格下降）、服装、家电、汽车、房地产，产能过剩，大量工厂倒闭。
		// 人口质量提高：受教育程度继续提高，不断向特大城市聚集（长三角、）
		// 农村人口出空：农村人口大幅减少，人口继续向大城市迁移，大城市将出现大量永久租客（永远买不起房）。
		// 城市人口竞争：
		// 劳动人口减少：人工成本加速提高，工业加快向智能化转型，物价飞升（消费端价格包含房租和人工成本）。
		// 养老人口增加：预期寿命提高，人均医疗支出提升，医疗保健行业高速扩张，高瓴布局了很多医疗、抗癌药。

		// 国家政策
		// 国内（工业4.0、一带一路、碳达峰）：必须淘汰落后产能、高耗能产业，促进产业升级，突破中等收入陷阱。
		// 国际（一带一路、人民币国际化、亚投行）：加大向东南亚和非洲投资、设厂，提高国际影响力和竞争力，还需观察成效。

		// 人工智能、智能制造、芯片、软件，成为最重要的动力。
		// 只有高端制造业才能有竞争力和高回报：宁德时代、锂电池、新能源汽车、光伏、风电
		// 继续改革：进一步保护私人利益，支持资本在更多行业竞争。
		// 继续开放：开放更多行业允许国际资本参与，开放中国资本走出去。
		// 国际局势
		// 2010年美国15万亿，中国6.5万亿，2020年美国21，中国16
		// 到2025年，GDP达到20万亿美元，2030年达到25万亿美元，2035年左右GDP超过美国，人均GDP进入发达国家行列
		// 届时将具备解放台湾的军事实力，国际局势会减轻，贸易提升，税负减轻。
		
	}

	@PostConstruct
//	@Scheduled(cron = "30 15 15 * * 1-5")
	public void fetchIndices() throws Exception {
		subjectIndexRepository.deleteAll();
		indexQuoteRepository.deleteAll();
		for (int i = 1, totalPage = 2; i <= totalPage; i++) {
			SubjectIndexResp<SubjectIndex> indices = indices(i, 20);
			subjectIndexRepository.saveAll(indices.getData().stream().filter(o -> //
			"人民币".equals(o.getCurrency()) //
					&& "境内".equals(o.getRegion())//
					&& pivotalMap.containsKey(o.getIndexCode())// 关键指标

			).collect(Collectors.toList()));
			totalPage = indices.getTotal() / 20 + 1;
		}

		List<SubjectIndex> findAll = subjectIndexRepository.findAll();
		for (SubjectIndex subjectIndexBak : findAll) {
			try {
				indexQuoteRepository.saveAll(detail(subjectIndexBak.getIndexCode()));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private static WebClient webClient = WebClient.create();

	public static SubjectIndexResp<SubjectIndex> indices(int page, int page_size)
			throws IOException, InterruptedException {
		String url = "https://www.csindex.com.cn/csindex-home/index-list/query-index-item";

		// + "?page=" + page + "&page_size=" + page_size
		// +
		// "&by=asc&order=%E5%8F%91%E5%B8%83%E6%97%B6%E9%97%B4&data_type=json&class_1=1&class_7=7&class_10=10&class_20=20&is_custom_0=1";

//		log.info(">> {}", JSONObject.toJSONString(new SubjectIndexReq(), SerializerFeature.WriteMapNullValue));
//		String jsonString = JSONObject.toJSONString(new SubjectIndexReq(), SerializerFeature.WriteMapNullValue);

		String vvString = "{\"sorter\":{\"sortField\":\"null\",\"sortOrder\":null},\"pager\":{\"pageNum\":page_Num,\"pageSize\":page_Size},\"indexFilter\":{\"ifCustomized\":null,\"ifTracked\":null,\"ifWeightCapped\":null,\"indexCompliance\":null,\"hotSpot\":null,\"indexClassify\":[\"20\"],\"currency\":null,\"region\":null,\"indexSeries\":null,\"undefined\":null}}";

		vvString = vvString.replaceAll("page_Num", page + "");
		vvString = vvString.replaceAll("page_Size", page_size + "");
		log.info(">> {}", vvString);
		Mono<String> resp = webClient.post().uri(url, page, page_size).contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(vvString)).retrieve().bodyToMono(String.class);
		String response = resp.block();
		log.info("<< {}", response);

		return JSONObject.parseObject(response, new TypeReference<SubjectIndexResp<SubjectIndex>>() {
		});

//		return JSONObject.parseObject(response.replace("\uFEFF", ""), PageResult.class);
	}

	public final static String DATE_PATTERN = "yyyyMMdd";

	public static List<IndexQuote> detail(String indexCode) throws IOException, InterruptedException {
		String endDate = DateFormatUtils.format(new Date(), DATE_PATTERN);
		String startDate = DateFormatUtils.format(DateUtils.addMonths(new Date(), -3), DATE_PATTERN);

		Mono<String> resp = webClient.get().uri(
//				"http://www.csindex.com.cn/zh-CN/indices/index-detail/{indexCode}?earnings_performance=3个月&data_type=json",
				"https://www.csindex.com.cn/csindex-home/perf/index-perf?indexCode={indexCode}&startDate={startDate}&endDate={endDate}",
				indexCode, startDate, endDate).retrieve().bodyToMono(String.class);
		String response = resp.block();
		log.info("<< {}", response);

//		List<IndexQuote> list = JSONObject.parseArray(response.replace("\uFEFF", ""), IndexQuote.class);

		SubjectIndexResp<IndexQuote> list = JSONObject.parseObject(response,
				new TypeReference<SubjectIndexResp<IndexQuote>>() {
				});
		for (IndexQuote indexQuote : list.getData()) {
			indexQuote.setId(indexQuote.getIndexCode() + "_" + indexQuote.getTradeDate());
		}

		return list.getData();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		SubjectIndexResp<SubjectIndex> indices = indices(1, 5);

		for (SubjectIndex subjectIndex : indices.getData()) {
			log.info("{}", subjectIndex);
		}
	}

	public static void main1(String[] args) throws IOException, InterruptedException {
		List<IndexQuote> detail = detail("H30090");
		System.out.println(detail.toString());
	}
}
