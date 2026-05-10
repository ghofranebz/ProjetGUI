package main;


import entities.Service;
import entities.Reservation;
import services.Serviceanimal;
import services.ServiceReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class TestService {
    public static void main(String[] args) {

        Serviceanimal serviceAnimal = new Serviceanimal();
        ServiceReservation serviceReservation = new ServiceReservation();
        Scanner sc = new Scanner(System.in);

        try {


            System.out.println("=== Ajouter un service ===");

            System.out.print("Title : ");
            String title = sc.nextLine();

            System.out.print("Type : ");
            String type = sc.nextLine();

            System.out.print("Description : ");
            String description = sc.nextLine();

            System.out.print("Tarif : ");
            float tarif = sc.nextFloat();
            sc.nextLine();

            System.out.print("Localisation : ");
            String localisation = sc.nextLine();

            System.out.print("User ID : ");
            int user_id = sc.nextInt();
            sc.nextLine();

            Service s1 = new Service();
            s1.setTitle(title);
            s1.setType(type);
            s1.setDescription(description);
            s1.setTarif(tarif);
            s1.setLocalisation(localisation);
            s1.setUser_id(user_id);
            s1.setCreatedAt(LocalDateTime.now());

            serviceAnimal.addEntity(s1);
            System.out.println("Service ajouté !");


            List<Service> allServices = serviceAnimal.getAllEntities();

            System.out.println("\n=== Liste des services ===");
            allServices.forEach(System.out::println);


            System.out.println("\n=== Ajouter une réservation ===");

            System.out.print("ID Service : ");
            int idService = sc.nextInt();

            System.out.print("ID Animal : ");
            int idAnimal = sc.nextInt();

            System.out.print("ID Client : ");
            int idClient = sc.nextInt();
            sc.nextLine();

            System.out.print("Date début (yyyy-mm-dd) : ");
            String startDate = sc.nextLine();

            System.out.print("Date fin (yyyy-mm-dd) : ");
            String endDate = sc.nextLine();

            System.out.print("Prix total : ");
            float totalPrice = sc.nextFloat();
            sc.nextLine();

            Reservation r = new Reservation();
            r.setId_service(idService);
            r.setAnimal_id(idAnimal);
            r.setClient_id(idClient);
            r.setStart_date(java.sql.Date.valueOf(startDate));
            r.setEnd_date(java.sql.Date.valueOf(endDate));
            r.setTotal_price(totalPrice);
            r.setCancelled_reason(null);

            serviceReservation.addEntity(r);
            System.out.println("Réservation ajoutée !");


            List<Reservation> allReservations = serviceReservation.getAllEntities();

            System.out.println("\n=== Liste des réservations ===");
            allReservations.forEach(System.out::println);


            System.out.print("\nID réservation à annuler : ");
            int idCancel = sc.nextInt();
            sc.nextLine();

            System.out.print("Raison annulation : ");
            String reason = sc.nextLine();

            serviceReservation.annulerReservation(idCancel, reason);


            System.out.print("\nID service à supprimer : ");
            int idDelete = sc.nextInt();

            serviceAnimal.deleteEntity(idDelete);

        } catch (Exception e) {
            e.printStackTrace();
        }

        sc.close();
    }
}
