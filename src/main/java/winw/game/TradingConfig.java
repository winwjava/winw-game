package winw.game;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import winw.game.quant.QuantTradingStrategy;
import winw.game.quant.strategy.MeanReversionStrategy;
import winw.game.quant.strategy.TrendFollowingStrategy;

/**
 * 交易券商和策略配置。
 * 
 * @author winw
 *
 */
@Component
@ConfigurationProperties(prefix = "trading", ignoreUnknownFields = true)
public class TradingConfig {

	/**
	 * 券商，如果为空，则使用模拟交易。
	 */
	private String broker;
	/**
	 * 券商接口用户名
	 */
	private String username;
	/**
	 * 券商接口密码。
	 */
	private String password;
	/**
	 * 券商接口程序位置。
	 */
	private String brokerExe;
	/**
	 * 投资组合名称。
	 */
	private String portfolio;

	private Double initAssets;// 初始资产
	private Integer maxPosition;// 最多持仓
	private Double drawdownLimit;// 回撤限制。
	private Double stoplossLimit;// 亏损限制。

	private String mailHost;
	private String mailPort;

	private String mailUser;
	private String mailAuth;

	private String mailRecipients;

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(String portfolio) {
		this.portfolio = portfolio;
	}

	public Class<? extends QuantTradingStrategy> getStrategy() {
		if ("MeanReversionStrategy(300_TOP)".equals(portfolio)) {
			return MeanReversionStrategy.class;
		}
		if ("TrendFollowingStrategy(300_ETF)".equals(portfolio)) {
			return TrendFollowingStrategy.class;
		}
		if ("TrendFollowingStrategy(300_TOP)".equals(portfolio)) {
			return TrendFollowingStrategy.class;
		}
		return null;
	}

	public String getBrokerExe() {
		return brokerExe;
	}

	public void setBrokerExe(String brokerExe) {
		this.brokerExe = brokerExe;
	}

	public Double getInitAssets() {
		return initAssets;
	}

	public void setInitAssets(Double initAssets) {
		this.initAssets = initAssets;
	}

	public Integer getMaxPosition() {
		return maxPosition;
	}

	public void setMaxPosition(Integer maxPosition) {
		this.maxPosition = maxPosition;
	}

	public Double getDrawdownLimit() {
		return drawdownLimit;
	}

	public void setDrawdownLimit(Double drawdownLimit) {
		this.drawdownLimit = drawdownLimit;
	}

	public Double getStoplossLimit() {
		return stoplossLimit;
	}

	public void setStoplossLimit(Double stoplossLimit) {
		this.stoplossLimit = stoplossLimit;
	}

	public String getMailHost() {
		return mailHost;
	}

	public void setMailHost(String mailHost) {
		this.mailHost = mailHost;
	}

	public String getMailPort() {
		return mailPort;
	}

	public void setMailPort(String mailPort) {
		this.mailPort = mailPort;
	}

	public String getMailUser() {
		return mailUser;
	}

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public String getMailAuth() {
		return mailAuth;
	}

	public void setMailAuth(String mailAuth) {
		this.mailAuth = mailAuth;
	}

	public String getMailRecipients() {
		return mailRecipients;
	}

	public void setMailRecipients(String mailRecipients) {
		this.mailRecipients = mailRecipients;
	}

}
