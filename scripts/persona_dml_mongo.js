// Switch to the persona_db database
db = db.getSiblingDB("persona_db");

db.persona.insertMany(
  [
    {
      _id: NumberInt(123456789),
      nombre: "Pepe",
      apellido: "Perez",
      genero: "M",
      edad: NumberInt(30),
      _class: "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument",
    },
    {
      _id: NumberInt(987654321),
      nombre: "Pepito",
      apellido: "Perez",
      genero: "M",
      _class: "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument",
    },
    {
      _id: NumberInt(321654987),
      nombre: "Pepa",
      apellido: "Juarez",
      genero: "F",
      edad: NumberInt(30),
      _class: "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument",
    },
    {
      _id: NumberInt(147258369),
      nombre: "Pepita",
      apellido: "Juarez",
      genero: "F",
      edad: NumberInt(10),
      _class: "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument",
    },
    {
      _id: NumberInt(963852741),
      nombre: "Fede",
      apellido: "Perez",
      genero: "M",
      edad: NumberInt(18),
      _class: "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument",
    },
    {
      _id: NumberInt(404040404),
      nombre: "Luis",
      apellido: "Gomez",
      genero: "M",
      edad: NumberInt(28),
      _class: "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument",
    },
    {
      _id: NumberInt(505050505),
      nombre: "Sofia",
      apellido: "Hernandez",
      genero: "F",
      edad: NumberInt(33),
      _class: "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument",
    },
    {
      _id: NumberInt(606060606),
      nombre: "David",
      apellido: "Ramirez",
      genero: "M",
      edad: NumberInt(22),
      _class: "co.edu.javeriana.as.personapp.mongo.document.PersonaDocument",
    },
  ],
  { ordered: false }
);

// Insert sample profesion data
db.profesion.insertMany(
  [
    {
      _id: NumberInt(1),
      nombre: "Ingeniero de Sistemas",
      descripcion: "Profesional en ingeniería de sistemas y computación",
      _class: "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument",
    },
    {
      _id: NumberInt(2),
      nombre: "Médico",
      descripcion: "Profesional en medicina",
      _class: "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument",
    },
  ],
  { ordered: false }
);

// Insert sample telefono data
db.telefono.insertMany(
  [
    {
      _id: "3101234567",
      oper: "Claro",
      duenio: NumberInt(123456789),
      _class: "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument",
    },
    {
      _id: "3219876543",
      oper: "Movistar",
      duenio: NumberInt(987654321),
      _class: "co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument",
    },
  ],
  { ordered: false }
);

// Insert sample estudios data
db.estudios.insertMany(
  [
    {
      _id: "123456789_1",
      idProf: NumberInt(1),
      ccPer: NumberInt(123456789),
      fecha: new Date("2020-01-15T00:00:00Z"),
      univer: "Universidad Javeriana",
      _class: "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument",
    },
    {
      _id: "987654321_2",
      idProf: NumberInt(2),
      ccPer: NumberInt(987654321),
      fecha: new Date("2019-06-20T00:00:00Z"),
      univer: "Universidad Nacional",
      _class: "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument",
    },
  ],
  { ordered: false }
);
