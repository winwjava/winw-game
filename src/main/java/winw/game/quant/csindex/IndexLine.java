package winw.game.quant.csindex;

import java.util.List;

import lombok.Data;

/**
 * 指数走势线。
 * 
 * @author winw
 *
 */
@Data
public class IndexLine {

	private String label;

	private List<String> data;
}
