# Fase 1 — Inspección del JAR real de TaCZ

Estado: **investigación técnica en progreso**.

## Artefacto proporcionado

- Archivo: `tacz-1.20.1-1.1.8-hotfix2.jar`
- Mod ID: `tacz`
- Versión declarada en el manifiesto: `1.1.8-hotfix2`
- Minecraft declarado en `META-INF/mods.toml`: `[1.20,1.20.2)`, que incluye Minecraft 1.20.1.
- Loader declarado: `javafml`
- Rango Forge declarado: `[46,)`
- Contenido del archivo: clases compiladas; no se encontró código fuente Java incluido como fuente legible.
- No se han requerido ni inspeccionado configuraciones personales ni Gun Packs del usuario.

## API verificada mediante inspección del bytecode

La inspección se hizo sobre las clases que existen en el JAR proporcionado, no solo sobre la rama remota del repositorio.

### Evento de disparo: `com.tacz.guns.api.event.common.GunFireEvent`

La clase contiene estos métodos públicos relevantes:

- `getShooter(): LivingEntity`
- `getGunItemStack(): ItemStack`
- `getLogicalSide(): LogicalSide`
- El evento es cancelable.

Esto ofrece una vía concreta para identificar el `ItemStack` del arma durante un disparo. Cualquier lógica de gameplay debe ejecutarse en el lado de servidor adecuado para evitar duplicar efectos entre cliente y servidor.

### Eventos de impacto: `EntityHurtByGunEvent`

La clase base expone:

- `getBullet(): Entity`
- `getHurtEntity(): Entity`
- `getAttacker(): LivingEntity`
- `getGunId(): ResourceLocation`
- `getGunDisplayId(): ResourceLocation`
- `getAmount()` y `getBaseAmount()`
- `isHeadShot()`
- `getLogicalSide()`

Hay subclases `EntityHurtByGunEvent.Pre` y `EntityHurtByGunEvent.Post`. La variante `Pre` expone setters para la entidad afectada, atacante, ID del arma, daño base, fuentes de daño y datos de headshot. Antes de elegir el evento final para cada encantamiento, se debe verificar su orden y comportamiento en ejecución.

### Identificación de armas: `IGun` y `AbstractGunItem`

- Existe `IGun.getIGunOrNull(ItemStack)`, que permite comprobar si un `ItemStack` implementa la API de arma de TaCZ.
- `IGun` ofrece métodos para obtener/establecer el `gunId` y `gunDisplayId`, gestionar munición y consultar accesorios.
- `AbstractGunItem` implementa `IGun`.
- Existe `GunItemDataAccessor`, con constantes de datos NBT relacionados con el ID del arma, modo de disparo, munición, accesorios, experiencia y otros estados.

Estos métodos son candidatos a usar para reconocer armas y mantener los datos propios de Tacz Enchanting sin depender del nombre visible del objeto.

### Proyectiles

Existe `com.tacz.guns.entity.EntityKineticBullet`, una entidad de proyectil que conserva identificadores del arma y contiene lógica de movimiento e impacto. La existencia de esta clase no implica que todos los tipos de munición o todas las mecánicas de TaCZ utilicen necesariamente esa misma ruta; eso requiere pruebas específicas.

## Decisiones técnicas provisionales

1. Usar el JAR del usuario como referencia principal de compatibilidad.
2. Para identificar armas, comprobar primero `IGun.getIGunOrNull(stack)` y consultar los identificadores de arma de la API en lugar de comparar nombres traducidos.
3. Para efectos al disparar, evaluar `GunFireEvent` y su lado lógico.
4. Para efectos al impactar, evaluar `EntityHurtByGunEvent.Pre/Post` según si el efecto necesita modificar el daño o reaccionar a un impacto procesado.
5. No asumir que encantamientos vanilla activan automáticamente efectos en las armas de TaCZ.
6. No añadir dependencias de configuración personal; la investigación se basa en el JAR proporcionado.

## Pendiente antes de cerrar la Fase 1

- [ ] Verificar en ejecución qué datos del arma están disponibles en cada evento y en qué momento.
- [ ] Confirmar el flujo de daño y cancelación de `Pre/Post`.
- [ ] Investigar cómo representar y persistir los encantamientos en el `ItemStack` del arma sin interferir con los datos NBT de TaCZ.
- [ ] Verificar clasificación de armas, miras, accesorios y cargadores a través de la API disponible.
- [ ] Decidir si la primera versión se limita a armas de fuego y qué comportamientos quedan fuera.
- [ ] Documentar resultados y criterios de aceptación para dar por cerrada la Fase 1.

