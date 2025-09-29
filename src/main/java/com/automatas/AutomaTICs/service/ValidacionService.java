package com.automatas.AutomaTICs.service;

import com.automatas.AutomaTICs.model.ValidacionResponse;
import com.automatas.AutomaTICs.validator.CadenaValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ValidacionService {

  private final CadenaValidator validator;
  private static final List<String> REGLAS = List.of(
          "reglaA", "reglaB", "reglaC", "reglaD", "reglaE",
          "reglaF", "reglaG", "reglaH", "reglaI", "reglaJ"
  );

  @Autowired
  public ValidacionService(CadenaValidator validator) {
    this.validator = validator;
  }

  public ValidacionResponse validarCadena(String input) {
    Map<String, List<String>> errores = new LinkedHashMap<>();
    for (String regla : REGLAS) {
      errores.put(regla, null);
    }

    boolean esSoloNumerico = validator.esSoloNumeros(input);

    if (!validator.noMezclaTextoNumeros(input)) {
      agregarError(errores, "reglaF", "Se mezcló texto y números");
    }

    if (!esSoloNumerico) {
      if (!validator.iniciaConMayuscula(input)) {
        agregarError(errores, "reglaC", "Debe iniciar con mayúscula");
      }
      if (!validator.uniformidadMayusculasMinusculas(input)) {
        agregarError(errores, "reglaG", "Formato de mayúsculas/minúsculas incorrecto");
      }
      if (!validator.terminaConPunto(input)) {
        agregarError(errores, "reglaH", "Debe terminar con punto");
      }
    } else {
      if (validator.terminaConPunto(input)) {
        agregarError(errores, "reglaH", "Cadena numérica no debe terminar con punto");
      }
    }

    if (!validator.noExcesoRepeticionCaracteres(input)) {
      if (input.matches(".*[oO]{3,}.*") || input.matches(".*[eE]{3,}.*")) {
        agregarError(errores, "reglaE", "Vocal repetida no permitida (máx 2)");
      } else if (input.matches(".*[cC]{3,}.*") || input.matches(".*[lL]{3,}.*") || input.matches(".*[rR]{3,}.*")) {
        agregarError(errores, "reglaD", "Consonante doble repetida en exceso (máx 2)");
      }
    }

    if (!validator.noVocalesDoblesNoPermitidas(input)) {
      agregarError(errores, "reglaE", "Vocal doble (aa, ii, uu) no permitida");
    }

    if (!validator.noCaracteresEspecialesNoPermitidos(input)) {
      agregarError(errores, "reglaI", "Caracter no permitido");
    }

    boolean hayErrores = errores.values().stream().anyMatch(list -> list != null && !list.isEmpty());
    return new ValidacionResponse(!hayErrores, errores);
  }

  private void agregarError(Map<String, List<String>> errores, String regla, String mensaje) {
    errores.computeIfAbsent(regla, k -> new ArrayList<>()).add(mensaje);
  }
}
