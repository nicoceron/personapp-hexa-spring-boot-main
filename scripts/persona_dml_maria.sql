-- Insert persona data
INSERT INTO 
	`persona_db`.`persona`(`cc`,`nombre`,`apellido`,`genero`,`edad`) 
VALUES
	(1,'Test','UserOne','M',21),
	(2,'Sample','UserTwo','F',22),
	(3,'Demo','UserThree','M',23),
	(123456789,'Pepe','Perez','M',30),
	(987654321,'Pepito','Perez','M',null),
	(321654987,'Pepa','Juarez','F',30),
	(147258369,'Pepita','Juarez','F',10),
	(963852741,'Fede','Perez','M',18),
	(101010101,'Maria','Garcia','F',25),
	(202020202,'Carlos','Lopez','M',42),
	(303030303,'Ana','Martinez','F',35);

-- Insert profesion data  
INSERT INTO 
	`persona_db`.`profesion`(`id`,`nom`,`des`) 
VALUES
	(1,'Ingeniero de Sistemas','Profesional en ingeniería de sistemas y computación'),
	(2,'Médico','Profesional en medicina');

-- Insert telefono data
INSERT INTO 
	`persona_db`.`telefono`(`num`,`oper`,`duenio`) 
VALUES
	('3101234567','Claro',123456789),
	('3219876543','Movistar',987654321);

-- Insert estudios data
INSERT INTO 
	`persona_db`.`estudios`(`id_prof`,`cc_per`,`fecha`,`univer`) 
VALUES
	(1,123456789,'2020-01-15','Universidad Javeriana'),
	(2,321654987,'2019-06-20','Universidad Nacional');