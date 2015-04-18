FROM pointslope/clojure:lein-2.5.0

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

COPY project.clj /usr/src/app/
RUN lein deps

EXPOSE 1234

COPY . /usr/src/app
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" \
       /usr/app-standalone.jar

# this step prevents unwanted code (e.g. in the dev directory)
# making it into the production application image.
RUN rm -rf /usr/src/app/*

CMD ["java", "-jar", "/usr/app-standalone.jar"]
