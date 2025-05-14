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
  ],
  { ordered: false }
);

// Insert sample profesion data
db.profesion.insertMany(
  [
    {
      _id: NumberInt(1),
      nom: "Ingeniero de Sistemas",
      des: "Profesional en ingeniería de sistemas y computación",
      _class: "co.edu.javeriana.as.personapp.mongo.document.ProfesionDocument",
    },
    {
      _id: NumberInt(2),
      nom: "Médico",
      des: "Profesional en medicina",
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
      id_prof: NumberInt(1),
      cc_per: NumberInt(123456789),
      fecha: new Date("2020-01-15"),
      univer: "Universidad Javeriana",
      _class: "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument",
    },
    {
      id_prof: NumberInt(2),
      cc_per: NumberInt(321654987),
      fecha: new Date("2019-06-20"),
      univer: "Universidad Nacional",
      _class: "co.edu.javeriana.as.personapp.mongo.document.EstudiosDocument",
    },
  ],
  { ordered: false }
);
