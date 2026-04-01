package com.krstf.newsfeed;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Profile("!dev")
public class FirebaseConfig {
    @PostConstruct
    public void init() {
        try {
            String credentialsPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
            InputStream serviceAccount = (credentialsPath != null)
                    ? new FileInputStream(credentialsPath)
                    : new ClassPathResource("firebase.json").getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            System.err.println("[FirebaseConfig] Firebase credentials not found, authentication disabled: " + e.getMessage());
        }
    }
}
