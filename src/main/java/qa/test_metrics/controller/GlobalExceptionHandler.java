package qa.test_metrics.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import qa.test_metrics.model.PublishReportResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<PublishReportResponse> requestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        StringBuilder message = new StringBuilder();
        message.append(ex.getMethod());
        message.append(" method is not supported for this request. Supported methods are: ");
        ex.getSupportedHttpMethods().forEach(method -> message.append(method + " "));

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new PublishReportResponse(message.toString(), "failed"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<PublishReportResponse> resourceNotFoundException(NoResourceFoundException ex) {
        StringBuilder message = new StringBuilder();
        message.append("Resource not found: ");
        message.append(ex.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new PublishReportResponse(message.toString(), "failed"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<PublishReportResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PublishReportResponse(errors.toString(), "failed"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseEntity<PublishReportResponse> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        StringBuilder message = new StringBuilder();
        message.append(ex.getContentType());
        message.append(" media type is not supported. Supported media types are: ");
        ex.getSupportedMediaTypes().forEach(type -> message.append(type + " "));

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new PublishReportResponse(message.toString(), "failed"));
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<PublishReportResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String missingParam = ex.getParameterName();
        String message = "Required request parameter is missing: " + missingParam;

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new PublishReportResponse(message, "failed"));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<PublishReportResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        List<String> errors = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PublishReportResponse(errors.toString(), "failed"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<PublishReportResponse> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PublishReportResponse("Data type mismatch is request", "failed"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<PublishReportResponse> constraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PublishReportResponse(errors.toString(), "failed"));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<PublishReportResponse> missingServletRequestPartException(MissingServletRequestPartException ex) {
        String errors = ex.getRequestPartName()
                + " is missing in the request";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new PublishReportResponse(errors, "failed"));
    }
}
