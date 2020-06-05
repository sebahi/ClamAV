FROM mkodockx/docker-clamav:alpine
RUN apk update
RUN apk upgrade

#############################################################
########## copy from openjdk:8-jre-alpine
#############################################################
# Default to UTF-8 file.encoding
ENV LANG C.UTF-8

# add a simple script that can auto-detect the appropriate JAVA_HOME value
# based on whether the JDK or only the JRE is installed
RUN { \
		echo '#!/bin/sh'; \
		echo 'set -e'; \
		echo; \
		echo 'dirname "$(dirname "$(readlink -f "$(which javac || which java)")")"'; \
	} > /usr/local/bin/docker-java-home \
	&& chmod +x /usr/local/bin/docker-java-home
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk/jre
ENV PATH $PATH:/usr/lib/jvm/java-1.8-openjdk/jre/bin:/usr/lib/jvm/java-1.8-openjdk/bin

#ENV JAVA_VERSION 8u171
#ENV JAVA_ALPINE_VERSION 8.242.08-r0

#RUN set -x \
#	&& apk add --no-cache \
#		openjdk8-jre="$JAVA_ALPINE_VERSION" \
#	&& [ "$JAVA_HOME" = "$(docker-java-home)" ]
RUN set -x \
	&& apk add --no-cache openjdk8-jre \
	&& [ "$JAVA_HOME" = "$(docker-java-home)" ]
#############################################################
ADD target/spring-boot-docker.jar app.jar
COPY ./freshclam.conf /etc/clamav/freshclam.conf
COPY ./clamd.conf /etc/clamav/clamd.conf
COPY ./docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh
ENTRYPOINT ["./bin/sh", "docker-entrypoint.sh"]
