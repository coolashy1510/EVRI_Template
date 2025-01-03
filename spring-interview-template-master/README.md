# Tasks

## PUT endpoint
We would like you to implement a PUT endpoint, which will accept a json body and a courierId path parameter.
This endpoint should check that the courier exists and then update all details of an existing courier.
If the courier does not exist, a 404 response should be returned.

## GET only active couriers
The courier-service currently provides a GET endpoint for retrieving a list of all courier details.
We would like to enhance this endpoint, adding an optional boolean query param called 'isActive', which defaults to False.
If this parameter is True, the endpoint should only return couriers whom have an active flag of True.



