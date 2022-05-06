package com.ozerutkualtun.gcad.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.api.services.calendar.Calendar;
import com.ozerutkualtun.gcad.model.ApiResponse;
import com.ozerutkualtun.gcad.service.EventService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;

@Service
@Getter
@Setter
@Slf4j
public class EventServiceImpl implements EventService {

    @Value("${calendar.service.application-name}")
    private String applicationName;

    @Value("${calendar.service.account-id}")
    private String serviceAccountId;

    private URL url;
    private File keyFile;
    private JsonFactory jsonFactory;

    EventServiceImpl(@Value("${calendar.service.auth-file-path}") String authFilePath) {
        this.url = getClass().getResource(authFilePath);
        this.keyFile = new File(url.getPath());
        this.jsonFactory = new JacksonFactory();
    }

    //    GoogleClientSecrets clientSecrets;  // for Oauth 2.0
    //    GoogleAuthorizationCodeFlow flow;

    @Override
    public ApiResponse createEvent() {

        List<Event> eventsResponse;
        com.google.api.services.calendar.model.Events eventList;
        HttpTransport transport;
        Credential credential;
        Calendar client;

        // 	TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
        // 	credential = flow.createAndStoreCredential(response, "userID"); //for Oauth2

        Preconditions.checkArgument(!Strings.isNullOrEmpty(applicationName), "applicationName cannot be null or empty!");

        try {

            transport = GoogleNetHttpTransport.newTrustedTransport();
            credential = new GoogleCredential.Builder().setTransport(transport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(serviceAccountId)
                    .setServiceAccountScopes(Collections.singleton(CalendarScopes.CALENDAR))
                    .setServiceAccountPrivateKeyFromP12File(keyFile)
                    .build();

            credential.refreshToken();
            client = new com.google.api.services.calendar.Calendar.Builder(transport, jsonFactory, credential)
                    .setApplicationName(applicationName).build();

            Events events = client.events();
            eventList = events.list("primary").setTimeMin(new DateTime("2022-07-31T09:00:00-07:00")).setTimeMax(new DateTime("2022-08-01T17:00:00-07:00")).execute();

            eventsResponse = eventList.getItems();

            Event event = new Event()
                    .setSummary("Sample Event")
                    .setLocation("Sample Location in somewhere")
                    .setDescription("Sample description");

            DateTime startDateTime = new DateTime("2022-07-31T09:00:00-07:00");
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Turkey");
            event.setStart(start);

            DateTime endDateTime = new DateTime("2022-08-01T17:00:00-07:00");
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Turkey");
            event.setEnd(end);

            String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
            event.setRecurrence(Arrays.asList(recurrence));

            //	EventAttendee[] attendees = new EventAttendee[] {
            //	new EventAttendee().setEmail("sample@gmail.com"),
            //	};
            //	event.setAttendees(Arrays.asList(attendees));

            // NOTE: Service accounts cannot invite attendees without Domain-Wide Delegation of Authority Exception. (Workspace account istiyor.)  reference: https://developers.google.com/admin-sdk/directory/v1/guides/delegation

            EventReminder[] reminderOverrides = new EventReminder[]{
                    new EventReminder().setMethod("email").setMinutes(24 * 60),
                    new EventReminder().setMethod("popup").setMinutes(10),
            };
            Event.Reminders reminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(Arrays.asList(reminderOverrides));
            event.setReminders(reminders);
            client.events().insert("utkuininal@gmail.com", event).execute(); // ekleme işlemi.


            return new ApiResponse(true, eventsResponse);

        } catch (IOException | GeneralSecurityException e) {
            log.info(e.toString());
        }
        return new ApiResponse(false, "Something went wrong.");
    }

}
