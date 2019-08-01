package winw.game.quant.util;

import java.util.Properties;

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

public class MailService {

	private String username = "winwjava@aliyun.com";
	private String password = "winw.game";
	private String defaultRecipients = "winwgame@sina.com";

	private Properties properties;

	private Session session;

	private Session getSession() {
		if (session != null) {
			return session;
		}
		if (properties == null) {
			properties = new Properties();
			properties.put("mail.transport.protocol", "smtp");
			properties.put("mail.smtp.host", "smtp.aliyun.com");
			properties.put("mail.smtp.port", 465);
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.ssl.enable", "true");
			// properties.put("mail.debug", "true");
		}
		session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}

		});
		return session;
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

}
