package com.grimoire.common.util;

/**
 * Resultado de validação para regras do T20 JdA
 */
public record ValidationResult(boolean isValid, String message) {}
