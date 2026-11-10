# 📷 Blog de Fotografía - Java & Spring Boot

Este proyecto es un **blog de fotografía** completo: portada con carrusel y galería por categorías, blog con entradas paginadas y buscador, vista detallada de cada entrada con comentarios y "me gusta", formulario de contacto con validación, gestión de usuarios (registro/login) y una **zona de administración** para categorías e imágenes. Está construido con el patrón **MVC (Modelo-Vista-Controlador)** siguiendo la teoría del curso (creación de un blog).

## 📘 ¿Qué es Spring Boot y cómo funciona? (Guía para principiantes)

Si nunca has trabajado con **Spring Boot**, piénsalo como el **esqueleto de una aplicación web** listo para usar. Tú escribes las clases y Spring se encarga de montar el servidor (Tomcat embebido), inyectar las dependencias entre ellas y conectar todo con la base de datos, sin que tengas que configurar casi nada.

Spring Boot funciona principalmente con cuatro conceptos fundamentales:

### 1. Los Controladores y sus rutas (Endpoints)

Cuando un usuario escribe una dirección en el navegador (por ejemplo, `http://localhost:8080/blog`), el navegador envía una **petición HTTP**. Los controladores (clases anotadas con `@Controller`) se encargan de escuchar esas peticiones y responder en función de la URL y del método utilizado:

- **`GET`**: Se usa cuando el usuario quiere **solicitar o ver** información (ej. entrar a `/blog` o ver una entrada en `/single_post/doloremque-illum`).
- **`POST`**: Se usa cuando el usuario **vía un formulario envía datos** para guardarlos o procesarlos (ej. enviar el formulario de contacto o de registro, publicar un comentario o dar un "me gusta").

### 2. La seguridad (El "control de aduana" de Spring Security)

Spring Security funciona como los *middlewares* de otros frameworks: es un **filtro que se ejecuta antes de llegar al controlador**. Cada petición pasa primero por él.

- *Ejemplo en este proyecto:* cuando intentas entrar a `/blog/new` sin haber iniciado sesión, el filtro te intercepta y te redirige a `/login`. Y si un usuario normal intenta entrar a `/admin/**`, como exige el rol `ADMIN`, acaba en la página `/acceso-denegado`.

### 3. Las plantillas (Thymeleaf)

Cuando el controlador ya ha hablado con la base de datos, devuelve el nombre de una plantilla. **Thymeleaf** mezcla el HTML con los datos (por ejemplo, `th:each` para recorrer los posts) y le envía al navegador la página final renderizada. Es el equivalente a Twig en PHP o EJS en Node.

### 4. Los repositorios (Spring Data JPA)

Los repositorios son interfaces que **heredan de `JpaRepository`** y por arte de magia ya saben hacer CRUD. Además, Spring Data genera las consultas a partir del nombre del método: si llamas a un método `findBySlug`, él solo traduce eso a `SELECT ... WHERE slug = ?`.

## 🚀 Tecnologías y para qué sirve cada una

- **Java 17 & Spring Boot 4.1:** Es el motor del servidor. Spring MVC gestiona el tráfico de la web (las rutas como `/blog` o `/single_post/{slug}`), recibe los datos de los formularios y devuelve las páginas al usuario.
- **MySQL 8:** La base de datos relacional donde se guardan de forma permanente los usuarios, posts, comentarios, categorías, imágenes y mensajes de contacto (base `blog_db`).
- **Spring Data JPA / Hibernate:** Es el puente entre Java y MySQL. Permite hacer consultas escribiendo código Java (`findBySlug`, `findTop5ByOrderByPublishedAtDesc`) en lugar de SQL, y sincroniza las entidades con las tablas (`ddl-auto=update`).
- **Spring Security & BCrypt:** Se encarga de la seguridad. Gestiona el registro, el login, las sesiones, bloquea la zona admin y cifra las contraseñas antes de guardarlas.
- **Thymeleaf & Bootstrap 3:** Crean las pantallas visuales (HTML) con el carrusel, la galería, la paginación y los formularios adaptables a cualquier pantalla.

## 🧠 ¿Cómo funciona la aplicación por dentro? (Guía básica)

El proyecto está organizado en paquetes para mantener el código ordenado bajo el patrón **MVC**:

- **Los Modelos (`model/`):** Las **entidades JPA** que definen la estructura de los datos. Le dicen a Hibernate cómo son las tablas en MySQL. Hay 6 entidades: `User`, `Category`, `ImageItem`, `Post`, `Comment` y `ContactMessage`.
- **Los Repositorios (`repository/`):** Uno por entidad. Hacen las consultas a la base de datos (por nombre de método o con `@Query` cuando es más compleja, como el buscador o el listado con paginación).
- **Los Controladores (`controller/`):** Son la "lógica" de la aplicación. `PageController` (páginas públicas y blog), `RegisterController` (registro de usuarios) y `AdminController` (zona admin).
- **Las Vistas (`templates/`):** Las plantillas HTML con Thymeleaf que muestran los datos recuperados de la base de datos.
- **`SecurityConfig`:** Define qué rutas son públicas, cuáles requieren login y cuáles solo ADMIN.
- **`DataInitializer`:** La primera vez que arrancas la app, crea el usuario admin y datos de ejemplo (posts, categorías e imágenes).

