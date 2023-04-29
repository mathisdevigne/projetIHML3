package jeuDesFourmis.vue;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Fourmi extends Circle {
    private int x;
    private int y;

    private boolean porteGraine;

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Fourmi{" +
                "x=" + x +
                ", y=" + y +
                ", porteGraine=" + porteGraine +
                '}';
    }

    public int getY() {
        return y;
    }

    public Fourmi(int x, int y, int radius){
        super();
        this.x = x;
        this.y = y;
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setRadius(radius);
        this.setPorteGraine(false);
    }

    public boolean porteGraine(){
        return porteGraine;
    }

    public void setPorteGraine(boolean pg){
        if(pg){
            this.setFill(Color.GREEN);
        }
        else{
            this.setFill(Color.BLUE);
        }
        this.porteGraine = pg;
    }
}
