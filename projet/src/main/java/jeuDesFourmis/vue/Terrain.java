package jeuDesFourmis.vue;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Terrain extends Pane {

    private PausedBinding pausedBinding;
    private Case derniereCase = null;
    private final int minSize = 20;
    private final int maxSize = 100;
    private final int defSize = 500;
    private final int tailleCase = 10;

    private int tailleFourmi;

    private final SimpleIntegerProperty sizeProperty;

    private List<Fourmi> fourmis;

    private Case[][] cases;
    private List<Line> linesx;
    private List<Line> linesy;

    private DoubleBinding lineSizeBinding;

    private StringBinding lineStyleBinding;

    private SimpleIntegerProperty maxNbGraine;

    public Terrain() {
        super();
        this.sizeProperty = new SimpleIntegerProperty(0);
        this.pausedBinding = new PausedBinding();
        this.maxNbGraine = new SimpleIntegerProperty(10);
        this.fourmis = new ArrayList<>();
        this.cases = new Case[maxSize][maxSize];
        for (int x = 0; x < maxSize; x++) {
            for (int y = 0; y < maxSize; y++) {
                Case c = new Case(x * 10, y * 10, maxNbGraine);
                this.cases[x][y] = c;
            }
        }
        this.linesx = new ArrayList<>();
        this.linesy = new ArrayList<>();
        lineStyleBinding = new StringBinding() {
            {
                super.bind(pausedBinding);
            }

            @Override
            protected String computeValue() {
                return "visibility: " + (pausedBinding.get() ? "visible;" : "hidden;");
            }
        };
        lineSizeBinding = new DoubleBinding() {
            {
                super.bind(sizeProperty);
            }
            @Override
            protected double computeValue() {
                return tailleCase * sizeProperty.get();
            }
        };
        for (int i = 0; i <= maxSize; i++) {
            Line ly = new Line();
            ly.setMouseTransparent(true);
            ly.setStroke(Color.BLACK);
            ly.setStartX(0);
            ly.setStartY(i * 10);
            ly.endXProperty().bind(lineSizeBinding);
            ly.setEndY(i * 10);

            Line lx = new Line();
            lx.setMouseTransparent(true);
            lx.setStroke(Color.BLACK);
            lx.setStartX(i * 10);
            lx.setStartY(0);
            lx.setEndX(i * 10);
            lx.endYProperty().bind(lineSizeBinding);

            if (i > 0 && i < maxSize) {
                ly.styleProperty().bind(lineStyleBinding);
                lx.styleProperty().bind(lineStyleBinding);
            }

            linesy.add(ly);
            linesx.add(lx);
        }
        this.getChildren().addAll(linesy.get(0),linesx.get(0));

        this.setSize(defSize/10);

        this.minWidthProperty().bind(sizeProperty.multiply(tailleCase));
        this.maxWidthProperty().bind(sizeProperty.multiply(tailleCase));
        this.prefWidthProperty().bind(sizeProperty.multiply(tailleCase));
    }

    public void ajouteFourmi(int x,int y, boolean porte){
        Fourmi f = new Fourmi(x*10+5, y*10+5);
        f.setPorteGraine(porte);
        this.getChildren().add(f);
        fourmis.add(f);
    }


    public PausedBinding pausedBindingProperty() {
        return pausedBinding;
    }

    public void setDerniereCase(Case derniereCase) {
        this.derniereCase = derniereCase;
    }

    public boolean isDerniereCase(Case c) {
        if (!c.equals(derniereCase)) {
            this.setDerniereCase(c);
            return false;
        }
        return true;
    }

    public boolean isPaused(){
        return pausedBinding.get();
    }

    public int getSize() {
        return sizeProperty.get();
    }

    public Case getCases(int x, int y) {
        return cases[x][y];
    }

    public boolean contientFourmi(int x, int y){
        return fourmis.stream().anyMatch(fourmi -> fourmi.getY() == x && fourmi.getY() == y);
    }

    public void enleveFourmi(int x, int y){
        List<Fourmi> frm = fourmis.stream().filter(fourmi -> fourmi.getY() == x && fourmi.getY() == y).collect(Collectors.toList());
        fourmis.removeAll(frm);
        getChildren().removeAll(frm);
    }

    public void resetFourmis(){
        this.
        getChildren().removeAll(fourmis);
        fourmis = new ArrayList<>();
    }
    public void setSize(int newSize){
        int size = sizeProperty.get();
        if(newSize != size && newSize <= maxSize && newSize>=minSize) {
            if (newSize > size) {
                for (int x = 0; x < newSize; x++) {
                    for (int y = 0; y < newSize; y++) {
                        if(x >= size ||y >= size) {
                            this.getChildren().add(this.cases[x][y]);
                        }
                    }
                }
            } else {
                for (int x = 0; x < size; x++) {
                    for (int y = 0; y < size; y++) {
                        if(x >= newSize ||y >= newSize) {
                            this.cases[x][y].setMur(false);
                            this.getChildren().remove(this.cases[x][y]);
                        }
                    }
                }
            }
            updateLines(newSize);
            sizeProperty.set(newSize);
            List<Fourmi> fourmisHorsRange = fourmis.stream().filter(fourmi -> fourmi.getX()>newSize*10 || fourmi.getY() > newSize*10).collect(Collectors.toList());
            getChildren().removeAll(fourmisHorsRange);
            fourmis.removeAll(fourmisHorsRange);
        }
    }

    public SimpleIntegerProperty getMaxNbGraine() {
        return maxNbGraine;
    }

    public SimpleIntegerProperty maxNbGraineProperty() {
        return maxNbGraine;
    }

    private void updateLines(int newSize) {
        int size = sizeProperty.get();
        if(newSize != size && newSize <= maxSize && newSize>=minSize) {

            if (newSize > size) {
                if (size != 0) {
                    linesx.get(size).styleProperty().bind(lineStyleBinding);
                    linesy.get(size).styleProperty().bind(lineStyleBinding);
                }
                else{

                }
                for (int i = size+1; i <= newSize; i++) {

                    Line ly = linesy.get(i);
                    Line lx = linesx.get(i);
                    if (i == 0 || i == newSize) {
                        ly.styleProperty().unbind();
                        lx.styleProperty().unbind();
                    }
                    this.getChildren().addAll(ly, lx);
                }
            } else {
                for (int i = newSize; i <= size; i++) {
                    if(i == newSize){
                        linesx.get(newSize).styleProperty().bind(lineStyleBinding);
                        linesy.get(newSize).styleProperty().bind(lineStyleBinding);
                    }
                    else{
                        this.getChildren().removeAll(linesy.get(i), linesx.get(i));
                    }
                }
            }
        }
    }

    public Rectangle[][] getRectangles() {
        return cases;
    }

    public void setRectangles(Case[][] cases) {
        this.cases = cases;
    }

}
