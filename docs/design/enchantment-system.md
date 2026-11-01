# Fase 2 — Diseño del sistema de encantamientos

**Estado: EN DISEÑO — dirección acordada; catálogo y números aún pendientes de aprobación final.**

**Dirección elegida por el usuario:** híbrida (táctica + fantástica), con potencia equilibrada. Los encantamientos deben sentirse útiles y visibles sin convertir las armas en algo invencible ni borrar la identidad original de TaCZ.

## Objetivo

Diseñar un sistema de personalización para armas y miras de TaCZ que se sienta integrado al juego, conserve el comportamiento original de las armas y pueda extenderse a armas de Gun Packs sin depender de una lista cerrada de IDs.

## Principios de diseño

1. **TaCZ sigue siendo responsable del arma.** Tacz Enchanting añade efectos; no reemplaza el sistema de disparo, recarga, munición ni accesorios de TaCZ.
2. **Táctico + fantástico.** Combinar precisión, control y especialización con efectos como fuego, frío o electricidad, manteniendo una presentación sobria y coherente con TaCZ.
3. **Activación determinista, sin azar.** No habrá probabilidades aleatorias para activar encantamientos. Si un disparo impacta y se cumplen las condiciones descritas, el efecto se aplica siempre. El equilibrio se consigue mediante intensidad, duración, alcance, condiciones explícitas, consumo, precisión y contrapartidas.
4. **Compatibilidad por perfiles ampliables.** No asumir que el nombre o ID de un arma revela su categoría. Los perfiles se podrán extender y las armas desconocidas tendrán un comportamiento seguro.
5. **Datos aislados.** Los datos propios se guardarán bajo el espacio de nombres `taczenchanting`, sin reemplazar el NBT de TaCZ ni el de otros mods.
6. **Separación de objetivos.** Los encantamientos del arma y los de la mira son sistemas distintos, aunque compartan infraestructura.
7. **Sin configuración obligatoria.** El mod debe funcionar con TaCZ y sus recursos normales; cualquier extensión futura debe ser opcional.
8. **Seguro ante contenido desconocido.** Un arma, accesorio o entidad no reconocidos no deben provocar errores ni recibir bonificaciones arbitrarias.

## Reglas generales propuestas

- Cada encantamiento tendrá identificador estable, nombre traducible, nivel máximo, objetivo compatible y descripción de su ventaja y contrapartida.
- **Nivel máximo inicial propuesto: III** para la mayoría de encantamientos de armas. Un efecto que no escale de forma saludable podrá tener un máximo menor.
- Los niveles aumentarán el efecto gradualmente; no se asumirá que duplicar el nivel duplica el poder.
- Un arma solo podrá recibir encantamientos compatibles con su perfil y sus capacidades detectadas.
- Las incompatibilidades serán explícitas y centralizadas.
- Retirar un encantamiento eliminará solo sus propios datos y no alterará otros encantamientos, el arma ni sus accesorios.
- La ausencia de datos de Tacz Enchanting se interpretará como un arma sin encantamientos.
- Los efectos de combate se procesarán en el lado lógico del servidor para evitar aplicar daño dos veces. Los efectos puramente visuales podrán sincronizarse aparte.
- No se define todavía cómo se consiguen o aplican los encantamientos; eso se coordinará con las fases 6 y 7 (mesa y materiales/costes).
- Ningún encantamiento debe saltarse las reglas de TaCZ sobre munición, recarga, accesorios o funcionamiento del arma.

## Catálogo inicial propuesto para armas

Este es un catálogo de diseño, no una lista implementada. Los números son límites de diseño tentativos para mantener una potencia equilibrada y se ajustarán tras pruebas.

| Encantamiento | Perfil inicial | Efecto previsto | Contrapartida / límite | Niveles |
| --- | --- | --- | --- | --- |
| **Ejecución** | Armas de precisión; perfiles adicionales por decidir | Da una bonificación moderada contra objetivos ya debilitados; no debe ser una eliminación automática | No aumenta el daño general contra objetivos sanos; umbral y bonificación por definir | I–III |
| **Penetración** | Armas cuyo perfil permita proyectiles perforantes | Permite atravesar ciertos objetivos o mitigar una parte limitada de la protección, solo si la ruta de impacto de TaCZ lo permite | Eficacia reducida por objetivo/material y límites estrictos; no prometer penetración universal hasta validar el motor | I–III |
| **Adrenalina** | Subfusiles y perfiles móviles compatibles | Una baja o condición de combate puede conceder un impulso breve de movilidad o manejo | Duración breve, activación limitada y sin acumulación infinita | I–III |
| **Descarga eléctrica** | Subfusiles u otras armas compatibles | Cada impacto válido aplica siempre la descarga eléctrica al objetivo | Efecto de control breve y limitado; no se acumula sin límite. Los impactos posteriores vuelven a aplicar/renovar el efecto según reglas fijas, sin tiradas aleatorias | I–III |
| **Incendiario** | Fusiles de asalto y perfiles compatibles | Aplica una quemadura corta con daño total limitado | No debe mantener al objetivo ardiendo indefinidamente; posible coste de recursos o menor rendimiento en otra área | I–III |
| **Sobrecarga** | Fusiles de asalto u otras armas compatibles | Potenciación bajo una condición fija y visible (por ejemplo, tras una secuencia definida de disparos), nunca mediante una tirada aleatoria | Cadencia de activación predecible y contrapartida de manejo, precisión o consumo; nunca un multiplicador constante | I–III |
| **Análisis anatómico** | Francotiradores y armas de precisión | Tras apuntar o cumplir una condición, ofrece información limitada sobre el objetivo o mejora moderadamente un impacto preciso | Requiere preparación/precisión; no revela información a través de paredes ni garantiza daño crítico | I–III |
| **Impacto devastador** | Escopetas y perfiles de corto alcance | Refuerza el impacto a corta distancia | La bonificación cae rápidamente con la distancia; no debe volver a las escopetas efectivas a cualquier alcance | I–III |
| **Congelación** | Escopetas u otras armas compatibles | Aplica ralentización breve o acumulación de frío con un efecto visible | No congela indefinidamente ni inmoviliza jefes/entidades resistentes sin reglas explícitas | I–III |
| **Supresión** | Ametralladoras y perfiles de fuego sostenido | Los impactos sostenidos pueden reducir brevemente la precisión o velocidad de un objetivo susceptible | Requiere impactos repetidos; efecto corto, no se acumula sin límite y puede no afectar entidades inmunes | I–III |

