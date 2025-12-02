FROM tomcat:latest
ADD target/erinspetitions.war /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]