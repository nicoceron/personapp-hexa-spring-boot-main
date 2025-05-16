// Connect to the correct database and collection
// The database name is 'persona_db' as per docker-compose.yml environment variables for MongoDB
// The collection name will be 'profesion' (conventionally, Spring Data MongoDB uses the class name, lowercased)

db = db.getSiblingDB("persona_db");

// Drop the collection if it exists to ensure a clean slate, or use updateMany with upsert
// For simplicity in DML, we often drop and recreate if IDs are fixed.
// However, using update with upsert is safer if other data might exist.
// Let's use update with upsert to be more robust.

const professions = [
  {
    _id: 1,
    nombre: "Ingeniero de Sistemas",
    descripcion:
      "Profesional especializado en el diseño, desarrollo y mantenimiento de sistemas de software.",
  },
  {
    _id: 2,
    nombre: "Medico",
    descripcion:
      "Profesional de la salud encargado de diagnosticar y tratar enfermedades.",
  },
  {
    _id: 3,
    nombre: "Abogado",
    descripcion:
      "Profesional del derecho que asesora y representa a sus clientes en asuntos legales.",
  },
  {
    _id: 4,
    nombre: "Arquitecto",
    descripcion: "Profesional que diseña edificios y espacios urbanos.",
  },
  {
    _id: 5,
    nombre: "Contador Publico",
    descripcion:
      "Profesional encargado de la gestión y auditoría de la información financiera.",
  },
];

professions.forEach((profession) => {
  db.profesion.updateOne(
    { _id: profession._id }, // query
    { $set: profession }, // update
    { upsert: true } // options
  );
});

print(
  "Profesion DML script executed for MongoDB. " +
    db.profesion.countDocuments() +
    " documents in collection."
);
