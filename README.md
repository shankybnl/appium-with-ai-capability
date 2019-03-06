# appium-with-ai-capability
Boilerplate code for BDD (Behavior driven development) style mobile automation framework. It also includes example of finding element using AI capability [plugin](https://github.com/testdotai/appium-classifier-plugin)

#### Prerequisite before executing tests:

1. Install [test-ai-classifier](https://github.com/testdotai/appium-classifier-plugin) plugin on your system.
   Follow the steps present on their page.
  
2. Install appium version atleast 1.9.2-beta.2. Execute below command to install on linux (node should be installed)
   
   `npm install -g appium@1.9.2-beta.2 --unsafe-perm=true --allow-root `
3. I have configured it on mac (10.14.2) with below versions:
   node v11.4.0
   
#### BDD framework
Go through the [Selenium BDD framework](https://github.com/shankybnl/selenium_BDD_framework) readme file to know about the structure. It is designed on the similar lines keeping logic, elements and tests in different files.


#### Running a test:
$ mvn test


### How to leverage ai element finding capability in your existing framework?


1. Install [test-ai-classifier](https://github.com/testdotai/appium-classifier-plugin) plugin on your system.
2. Install appium version atleast 1.9.2-beta.2. Execute below command to install on linux (node should be installed).

   `npm install -g appium@1.9.2-beta.2 --unsafe-perm=true --allow-root `
   
3. Add the below dependency and repository in your pom.xml file

         <repositories>
            <repository>
                <id>jitpack.io</id>
                <url>https://jitpack.io</url>
            </repository>
        </repositories> 
        
        <dependency>
            <groupId>com.github.appium</groupId>
            <artifactId>java-client</artifactId>
            <version>969fac6a1c6dd229bdefb02ff10e71c812701e0c</version>
        </dependency> 
        
4. Below are the additional capabilities that should be present while creating driver:


        // This is required capability to use with test ai classifier plugin.
        
        capabilities.setCapability("automationName", "UiAutomator2");

         /*This capability determines what should be the lowest confidence to consider an element.
         By default, value is 0.2. This capability should be a number between 0 and 1,
         where 1 means confidence must be perfect, and 0 means no confidence at all is required.*/
         
        capabilities.setCapability("testaiConfidenceThreshold", 0.1);

        /* This directs Appium to include extra information about elements while they are being found,
         which dramatically speeds up the process of getting inputs to this plugin. */
        
        capabilities.setCapability("shouldUseCompactResponses", false);

        // passing reference of the plugin to appium
        HashMap<String, String> customFindModules = new HashMap<>();
        customFindModules.put("ai", "test-ai-classifier");
        
        capabilities.setCapability("customFindModules", customFindModules);


5. Find the elements with the below syntax:

        public By cartImageWithAI = MobileBy.custom("ai:cart");
        public By notificationImageWithAI = MobileBy.custom("ai:notifications");
        
6. Check test-ai-classifier/lib/labels.js file to get the list of predefined labels.

7. If you wish to add your labels which can be used for your app. Here's the [article](https://medium.com/testdotai/training-data-for-app-classifier-f217dc005523) to train the data for classifier plugin


