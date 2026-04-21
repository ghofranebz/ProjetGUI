package services;

import java.sql.SQLException;
import java.util.List;

import entities.Reservation;
import entities.Service;
import java.util.List;

public interface ICrud<T> {
    void add(T entity) throws SQLException;
    List<T> getAll() throws SQLException;
    T getById(int id) throws SQLException;
    void update(T entity) throws SQLException;
    void delete(int id) throws SQLException;

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
}
