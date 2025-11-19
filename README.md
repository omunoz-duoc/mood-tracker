# Mood Tracker 📊

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)

Aplicación móvil Android para seguimiento de estados de ánimo desarrollada como proyecto para el curso **Aplicaciones Móviles (DSY1105)** de DUOC UC.

## 📱 Descripción

Mood Tracker permite a los usuarios registrar y visualizar su estado emocional diario, ayudándoles a comprender mejor sus patrones emocionales a lo largo del tiempo. La aplicación utiliza las últimas tecnologías de desarrollo Android con Jetpack Compose y sigue las mejores prácticas de arquitectura MVVM.

### Características Principales

✅ **Autenticación de Usuarios**
- Sistema completo de login y registro
- Validación de formularios en tiempo real
- Persistencia de sesión con DataStore
- Contraseñas hasheadas con SHA-256

✅ **Seguimiento de Estados de Ánimo**
- 7 emociones diferentes (Feliz, Triste, Ansioso, Tranquilo, Enojado, Emocionado, Cansado)
- Feedback háptico (vibración) al seleccionar emoción
- Notas opcionales para cada registro
- Animaciones fluidas y modernas

✅ **Visualización de Historial**
- Lista cronológica de registros emocionales
- Animaciones de entrada escalonadas
- Tarjetas con código de colores por emoción
- Fechas formateadas en español

✅ **Recordatorios Diarios**
- Notificaciones locales configurables
- Programación diaria a las 20:00 hrs
- Toggle fácil desde la pantalla principal

## 🛠 Tecnologías Utilizadas

### Lenguaje y Framework
- **Kotlin 2.0.21**: Lenguaje de programación moderno
- **Jetpack Compose**: UI declarativa con Material Design 3
- **Gradle 8.13.1**: Sistema de construcción

### Arquitectura y Librerías
- **MVVM Architecture**: Separación clara de capas (UI, Domain, Data)
- **Navigation Compose 2.8.5**: Navegación entre pantallas
- **Room 2.6.1**: Base de datos SQLite local
- **KSP**: Procesamiento de anotaciones en tiempo de compilación
- **DataStore Preferences 1.1.1**: Almacenamiento persistente
- **Coroutines & Flow**: Programación asíncrona y reactiva
- **ViewModel & StateFlow**: Gestión de estado del ciclo de vida

### Recursos Nativos
1. **Vibración (Haptic Feedback)**: Retroalimentación táctil en selección de ánimo
2. **Notificaciones Locales**: Recordatorios diarios con AlarmManager

## 📋 Requisitos

- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 36
- **Java**: 11+
- **Gradle**: 8.13.1+

## 🚀 Instalación

### Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/mood-tracker.git
cd mood-tracker
```

### Construir el Proyecto

```bash
# En Windows
gradlew.bat build

