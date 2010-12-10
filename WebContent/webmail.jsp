<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="javax.mail.*,javax.mail.internet.*,java.util.*" %>
<%
    String from = "jinny@usemodj.com";
    String to = "usemodj@gmail.com";
    //String to = "blog@usemodj.com";
   try {
      Properties props = new Properties();
      props.put("mail.transport.protocol", "smtp");
      //props.put("mail.smtp.host", "smtp.gmail.com");
      //props.put("mail.smtp.host", "mail.usemodj.com");
      //props.put("mail.smtp.host", "127.0.0.1");
      props.put("mail.smtp.host", "localhost");
      props.put("mail.smtp.port", "25");
      //props.put("mail.smtp.starttls.enable","true");
      props.put("mail.smtp.auth", "true");  // If you need to authenticate
      //props.put("mail.user", "jinny@usemodj.com");
      //props.put("mail.password", "jinny");
      
          // Use the following if you need SSL
          //props.put("mail.smtp.socketFactory.port", 25);
         // props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //  props.put("mail.smtp.socketFactory.fallback", "false");

      javax.mail.Authenticator authenticator = new javax.mail.Authenticator() {
        protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
          //return new javax.mail.PasswordAuthentication( "usemodj@gmail.com", "qkfkathfl");
          return new javax.mail.PasswordAuthentication( "jinny@usemodj.com", "jinny");
        }
      };
      Session sess = Session.getDefaultInstance(props, authenticator);
      //Session sess = Session.getDefaultInstance(props, null);
      sess.setDebug(true);
      MimeMessage  msg = new MimeMessage(sess);
      msg.setFrom(new InternetAddress(from));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      msg.setSubject("Hello JavaMail:한글제목", "utf-8");
      msg.setText("Welcome to JavaMail:한글메시지 ", "utf-8");
      Transport transport = sess.getTransport("smtp");
      transport.connect();
      //transport.connect("mail.usemodj.com", "jinny@usemodj.com", "jinny");
      transport.send(msg);
      transport.close();
      
    } catch (Exception e) {
      System.out.println("err" + e);
      e.printStackTrace();
    }

%>

<h3>메일 전송 완료 </h3>