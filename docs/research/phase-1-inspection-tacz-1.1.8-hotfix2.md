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

