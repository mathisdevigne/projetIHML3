package jeuDesFourmis.controlleur;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.util.Pair;
import jeuDesFourmis.model.Fourmi;
import jeuDesFourmis.model.Fourmiliere;
import jeuDesFourmis.vue.Case;
import jeuDesFourmis.vue.Interface;
import jeuDesFourmis.vue.Loupe;
import jeuDesFourmis.vue.Terrain;

import javax.security.auth.callback.ConfirmationCallback;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Controlleur extends Application {

    private final int defQGraineMax = 10;

    private Fourmiliere f;

    private Interface root;

    private Terrain t;

    private double tempsEvolue=1;

    private Service<Void> playService;

    private SimpleIntegerProperty nbIteProperty;

    private SimpleIntegerProperty nbGraineProperty;

    private SimpleIntegerProperty nbFourmisProperty;

    private Pair<Case,Integer> derniereCasePointe;

    private int loupeX = 6;
    private int loupeY = 6;

    private boolean isValid(int x, int y){
        return x >= 0 && x < t.getSize() && y >= 0 && y < t.getSize();
    }
    private void changeMur(int x, int y){
        if (isValid(x, y)) {
            Case c = t.getCases(x, y);
            boolean isMur = f.getMur(x+1, y+1);
            if (c.getNbGraines() == 0) {
                c.setMur(!isMur);
                f.setMur(x+1, y+1, !isMur);
            }
        }
    }

    @Override
    public void start(Stage primaryStage){
        f = new Fourmiliere(100, 100, defQGraineMax);
        root = new Interface(defQGraineMax);
        t = root.getTerrain();
        Loupe loupe = new Loupe(t);
        derniereCasePointe = new Pair<>(null, -1);
        nbFourmisProperty = new SimpleIntegerProperty(0);
        nbIteProperty = new SimpleIntegerProperty(0);
        nbGraineProperty = new SimpleIntegerProperty(0);
        t.getMaxNbGraine().bind(root.getCapCase().getValueProperty());

        nbGraineProperty.bind(new IntegerBinding() {
            final List<IntegerProperty> listProperty;
            {
                listProperty = new ArrayList<>();
                for (int x = 0; x <100 ; x++) {
                    for (int y = 0; y < 100; y++) {
                        IntegerProperty p = t.getCases(x,y).nbGraineProperty();
                        bind(p);
                        listProperty.add(p);
                    }
                }
            }
            @Override
            protected int computeValue() {
                return listProperty.stream().map(ObservableIntegerValue::get).reduce(0,Integer::sum);
            }
        });
        Scene sceneLoupe = new Scene(loupe, 330, 330);
        Stage stageLoupe = new Stage();
        stageLoupe.setTitle("Loupe");
        stageLoupe.setScene(sceneLoupe);
        stageLoupe.setAlwaysOnTop(true);

        playService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        while (true){
                            if(this.isCancelled()){
                                break;
                            }
                            if(!t.isPaused()){
                                f.evolue();
                                Platform.runLater(() -> {
                                    int size = t.getSize();
                                    t.resetFourmis();
                                    f.getLesFourmis().stream().filter(fourmi -> fourmi.getX()>0 && fourmi.getY()>0 &&fourmi.getX() <=t.getSize()&&fourmi.getX()<=t.getSize()).forEach(fourmi -> t.ajouteFourmi(fourmi.getX()-1, fourmi.getY()-1, fourmi.porte()));
                                    nbIteProperty.set(nbIteProperty.get()+1);
                                    for (int x = 0; x <size ; x++) {
                                        for (int y = 0; y < size; y++) {
                                            if(f.getQteGraines(x+1,y+1) != t.getCases(x,y).getNbGraines()){
                                                t.getCases(x,y).setNbGraine(f.getQteGraines(x+1,y+1)); ;
                                            }
                                        }
                                    }
                                });
                            }
                            Platform.runLater(()->{
                                nbFourmisProperty.set(f.getLesFourmis().size());
                            });

                            loupe.update(loupeX,loupeY);
                            Thread.sleep((long) (1000/tempsEvolue));
                        }
                        return null;
                    }
                };
            }
        };
        t.setOnMouseClicked(mouseEvent -> {
            if(t.isPaused()) {
                int x = (int) mouseEvent.getX() / 10;
                int y = (int) mouseEvent.getY() / 10;
                if(isValid(x,y)){
                    if(mouseEvent.isShiftDown()) {
                        if (!f.getMur(x+1, y+1)) {
                            f.ajouteFourmi(x+1, y+1);
                            t.ajouteFourmi(x, y, false);
                        }
                    }
                    else{
                        if(!f.contientFourmi(x+1,y+1)){
                            changeMur(x, y);
                        }
                    }
                }

            }
        });
        t.setOnMouseMoved(mouseEvent -> {
            int x = (int) mouseEvent.getX() / 10;
            int y = (int) mouseEvent.getY() / 10;
            if(isValid(x,y)) {
                if (loupeY != y || loupeX != x) {
                    loupeY = y;
                    loupeX = x;
                }
            }
        });

        t.setOnMouseDragged(mouseEvent -> {
            if(t.isPaused()) {
                int x = (int) mouseEvent.getX() / 10;
                int y = (int) mouseEvent.getY() / 10;
                if(isValid(x,y)){
                    if(mouseEvent.isShiftDown()) {
                        if (!f.getMur(x+1, y+1)) {
                            f.ajouteFourmi(x+1, y+1);
                            t.ajouteFourmi(x, y, false);
                        }
                    }
                    else{
                        if(!t.isDerniereCase(t.getCases(x,y))&&!f.contientFourmi(x+1,y+1))
                            changeMur(x, y);
                    }
                }
            }
        });

        t.setOnScroll(scrollEvent -> {
            if(t.isPaused()) {
                int x = (int) scrollEvent.getX() / 10;
                int y = (int) scrollEvent.getY() / 10;
                if(isValid(x,y)){
                    if(!f.getMur(x+1,y+1)){
                        int newQteGraine = f.getQteGraines(x+1,y+1) + (scrollEvent.getDeltaY()>0?1:-1);
                        f.setQteGraines(x+1,y+1,newQteGraine);
                        t.getCases(x,y).setNbGraine(newQteGraine);
                    }
                }

            }
        });
        root.getQuit().setOnAction(e ->{
            Platform.exit();
            System.exit(0);
        });

        root.getNbGraine().textProperty().bind(new StringBinding() {
            {
                bind(nbGraineProperty);
            }
            @Override
            protected String computeValue() {
                return "Graines : "+ nbGraineProperty.get();
            }
        });

        root.getNbFourmis().textProperty().bind(new StringBinding() {
            {
                bind(nbFourmisProperty);
            }
            @Override
            protected String computeValue() {
                return "Fourmis : "+nbFourmisProperty.get();
            }
        });
        root.getNbIte().textProperty().bind(new StringBinding() {
            {
                bind(nbIteProperty);
            }
            @Override
            protected String computeValue() {
                return "Itération : "+nbIteProperty.get();
            }
        });

        root.getPlayPause().setOnAction(e -> root.getTerrain().pausedBindingProperty().pausePlay());
        root.getReset().setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Vous êtes sur de reset la partie ?");
            Optional<ButtonType> opt = alert.showAndWait();
            if(opt.isPresent() && opt.get() == ButtonType.OK){
                int size = t.getSize();
                t.resetFourmis();
                f.resetFourmiAndGraines();
                for (int x = 0; x <size ; x++) {
                    for (int y = 0; y < size; y++) {
                        f.setMur(x+1,y+1,false);
                        Case c = t.getCases(x,y);
                        c.setMur(false);
                        c.setNbGraine(0);
                    }
                }
                nbIteProperty.set(0);
                nbFourmisProperty.set(0);
            }
        });
        root.getResetAlea().setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Vous êtes sur de reset la partie, puis de l'initialiser aléatoirement ?");
            Optional<ButtonType> opt = alert.showAndWait();
            if(opt.isPresent() && opt.get() == ButtonType.OK) {
                int size = t.getSize();
                t.resetFourmis();
                f.resetFourmiAndGraines();
                double pourcFourmis = -1;
                while(pourcFourmis<0){
                    TextInputDialog pourcFourmisTD = new TextInputDialog("Pourcentage fourmis");
                    pourcFourmisTD.showAndWait();
                    try{
                        if(Double.parseDouble(pourcFourmisTD.getEditor().getText()) <=1)
                            pourcFourmis = Double.parseDouble(pourcFourmisTD.getEditor().getText());
                    } catch (Exception ignored){pourcFourmis = -1;};
                }
                double pourcMur = -1;
                while(pourcMur<0){
                    TextInputDialog pourcMurTD = new TextInputDialog("Pourcentage mur");
                    pourcMurTD.showAndWait();
                    try{
                        if(Double.parseDouble(pourcMurTD.getEditor().getText()) <=1)
                            pourcMur = Double.parseDouble(pourcMurTD.getEditor().getText());
                    } catch (Exception ignored){pourcMur = -1;};
                }
                double pourcGraine = -1;
                while(pourcGraine<0){
                    TextInputDialog pourcGraineTD = new TextInputDialog("Pourcentage graine");
                    pourcGraineTD.showAndWait();
                    try{
                        if(Double.parseDouble(pourcGraineTD.getEditor().getText()) <=1)
                            pourcGraine = Double.parseDouble(pourcGraineTD.getEditor().getText());
                    } catch (Exception ignored){pourcGraine = -1;};
                }
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        Case c = t.getCases(x, y);
                        boolean isMur = Math.random() < pourcMur;
                        c.setMur(isMur);
                        f.setMur(x + 1, y + 1, isMur);

                        int nbGraine = Math.random()<pourcGraine? (int) (Math.random() * t.getMaxNbGraine().get()) : 0;
                        c.setNbGraine(nbGraine);
                        f.setQteGraines(x + 1, y + 1, nbGraine);
                        if (!isMur && Math.random() < pourcFourmis) {
                            t.ajouteFourmi(x, y, false);
                            f.ajouteFourmi(x + 1, y + 1);
                        }
                    }
                }
                nbIteProperty.set(0);
            }
        });
        root.getVitesseSim().getValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                tempsEvolue = newValue.doubleValue();
            }
        });
        root.getBouttonTailleJeu().setOnAction(event ->  {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Vous êtes sur de changer la taille ?");
                Optional<ButtonType> opt = alert.showAndWait();
                if(opt.isPresent() && opt.get() == ButtonType.OK){
                    int newSize = (int)root.getTailleJeu().getValue();
                    t.setSize(newSize);
                    f.changeSize(newSize);
                }
            });
        root.getCapCase().getValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                f.setqMax(newValue.intValue());
                int size = t.getSize();
                for (int x = 0; x <size ; x++) {
                    for (int y = 0; y < size; y++) {
                        if(f.getQteGraines(x,y) > newValue.intValue()){
                            f.setQteGraines(x,y,newValue.intValue());
                        }
                    }
                }
            }
        });

        root.getLoupe().setOnAction(event -> {
            if(stageLoupe.isShowing()){
                stageLoupe.close();
            }
            else {
                stageLoupe.show();
            }
        });




        Scene scene = new Scene(root, 1500, 1000);
        primaryStage.setTitle("Fourmis");
        primaryStage.setScene(scene);
        primaryStage.show();

        playService.restart();

    }

    public static void main(String[] args){
        launch(args);
    }
}
