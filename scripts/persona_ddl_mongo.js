// --- MongoDB User Creation and DDL Script ---
print(
  "Starting MongoDB initialization script: User creation and DDL for persona_db."
);

// Part 1: User Creation
print("Attempting to connect to 'admin' database for user management...");
// Get a handle to the admin database.
// The initial 'db' object depends on how mongosh was launched.
// db.getSiblingDB('admin') ensures we are targeting the admin database for user operations.
const adminDbForUserCreation = db.getSiblingDB("admin");
print(
  `Operating in context of '${adminDbForUserCreation.getName()}' for user creation (this should be 'admin').`
);

const userName = "persona_db";
const userPassword = "persona_db"; // In a real production scenario, this should not be hardcoded.
const targetDbForUser = "persona_db";

// Check if the user already exists in the admin database
const userDetails = adminDbForUserCreation.getUser(userName, {
  showPrivileges: false,
});

if (!userDetails) {
  try {
    adminDbForUserCreation.createUser({
      user: userName,
      pwd: userPassword,
      roles: [{ role: "readWrite", db: targetDbForUser }],
    });
    print(
      `User '${userName}' created successfully in '${adminDbForUserCreation.getName()}' database with readWrite access to '${targetDbForUser}'.`
    );
  } catch (e) {
    print(`An error occurred during user creation for '${userName}': ${e}`);
    // Depending on the error, you might want to stop the script.
    // For now, we'll print the error and let the script continue to the DDL part,
    // as the DDL part might still be useful if the user was created manually or in a previous run.
  }
} else {
  print(
    `User '${userName}' already exists in '${adminDbForUserCreation.getName()}' database. Skipping creation.`
  );
}

// Part 2: DDL operations for 'persona_db'
print("\nTransitioning to DDL operations for 'persona_db'.");
// IMPORTANT: Switch the 'db' variable to point to 'persona_db' for all subsequent DDL commands.
db = db.getSiblingDB(targetDbForUser);
print(
  `Switched current database context to: '${db.getName()}' for DDL operations (this should be '${targetDbForUser}').`
);

// Ensure we are on the correct database for DDL
if (db.getName() !== targetDbForUser) {
  const errorMsg = `Error: Failed to switch to '${targetDbForUser}' for DDL. Current database is '${db.getName()}'. Aborting DDL part.`;
  print(errorMsg);
  throw errorMsg; // Stop script execution if not on the correct database
}

// Drop collections if they exist to ensure a clean slate for DDL operations
// Important: This will delete any existing data in these collections.
print(
  "Dropping existing collections (persona, profesion, telefono, estudios) if they exist..."
);
db.persona.drop();
db.profesion.drop();
db.telefono.drop();
db.estudios.drop();
print("Finished dropping collections.");

// Create collections with validation
print("Creating collection: persona");
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

print("Creating collection: profesion");
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

print("Creating collection: telefono");
db.createCollection("telefono", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "oper", "duenio"],
      properties: {
        _id: { bsonType: "string" },
        oper: { bsonType: "string" },
        duenio: { bsonType: "int" }, // Represents the _id of a person
        _class: { bsonType: "string" },
      },
    },
  },
});

print("Creating collection: estudios");
db.createCollection("estudios", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "idProf", "ccPer"],
      properties: {
        _id: { bsonType: "string" }, // Consider if this should be ObjectId or a composite key structure
        idProf: { bsonType: "int" }, // Represents the _id of a profesion
        ccPer: { bsonType: "int" }, // Represents the _id of a person
        fecha: { bsonType: ["date", "null"] },
        univer: { bsonType: ["string", "null"] },
        _class: { bsonType: "string" },
      },
    },
  },
});

// Create indexes for references (similar to foreign keys)
print("Creating indexes for telefono collection...");
db.telefono.createIndex({ duenio: 1 });

print("Creating indexes for estudios collection...");
db.estudios.createIndex({ ccPer: 1 });
db.estudios.createIndex({ idProf: 1 });
db.estudios.createIndex({ ccPer: 1, idProf: 1 }, { unique: true });

print("DDL script finished: Collections and indexes created in persona_db.");
