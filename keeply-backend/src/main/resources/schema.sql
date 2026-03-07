-- 1. Crear la base de datos
CREATE DATABASE IF NOT EXISTS keeply_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE keeply_db;

-- 2. Tabla USUARIO
-- Según tu documento: almacena perfiles y acceso [cite: 47, 48, 49]
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(255) NOT NULL, -- Recuerda hashear esto en el backend
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    avatar_url VARCHAR(255) -- Extra: Útil para la foto de perfil del prototipo
);

-- 3. Tabla OBRA (Catálogo Maestro)
-- Plantilla flexible para juegos, libros, series, etc. [cite: 50, 51, 52]
-- 3. Tabla OBRA (Catálogo Maestro Mejorado)
-- Actualizada para soportar APIs externas y datos flexibles
CREATE TABLE Obra (
    id_obra INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,

    -- Cambiamos VARCHAR por ENUM para evitar errores de escritura en Java
    tipo_obra ENUM('VIDEOJUEGO', 'PELICULA', 'SERIE', 'ANIME', 'MANGA', 'LIBRO', 'COMIC') NOT NULL,

    autor_creador VARCHAR(150), -- Desarrollador, Autor o Director [cite: 51]
    fecha_lanzamiento DATE,
    url_imagen_principal VARCHAR(255),

    -- NUEVO: El ID que tiene la obra en la API externa (Vital para no duplicar)
    id_externo_api VARCHAR(100),

    -- NUEVO: Para guardar ISBN, Plataformas, Editorial, etc. sin crear más columnas
    detalles_json JSON,

    origen_datos ENUM('MANUAL', 'API') DEFAULT 'MANUAL'
);

-- 4. Tabla ITEM_USUARIO (La Biblioteca Personal)
-- Relación M:N entre Usuario y Obra con estado y valoración [cite: 53, 54, 55]
CREATE TABLE Item_Usuario (
    id_item_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_obra INT NOT NULL,
    estado ENUM('Pendiente', 'En Progreso', 'Completado', 'Abandonado') DEFAULT 'Pendiente',
    valoracion_personal DECIMAL(3, 1), -- Permite notas como 9.5
    fecha_adicion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_obra) REFERENCES Obra(id_obra) ON DELETE CASCADE
);

-- 5. Tabla CONTENIDO_USUARIO
-- Notas, fotos o audios asociados a un item específico [cite: 56, 57, 58]
CREATE TABLE Contenido_Usuario (
    id_contenido INT AUTO_INCREMENT PRIMARY KEY,
    id_item_usuario INT NOT NULL,
    tipo_contenido ENUM('Nota', 'Imagen', 'Video', 'Audio') NOT NULL,
    texto_nota TEXT, -- Se usa si es 'Nota'
    url_archivo VARCHAR(255), -- Se usa si es Imagen/Video/Audio
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_item_usuario) REFERENCES Item_Usuario(id_item_usuario) ON DELETE CASCADE
);

-- 6. Tabla SEGUIMIENTO (Social)
-- Quién sigue a quién [cite: 59, 60, 61]
CREATE TABLE Seguimiento (
    id_seguimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario_seguidor INT NOT NULL,
    id_usuario_seguido INT NOT NULL,
    fecha_seguimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario_seguidor) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_seguido) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    CHECK (id_usuario_seguidor <> id_usuario_seguido) -- Evita seguirse a uno mismo
);