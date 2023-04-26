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
import javafx.stage.Stage;
import jeuDesFourmis.model.Fourmi;
import jeuDesFourmis.model.Fourmiliere;
import jeuDesFourmis.vue.Case;
import jeuDesFourmis.vue.Interface;
import jeuDesFourmis.vue.Terrain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private boolean isValid(int x, int y){
        return x >= 0 && x < t.getSize() && y >= 0 && y < t.getSize();
    }
    private void changeMur(int x, int y){
        if (isValid(x, y)) {
            Case c = t.getCases(x, y);
            boolean isMur = f.getMur(x, y);
            if (c.getNbGraines() == 0) {
                c.setMur(!isMur);
                f.setMur(x, y, !isMur);
            }
        }
    }

    @Override
    public void start(Stage primaryStage){
        f = new Fourmiliere(50, 50, defQGraineMax);
        root = new Interface(defQGraineMax);
        t = root.getTerrain();
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
                                    f.getLesFourmis().forEach(fourmi -> t.ajouteFourmi(fourmi.getX(), fourmi.getY(), fourmi.porte()));
                                    nbIteProperty.set(nbIteProperty.get()+1);
                                    for (int x = 0; x <size ; x++) {
                                        for (int y = 0; y < size; y++) {
                                            if(f.getQteGraines(x,y) != t.getCases(x,y).getNbGraines()){
                                                t.getCases(x,y).setNbGraine(f.getQteGraines(x,y)); ;
                                            }
                                        }
                                    }
                                });
                            }
                            Platform.runLater(()->nbFourmisProperty.set(f.getLesFourmis().size()));
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
                if(mouseEvent.isShiftDown()) {
                    if (!f.getMur(x, y)) {
                        f.ajouteFourmi(x, y);
                        t.ajouteFourmi(x, y, false);
                    }
                }
                else{
                    if(!f.contientFourmi(x,y)){
                        changeMur(x, y);
                    }
                }
            }
        });

        t.setOnMouseDragged(mouseEvent -> {
            if(t.isPaused()) {
                int x = (int) mouseEvent.getX() / 10;
                int y = (int) mouseEvent.getY() / 10;
                if(mouseEvent.isShiftDown()) {
                    if (!f.getMur(x, y)) {
                        f.ajouteFourmi(x, y);
                        t.ajouteFourmi(x, y, false);
                    }
                }
                else{
                    if(!t.isDerniereCase(t.getCases(x,y))&&!f.contientFourmi(x,y))
                        changeMur(x, y);
                }
            }
        });

        t.setOnScroll(scrollEvent -> {
            if(t.isPaused()) {
                int x = (int) scrollEvent.getX() / 10;
                int y = (int) scrollEvent.getY() / 10;
                if(!f.getMur(x,y)){
                    int newQteGraine = f.getQteGraines(x,y) + (scrollEvent.getDeltaY()>0?1:-1);
                    f.setQteGraines(x,y,newQteGraine);
                    t.getCases(x,y).setNbGraine(newQteGraine);
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
            int size = t.getSize();
            t.resetFourmis();
            f.resetFourmiAndGraines();
            for (int x = 0; x <size ; x++) {
                for (int y = 0; y < size; y++) {
                    f.setMur(x,y,false);
                    Case c = t.getCases(x,y);
                    c.setMur(false);
                    c.setNbGraine(0);
                }
            }
            nbGraineProperty.set(0);
            nbIteProperty.set(0);
            nbFourmisProperty.set(0);
        });
        root.getVitesseSim().getValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                tempsEvolue = newValue.doubleValue();
            }
        });
        root.getTailleJeu().getValueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                t.setSize(newValue.intValue());
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

        Scene scene = new Scene(root, 1000, 600);

        primaryStage.setTitle("Fourmis");
        primaryStage.setScene(scene);
        primaryStage.show();

        playService.restart();

    }

    public static void main(String[] args){
        launch(args);
    }
}
