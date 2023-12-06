# Illustration du principe d'inversion de contrôle

## Description

Ce projet est une annexe du [cours sur patrons de conception](https://mborne.github.io/cours-patron-conception/) qui illustre le concept d'injection de dépendance avec Spring.

## Problème

On veut pouvoir **automatiser et configurer la construction d'un ensemble d'objets présentant des dépendances** à l'échelle d'une application.

## Solution : Injection de dépendance

* Laisser le framework appeler les constructeurs et aux accesseurs qui établissent les relations entre les objets.
* Déléguer la construction des objets et la définition des relations à un framework

## Exemple (ce code)

On dispose d'un ensemble de classes :

![Diagramme de classe](uml/exemple.png)

On veut créer une application avec une pile de message (identifiée par "queue") partagée par :

* Un Thread de production de message ([Producer](src/main/java/org/acme/ioc/Producer.java)) qui **empile des messages à traiter** et s'arrête
* Un Thread de consommation ([Consumer](src/main/java/org/acme/ioc/Consumer.java)) des messages de la "queue" qui se contente d'**afficher ces messages**.
* Une IHM ([Window]([Consumer](src/main/java/org/acme/ioc/Window.java))) qui permet à l'utilisateur d'**ajouter manuellement des messages à la pile**.

Mieux : On veut démarrer les Thread au démarrage de l'application.

## Mise en oeuvre avec Spring

Un contexte spring [ApplicationContext](https://www.baeldung.com/spring-application-context) est initialisé à l'aide d'un fichier XML qui fait partie des ressources du programme :

```java
// Initialisation du contexte spring
@SuppressWarnings("resource")
ApplicationContext context = new ClassPathXmlApplicationContext("/spring/beans.xml");
```

Ce fichier XML [/src/main/resources/spring/beans.xml](src/main/resources/spring/beans.xml) est commenté. Il contient les informations nécessaires à la **construction des objets (inversion de contrôle)** et à la **définition des relations entre ces objets (injection de dépendances)**.

## Démonstration

* `mvn package exec:java`
* Suivre les logs dans la console
* Jouer avec l'interface

(Le main est dans la classe [Application](src/main/java/org/acme/ioc/Application.java))

## Comment ça marche?

L'initialisation des objets va s'appuyer massivement sur les concepts d'introspection et de réflexion supportés par le langage Java.

### Création de l'objet "queue"

```xml
<bean id="queue" class="java.util.concurrent.ArrayBlockingQueue">
	<constructor-arg index="0" type="int" value="5" />
</bean>
```
 
* Récupération de la classe `java.util.concurrent.ArrayBlockingQueue` par son nom
* Récupération du constructeur `ArrayBlockingQueue(int)` 
* Appel du constructeur avec un entier de valeur 5
* Stockage du bean dans une `beans: Map<String,Object>`

### Création d'un objet "consumer" de type "Consumer"

```xml
<bean id="consumer" class="org.acme.ioc.Consumer" 
	init-method="start" 
	destroy-method="interrupt"
>
	<property name="queue" ref="queue"></property>
</bean>
```

* Construction d'une instance de la classe `org.acme.ioc.Consumer`
* Passage d'une référence au bean "queue" par appel de la méthode `setQueue`
* Appel d'une méthode d'initialisation `start` (Consumer est un Thread)

Remarque : 

* Par défaut, le framework recherche un constructeur sans paramètre. 
* [Un bean doit normalement posséder un constructeur sans paramètre](http://www.jmdoudoux.fr/java/dej/chap-javabean.htm).
* `ApplicationContext` peut être manipulé par programmation, par exemple pour récupérer 
les beans par leur nom.

```java
Window window = (Window) context.getBean("window");
window.setVisible(true);
```

## Intérêt au niveau de l'application

* L'initialisation des objets partagés par plusieurs classes est simplifiée (moins de risque de "singletonite")
* Le **paramétrage de l'application est géré de manière cohérente** au niveau framework (voir [docs.spring.io - Externalized Configuration](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html)).
* L'ajout d'un paramètre est indolore (ex : ajout du support d'un proxy sortant dans un "httpClient")
* L'application peut être construite par assemblage de modules.

## Allez plus loin

* Comprendre la notion de [scope](http://www.tutorialspoint.com/spring/spring_bean_scopes.htm) (par défaut, spring initialise des singletons mais il est par possible d'initialiser des objets rattachés aux sessions des requêtes HTTP)
* Consulter la documentation de Spring : [Vue d'ensemble de spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/overview.html#overview-modules)


## Variantes d'implémentation de IoC

### Variante en Java avec des annotations (Spring moderne)

Les versions récentes de Spring s'appuie sur des [annotations et les fonctionnalités d'introspection associées](https://jenkov.com/tutorials/java-reflection/annotations.html) plutôt que sur des fichiers XML (voir [spring.io - Building an Application with Spring Boot](https://spring.io/guides/gs/spring-boot/) ). Cet exemple conserve volontairement la version XML qui a le mérite d'être explicite.


### Variante légère en Java avec ServiceLoader

L'API java intègre un [ServiceLoader](https://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html) qui permet de créer et récupérer des instances implémentant une interface.

Les instances à créer pour une interface `org.acme.Format` sont listées dans un fichier `main/resources/META-INF/services/org.acme.Format` :

```
org.acme.format.WKT
org.acme.format.WKB
```

Nous soulignerons que :

* Les classes doivent impérativement offrir un **constructeur par défaut**
* Ce principe est souvent utilisé pour la production de système de plugin (sans dépendance à un framework particulier)

Exemple : [Plugin pour la lecture des shapefile dans Geotools](https://github.com/geotools/geotools/blob/master/modules/plugin/shapefile/src/main/resources/META-INF/services/org.geotools.data.DataStoreFactorySpi).

* Attention à bien concaténer ces fichiers si vous formez un jar qui embarque un ensemble de dépendances!


## Variante en PHP

Le framework Symfony se repose massivement sur le concept d'injection de dépendances.

