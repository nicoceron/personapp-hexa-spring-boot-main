// Create persona_db database
db = db.getSiblingDB("admin");
db.auth("root", "root");

db = db.getSiblingDB("persona_db");

// Create persona_db user
db.createUser({
  user: "persona_db",
  pwd: "persona_db",
  roles: [
    { role: "readWrite", db: "persona_db" },
    { role: "dbAdmin", db: "persona_db" },
  ],
});

// Create persona collection
db.createCollection("persona");

// Drop collections if they exist
db.persona.drop();
db.profesion.drop();
db.telefono.drop();
db.estudios.drop();

// Create collections with validation
db.createCollection("persona", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "nombre", "apellido", "genero"],
      properties: {
        _id: { bsonType: "int" },
        nombre: { bsonType: "string" },
        apellido: { bsonType: "string" },
        genero: { bsonType: "string", enum: ["M", "F"] },
        edad: { bsonType: ["int", "null"] },
        _class: { bsonType: "string" },
      },
    },
  },
});

db.createCollection("profesion", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "nombre"],
      properties: {
        _id: { bsonType: "int" },
        nombre: { bsonType: "string" },
        descripcion: { bsonType: ["string", "null"] },
        _class: { bsonType: "string" },
      },
    },
  },
});

db.createCollection("telefono", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "oper", "duenio"],
      properties: {
        _id: { bsonType: "string" },
        oper: { bsonType: "string" },
        duenio: { bsonType: "int" },
        _class: { bsonType: "string" },
      },
    },
  },
});

db.createCollection("estudios", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "idProf", "ccPer"],
      properties: {
        _id: { bsonType: "string" },
        idProf: { bsonType: "int" },
        ccPer: { bsonType: "int" },
        fecha: { bsonType: ["date", "null"] },
        univer: { bsonType: ["string", "null"] },
        _class: { bsonType: "string" },
      },
    },
  },
});

// Create indexes for references (similar to foreign keys)
db.telefono.createIndex({ duenio: 1 });
db.estudios.createIndex({ ccPer: 1 });
db.estudios.createIndex({ idProf: 1 });
db.estudios.createIndex({ ccPer: 1, idProf: 1 }, { unique: true });
