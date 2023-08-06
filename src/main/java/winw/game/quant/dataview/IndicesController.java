package winw.game.quant.dataview;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

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
public class IndicesController {

//	private static WebClient webClient = WebClient.create();

	@Resource
	IndicesRepository indicesRepository;

	@Resource
	IndexQuoteRepository indexQuoteRepository;

//	@GetMapping(value = "/indices")
//	public PageResult indices() throws IOException, InterruptedException {
//		return indices(1, 50);
//	}

	/**
	 * 取最近 30 天 的每个日期。
	 * 
	 * @param days
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@GetMapping(value = "/getIndexDays")
	public List<String> getIndexDays(int days) throws IOException, InterruptedException {
		List<String> lastDays = indexQuoteRepository.getLastDays("H30007", PageRequest.of(0, 30));
		Collections.reverse(lastDays);
		ArrayList<String> arrayList = new ArrayList<String>();
		for (String string : lastDays) {
			arrayList.add(string.substring(4));
		}
		return arrayList;
	}

	// TODO 过滤掉低波动率的指数。
	// TODO 过滤掉振幅较低的指数。
	@GetMapping(value = "/getIndexQuoteList")
	public List<IndexLine> getIndexQuoteList(String indexCode) throws IOException, InterruptedException {
		ArrayList<IndexLine> list = new ArrayList<IndexLine>();

		List<Indices> all = indicesRepository
				.findAll(PageRequest.of(0, 50, Sort.by(Direction.fromString("desc"), "monthlyReturn"))).getContent();

		for (Indices indices : all) {
			IndexLine line = new IndexLine();
			line.setLabel(indices.getIndexName());
			line.setData(indexQuoteRepository.findByIndexCode(indices.getIndexCode(), PageRequest.of(0, 90)));
			Collections.reverse(line.getData());

//			double first = line.getData().get(0);

			double max = Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			for (Double temp : line.getData()) {
				max = Math.max(max, temp);
				min = Math.min(min, temp);
			}

			double zero = (max + min) / 2;// 零刻度

			// 以零刻度为基数，计算涨跌百分比
			line.setData(line.getData().stream().map(d -> (d - zero) / zero).collect(Collectors.toList()));

			// 截取最近30天。
			line.setData(line.getData().subList(30, line.getData().size()));
			if ((max - min) / 2 / zero < 0.06) {// 振幅小于0.05
				log.info("{} amplitude less then 0.06", indices.getIndexName());
				continue;
			}

			list.add(line);
		}

		return list;
//		return indexQuoteRepository.findByIndexCode(indexCode);
	}

//	private static final NumberFormat percentFormat = new DecimalFormat("#.##%");

//	private static final NumberFormat percentFormat = new DecimalFormat("#.##%");
	
	// TODO 过滤掉低波动率的指数。
	// TODO 过滤掉振幅较低的指数。
	@GetMapping(value = "/getIndexQuoteList0")
	public List<List<Object>> getIndexQuoteList0(String indexCode) throws IOException, InterruptedException {
		ArrayList<List<Object>> list = new ArrayList<List<Object>>();
		list.add(Arrays.asList("close", "date", "name"));
		List<Indices> all = indicesRepository
				.findAll(PageRequest.of(0, 50, Sort.by(Direction.fromString("desc"), "monthlyReturn"))).getContent();

		for (Indices indices : all) {
			List<IndexQuote> data = indexQuoteRepository.findByIndexCode0(indices.getIndexCode(),
					PageRequest.of(0, 90));
			Collections.reverse(data);
			double max = Double.MIN_VALUE;
			double min = Double.MAX_VALUE;
			for (IndexQuote temp : data) {
				max = Math.max(max, temp.getClose());
				min = Math.min(min, temp.getClose());
			}

			double zero = (max + min) / 2;// 零刻度

			// 以零刻度为基数，计算涨跌百分比
			// 截取最近30天。
			data = data.subList(data.size() < 30 ? 0 : 30 , data.size());// .stream().map(d -> (d - zero) / zero).collect(Collectors.toList());
			if ((max - min) / 2 / zero < 0.06) {// 振幅小于0.05
				log.info("{} amplitude less then 0.06", indices.getIndexName());
				continue;
			}
			for (IndexQuote temp : data) {

				double d = (temp.getClose() - zero) / zero * 100;
				BigDecimal close = new BigDecimal(d).setScale(1, RoundingMode.DOWN);
				list.add(Arrays.asList(close, temp.getTradeDate(),
						indices.getIndexName()));
			}
		}

		return list;
//		return indexQuoteRepository.findByIndexCode(indexCode);
	}

	public static void main(String[] args) {
		List<Integer> asList = Arrays.asList(3, 21, 5, 67, 24, 980, 23);
		System.out.println(asList.subList(1, 7));
	}

}
