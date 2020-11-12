package org.ciclo.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.ciclo.MainApp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import org.ciclo.model.Artist;
import org.ciclo.model.ArtistDAO;

public class ArtistFormController implements Initializable {

    @FXML
    TextField name;
    @FXML
    TextField from;
    @FXML
    TextField photo;

    int id = 0;
    Artist artist;

    public void save() {

        if (id == 0) {
            Artist artist = new Artist(name.getText(), photo.getText(), from.getText());
            ArtistDAO artistDAO = new ArtistDAO(artist);
            artistDAO.Insert_Artist();
        } else {
            ArtistDAO artistDAO = (ArtistDAO) artist;
            artistDAO.setName(name.getText());
            artistDAO.setNationality(from.getText());
            artistDAO.setPhoto(photo.getText());
            artistDAO.Update_Artist();
        }

        try {
            back();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void back() throws IOException {
            MainApp.setRoot("ArtistTable");

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void showData(){
        if (id != 0) {
            artist = new ArtistDAO(id);
            name.setText(artist.getName());
            from.setText(artist.getFrom());
            photo.setText(artist.getPhoto());
        }
    }

}