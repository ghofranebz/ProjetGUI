package main;

import entities.Reservation;
import entities.Service;
import services.ServiceReservation;
import services.Serviceanimal;

import java.util.List;
import java.util.Scanner;

public class TestReservation {

    public static void main(String[] args) {

        Serviceanimal serviceAnimal = new Serviceanimal();
        ServiceReservation serviceReservation = new ServiceReservation();
        Scanner sc = new Scanner(System.in);

        try {


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



            Reservation r = new Reservation();
            r.setId_service(idService);
            r.setAnimal_id(idAnimal);
            r.setClient_id(idClient);
            r.setStart_date(java.sql.Date.valueOf(startDate));
            r.setEnd_date(java.sql.Date.valueOf(endDate));
            r.setCancelled_reason(null);


            serviceReservation.addEntity(r);

            System.out.println("Réservation ajoutée avec prix calculé automatiquement !");


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
