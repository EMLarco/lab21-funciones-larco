package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.PrioridadAlta;
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

        System.out.println("=== Fase 3: contrato ReglaIncidencia ===");

        // 1) Clase con nombre
        ReglaIncidencia prioridadAltaClase = new PrioridadAlta();

        // 2) Clase anónima
        ReglaIncidencia abiertaAnonima = new ReglaIncidencia() {
            @Override
            public boolean evaluar(Incidencia incidencia) {
                return "ABIERTA".equals(incidencia.estado());
            }
        };

        // 3) Expresión lambda
        ReglaIncidencia abiertaLambda = incidencia -> "ABIERTA".equals(incidencia.estado());

        ReglaIncidencia prioridadAltaAnonima = new ReglaIncidencia() {
            @Override
            public boolean evaluar(Incidencia incidencia) {
                return incidencia.prioridad() >= 4;
            }
        };

        ReglaIncidencia prioridadAltaLambda = incidencia -> incidencia.prioridad() >= 4;

        Incidencia[] incidencias = {
            new Incidencia("INC-101", "Servidor caído", "ABIERTA", 5),
            new Incidencia("INC-102", "Lentitud", "ABIERTA", 3),
            new Incidencia("INC-103", "Respaldo", "CERRADA", 4)
        };

        System.out.println("Regla 'prioridad alta': clase con nombre, clase anónima y lambda");
        System.out.printf("%-9s %-18s %-18s %-18s %-12s%n",
                "ID", "clase", "anónima", "lambda", "coinciden");
        for (Incidencia incidencia : incidencias) {
            boolean porClase = prioridadAltaClase.evaluar(incidencia);
            boolean porAnonima = prioridadAltaAnonima.evaluar(incidencia);
            boolean porLambda = prioridadAltaLambda.evaluar(incidencia);
            System.out.printf("%-9s %-18s %-18s %-18s %-12s%n",
                    incidencia.id(), porClase, porAnonima, porLambda,
                    porClase == porAnonima && porAnonima == porLambda);
        }

        System.out.println("Regla 'abierta': clase anónima vs lambda");
        System.out.printf("%-9s %-18s %-18s %-12s%n", "ID", "anónima", "lambda", "coinciden");
        for (Incidencia incidencia : incidencias) {
            boolean porAnonima = abiertaAnonima.evaluar(incidencia);
            boolean porLambda = abiertaLambda.evaluar(incidencia);
            System.out.printf("%-9s %-18s %-18s %-12s%n",
                    incidencia.id(), porAnonima, porLambda, porAnonima == porLambda);
        }

        // Combinación con el método default y adaptación a Predicate mediante referencia a método
        ReglaIncidencia requiereAtencion = abiertaLambda.y(new PrioridadAlta());
        Predicate<Incidencia> comoPredicado = requiereAtencion::evaluar;

        System.out.println("Combinación: abiertaLambda.y(new PrioridadAlta()) y referencia a método");
        System.out.printf("%-9s %-22s %-22s%n", "ID", "Regla.evaluar", "Predicate.test");
        for (Incidencia incidencia : incidencias) {
            System.out.printf("%-9s %-22s %-22s%n",
                    incidencia.id(),
                    requiereAtencion.evaluar(incidencia),
                    comoPredicado.test(incidencia));
        }

        // La Fase 5 se agregará a continuación en este mismo main.
    }
}
