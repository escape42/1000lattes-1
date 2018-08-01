package example;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class EmailSenderSMTP {
	private static String ext = "@TODO@1000LattesDomain.com";

	public void myHandler(SNSEvent event, Context context) throws Exception {

		System.out.println("Running now.");
		String message = event.getRecords().get(0).getSNS().getMessage();
		String[] users = message.split(" ");
		sendEmail(users[0], users[1]);    }

	public static void main(String[] args) throws Exception {
		String message = "jukyungc jukyungc";
		String[] users = message.split(" ");
		sendEmail(users[1], users[0]);
	}

	/**
	 * Returns the date of the next dow-day.
	 * @param dow Integer corresponding to the day of week
	 * @return
	 */
	  public static Calendar nextDayOfWeek(int dow) {
	      Calendar date = Calendar.getInstance();
	      int diff = dow - date.get(Calendar.DAY_OF_WEEK);

	      if (!(diff > 0)) {
	          diff += 7;
	      }
	      date.add(Calendar.DAY_OF_MONTH, diff);
	      date.set(Calendar.HOUR_OF_DAY, 21);
	      date.set(Calendar.MINUTE, 0);
	      return date;
	  }

  /**
   * Sends emails to the users that have been matched.
   * @param user1
   * @param user2
   * @throws Exception
   */
	public static void sendEmail(String user1, String user2) throws Exception{
		String fromEmail = user1 + ext;
		String toEmail = user2 + ext;
		final String username = "TODO@admin@1000LattesDomain.com";
		final String password = "Sheaves-pentagon-154";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.1and1.com");
		props.put("mail.smtp.port", "25");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {
			//register the text/calendar mime type
			MimetypesFileTypeMap mimetypes = (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
			mimetypes.addMimeTypes("text/calendar ics ICS");
			//register the handling of text/calendar mime type
		    MailcapCommandMap mailcap = (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
		    mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");

		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(new InternetAddress("TODO@admin@1000LattesDomain.com"));
		    message.setSubject("Thousand Lattes");
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(fromEmail));

		    // Create an alternative Multipart
		    Multipart multipart = new MimeMultipart("alternative");

		  //part 1, html text
		    BodyPart messageBodyPart = buildHtmlTextPart(user1, user2);
		    multipart.addBodyPart(messageBodyPart);

		 // Add part two, the calendar
//		    BodyPart calendarPart = buildCalendarPart(user1, user2);
//		    multipart.addBodyPart(calendarPart);

		  //Put the multipart in message
		    message.setContent(multipart);

		    // send the message
		    Transport transport = session.getTransport("smtp");
		    transport.connect();
		    transport.sendMessage(message, message.getAllRecipients());
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
		    message.addRecipient(Message.RecipientType.TO, new InternetAddress(fromEmail));
		    transport.close();
			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}


	private static BodyPart buildHtmlTextPart(String fromUser, String toUser) throws MessagingException {

        MimeBodyPart descriptionPart = new MimeBodyPart();

        //Note: even if the content is spcified as being text/html, outlook won't read correctly tables at all
        // and only some properties from div:s. Thus, try to avoid too fancy content


//        "<div style=\"color:red;\">BRIDGEYE</div>"


        String content =
//        		+ "<h1 style=\"color:black\">content></h1>"
        		"<div style=\"width:500px;\">"
        			+ "<div style=\"background-color:rgb(184,198,206);padding-left:50px;padding-right:50px;text-align:center;padding-top:0px;margin-top:0px;padding-bottom:5px\">"
        				+ "<img src=\"https://TODO@1000LattesDomain.com/1000lattes/1000_Lattes_logo.png\" style=\"width:70px;\"alt=\"Mountain View\">"
	        			+ "	<h3 style=\"font-size:20px\">Hi " + fromUser + ", you have a new Coffee Buddy!</h3>"
	        			+ "<p style=\"font-size:16px\">You have been matched with " + toUser + " for a 1,000 Lattes<br>"
	        			+ "meetup for a latte at your favorite coffee shop.</p>"
        			+ "</div>"
	    			+ "<div style=\"padding-left:25px;padding-right:25px;text-align:center\">"
	        			// images
	        			+"<p style=\"font-size:16px\">We did our best to match you with someone with similar interests but in the event that no co-workers surfaced with similar interests to you, you became an \"interest orphan\" and we paired you with another \"interest orphan.\"</p>"
	        			+ "<div style=\"font-weight:bold;font-size:18px;\">"
//	        				+ "<p>Meet in at your favorite coffee shop</p>"
						+ "<h3>Set up a time and meet at your favorite coffee shop</h3>"
	    				+ "</div>"
    				+"</div>"
	    			+"<div style=\"width:500px;height:20px;background-color:rgb(184,198,206)\">"
        		+"</div>";
        descriptionPart.setContent(content, "text/html; charset=utf-8");

        return descriptionPart;
    }

    //define somewhere the icalendar date format
    private static SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

//    private static BodyPart buildCalendarPart(String fromUser, String toUser) throws Exception {
//
//    	Calendar cal = nextDayOfWeek(Calendar.FRIDAY);
//    	Date start = cal.getTime();
//	    cal.add(Calendar.MINUTE, 30);
//	    Date end = cal.getTime();
//        BodyPart calendarPart = new MimeBodyPart();
//
//        //check the icalendar spec in order to build a more complicated meeting request
//        String calendarContent =
//        		 "BEGIN:VCALENDAR\n" +
//                         "METHOD:REQUEST\n" +
//                         "PRODID: BCP - Meeting\n" +
//                         "VERSION:2.0\n" +
//                         "BEGIN:VEVENT\n" +
//                         "DTSTART:" + iCalendarDateFormat.format(start)+ "Z\n" +
//                         "DTEND:"  + iCalendarDateFormat.format(end)+ "Z\n" +
//
//                         "SUMMARY:Coffee: " + toUser + ", " + fromUser + "\n" +
//                         "UID:324\n" +
//                         "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE:MAILTO:TODO@admin@1000LattesDomain.com\n" +
//                         "ORGANIZER:MAILTO:TODO@admin@1000LattesDomain.com\n" +
//                         "LOCATION:7th floor kitchen\n" +
//                         "DESCRIPTION:Coffee: " + toUser + ", " + fromUser + "\n" +
//                         "SEQUENCE:0\n" +
//                         "PRIORITY:5\n" +
//                         "CLASS:PUBLIC\n" +
//                         "STATUS:CONFIRMED\n" +
//                         "TRANSP:OPAQUE\n" +
//                         "BEGIN:VALARM\n" +
//                         "ACTION:DISPLAY\n" +
//                         "DESCRIPTION:REMINDER\n" +
//                         "TRIGGER;RELATED=START:-PT00H15M00S\n" +
//                         "END:VALARM\n" +
//                         "END:VEVENT\n" +
//                         "END:VCALENDAR";
//
//        calendarPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
//        calendarPart.setContent(calendarContent, "text/calendar;method=CANCEL");
//
//        return calendarPart;
//    }


}
