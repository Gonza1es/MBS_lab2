package com.example.lab2;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static java.util.List.*;


public class HelloController {

    @FXML
    public TableView<Person> table;
    public Button addNameButton;
    public TextField nameField;
    public Button addLetterButton;
    public TextField charField;
    public Button startButton;

    private Set<String> letters = new HashSet<>(of("A", "B", "C"));

    @FXML
    public void initialize() {
        ObservableList<Person> tableData = FXCollections.observableArrayList(
                new Person("Петр", new HashSet<>(of("A", "B", "C"))),
                new Person("Иван", new HashSet<>(of("A", "C")))
        );

        table.getItems().addAll(tableData);
        table.setEditable(true);
        TableColumn<Person, String> nameColumn = new TableColumn<>("Имя");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.getColumns().add(nameColumn);
        letters.forEach(it -> {
            TableColumn<Person, Boolean> letterColumn = new TableColumn<>(it);
            letterColumn.setCellValueFactory(cellData ->
                    new SimpleBooleanProperty(cellData.getValue().getAvailableLetters().contains(it)));
            letterColumn.setCellFactory(p -> {
                CheckBox checkBox = new CheckBox();
                TableCell<Person, Boolean> tableCell = new TableCell<>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {

                        super.updateItem(item, empty);
                        if (empty || item == null)
                            setGraphic(null);
                        else {
                            setGraphic(checkBox);
                            checkBox.setSelected(item);
                        }
                    }
                };
                checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    tableCell.getTableRow().getItem().updateSet(!checkBox.isSelected(), it);
                });
                tableCell.setAlignment(Pos.CENTER);
                tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                return tableCell;
            });
            table.getColumns().add(letterColumn);
        });


        addNameButton.setOnAction(actionEvent -> {
            String name = nameField.getText();
            if (validateField(name, false)) {
                Person person = new Person(name, new HashSet<>());
                table.getItems().add(person);
            }
        });

        addLetterButton.setOnAction(actionEvent -> {
            String letter = charField.getText();
            if (validateField(letter, true)) {
                TableColumn<Person, Boolean> column = new TableColumn<>(letter);
                column.setCellValueFactory(cellData ->
                        new SimpleBooleanProperty(cellData.getValue().getAvailableLetters().contains(letter)));
                column.setCellFactory(p -> {
                    CheckBox checkBox = new CheckBox();
                    TableCell<Person, Boolean> tableCell = new TableCell<>() {
                        @Override
                        protected void updateItem(Boolean item, boolean empty) {

                            super.updateItem(item, empty);
                            if (empty || item == null)
                                setGraphic(null);
                            else {
                                setGraphic(checkBox);
                                checkBox.setSelected(item);
                            }
                        }
                    };
                    checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                        tableCell.getTableRow().getItem().updateSet(!checkBox.isSelected(), letter);
                    });
                    tableCell.setAlignment(Pos.CENTER);
                    tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                    return tableCell;
                });
                letters.add(letter);
                table.getColumns().add(column);
            }
        });

        startButton.setOnAction(actionEvent -> {
            try {
                table.refresh();
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("modal.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 591, 370);
                Stage stage = new Stage();
                ModalController elevatorController = fxmlLoader.getController();
                elevatorController.initialize(table.getItems(), letters);
                stage.setTitle("Лифт");
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException e) {
                alert("Что-то пошло не так");
            }
        });
    }






    private boolean validateField(String field, Boolean allowWhiteSpace) {
        if (allowWhiteSpace) {
            if (field.isEmpty()) {
                alert("Поле не может быть пустым");
                return false;
            }
        } else {
            if (field.isBlank()) {
                alert("Поле не может содержать пробелы или быть пустым");
                return false;
            }
        }
        return true;
    }


    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);

        alert.showAndWait();
    }


}