# En Linux/Mac
./gradlew build
```

### Ejecutar en Emulador/Dispositivo

```bash
# Instalar APK debug
gradlew.bat installDebug
```

## 📱 Flujo de la Aplicación

### Flujo de Autenticación
1. **Login**: Ingreso con email y contraseña validados
2. **Registro**: Creación de cuenta con confirmación de contraseña

### Funcionalidades Principales
1. **Home**: Pantalla principal con toggle de notificaciones y navegación
2. **Selección de Ánimo**: Cuadrícula de 7 emociones con animaciones y haptic feedback
3. **Historial**: Lista animada de registros emocionales previos con filtrado por fecha

## 🏗 Arquitectura

### Estructura del Proyecto

```
app/src/main/java/cl/duoc/dsy1105/moodtracker/
├── ui/                    # Capa de presentación
│   ├── screens/          # Pantallas Compose
│   ├── viewmodel/        # ViewModels para state management
│   └── theme/            # Tema Material3
├── domain/               # Lógica de negocio
│   ├── model/           # Modelos de dominio
│   └── validators/      # Validación de formularios
├── data/                # Capa de datos
│   ├── local/          # Fuentes de datos locales
│   │   ├── entities/   # Entidades de Room
│   │   ├── dao/        # Data Access Objects
│   │   └── *.kt        # Database, SessionManager, Preferences
│   └── repository/     # Patrón Repository
├── notifications/       # Sistema de notificaciones
└── navigation/         # Grafo de navegación
```

### Base de Datos

**Room Database v2** con dos tablas principales:

**users**
- id (PK, auto-increment)
- email (único)
- passwordHash (SHA-256)
- createdAt

**mood_entries**
- id (PK, auto-increment)
- userId (FK → users, CASCADE)
- moodType (String)
- note (String?, opcional)
- date (timestamp)

## 🎯 Funcionalidades por Fase

### ✅ Fase 1: Autenticación
- Login con validación de email/contraseña
- Registro con confirmación de contraseña
- Persistencia de sesión con DataStore
- Animaciones de shake en errores

### ✅ Fase 2: Tracking de Ánimo
- 7 emociones con emojis y colores
- Feedback háptico (vibración)
- Notas opcionales
- Persistencia en Room

### ✅ Fase 3: Visualización
- Historial con LazyColumn
- Animaciones de entrada (slide + fade)
- Tarjetas con código de colores
- Estado vacío personalizado

### ✅ Fase 4: Recursos Nativos
- Notificaciones locales diarias
- AlarmManager para programación
- Toggle de notificaciones
- Permisos para Android 13+

### ✅ Fase 5: Cierre del Proyecto
- Documentación completa
- APK exportado
- Código limpio y organizado

## 🔐 Permisos

La aplicación requiere los siguientes permisos:

```xml
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

## 📝 Convención de Commits

El proyecto sigue una convención estructurada de commits:

- `feat: ` - Nueva funcionalidad
- `fix: ` - Corrección de bug
- `docs: ` - Cambios en documentación
- `chore: ` - Tareas de mantenimiento

Ejemplo:
```
feat: add mood selection screen with haptic feedback
```

## 👨‍💻 Desarrollo

### Branching Strategy

- `main`: Rama principal estable
- `dev`: Desarrollo activo
- `feature/*`: Ramas de características individuales

### Flujo de Trabajo

1. Crear rama desde `dev`: `git checkout -b feature/nueva-funcionalidad`
2. Desarrollar y commit: `git commit -m "feat: descripción"`
3. Merge a `dev`: `git merge feature/nueva-funcionalidad`

## 🧪 Testing

```bash
# Ejecutar tests unitarios
gradlew.bat test

# Ejecutar tests instrumentados
gradlew.bat connectedAndroidTest
```

## 📦 Generación de APK

```bash
# APK Debug
gradlew.bat assembleDebug

# APK Release
gradlew.bat assembleRelease
```

El APK se genera en: `app/build/outputs/apk/`

## 📖 Documentación Adicional

- Ver `CLAUDE.md` para guía técnica detallada
- Ver `FEATURES.md` para changelog de características
- Ver `instrucciones-especificas.md` para plan del proyecto

## ✅ Requisitos Académicos Cumplidos

Este proyecto cumple con todos los requisitos del curso DSY1105:

- ✅ Interfaz móvil estructurada y usable
- ✅ Formularios con validación completa
- ✅ Validaciones desde lógica (no en UI)
- ✅ Iconos y mensajes visuales
- ✅ Animaciones funcionales (shake, slide, fade, scale)
- ✅ Proyecto modular con arquitectura clara
- ✅ Persistencia local con SQLite/Room
- ✅ **Acceso a 2 recursos nativos**: Vibración + Notificaciones

## 👤 Autor

**Proyecto Académico DUOC UC**
- Curso: Aplicaciones Móviles (DSY1105)
- Institución: DUOC UC

## 📄 Licencia

Este proyecto es académico y fue desarrollado con fines educativos.

## 🙏 Agradecimientos

- Profesor del curso DSY1105
- DUOC UC
- Comunidad de Android Developers
- Claude Code para asistencia en desarrollo

---

**Desarrollado con ❤️ usando Kotlin y Jetpack Compose**
