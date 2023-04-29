package jeuDesFourmis.vue;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Interface extends BorderPane {

    private Terrain terrain;

    private Button loupe;
    private Button quit;
    private Button playPause;

    private Label nbGraine;
    private Label nbFourmis;
    private Label nbIte;

    private final SliderBetter tailleJeu;
    private final SliderBetter capCase;
    private final SliderBetter vitesseSim;

    private final Button bouttonTailleJeu;

    private final Button reset;

    private final Button resetAlea;

    private final int MAXSIZE = 100;

    public Button getBouttonTailleJeu() {
        return bouttonTailleJeu;
    }

    public Interface(int qGraineMax) {
        super();
        this.terrain = new Terrain(MAXSIZE);
        this.setCenter(this.terrain);
        this.bouttonTailleJeu = new Button("Actualiser la taille");

        Label titre = new Label();
        titre.textProperty().bind(new StringBinding() {
            {
                super.bind(terrain.pausedBindingProperty());
            }
            @Override
            protected String computeValue() {
                return "Jeu des fourmis"+ (terrain.pausedBindingProperty().get() ? " (En pause)" : "");
            }
        });
        titre.setStyle("-fx-font-family: 'Comic Sans MS', sans-serif; -fx-font-size: 30;");
        BorderPane.setAlignment(titre, Pos.CENTER);
        this.setTop(titre);

        nbGraine = new Label("graines : 0");
        nbFourmis = new Label("fourmis : 0");
        nbIte = new Label("ite : 0");
        loupe = new Button("Loupe");
        playPause = new Button();
        playPause.textProperty().bind(new StringBinding() {
            {bind(terrain.pausedBindingProperty());}
            @Override
            protected String computeValue() {
                return terrain.pausedBindingProperty().get() ? "Play":"Pause";
            }
        });
        VBox left = new VBox(nbGraine, nbFourmis, nbIte, loupe, playPause);
        left.setAlignment(Pos.CENTER);
        left.spacingProperty().bind(this.terrain.heightProperty().divide(7));
        this.setLeft(left);


        tailleJeu = new SliderBetter("Taille du jeu", 20, 100, 50);
        capCase = new SliderBetter("Capacité des cases", 1, 100, qGraineMax);
        reset = new Button("Reset");
        resetAlea = new Button("Reset aléatoirement");
        vitesseSim = new SliderBetter("Vitesse de la simulation", 1, 10, 1);
        VBox taille = new VBox(tailleJeu, bouttonTailleJeu);
        taille.setSpacing(10);
        taille.setAlignment(Pos.CENTER);
        VBox right = new VBox(taille, capCase, reset, resetAlea, vitesseSim);
        right.setAlignment(Pos.CENTER);
        BorderPane.setMargin(right, new Insets(15));
        right.spacingProperty().bind(this.terrain.heightProperty().divide(7));
        this.setRight(right);

        left.prefWidthProperty().bind(right.widthProperty());

        tailleJeu.visibleProperty().bind(terrain.pausedBindingProperty());
        bouttonTailleJeu.visibleProperty().bind(terrain.pausedBindingProperty());
        tailleJeu.getValueProperty().addListener((obs, oldval, newVal) ->
                tailleJeu.getValueProperty().setValue(newVal.intValue()));



        capCase.visibleProperty().bind(terrain.pausedBindingProperty());
        capCase.getValueProperty().addListener((obs, oldval, newVal) ->
                capCase.getValueProperty().setValue(newVal.intValue()));
        vitesseSim.visibleProperty().bind(terrain.pausedBindingProperty().not());
        resetAlea.visibleProperty().bind(terrain.pausedBindingProperty());
        reset.visibleProperty().bind(terrain.pausedBindingProperty());

        BorderPane.setMargin(left, new Insets(15));
        BorderPane.setMargin(right, new Insets(15));
        BorderPane.setMargin(titre, new Insets(15));

        quit = new Button("Quitter");
        HBox bot = new HBox(quit);
        bot.setAlignment(Pos.BOTTOM_RIGHT);
        this.setBottom(bot);

        BorderPane.setAlignment(left, Pos.CENTER);
        BorderPane.setAlignment(right, Pos.CENTER);
        BorderPane.setAlignment(terrain, Pos.CENTER);
        this.minWidthProperty().bind(left.widthProperty().add(terrain.widthProperty()).add(right.widthProperty()).add(30));
    }

    public Button getLoupe() {
        return loupe;
    }

    public void setLoupe(Button loupe) {
        this.loupe = loupe;
    }

    public Button getQuit() {
        return quit;
    }

    public void setQuit(Button quit) {
        this.quit = quit;
    }

    public Button getPlayPause() {
        return playPause;
    }

    public Button getReset() {
        return reset;
    }

    public Button getResetAlea() {
        return resetAlea;
    }

    public void setPlayPause(Button playPause) {
        this.playPause = playPause;
    }

    public Label getNbGraine() {
        return nbGraine;
    }

    public void setNbGraine(Label nbGraine) {
        this.nbGraine = nbGraine;
    }

    public Label getNbFourmis() {
        return nbFourmis;
    }

    public void setNbFourmis(Label nbFourmis) {
        this.nbFourmis = nbFourmis;
    }

    public Label getNbIte() {
        return nbIte;
    }

    public void setNbIte(Label nbIte) {
        this.nbIte = nbIte;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public SliderBetter getTailleJeu() {
        return tailleJeu;
    }

    public SliderBetter getCapCase() {
        return capCase;
    }

    public SliderBetter getVitesseSim() {
        return vitesseSim;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
