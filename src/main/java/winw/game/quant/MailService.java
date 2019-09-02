package winw.game.quant;

import java.util.Properties;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import winw.game.TradingConfig;

/**
 * 
 * @author winw
 *
 */
@ManagedBean
public class MailService extends Authenticator {

	@Resource
	private TradingConfig config;

	private String smtpHost;
	private String smtpPort;

	private String username;
	private String password;
	private String defaultRecipients;

	private Session session;

	private Properties properties;

	public MailService() {
	}

	public MailService(String smtpHost, String smtpPort, String username, String password, String defaultRecipients) {
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this.username = username;
		this.password = password;
		this.defaultRecipients = defaultRecipients;
	}

	@PostConstruct
	public void init() {
		if (config != null) {
			smtpHost = config.getMailHost();
			smtpPort = config.getMailPort();
			username = config.getMailUser();
			password = config.getMailAuth();
			defaultRecipients = config.getMailRecipients();
		}
	}

	private Session getSession() {
		if (session != null) {
			return session;
		}
		properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", smtpPort);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.ssl.enable", "true");
		// properties.put("mail.debug", "true");
		session = Session.getInstance(properties, this);
		return session;
	}

	@Override
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(username, password);
	}

	/**
	 * Send a email use defaultRecipients.
	 * 
	 * @param subject
	 * @param text
	 * @return Whether to send successfully
	 */
	public boolean send(String subject, String text) {
		return send(defaultRecipients, subject, text, null);
	}

	/**
	 * 
	 * Send a email use defaultRecipients.
	 * 
	 * @param subject
	 * @param content
	 * @param type
	 *            MIME type of this object.
	 * @return Whether to send successfully
	 */
	public boolean send(String subject, String content, String type) {
		return send(defaultRecipients, subject, content, type);
	}

	public static void main(String[] args) {
		MailService mailService = new MailService();

		mailService.smtpHost = "smtp.aliyun.com";
		mailService.smtpPort = "465";
		mailService.username = "winwjava@aliyun.com";
		mailService.password = "winw.game";
		mailService.defaultRecipients = "inwgame@sina.com";

		StringBuilder html = new StringBuilder();
		html.append("<html>");
		html.append("<h2>").append("test html").append("</h2>");
		html.append("<br>test");
		html.append("<br>");
		html.append("<h2>").append("test html").append("</h2>");
		html.append("</html>");
		mailService.send("html", html.toString(), "text/html;charset=utf-8");
	}

	/**
	 * Send a email.
	 * 
	 * @param recipients
	 *            comma separated email address strings
	 * @param subject
	 * @param content
	 * @return Whether to send successfully
	 */
	public boolean send(String recipients, String subject, String content, String type) {
		try {
			Message message = new MimeMessage(getSession());
			message.setFrom(new InternetAddress(username));
			message.setRecipients(RecipientType.TO, InternetAddress.parse(recipients));
			message.setSubject(subject);
			if (type == null) {
				message.setText(content.toString());
			} else {
				message.setContent(content, type);
			}
			Transport.send(message);
			return true;
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public String getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
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

	public String getDefaultRecipients() {
		return defaultRecipients;
	}

	public void setDefaultRecipients(String defaultRecipients) {
		this.defaultRecipients = defaultRecipients;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}
