
# Carparking

For setting up the application please follow the steps

1. Check out the project code.
2. Open the "mongo_startup.bat" file in the project and provide the location of your MongoDB for the variable "MONGO"
3. Open src/main/resources/application.properties and change the VM and port value of spring.data.mongodb.uri  
3. Execute the bat file to start MongoDB.
4. Start the application using the Maven command "mvn spring-boot:run".


REST Endpoints
----------------

**Get All Locations**:

Endpoint: http://localhost:8080/parkingmgmt/locations 

Response:

```	
{
	"locations": [
	  {
		"id": 1,
		"name": "SoftwareAG",
		"lattitude": 12.9856,
		"longitude": 80.2462,
		"slots": [],
		"total": 4,
		"available": 2,
		"layout":"LISTVIEW",
		"active": true
	},
	  {
		"id": 2,
		"name": "TidelPark",
		"lattitude": 12.987,
		"longitude": 80.2515,
		"slots": [],
		"total": 2,
		"available": 2,
		"layout":"GRIDVIEW",
		"active": true
	  }
	],
}
```

**Get Locations by Name**:

Endpoint: http://localhost:8080/parkingmgmt/locations/SoftwareAG

Response:

```
{
	"id": 1,
	"name": "SoftwareAG",
	"lattitude": 12.9856,
	"longitude": 80.2462,
	"slots": [
	  {
		"name": "s1",
		"status": "available",
		"ownerId": 1
	  },
	  {
		"name": "s2",
		"status": "available",
		"ownerId": 1
	  },
	  {
		"name": "s3",
		"status": "occupied",
		"ownerId": 1
	  },
	  {
		"name": "s4",
		"status": "occupied",
		"ownerId": 1
	  }
	],
	"total": 4,
	"available": 2,
	"layout":"LISTVIEW",
	"active": true
}
```

**Add Location**: POST - Method

Endpoint: http://localhost:8080/parkingmgmt/locations/{name}

Response:

```
{   "name": "SoftwareAG", 
	"lattitude":"12.9856",
	"longitude":"80.2462",
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
	"layout":"LISTVIEW",
	"available": 2
}
```

**Delete Location**: DELETE - Method

Endpoint: http://localhost:8080/parkingmgmt/locations/SoftwareAG

Response:

200 - Success

