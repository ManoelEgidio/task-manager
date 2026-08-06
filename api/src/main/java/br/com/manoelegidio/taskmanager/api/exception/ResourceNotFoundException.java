package br.com.manoelegidio.taskmanager.api.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s com ID %d não foi encontrado(a).", resourceName, id));
    }
}
