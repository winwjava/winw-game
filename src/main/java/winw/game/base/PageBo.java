package winw.game.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PageBo {

	@ApiModelProperty(value = "分页查询返回最多行数", example = "10", required = true)
	private int size = 10;

	@ApiModelProperty(value = "分页查询第几页，从0开始", example = "0", required = true)
	private int page = 0;

	@ApiModelProperty(value = "分页查询排序字段及正序或倒叙，按照字段先后顺序优先排序，例如：{\"name\": \"desc\", \"age\": \"asc\" }", example = "", required = false)
	private LinkedHashMap<String, String> sort = new LinkedHashMap<String, String>();

	public void sort(String field, String order) {
		if (sort == null) {
			sort = new LinkedHashMap<String, String>();
		}
		sort.put(field, order);
	}

	public PageRequest getPageRequest() {
		if (sort == null || sort.isEmpty()) {
			return PageRequest.of(page, size);
		}

		ArrayList<Order> orderList = new ArrayList<Order>();
		for (String key : sort.keySet()) {
			orderList.add(new Order(Direction.fromString(sort.get(key)), key));
		}
		return PageRequest.of(page, size, Sort.by(orderList));
	}

}
