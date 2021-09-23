package winw.game.quant.csindex;

import java.util.List;

import lombok.Data;

@Data
public class PageResult {

	private Integer total;
	private String page_size;

	private Integer total_page;
	private List<SubjectIndex> list;// 
//	"total":288,
//	"page_size":"50",
//	"total_page":7,
//	"list":[
}
