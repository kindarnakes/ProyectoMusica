package org.ciclo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.ciclo.MainApp;
import org.ciclo.Utils.Utils;
import org.ciclo.model.Artist;
import org.ciclo.model.ArtistDAO;
import org.ciclo.model.Disc;
import org.ciclo.model.DiscDAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class DiscFormController implements Initializable {
    @FXML
    TextField name;
    @FXML
    TextField photo;
    @FXML
    DatePicker release;
    @FXML
    ListView<Artist> artistList;
    @FXML
    TextField filter;
    @FXML
    Label error;

    int id = 0;
    private final Controller c = new Controller();
    private DiscDAO discDAO;

    ObservableList<Artist> artistObservableList;
    FilteredList<Artist> artistFilteredList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        artistObservableList = FXCollections.observableList(ArtistDAO.List_All_Artist());
        artistFilteredList = new FilteredList<>(artistObservableList);
        artistList.setItems(artistFilteredList);

        filter.textProperty().addListener((observable, oldValue, newValue) -> {
            Predicate<Artist> artistPredicate = i -> i.getName().startsWith(filter.getText());
            if (artistFilteredList != null) {
                artistFilteredList.setPredicate(artistPredicate);
            }
        });
    }

    public void save() throws IOException {
        if (release.getValue() != null) {
            if (artistList.getSelectionModel().getSelectedItem() != null) {
                if (name.getText() != null && !name.getText().equals("")) {
                    if (id == 0) {
                        if(c.createDisc(name.getText(), photo.getText(), release.getValue(), artistList.getSelectionModel().getSelectedItem(), null)){
                            back();
                        }else{
                            Utils.popUp("Error de guardado", "No se ha podido guardar");
                        }
                    } else {
                        if (c.updateDisc(discDAO, name.getText(), photo.getText(), release.getValue(), artistList.getSelectionModel().getSelectedItem())) {
                            back();
                        } else {
                            Utils.popUp("Error de guardado", "No se ha podido guardar");
                        }
                    }
                }else{
                    error.setText("Debe escribir un nombre válido");
                }
            } else {
                Utils.popUp("Error", "Debe elegir un artista");
            }
        } else {
            Utils.popUp("Error", "Debe introducir la fecha");
        }

    }

    public void setId(int id) {
        this.id = id;
    }

    public void back() throws IOException {
        MainApp.setRoot("DiscTable");

    }

    public void showData(){
        discDAO = new DiscDAO(id);
        name.setText(discDAO.getName());
        photo.setText(discDAO.getPhoto());
        release.setValue(discDAO.getReleaseDate());
        artistList.getSelectionModel().select((Artist) discDAO.getArtist());
    }


}
