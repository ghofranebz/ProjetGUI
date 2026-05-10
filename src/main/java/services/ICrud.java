package services;

import java.util.List;

public interface ICrud<T> {

    void addEntity(T t);

    void addEntity2(T t);

    void deleteEntity(int id);

    void updateEntity(int id, T t);

    List<T> getAllEntities();

    T getEntityById(int id);

    void addReservation(T t);

    void annulerReservation(int id, String raison);

    List<T> getAllReservations();

    T getReservationById(int id);

    List<T> getReservationsByUser(int userId);
    float calculateTotalPrice(java.sql.Date startDate, java.sql.Date endDate, float pricePerDay);
}
