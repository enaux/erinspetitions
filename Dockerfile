FROM tomcat:latest
ADD target/*.war /usr/local/tomcat/webapps/
EXPOSE 8080
EXPOSE 9090
CMD ["catalina.sh", "run"]