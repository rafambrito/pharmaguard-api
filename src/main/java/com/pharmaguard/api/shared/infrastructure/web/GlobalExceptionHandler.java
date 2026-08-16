package com.pharmaguard.api.shared.infrastructure.web;

import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.pharmaguard.api.shared.config.MessageKeys.MSG_ERRO_GENERICO;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_ERRO_REQUISICAO_INVALIDA_CAMPOS;
import static com.pharmaguard.api.shared.config.MessageKeys.MSG_ERRO_REQUISICAO_INVALIDA_PARAMETROS;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, HttpServletRequest request) {
        LOGGER.warn("event=business_exception path={} message={}", request.getRequestURI(), ex.getMessage());
        return buildProblem(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        LOGGER.warn("event=illegal_argument path={} message={}", request.getRequestURI(), ex.getMessage());
        return buildProblem(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler({ResourceNotFoundException.class, NoSuchElementException.class})
    public ProblemDetail handleNotFoundException(RuntimeException ex, HttpServletRequest request) {
        LOGGER.warn("event=resource_not_found path={} message={}", request.getRequestURI(), ex.getMessage());
        return buildProblem(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                               HttpServletRequest request) {
        LOGGER.warn("event=validation_error path={} message={}", request.getRequestURI(), ex.getMessage());

        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST, MSG_ERRO_REQUISICAO_INVALIDA_CAMPOS, request);

        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
            .map(error -> {
                String code = error.getDefaultMessage();
                String message = resolveMessage(code);
                return Map.of(
                    "field", error.getField(),
                    "code", code,
                    "message", message);
            })
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex,
                                                            HttpServletRequest request) {
        LOGGER.warn("event=constraint_violation path={} message={}", request.getRequestURI(), ex.getMessage());

        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST, MSG_ERRO_REQUISICAO_INVALIDA_PARAMETROS, request);

        List<Map<String, String>> errors = ex.getConstraintViolations()
                .stream()
            .map(violation -> {
                String code = violation.getMessage();
                String message = resolveMessage(code);
                return Map.of(
                    "field", violation.getPropertyPath().toString(),
                    "code", code,
                    "message", message);
            })
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex, HttpServletRequest request) {
        LOGGER.error("event=unexpected_exception path={} type={} message={}",
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                ex.getMessage());

        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, MSG_ERRO_GENERICO, request);
    }

    private ProblemDetail buildProblem(HttpStatus status, String code, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, resolveMessage(code));
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("code", code);
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }

    private String resolveMessage(String code) {
        if (code == null || code.isBlank()) {
            return code;
        }
        return messageSource.getMessage(code, null, code, Locale.getDefault());
    }
}
