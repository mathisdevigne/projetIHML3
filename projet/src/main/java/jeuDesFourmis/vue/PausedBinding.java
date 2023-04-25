package jeuDesFourmis.vue;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;

public class PausedBinding extends BooleanBinding {
    SimpleBooleanProperty pausedProperty;

    public PausedBinding(){
        pausedProperty = new SimpleBooleanProperty(true);
        bind(pausedProperty);
    }

    public void pausePlay(){
        pausedProperty.set(!pausedProperty.get());
    }
    @Override
    protected boolean computeValue(){
        return pausedProperty.get();
    }
}
