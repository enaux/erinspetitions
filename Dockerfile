FROM tomcat:latest
ADD target/erinspetitions.war /usr/local/tomcat/webapps/
EXPOSE 9090
CMD ["catalina.sh", "run"]