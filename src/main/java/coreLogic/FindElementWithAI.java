package coreLogic;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import frameworkUtilities.CreateSession;
import screens.FindElementWithAIScreen;

public class FindElementWithAI extends CreateSession {

    FindElementWithAIScreen findElementWithAIScreen;

    public FindElementWithAI() {
        findElementWithAIScreen = new FindElementWithAIScreen(driver);
    }

    @Given("^Flipkart app is launched$")
    public void amazonAppIsOpen() throws Throwable {
       findElementWithAIScreen.waitForVisibility(findElementWithAIScreen.skipSignInButton);
        findElementWithAIScreen.findElement(findElementWithAIScreen.skipSignInButton).click();
    }

    @When("^cart element is found on app with AI$")
    public void cartElementIsFoundOnAppWithAI() throws Throwable {
        findElementWithAIScreen.waitForVisibility(findElementWithAIScreen.cartImageWithAI);
    }

    @Then("^tap on the cart element$")
    public void tapOnTheCartElement() throws Throwable {
        findElementWithAIScreen.findElement(findElementWithAIScreen.cartImageWithAI).click();
    }
}
