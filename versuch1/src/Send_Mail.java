import java.util.Date;
import java.util.Properties;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Send_Mail {
	public static void main(String[] args) {
		sendMail();
	}

	public static void sendMail() {
		try {
			// SMTP-Server (Postfix) laeuft lokal, nur der Hostname wird benoetigt
			Properties properties = new Properties();
			properties.put("mail.smtp.host", "localhost");
			Session session = Session.getInstance(properties);

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("labrat@localhost"));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress("labrat@localhost"));
			message.setSubject("Testmail aus Versuch 1");
			message.setSentDate(new Date());
			message.setText("Hallo labrat,\n\ndies ist eine Testmail, versendet mit Jakarta Mail.\n");

			Transport.send(message);
			System.out.println("E-Mail erfolgreich versendet.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
