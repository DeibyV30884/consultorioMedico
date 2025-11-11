-- Sección de administración
DROP DATABASE IF EXISTS consultorio_medico;
DROP USER IF EXISTS usuario_consultorio;
DROP USER IF EXISTS usuario_reportes;

-- Creación del esquema
CREATE DATABASE consultorio_medico
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creación de usuarios
CREATE USER 'usuario_consultorio'@'%' IDENTIFIED BY 'Consultorio.123';
CREATE USER 'usuario_reportes'@'%' IDENTIFIED BY 'Reportes.123';

-- Asignación de permisos
GRANT SELECT, INSERT, UPDATE, DELETE ON consultorio_medico.* TO 'usuario_consultorio'@'%';
GRANT SELECT ON consultorio_cerdas.* TO 'usuario_reportes'@'%';
FLUSH PRIVILEGES;

USE consultorio_medico;

-- SECCION DE CREACIÓN DE TABLAS

-- Tabla de usuarios
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  password VARCHAR(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellidos VARCHAR(30) NOT NULL,
  correo VARCHAR(75) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  INDEX ndx_username (username))
  ENGINE = InnoDB;

-- Tabla de roles
CREATE TABLE rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(20) UNIQUE,
  descripcion VARCHAR(100),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rol))
  ENGINE = InnoDB;

-- Tabla de relación entre usuarios y roles
CREATE TABLE usuario_rol (
  id_usuario INT NOT NULL,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_rol),
  FOREIGN KEY fk_usuarioRol_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_usuarioRol_rol (id_rol) REFERENCES rol(id_rol))
  ENGINE = InnoDB;

-- Tabla de rutas
CREATE TABLE ruta (
  id_ruta INT AUTO_INCREMENT NOT NULL,
  ruta VARCHAR(255) NOT NULL,
  id_rol INT NULL,
  requiere_rol BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (id_rol IS NOT NULL OR requiere_rol = FALSE),
  PRIMARY KEY (id_ruta),
  FOREIGN KEY (id_rol) REFERENCES rol(id_rol))
  ENGINE = InnoDB;

-- Tabla de pacientes
CREATE TABLE paciente (
  id_paciente INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NULL,
  nombre VARCHAR(50) NOT NULL,
  apellido_1 VARCHAR(30) NOT NULL,
  apellido_2 VARCHAR(30),
  fecha_nacimiento DATE,
  correo_electronico VARCHAR(75),
  ocupacion VARCHAR(50),
  estado_civil VARCHAR(20),
  telefono VARCHAR(25),
  antecedentes_heredo_familiares TEXT,
  antecedentes_personales TEXT,
  antecedentes_quirurgicos TEXT,
  antecedentes_gineco_obstetricos TEXT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_paciente),
  FOREIGN KEY fk_paciente_usuario (id_usuario) REFERENCES usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla de médicos
CREATE TABLE medico (
  id_medico INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL UNIQUE,
  nombre VARCHAR(50) NOT NULL,
  apellido_1 VARCHAR(30) NOT NULL,
  apellido_2 VARCHAR(30),
  especialidad VARCHAR(50),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_medico),
  FOREIGN KEY fk_medico_usuario (id_usuario) REFERENCES usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla de administradores
CREATE TABLE administrador (
  id_administrador INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL UNIQUE,
  nombre VARCHAR(50) NOT NULL,
  apellido_1 VARCHAR(30) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_administrador),
  FOREIGN KEY fk_administrador_usuario (id_usuario) REFERENCES usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla de secretarias
CREATE TABLE secretaria (
  id_secretaria INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL UNIQUE,
  nombre VARCHAR(50) NOT NULL,
  apellido_1 VARCHAR(30) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_secretaria),
  FOREIGN KEY fk_secretaria_usuario (id_usuario) REFERENCES usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla de citas
CREATE TABLE cita (
  id_cita INT NOT NULL AUTO_INCREMENT,
  id_paciente INT NOT NULL,
  id_medico INT NOT NULL,
  fecha_hora DATETIME,
  estado ENUM('Pendiente', 'Confirmada', 'Cancelada', 'Completada') NOT NULL,
  tratamiento TEXT,
  tipo_consulta VARCHAR(50),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_cita),
  INDEX ndx_id_paciente (id_paciente),
  INDEX ndx_id_medico (id_medico),
  FOREIGN KEY fk_cita_paciente (id_paciente) REFERENCES paciente(id_paciente),
  FOREIGN KEY fk_cita_medico (id_medico) REFERENCES medico(id_medico))
  ENGINE = InnoDB;

