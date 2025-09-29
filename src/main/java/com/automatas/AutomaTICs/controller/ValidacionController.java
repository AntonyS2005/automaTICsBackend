package com.automatas.AutomaTICs.controller;

import com.automatas.AutomaTICs.model.CadenaRequest;
import com.automatas.AutomaTICs.model.ValidacionResponse;
import com.automatas.AutomaTICs.service.ValidacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidacionController {

  private final ValidacionService validacionService;

  // Inyección por constructor (Buena Práctica)
  public ValidacionController(ValidacionService validacionService) {
    this.validacionService = validacionService;
  }

  @PostMapping("/validar")
  public ResponseEntity<ValidacionResponse> validar(
          // @Valid activa las validaciones de CadenaRequest (ej: @NotBlank)
          @Valid @RequestBody CadenaRequest request) {

    // Se obtiene la cadena del Request
    String cadena = request.getCadena();

    // Se llama al servicio para ejecutar la validación
    ValidacionResponse response = validacionService.validarCadena(cadena);

    // Se retorna la respuesta
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}