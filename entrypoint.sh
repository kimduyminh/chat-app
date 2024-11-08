#!/bin/bash

# Start SQL Server in the background
/opt/mssql/bin/sqlservr &

# Wait for SQL Server to fully start (max 90 seconds)
for i in {1..90}; do
    /opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'KimDuyMinh1234@' -Q "SELECT 1" &> /dev/null
    if [ $? -eq 0 ]; then
        echo "SQL Server is up - executing init script"
        break
    fi
    echo "Waiting for SQL Server to start..."
    sleep 1
done

# Execute the database initialization script
/opt/mssql-tools/bin/sqlcmd -S localhost -U sa -P 'KimDuyMinh1234@' -d master -i /docker-entrypoint-initdb.d/init-db.sql

# Keep the container running
tail -f /dev/null