-- Tabla de registro clínico
CREATE TABLE registro_clinico (
  id_registro INT NOT NULL AUTO_INCREMENT,
  id_paciente INT NOT NULL,
  id_medico INT NOT NULL,
  id_cita INT NULL,
  fecha DATE,
  motivo_consulta TEXT,
  diagnostico TEXT,
  plan_tratamiento TEXT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_registro),
  INDEX ndx_paciente (id_paciente),
  INDEX ndx_cita (id_cita),
  FOREIGN KEY fk_registro_paciente (id_paciente) REFERENCES paciente(id_paciente),
  FOREIGN KEY fk_registro_medico (id_medico) REFERENCES medico(id_medico),
  FOREIGN KEY fk_registro_cita (id_cita) REFERENCES cita(id_cita))
  ENGINE = InnoDB;

-- Tabla de medicamentos
CREATE TABLE medicamento (
  id_medicamento INT NOT NULL AUTO_INCREMENT,
  codigo VARCHAR(50) UNIQUE,
  nombre VARCHAR(100) NOT NULL,
  principio_activo VARCHAR(100),
  presentacion VARCHAR(100),
  stock_actual INT UNSIGNED,
  stock_minimo INT UNSIGNED,
  fecha_vencimiento DATE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_medicamento))
  ENGINE = InnoDB;

-- Tabla de prescripciones
CREATE TABLE prescripcion (
  id_prescripcion INT NOT NULL AUTO_INCREMENT,
  id_registro_clinico INT NOT NULL,
  id_medicamento INT NOT NULL,
  dosis VARCHAR(100),
  duracion_dias INT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_prescripcion),
  FOREIGN KEY fk_prescripcion_registro (id_registro_clinico) REFERENCES registro_clinico(id_registro),
  FOREIGN KEY fk_prescripcion_medicamento (id_medicamento) REFERENCES medicamento(id_medicamento))
  ENGINE = InnoDB;

-- Tabla de constantes de la aplicación
CREATE TABLE constante (
  id_constante INT AUTO_INCREMENT NOT NULL,
  atributo VARCHAR(25) NOT NULL,
  valor VARCHAR(150) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_constante),
  UNIQUE (atributo))
  ENGINE = InnoDB;

-- INSERCIÓN DE DATOS

-- Inserción de usuarios
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, activo) VALUES 
('admin', '1234', 'Carlos', 'Rodriguez', 'admin@consultorio.com', '8888-8888', TRUE),
('dr.perez', '1234', 'Juan', 'Pérez Ramírez', 'jperez@consultorio.com', '8777-7777', TRUE),
('secretaria', '1234', 'Ana', 'Gutierres López', 'anag2004@gmail.com', '0909-3490', TRUE),
('paciente1', '1111', 'Erick', 'Johnson', 'ejohnson@gmail.com', '8555-5555', TRUE);

-- Inserción de roles
INSERT INTO rol (nombre, descripcion) VALUES 
('MEDICO', 'Personal médico del consultorio'),
('CLIENTE', 'Pacientes del consultorio'),
('ADMINISTRADOR', 'Administradores del sistema'),
('SECRETARIA', 'Personal administrativo');

-- Asignación de roles a usuarios
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(1, 3), -- admin es ADMINISTRADOR
(2, 1), -- dr.perez es MEDICO
(3, 4), -- secretaria es SECRETARIA
(4, 2); -- paciente1 es CLIENTE

-- Inserción de administrador
INSERT INTO administrador (id_usuario, nombre, apellido_1) VALUES
(1, 'Carlos', 'Rodriguez');

-- Inserción de médicos
INSERT INTO medico (id_usuario, nombre, apellido_1, apellido_2, especialidad) VALUES
(2, 'Juan', 'Pérez', 'Ramírez', 'Medicina General');

