# 📇 Gestor de Contactos - Express & Sequelize

Este proyecto es una aplicación web para la gestión de contactos (CRUD) con sistema de usuarios e inicio de sesión. Está construida desde cero utilizando el patrón de diseño **MVC (Modelo-Vista-Controlador)**.

## 📘 ¿Qué es Express.js y cómo funciona? (Guía para principiantes)

Si nunca has trabajado con **Express.js**, piénsalo como el **director de tráfico de un sitio web**. Node.js por sí solo te permite ejecutar código JavaScript fuera del navegador, pero Express es el *framework* que te da las herramientas para construir una aplicación web de forma rápida y ordenada.

Express funciona principalmente basándose en tres conceptos fundamentales:

### 1\. Las Rutas (Endpoints)

Cuando un usuario escribe una dirección en su navegador (por ejemplo, `http://localhost:8080/contactos`), el navegador envía una **petición HTTP**. Express se encarga de escuchar esas peticiones y responder en función de la URL y del método utilizado:

-   **`**GET**`**: Se usa cuando el usuario quiere **solicitar o ver** información (ej. entrar a `/login` o ver la lista de `/contactos`).
-   **`**POST**`**: Se usa cuando el usuario **vía un formulario envía datos** para guardarlos o procesarlos (ej. enviar el formulario de registro o crear un contacto).

### 2\. Los Middlewares (Filtros intermedios)

Un *middleware* en Express es como un **control de seguridad o una aduana**. Es una función que se ejecuta **antes** de llegar al destino final de la ruta.

-   *Ejemplo en este proyecto:* Cuando intentas entrar a `/contactos/1/editar`, un middleware de autenticación comprueba si has iniciado sesión. Si estás logueado, te deja pasar; si no lo estás, te intercepta y te redirige a `/auth/login`.

### 3\. Las Respuestas y Plantillas

Una vez que la ruta procesa la petición y habla con la base de datos, Express genera una respuesta para el navegador. En este proyecto, Express usa un **motor de vistas (EJS)** para mezclar código HTML tradicional con los datos recuperados de PostgreSQL y enviarle al usuario la página final renderizada.

## 🚀 Tecnologías y para qué sirve cada una

-   **Node.js & Express:** Es el motor del servidor. Express se encarga de gestionar el tráfico de la web (las rutas como `/login` o `/contactos`), recibir los datos de los formularios y devolver las páginas al usuario.
-   **PostgreSQL:** La base de datos relacional donde se guardan de forma permanente los usuarios, contactos, provincias y países.
-   **Sequelize (ORM):** Es el puente de comunicación entre Node.js y PostgreSQL. Permite hacer consultas a la base de datos escribiendo código JavaScript en lugar de consultas SQL complejas.
-   **Passport.js & bcrypt:** Se encarga de la seguridad. Permite iniciar sesión, mantener la sesión abierta del usuario y encriptar las contraseñas antes de guardarlas en la base de datos.
-   **EJS & Bootstrap 5:** Crean las pantallas visuales (HTML) con botones, tablas y formularios adaptables para cualquier pantalla.

## 🧠 ¿Cómo funciona la aplicación por dentro? (Guía básica)

El proyecto está organizado en tres partes principales para mantener el código ordenado bajo el patrón **MVC**:

-   **Los Modelos (`**/models**`):** Definen la estructura de los datos. Le dicen a Sequelize cómo son las tablas en PostgreSQL (por ejemplo: la tabla `User` tiene usuario, correo, teléfono y contraseña; y la tabla `Contacto` tiene una provincia vinculada).
-   **Las Rutas y Controladores (`**/routes**`):** Son la "lógica" de la aplicación. Cuando entras a una página o envías un formulario:
-   1.  Verifican si estás autenticado mediante un middleware.
    2.  Le piden los datos a Sequelize.
    3.  Cifran contraseñas o guardan nuevos contactos.
    4.  Te redirigen a la página correspondiente.
-   **Las Vistas (`**/views**`):** Son las plantillas HTML (archivos `.ejs`). Muestran en pantalla los datos recuperados de la base de datos y los formularios estilizados con Bootstrap 5.

## 🔄 El flujo completo del usuario

1.  **Registro e Inicio de Sesión (`**/registro**` y `**/login**`):** El usuario crea una cuenta. La contraseña se cifra con `bcrypt` y se guarda en la tabla `users`. Al iniciar sesión, Passport valida los datos y mantiene la sesión activa.
2.  **Lista de Contactos (`**/contactos**`):** Una vez dentro, el usuario ve una tabla con sus contactos y la información de la provincia y país asociados.
3.  **Creación y Edición (`**/contactos/crear**` y `**/contactos/editar/:id**`):** Se puede añadir un nuevo contacto seleccionando su provincia desde un desplegable o modificar un contacto existente y guardarlo o borrarlo. Si un usuario no logueado intenta acceder o pulsa en **"Solo lectura"**, el middleware lo redirigirá automáticamente a `/login`.

## 🗄️ Configuración de la Base de Datos

Para ejecutar el proyecto, se crea la base de datos en PostgreSQL ejecutando en la consola:

sudo -u postgres psql  

CREATE DATABASE proyecto\_contactos;  
CREATE USER alumno WITH ENCRYPTED PASSWORD 'alumno';  
ALTER DATABASE proyecto\_contactos OWNER TO alumno;  
\\q  

*(Nota: Sequelize se encarga automáticamente de sincronizar y crear las tablas en la base de datos al arrancar el servidor).*

## ⚙️ Cómo ejecutar el proyecto

1.  **Instalar dependencias:**  
    
    npm install  
    
2.  **Iniciar el servidor en modo desarrollo:**  
   
    npm run dev  
    
3.  **Abrir en el navegador:**[http://localhost:8080/login](http://localhost:8080/login)

Desarrollado por **Iker Valls Jiménez** - 2º DAW

🔗[****Enlace IA****](https://share.gemini.google/ZhwBM4xT6zzk)
