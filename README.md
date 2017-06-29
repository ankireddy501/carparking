# carparking
For setting up the application please follow the steps

1. Checkout the project code.
2. Open the "mongo_startup.bat" file in the project and provide the location of your mongodb for the variable "MONGO"
3. Execute the bat file to start mongo db.
4. Start the application using the maven command "mvn spring-boot:run".

```json
{   "name": "SoftwareAG", 
	"lattitude":"",
	"longitude":"",
	"slots": [
		{
			"id":"1",
			 "name":"s1",
			 "status":"available",
			 "ownerId":"1"
		},
		{
			"id":"2",
			 "name":"s2",
			 "status":"available",
			 "ownerId":"1"
		}
	]
	,
	"total": 2, 
	"available": 2
}
```
