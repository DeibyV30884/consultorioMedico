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
GRANT SELECT ON consultorio_medico.* TO 'usuario_reportes'@'%';
FLUSH PRIVILEGES;

USE consultorio_medico;

-- SECCION DE CREACIÓN DE TABLAS

-- Tabla de usuarios
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  password VARCHAR(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellido_1 VARCHAR(30) NOT NULL,
  apellido_2 VARCHAR(30),
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
  fecha_nacimiento DATE NULL,
  correo_electronico VARCHAR(75) NULL,
  ocupacion VARCHAR(50) NULL,
  genero VARCHAR(20)NULL,
  estado_civil VARCHAR(20) NULL,
  telefono VARCHAR(25) NULL,
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
  apellido_2 VARCHAR(30) NOT NULL,
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
  apellido_2 VARCHAR(30) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_secretaria),
  FOREIGN KEY fk_secretaria_usuario (id_usuario) REFERENCES usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla de citas motivo
CREATE TABLE motivo_cita (
  id_motivo_cita INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255),
  activo BOOLEAN DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_motivo_cita))
  ENGINE = InnoDB;

-- Tabla de citas
CREATE TABLE cita (
  id_cita INT NOT NULL AUTO_INCREMENT,
  id_paciente INT NOT NULL,
  id_medico INT NOT NULL,
  id_motivo_cita INT NULL,
  fecha DATE NOT NULL,
  hora TIME NOT NULL,
  estado VARCHAR(20) NOT NULL,
  tratamiento TEXT,
  observaciones TEXT,
  tipo_consulta VARCHAR(50),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_cita),
  INDEX ndx_id_paciente (id_paciente),
  INDEX ndx_id_medico (id_medico),
  INDEX ndx_fecha_hora (id_medico, fecha, hora),
  FOREIGN KEY fk_cita_paciente (id_paciente) REFERENCES paciente(id_paciente),
  FOREIGN KEY fk_cita_medico (id_medico) REFERENCES medico(id_medico),
  FOREIGN KEY fk_cita_motivo (id_motivo_cita) REFERENCES motivo_cita(id_motivo_cita),
  UNIQUE KEY uk_medico_fecha_hora (id_medico, fecha, hora))
  ENGINE = InnoDB;

-- Tabla de prescripciones (ahora referencia directamente a cita)
CREATE TABLE prescripcion (
  id_prescripcion INT NOT NULL AUTO_INCREMENT,
  id_cita INT NOT NULL,
  medicamento TEXT,
  dosis VARCHAR(100),
  duracion_dias INT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_prescripcion),
  FOREIGN KEY fk_prescripcion_cita (id_cita) REFERENCES cita(id_cita))
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
INSERT INTO usuario (username, password, nombre, apellido_1, apellido_2, correo, telefono, activo) VALUES 
('admin', '$2a$10$fdww1uEuuEynom5qBwEV.OjKl.vjr7jC3/noBaOxEjqL7zFH70ule', 'Carlos', 'Rodriguez','Ramírez', 'admin@consultorio.com', '8888-8888', TRUE),-- 1234
('dr.perez', '$2a$10$fdww1uEuuEynom5qBwEV.OjKl.vjr7jC3/noBaOxEjqL7zFH70ule', 'Juan', 'Pérez','Ramírez','jperez@consultorio.com', '8777-7777', TRUE),-- 1234
('secretaria', '$2a$10$fdww1uEuuEynom5qBwEV.OjKl.vjr7jC3/noBaOxEjqL7zFH70ule', 'Ana', 'Gutierres','López', 'anag2004@gmail.com', '0909-3490', TRUE),-- 1234
('paciente1', '$2a$10$r1JSQWLy0ezvORD.zrlyu.pqbmYJha0O7yMujUbXEuMOXTvx.h4cq', 'Erick', 'Johnson','Johnson', 'ejohnson@gmail.com', '8555-5555', TRUE),-- 1111
('dr.martinez', '$2a$10$fdww1uEuuEynom5qBwEV.OjKl.vjr7jC3/noBaOxEjqL7zFH70ule', 'Laura', 'Martinez','Vargas', 'lmartinez@consultorio.com', '8888-1234', TRUE),-- 1234
('paciente2', '$2a$10$r1JSQWLy0ezvORD.zrlyu.pqbmYJha0O7yMujUbXEuMOXTvx.h4cq', 'Maria', 'Lopez','Gonzalez', 'mlopez@gmail.com', '8666-7777', TRUE),-- 1111
('paciente3', '$2a$10$r1JSQWLy0ezvORD.zrlyu.pqbmYJha0O7yMujUbXEuMOXTvx.h4cq', 'Roberto', 'Sanchez','Mora', 'rsanchez@gmail.com', '8444-3333', TRUE);-- 1111

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
(4, 2), -- paciente1 es CLIENTE
(5, 1), -- dr.martinez es MEDICO
(6, 2), -- paciente2 es CLIENTE
(7, 2); -- paciente3 es CLIENTE

