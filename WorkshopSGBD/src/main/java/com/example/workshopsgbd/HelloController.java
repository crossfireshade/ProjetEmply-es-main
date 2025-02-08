package com.example.workshopsgbd;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    public TableView emloyeesTable;
    @FXML
    private TableColumn<Employee, String> nomColumn;
    @FXML
    public TableColumn<Employee, String> emailColumn;
    public TextField nomField;
    public TextField emailFiled;
    public Button ajouterButton;
    public Button modifierButton;
    public Button supprimerButton;
    private final ObservableList<Employee> employeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nomColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nom"));
        emailColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));


        emloyeesTable.setItems(employeList);
        loadEmployesFromDatabase();

        emloyeesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            Employee employe = (Employee) newSelection; // Cast en Employe
            if (employe != null) {
                nomField.setText(employe.getNom());
                emailFiled.setText(employe.getEmail());
            }
        });


    }
    public void handleAjouter(ActionEvent actionEvent) {
        String nom = nomField.getText();
        String email = emailFiled.getText();

        if (!nom.isEmpty() && !email.isEmpty()) {
            String query = "INSERT INTO employees (nom, email) VALUES (?, ?)";

            try (Connection conn = ConnectionDB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, nom);
                stmt.setString(2, email);
                stmt.executeUpdate();

                loadEmployesFromDatabase();
                nomField.clear();
                emailFiled.clear();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        } else {
            showAlert( "Erreur","Veuillez remplir tous les champs.");
        }
    }

    public void handleModifer(ActionEvent actionEvent) {
        Employee selectedEmploye = (Employee) emloyeesTable.getSelectionModel().getSelectedItem();
        if (selectedEmploye != null) {
            String query = "UPDATE employees SET nom = ?, email = ? WHERE id = ?";

            try (Connection conn = ConnectionDB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, nomField.getText());
                stmt.setString(2, emailFiled.getText());
                stmt.setInt(3, selectedEmploye.getId());

                stmt.executeUpdate();
                loadEmployesFromDatabase();
                nomField.clear();
                emailFiled.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Modification échouée.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un employé à modifier.");
        }
    }

    public void handleSupprimer(ActionEvent actionEvent) {
        Employee selectedEmploye = (Employee) emloyeesTable.getSelectionModel().getSelectedItem();
        if (selectedEmploye != null) {
            String query = "DELETE FROM employees  WHERE id = ?";

            try (Connection conn = ConnectionDB.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {


                stmt.setInt(1, selectedEmploye.getId());

                stmt.executeUpdate();
                loadEmployesFromDatabase();
                nomField.clear();
                emailFiled.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "suppression échouée.");
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un employé à supprimer.");
        }
    }
    private void loadEmployesFromDatabase() {
        employeList.clear();
        String query = "SELECT * FROM employees";

        try (Connection conn = ConnectionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employeList.add(new Employee(rs.getInt("id"), rs.getString("nom"), rs.getString("email")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}