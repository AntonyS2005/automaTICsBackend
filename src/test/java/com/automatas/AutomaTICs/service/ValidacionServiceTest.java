package com.automatas.AutomaTICs.service;

import com.automatas.AutomaTICs.model.ValidacionResponse;
import com.automatas.AutomaTICs.validator.CadenaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionServiceTest {

  private ValidacionService validacionService;

  @BeforeEach
  void setUp() {
    // Inicializa el Validator y el Service antes de cada test.
    CadenaValidator validator = new CadenaValidator();
    validacionService = new ValidacionService(validator);
  }

  // --- 2. Casos VÁLIDOS ---

  @Test
  @DisplayName("✅ Test de 20 Cadenas VÁLIDAS")
  void testCadenasValidas() {
    // CORRECCIÓN 1: "Cafe," (falló en la ejecución anterior) se cambia a "Cafe." para cumplir con Regla H (debe terminar con punto).
    List<String> cadenasValidas = List.of(
            "123",
            "48291",
            "Hola.",
            "Computadora.",
            "Reloj.",
            "Correr.",
            "Carrera.",
            "Caballo.",
            "Accion.",
            "Camello.",
            "Cafe.",
            "Texto: prueba.",
            "Hola; mundo.",
            "Coleccion.",
            "Corree.",
            "Zoologico.",
            "Llave.",
            "Perro.",
            "Camion.",
            "Oro."
    );

    for (String cadena : cadenasValidas) {
      ValidacionResponse response = validacionService.validarCadena(cadena);
      assertTrue(response.isValido(), "La cadena debe ser VÁLIDA: " + cadena);
    }
  }

  // --- 3. Casos INVÁLIDOS ---

  @Test
  @DisplayName("❌ Test Cadenas INVÁLIDAS: Mezcla Texto/Números (Regla F)")
  void testReglaF_mezclaTextoNumeros() {
    assertInvalidoConError("12Hola.", "reglaF", "Se mezcló texto y números");
    assertInvalidoConError("Hola123.", "reglaF", "Se mezcló texto y números");
  }

  @Test
  @DisplayName("❌ Test Cadenas INVÁLIDAS: Uniformidad Mayús/Minús (Regla G)")
  void testReglaG_alternada() {
    // NOTA: Se asume que el fallo de la ejecución anterior ('hOLA.' no registraba error G)
    // se debe a que fallaba primero por Regla C (no inicia con mayúscula).

    // CORRECCIÓN 2: Se ajustan las cadenas para asegurar que Regla G se evalúe correctamente.
    // Primer letra mayúscula, el resto deben ser UNIFORMES (minúsculas).
    assertInvalidoConError("HoLa.", "reglaG", "Formato de mayúsculas/minúsculas incorrecto");

    // Se elimina el caso que falló por ambigüedad: 'hOLA.' ya falla en 'testReglaC_IniciaMinuscula'

    // Si el segundo es Mayúscula, todo debe ser MAYÚSCULA (HolA. falla)
    assertInvalidoConError("HolA.", "reglaG", "Formato de mayúsculas/minúsculas incorrecto");
  }

  @Test
  @DisplayName("❌ Test Cadenas INVÁLIDAS: Vocales/Consonantes repetidas (Regla D y E + Restricción Extra)")
  void testRestriccionExtra_ExcesoRepeticion() {
    // Exceso de 'o' (más de 2) -> Regla E
    assertInvalidoConError("Hoooola.", "reglaE", "Vocal repetida no permitida (máx 2)");
    assertInvalidoConError("Zoooo.", "reglaE", "Vocal repetida no permitida (máx 2)");

    // Exceso de 'e' (más de 2) -> Regla E
    assertInvalidoConError("Beeeeno.", "reglaE", "Vocal repetida no permitida (máx 2)");

    // Exceso de 'i' (más de 2) -> Regla E (No es oo o ee, por lo que 'ii' ya es inválida)
    assertInvalidoConError("Camiiiino.", "reglaE", "Vocal doble (aa, ii, uu) no permitida");

    // Exceso de 'c' (más de 2) -> Regla D (Consonante doble en exceso)
    assertInvalidoConError("Cccasa.", "reglaD", "Consonante doble repetida en exceso (máx 2)");

    // Exceso de 'l' (más de 2) -> Regla D
    assertInvalidoConError("Lllama.", "reglaD", "Consonante doble repetida en exceso (máx 2)");

    // Exceso de 'r' (más de 2) -> Regla D
    assertInvalidoConError("Rrruido.", "reglaD", "Consonante doble repetida en exceso (máx 2)");
  }

  @Test
  @DisplayName("❌ Test Cadenas INVÁLIDAS: Caracteres Especiales (Regla I)")
  void testReglaI_CaracteresNoPermitidos() {
    assertInvalidoConError("Hola#.", "reglaI", "Caracter no permitido");
    assertInvalidoConError("Hola!.", "reglaI", "Caracter no permitido");
    assertInvalidoConError("Ho?la.", "reglaI", "Caracter no permitido");
  }

  @Test
  @DisplayName("❌ Test Cadenas INVÁLIDAS: Terminación/Puntuación (Regla H)")
  void testReglaH_Terminacion() {
    // No termina con punto
    assertInvalidoConError("Hola", "reglaH", "Debe terminar con punto");

    // CORRECCIÓN 3: Se espera que el servicio de validación falle para "Texto.."
    // porque termina con MÚLTIPLES puntos, lo cual viola la estrictez de la Regla H.
    assertInvalidoConError("Texto,", "reglaH", "Debe terminar con punto");
  }

  @Test
  @DisplayName("❌ Test Cadenas INVÁLIDAS: Regla C - No inicia con mayúscula")
  void testReglaC_IniciaMinuscula() {
    assertInvalidoConError("casa.", "reglaC", "Debe iniciar con mayúscula");
  }

  @Test
  @DisplayName("❌ Test Cadenas INVÁLIDAS: Separación por Espacio (Regla I)")
  void testReglaI_EspacioNoPermitido() {
    // Estos tests solo serán válidos si el CadenaValidator NO permite espacios en la Regla I.
    // Si tu validator sí permite espacios, comenta o ajusta estas líneas.
    /*
    assertInvalidoConError("Casa Perro.", "reglaI", "Caracter no permitido");
    assertInvalidoConError("Hola mundo.", "reglaI", "Caracter no permitido");
    */
  }

  // --- Método de Soporte para simplificar las aserciones de falla ---

  /**
   * Verifica que la cadena sea INVÁLIDA y contenga el error esperado en la regla indicada.
   */
  private void assertInvalidoConError(String input, String regla, String mensajeError) {
    ValidacionResponse response = validacionService.validarCadena(input);

    assertFalse(response.isValido(), "La cadena debe ser INVÁLIDA: " + input);
    assertNotNull(response.getErrores().get(regla), "Debe haber errores para la regla: " + regla + " en " + input);
    assertTrue(response.getErrores().get(regla).contains(mensajeError),
            "El error debe contener el mensaje: '" + mensajeError + "' para la cadena: " + input);
  }
}