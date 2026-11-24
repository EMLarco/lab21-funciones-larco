# lab21-funciones-larco

Refactorización de código imperativo a funcional en **Java 21** usando interfaces funcionales
(`Predicate`, `Function`, `Consumer`, `Supplier`), un contrato propio con `@FunctionalInterface`,
expresiones lambda, referencias a métodos y composición de funciones. La equivalencia entre
ambas versiones se verifica con **JUnit 5**.

Laboratorio 2.1 de Programación Avanzada — Universidad de las Fuerzas Armadas ESPE, sede Santo Domingo.

## Caso práctico

Un pequeño gestor de incidencias de soporte con datos sintéticos. La versión imperativa recorre la
lista con un `for` + `if`; la versión funcional separa tres responsabilidades que llegan como parámetros:

| Responsabilidad | Interfaz | Ejemplo |
|---|---|---|
| Seleccionar | `Predicate<Incidencia>` | `REQUIERE_ATENCION = ESTA_ABIERTA.and(PRIORIDAD_ALTA)` |
| Transformar | `Function<Incidencia, R>` | `Incidencia::titulo`, `RESUMEN`, `CATEGORIA` |
| Actuar | `Consumer<Incidencia>` | `NOTIFICAR` |
| Proveer | `Supplier<Incidencia>` | `generadorSintetico()` |

## Estructura

```
src/main/java/ec/edu/espe/pa/funcional/
├── modelo/       Incidencia.java (record con validación)
├── contrato/     ReglaIncidencia.java, FabricaIncidencia.java
├── reglas/       ReglasIncidencia.java, PrioridadAlta.java
├── imperativo/   ProcesadorImperativo.java (referencia, no se modifica)
├── funcional/    ProcesadorFuncional.java
├── Demostracion.java
└── App.java
src/test/java/ec/edu/espe/pa/funcional/
├── ReglasIncidenciaTest.java
└── EquivalenciaTest.java
```

## Ejecución

```bash
mvn test                                                    # 28 pruebas, 0 fallos
mvn -q compile exec:java -Dexec.mainClass=ec.edu.espe.pa.funcional.App
```

Salida de `App`:

```
== Versión imperativa ==
Caída del servidor
Error de acceso al sistema
== Versión funcional ==
Caída del servidor
Error de acceso al sistema
== Notificaciones ==
[ATENCION] INC-001 - Caída del servidor
[ATENCION] INC-005 - Error de acceso al sistema
== Incidencias abiertas con su categoría ==
INC-001 - Caída del servidor [CRITICA]
INC-002 - Error tipográfico en el menú [MEDIA]
INC-004 - Lentitud en reportes [MEDIA]
INC-005 - Error de acceso al sistema [ALTA]
```

## Autor

Esteban Larco — [@EMLarco](https://github.com/EMLarco)
