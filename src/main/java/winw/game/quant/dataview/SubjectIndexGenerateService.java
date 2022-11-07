package winw.game.quant.dataview;

import javax.annotation.ManagedBean;

import org.springframework.scheduling.annotation.Scheduled;

@ManagedBean
public class SubjectIndexGenerateService {

	@Scheduled(cron = "00 00 15-19 * * 1-5")
	public void generateSubjectIndex() throws Exception {
		// 生成主题指数，根据配置表，根据固定权重占比？
	}
}
