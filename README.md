# ToDoList ♡

App Android de organización personal: tareas con prerrequisitos, notas, listas de compra, paletas de color y seguimiento diario de agua y ánimo.

Construida en Kotlin con SQLite como única fuente de datos.

---

## Características

- **Tareas** — nombre, dificultad, marca académica, fecha de vencimiento, estado completada y prerrequisitos (una tarea puede depender de otra).
- **Calendario** — vista de las tareas académicas por día. 
- **Notas** — notas de texto libre.
- **Lista de compras** — productos con cantidad y estado comprado/pendiente.
- **Personalización** — 7 paletas de color predefinidas, aplicadas a toda la app.
- **Bienestar** — registro diario de vasos de agua (0–6) y ánimo (feliz, neutral, cansado, relajado, inspirado).
- **Bienestar (tiempo)** — enlaces a música, videos y un temporizador.

---

## Stack

| Componente | Versión |
| --- | --- |
| Kotlin | 2.0.21 |
| AGP | 8.7.3 |
| compileSdk / targetSdk | 35 |
| minSdk | 24 |
| AppCompat | 1.7.0 |
| Material | 1.12.0 |
| ConstraintLayout | 2.2.0 |

---

## Estructura del proyecto

```
app/src/main/java/com/example/todolist/
├── AppPalette.kt            # Modelo de paleta de colores
├── PaletteManager.kt        # Carga y aplica la paleta activa
├── Task.kt                  # Modelo de tarea
├── DatabaseHelper.kt        # SQLiteOpenHelper: tablas y operaciones
├── MainActivity.kt          # Inicio, menú lateral, agua y ánimo
├── TasksActivity.kt         # Lista de tareas
├── CalendarActivity.kt      # Tareas por día
├── NotesActivity.kt         # Notas
├── ShoppingActivity.kt      # Lista de compras
├── CustomizationActivity.kt # Selección de paleta
├── WellnessActivity.kt      # Música, videos y temporizador
└── WellnessTimerActivity.kt # Temporizador
```

---

## Base de datos

Base `soft_days.db`, versión **2**, gestionada por `DatabaseHelper`.

### Tablas

| Tabla | Descripción |
| --- | --- |
| `TAREA` | Tareas. Autorreferenciada por `prerequisite_id`. |
| `NOTA` | Notas de texto. |
| `PRODUCTO_COMPRA` | Productos de la lista de compras. |
| `PALETA` | Paletas de color (9 columnas de color). |
| `CONFIGURACION` | Paleta activa. PK fija en 1: una sola fila. |
| `REGISTRO_DIARIO` | Agua y ánimo por día. `fecha` como PK. |

### Relaciones

- `TAREA.prerequisite_id` → `TAREA.id_tarea` con `ON DELETE SET NULL`
- `CONFIGURACION.id_paleta` → `PALETA.id_paleta`
- Claves foráneas activadas en `onConfigure`

### Normalización

Diseño en **3FN**:

- **1FN** — todos los atributos son atómicos, sin grupos repetidos.
- **2FN** — sin dependencias parciales de la clave.
- **3FN** — sin dependencias transitivas. La paleta se separó de la configuración en su propia tabla.

### Decisiones de diseño

- **El calendario no tiene tabla propia.** Consulta `TAREA` filtrando por `academica` y `fecha_vencimiento`.
- **`CONFIGURACION` con PK fija en 1** garantiza una única configuración activa sin lógica adicional.
- **`REGISTRO_DIARIO` usa `CONFLICT_REPLACE`.** Como el reemplazo afecta la fila completa, antes de guardar el agua se lee el ánimo existente y se reescribe junto con el nuevo valor (y viceversa). Sin esa lectura previa, registrar un vaso borraría el ánimo del día.
- **`prerequisite` (TEXT)** es una columna heredada sin uso. Se mantiene por compatibilidad; la lógica actual usa `prerequisite_id`.

---

## Compilar y ejecutar

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar en un dispositivo o emulador con API 24 o superior.

```bash
./gradlew assembleDebug
```

---
## Nueva versión: Migración de módulos a Firebase Firestore

En esta etapa se comenzó la migración de los módulos principales de la aplicación Soft Days desde SQLite hacia Firebase Firestore, con el objetivo de centralizar el almacenamiento de información en la nube y mantener la funcionalidad existente.

### Actividades realizadas

- **Migración del módulo de tareas:** se implementó la conexión con Firestore para registrar, consultar, actualizar y eliminar tareas.
- **Migración del calendario:** se adaptó para obtener las tareas almacenadas en Firestore, conservando la visualización y navegación del calendario.
- **Migración del módulo de compras:** se implementó el almacenamiento de productos en Firestore, permitiendo agregar productos, definir cantidades, marcar productos como comprados y eliminarlos.
- **Implementación de modelos y repositorios:** se crearon las clases `Tarea`, `TareaRepository`, `Producto` y `ProductoRepository` para organizar el acceso y la manipulación de los datos.
- **Operaciones asíncronas:** se utilizaron corrutinas y `await()` para realizar las operaciones con Firestore sin bloquear el hilo principal de la aplicación.
- **Pruebas funcionales:** se verificó el funcionamiento de los módulos migrados, comprobando que los datos se guardaran y que las operaciones de modificación y eliminación funcionaran correctamente.

### Estado de la etapa

Los módulos de tareas, calendario y compras se encuentran migrados y probados con Firebase Firestore.

Quedan pendientes la migración del módulo de notas, la configuración de paletas y la eliminación de las dependencias restantes de SQLite.

**Resultado:** se logró avanzar en la transición del almacenamiento local SQLite hacia Firebase Firestore, manteniendo las funcionalidades principales de los módulos migrados.
