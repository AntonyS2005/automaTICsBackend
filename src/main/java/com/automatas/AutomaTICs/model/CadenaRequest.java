package com.automatas.AutomaTICs.model;

import jakarta.validation.constraints.NotBlank;

public class CadenaRequest {

  @NotBlank(message = "La cadena no puede estar vacía.")
  private String cadena;

  // Getters y Setters
  public String getCadena() {
    return cadena;
  }

  public void setCadena(String cadena) {
    this.cadena = cadena;
  }
}