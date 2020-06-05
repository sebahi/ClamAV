#############To build docker images############
docker build -f Dockerfile -t spring-boot-docker .


#############To build the containers###########
docker run -p 8080:8080 spring-boot-docker  //use -d to run in detached mode.

docker exec -it <containerId> sh  //will go inside the container.


#It will take about 10 min before all the containers goes run.

#You can test it using postman 
http://localhost:8080/scan
parameters are:
1. upload file
2. name

bootstrap.py has been replaced with bootstap.sh# ClamAV