package com.automatas.AutomaTICs.validator;

import org.springframework.stereotype.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CadenaValidator {

  private static final String REGEX_NO_PERMITIDOS = "[^a-zA-Z0-9\\s,;:.áéíóúÁÉÍÓÚ]";

  // Restricción extra: Máximo 2 repeticiones seguidas para cc, ll, rr, oo, ee.
  // Ejemplo: no permitir ccc, lll, rrr, ooo, eee.
  private static final String REGEX_MAX_REPETICION = "(c{3,}|l{3,}|r{3,}|o{3,}|e{3,})";

  /**
   * Regla A: Aceptar solo cadenas numéricas de una o más cifras.
   */
  public boolean esSoloNumeros(String input) {
    if (input == null || input.isEmpty()) return false;
    return input.matches("\\d+");
  }

  /**
   * Regla B: Aceptar cadenas de texto de cualquier longitud. (Siempre true si no es nula/vacía)
   */
  public boolean esTexto(String input) {
    return input != null && !input.isEmpty();
  }

  /**
   * Regla C: Si es texto, debe iniciar con mayúscula.
   * Solo se evalúa si es una cadena de texto (no solo números).
   */
  public boolean iniciaConMayuscula(String input) {
    if (esSoloNumeros(input)) return true; // No aplica si es solo números

    if (input == null || input.isEmpty()) return false;
    char primerCaracter = input.charAt(0);

    // Verifica si el primer caracter es una letra y si es mayúscula
    return Character.isLetter(primerCaracter) && Character.isUpperCase(primerCaracter);
  }

  /**
   * Regla D: Se permiten consonantes dobles cc, ll, rr.
   * (Esta regla es de permiso, no de restricción, por lo que siempre es true
   * a menos que se viole la "Restricción Extra").
   */
  public boolean permiteConsonantesDobles(String input) {
    // La restricción extra cubre las repeticiones excesivas.
    return true;
  }

  /**
   * Regla E: No permitir vocales dobles seguidas, excepto oo y ee.
   * Restricciones: No 'aa', 'ii', 'uu'.
   * La restricción extra cubre las repeticiones excesivas de 'oo' y 'ee'.
   */
  public boolean noVocalesDoblesNoPermitidas(String input) {
    if (input == null || input.isEmpty()) return true;

    // Patrón para 'aa', 'ii', 'uu' (minúsculas y mayúsculas)
    return !input.matches(".*[aA]{2}.*") &&
            !input.matches(".*[iI]{2}.*") &&
            !input.matches(".*[uU]{2}.*");
  }

  /**
   * Regla F: No permitir combinaciones de números y texto en la misma cadena.
   */
  public boolean noMezclaTextoNumeros(String input) {
    if (input == null || input.isEmpty()) return true;

    boolean tieneLetras = input.matches(".*[a-zA-ZáéíóúÁÉÍÓÚ].*");
    boolean tieneNumeros = input.matches(".*\\d.*");

    // Falla si tiene letras Y números.
    return !(tieneLetras && tieneNumeros);
  }

  /**
   * Regla G (ajustada): Uniformidad de mayúsculas/minúsculas.
   * - Si el segundo carácter es mayúscula, toda la cadena debe estar en mayúsculas.
   * - Si el segundo carácter es minúscula, toda la cadena debe estar en minúsculas.
   * - No se aceptan combinaciones alternadas. (El primer caracter es la única excepción obligatoria a mayúscula).
   * * Se ignora el primer carácter si es letra, ya que la Regla C lo exige mayúscula.
   * Esta regla solo aplica a cadenas de 2 o más caracteres.
   */
  public boolean uniformidadMayusculasMinusculas(String input) {
    if (input == null || input.length() < 2) return true;

    // Buscamos el primer carácter que sea letra y no sea el primero de la cadena.
    char segundoCaracterLetra = '\u0000';
    int startIndex = Character.isLetter(input.charAt(0)) ? 1 : 0;

    for (int i = startIndex; i < input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isLetter(c)) {
        segundoCaracterLetra = c;
        break;
      }
    }

    // Si no hay más letras después del primer caracter (o no hay letras)
    if (segundoCaracterLetra == '\u0000') {
      return true;
    }

    boolean esSegundoMayuscula = Character.isUpperCase(segundoCaracterLetra);

    // Iteramos desde el primer carácter después del potencialmente mayúscula inicial
    // y después del segundo carácter letra identificado.
    for (int i = startIndex; i < input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isLetter(c)) {
        if (esSegundoMayuscula) {
          // Si el segundo es mayúscula, todos los subsiguientes deben ser mayúsculas.
          if (Character.isLowerCase(c)) {
            return false;
          }
        } else {
          // Si el segundo es minúscula, todos los subsiguientes deben ser minúsculas.
          if (Character.isUpperCase(c)) {
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * Regla H: La cadena debe terminar con un punto (.).
   */
  public boolean terminaConPunto(String input) {
    if (input == null || input.isEmpty()) return false;
    return input.endsWith(".");
  }

  /**
   * Regla I: No aceptar caracteres especiales distintos de letras, números, espacio, , ; : ..
   */
  public boolean noCaracteresEspecialesNoPermitidos(String input) {
    if (input == null || input.isEmpty()) return true;

    // Se utiliza el REGEX_NO_PERMITIDOS definido al inicio.
    Pattern pattern = Pattern.compile(REGEX_NO_PERMITIDOS);
    Matcher matcher = pattern.matcher(input);

    return !matcher.find(); // Si encuentra un carácter no permitido, falla (retorna false)
  }

  /**
   * Regla J: Los signos de puntuación permitidos , ; : son válidos.
   * (Esta es una regla de permiso, siempre true a menos que se viole la "Restricción Extra"
   * o la "Regla I").
   */
  public boolean signosPuntuacionValidos(String input) {
    // La validación de caracteres no permitidos se hace en la Regla I.
    return true;
  }

  /**
   * Restricción extra: Solo se permite repetición máxima de dos veces por carácter especial permitido (cc, ll, rr, oo, ee).
   */
  public boolean noExcesoRepeticionCaracteres(String input) {
    if (input == null || input.isEmpty()) return true;

    // Se utiliza el REGEX_MAX_REPETICION definido al inicio.
    Pattern pattern = Pattern.compile(REGEX_MAX_REPETICION, Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(input);

    return !matcher.find(); // Si encuentra 3 o más repeticiones seguidas, falla
  }
}