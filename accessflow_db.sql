-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 22-07-2026 a las 19:52:01
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `accessflow_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Admin`
--

CREATE TABLE `Admin` (
  `id` bigint(20) NOT NULL,
  `creado_en` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `Admin`
--

INSERT INTO `Admin` (`id`, `creado_en`, `email`, `nombre`, `password_hash`) VALUES
(1, '2026-07-20 21:51:32.000000', 'admin@escuela.edu', 'Dylan Oliver', '$2a$10$3X0pb1U8/lsXJD1tiOWJjux2aOCRIEKsJ8vhsFFbYLLh6YlbdBZQy');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Alumno`
--

CREATE TABLE `Alumno` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `rfid_uid` varchar(255) DEFAULT NULL,
  `grupo_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `Alumno`
--

INSERT INTO `Alumno` (`id`, `nombre`, `rfid_uid`, `grupo_id`) VALUES
(1, 'Arturo Salinas', NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `AlumnoTutor`
--

CREATE TABLE `AlumnoTutor` (
  `id` bigint(20) NOT NULL,
  `parentesco` varchar(255) DEFAULT NULL,
  `alumno_id` bigint(20) DEFAULT NULL,
  `tutor_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Asistencia`
--

CREATE TABLE `Asistencia` (
  `id` bigint(20) NOT NULL,
  `fecha_entrada` datetime(6) DEFAULT NULL,
  `fecha_salida` datetime(6) DEFAULT NULL,
  `alumno_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Configuracion`
--

CREATE TABLE `Configuracion` (
  `clave` varchar(255) NOT NULL,
  `valor` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Grupo`
--

CREATE TABLE `Grupo` (
  `id` bigint(20) NOT NULL,
  `grado` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `turno` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Mensaje`
--

CREATE TABLE `Mensaje` (
  `id` bigint(20) NOT NULL,
  `asunto` varchar(255) DEFAULT NULL,
  `cuerpo` text DEFAULT NULL,
  `enviado_en` datetime(6) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL,
  `admin_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `MensajeDestinatario`
--

CREATE TABLE `MensajeDestinatario` (
  `id` bigint(20) NOT NULL,
  `mensaje_id` bigint(20) DEFAULT NULL,
  `tutor_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `Tutor`
--

CREATE TABLE `Tutor` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `Admin`
--
ALTER TABLE `Admin`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKjl20d0ecx48g7qwy1dxe2akre` (`email`);

--
-- Indices de la tabla `Alumno`
--
ALTER TABLE `Alumno`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKoe3i3sk36cp7q45s5mxfokxck` (`rfid_uid`),
  ADD KEY `FKe9j4tcgsj8boxn00m0h7wdcgq` (`grupo_id`);

--
-- Indices de la tabla `AlumnoTutor`
--
ALTER TABLE `AlumnoTutor`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK4vipq603uvc65yla0j6u52w9i` (`alumno_id`),
  ADD KEY `FKjm40bd3q4byblc6817xlergww` (`tutor_id`);

--
-- Indices de la tabla `Asistencia`
--
ALTER TABLE `Asistencia`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKnq42e1ofgrph75me0ryq4xd3r` (`alumno_id`);

--
-- Indices de la tabla `Configuracion`
--
ALTER TABLE `Configuracion`
  ADD PRIMARY KEY (`clave`);

--
-- Indices de la tabla `Grupo`
--
ALTER TABLE `Grupo`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `Mensaje`
--
ALTER TABLE `Mensaje`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK3co7iuu9uwhslctdqdx11hbj3` (`admin_id`);

--
-- Indices de la tabla `MensajeDestinatario`
--
ALTER TABLE `MensajeDestinatario`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKsliwhuwp1xs6m0bdlb2mxypma` (`mensaje_id`),
  ADD KEY `FK6qlmawo2i4d5m9rf04oqhk8du` (`tutor_id`);

--
-- Indices de la tabla `Tutor`
--
ALTER TABLE `Tutor`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `Admin`
--
ALTER TABLE `Admin`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `Alumno`
--
ALTER TABLE `Alumno`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `AlumnoTutor`
--
ALTER TABLE `AlumnoTutor`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `Asistencia`
--
ALTER TABLE `Asistencia`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `Grupo`
--
ALTER TABLE `Grupo`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `Mensaje`
--
ALTER TABLE `Mensaje`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `MensajeDestinatario`
--
ALTER TABLE `MensajeDestinatario`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `Tutor`
--
ALTER TABLE `Tutor`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `Alumno`
--
ALTER TABLE `Alumno`
  ADD CONSTRAINT `FKe9j4tcgsj8boxn00m0h7wdcgq` FOREIGN KEY (`grupo_id`) REFERENCES `Grupo` (`id`);

--
-- Filtros para la tabla `AlumnoTutor`
--
ALTER TABLE `AlumnoTutor`
  ADD CONSTRAINT `FK4vipq603uvc65yla0j6u52w9i` FOREIGN KEY (`alumno_id`) REFERENCES `Alumno` (`id`),
  ADD CONSTRAINT `FKjm40bd3q4byblc6817xlergww` FOREIGN KEY (`tutor_id`) REFERENCES `Tutor` (`id`);

--
-- Filtros para la tabla `Asistencia`
--
ALTER TABLE `Asistencia`
  ADD CONSTRAINT `FKnq42e1ofgrph75me0ryq4xd3r` FOREIGN KEY (`alumno_id`) REFERENCES `Alumno` (`id`);

--
-- Filtros para la tabla `Mensaje`
--
ALTER TABLE `Mensaje`
  ADD CONSTRAINT `FK3co7iuu9uwhslctdqdx11hbj3` FOREIGN KEY (`admin_id`) REFERENCES `Admin` (`id`);

--
-- Filtros para la tabla `MensajeDestinatario`
--
ALTER TABLE `MensajeDestinatario`
  ADD CONSTRAINT `FK6qlmawo2i4d5m9rf04oqhk8du` FOREIGN KEY (`tutor_id`) REFERENCES `Tutor` (`id`),
  ADD CONSTRAINT `FKsliwhuwp1xs6m0bdlb2mxypma` FOREIGN KEY (`mensaje_id`) REFERENCES `Mensaje` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
