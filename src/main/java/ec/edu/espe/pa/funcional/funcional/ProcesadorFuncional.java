package ec.edu.espe.pa.funcional.funcional;

import ec.edu.espe.pa.funcional.modelo.Incidencia;
import ec.edu.espe.pa.funcional.reglas.ReglasIncidencia;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Versión funcional del procesamiento de incidencias: la selección (Predicate),
 * la transformación (Function) y la acción (Consumer) llegan como parámetros.
 */
public class ProcesadorFuncional {

    // Funciones como parámetros: el método no conoce las reglas concretas
    public <R> List<R> seleccionar(List<Incidencia> incidencias,
                                   Predicate<Incidencia> regla,
                                   Function<Incidencia, R> transformacion) {
        return incidencias.stream()
                .filter(regla)
                .map(transformacion)
                .toList();
    }

    public List<String> titulosQueRequierenAtencion(List<Incidencia> incidencias) {
        return seleccionar(incidencias, ReglasIncidencia.REQUIERE_ATENCION, Incidencia::titulo);
    }

    public void ejecutar(List<Incidencia> incidencias,
                         Predicate<Incidencia> regla,
                         Consumer<Incidencia> accion) {
        incidencias.stream().filter(regla).forEach(accion);
    }
}
