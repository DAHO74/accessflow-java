-- ============================================================
--  AccessFlow - Base de datos (versión Swing + JDBC)
--  Ejecutar en XAMPP antes de iniciar la aplicación
-- ============================================================

DROP DATABASE IF EXISTS accessflow_db;
CREATE DATABASE accessflow_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE accessflow_db;

-- Usuarios del sistema: administradores y profesores
CREATE TABLE Usuario (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol           VARCHAR(20)  NOT NULL DEFAULT 'PROFESOR',
    creado_en     DATETIME     DEFAULT NOW()
);

-- Grupos escolares
CREATE TABLE Grupo (
    id     INT          AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    grado  VARCHAR(50),
    turno  VARCHAR(50)
);

-- Tutores (padres o madres de familia) — debe ir ANTES de Alumno
CREATE TABLE Tutor (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    email      VARCHAR(150),
    telefono   VARCHAR(20),
    parentesco VARCHAR(50)
);

-- Alumnos registrados en el sistema
CREATE TABLE Alumno (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(100) NOT NULL,
    rfid_uid VARCHAR(100) UNIQUE,
    grupo_id INT,
    tutor_id INT,
    FOREIGN KEY (grupo_id) REFERENCES Grupo(id)  ON DELETE SET NULL,
    FOREIGN KEY (tutor_id) REFERENCES Tutor(id)  ON DELETE SET NULL
);

-- Registro de asistencias (entrada y salida)
CREATE TABLE Asistencia (
    id            INT      AUTO_INCREMENT PRIMARY KEY,
    alumno_id     INT      NOT NULL,
    fecha_entrada DATETIME,
    fecha_salida  DATETIME,
    FOREIGN KEY (alumno_id) REFERENCES Alumno(id) ON DELETE CASCADE
);

-- Historial de mensajes enviados a tutores
CREATE TABLE Mensaje (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT          NOT NULL,
    tutor_id   INT,
    asunto     VARCHAR(255),
    cuerpo     TEXT,
    enviado_en DATETIME     DEFAULT NOW(),
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
    FOREIGN KEY (tutor_id)   REFERENCES Tutor(id) ON DELETE SET NULL
);

-- Configuración general del sistema (clave-valor)
CREATE TABLE Configuracion (
    clave VARCHAR(100) PRIMARY KEY,
    valor TEXT
);

-- Valores iniciales de configuración SMTP
INSERT INTO Configuracion (clave, valor) VALUES
('smtp_host',     'smtp.gmail.com'),
('smtp_puerto',   '587'),
('smtp_usuario',  ''),
('smtp_password', '');
