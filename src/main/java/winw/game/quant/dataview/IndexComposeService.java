package winw.game.quant.dataview;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@ManagedBean
public class IndexComposeService {

	@Resource
	IndexComposeRepository indexComposeRepository;

	private Map<String, String> composeMap = new HashMap<String, String>();
	private Map<String, String> pivotalMap = new HashMap<String, String>();

	{ // 市盈率越高，说明市场越看好，未来更有前景或短期看好
		// 第一产业：农牧业（农林牧渔）
		pivotalMap.put("930707", "中证畜牧");// 牧原股份
		
		composeMap.put("", "930707");
		
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
		pivotalMap.put("H11060", "中证物流");// 物流，9月中旬快递会涨价，出现行情。 顺丰控股 中远海控 韵达股份
		pivotalMap.put("399812", "养老产业");// 养老
		pivotalMap.put("H30095", "中证医药");// 医药 新华制药 药明康德
//		pivotalMap.put("H50026", "上证医药");// 医药 千金药业 浙江医药
		pivotalMap.put("930641", "中证中药");// 以岭药业

		pivotalMap.put("930781", "中证影视");// 万达电影
		pivotalMap.put("H30365", "中证文娱");// 三七互娱

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
		pivotalMap.put("930997", "新能源车");
		pivotalMap.put("399959", "军工指数");// 军工
	}

	@PostConstruct
	public void init() {

	}
}
