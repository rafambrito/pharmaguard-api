package com.pharmaguard.api.shared.infrastructure.web;

import com.pharmaguard.api.shared.domain.exception.BusinessException;
import com.pharmaguard.api.shared.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST,
                "Requisição inválida. Verifique os campos informados.",
                request);

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex,
                                                            HttpServletRequest request) {
        LOGGER.warn("event=constraint_violation path={} message={}", request.getRequestURI(), ex.getMessage());

        ProblemDetail problem = buildProblem(HttpStatus.BAD_REQUEST,
                "Requisição inválida. Verifique os parâmetros informados.",
                request);

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
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

        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado.",
                request);
    }

    private ProblemDetail buildProblem(HttpStatus status, String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("timestamp", OffsetDateTime.now());
        problem.setProperty("path", request.getRequestURI());
        return problem;
    }
}
