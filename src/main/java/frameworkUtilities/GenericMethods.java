package frameworkUtilities;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.*;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class GenericMethods {

    static AndroidDriver driver = null;
    public final int timeOut = 30;

    public GenericMethods(AndroidDriver driver) {
        if (driver != null) {
            this.driver = driver;
        }
    }

    /**
     * method verify whether element is present on screen
     *
     * @param targetElement element to be present
     * @return true if element is present else throws exception
     * @throws InterruptedException Thrown when a thread is waiting, sleeping,
     *                              or otherwise occupied, and the thread is interrupted, either before
     *                              or during the activity.
     */
    public Boolean isElementPresent(By targetElement) throws InterruptedException {
        Thread.sleep(2000);
        Boolean isPresent = driver.findElements(targetElement).size() > 0;
        return isPresent;
    }

    /**
     * method to hide keyboard
     */
    public void hideKeyboard() {
        ((AppiumDriver) driver).hideKeyboard();
    }


    /**
     * faster way to set a text in textfield.
     *
     * @param text text to be sent in text box
     */
 /*   public void setValue(By element, String text) {
        ((MobileElement) driver).findElement(element).setValue(text);
    }*/

    /**
     * method to go back by Android Native back click
     */
    public void back() {
        ((AndroidDriver) driver).pressKeyCode(AndroidKeyCode.BACK);
    }

    /**
     * method to wait for an element to be visible
     *
     * @param targetElement element to be visible
     * @return true if element is visible else throws TimeoutException
     */
    public boolean waitForVisibility(By targetElement) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeOut);
            wait.until(ExpectedConditions.visibilityOfElementLocated(targetElement));
            return true;
        } catch (TimeoutException e) {
            System.out.println("Element is not visible: " + targetElement);
            System.out.println();
            System.out.println(e.getMessage());
            throw new TimeoutException();

        }
    }

    public boolean waitForVisibility(By targetElement, int timeOut) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeOut);
            wait.until(ExpectedConditions.visibilityOfElementLocated(targetElement));
            return true;
        } catch (TimeoutException e) {
            System.out.println("Element is not visible: " + targetElement);
            System.out.println();
            System.out.println(e.getMessage());
            throw new TimeoutException();

        }
    }
    public boolean waitForElementToBeClickable(By targetElement) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeOut);
            wait.until(ExpectedConditions.elementToBeClickable(targetElement));
            return true;
        } catch (TimeoutException e) {
            System.out.println("Element is not visible: " + targetElement);
            System.out.println();
            System.out.println(e.getMessage());
            throw new TimeoutException();

        }
    }


    /**
     * method to wait for an element until it is invisible
     *
     * @param targetElement element to be invisible
     * @return true if element gets invisible else throws TimeoutException
     */
    public boolean waitForInvisibility(By targetElement) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeOut);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(targetElement));
            return true;
        } catch (TimeoutException e) {
            System.out.println("Element is still visible: " + targetElement);
            System.out.println();
            System.out.println(e.getMessage());
            throw new TimeoutException();

        }
    }

    /**
     * method to tap on the screen on provided coordinates
     *
     * @param xPosition x coordinate to be tapped
     * @param yPosition y coordinate to be tapped
     */
    public void tap(double xPosition, double yPosition) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        HashMap <String, Double> tapObject = new HashMap <String, Double>();
        tapObject.put("startX", xPosition);
        tapObject.put("startY", yPosition);
        js.executeScript("mobile: tap", tapObject);
    }


    /**
     * method to find an element
     *
     * @param locator element to be found
     * @return WebElement if found else throws NoSuchElementException
     */
    public WebElement findElement(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element;
        } catch (NoSuchElementException e) {
            Log.logError(this.getClass().getName(), "findElement", "Element not found" + locator);
            throw new NoSuchElementException(e.getMessage());
        }
    }


    /**
     * method to find all the elements of specific locator
     *
     * @param locator element to be found
     * @return return the list of elements if found else throws NoSuchElementException
     */
    public List <WebElement> findElements(By locator) {
        try {
            List <WebElement> element = driver.findElements(locator);
            return element;
        } catch (NoSuchElementException e) {
            Log.logError(this.getClass().getName(), "findElements", "element not found" + locator);
            throw new NoSuchElementException(e.getMessage());
        }
    }

    /**
     * method to get message test of alert
     *
     * @return message text which is displayed
     */
    public String getAlertText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            return alertText;
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException();
        }
    }

    /**
     * method to verify if alert is present
     *
     * @return returns true if alert is present else false
     */
    public boolean isAlertPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeOut);
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException();
        }
    }

    /**
     * method to Accept Alert if alert is present
     */

    public void acceptAlert() {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
    }

    /**
     * method to Dismiss Alert if alert is present
     */

    public void dismissAlert() {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().dismiss();
    }



    /**
     *  method to set network settings
     * @param airplaneMode pass true to activate airplane mode else false
     * @param wifi  pass true to activate wifi mode else false
     * @param data  pass true to activate data mode else false
     */
    /*public void setNetworkConnection(boolean  airplaneMode, boolean wifi, boolean data){
		NetworkConnectionSetting connection = new NetworkConnectionSetting(airplaneMode, wifi, data);
		((AndroidDriver) driver).setNetworkConnection(connection);
		System.out.println("Your current connection settings are :" + ((AndroidDriver) driver).getConnection());
	}
	 */

    /**
     * method to get all the context handles at particular screen
     */
    public void getContext() {
        ((AppiumDriver) driver).getContextHandles();
    }

    /**
     * method to set the context to required view.
     *
     * @param context view to be set
     */
    public void setContext(String context) {

        Set <String> contextNames = ((AppiumDriver) driver).getContextHandles();

        contextNames.forEach(contexts -> System.out.println(contexts));
        if (contextNames.contains(context)) {
            ((AppiumDriver) driver).context(context);
            Log.info("Context changed successfully");
        } else {
            Log.info(context + " not found on this page");
        }

        Log.info("Current context: " + ((AppiumDriver) driver).getContext());
    }


    /**
     * method to swipe on the screen on provided coordinates
     *
     * @param startX   - start X coordinate to be tapped
     * @param endX     - end X coordinate to be tapped
     * @param startY   - start y coordinate to be tapped
     * @param endY     - end Y coordinate to be tapped
     * @param duration duration to be tapped
     */

    public void swipe(double startX, double startY, double endX, double endY, double duration) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        HashMap <String, Double> swipeObject = new HashMap <String, Double>();
        // swipeObject.put("touchCount", 1.0);
        swipeObject.put("startX", startX);
        swipeObject.put("startY", startY);
        swipeObject.put("endX", endX);
        swipeObject.put("endY", endY);
        swipeObject.put("duration", duration);
        js.executeScript("mobile: swipe", swipeObject);
    }


    /**
     * Scroll forward to the element which has a description or name which contains the input text.
     * The scrolling is performed on the first scrollView present on the UI
     *
     * @param text text of the element text to be found
     */

    public void scrollTo(String text) {
        try {
         //   ( (AndroidDriver) driver.scrollTo(text));
            Log.info("Scroll Done for element: " + text);
        } catch (NoSuchElementException e) {
            Log.logError(this.getClass().getName(), "findElement", "Element not found with Scroll" + text);
            throw new NoSuchElementException(e.getMessage());
        }
    }


    /**
     * Scroll forward to the element which has a description or name which exactly matches the input text.
     * The scrolling is performed on the first scrollView present on the UI
     *
     * @param text - exact text of the element to be found
     */

    public void scrollToExact(String text) {
        try {
          //  ( (AndroidDriver) driver.scrollToExact(text);
            Log.info("Scroll To Exact Done: " + text);
        } catch (NoSuchElementException e) {
            Log.logError(this.getClass().getName(), "findElement", "Element not found with Scroll To Exact" + text);
            throw new NoSuchElementException(e.getMessage());
        }
    }

    static String UiScrollable(String uiSelector) {
        return "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(" + uiSelector + ".instance(0));";
    }

    /**
     * method to open notifications on Android
     */

    public void openNotifications() {
        ((AndroidDriver) driver).openNotifications();
    }

    /**
     * method to launchApp
     */

    public void launchApp() {
        ((AppiumDriver) driver).launchApp();
    }


    /**
     * method to click on Element By Name
     *
     * @param connection - String element to click
     */

    public void click(String connection) {
        ((AppiumDriver) driver).findElementByName(connection).click();
    }

    /**
     * method to Swipe Left on Element By Locator
     *
     * @param locator  - By locator
     * @param duration - Time to swipe
     */

    public void swipeLeft(By locator, int duration) {
        MobileElement element = (MobileElement) driver.findElement(locator);
        //element.swipe(SwipeElementDirection.LEFT, duration);

    }

    /**
     * method to Swipe UP on Element By Locator
     *
     * @param locator  - By locator
     * @param duration - Time to swipe
     */

    public void swipeUP(By locator, int duration) {
        MobileElement element = (MobileElement) driver.findElement(locator);
        //	element.swipe(SwipeElementDirection.UP, duration);

    }
    public void swipeDown(By locator, int duration) {
        MobileElement element = (MobileElement) driver.findElement(locator);
        //	element.swipe(SwipeElementDirection.Down, duration);

    }

    public void maximizeBrowser() {
        driver.manage().window().maximize();
    }

    /**
     * method to set Location
     */

    public void setLocation() {
        Location location = new Location(28.4396185, 77.0971527, 245.82867432);
        ((AppiumDriver) driver).setLocation(location);
    }


    public Set<String> getAllPresentContexts(){
        Set<String> contextNames =  driver.getContextHandles();
        return contextNames;
    }



}

