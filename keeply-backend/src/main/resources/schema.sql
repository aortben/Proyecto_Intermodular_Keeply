CREATE DATABASE IF NOT EXISTS keeply_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE keeply_db;
-- Tabla USUARIO

CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    avatar_url VARCHAR(255)
);

-- Tabla OBRA
CREATE TABLE Obra (
    id_obra INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,

    tipo_obra ENUM('VIDEOJUEGO', 'PELICULA', 'SERIE', 'ANIME', 'MANGA', 'LIBRO', 'COMIC') NOT NULL,

    autor_creador VARCHAR(150),
    fecha_lanzamiento DATE,
    url_imagen_principal VARCHAR(255),
    id_externo_api VARCHAR(100),
    detalles_json JSON,

    origen_datos ENUM('MANUAL', 'API') DEFAULT 'MANUAL'
);

-- Tabla ITEM_USUARIO (La Biblioteca Personal)
CREATE TABLE Item_Usuario (
    id_item_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_obra INT NOT NULL,
    estado ENUM('Pendiente', 'En Progreso', 'Completado', 'Abandonado') DEFAULT 'Pendiente',
    valoracion_personal DECIMAL(3, 1),
    fecha_adicion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_obra) REFERENCES Obra(id_obra) ON DELETE CASCADE
);

-- Tabla CONTENIDO_USUARIOCREATE TABLE Contenido_Usuario (
    id_contenido INT AUTO_INCREMENT PRIMARY KEY,
    id_item_usuario INT NOT NULL,
    tipo_contenido ENUM('Nota', 'Imagen', 'Video', 'Audio') NOT NULL,
    texto_nota TEXT, 
    url_archivo VARCHAR(255), 
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_item_usuario) REFERENCES Item_Usuario(id_item_usuario) ON DELETE CASCADE
);

-- Tabla SEGUIMIENTO (Social)
CREATE TABLE Seguimiento (
    id_seguimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario_seguidor INT NOT NULL,
    id_usuario_seguido INT NOT NULL,
    fecha_seguimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario_seguidor) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_seguido) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    CHECK (id_usuario_seguidor <> id_usuario_seguido) -- Evita seguirse a uno mismo
);