# Gateway app

gets exchange rates from fixer api,
saves them into a database, provides api for clients to request latest rates
and history logs, uses redis for cache, uses rabbitmq to send the 
client request data and latest rates fetched from fixer



## note

has a powershell script to simulate clients sending requests