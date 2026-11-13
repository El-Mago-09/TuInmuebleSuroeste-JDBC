# Tu Inmueble Suroeste – Módulo de inmuebles (JDBC)

Evidencia **GA7-220501096-AA2-EV01 – Codificación de módulos del software**
Aprendiz: **Ometh Martinez Potes** · Análisis y Desarrollo de Software (ADSO) · Ficha 3336217 · SENA 2026

Módulo de gestión de inmuebles de la plataforma *Tu Inmueble Suroeste*, codificado en Java
con conexión a MySQL mediante **JDBC** y las cuatro operaciones del **CRUD**
(insertar, consultar, actualizar y eliminar). Usa la base de datos diseñada en la
evidencia GA6-220501096-AA2-EV03 (tabla `inmuebles`).

## Tecnologías

| Elemento | Versión |
|---|---|
| Java (JDK) | 21 (compatible con 17+) |
| Base de datos | MySQL Server 8.0 + MySQL Workbench 8.0 |
| Conector JDBC | MySQL Connector/J 26.10.0 (`lib/`) |
| Versionamiento | Git + GitHub |

## Estructura

```
TuInmuebleSuroeste-JDBC/
├── lib/mysql-connector-j-26.10.0.jar     conector JDBC de MySQL
├── sql/
│   ├── 00_crear_usuario_app.sql          usuario de MySQL para la aplicación
│   ├── 01_Tu_Inmueble_Suroeste.sql       crea la base de datos y sus tablas
│   └── 02_datos_prueba.sql               usuario e inmuebles de prueba
├── src/com/tuinmueble/
│   ├── conexion/ConexionBD.java          conexión JDBC (lee db.properties)
│   ├── modelo/Inmueble.java              entidad de la tabla inmuebles
│   ├── dao/InmuebleDAO.java              interfaz con las operaciones CRUD
│   ├── dao/InmuebleDAOImpl.java          implementación JDBC del CRUD
│   └── principal/Main.java               menú de consola y prueba automática
├── db.properties.example                 plantilla de configuración
└── compilar_y_ejecutar.bat               compila y ejecuta en Windows
```

## Operaciones CRUD (`InmuebleDAOImpl`)

| Operación | Método | Sentencia SQL |
|---|---|---|
| Create | `insertar(Inmueble)` | `INSERT INTO inmuebles ...` |
| Read | `consultarPorId(int)`, `listarTodos()`, `listarPorMunicipio(String)` | `SELECT ... FROM inmuebles` |
| Update | `actualizar(Inmueble)` | `UPDATE inmuebles SET ... WHERE id_inmueble = ?` |
| Delete | `eliminar(int)` | `DELETE FROM inmuebles WHERE id_inmueble = ?` |

Todas usan `PreparedStatement` (evita inyección SQL) y *try-with-resources*
(cierra siempre conexión, sentencia y resultados).

## Estándar de codificación

| Elemento | Convención | Ejemplos |
|---|---|---|
| Paquetes | minúsculas, dominio invertido | `com.tuinmueble.dao`, `com.tuinmueble.modelo` |
| Clases e interfaces | PascalCase, sustantivos | `Inmueble`, `ConexionBD`, `InmuebleDAO`, `InmuebleDAOImpl` |
| Métodos | camelCase, verbo + complemento | `insertar()`, `consultarPorId()`, `listarPorMunicipio()` |
| Variables y atributos | camelCase, nombres descriptivos | `idInmueble`, `fechaPublicacion`, `inmuebleEditado` |
| Constantes | MAYÚSCULAS_CON_GUION_BAJO | `SQL_INSERTAR`, `ARCHIVO_CONFIGURACION` |
| Columnas de BD | snake_case | `id_inmueble`, `fecha_publicacion` |
| Comentarios | Javadoc en clases y métodos públicos | `/** Inserta un inmueble ... */` |

Otras reglas: un archivo por clase, idioma español en el dominio del negocio,
sangría de 4 espacios, sin credenciales escritas en el código.

## Configuración y ejecución

1. En MySQL Workbench, con la conexión de `root`, ejecutar `sql/00_crear_usuario_app.sql`
   (antes, cambiar `CAMBIE_ESTA_CONTRASENA`). La aplicación usa ese usuario, no `root`.
2. Ejecutar `sql/01_Tu_Inmueble_Suroeste.sql` (crea la base y las 6 tablas).
3. Ejecutar `sql/02_datos_prueba.sql` (1 usuario y 2 inmuebles de prueba).
4. Copiar `db.properties.example` como `db.properties` y escribir la contraseña del paso 1.
5. Compilar y ejecutar desde la carpeta del proyecto:

```bat
compilar_y_ejecutar.bat --demo     :: prueba automática de todo el CRUD
compilar_y_ejecutar.bat            :: menú interactivo
```

## Pruebas realizadas (13/11/2026, MySQL 8.0.46)

Salida real de `compilar_y_ejecutar.bat --demo`:

| # | Prueba | Resultado |
|---|---|---|
| 1 | Conexión JDBC y `SELECT` de los inmuebles existentes | 2 inmuebles listados |
| 2 | `INSERT` de "Finca cafetera en Andes" | Insertado con id 3 |
| 3 | `UPDATE` de precio (750.000.000) y estado (reservado) | `true`, cambio visible al consultar |
| 4 | `SELECT` por municipio (Andes) | Devuelve el inmueble 3 |
| 5 | `DELETE` del inmueble 3 | `true`, vuelve a haber 2 inmuebles |
| 6 | Consultar, actualizar y eliminar un id inexistente | "no existe", `false`, `false` |
| 7 | Insertar precio negativo | Rechazado por `chk_inmuebles_precio` |

En el menú interactivo se probó además: opción inválida, texto en lugar de número
y precio inválido (texto y negativo); el programa avisa y no se cierra.
Las conexiones se cierran con *try-with-resources* en cada operación.

## Repositorio

https://github.com/El-Mago-09/TuInmuebleSuroeste-JDBC
