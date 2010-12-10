package client;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class SmtpsHepler {
	  private static Logger logger = Logger.getLogger(SmtpsHepler.class);
	  private File cert;
	  private String host;
	  private int port = 465;
	  private String user;
	  private String password;
	 
	  public SmtpsHepler(String host, int port, String user, String password) {
	    this.host = host;
	    this.port = port;
	    this.user = user;
	    this.password = password;
	  }
	 
	  private Properties getSettingTemplate() {
	    Properties props = new Properties();
	    props.put("mail.smtp.host", host);
	    props.put("mail.from", "");
	    props.put("mail.transport.protocol", "smtps");
	    //props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.auth", "true");
	    return props;
	  }
	 
	  private void setFromMail(Properties props, String mail) {
	    props.put("mail.from", mail);
	  }
	 
	  public void sendMail(String from, String to, String subject, String content) {
	    if(cert == null){
	      try {
	        cert = CertUtil.get(host, port);
	      } catch (Exception e) {
	        logger.error(e.getMessage(), e);
	      }
	    }
	 
	    if (cert != null) {
	      System.setProperty("javax.net.ssl.trustStore", cert
	          .getAbsolutePath());
	    }
	 
	    Properties props = getSettingTemplate();
	    setFromMail(props, from);
	 
	    Session session = Session.getInstance(props, new Authenticator() {
	      @Override
	      protected PasswordAuthentication getPasswordAuthentication() {
	        return new PasswordAuthentication(user, password);
	      }
	    });
	 
	    try {
	      MimeMessage msg = new MimeMessage(session);
	      msg.setFrom();
	      msg.setRecipients(Message.RecipientType.TO, to);
	      msg.setSubject(subject);
	      msg.setSentDate(new Date());
	      msg.setText(content);
	      Transport.send(msg);
	    } catch (MessagingException e) {
	      logger.error(e.getMessage(), e);
	    }
	  }
	 
	  public static void main(String[] args) throws Exception {
	    //SmtpsHepler hepler = new SmtpsHepler("example.com", 465, "user", "password");
	    SmtpsHepler hepler = new SmtpsHepler("localhost", 25, "jinny@usemodj.com", "jinny");
	    hepler.sendMail("jinny@usemodj.com", "usemodj@gmail.com", "javamail", "hello world!");
	  }

}