## 🧩 Entidades y relaciones

```
users 1 ──── N posts 1 ── N comments
                              (cada comentario pertenece a un post)
categories 1 ──── N images
```

| Entidad | Tabla | Relación |
|---|---|---|
| `User` | `users` | 1 usuario escribe N posts (`postUser`, FK `user_id`) |
| `Post` | `posts` | 1 post tiene N comentarios (FK `post_id`); slug único SEO-friendly |
| `Comment` | `comments` | Muchos a 1 con `Post` (obligatorio) |
| `Category` | `categories` | 1 categoría tiene N imágenes |
| `ImageItem` | `images` | Muchos a 1 con `Category` (FK `category_id`); campo `section` (carrusel/portfolio) |
| `ContactMessage` | `contact_messages` | Independiente, guarda los mensajes del formulario de contacto |

### Consultas más importantes (repositorios)

- `PostRepository.findBySlug(slug)` → busca la entrada para `/single_post/{slug}`
- `PostRepository.findTop5ByOrderByPublishedAtDesc()` → los 5 posts recientes del sidebar
- `PostRepository.findAllByOrderByPublishedAtDesc(Pageable)` → listado del blog con **paginación** (5 por página)
- `PostRepository.findBuscadosPaginados(searchTerm, pageable)` → **buscador** por título y contenido con `LIKE`, paginado
- `CommentRepository.findByPostOrderByPublishedAtDesc(post)` → comentarios de una entrada
- `CategoryRepository.findAllWithImages()` → categorías con sus imágenes (`JOIN FETCH`) para la portada

## 🔄 El flujo completo del usuario

1. **Portada (`/`):** Carrusel de imágenes y galería dividida en pestañas por categoría (los enlaces del sidebar del blog saltan a la pestaña concreta).
2. **Registro e inicio de sesión (`/register` y `/login`):** El usuario crea una cuenta. La contraseña se cifra con **BCrypt** y se guarda en la tabla `users`. Spring Security valida el login y mantiene la sesión.
3. **Blog (`/blog`):** Listado de entradas paginado (5 por página) con las más recientes primero, buscador (`/blog/buscar?searchTerm=...`) y sidebar con los 5 posts recientes.
4. **Entrada (`/single_post/{slug}`):** Vista completa del post. Se puede **comentar** (sube el contador) y dar a **LIKE** vía AJAX (sube +1 sin recargar la página).
5. **Crear entrada (`/blog/new`):** Solo con sesión iniciada. Sube una imagen al servidor, genera el slug desde el título y redirige al post recién creado.
6. **Contacto (`/contact`):** Formulario con validación (`@NotBlank`, `@Email`, tamaños máximos) que guarda el mensaje en `contact_messages` y redirige a `/contact/thankyou`.
7. **Zona admin (`/admin/**`):** Solo con rol `ADMIN`. Gestión de categorías e imágenes: crear, subir archivos y borrar (las imágenes de una categoría borrada se quedan huérfanas sin categoría).

## 🗄️ Configuración de la Base de Datos (MySQL + DBeaver)

La aplicación se conecta a MySQL en `localhost:3306`, base de datos **`blog_db`**, usuario `root` y contraseña `sa` (ver `application.properties`).

### Crear la base de datos con DBeaver

1. Abre DBeaver → **Base de datos → Nueva conexión → MySQL**:
   - Server Host: `localhost` · Port: `3306` · Username: `root` · Password: `sa`
2. Al conectar por primera vez pide descargar el driver → **Download** → **Test Connection**.
3. Ejecuta el script del proyecto: **Archivo → Abrir script** (`Ctrl+Shift+O`) → selecciona **`sql/blog_db.sql`** → botón **▶ Ejecutar script SQL** (`Alt+X`).
   - Crea la base `blog_db`, las 6 tablas (`users`, `categories`, `images`, `posts`, `comments`, `contact_messages`) con sus claves foráneas e índices únicos, y mete datos semilla (3 categorías y 13 imágenes para la portada).
4. Refresca el navegador (F5) y comprueba las tablas dentro de `blog_db`.

### Copia de seguridad

- **`sql/backup_blog_db.sql`**: volcado completo (esquema + datos) por si hay que restaurar todo:
  ```bash
  mysql -h 127.0.0.1 -P 3306 -u root -psa < sql/backup_blog_db.sql
  ```
- Hibernate está en modo `ddl-auto=update`, así que al arrancar la app **no duplica nada**: si el esquema ya existe, solo añade lo que falte.
- El usuario admin y las entradas de ejemplo los crea `DataInitializer` automáticamente en el primer arranque (no van en el SQL porque la contraseña va cifrada en BCrypt).

## ⚙️ Cómo ejecutar el proyecto

1.  **Arrancar el servidor** (necesita MySQL encendido y `blog_db` creada):

    ```bash
    ./mvnw spring-boot:run
    ```

2.  **Abrir en el navegador:** http://localhost:8080/

3.  **Usuario de prueba:** `admin` / `1234` (rol ADMIN, creado automáticamente la primera vez que arranca).

Desarrollado por **Iker Valls Jiménez** - 2º DAW

🔗[**Enlace IA**](https://share.gemini.google/ZhwBM4xT6zzk)