-- Inserción de administrador
INSERT INTO administrador (id_usuario, nombre, apellido_1, apellido_2) VALUES
(1, 'Carlos', 'Rodriguez', 'Rodriguez');

-- Inserción de médicos
INSERT INTO medico (id_usuario, nombre, apellido_1, apellido_2, especialidad) VALUES
(2, 'Juan', 'Pérez', 'Ramírez', 'Medicina General'),
(5, 'Laura', 'Martinez', 'Vargas', 'Cardiología');

-- Inserción de secretaria
INSERT INTO secretaria (id_usuario, nombre, apellido_1, apellido_2) VALUES
(3, 'Ana', 'Gutierres', 'Pérez');

-- Inserción de pacientes
INSERT INTO paciente (id_usuario, nombre, apellido_1, apellido_2, fecha_nacimiento, genero, correo_electronico, telefono, ocupacion, estado_civil, antecedentes_personales, antecedentes_heredo_familiares) VALUES
(4, 'Erick', 'Johnson', 'Johnson', '1990-05-15', 'Masculino', 'ejohnson@gmail.com', '8555-5555', 'Ingeniero', 'Soltero', 'Hipertensión controlada', 'Padre con diabetes tipo 2'),
(6, 'Maria', 'Lopez', 'Gonzalez', '1995-03-20', 'Femenino', 'mlopez@gmail.com', '8666-7777', 'Abogada', 'Casada', 'Alergia a la penicilina', 'Madre con hipertensión'),
(7, 'Roberto', 'Sanchez', 'Mora', '1982-11-08', 'Masculino', 'rsanchez@gmail.com', '8444-3333', 'Arquitecto', 'Divorciado', 'Fumador ocasional', 'Abuelo con problemas cardíacos'),
(NULL, 'Karen', 'Fernandez', 'Mora', '1985-08-22', 'Femenino', 'kfernandez@gmail.com', '8666-6666', 'Docente', 'Casada', 'Asma leve', 'Sin antecedentes relevantes'),
(NULL, 'Carlos', 'Rodriguez', 'Salas', '1978-12-10', 'Masculino', 'crodriguez@gmail.com', '8444-4444', 'Contador', 'Casado', 'Gastritis crónica', 'Padre con cáncer de próstata'),
(NULL, 'Ana', 'Venegas', 'Castro', '1992-03-18', 'Femenino', 'avenegas@gmail.com', '8333-3333', 'Diseñadora', 'Soltera', NULL, 'Madre con migraña crónica'),
(NULL, 'Juan', 'Ramirez', 'Solano', '1988-07-25', 'Masculino', 'jramirez@gmail.com', '8222-2222', 'Empresario', 'Divorciado', 'Colesterol alto', 'Hermano con obesidad'),
(NULL, 'Patricia', 'Mora', 'Jimenez', '1991-06-12', 'Femenino', 'pmora@gmail.com', '8111-1111', 'Enfermera', 'Soltera', 'Tiroides bajo control', 'Abuela con diabetes'),
(NULL, 'Diego', 'Castro', 'Vargas', '1986-09-30', 'Masculino', 'dcastro@gmail.com', '8999-9999', 'Chef', 'Casado', 'Reflujo gastroesofágico', 'Padre con hipertensión'),
(NULL, 'Sofia', 'Gomez', 'Rojas', '1993-01-15', 'Femenino', 'sgomez@gmail.com', '8777-8888', 'Psicóloga', 'Soltera', 'Ansiedad', 'Madre con depresión');

-- Inserción de motivos de cita
INSERT INTO motivo_cita (nombre, descripcion) VALUES
('Consulta General', 'Consulta médica general'),
('Control', 'Control de seguimiento'),
('Urgencia', 'Atención de urgencia'),
('Chequeo', 'Chequeo médico preventivo'),
('Resultados', 'Revisión de resultados de exámenes'),
('Vacunación', 'Aplicación de vacunas'),
('Certificado Médico', 'Emisión de certificado médico'),
('Control de Presión', 'Monitoreo de presión arterial'),
('Control de Diabetes', 'Seguimiento de diabetes');

-- Inserción de citas (variedad de estados y fechas)
INSERT INTO cita (id_paciente, id_medico, id_motivo_cita, fecha, hora, estado, tipo_consulta, tratamiento, observaciones) VALUES
-- Citas del Dr. Pérez
(1, 1, 1, '2025-12-15', '08:00:00', 'Pendiente', 'Consulta General', NULL, NULL),
(2, 1, 4, '2025-12-15', '09:00:00', 'Confirmada', 'Chequeo', NULL, 'Paciente solicita chequeo anual'),
(3, 1, 2, '2025-12-15', '10:00:00', 'Pendiente', 'Control', NULL, NULL),
(4, 1, 8, '2025-12-16', '08:00:00', 'Confirmada', 'Control de Presión', 'Continuar con medicamento actual', 'Presión controlada'),
(5, 1, 1, '2025-12-16', '09:30:00', 'Pendiente', 'Consulta General', NULL, NULL),
(6, 1, 7, '2025-12-17', '11:00:00', 'Confirmada', 'Certificado Médico', NULL, 'Para trabajo'),
(7, 1, 5, '2025-12-18', '08:30:00', 'Pendiente', 'Resultados', NULL, 'Revisar exámenes de laboratorio'),
(1, 1, 2, '2025-12-10', '09:00:00', 'Completada', 'Control', 'Losartán 50mg 1 vez al día', 'Presión arterial estable'),
(2, 1, 1, '2025-12-08', '10:00:00', 'Completada', 'Consulta General', 'Reposo y abundantes líquidos', 'Gripe común'),
(8, 1, 9, '2025-12-19', '14:00:00', 'Confirmada', 'Control de Diabetes', NULL, 'Control trimestral'),

