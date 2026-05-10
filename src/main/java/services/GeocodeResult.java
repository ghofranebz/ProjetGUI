package services;

/**
 * Point renvoyé par l’API de géocodage (Nominatim).
 */
public record GeocodeResult(String displayName, double latitude, double longitude) {

    /** Valeur stockée dans {@code services.localisation} : adresse + coordonnées précises. */
    public String toStoredLocalisation() {
        return displayName + " [" + latitude + ", " + longitude + "]";
    }

    @Override
    public String toString() {
        return displayName;
    }
}
