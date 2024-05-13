package Util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public class ZoomingPane extends Pane {
    private Node content;
    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);

    private double minZoomFactor = 0.1;
    private double maxZoomFactor = 5.0;

    public ZoomingPane(Node content) {
        this.content = content;
        getChildren().add(content);
        Scale scale = new Scale(1, 1);
        content.getTransforms().add(scale);

        zoomFactor.addListener((observable, oldValue, newValue) -> {
            double newZoomFactor = newValue.doubleValue();
            if (newZoomFactor < minZoomFactor) {
                zoomFactor.set(minZoomFactor);
            } else if (newZoomFactor > maxZoomFactor) {
                zoomFactor.set(maxZoomFactor);
            } else {
                scale.setX(newZoomFactor);
                scale.setY(newZoomFactor);
                requestLayout();
            }
        });
    }

    public void zoomIn() {
        zoomFactor.set(zoomFactor.get() * 1.1);
    }

    public void zoomOut() {
        zoomFactor.set(zoomFactor.get() / 1.1);
    }

}
