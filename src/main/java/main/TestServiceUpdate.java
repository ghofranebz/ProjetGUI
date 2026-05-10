package main;

import entities.Service;
import services.serviceanimal;

import java.util.List;
import java.util.Scanner;

public class TestServiceUpdate {

    public static void main(String[] args) {

        serviceanimal serviceAnimal = new serviceanimal();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=" .repeat(60));
        System.out.println("🔍 RECHERCHE DE SERVICES - MODE INTERACTIF");
        System.out.println("=" .repeat(60));

        while (true) {
            System.out.println("\n" + "-".repeat(50));
            System.out.print("📝 Entrez le mot-clé à rechercher (ou 'quit' pour quitter) : ");

            String keyword = scanner.nextLine().trim();

            // Quitter le programme
            if (keyword.equalsIgnoreCase("quit") || keyword.equalsIgnoreCase("q")) {
                System.out.println("\n👋 Au revoir !");
                break;
            }

            // Recherche vide
            if (keyword.isEmpty()) {
                System.out.println("⚠️ Veuillez entrer un mot-clé valide !");
                continue;
            }

            // LANCER LA RECHERCHE
            System.out.println("\n⏳ Recherche en cours pour : \"" + keyword + "\"...\n");
            List<Service> resultats = serviceAnimal.rechercherServices(keyword);

            // AFFICHER LES RÉSULTATS
            if (resultats == null || resultats.isEmpty()) {
                System.out.println("❌ Aucun service trouvé pour \"" + keyword + "\"");
            } else {
                System.out.println("✅ " + resultats.size() + " service(s) trouvé(s) :\n");

                for (int i = 0; i < resultats.size(); i++) {
                    Service s = resultats.get(i);
                    System.out.println("   ┌─────────────────────────────────────────");
                    System.out.println("   │ [" + (i+1) + "] ID : " + s.getId_services());
                    System.out.println("   │    Titre : " + s.getTitle());
                    System.out.println("   │    Type : " + s.getType());
                    System.out.println("   │    Description : " + s.getDescription());
                    System.out.println("   │    Tarif : " + s.getTarif() + " DT");
                    System.out.println("   │    Localisation : " + s.getLocalisation());
                    System.out.println("   └─────────────────────────────────────────");
                    System.out.println();
                }
            }
        }

        scanner.close();
    }
}