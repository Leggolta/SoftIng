package org.example;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.gax.core.FixedCredentialsProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GoogleCredentialsProvider {
    private static final String CREDENTIALS_PATH = "credentials/credentials.json";
    private static FixedCredentialsProvider provider;

    /**
     * Returns a FixedCredentialsProvider loaded from the JSON file.
     * Caches the provider so the file is read only once.
     * @throws IOException if the credentials file is missing or unreadable
     */
    public static FixedCredentialsProvider getProvider() throws IOException {
        if (provider == null) {
            Path credPath = Paths.get(CREDENTIALS_PATH).toAbsolutePath();
            try (FileInputStream fis = new FileInputStream(credPath.toFile())) {
                GoogleCredentials creds = GoogleCredentials
                        .fromStream(fis)
                        .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
                provider = FixedCredentialsProvider.create(creds);
            }
        }
        return provider;
    }
}