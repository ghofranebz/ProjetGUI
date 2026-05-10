-- Table des avis (une ligne par réservation).
-- À exécuter sur la base MySQL « purrly » si la création automatique au runtime échoue.

CREATE TABLE IF NOT EXISTS service_reviews (
    id_review INT AUTO_INCREMENT PRIMARY KEY,
    id_booking INT NOT NULL UNIQUE,
    client_id INT NOT NULL,
    id_service INT NOT NULL,
    rating TINYINT NOT NULL,
    comment VARCHAR(1024),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_service (id_service),
    INDEX idx_client (client_id)
);
