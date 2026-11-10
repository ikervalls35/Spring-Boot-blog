-- ============================================================
-- blog_db - Esquema generado a partir de las entidades JPA
-- del proyecto Spring-Boot-blog (paquete springbootblog.model)
--
-- Servidor : localhost:3306  (MySQL 8)
-- Usuario  : root / sa
-- BBDD     : blog_db
--
-- Tablas y columnas según cada entidad:
--   User          -> users          (id, username, name, email, password, role)
--   Category      -> categories     (id, name, slug)
--   ImageItem     -> images         (id, title, image_url, section, category_id)
--   Post          -> posts          (id, title, content, image, slug, published_at,
--                                    num_likes, num_comments, user_id)
--   Comment       -> comments       (id, name, email, text, published_at, post_id)
--   ContactMessage-> contact_messages (id, name, email, subject, message, created_at)
--
-- NOTA: si arrancas la app con spring.jpa.hibernate.ddl-auto=update
-- y este esquema ya existe, Hibernate no lo duplicará (es idéntico).
-- El usuario admin y los posts de ejemplo los crea DataInitializer
-- la primera vez que se ejecuta la aplicación.
-- ============================================================

CREATE DATABASE IF NOT EXISTS blog_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE blog_db;

-- ------------------------------------------------------------
-- Tabla: users  (entidad User)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    username  VARCHAR(255) NOT NULL,
    name      VARCHAR(100) NULL,
    email     VARCHAR(150) NULL,
    password  VARCHAR(255) NOT NULL,
    role      VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Tabla: categories  (entidad Category)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_categories_name (name),
    UNIQUE KEY uk_categories_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Tabla: images  (entidad ImageItem)
-- category_id -> categories.id (ManyToOne con Category)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS images (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(255) NULL,
    image_url   VARCHAR(255) NULL,
    section     VARCHAR(255) NULL,
    category_id BIGINT       NULL,
    PRIMARY KEY (id),
    KEY idx_images_category (category_id),
    CONSTRAINT fk_images_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Tabla: posts  (entidad Post)
-- user_id -> users.id (ManyToOne con User, @JoinColumn user_id)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS posts (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    title        VARCHAR(255)  NOT NULL,
    content      TEXT          NOT NULL,
    image        VARCHAR(255)  NOT NULL,
    slug         VARCHAR(255)  NOT NULL,
    published_at DATETIME(6)   NOT NULL,
    num_likes    INT           NOT NULL DEFAULT 0,
    num_comments INT           NOT NULL DEFAULT 0,
    user_id      BIGINT        NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_posts_slug (slug),
    KEY idx_posts_user (user_id),
    CONSTRAINT fk_posts_user
        FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Tabla: comments  (entidad Comment)
-- post_id -> posts.id (ManyToOne con Post, obligatorio)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS comments (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    text         TEXT         NOT NULL,
    published_at DATETIME(6)  NOT NULL,
    post_id      BIGINT       NOT NULL,
    PRIMARY KEY (id),
    KEY idx_comments_post (post_id),
    CONSTRAINT fk_comments_post
        FOREIGN KEY (post_id) REFERENCES posts (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Tabla: contact_messages  (entidad ContactMessage)
-- Sin relaciones con otras tablas
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS contact_messages (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    name       VARCHAR(150)  NULL,
    email      VARCHAR(150)  NULL,
    subject    VARCHAR(200)  NULL,
    message    VARCHAR(1000) NULL,
    created_at DATETIME(6)   NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- DATOS DE EJEMPLO (opcional)
-- Reproduce las categorías e imágenes que crea DataInitializer.
-- Los posts, comentarios y el usuario admin se crean
-- automáticamente al arrancar la aplicación.
-- ============================================================

INSERT INTO categories (name, slug) VALUES
    ('Category I',   'category-i'),
    ('Category II',  'category-ii'),
    ('Category III', 'category-iii');

INSERT INTO images (title, image_url, section, category_id) VALUES
    ('Portfolio 1',  '/images/index/portfolio/1.jpg',  'portfolio', 1),
    ('Portfolio 2',  '/images/index/portfolio/2.jpg',  'portfolio', 1),
    ('Portfolio 3',  '/images/index/portfolio/3.jpg',  'portfolio', 1),
    ('Portfolio 4',  '/images/index/portfolio/4.jpg',  'portfolio', 1),
    ('Portfolio 5',  '/images/index/portfolio/5.jpg',  'portfolio', 2),
    ('Portfolio 6',  '/images/index/portfolio/6.jpg',  'portfolio', 2),
    ('Portfolio 7',  '/images/index/portfolio/7.jpg',  'portfolio', 2),
    ('Portfolio 8',  '/images/index/portfolio/8.jpg',  'portfolio', 2),
    ('Portfolio 9',  '/images/index/portfolio/9.jpg',  'portfolio', 3),
    ('Portfolio 10', '/images/index/portfolio/10.jpg', 'portfolio', 3),
    ('Portfolio 11', '/images/index/portfolio/11.jpg', 'portfolio', 3),
    ('Portfolio 12', '/images/index/portfolio/12.jpg', 'portfolio', 3),
    ('Portada',      '/images/index/woman.jpg',        'carousel',  NULL);
