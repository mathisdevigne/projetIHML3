package jeuDesFourmis.vue;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

public class SliderBetter  extends HBox {

    Label l;
    TextField tf;
    Slider s;

    public SliderBetter(String st, double min, double max, double value) {
        super();
        this.l = new Label(st);
        this.tf = new TextField();
        this.tf.setMaxWidth(40);
        this.s = new Slider(min, max, value);
        VBox stv = new VBox(l, s);
        stv.setAlignment(Pos.CENTER);
        Bindings.bindBidirectional(tf.textProperty(), s.valueProperty(), new NumberStringConverter());

        this.getChildren().addAll(stv,tf);
    }

    public double getValue(){
        return s.getValue();
    }
    public double getMax(){
        return s.getMax();
    }
    public double getMin(){
        return s.getMin();
    }

    public DoubleProperty getValueProperty(){
        return s.valueProperty();
    }
    public DoubleProperty getMaxProperty(){
        return s.maxProperty();
    }
}
