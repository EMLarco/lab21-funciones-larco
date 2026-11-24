package ec.edu.espe.pa.funcional.reglas;

import ec.edu.espe.pa.funcional.modelo.Incidencia;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Funciones del caso práctico. Cada constante declara su interfaz funcional
 * según lo que recibe y lo que devuelve.
 */
public final class ReglasIncidencia {

    private ReglasIncidencia() { }

    /** Única fuente del umbral de prioridad alta: reglas y pruebas usan este valor. */
    public static final int UMBRAL_PRIORIDAD_ALTA = 4;

    // 1. Decide (Incidencia -> boolean): Predicate.
    //    Recibe una incidencia y responde sí/no; por eso se usa test(T) -> boolean.
    public static final Predicate<Incidencia> ESTA_ABIERTA =
            incidencia -> "ABIERTA".equals(incidencia.estado());

    // 2. Decide (Incidencia -> boolean): Predicate.
    //    Compara la prioridad con el umbral y devuelve un boolean.
    public static final Predicate<Incidencia> PRIORIDAD_ALTA =
            incidencia -> incidencia.prioridad() >= UMBRAL_PRIORIDAD_ALTA;

    // 3. Combina las dos reglas anteriores: sigue siendo Incidencia -> boolean,
    //    así que es un Predicate construido con and() en lugar de un if nuevo.
    public static final Predicate<Incidencia> REQUIERE_ATENCION =
            ESTA_ABIERTA.and(PRIORIDAD_ALTA);

    // 4. Transforma (Incidencia -> String): Function.
    //    Recibe una incidencia y entrega otro tipo de dato (un texto resumen).
    public static final Function<Incidencia, String> RESUMEN =
            incidencia -> incidencia.id() + " - " + incidencia.titulo();

    // 5. Calcula una categoría (Incidencia -> String): Function.
    //    Traduce la prioridad numérica a una etiqueta de negocio.
    public static final Function<Incidencia, String> CATEGORIA = incidencia ->
            switch (incidencia.prioridad()) {
                case 5 -> "CRITICA";
                case 4 -> "ALTA";
                case 2, 3 -> "MEDIA";
                case 1 -> "BAJA";
                default -> throw new IllegalArgumentException(
                        "Prioridad fuera de rango: " + incidencia.prioridad());
            };

    // 6. Actúa sin devolver valor (Incidencia -> void): Consumer.
    //    Su único propósito es el efecto (imprimir una notificación).
    public static final Consumer<Incidencia> NOTIFICAR =
            incidencia -> System.out.println("[ATENCION] " + RESUMEN.apply(incidencia));

    // 7. Provee datos sin recibir entrada (() -> Incidencia): Supplier.
    //    Es además un ejemplo de función como valor de retorno.
    public static Supplier<Incidencia> generadorSintetico() {
        AtomicInteger contador = new AtomicInteger(1);
        String[] estados = {"ABIERTA", "EN_PROCESO", "CERRADA"};
        return () -> {
            int n = contador.getAndIncrement();
            return new Incidencia("INC-%03d".formatted(n), "Incidencia sintética " + n,
                    estados[n % estados.length], (n % 5) + 1);
        };
    }
}
