## Synopsis

AWS lambda function that sends emails to two users based on their email ids. EmailSenderSMTP.java uses SMTP+java mail to send the emails and EmailSenderSendgrid.java uses Sendgrid api. 

Package the project with eclipse IDE by going to Run as->Maven build... and type "package" for goal. Then package again using "package shade:shade" to get the aws lambda jar.
