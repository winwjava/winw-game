package winw.game.quant.csindex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 中证主题指数。
 * <p>
 * 数据来源：http://www.csindex.com.cn/zh-CN/indices/index?class_1=1&class_20=20&class_10=10&class_7=7&is_custom_0=1
 * 
 * @author winw
 *
 */
@Slf4j
@RestController
@RequestMapping("/SubjectIndex")
public class SubjectIndexController {

	private static WebClient webClient = WebClient.create();

	@Resource
	SubjectIndexRepository subjectIndexRepository;

	@Resource
	IndexQuoteRepository indexQuoteRepository;

	@PostConstruct
	@Scheduled(cron = "30 15 15 * * 1-5")
	public void fetchIndices() throws Exception {
		for (int i = 0, totalPage = 7; i < totalPage; i++) {
			PageResult indices = indices(i, 50);

			for (SubjectIndex temp : indices.getList()) {
				if (temp.getYld_1_mon() != null) {
					temp.setYieldOneMonth(Double.valueOf(temp.getYld_1_mon()));
				}
			}
			subjectIndexRepository.saveAll(indices.getList());
		}
	}

	@Scheduled(cron = "30 30 15 * * 1-5")
	public void init() throws IOException, InterruptedException {
		List<SubjectIndex> findAll = subjectIndexRepository.findAll();
		for (SubjectIndex subjectIndex : findAll) {
			try {
				indexQuoteRepository.saveAll(detail(subjectIndex.getIndex_code()));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

//	@GetMapping(value = "/indices")
//	public PageResult indices() throws IOException, InterruptedException {
//		return indices(1, 50);
//	}

	// 取最近 30 天 的每个日期。

	@GetMapping(value = "/getIndexDays")
	public List<String> getIndexDays(int days) throws IOException, InterruptedException {
		List<String> lastDays = indexQuoteRepository.getLastDays("H30090", PageRequest.of(0, 30));
		Collections.reverse(lastDays);
		ArrayList<String> arrayList = new ArrayList<String>();
		for (String string : lastDays) {
			arrayList.add(string.substring(4));
		}
		return arrayList;
	}

	@GetMapping(value = "/getIndexQuoteList")
	public List<IndexLine> getIndexQuoteList(String indexCode) throws IOException, InterruptedException {
		ArrayList<IndexLine> list = new ArrayList<IndexLine>();

		List<SubjectIndex> all = subjectIndexRepository
				.findAll(PageRequest.of(0, 50, Sort.by(Direction.fromString("desc"), "yieldOneMonth"))).getContent();

		for (SubjectIndex subjectIndex : all) {
			IndexLine line = new IndexLine();
			line.setLabel(subjectIndex.getIndx_sname());
			line.setData(indexQuoteRepository.findByIndexCode(subjectIndex.getIndex_code(), PageRequest.of(0, 30)));
			Collections.reverse(line.getData());

			double first = Double.parseDouble(line.getData().get(0));

			// 将初始值设为10000；后面的值以10000为基础按比例
			line.setData(line.getData().stream().map(s -> String.valueOf(Double.parseDouble(s) * 10000 / first))
					.collect(Collectors.toList()));

			list.add(line);
		}

		return list;
//		return indexQuoteRepository.findByIndexCode(indexCode);
	}

	public static PageResult indices(int page, int page_size) throws IOException, InterruptedException {
		String url = "http://www.csindex.com.cn/zh-CN/indices/index" + "?page=" + page + "&page_size=" + page_size
				+ "&by=asc&order=%E5%8F%91%E5%B8%83%E6%97%B6%E9%97%B4&data_type=json&class_1=1&class_7=7&class_10=10&class_20=20&is_custom_0=1";
		log.info(">> {}", url);
		Mono<String> resp = webClient.get().uri(url, page, page_size).retrieve().bodyToMono(String.class);
		String response = resp.block();
		log.info("<< {}", response);

		return JSONObject.parseObject(response.replace("\uFEFF", ""), PageResult.class);
	}

	public static List<IndexQuote> detail(String indexCode) throws IOException, InterruptedException {
		Mono<String> resp = webClient.get().uri(
				"http://www.csindex.com.cn/zh-CN/indices/index-detail/{indexCode}?earnings_performance=3个月&data_type=json",
				indexCode).retrieve().bodyToMono(String.class);
		String response = resp.block();
		log.info("<< {}", response);

		List<IndexQuote> list = JSONObject.parseArray(response.replace("\uFEFF", ""), IndexQuote.class);

		for (IndexQuote indexQuote : list) {
			indexQuote.setTradedate(indexQuote.getTradedate().substring(0, 10).replace("-", ""));
			indexQuote.setId(indexQuote.getTradedate() + "_" + indexQuote.getIndx_code());
		}

		return list;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		List<IndexQuote> detail = detail("H30090");
		System.out.println(detail.toString());
	}
}
