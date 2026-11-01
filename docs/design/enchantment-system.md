# Fase 2 — Diseño del sistema de encantamientos

**Estado: BORRADOR DE DISEÑO.** Este documento inicia el diseño funcional; las cifras de equilibrio y las combinaciones todavía no están aprobadas ni implementadas.

## Objetivo

Diseñar un sistema de personalización para armas y miras de TaCZ que se sienta integrado al juego, conserve el comportamiento original de las armas y pueda extenderse a armas de Gun Packs sin depender de una lista cerrada de IDs.

## Principios de diseño

1. **TaCZ sigue siendo responsable del arma.** Tacz Enchanting añade efectos; no reemplaza el sistema de disparo, recarga, munición ni accesorios de TaCZ.
2. **Compatibilidad por capacidades y perfiles.** No asumir que el nombre o ID de un arma revela su categoría. Los perfiles deben poder ampliarse y las armas desconocidas deben recibir un comportamiento seguro.
3. **Ventajas con contrapartidas.** Los encantamientos poderosos deben tener un coste, desventaja o restricción significativa.
4. **Datos aislados.** Los datos propios se guardarán bajo el espacio de nombres taczenchanting, sin reemplazar el NBT de TaCZ ni el de otros mods.
5. **Separación de objetivos.** Los encantamientos del arma y los de la mira son sistemas distintos, aunque compartan infraestructura.
6. **Sin configuración obligatoria.** El mod debe funcionar con TaCZ y sus recursos normales; cualquier extensión futura debe ser opcional.
7. **Seguro ante contenido desconocido.** Un arma, accesorio o entidad no reconocidos no deben provocar errores ni recibir bonificaciones arbitrarias.

## Reglas generales propuestas

- Cada encantamiento tendrá un identificador estable, nombre traducible, nivel máximo, objetivo compatible y descripción de su ventaja y desventaja.
- Los niveles aumentarán el efecto de forma controlada; no se debe asumir que duplicar el nivel duplica el poder.
- Un arma solo podrá recibir encantamientos compatibles con su perfil y sus capacidades detectadas.
- Las incompatibilidades serán explícitas y centralizadas, no repartidas entre condiciones improvisadas.
- Retirar un encantamiento deberá eliminar solo sus propios datos y no alterar los demás encantamientos, el arma ni sus accesorios.
- La ausencia de datos de Tacz Enchanting se interpretará como un arma sin encantamientos.
- Los efectos de combate se procesarán en el lado lógico del servidor para evitar aplicar daño dos veces. Los efectos puramente visuales podrán sincronizarse aparte.
- No se definirá todavía un método de aplicación (mesa, receta u otro) hasta diseñar la mesa y los costes en las fases 6 y 7.

## Catálogo inicial propuesto para armas

Los siguientes nombres son ideas iniciales de la hoja de ruta, no una lista definitiva ni funciones ya implementadas.

| Perfil de arma | Ideas de encantamientos | Dirección de diseño |
| --- | --- | --- |
| Pistolas | Ejecución, Penetración | Precisión táctica y utilidad contra objetivos resistentes, con límites claros |
| Subfusiles | Adrenalina, Descarga eléctrica | Movilidad/ritmo de combate y control breve, evitando aturdimientos permanentes |
| Fusiles de asalto | Incendiario, Sobrecarga | Daño elemental o ráfagas de potencia a cambio de una desventaja medible |
| Francotiradores | Análisis anatómico, Ejecución | Recompensar impactos precisos sin crear multiplicadores descontrolados |
| Escopetas | Impacto devastador, Congelación | Potencia a corta distancia y control temporal con alcance limitado |
| Ametralladoras | Supresión, Perforación | Control de área y penetración con coste de precisión, movilidad o consumo |

**Nota de clasificación:** estas categorías serán perfiles configurables/extensibles, no una suposición sobre los campos internos de TaCZ. Si un arma de un Gun Pack no puede clasificarse con seguridad, conservará el funcionamiento normal de TaCZ y no recibirá efectos exclusivos de categoría.

## Encantamientos compartidos

Se podrá evaluar un pequeño grupo de encantamientos compartidos cuando el perfil del arma lo permita. No se aprobará uno como universal solo porque funcione técnicamente: debe tener sentido para cada tipo de arma y mantener sus contrapartidas.

## Encantamientos de miras

Se tratarán por separado de los encantamientos del arma. El diseño deberá respetar el zoom y las funciones originales de cada mira. La información adicional al apuntar y los efectos visuales se especificarán antes de implementar el sistema de miras (fase 10).

## Decisiones pendientes para cerrar la fase

- Aprobar el catálogo inicial y descartar o renombrar ideas.
- Definir nivel máximo por encantamiento.
- Especificar numéricamente ventajas y desventajas.
- Definir incompatibilidades entre encantamientos.
- Determinar qué perfiles de armas pueden usar cada encantamiento.
- Decidir cómo se obtienen/aplican los encantamientos en coordinación con las fases 6 y 7.
- Definir el comportamiento de las armas desconocidas y de los Gun Packs.
- Definir las reglas de los encantamientos de miras sin mezclarlas con las de armas.

## Criterio de finalización

La fase se considerará completa cuando el catálogo, los niveles, las ventajas/desventajas, las incompatibilidades y las reglas de compatibilidad estén documentados y aprobados. Este documento por sí solo no significa que la fase esté terminada ni que exista código funcional.
