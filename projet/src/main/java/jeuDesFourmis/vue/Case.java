package jeuDesFourmis.vue;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Case extends Rectangle {

    SimpleIntegerProperty nbGraines;

    SimpleIntegerProperty maxNbGraines;

    SimpleBooleanProperty isMur;
    public Case(double layoutx, double layouty, SimpleIntegerProperty maxNbGraine) {
        super();
        this.nbGraines = new SimpleIntegerProperty(0);
        this.isMur = new SimpleBooleanProperty(false);
        this.setLayoutX(layoutx);
        this.setLayoutY(layouty);
        this.setViewOrder(1);
        this.maxNbGraines = maxNbGraine;
        setFill(Color.WHITE);
        this.maxNbGraines.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(nbGraines.get() > newValue.intValue()){
                    nbGraines.set(newValue.intValue());
                }
            }
        });
        this.styleProperty().bind(new StringBinding() {
            {
                bind(maxNbGraines, nbGraines, isMur);
            }
            @Override
            protected String computeValue() {
                double mult = (double)nbGraines.get()/(double)maxNbGraines.get();
                return "-fx-fill:" + (isMur.get()? "black": "rgb(" + (255-(mult*50))+"," + (255-(mult* 255))+","+ (255-(mult*255))+")")+";";
            }
        });
        this.setHeight(10);
        this.setWidth(10);

    }

    public void setMaxNbGraines(SimpleIntegerProperty maxNbGraine) {
        this.maxNbGraines = maxNbGraine;
    }

    public boolean isMur() {
        return isMur.get();
    }

    public void setMur(boolean b) {
        isMur.set(b);
    }

    public void setNbGraine(int n){
        if(n >= 0 && n <= maxNbGraines.get()){
            nbGraines.set(n);
        }
    }

    public int getNbGraines() {
        return nbGraines.get();
    }
    public IntegerProperty nbGraineProperty() {
        return nbGraines;
    }

    public void changeGraine(int nbChange){
        int newNbGraine = nbGraines.get()+nbChange;
        if(newNbGraine >= 0 && newNbGraine <= maxNbGraines.get()){
            nbGraines.set(newNbGraine);
        }
    }
}
