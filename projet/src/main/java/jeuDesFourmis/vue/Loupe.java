package jeuDesFourmis.vue;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import jeuDesFourmis.model.Fourmiliere;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Loupe extends Pane {
    private int sourisX;
    private int sourisY;

    private Terrain t;
    private static final int caseSize = 30;
    private static final int MAXSIZE = 11;

    private Case[][] cases;
    public Loupe(Terrain t){
        super();
        this.cases = new Case[MAXSIZE][MAXSIZE];
        for (int x = 0; x < MAXSIZE; x++) {
            for (int y = 0; y < MAXSIZE; y++) {
                Case c = new Case(x * 30, y * 30, t.getMaxNbGraine(),30);
                this.cases[x][y] = c;
                this.getChildren().add(c);
            }
        }
        this.t = t;
        this.setPrefHeight(MAXSIZE*caseSize);
    }

    public void update(int x, int y){
        try {
            List<Node> nodeStream = this.getChildren().stream().filter(node -> node.getClass() == Fourmi.class).collect(Collectors.toList());
            Platform.runLater(()->this.getChildren().removeAll(nodeStream));
            for (int i = 0; i < 11; i++) {
                for (int j = 0; j < 11; j++) {
                    int newX = x - 5 + i, newY = y - 5 + j;
                    Case c = this.cases[i][j];
                    if (newX >= 0 && newY >= 0 && newX < t.getSize() && newY < t.getSize()) {
                        c.setIsBordLoupe(false);
                        c.setMur(t.getCases(newX, newY).isMur());
                        c.setNbGraine(t.getCases(newX, newY).getNbGraines());
                        if (t.contientFourmi(newX * 10 + 5, newY * 10 + 5)) {
                            Fourmi four = new Fourmi((i * 30) + 15, (j * 30) + 15, 9);
                            four.setPorteGraine(t.getFourmi(newX, newY).get().porteGraine());
                            Platform.runLater(() -> this.getChildren().add(four));
                        }
                    } else {
                        c.setIsBordLoupe(true);
                    }
                }
            }
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    public int getSourisX() {
        return sourisX;
    }

    public int getSourisY() {
        return sourisY;
    }
}
