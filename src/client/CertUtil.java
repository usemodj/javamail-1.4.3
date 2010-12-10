package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

public class CertUtil {
	 
	 
	  private static Logger logger = Logger.getLogger(CertUtil.class);
	 
	  public static File get(String host, int port) throws Exception {
	    char[] passphrase = "changeit".toCharArray();
	 
	    File file = new File("jssecacerts");
	    if (file.isFile() == false) {
	      char SEP = File.separatorChar;
	      File dir = new File(System.getProperty("java.home") + SEP + "lib"
	          + SEP + "security");
	      file = new File(dir, "jssecacerts");
	      if (file.isFile() == false) {
	        file = new File(dir, "cacerts");
	      }
	    }
	 
	    logger.info("Loading KeyStore " + file + "...");
	    InputStream in = new FileInputStream(file);
	    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
	    ks.load(in, passphrase);
	    in.close();
	 
	    SSLContext context = SSLContext.getInstance("TLS");
	    TrustManagerFactory tmf = TrustManagerFactory
	        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
	    tmf.init(ks);
	    X509TrustManager defaultTrustManager = (X509TrustManager) tmf
	        .getTrustManagers()[0];
	    SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
	    context.init(null, new TrustManager[] { tm }, null);
	    SSLSocketFactory factory = context.getSocketFactory();
	 
	    System.out
	        .println("Opening connection to " + host + ":" + port + "...");
	    SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
	    socket.setSoTimeout(10000);
	    try {
	      logger.info("Starting SSL handshake...");
	      socket.startHandshake();
	      socket.close();
	      logger.info("No errors, certificate is already trusted");
	    } catch (SSLException e) {
	      e.printStackTrace(System.out);
	    }
	 
	    X509Certificate[] chain = tm.chain;
	    if (chain == null) {
	      logger.info("Could not obtain server certificate chain");
	      return null;
	    }
	 
	    logger.info("Server sent " + chain.length + " certificate(s):");
	    MessageDigest sha1 = MessageDigest.getInstance("SHA1");
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    for (int i = 0; i < chain.length; i++) {
	      X509Certificate cert = chain[i];
	      logger.info(" " + (i + 1) + " Subject " + cert.getSubjectDN());
	      logger.info("   Issuer  " + cert.getIssuerDN());
	      sha1.update(cert.getEncoded());
	      logger.info("   sha1    " + toHexString(sha1.digest()));
	      md5.update(cert.getEncoded());
	      logger.info("   md5     " + toHexString(md5.digest()));
	    }
	 
	    int k = 1;
	 
	    X509Certificate cert = chain[k];
	    String alias = host + "-" + (k + 1);
	    ks.setCertificateEntry(alias, cert);
	 
	    File cafile = new File("jssecacerts");
	    OutputStream out = new FileOutputStream(cafile);
	    ks.store(out, passphrase);
	    out.close();
	 
	    logger.info(cert);
	    logger.info("Added certificate to keystore 'jssecacerts' using alias '"
	        + alias + "'");
	    return cafile;
	  }
	 
	  private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();
	 
	  private static String toHexString(byte[] bytes) {
	    StringBuilder sb = new StringBuilder(bytes.length * 3);
	    for (int b : bytes) {
	      b &= 0xff;
	      sb.append(HEXDIGITS[b >> 4]);
	      sb.append(HEXDIGITS[b & 15]);
	      sb.append(' ');
	    }
	    return sb.toString();
	  }
	 
	  private static class SavingTrustManager implements X509TrustManager {
	 
	    private final X509TrustManager tm;
	    private X509Certificate[] chain;
	 
	    SavingTrustManager(X509TrustManager tm) {
	      this.tm = tm;
	    }
	 
	    public X509Certificate[] getAcceptedIssuers() {
	      throw new UnsupportedOperationException();
	    }
	 
	    public void checkClientTrusted(X509Certificate[] chain, String authType)
	        throws CertificateException {
	      throw new UnsupportedOperationException();
	    }
	 
	    public void checkServerTrusted(X509Certificate[] chain, String authType)
	        throws CertificateException {
	      this.chain = chain;
	      tm.checkServerTrusted(chain, authType);
	    }
	  }
	 

}
