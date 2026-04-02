package com.krstf.newsfeed;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Profile("!dev")
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void init() {
        String credentialsPath = System.getenv("FIREBASE_CREDENTIALS_PATH");
        log.info("[Firebase] FIREBASE_CREDENTIALS_PATH={}", credentialsPath);

        try {
            InputStream serviceAccount;
            if (credentialsPath != null) {
                File file = new File(credentialsPath);
                log.info("[Firebase] File exists={}, readable={}, size={}", file.exists(), file.canRead(), file.length());
                serviceAccount = new FileInputStream(file);
            } else {
                ClassPathResource resource = new ClassPathResource("firebase.json");
                log.info("[Firebase] Falling back to classpath, exists={}", resource.exists());
                serviceAccount = resource.getInputStream();
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("[Firebase] Initialized successfully");
        } catch (IOException e) {
            log.error("[Firebase] Failed to initialize, authentication will be disabled: {}", e.getMessage());
        }
    }
}