### Consideraciones sobre Penetración

La hoja de ruta también menciona perforación para ametralladoras. Se tratará como un efecto compartido o una variante de perfil, no como dos encantamientos casi idénticos, hasta definir diferencias reales. No se implementará una penetración de entidades o bloques fingida si la API/ruta de impactos de TaCZ no la soporta de forma fiable.

### Perfiles de arma y compatibilidad

Los perfiles iniciales de diseño son pistola, subfusil, fusil de asalto, francotirador, escopeta y ametralladora. Son etiquetas del **mod**, no una afirmación de que TaCZ exponga una categoría interna universal equivalente.

- La clasificación deberá ser extensible para Gun Packs.
- No depender exclusivamente del nombre visible o de IDs concretos del pack por defecto.
- Si una arma no puede clasificarse con seguridad, conservará el comportamiento normal de TaCZ y no recibirá encantamientos exclusivos de perfil hasta que exista una regla explícita.
- Las reglas genéricas compartidas se podrán habilitar en armas desconocidas solo cuando su compatibilidad técnica se haya demostrado.

## Encantamientos compartidos

Candidatos por evaluar, sin aprobación definitiva:

- **Estabilidad:** mejora moderadamente el control en condiciones definidas, a cambio de reducir otra característica como velocidad de movimiento o manejo.
- **Recuperación:** ayuda a recuperar manejo tras disparar o tras una pausa; no debe aumentar directamente el daño.
- **Reserva:** podría modificar de forma limitada una característica de munición solo si TaCZ permite hacerlo sin romper recargas, cargadores ni sincronización.

Estos candidatos no se sumarán al catálogo definitivo hasta describir su efecto medible, su contrapartida y su compatibilidad.

## Encantamientos de miras

Se tratarán por separado de los encantamientos del arma. El diseño deberá respetar el zoom y las funciones originales de cada mira. Candidatos para la fase 10:

- **Telémetro:** información aproximada de distancia, si puede mostrarse sin interferir con la interfaz de TaCZ.
- **Enfoque:** mejora visual acotada al apuntar, sin alterar arbitrariamente el zoom original.
- **Marcado:** indicador temporal y limitado sobre un objetivo visible, sin revelar entidades a través de paredes.

Estos son conceptos preliminares. Se especificarán al llegar a la fase 10 y después de confirmar las posibilidades de la interfaz de miras.

## Reglas de equilibrio iniciales

1. Los encantamientos no deben aumentar el daño base de forma permanente y masiva.
2. Los efectos de control (frío/electricidad/supresión) deben ser breves, tener límites de acumulación y respetar inmunidades o resistencias razonables.
3. La activación debe ser predecible: ninguna tirada de probabilidad decide si un encantamiento funciona. Si depende de una condición (por ejemplo, objetivo debilitado o secuencia de disparos), esa condición debe estar definida y explicada.
4. Los efectos no se deben activar dos veces por el mismo disparo debido a eventos cliente/servidor.
5. Un encantamiento no puede conceder simultáneamente todas las ventajas sin compromisos; las incompatibilidades se decidirán con el catálogo final.
6. Los valores exactos de daño, intensidad, duración, distancia y coste quedan pendientes de pruebas y no se presentan como cifras finales. Las probabilidades aleatorias de activación quedan descartadas.

## Decisiones pendientes para cerrar la fase

- Aprobar, renombrar o descartar cada encantamiento del catálogo inicial.
- Confirmar niveles máximos por encantamiento.
- Definir valores medibles para ventajas, desventajas, condiciones deterministas, duraciones y distancias.
- Definir incompatibilidades y combinaciones permitidas.
- Aprobar qué perfiles admiten cada encantamiento.
- Decidir cómo se obtienen/aplican los encantamientos en coordinación con las fases 6 y 7.
- Definir el comportamiento de las armas desconocidas y de los Gun Packs.
- Especificar el catálogo de miras al abordar la fase 10 sin mezclarlo con los efectos de armas.

## Criterio de finalización

La fase se considerará completa cuando el catálogo, los niveles, las ventajas/desventajas, las incompatibilidades y las reglas de compatibilidad estén documentados y aprobados. La dirección híbrida y la potencia equilibrada ya están acordadas; el catálogo y los valores siguen siendo propuestas. Este documento no significa que exista código funcional ni que la fase esté cerrada.
