package pl.underman.playerstatz.exceptions;

public class ConfigValidationException extends RuntimeException{

        public ConfigValidationException(String message) {
            super(message);
        }
}
