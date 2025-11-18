# Mood Tracker App  
Una aplicación móvil minimalista para registrar estados de ánimo, construida con **Kotlin**, **Jetpack Compose**, **Room (SQLite)** y **Android Jetpack**.  
Creada siguiendo requisitos académicos estrictos: formularios validados, arquitectura modular, persistencia local, animaciones y uso de recursos nativos del dispositivo.

---

## Características

### Autenticación
- Pantallas de inicio de sesión y registro  
- Validación de email y contraseña manejada desde clases de lógica (no en la UI)  
- Retroalimentación visual con íconos y mensajes de error  
- Animaciones en envíos de formulario inválidos  
- Persistencia de sesión local

### Registro del estado de ánimo
- Pantalla de selección de ánimo con íconos animados  
- Retroalimentación háptica (vibración) al seleccionar un ánimo  
- Nota opcional del día con validación  
- Almacenamiento local de registros usando **Room (SQLite)**

### Historial y visualización
- Pantalla de historial con estados de ánimo anteriores  
- Gráficos simples o listas animadas (según implementación final)

### Integración con recursos nativos
- Vibración háptica  
- Notificaciones locales (recordatorio diario para registrar el ánimo)

### Arquitectura
- **Estructura modular**
```

/ui
/components
/domain
/data
/resources

```
- Patrón MVVM + Repository  
- Gestión de estado con `ViewModel` y `StateFlow`  

---

## Stack Tecnológico
- **Kotlin**
- **Jetpack Compose**
- **Navigation Compose**
- **Room (SQLite)**
- **ViewModel + StateFlow**
- **Material3**
- **Notificaciones de Android**
- **APIs de retroalimentación háptica**

---

## Estructura del Proyecto

```

app/
├── data/
│   ├── local/        # Base de datos Room, DAO, entidades
│   └── repository/   # Repositorios de Usuario y Mood
├── domain/
│   ├── models/       # Modelos de negocio
│   └── validators/   # Lógica de validación (login, registro, notas)
├── ui/
│   ├── login/
│   ├── register/
│   ├── home/
│   ├── mood/
│   └── history/
├── components/       # Componentes reutilizables de Compose
└── resources/        # Imágenes, íconos, temas

````

---

## Requisitos (del encargo académico)

Este proyecto cumple los siguientes requisitos:

- ✔️ Interfaz móvil estructurada y usable  
- ✔️ Formularios con validación, íconos y mensajes visuales  
- ✔️ Validaciones manejadas desde lógica (no en la interfaz)  
- ✔️ Animaciones funcionales  
- ✔️ Proyecto modular  
- ✔️ Persistencia local con **SQLite**  
- ✔️ Repositorio en GitHub + planificación en Trello  
- ✔️ Acceso a **dos recursos nativos como mínimo**

---

## Cómo ejecutar la app

1. Clona el repositorio:  
   ```bash
   git clone https://github.com/omunoz-duoc/mood-tracker.git
````

2. Abre el proyecto en **Android Studio**
3. Sincroniza Gradle
4. Ejecuta en un emulador o dispositivo físico (Android 8+)

---

## Commits planificados (Conventional Commits)

* `feat: add login screen layout`
* `feat: add form validation logic`
* `feat: add Room database`
* `feat: add mood selection screen`
* `feat: add haptic feedback`
* `feat: add history screen`
* `feat: add local notifications`

El flujo completo de commits está documentado en el tablero de Trello.

---

## Roadmap

* [ ] Interfaz de inicio de sesión
* [ ] Lógica de validación
* [ ] Registro de usuario
* [ ] Integración de base de datos
* [ ] Módulo de registro de ánimo
* [ ] Historial de estados de ánimo
* [ ] Notificaciones
* [ ] Pulido final de la UI

---

## Licencia

Este proyecto es para uso educativo y responde a un encargo académico específico.
Puedes reutilizar su estructura con fines de aprendizaje.

---

## ✨ Autor

Desarrollado por **Aframuz**, siguiendo un flujo de desarrollo estructurado por etapas.