package winw.game.stock.util;

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
	public boolean send(String subject, Object text) {
		return send(defaultRecipients, subject, text);
	}

	/**
	 * Send a email.
	 * 
	 * @param recipients
	 *            comma separated email address strings
	 * @param subject
	 * @param text
	 * @return Whether to send successfully
	 */
	public boolean send(String recipients, String subject, Object text) {
		try {
			Message message = new MimeMessage(getSession());
			message.setFrom(new InternetAddress(username));
			message.setRecipients(RecipientType.TO, InternetAddress.parse(recipients));
			message.setSubject(subject);
			message.setText(text.toString());
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
