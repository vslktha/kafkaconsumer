FROM airdock/oracle-jdk:1.8

# Set JAVA_HOME
ENV JAVA_HOME /srv/java/jdk

WORKDIR /usr/src/app

COPY . /usr/src/app
# Download and setup gradle server
RUN apt-get update && apt-get install -y unzip && apt-get install krb5-user -y && curl -OL -k https://services.gradle.org/distributions/gradle-3.3-bin.zip && unzip gradle-3.3-bin.zip -d /opt && rm gradle-3.3-bin.zip

ENV PATH=$PATH:/opt/gradle-3.3/bin

RUN gradle clean bootRepackage

ADD krb5.conf /etc/krb5.conf

EXPOSE 8080

ONBUILD RUN kinit -kt /secrets/kafka-service/nifi.keytab nifi@AMWATERNP.NET

ENTRYPOINT  ["java","-Djava.security.auth.login.config=/secrets/kafka-service/kafka-jaas.conf","-Djava.security.krb5.conf=/etc/krb5.conf","-Dsun.security.krb5.debug=true","-jar","/usr/src/app/build/libs/kafkaService.war"]

