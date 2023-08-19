package pl.underman.playerstatz.util.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(ComponentScans.class)
public @interface ComponentScan {
    String[] value() default {};
}
