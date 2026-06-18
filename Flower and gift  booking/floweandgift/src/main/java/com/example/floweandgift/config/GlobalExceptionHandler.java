package com.example.floweandgift.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // IGNORE missing static resources (favicon, .well-known etc.)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleMissingStaticResources(NoResourceFoundException ex) {
        logger.debug("Ignoring missing static resource: " + ex.getMessage());
        return null; // Let Spring MVC handle it (no 500 error)
    }

    // Handle unmapped paths like /favicon.ico or /.well-known/... with 404
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFoundException(NoHandlerFoundException ex, Model model) {
        String requestUri = ex.getRequestURL();
        logger.debug("No handler found for: " + requestUri);
        if (requestUri != null && (requestUri.contains("/favicon.ico") || requestUri.contains("/.well-known/"))) {
            // For common browser/probe requests, return empty response to avoid error page
            return null;
        }
        model.addAttribute("error", "The requested page was not found.");
        return "error/404";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(MethodArgumentNotValidException ex, Model model) {
        logger.warn("Validation error: ", ex);
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        model.addAttribute("error", "Validation failed: " + errors.toString());
        return "error/400";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        logger.warn("Runtime exception: ", ex);
        model.addAttribute("error", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        logger.warn("Access denied: ", ex);
        model.addAttribute("error", "You do not have permission to access this resource.");
        return "error/403";
    }

    // GENERAL UNCATEGORIZED ERRORS
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, Model model) {
        logger.error("An error occurred: ", ex);
        model.addAttribute("error", "An unexpected error occurred.");
        return "error/500";
    }
}
