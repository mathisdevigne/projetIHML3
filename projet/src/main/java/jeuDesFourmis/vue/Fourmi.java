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

    public int getY() {
        return y;
    }

    public Fourmi(int x, int y){
        super();
        this.x = x;
        this.y = y;
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setRadius(3);
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
