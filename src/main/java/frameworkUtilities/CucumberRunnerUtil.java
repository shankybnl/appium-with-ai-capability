package frameworkUtilities;


import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import cucumber.api.testng.CucumberFeatureWrapper;
import cucumber.api.testng.TestNGCucumberRunner;
import org.apache.commons.configuration2.Configuration;
import org.junit.runner.RunWith;
import org.testng.annotations.*;

import java.lang.reflect.Method;


@RunWith(Cucumber.class)
@CucumberOptions(
        monochrome = true,
        features = "src/test/java/features",
      //  plugin = {"pretty", "html:target/cucumber-html-report"},
        glue = {"coreLogic"},
        tags = {"@ai"},
        format = {
                "pretty",
                "html:target/cucumber-reports/cucumber-pretty",
                "json:target/cucumber-reports/CucumberTestReport.json",
                "rerun:target/cucumber-reports/rerun.txt"}
)
public class CucumberRunnerUtil {

    private TestNGCucumberRunner testNGCucumberRunner;
    CreateSession createSession;
    public static Configuration loadTestData;
    public static String environment;

    @BeforeSuite(alwaysRun = true)
    public void setCreateSession() throws Exception {

        try {
           // create appium session
            createSession = new CreateSession("config");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }


    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        testNGCucumberRunner = new TestNGCucumberRunner(this.getClass());
    }


    @Parameters({"invokeDriver", "env", "level", "saucelabs","browserName"})
    @BeforeMethod(alwaysRun = true)
    public void driverObjectCreation(String invokeDriver, @Optional  String env, @Optional String level,
                                     boolean saucelabs, Method methodName,
                                     @Optional String browserName){
        try {
            createSession.createDriver(invokeDriver,env,saucelabs,methodName,"chrome");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(groups = "cucumber", description = "Runs Cucumber Feature",dataProvider = "features" )
    public void feature(CucumberFeatureWrapper cucumberFeature) {
        testNGCucumberRunner.runCucumber(cucumberFeature.getCucumberFeature());
    }




    @DataProvider
    public Object[][] features() {
        return testNGCucumberRunner.provideFeatures();
    }



    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        testNGCucumberRunner.finish();
    }


    @AfterSuite
    public void cleanUp(){
           }


}

