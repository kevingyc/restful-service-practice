# restful-service-practice

## Used tools:

* Vue.js for front-end 

* restful api @ jdk8

* gradle for build

* firebase for data store

## Before Use:

1. fill data root url in your firebase project in system.properties for access.
	for example: 
```
https://{project-name}.firebaseio.com/{node}.json?auth={auto-token}
```

2. build by gradle command
```
gradlew build
```
3. put built .war to tomcat 

## APIs: 

### list: 

```
http://{application-url}/services/v1?_wadl
```
When you want to call API, you need authenticated token which in the response after success login.

### base URL: 

```
http://{application-url}/services/v1/rest/
```

