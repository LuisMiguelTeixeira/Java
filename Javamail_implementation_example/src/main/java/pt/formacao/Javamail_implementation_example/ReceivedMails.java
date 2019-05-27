package pt.formacao.Javamail_implementation_example;

import java.io.IOException;  
import java.util.Properties;  
import javax.mail.Folder;  
import javax.mail.Message;  
import javax.mail.MessagingException;  
import javax.mail.NoSuchProviderException;  
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.File;
import javax.mail.Address;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
//import com.sun.mail.pop3.POP3Store;  
import javax.mail.*;

// classe para receber emails da caixa de correio da conta de email
public class ReceivedMails{
	
	//atributo que recebe o path para salvar os emails
	private String saveDirectory;
	
	//método para definir o path escolhido como pré-definido como host dos emails verificados
	public void setSaveDirectory(String dir) {
        this.saveDirectory = dir;
    }
		
	
	//método que recebe as caracteristicas da conta de email escolhida e estabelece conexão à mesma
	public void receiveEmail(String pop3Host, String storeType, final String user, final String password) {  
		  
	 try {  
		 Properties properties = new Properties();  
		 properties.put("mail.store.protocol", "imaps");  
		 Session emailSession = Session.getDefaultInstance(properties,
		   new javax.mail.Authenticator() {
			 	protected PasswordAuthentication getPasswordAuthentication() {
			 		return new PasswordAuthentication(user,password);
		    }
   }); 
   
    //Criação do objeto emailStore da classe Store, através da invocação do objeto emailSession e do seu método getStore
   Store emailStore = emailSession.getStore("imaps");
    //invocação do método connect com as credenciais da conta de email
   emailStore.connect("imap.indra.es",user, password);
    
  
    //Criação da estrutura do objeto emailFolder, de Folder, que vai acolher os emails. Estabelecimento de regras de acesso somente à inbox e apenas a leitura
   Folder emailFolder = emailStore.getFolder("INBOX");  
   emailFolder.open(Folder.READ_ONLY);  
  
   	//Criação de um ArrayList de emails (objetos do tipo Message)
   Message[] messages = emailFolder.getMessages();  
   
    //ciclo for para iterar pelo ArrayList messages, que contém os emails
   for (Message email: messages) {
//   for (int i = 0; i < messages.length; i++) {  
//	   Message message = messages[i];
//	   O ciclo for também podia ser feito desta maneira, se nos interessasse um contador
	   
	
       Address[] fromAddress = email.getFrom(); //criação de um ArrayList com os "from" de cada email
       String from = fromAddress[0].toString(); //conversão em string
       String subject = email.getSubject(); //obtenção do assunto de cada email
       String sentDate = email.getSentDate().toString(); //obtenção da data de cada email e conversão em string

       String contentType = email.getContentType();
       String messageContent = "";

       
       String attachFiles = ""; // String que vai conter os nomes dos emails com anexos
       
       //Terá de ser invocado um objeto Multipart, pois o corpo da mensagem e os anexos fazem para do Multipart.
       if (contentType.contains("multipart")) {
           Multipart multiPart = (Multipart) email.getContent();
           int numberOfParts = multiPart.getCount(); //um email poderá conter vários anexos
           for (int partCount = 0; partCount < numberOfParts; partCount++) {
               MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
               if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                   String fileName = part.getFileName();// tratamento no anexo
                   attachFiles += fileName + ", ";
                   part.saveFile(saveDirectory + File.separator + fileName); //salvar o anexo no path pretendido
               } else {
                   messageContent = part.getContent().toString(); // Poderá conter o corpo do email, se existir
               }
           }

           if (attachFiles.length() > 1) {
               attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
           }
       } else if (contentType.contains("text/plain") || contentType.contains("text/html")) {
           Object content = email.getContent();
           if (content != null) {
               messageContent = content.toString();
           }
       }

       // Detalhes de cada email
       System.out.println("-------===-------");  //separador
       System.out.println("\t From: " + from); //de
       System.out.println("\t Subject: " + subject); //assunto
       System.out.println("\t Sent Date: " + sentDate); //data
       System.out.println("\t Message: " + messageContent); //corpo do email
       System.out.println("\t Attachments: " + attachFiles); //nome dos anexos
   }

   // encerrar o email store mas não o folder
   emailFolder.close(false);
   emailStore.close();
   
   //tratamento de exceções

//} catch (AuthenticationFailedException ex) {	 
} catch (NoSuchProviderException ex) {
   System.out.println("No provider for pop3.");
   ex.printStackTrace();
} catch (MessagingException ex) {
   System.out.println("Could not connect to the message store");
   ex.printStackTrace();
} catch (IOException ex) {
   ex.printStackTrace();
} 
}
   
	
	//método main com as informações da conta de email a que queremos aceder
    public static void main(String[] args) {
        String host = "imap.indra.es"; 															     //conexão ao servidor
        String port = "443";
        String username = "lmteixeira@eservicios.indracompany.com";
        String password = "chAmpion7!";
        String saveInThisDirectory = "C:/Users/lmteixeira/Desktop/Anexos_emails";
	        
        ReceivedMails receiver = new ReceivedMails();
        receiver.setSaveDirectory(saveInThisDirectory);
        
        //invocação de método receiveEmail definido na classe ReceivedMails, com os atributos da conta de email escolhida
        receiver.receiveEmail(host, port, username, password);
 
    }
  
}  