-- Inserción de secretaria
INSERT INTO secretaria (id_usuario, nombre, apellido_1) VALUES
(3, 'Ana', 'Gutierres');

-- Inserción de pacientes
INSERT INTO paciente (id_usuario, nombre, apellido_1, apellido_2, fecha_nacimiento, correo_electronico, telefono, ocupacion, estado_civil) VALUES
(4, 'Erick', 'Johnson', NULL, '1990-05-15', 'ejohnson@gmail.com', '8555-5555', 'Ingeniero', 'Soltero'),
(NULL, 'Karen', 'Fernandez', 'Mora', '1985-08-22', 'kfernandez@gmail.com', '8666-6666', 'Docente', 'Casado'),
(NULL, 'Carlos', 'Rodriguez', 'Salas', '1978-12-10', 'crodriguez@gmail.com', '8444-4444', 'Contador', 'Casado'),
(NULL, 'Ana', 'Venegas', 'Castro', '1992-03-18', 'avenegas@gmail.com', '8333-3333', 'Diseñadora', 'Soltero'),
(NULL, 'Juan', 'Ramirez', 'Solano', '1988-07-25', 'jramirez@gmail.com', '8222-2222', 'Empresario', 'Divorciado');

-- Inserción de citas
INSERT INTO cita (id_paciente, id_medico, fecha_hora, estado, tipo_consulta) VALUES
(1, 1, '2025-11-10 08:00:00', 'Cancelada', 'Consulta Gral.'),
(2, 1, '2025-11-10 11:00:00', 'Completada', 'Consulta Gral.'),
(3, 1, '2025-11-10 08:00:00', 'Cancelada', 'Consulta Gral.'),
(4, 1, '2025-11-10 11:00:00', 'Completada', 'Consulta Gral.'),
(5, 1, '2025-11-10 13:00:00', 'Pendiente', 'Consulta Gral.'),
(1, 1, '2025-11-15 09:00:00', 'Pendiente', 'Control'),
(2, 1, '2025-11-15 10:00:00', 'Pendiente', 'Consulta Gral.'),
(3, 1, '2025-11-16 14:00:00', 'Confirmada', 'Control');

-- Inserción de medicamentos
INSERT INTO medicamento (codigo, nombre, principio_activo, presentacion, stock_actual, stock_minimo) VALUES
('MED-001', 'Acetaminofen 500mg', 'Acetaminofen', 'Tabletas', 100, 20),
('MED-002', 'Ibuprofeno 400mg', 'Ibuprofeno', 'Tabletas', 80, 15),
('MED-003', 'Amoxicilina 500mg', 'Amoxicilina', 'Cápsulas', 60, 10),
('MED-004', 'Omeprazol 20mg', 'Omeprazol', 'Cápsulas', 50, 10);

-- Inserción de rutas con roles específicos (ADMINISTRADOR)
INSERT INTO ruta (ruta, id_rol) VALUES 
('/admin/**', 3),
('/usuario/**', 3),
('/rol/**', 3),
('/ruta/**', 3),
('/constante/**', 3);

-- Rutas para MEDICO
INSERT INTO ruta (ruta, id_rol) VALUES 
('/medico/**', 1),
('/paciente/ver/**', 1),
('/cita/ver/**', 1),
('/registro-clinico/**', 1),
('/prescripcion/**', 1);

-- Rutas para SECRETARIA
INSERT INTO ruta (ruta, id_rol) VALUES 
('/secretaria/**', 4),
('/paciente/**', 4),
('/cita/**', 4);

-- Rutas para PACIENTE
INSERT INTO ruta (ruta, id_rol) VALUES 
('/paciente/inicio', 2),
('/paciente/tratamientos', 2),
('/paciente/perfil', 2),
('/cita/mis-citas', 2);

-- Rutas públicas (sin rol requerido)
INSERT INTO ruta (ruta, requiere_rol) VALUES 
('/', FALSE),
('/index', FALSE),
('/login', FALSE),
('/registro', FALSE),
('/error/**', FALSE),
('/webjars/**', FALSE);

-- Constantes del sistema
INSERT INTO constante (atributo, valor) VALUES 
('nombre_consultorio', 'Consultorio Doctor Cerdas'),
('telefono_contacto', '2315-2832');