-- Citas de la Dra. Martinez
(3, 2, 1, '2025-12-15', '08:30:00', 'Confirmada', 'Consulta General', NULL, NULL),
(9, 2, 8, '2025-12-16', '10:00:00', 'Pendiente', 'Control de Presión', NULL, 'Primera consulta cardiológica'),
(10, 2, 4, '2025-12-17', '09:00:00', 'Confirmada', 'Chequeo', NULL, 'Chequeo cardiológico preventivo'),
(4, 2, 2, '2025-12-18', '11:00:00', 'Pendiente', 'Control', NULL, NULL),
(5, 2, 5, '2025-12-19', '08:00:00', 'Confirmada', 'Resultados', NULL, 'Electrocardiograma de seguimiento'),
(3, 2, 8, '2025-12-09', '09:00:00', 'Completada', 'Control de Presión', 'Enalapril 10mg 1 vez al día', 'Hipertensión leve detectada'),
(9, 2, 1, '2025-12-11', '10:30:00', 'Cancelada', 'Consulta General', NULL, 'Cancelada por paciente');

-- Inserción de prescripciones
INSERT INTO prescripcion (id_cita, medicamento, dosis, duracion_dias) VALUES
-- Prescripciones de citas completadas
(8, 'Losartán', '50mg cada 24 horas', 30),
(9, 'Paracetamol', '500mg cada 8 horas', 5),
(9, 'Loratadina', '10mg cada 24 horas', 5),
(16, 'Enalapril', '10mg cada 24 horas', 30),
(16, 'Ácido Acetilsalicílico', '100mg cada 24 horas', 30);

-- Rutas públicas (sin rol)
INSERT INTO ruta (ruta, requiere_rol) VALUES 
('/', FALSE),
('/index', FALSE),
('/login', FALSE),
('/registro/**', FALSE),
('/registro/nuevo', FALSE),
('/registro/crearUsuario', FALSE),
('/registro/activacion/**', FALSE),
('/registro/activar', FALSE),      
('/registro/recordar', FALSE),     
('/registro/recordarUsuario', FALSE),
('/error/**', FALSE),
('/webjars/**', FALSE),
('/js/**', FALSE),
('/img/**', FALSE),
('/css/**', FALSE);

-- Rutas para PACIENTE/CLIENTE (id_rol = 2)
INSERT INTO ruta (ruta, id_rol, requiere_rol) VALUES 
('/paciente/inicio', 2, TRUE),
('/paciente/tratamientos', 2, TRUE),
('/paciente/perfil', 2, TRUE),
('/cita/mis-citas', 2, TRUE),
('/paciente/inicio/**', 2, TRUE),
('/paciente/perfil/**', 2, TRUE),
('/paciente/tratamientos/**', 2, TRUE),
('/paciente/citas/**', 2, TRUE),
('/paciente/guardar/**', 2, TRUE);

-- Rutas para ADMINISTRADOR (id_rol = 3)
INSERT INTO ruta (ruta, id_rol, requiere_rol) VALUES 
('/admin/**', 3, TRUE),
('/usuario/**', 3, TRUE),
('/rol/**', 3, TRUE),
('/ruta/**', 3, TRUE),
('/constante/**', 3, TRUE);

-- Rutas para MEDICO (id_rol = 1)
INSERT INTO ruta (ruta, id_rol, requiere_rol) VALUES 
('/medico/**', 1, TRUE),
('/paciente/ver/**', 1, TRUE),
('/medico/pacientes/**', 1, TRUE),
('/registro-clinico/**', 1, TRUE),
('/prescripcion/**', 1, TRUE);

-- Rutas para SECRETARIA (id_rol = 4)
INSERT INTO ruta (ruta, id_rol, requiere_rol) VALUES 
('/secretaria/**', 4, TRUE),
('/pacientes/**', 4, TRUE),
('/citas/**', 4, TRUE),
('/cita/**', 4, TRUE),
('/secretaria/citasRegistro/**', 4, TRUE);

-- Constantes del sistema
INSERT INTO constante (atributo, valor) VALUES 
('nombre_consultorio', 'Consultorio Doctor Cerdas'),
('telefono_contacto', '2315-2832');