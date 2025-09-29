package com.automatas.AutomaTICs.model;

import java.util.List;
import java.util.Map;

public class ValidacionResponse {

  private final boolean valido;
  private final Map<String, List<String>> errores;

  public ValidacionResponse(boolean valido, Map<String, List<String>> errores) {
    this.valido = valido;
    this.errores = errores;
  }

  public boolean isValido() {
    return valido;
  }

  public Map<String, List<String>> getErrores() {
    return errores;
  }
}