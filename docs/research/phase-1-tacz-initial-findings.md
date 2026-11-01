# Fase 1 — Investigación técnica de TaCZ

Estado: **en progreso**. Este documento recoge hallazgos iniciales del código público de TaCZ para la rama `1.20.1`. Deben contrastarse con el JAR exacto que se usará en el modpack antes de implementar funcionalidades.

## Fuentes consultadas

- Repositorio oficial: https://github.com/MCModderAnchor/TACZ
- Rama consultada: `1.20.1`
- `EntityKineticBullet.java`: https://github.com/MCModderAnchor/TACZ/blob/1.20.1/src/main/java/com/tacz/guns/entity/EntityKineticBullet.java
- `AbstractGunItem.java`: https://github.com/MCModderAnchor/TACZ/blob/1.20.1/src/main/java/com/tacz/guns/api/item/gun/AbstractGunItem.java
- `EntityHurtByGunEvent.java`: https://github.com/MCModderAnchor/TACZ/blob/1.20.1/src/main/java/com/tacz/guns/api/event/common/EntityHurtByGunEvent.java
- `GunFireEvent.java`: https://github.com/MCModderAnchor/TACZ/blob/1.20.1/src/main/java/com/tacz/guns/api/event/common/GunFireEvent.java

## Hallazgos iniciales verificados en el código público

### Proyectiles y disparos

- TaCZ tiene una entidad de proyectil cinético llamada `EntityKineticBullet`.
- El código de la bala mantiene identificadores de arma (`gunId`) y de presentación (`gunDisplayId`), además de datos de daño, penetración, ignición y explosión.
- La lógica de impacto de la bala se ejecuta en el servidor.
- El proyectil publica eventos de TaCZ durante el procesamiento del daño.

### Eventos de integración candidatos

- `GunFireEvent`: evento relacionado con el disparo; el código público muestra su publicación tanto en rutas de cliente como de servidor. Hay que respetar el lado lógico y evitar aplicar efectos de juego dos veces.
- `EntityHurtByGunEvent.Pre`: se publica antes de resolver el daño por arma y permite inspeccionar/modificar el procesamiento antes de que concluya.
- `EntityHurtByGunEvent.Post`: se publica después del procesamiento del daño por arma y resulta candidato para efectos que dependan de un impacto confirmado.
- La implementación final debe comprobar cancelación, lado lógico, entidad atacante, bala y datos del arma antes de ejecutar efectos.

## Implicaciones para Tacz Enchanting

1. No asumir que los encantamientos vanilla afectarán automáticamente a las armas de TaCZ.
2. Investigar si el `ItemStack` del arma puede asociarse de forma fiable al proyectil/evento en la versión exacta que se instalará.
3. Para efectos de impacto, evaluar primero `EntityHurtByGunEvent.Pre/Post`; para efectos de disparo, evaluar `GunFireEvent`.
4. La clasificación de armas no debe depender solo del nombre visible. Investigar identificadores de arma y datos de Gun Packs.
5. Los datos personalizados deben guardarse en el `ItemStack` del arma o en un mecanismo persistente compatible; no asumir que el proyectil temporal es persistente.
6. Los encantamientos de miras y accesorios requieren investigación separada de la identificación de accesorios y su almacenamiento.

## Pendiente de verificar con la instalación del usuario

- Versión exacta del archivo `tacz-1.20.1-*.jar`.
- Firma y disponibilidad de los eventos/API en esa versión concreta.
- Cómo obtener el `ItemStack` de arma correcto al disparar y al impactar.
- Cómo TaCZ identifica categorías, armas, cargadores, miras y accesorios.
- Formato y ubicación de los Gun Packs usados por el usuario.
- Si el proyecto debe compilar contra el JAR normal o el artefacto de fuentes/de desarrollo publicado para la versión elegida.

## Próximos pasos de la Fase 1

- [ ] Revisar el JAR exacto de TaCZ instalado por el usuario.
- [ ] Revisar configuraciones y Gun Packs presentes en la instalación.
- [ ] Verificar las firmas de eventos y APIs contra esa versión.
- [ ] Documentar clasificación de armas, accesorios, cargadores y miras.
- [ ] Cerrar el informe técnico antes de iniciar la Fase 2.
