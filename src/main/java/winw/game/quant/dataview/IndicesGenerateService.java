package winw.game.quant.dataview;

import java.util.List;
import java.util.TreeMap;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import winw.game.quant.Quote;
import winw.game.quant.QuoteService;

@Slf4j
@ManagedBean
public class IndicesGenerateService {

	@Resource
	IndicesRepository indicesRepository;
	@Resource
	IndexQuoteRepository indexQuoteRepository;

	@Resource
	SubjectRepository subjectRepository;
	@Resource
	SubjectEntryRepository subjectEntryRepository;

	private QuoteService quoteService = QuoteService.getDefault();

	@PostConstruct
//	@Scheduled(cron = "00 00 15-19 * * 1-5")
	public void generateSubjectIndex() throws Exception {
		log.info("Indices generate......");

		for (SubjectEntry entry : subjectEntryRepository.findAll()) {

			Quote quote = quoteService.get(Quote.class, entry.getCode());
			if(quote == null) {
				continue;
			}
			
			entry.setWeight(quote.getMarketVal() / quote.getPrice());
			subjectEntryRepository.save(entry);
		}

		// 从主题表中，取一个主题，计算最近一个月的情况并更新到indexQuote和indices中。

		// TODO 权重怎么计算？
		List<Subject> subjectList = subjectRepository.findBySize(5);

		for (Subject subject : subjectList) {

			Indices indices = new Indices();
			indices.setIndexCode(subject.getId() + "");
			indices.setIndexName(subject.getName());
			indices.setConsNumber(subject.getSize() + "");
			indicesRepository.save(indices);

			List<SubjectEntry> entryList = subjectEntryRepository.findBySubjectId(subject.getId());

			// 生成最近一个月的IndexQuote
			TreeMap<String, IndexQuote> dateQuoteMap = new TreeMap<String, IndexQuote>();
			for (SubjectEntry entry : entryList) {
				List<Quote> quoteList = quoteService.get(Quote.class, entry.getCode(), "2023-06-01", "2023-08-04");
				if(quoteList == null || quoteList.size() == 0) {
					continue;
				}
				
				for (Quote quote : quoteList) {
					String date = quote.getDate().replace("-", "");
					IndexQuote indexQuote = dateQuoteMap.get(quote.getDate());
					if (indexQuote == null) {
						indexQuote = new IndexQuote();

						indexQuote.setId(subject.getId() + "_" + date);
						indexQuote.setIndexCode(subject.getId() + "");
						indexQuote.setTradeDate(date);
						
						indexQuote.setClose(quote.getClose() * entry.getWeight() / 10000);// 以万为单位

						dateQuoteMap.put(date, indexQuote);
					} else {
						indexQuote.setClose(quote.getClose() * entry.getWeight() / 10000 + indexQuote.getClose());// 以万为单位
						dateQuoteMap.put(date, indexQuote);
					}
				}
			}
			indexQuoteRepository.saveAll(dateQuoteMap.values());
		}

	}

}
