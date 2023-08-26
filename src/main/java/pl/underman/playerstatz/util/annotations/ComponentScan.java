package pl.underman.playerstatz.util.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ComponentScan {
    String[] value() default {};
}
