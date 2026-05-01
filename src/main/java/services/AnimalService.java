package services;

import entities.Animal;
import tools.Mydb;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnimalService implements ICrud<Animal> {

    private final Connection conn;

    public AnimalService() {
        this.conn = Mydb.getInstance().getConnection();
    }

    // ========================
    // ADD
    // ========================
    @Override
    public void add(Animal animal) throws SQLException {
        // Fixed: 11 columns + 2 timestamps = 13 total. Placeholders now match (13).
        String sql = """
            INSERT INTO animals
              (owner_id, name, species, breed, birth_date, gender,
               weight, color, photo, is_neutered,
               microchip_number, created_at, updated_at)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        PreparedStatement ps = conn.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, animal.getOwnerId());
        ps.setString(2, animal.getName());
        ps.setString(3, animal.getSpecies());
        ps.setString(4, animal.getBreed());
        ps.setDate(5, animal.getBirthDate() != null
                ? Date.valueOf(animal.getBirthDate()) : null);
        ps.setString(6, animal.getGender());
        ps.setFloat(7, animal.getWeight());
        ps.setString(8, animal.getColor());
        ps.setString(9, animal.getPhoto());
        ps.setBoolean(10, animal.isNeutered());
        ps.setString(11, animal.getMicrochipNumber());
        ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
        ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));

        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) animal.setId(keys.getInt(1));
        ps.close();
    }

    // ========================
    // GET ALL
    // ========================
    @Override
    public List<Animal> getAll() throws SQLException {
        List<Animal> list = new ArrayList<>();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(
                "SELECT * FROM animals ORDER BY created_at DESC");
        while (rs.next()) list.add(mapRow(rs));
        rs.close();
        st.close();
        return list;
    }

    // ========================
    // GET BY ID
    // ========================
    @Override
    public Animal getById(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM animals WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        Animal animal = rs.next() ? mapRow(rs) : null;
        rs.close();
        ps.close();
        return animal;
    }

    // ========================
    // GET BY OWNER
    // ========================
    public List<Animal> getByOwnerId(int ownerId) throws SQLException {
        List<Animal> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM animals WHERE owner_id = ?");
        ps.setInt(1, ownerId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        rs.close();
        ps.close();
        return list;
    }

    // ========================
    // GET BY SPECIES
    // ========================
    public List<Animal> getBySpecies(String species) throws SQLException {
        List<Animal> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM animals WHERE species = ?");
        ps.setString(1, species);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        rs.close();
        ps.close();
        return list;
    }

    // ========================
    // UPDATE
    // ========================
    @Override
    public void update(Animal animal) throws SQLException {
        String sql = """
            UPDATE animals SET
              name=?, species=?, breed=?, birth_date=?, gender=?,
              weight=?, color=?, photo=?, is_neutered=?,
              microchip_number=?, updated_at=?
            WHERE id=?
            """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, animal.getName());
        ps.setString(2, animal.getSpecies());
        ps.setString(3, animal.getBreed());
        ps.setDate(4, animal.getBirthDate() != null
                ? Date.valueOf(animal.getBirthDate()) : null);
        ps.setString(5, animal.getGender());
        ps.setFloat(6, animal.getWeight());
        ps.setString(7, animal.getColor());
        ps.setString(8, animal.getPhoto());
        ps.setBoolean(9, animal.isNeutered());
        ps.setString(10, animal.getMicrochipNumber());
        ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
        ps.setInt(12, animal.getId());
        ps.executeUpdate();
        ps.close();
    }

    // ========================
    // DELETE
    // ========================
    @Override
    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM animals WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
    }

    // ========================
    // MAPPER
    // ========================
    private Animal mapRow(ResultSet rs) throws SQLException {
        // Updated to match your new Animal constructor (no availabilityStatus)
        return new Animal(
                rs.getInt("id"),
                rs.getInt("owner_id"),
                rs.getString("name"),
                rs.getString("species"),
                rs.getString("breed"),
                rs.getDate("birth_date") != null
                        ? rs.getDate("birth_date").toLocalDate() : null,
                rs.getString("gender"),
                rs.getFloat("weight"),
                rs.getString("color"),
                rs.getBoolean("is_neutered"),
                rs.getString("microchip_number"),
                rs.getString("photo"),
                rs.getTimestamp("created_at") != null
                        ? rs.getTimestamp("created_at").toLocalDateTime() : null,
                rs.getTimestamp("updated_at") != null
                        ? rs.getTimestamp("updated_at").toLocalDateTime() : null
        );
    }
}