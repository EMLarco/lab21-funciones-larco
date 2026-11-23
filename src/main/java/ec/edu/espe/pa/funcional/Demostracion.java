package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Demostracion {

    public static void main(String[] args) {
        System.out.println("=== Fase 2: interfaces estándar ===");

        Predicate<Incidencia> estaAbierta =
                incidencia -> "ABIERTA".equals(incidencia.estado());
        Function<Incidencia, String> obtenerResumen =
                incidencia -> incidencia.id() + " - " + incidencia.titulo();
        Consumer<Incidencia> mostrar =
                incidencia -> System.out.println(incidencia.titulo());
        Supplier<Incidencia> crearEjemplo =
                () -> new Incidencia("INC-001", "Error de acceso", "ABIERTA", 3);

        System.out.println("Lambdas definidas: todavía no se ha ejecutado ninguna");

        Incidencia ejemplo = crearEjemplo.get();            // Supplier -> get()
        boolean abierta    = estaAbierta.test(ejemplo);     // Predicate -> test()
        String resumen     = obtenerResumen.apply(ejemplo); // Function -> apply()
        mostrar.accept(ejemplo);                            // Consumer -> accept()

        System.out.println("Supplier -> get(): " + ejemplo);
        System.out.println("Predicate -> test(): " + abierta);
        System.out.println("Function -> apply(): " + resumen);
        System.out.println("Consumer -> accept(): título impreso arriba");

        System.out.println("--- Sintaxis de lambdas ---");
        Predicate<Incidencia> altaExpresion = i -> i.prioridad() >= 4;
        Predicate<Incidencia> altaBloque = i -> {
            int p = i.prioridad();
            return p >= 4;
        };
        Predicate<Incidencia> altaTipada = (Incidencia i) -> i.prioridad() >= 4;
        Predicate<Incidencia> altaVar = (var i) -> i.prioridad() >= 4;

        System.out.println("Lambda de una expresión: " + altaExpresion.test(ejemplo));
        System.out.println("Lambda con bloque: " + altaBloque.test(ejemplo));
        System.out.println("Tipo declarado: " + altaTipada.test(ejemplo));
        System.out.println("Con var: " + altaVar.test(ejemplo));
        System.out.println("Las cuatro coinciden: "
                + (altaExpresion.test(ejemplo) == altaBloque.test(ejemplo)
                && altaBloque.test(ejemplo) == altaTipada.test(ejemplo)
                && altaTipada.test(ejemplo) == altaVar.test(ejemplo)));

        System.out.println("--- Captura de variables (efectivamente final) ---");
        int umbral = 4;                                  // efectivamente final
        Predicate<Incidencia> superaUmbral = i -> i.prioridad() >= umbral;
        // umbral = 5; // Si se descomenta, no compila: "local variables referenced
        //              // from a lambda expression must be final or effectively final"
        System.out.println("superaUmbral.test(ejemplo): " + superaUmbral.test(ejemplo));

        // Las Fases 3 y 5 se agregarán a continuación en este mismo main.
    }
}
