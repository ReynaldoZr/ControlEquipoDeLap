# Control de equipos de laboratorio

Programa para guardar, mostrar, actualizar y eliminar equipos. Permite buscar por nombre y filtrar por tipo.

## Requisitos

Java 21, IntelliJ IDEA y PostgreSQL. El proyecto usa Maven, JavaFX e Hibernate.

## Base de datos

En pgAdmin:

1. Ejecutar `sql/01_crear_base.sql` conectado a `postgres`, si la base no existe.
2. Conectarse a `bd_laboratorio_orm`.
3. Ejecutar `sql/02_tablas_datos.sql` una sola vez para crear las tablas y los datos iniciales.

Las tablas se crean con SQL. Hibernate comprueba su estructura usando `validate`.

## Contraseña y ejecución

En IntelliJ, abrir **Run → Edit Configurations** y agregar la variable `DB_PASSWORD` con la contraseña de PostgreSQL en **Environment variables**.

La contraseña no se guarda en el código.

La conexión está en `src/main/resources/META-INF/persistence.xml`. Usa la base `bd_laboratorio_orm`, usuario `postgres`, puerto `5432` y unidad de persistencia `laboratorioPU`.

Ejecutar `Launcher` con esa configuración.

Para ver el diseño en Scene Builder, abrir `hello-view.fxml`.

## Funcionamiento

`Equipo` y `TipoEquipo` representan las tablas. Cada equipo pertenece a un tipo mediante `@ManyToOne`.

Los DAO guardan y consultan los datos. El controlador maneja los botones y las validaciones.

Las búsquedas usan JPQL con parámetros. Las operaciones se confirman con `commit()` y se deshacen con `rollback()` si fallan.

El ID se genera automáticamente y no se puede editar en la ventana.
