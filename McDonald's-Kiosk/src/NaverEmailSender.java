import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class NaverEmailSender {
    public static void main(String[] args) {
    	
    }
    public static void sendEmail(String recipient, String subject, String content) {
        // SMTP 서버 설정
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.naver.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2"); // TLSv1.2 프로토콜 설정

        // 인증 정보 설정
        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("jieyn7", "1WVLBB1L5Z1X"); // 네이버 이메일과 앱 비밀번호
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("jieyn7@naver.com"));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(recipient)
            );
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("메일이 전송되었습니다.");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
