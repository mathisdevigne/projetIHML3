module projet.projet {
    requires javafx.controls;
    requires javafx.fxml;


    opens jeuDesFourmis.model;
    opens jeuDesFourmis.vue;
    opens jeuDesFourmis.controlleur;
}