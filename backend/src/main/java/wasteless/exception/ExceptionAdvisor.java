package wasteless.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionAdvisor extends ResponseEntityExceptionHandler {

  /**
   * Constructs the error response body as a Map object.
   *
   * @param message Message thrown with the exception, caught by the Exception handlers.
   * @param status Http Status code set or received by the Exception handlers.
   * @param path URI path of the request received by the Exception handlers.
   * @return Instance of Map with following properties: "timestamp": an instance of
   *     LocalDateTime.now() "status": The Http status code taken from status argument. "error": The
   *     type of error that corresponds to Http status code taken from status argument. "message":
   *     The message caught with the exception. (e.g. "E-mail is already taken") "path": The URI
   *     path of the request attempted. (e.g. "/users")
   */
  private Map<String, Object> errorBody(String message, HttpStatus status, String path) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);
    body.put("path", path);
    return body;
  }

  /**
   * Overrides default handleMethodArgumentNotValid method from spring. Obtains the message set in
   * the model and sets the message property of the response body to it.
   *
   * @param e Instance of MethodArgumentNotValidException that the method handles.
   * @param headers Http headers of the handled request.
   * @param status Http status code of the handled response.
   * @param request Instance of WebRequest, the request handled by the method.
   * @return ResponseEntity with response body and 400 status code.
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    String path = ((ServletWebRequest) request).getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles the custom exception BadRequestException thrown by the controllers.
   *
   * @param e Instance of BadRequestException that the method handles.
   * @param request Instance of ServletWebRequest that the method handles.
   * @return ResponseEntity with response body and 400 status code.
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Object> handleBadRequest(BadRequestException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles the custom exception ConflictException thrown by the controllers.
   *
   * @param e Instance of ConflictException that the method handles.
   * @param request Instance of ServletWebRequest that the method handles.
   * @return ResponseEntity with response body and a 409 status code.
   */
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<Object> handleConflict(ConflictException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.CONFLICT;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles the exception NoSuchElementException, thrown by the "orElseThrow()" method of objects
   * retrieved from repositories.
   *
   * @param e Instance of NoSuchElementException that the method handles.
   * @param request Instance of ServletWebRequest that the method handles.
   * @return ResponseEntity with the response body and 406 status code.
   */
  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<Object> handleNotFoundInDatabase(
      NoSuchElementException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles custom exception ForbiddenException thrown by the controllers.
   *
   * @param e Instance of ForbiddenException that the method handles.
   * @param request Instance of ServletRequest that the method handles.
   * @return ResponseEntity with the response body and 403 status code.
   */
  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<Object> handleForbidden(ForbiddenException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.FORBIDDEN;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles custom exception UnauthorizedException thrown by the controllers.
   *
   * @param e Instance of UnauthorizedException that the method handles.
   * @param request Instance of ServletWebRequest that the method handles.
   * @return ResponseEntity with response body and 401 status code.
   */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<Object> handleUnauthorized(
      UnauthorizedException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.UNAUTHORIZED;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles custom exception NotAcceptableStatusException
   *
   * @param e Instance of NotAcceptable exception that the method handles.
   * @param request Instance of ServletRequest that the method handles.
   * @return ResponseEntity with the response body and 406 status code.
   */
  @ExceptionHandler(NotAcceptableStatusException.class)
  public ResponseEntity<Object> handleNotAcceptable(
      NotAcceptableStatusException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.NOT_ACCEPTABLE;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles custom exception ConstraintViolationException
   *
   * @param e Instance of ConstraintViolation exception that the method handles.
   * @param request Instance of ServletRequest that the method handles.
   * @return ResponseEntity with the response body and 400 status code.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(
          ConstraintViolationException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles custom exception IllegalArgumentException
   *
   * @param e Instance of IllegalArgumentException exception that the method handles.
   * @param request Instance of ServletRequest that the method handles.
   * @return ResponseEntity with the response body and 400 status code.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(
          IllegalArgumentException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }

  /**
   * Handles custom exception NullPointerException
   *
   * @param e Instance of NullPointerException exception that the method handles.
   * @param request Instance of ServletRequest that the method handles.
   * @return ResponseEntity with the response body and 400 status code.
   */
  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<Object> handleNullPointer(
          NullPointerException e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }




  /**
   * Handles custom exception InternalServerError
   *
   * @param e Instance of InternalServerError exception that the method handles.
   * @param request Instance of ServletRequest that the method handles.
   * @return ResponseEntity with the response body and 500 status code.
   */
  @ExceptionHandler(InternalServerError.class)
  public ResponseEntity<Object> handleInternalError(
          InternalServerError e, ServletWebRequest request) {
    String message = e.getMessage();
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String path = request.getRequest().getRequestURI();
    Map<String, Object> body = errorBody(message, status, path);
    return new ResponseEntity<>(body, status);
  }
}
