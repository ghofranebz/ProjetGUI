package services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class GeocodingService {

    private static final String BASE = "https://nominatim.openstreetmap.org/search";
    private static final String REVERSE = "https://nominatim.openstreetmap.org/reverse";
    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    private final Gson gson = new Gson();

    private static final class NominatimItem {
        String lat;
        String lon;
        String display_name;
    }

    /**
     * Adresse lisible à partir de coordonnées GPS (clic sur carte).
     */
    public Optional<GeocodeResult> reverseGeocode(double latitude, double longitude)
            throws IOException, InterruptedException {

        String uri = String.format(Locale.US,
                "%s?lat=%f&lon=%f&format=json&zoom=18&addressdetails=1",
                REVERSE, latitude, longitude);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .header("User-Agent", "ProjetGUI/1.0 (JavaFX; contact: student project)")
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Nominatim reverse HTTP " + response.statusCode());
        }

        NominatimItem raw = gson.fromJson(response.body(), NominatimItem.class);
        if (raw == null || raw.display_name == null || raw.lat == null || raw.lon == null) {
            return Optional.empty();
        }
        try {
            double la = Double.parseDouble(raw.lat);
            double lo = Double.parseDouble(raw.lon);
            return Optional.of(new GeocodeResult(raw.display_name.trim(), la, lo));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Recherche d’adresses à partir d’un texte libre (rue, ville, pays…).
     */
    public List<GeocodeResult> searchAddresses(String query) throws IOException, InterruptedException {
        Objects.requireNonNull(query);
        String q = query.trim();
        if (q.isEmpty()) {
            return List.of();
        }

        String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
        String uri = BASE + "?q=" + encoded + "&format=json&limit=8&addressdetails=1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .header("User-Agent", "ProjetGUI/1.0 (JavaFX; contact: student project)")
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Nominatim HTTP " + response.statusCode());
        }

        Type listType = new TypeToken<List<NominatimItem>>() {}.getType();
        List<NominatimItem> raw = gson.fromJson(response.body(), listType);
        if (raw == null) {
            return List.of();
        }

        List<GeocodeResult> out = new ArrayList<>();
        for (NominatimItem item : raw) {
            if (item == null || item.display_name == null || item.lat == null || item.lon == null) {
                continue;
            }
            try {
                double la = Double.parseDouble(item.lat);
                double lo = Double.parseDouble(item.lon);
                out.add(new GeocodeResult(item.display_name.trim(), la, lo));
            } catch (NumberFormatException ignored) {
                // ignorer entrée invalide
            }
        }
        return out;
    }
}
