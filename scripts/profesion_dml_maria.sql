USE `persona_db`;

-- Sample Professions
INSERT INTO `profesion` (`id`, `nom`, `des`) VALUES
(1, 'Ingeniero de Sistemas', 'Profesional especializado en el diseño, desarrollo y mantenimiento de sistemas de software.'),
(2, 'Medico', 'Profesional de la salud encargado de diagnosticar y tratar enfermedades.'),
(3, 'Abogado', 'Profesional del derecho que asesora y representa a sus clientes en asuntos legales.'),
(4, 'Arquitecto', 'Profesional que diseña edificios y espacios urbanos.'),
(5, 'Contador Publico', 'Profesional encargado de la gestión y auditoría de la información financiera.')
ON DUPLICATE KEY UPDATE `nom`=VALUES(`nom`), `des`=VALUES(`des`);

COMMIT;
FLUSH PRIVILEGES; 