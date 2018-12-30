package com.tungvt.email;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.WatchRequest;
import com.google.api.services.gmail.model.WatchResponse;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;

@Component
public class Startup {
	private static final String PROJECT_ID = "gmail-pub-sub-227008";

	private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();

	static class MessageReceiverExample implements MessageReceiver {
		@Override
		public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
			messages.offer(message);
			consumer.ack();
		}
	}

	private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";

	/**
	 * Global instance of the scopes required by this quickstart. If modifying
	 * these scopes, delete your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @param HTTP_TRANSPORT
	 *            The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException
	 *             If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		// Load client secrets.
		InputStream in = Startup.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("vutung1891995@gmail.com");
	}

	// [END auth_cloud_explicit
	@PostConstruct
	public void init() throws Exception {
		List<String> labelIds = new ArrayList<>();

		WatchRequest watchRequest = new WatchRequest();
		watchRequest.setTopicName("projects/gmail-pub-sub-227008/topics/gmailNotification");
		labelIds.add("INBOX");
		watchRequest.setLabelIds(labelIds);
		// Build a new authorized API client service.
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(APPLICATION_NAME).build();

		// Print the labels in the user's account.
		String user = "me";
		WatchResponse listResponse = service.users().watch("vutung1891995@gmail.com", watchRequest).execute();
		System.out.println(listResponse.toString());
	}
	// try {
	// //
	// authExplicit("C:\\Users\\Hi\\Downloads\\gmail-pub-sub-6b7c165cc33e.json");
	// authCompute();
	// } catch (Exception e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// String subscriptionId = "email-webhook";
	// ProjectSubscriptionName subscriptionName =
	// ProjectSubscriptionName.of(PROJECT_ID, subscriptionId);
	// Subscriber subscriber = null;
	// try {
	// // create a subscriber bound to the asynchronous message receiver
	// subscriber = Subscriber.newBuilder(subscriptionName, new
	// MessageReceiverExample()).build();
	// subscriber.startAsync().awaitRunning();
	// // Continue to listen to messages
	// while (true) {
	// PubsubMessage message = messages.take();
	// System.out.println("Message Id: " + message.getMessageId());
	// System.out.println("Data: " + message.getData().toStringUtf8());
	// }
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// if (subscriber != null) {
	// subscriber.stopAsync();
	// }
	// }
	// }
}
