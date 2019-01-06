package musta.belmo.fx.mustagui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Pane {
    String name() default "";
    Class<? extends javafx.scene.layout.Pane> layout() default javafx.scene.layout.Pane.class;
}
