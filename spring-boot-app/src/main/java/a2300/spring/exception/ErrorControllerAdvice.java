package a2300.spring.exception;

import a2300.spring.dto.ErrorResponseDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class ErrorControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex) {
        return create(500, "Unknown error, see logs.");
    }

    @ExceptionHandler(DirtyTrickException.class)
    public ResponseEntity<Object> handle(DirtyTrickException ex) {
        return create(500, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handle(ResourceNotFoundException ex) {
        return create(404, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        var messages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("'%s' %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return create(422, messages);
    }

    private static ResponseEntity<Object> create(int httpCode, List<String> messages) {
        var error = new ErrorResponseDto(messages);
        var response = new ResponseEntity<Object>(error, HttpStatusCode.valueOf(httpCode));
        return response;
    }

    private static ResponseEntity<Object> create(int httpCode, String... messages) {
        return create(httpCode, List.of(messages));
    }
}
