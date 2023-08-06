package winw.game.quant.dataview;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class IndicesReq {

	private Sorter sorter = new Sorter();

	@Data
	class Sorter {
		private String sortField;
		private String sortOrder;
	}

	private Pager pager = new Pager();

	@Data
	class Pager {
		private int pageNum = 1;
		private int pageSize = 10;
	}
//	{"sorter":{"sortField":"null","sortOrder":null},
//		"pager":{"pageNum":1,"pageSize":10},

	private IndexFilter indexFilter = new IndexFilter();

	@Data
	class IndexFilter {
		private String ifCustomized;
		private String ifTracked;
		private String ifWeightCapped;
		private String indexCompliance;
		private String hotSpot;
		private String[] indexClassify = new String[] { "20" };
		private String currency;
		private String region;
		private String indexSeries;;
	}
//		"indexFilter":{"ifCustomized":null,"ifTracked":null,"ifWeightCapped":null,"indexCompliance":null,
//		"hotSpot":null,"indexClassify":["20"],"currency":null,"region":null,"indexSeries":null,"undefined":null}}
}
