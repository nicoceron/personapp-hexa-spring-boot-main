#!/bin/bash

# Test MongoDB connectivity from Docker container
echo "Testing MongoDB connectivity from Docker container:"
docker exec -it personapp-mongodb mongosh --username root --password root --authenticationDatabase admin --eval "db.adminCommand('ping')"

# List databases in MongoDB
echo -e "\nListing databases in MongoDB:"
docker exec -it personapp-mongodb mongosh --username root --password root --authenticationDatabase admin --eval "show dbs"

# List collections in persona_db
echo -e "\nListing collections in persona_db:"
docker exec -it personapp-mongodb mongosh --username root --password root --authenticationDatabase admin persona_db --eval "show collections"

echo -e "\nTest completed!" 