## Alcance

Este documento refleja la inspección estática del JAR suministrado. Todavía no certifica compatibilidad en ejecución ni constituye una compilación del mod.


### Accesorios y catálogo de recursos

La inspección del mismo JAR también confirma:

- `AttachmentType` incluye `SCOPE`, `MUZZLE`, `STOCK`, `GRIP`, `LASER`, `EXTENDED_MAG` y `NONE`.
- `TimelessAPI` expone métodos para consultar índices comunes de armas, munición, accesorios y bloques mediante `ResourceLocation`, así como métodos de índices de cliente para presentación.
- La API de arma incluye métodos para consultar accesorios instalados y sus identificadores.

Esto permite investigar compatibilidad con packs sin leer configuraciones personales: la clasificación se puede estudiar a partir de la API y de los identificadores registrados. Los métodos de índices del cliente no deben invocarse desde lógica de servidor dedicado sin verificar su lado de ejecución.


## Segunda pasada: flujo de eventos en bytecode

Se inspeccionó el bytecode del mismo JAR con `javap -c -p`, buscando los puntos que publican eventos. Esto sigue siendo análisis estático; no sustituye una prueba en un cliente/servidor real.

### `GunFireEvent`

- En la ruta de disparo del cliente (`LocalPlayerShoot`) se publica un `GunFireEvent` con `LogicalSide.CLIENT`.
- En la ruta de disparo del servidor (`ModernKineticGunScriptAPI`) también se publica un `GunFireEvent` con `LogicalSide.SERVER`.
- El servidor envía además un mensaje de evento que vuelve a publicar una versión CLIENT en el cliente. Por tanto, un listener puede recibir eventos de ambos lados para un disparo y debe filtrar el lado antes de ejecutar efectos de gameplay.
- En la ruta de servidor, el resultado booleano de `EVENT_BUS.post(...)` se consulta antes de continuar con el envío del evento de disparo. Como el evento es cancelable, un listener externo podría impedir que esa ruta continúe; nuestros encantamientos no deben cancelar el disparo salvo que un diseño lo requiera explícitamente.

### `EntityHurtByGunEvent.Pre` y `Post`

- En `EntityKineticBullet`, el evento `Pre` se publica en el lado `SERVER` antes de aplicar el daño final.
- Si el evento se cancela, el método retorna inmediatamente y no continúa con esa resolución de daño.
- Si no se cancela, la lógica vuelve a leer del evento la entidad afectada, el atacante, el identificador del arma, el daño base, las fuentes de daño y los valores de headshot. Por tanto, los cambios efectuados por listeners al evento `Pre` afectan a la resolución posterior.
- En la ruta observada para entidades vivas, el evento `Post` se publica en el lado `SERVER` tras el procesamiento de daño cuando la víctima no ha muerto; la ruta de muerte publica un evento separado de muerte por arma. Así que `Post` no debe interpretarse como sinónimo universal de «cualquier impacto», y los efectos de muerte requieren investigación aparte.
- El mensaje de daño del servidor vuelve a emitir un `EntityHurtByGunEvent.Post` en el lado `CLIENT` para la presentación. Los efectos que cambian gameplay deben mantenerse autoritativos en el servidor y no duplicarse en el cliente.
- Se observó una ruta adicional de impacto en `TargetMinecart`; no se debe asumir que toda clase de objetivo usa exactamente el mismo flujo que `EntityKineticBullet`.

### Consecuencia provisional para el diseño

1. Para reaccionar al disparo, usar `GunFireEvent` filtrado por `LogicalSide.SERVER`, y validar el tirador y el `ItemStack` antes de leer datos propios.
2. Para modificar el daño, estudiar `EntityHurtByGunEvent.Pre`; evitar cancelar el evento y preferir cambios mínimos y acotados.
3. Para efectos posteriores al daño, usar `Post` solo cuando el tipo de objetivo y la ruta de impacto estén cubiertos. Si se necesita soportar todos los impactos, incluidas muertes y objetivos no vivos, hace falta una decisión explícita y pruebas adicionales.
4. Añadir pruebas de cliente y servidor para verificar el número de invocaciones por disparo/impacto y prevenir efectos duplicados.

## Estado actualizado

Esta pasada reduce las dudas sobre el orden y los lados de los eventos en las rutas inspeccionadas, pero **no cierra la Fase 1**: faltan pruebas en ejecución, decisión de persistencia de encantamientos y criterios de alcance de la primera versión.