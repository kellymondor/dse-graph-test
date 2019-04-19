# Instructions for performing a simple unit test against DSE Graph in Docker.
  
First, you need to download Docker and the DSE Docker Image.  See 
https://github.com/datastax/docker-images for more information.

Then 

  ```
  docker run -e DS_LICENSE=accept --name my-dse -d datastax/dse-server -g
  ```
         
To run the JUnit test use Maven as follows;

  ```
  mvn test
  ```