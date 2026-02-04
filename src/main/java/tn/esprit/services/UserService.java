package tn.esprit.services;

import tn.esprit.entities.User;
import tn.esprit.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService<User> {

    private Connection connection;

    public UserService() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    @Override
    public void add(User user) throws SQLException {
        String query = "INSERT INTO user (nom, email, password, role) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getNom());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getRole());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Utilisateur ajouté avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
            throw e;
        }

        return users;
    }

    @Override
    public void update(User user) throws SQLException {
        String query = "UPDATE user SET nom = ?, email = ?, password = ?, role = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, user.getNom());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getRole());
            preparedStatement.setInt(5, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Utilisateur mis à jour avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec l'ID : " + user.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(User user) throws SQLException {
        String query = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, user.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Utilisateur supprimé avec succès !");
            } else {
                System.out.println("Aucun utilisateur trouvé avec l'ID : " + user.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }


    public User getUserById(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
            throw e;
        }

        return null;
    }

    public User getUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
            throw e;
        }

        return null;
    }
}
