import java.util.Properties;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;

public class Receive_Mail {
	public static void main(String[] args) throws Exception {
		fetchMail();
	}

	public static void fetchMail() {
		try {
			// POP3-Server auf localhost
			Properties properties = new Properties();
			properties.put("mail.store.protocol", "pop3");
			properties.put("mail.pop3.host", "localhost");
			properties.put("mail.pop3.port", "110");
			Session session = Session.getInstance(properties);

			Store store = session.getStore("pop3");
			store.connect("localhost", "labrat", "kn1lab");

			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);

			Message[] messages = inbox.getMessages();
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				System.out.println("---------------- E-Mail " + (i + 1) + " ----------------");
				System.out.println("Absender:   " + message.getFrom()[0]);
				System.out.println("Betreff:    " + message.getSubject());
				System.out.println("Datum:      " + message.getSentDate());
				System.out.println("Inhalt:");
				System.out.println(message.getContent());
			}

			inbox.close(false);
			store.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
