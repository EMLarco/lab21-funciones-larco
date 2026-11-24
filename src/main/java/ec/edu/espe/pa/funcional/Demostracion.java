package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.contrato.FabricaIncidencia;
import ec.edu.espe.pa.funcional.contrato.ReglaIncidencia;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.PrioridadAlta;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.List;

import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;

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

        System.out.println();
        System.out.println("=== Fase 4: funciones del caso práctico ===");
        System.out.println("UMBRAL_PRIORIDAD_ALTA = " + ReglasIncidencia.UMBRAL_PRIORIDAD_ALTA);
        System.out.printf("%-10s %-13s %-15s %-18s %-9s%n",
                "prioridad", "ESTA_ABIERTA", "PRIORIDAD_ALTA", "REQUIERE_ATENCION", "CATEGORIA");
        for (int prioridad = 1; prioridad <= 5; prioridad++) {
            Incidencia muestra = new Incidencia("INC-20" + prioridad, "Muestra", "ABIERTA", prioridad);
            System.out.printf("%-10d %-13s %-15s %-18s %-9s%n", prioridad,
                    ReglasIncidencia.ESTA_ABIERTA.test(muestra),
                    ReglasIncidencia.PRIORIDAD_ALTA.test(muestra),
                    ReglasIncidencia.REQUIERE_ATENCION.test(muestra),
                    ReglasIncidencia.CATEGORIA.apply(muestra));
        }
        System.out.println("RESUMEN.apply(INC-101): " + ReglasIncidencia.RESUMEN.apply(incidencias[0]));
        ReglasIncidencia.NOTIFICAR.accept(incidencias[0]);
        Supplier<Incidencia> generador = ReglasIncidencia.generadorSintetico();
        System.out.println("generadorSintetico().get(): " + generador.get());
        System.out.println("generadorSintetico().get(): " + generador.get());
        System.out.println("generadorSintetico().get(): " + generador.get());

        System.out.println();
        System.out.println("=== Fase 5: composición y referencias a métodos ===");
        Incidencia muestraCritica = incidencias[0];   // INC-101, ABIERTA, 5
        Incidencia muestraMedia = incidencias[1];     // INC-102, ABIERTA, 3
        Incidencia muestraCerrada = incidencias[2];   // INC-103, CERRADA, 4

        // Predicate: and, or, negate
        Predicate<Incidencia> noAbierta     = ESTA_ABIERTA.negate();
        Predicate<Incidencia> pendienteBaja = ESTA_ABIERTA.and(PRIORIDAD_ALTA.negate());
        Predicate<Incidencia> revisarHoy    = REQUIERE_ATENCION.or(i -> i.prioridad() == 5);

        System.out.printf("%-9s %-11s %-14s %-11s%n", "ID", "noAbierta", "pendienteBaja", "revisarHoy");
        for (Incidencia incidencia : incidencias) {
            System.out.printf("%-9s %-11s %-14s %-11s%n", incidencia.id(),
                    noAbierta.test(incidencia), pendienteBaja.test(incidencia), revisarHoy.test(incidencia));
        }

        // Function: andThen (primero this, luego la otra) y compose (primero la otra, luego this)
        Function<String, String> aMayusculas = String::toUpperCase;
        Function<Incidencia, String> resumenMayusculas = RESUMEN.andThen(aMayusculas);
        Function<Incidencia, String> mismoResultado    = aMayusculas.compose(RESUMEN);
        Function<Incidencia, String> etiqueta          = CATEGORIA.andThen(c -> "[" + c + "]");

        System.out.println("RESUMEN.andThen(aMayusculas)  : " + resumenMayusculas.apply(muestraCritica));
        System.out.println("aMayusculas.compose(RESUMEN)  : " + mismoResultado.apply(muestraCritica));
        System.out.println("¿Mismo texto?                 : "
                + resumenMayusculas.apply(muestraCritica).equals(mismoResultado.apply(muestraCritica)));
        System.out.println("CATEGORIA.andThen(etiqueta)   : " + etiqueta.apply(muestraMedia));

        // Ejemplo en el que el orden de composición SÍ cambia el resultado
        Function<String, String> agregarPrefijo = texto -> "inc: " + texto;
        System.out.println("aMayusculas.andThen(prefijo)  : "
                + aMayusculas.andThen(agregarPrefijo).apply(RESUMEN.apply(muestraCritica)));
        System.out.println("aMayusculas.compose(prefijo)  : "
                + aMayusculas.compose(agregarPrefijo).apply(RESUMEN.apply(muestraCritica)));

        // Consumer: andThen encadena dos acciones sobre el mismo elemento
        List<String> bitacora = new ArrayList<>();
        Consumer<Incidencia> registrar = i -> bitacora.add(i.id());
        Consumer<Incidencia> notificarYRegistrar = NOTIFICAR.andThen(registrar);
        notificarYRegistrar.accept(muestraCritica);
        notificarYRegistrar.accept(muestraCerrada);
        System.out.println("Bitácora tras NOTIFICAR.andThen(registrar): " + bitacora);

        // Referencias a métodos, en sus cuatro variantes
        System.out.println("--- Referencias a métodos ---");
        // Método estático:                     Clase::metodoEstatico
        Predicate<Incidencia> esCritica = ReglasIncidencia::esCritica;
        System.out.println("ReglasIncidencia::esCritica (INC-101): " + esCritica.test(muestraCritica));
        // Método de un objeto particular:      objeto::metodo
        List<Incidencia> listaIncidencias = List.of(incidencias);
        System.out.println("System.out::println sobre la lista:");
        listaIncidencias.forEach(System.out::println);
        // Método de una instancia arbitraria: Tipo::metodoDeInstancia
        Function<Incidencia, String> titulo = Incidencia::titulo;
        System.out.println("Incidencia::titulo (INC-102): " + titulo.apply(muestraMedia));
        // Constructor:                         Clase::new
        Supplier<List<Incidencia>> nuevaLista = ArrayList::new;
        FabricaIncidencia fabrica = Incidencia::new;
        List<Incidencia> creadas = nuevaLista.get();
        creadas.add(fabrica.crear("INC-300", "Creada con Incidencia::new", "ABIERTA", 4));
        System.out.println("ArrayList::new + Incidencia::new: " + creadas);
    }
}
