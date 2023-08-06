package winw.game.quant.dataview;

import java.util.List;

import lombok.Data;

@Data
public class IndicesResp<T> {

//	"append": null,
	private int size;
	private int total;
	private String code;
	private String msg;
	private int pageSize;
	private int currentPage;
	private boolean success;
	private List<T> data;
}
