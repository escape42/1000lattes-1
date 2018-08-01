package example;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
// using SendGrid's Java Library
// https://github.com/sendgrid/sendgrid-java
import com.sendgrid.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EmailSenderSendgrid {
	private static SimpleDateFormat iCalendarDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");
	private static String ext = "@TODO@1000LattesDomain.com";

	public void myHandler(SNSEvent event, Context context) throws Exception {
		String message = event.getRecords().get(0).getSNS().getMessage();
		String[] users = message.split(" ");
		sendEmail(users[0], users[1]);
		sendEmail(users[1], users[0]);
    }
	public void testHandler(Context context) throws Exception {
        sendEmail("TODO@you@1000LattesDomain.com", "TODO@you@1000LattesDomain.com");
    }

  public static void main(String[] args) throws IOException {
//      sendEmail(args[0], args[1]);
	  sendEmail("jukyungc", "jukyungc");
  }

  public static void sendEmail(String fromUser, String toUser) throws IOException {
	  String fromEmail = fromUser + ext;
	  String toEmail = toUser + ext;
	  Email from = new Email("admin@TODO@1000LattesDomain.com");
	    String subject = "Thousand Lattes";
	    Email to = new Email(toEmail);

	    Content content = new Content("text/plain", "Open the attachment to accept your coffee appointment with " + fromUser);
	    Mail mail = new Mail(from, subject, to, content);

	    SendGrid sg = new SendGrid("TODO@sendgridId");
	    Request request = new Request();
	    try {
		Attachments attachments = new Attachments();
		attachments.setType("text/calendar");
	    attachments.setFilename("meeting.ics");
	    attachments.setDisposition("attachment");
	    attachments.setContent(getCalendarContent(fromUser, toUser));
	    mail.addAttachments(attachments);

	      request.setMethod(Method.POST);
	      request.setEndpoint("mail/send");
	      request.setBody(mail.build());
	      Response response = sg.api(request);
	      System.out.println(response.getStatusCode());
	      System.out.println(response.getBody());
	      System.out.println(response.getHeaders());
	    } catch (IOException ex) {
	      throw ex;
	    }
  }

  public static Calendar nextDayOfWeek(int dow) {
      Calendar date = Calendar.getInstance();
      int diff = dow - date.get(Calendar.DAY_OF_WEEK);

      if (!(diff > 0)) {
          diff += 7;
      }
      date.add(Calendar.DAY_OF_MONTH, diff);
      date.set(Calendar.HOUR_OF_DAY, 14);
      date.set(Calendar.MINUTE, 0);
      return date;
  }

  public static String getCalendarContent(String fromUser, String toUser){

	  Calendar cal = nextDayOfWeek(Calendar.FRIDAY);
	    Date start = cal.getTime();
	    cal.add(Calendar.HOUR_OF_DAY, 1);
	    Date end = cal.getTime();

	    String calendarContent =
                "BEGIN:VCALENDAR\n" +
                        "METHOD:REQUEST\n" +
                        "PRODID: BCP - Meeting\n" +
                        "VERSION:2.0\n" +
                        "BEGIN:VEVENT\n" +
                        "DTSTAMP:" + iCalendarDateFormat.format(start) + "\n" +
                        "DTSTART:" + iCalendarDateFormat.format(start)+ "\n" +
                        "DTEND:"  + iCalendarDateFormat.format(end)+ "\n" +
                        "SUMMARY:Coffee\n" +
                        "UID:324\n" +
                        "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE:MAILTO:organizer@TODO@domain.com\n" +
                        "ORGANIZER:MAILTO:organizer@TODO@domain.com\n" +
                        "LOCATION:7th floor kitchen\n" +
                        "DESCRIPTION:Coffee with " + fromUser + "\n" +
                        "SEQUENCE:0\n" +
                        "PRIORITY:5\n" +
                        "CLASS:PUBLIC\n" +
                        "STATUS:CONFIRMED\n" +
                        "TRANSP:OPAQUE\n" +
                        "BEGIN:VALARM\n" +
                        "ACTION:DISPLAY\n" +
                        "DESCRIPTION:REMINDER\n" +
                        "TRIGGER;RELATED=START:-PT00H15M00S\n" +
                        "END:VALARM\n" +
                        "END:VEVENT\n" +
                        "END:VCALENDAR";
	 String base64encodedString="";
	try {
		base64encodedString = Base64.getEncoder().encodeToString(calendarContent.getBytes("utf-8"));
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  System.out.println(base64encodedString);
        return base64encodedString;
  }

}
