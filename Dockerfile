FROM centos

RUN mkdir /opt/tomcat/

WORKDIR /opt/tomcat
RUN curl -O https://downloads.apache.org/tomcat/tomcat-9/v9.0.56/bin/apache-tomcat-9.0.56.tar.gz
RUN tar -xvf apache-tomcat-9.0.56.tar.gz
RUN mv apache-tomcat-9.0.56/* /opt/tomcat/.
RUN yum -y install java
RUN java -version

# WORKDIR /opt/tomcat/webapps
# RUN curl -O -L https://github.com/AKSarav/SampleWebApp/raw/master/dist/SampleWebApp.war

EXPOSE 8080

CMD ["/opt/tomcat/bin/catalina.sh", "run"]