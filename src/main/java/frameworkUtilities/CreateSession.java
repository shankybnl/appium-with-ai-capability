package frameworkUtilities;


import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

/**
 * contains all the methods to create a new session and destroy the
 * session after the test(s) execution is over. Each test extends
 * this class.
 */
@Listeners({SauceOnDemandTestListener.class})
public class CreateSession implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

    public static AndroidDriver driver = null;
    Properties configFile;
    protected static Properties lobConfigProp = new Properties();
    protected static Properties localeConfigProp = new Properties();
    protected FileInputStream configFis, lobConfigFis, localeConfigFis;
    public Properties testDataFile;
    protected File file = new File("");
    static Properties configProp = new Properties();
    String OS;

    String sauceUsername;
    String sauceAcesskey;


  //  File fileToBeUploaded = new File("./App/ApiDemos.apk");
     File app = null;


     public CreateSession(){};

     public CreateSession(String propertyfile){
         try {
             propertiesFileLoad(propertyfile);
         } catch (Exception e) {
             e.printStackTrace();
         }
     }

    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();


    /**
     * this method starts Appium server. Calls startAppiumServer method to start the session depending upon your OS.
     * @throws Exception Unable to start appium server
     */
	/*@BeforeSuite(alwaysRun=true)
	public void invokeAppium() throws Exception{
		LanguageSelectionLogic l = new LanguageSelectionLogic(null);
		l.dutyOutAndDryRun();
	}*/
	/*
	{
		String OS = System.getProperty("os.name").toLowerCase();
		try{
			startAppiumServer(OS);
			Log.info("Appium server started successfully");
		}
		catch (Exception e) {
			Log.logError(getClass().getName(), "startAppium", "Unable to start appium server");
			throw new Exception(e.getMessage());
		}*/

    /**
     * this method stops Appium server.Calls stopAppiumServer method to
     * stop session depending upon your OS.
     *
     * @throws Exception Unable to stop appium server
     */
    //@AfterSuite(groups = "appiumStop")
    public void stopAppium() throws Exception {
        try {
            stopAppiumServer(OS);
            Log.info("Appium server stopped successfully");

        } catch (Exception e) {
            Log.logError(getClass().getName(), "stopAppium", "Unable to stop appium server");
            throw new Exception(e.getMessage());
        }
    }


    /**
     * this method creates the driver depending upon the passed parameter (android or iOS)
     * and loads the properties files (config and test data properties files).
     *
     * @param invokeDriver android or iOS
     * @param build        - to set up for different environments (staging,beta,production)
     * @param browserName  - for mobile web
     * @throws Exception issue while loading properties files or creation of driver.
     */
    public void createDriver(String invokeDriver, String build, boolean saucelabs, Method methodName,
                             @Optional String browserName) throws Exception {



        File propertiesFile = new File(file.getAbsoluteFile() + "//src//log4j.properties");
        PropertyConfigurator.configure(propertiesFile.toString());
        Log.info("--------------------------------------");

        // setting user name and access key for saucelabs
        sauceUsername = configProp.getProperty("sauceUsername");
        sauceAcesskey = configProp.getProperty("sauceAcessKey");

        if (saucelabs) {
            Log.info(methodName.getName() + " will run on saucelabs");
        }


        if (invokeDriver.equalsIgnoreCase("android")) {
            String buildPath = choosebuild(invokeDriver);

            driver = (AndroidDriver) androidDriver(buildPath, saucelabs, methodName, build);
            Log.info("Android driver created " + driver);

        } else if (invokeDriver.equalsIgnoreCase("iOS")) {
            String buildPath = choosebuild(invokeDriver);
            iOSDriver(buildPath, saucelabs, methodName, build);
            Log.info("iOS driver created");
        } else if (invokeDriver.equalsIgnoreCase("web")) {
            String buildPath = choosebuild(invokeDriver);
            webDriver(browserName, buildPath, saucelabs, methodName, build);
            Log.info("Web driver created");
        }

    }

    /**
     * this method quit the driver after the execution of test(s)
     *
     * @throws IOException
     */
    @AfterMethod(groups = "tearDown", alwaysRun = true)
    public void teardown(ITestResult result) throws Exception {
            getWebDriver().quit();
        }


    /**
     * this method creates the android driver
     *
     * @param buildPath - path to pick the location of the app
     * @return instance of android driver
     * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.
     */
    public WebDriver androidDriver(String buildPath, boolean saucelabs,
                                   Method methodName, String build) throws IOException, InterruptedException {


        DesiredCapabilities capabilities = new DesiredCapabilities();


        app = new File(buildPath);


        capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("app-package", "com.flipkart.android");
      //  capabilities.setCapability("app-wait-activity", "com.flipkart.android.activity.HomeFragmentHolderActivity");
        capabilities.setCapability("app-wait-activity", "com.flipkart.android.SplashActivity");
        capabilities.setCapability("autoGrantPermissions",true);
        //capabilities.setCapability("appiumVersion", "1.6");
        capabilities.setCapability("name", methodName.getName());
        capabilities.setCapability(MobileCapabilityType.NO_RESET, false);

        // This is required capability to use with test ai classifier plugin.
        capabilities.setCapability("automationName", "UiAutomator2");

         /*This capability determines what should be the lowest confidence to consider an element.
         By default, value is 0.2. This capability should be a number between 0 and 1,
         where 1 means confidence must be perfect, and 0 means no confidence at all is required.*/
        capabilities.setCapability("testaiConfidenceThreshold", 0.1);


        HashMap<String, String> customFindModules = new HashMap<>();
        customFindModules.put("ai", "test-ai-classifier");

        /* This directs Appium to include extra information about elements while they are being found,
         which dramatically speeds up the process of getting inputs to this plugin. */
        capabilities.setCapability("shouldUseCompactResponses", false);

        capabilities.setCapability("customFindModules", customFindModules);


        if (saucelabs) {
            if (build.equals("beta"))
                capabilities.setCapability("app", "sauce-storage:androidBeta.apk");
            else
                capabilities.setCapability("app", "sauce-storage:androidProd.apk");

            capabilities.setCapability("build", "Sanity_Pilot_app - " + build + "-" + getCurrentDate());
            capabilities.setCapability("browserName", "");
            capabilities.setCapability("public", "public restricted");
            capabilities.setCapability("testobject_api_key", "A582958785E34C45B2A9AA098D125DDF");
            capabilities.setCapability("testobject_device", "Motorola_Moto_E_2nd_gen_free");
            capabilities.setCapability("frameworkVersion", "1.6.4");
            capabilities.setCapability("testobject_suite_name", "Pilot_Stg_app_testing");
            capabilities.setCapability("testobject_test_name", "end-to-end-dry-run");
            capabilities.setCapability("testobject_suite_id", "7");


            webDriver.set(new AndroidDriver(
                    new URL("https://eu1.appium.testobject.com/wd/hub"), capabilities));

            /** "http://"+ sauceUsername + ":" + sauceAcesskey +"@ondemand.saucelabs.com:80/wd/hub"),
             *
             */
            sessionId.set(((AndroidDriver) getWebDriver()).getSessionId().toString());

        } else {

            capabilities.setCapability("app", app.getAbsolutePath());

            webDriver.set(new AndroidDriver(new URL("http://localhost:4723/wd/hub"), capabilities));
            // sessionId.set("2c6fa78461924993be28d2eee7cc1539");
        }
        return webDriver.get();
    }

    /**
     * this method creates the iOS driver
     *
     * @param buildPath- path to pick the location of the app
     * @return instance of iOS driver
     * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.
     */
    public WebDriver iOSDriver(String buildPath, boolean saucelabs, Method methodName, String build) throws MalformedURLException {
        File app = new File(buildPath);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "iOS");
        capabilities.setCapability("platformVersion", "8.3");
        //capabilities.setCapability("appiumVersion", "1.4");
        capabilities.setCapability("name", methodName.getName());
        capabilities.setCapability("udid", "192.168.4.151:5555");
        //capabilities.setCapability("autoAcceptAlerts", true);

        if (saucelabs) {
            if (build.equals("beta"))
                capabilities.setCapability("app", "sauce-storage:iosBeta.apk");
            else
                capabilities.setCapability("app", "sauce-storage:iosProd.zip");

            capabilities.setCapability("build", "Sanity_iOS - " + build + "-" + getCurrentDate());
            capabilities.setCapability("deviceName", "iPhone 6");
            capabilities.setCapability("browserName", "");
            capabilities.setCapability("public", "public restricted");

            webDriver.set(new IOSDriver(
                    new URL("http://" + sauceUsername + ":" + sauceAcesskey + "@ondemand.saucelabs.com:80/wd/hub"),
                    capabilities));

            sessionId.set(((IOSDriver) getWebDriver()).getSessionId().toString());


        } else {
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, "iPhone 5s");
            capabilities.setCapability("app", app.getAbsolutePath());
            webDriver.set(new IOSDriver(new URL("http://localhost:4723/wd/hub"), capabilities));
            sessionId.set("2c6fa78461924993be28d2eee7cc1539");
        }
        return webDriver.get();

    }


    /**
     * this method creates the web driver
     *
     * @param browserName - chrome,firefox
     * @param build       - staging,beta,production
     * @return instance of web driver
     * @throws MalformedURLException Thrown to indicate that a malformed URL has occurred.
     * @throws InterruptedException  Thrown when a thread is waiting, sleeping,
     *                               or otherwise occupied, and the thread is interrupted, either before
     *                               or during the activity.
     */

    public WebDriver webDriver(String browserName, String buildPath, boolean saucelabs, Method methodName, String build) throws MalformedURLException, InterruptedException {

        if (browserName.equals("Firefox")) {

            DesiredCapabilities capability = DesiredCapabilities.firefox();
            capability.setCapability("name", methodName.getName());
            capability.setCapability(CapabilityType.BROWSER_NAME, browserName);

            if (saucelabs) {
                capability.setCapability("build", "web_FirefoxSanity - " + getCurrentDate());
                webDriver.set(new RemoteWebDriver(
                        new URL("http://" + sauceUsername + ":" + sauceAcesskey + "@ondemand.saucelabs.com:80/wd/hub"),
                        capability));

                sessionId.set(((RemoteWebDriver) getWebDriver()).getSessionId().toString());
                webDriver.get().get(buildPath);
            } else {
                capability.setPlatform(Platform.WIN8_1);
                webDriver.set(new FirefoxDriver());
                webDriver.get().get(buildPath);
            }

        } else if (browserName.equals("Chrome")) {

            if (saucelabs) {
                DesiredCapabilities capability = DesiredCapabilities.chrome();
                capability.setCapability(CapabilityType.BROWSER_NAME, browserName);
                capability.setCapability("build", "web_ChromeSanity - " + getCurrentDate());
                capability.setCapability("name", methodName.getName());
                webDriver.set(new RemoteWebDriver(
                        new URL("http://" + sauceUsername + ":" + sauceAcesskey + "@ondemand.saucelabs.com:80/wd/hub"),
                        capability));

                sessionId.set(((RemoteWebDriver) getWebDriver()).getSessionId().toString());
                webDriver.get().get(buildPath);
            } else {

                String OS = System.getProperty("os.name");
                if (OS.contains("Windows")) {
                    System.setProperty("webdriver.chrome.driver", "libs\\chromedriver.exe");
                    DesiredCapabilities capability = DesiredCapabilities.chrome();
                    capability.setBrowserName("Chrome");
                    capability.setPlatform(Platform.WIN8_1);
                    webDriver.set(new ChromeDriver());
                    webDriver.get().get(buildPath);

                }
                if (OS.contains("Linux")) {
                    System.setProperty("webdriver.chrome.driver", "libs/chromedriver");
                    DesiredCapabilities capability = DesiredCapabilities.chrome();
                    capability.setBrowserName("Chrome");
                    capability.setPlatform(Platform.LINUX);
                    webDriver.set(new ChromeDriver());
                    webDriver.get().get(buildPath);

                }

                if (OS.contains("Mac OS X")) {

                    System.setProperty("webdriver.chrome.driver", "libs/chromedriver");
                    DesiredCapabilities capability = DesiredCapabilities.chrome();
                    capability.setBrowserName("Chrome");
                    capability.setPlatform(Platform.MAC);
                    webDriver.set(new ChromeDriver());
                    webDriver.get().get(buildPath);
                }

                sessionId.set("9d04e037d5bb4c838671f9fbf83c4517");
            }
        }

        return webDriver.get();

    }

    /**
     * this method starts the appium  server depending on your OS.
     *
     * @param os your machine OS (windows/linux/mac)
     * @throws IOException          Signals that an I/O exception of some sort has occurred
     * @throws ExecuteException     An exception indicating that the executing a subprocesses failed
     * @throws InterruptedException Thrown when a thread is waiting, sleeping,
     *                              or otherwise occupied, and the thread is interrupted, either before
     *                              or during the activity.
     */
    public void startAppiumServer(String os) throws ExecuteException, IOException, InterruptedException {
        if (os.contains("windows")) {
            CommandLine command = new CommandLine("cmd");
            command.addArgument("/c");
            command.addArgument("C:/Program Files/nodejs/node.exe");
            command.addArgument("C:/Appium/node_modules/appium/bin/appium.js");
            command.addArgument("--address", false);
            command.addArgument("127.0.0.1");
            command.addArgument("--port", false);
            command.addArgument("4723");
            command.addArgument("--full-reset", false);

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.execute(command, resultHandler);
            Thread.sleep(5000);
        } else if (os.contains("mac os x")) {
            CommandLine command = new CommandLine("/Applications/Appium.app/Contents/Resources/node/bin/node");
            command.addArgument("/Applications/Appium.app/Contents/Resources/node_modules/appium/bin/appium.js", false);
            command.addArgument("--address", false);
            command.addArgument("127.0.0.1");
            command.addArgument("--port", false);
            command.addArgument("4723");
            command.addArgument("--full-reset", false);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.execute(command, resultHandler);
            Thread.sleep(5000);
        } else if (os.contains("linux")) {
            //Runtime.getRuntime().exec("/bin/bash -c export ANDROID_HOME=$HOME/Android/sdk");
            //Start the appium server
            System.out.println("ANDROID_HOME : ");
            System.getenv("ANDROID_HOME");
            //	System.out.println("PATH :" +System.getenv("PATH"));
            CommandLine command = new CommandLine("/bin/bash");

            command.addArgument("-c");
            //		System.out.println("ANDROID_HOME : ");
            //		System.getenv("ANDROID_HOME");
            // /home/shanky/
            //command.addArgument("export");
            //command.addArgument("ANDROID_HOME=$HOME/Android/sdk");
            command.addArgument("~/.linuxbrew/bin/node");
            command.addArgument("~/.linuxbrew/lib/node_modules/appium/lib/appium.js", true);
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.execute(command, resultHandler);
            Thread.sleep(5000); //Wait for appium server to start

        } else {
            Log.info(os + "is not supported yet");
        }
    }

    /**
     * this method stops the appium  server.
     *
     * @param os your machine OS (windows/linux/mac).
     * @throws IOException      Signals that an I/O exception of some sort has occurred.
     * @throws ExecuteException An exception indicating that the executing a subprocesses failed.
     */
    public void stopAppiumServer(String os) throws ExecuteException, IOException {
        if (os.contains("windows")) {
            CommandLine command = new CommandLine("cmd");
            command.addArgument("/c");
            command.addArgument("Taskkill /F /IM node.exe");

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            executor.execute(command, resultHandler);
        } else if (os.contains("mac os x")) {
            String[] command = {"/usr/bin/killall", "-KILL", "node"};
            Runtime.getRuntime().exec(command);
            Log.info("Appium server stopped");
        } else if (os.contains("linux")) {
            // need to add it
        }
    }

    /**
     * this method loads properties files config and file having test data.
     *
     * @param filename name of the file to be loaded
     * @throws Exception property files are not loaded successfully
     */
    public void propertiesFileLoad(String filename) throws Exception {
        configFis = new FileInputStream(file.getAbsoluteFile()
                + "/src/main/resources/" + filename + ".properties");
        configProp.load(configFis);

    }

    public String choosebuild(String invokeDriver) {
        if (invokeDriver.equals("android")) {
            String appPath = file.getAbsoluteFile() + configProp.getProperty("AndroidAppPath");
            return appPath;
        }

        if (invokeDriver.equals("iOS")) {
            String appPath =file.getAbsoluteFile() +  configProp.getProperty("iOSAppPath");
            return appPath;
        }

        return "";
    }


    /**
     * Constructs a {@link com.saucelabs.common.SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication
     * supplied by environment variables or from an external file, use the no-arg {@link com.saucelabs.common.SauceOnDemandAuthentication} constructor.
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication("shankysharma",
            "5ddb76b4-f0d2-4ea6-878f-99d506e6098d");


    /**
     * @return the {@link WebDriver} for the current thread
     */
    public WebDriver getWebDriver() {
        System.out.println("WebDriver: " + webDriver.get());
        return webDriver.get();
    }

    /**
     * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key
     */
    public SauceOnDemandAuthentication getAuthentication() {
        return authentication;
    }


    /**
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    /**
     * @return the Current Date
     */

    public String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return (dateFormat.format(date));
    }

}

