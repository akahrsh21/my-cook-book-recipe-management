### my-cook-book-recipe-management
***
This application enables us to Manage different Recipes created by users. 
The different APIs supported are:
1. Create a single Recipe Record
2. Create multiple Recipe Records at once
3. Search Recipes by the Author
4. Search all available Recipes
5. Update Recipes
6. Delete Recipes
***

### Built With
**Language:** Java

**Framework**: SpringBoot

**Database**: In-Memory H2 Database

***
### Setting up in Local
1. Clone the below repository
   
      ``git clone  https://github.com/akahrsh21/my-cook-book-recipe-management.git``
  

2. The application uses H2 in memory DataBase and it is configured in the file 
   
    ``src/main/resources/application.yml``
    
   
3. Run the application
   

4. The documentation of the endpoints is documented and can be found in the below location
    
   ``http://<localhost:9090>/swagger-ui.html
    ; swagger.json in the root directory``

   

      
5. Get the Authentication token by executing the 'Authenticate' API and use the Token for the consecutive requests.
   
   ``userName: admin ; password: password``

***

### Postman Collection
The Postman collection for testing the APIs is available in the root location
``RecipeManagement Collection.postman_collection.json``
