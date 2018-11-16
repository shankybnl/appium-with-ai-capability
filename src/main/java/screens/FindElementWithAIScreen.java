package screens;

import frameworkUtilities.GenericMethods;
import io.appium.java_client.MobileBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class FindElementWithAIScreen extends GenericMethods {

    public FindElementWithAIScreen(AndroidDriver driver) {
        super(driver);
    }


    public By skipSignInButton = By.id("com.flipkart.android:id/btn_skip");
    public By cartImageWithAI = MobileBy.custom("ai:cart");
    public By notificationImageWithAI = MobileBy.custom("ai:notifications");
    public By searchImageWithAI = MobileBy.custom("ai:search");
}
