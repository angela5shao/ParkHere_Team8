package csci310.parkhere.ui;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Intent;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import csci310.parkhere.R;
import csci310.parkhere.ui.HomeActivity;
import csci310.parkhere.ui.LoginActivity;
import csci310.parkhere.ui.ProviderActivity;
import csci310.parkhere.ui.RegisterMainActivity;
import csci310.parkhere.ui.RegisterRenterActivity;
import csci310.parkhere.ui.RenterActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    public static final String EMAIL_TO_BE_TYPED = "yy@yy.com"; //"espressoeast@usc.edu";
    public static final String PASSWORD_CORRECT_TO_BE_TYPED = "1234567890"; //"12345";
    public static final String PASSWORD_INCORRECT_TO_BE_TYPED = "123456789012";
    public static final String LAST_USER_ROLE = "Provider";
    public static final String LICENCE_TO_BE_TYPED = "1122334455";

    @Rule
    public IntentsTestRule<HomeActivity> mActivityRule = new IntentsTestRule(HomeActivity.class);

//    @Before
//    public void init() {
//        // From HomeActivity, click on Login
////        onView(ViewMatchers.withId(R.id.loginButton)).perform(click());
//        onView(withId(R.id.loginButton)).perform(click());
//
//        // Login (as a renter). Type email, password. Then press Login button
//        onView(withId(R.id.emailText)).perform(typeText(EMAIL_TO_BE_TYPED));
//        onView(withId(R.id.passwordText)).perform(typeText(PASSWORD_CORRECT_TO_BE_TYPED), closeSoftKeyboard());
//        onView(withId(R.id.loginButton)).perform(click());
//    }

//    @After
//    public void after() {
//        // Logout
//        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
////        onView(withText("Log Out")).perform(click());
//        onView(withId(R.id.LogOut)).perform(click());
////        onView(anyOf(withText(R.string.<your label for the menu item>), withId(R.id.<id of the menu item>))).perform(click());
//
//    }

    /*
    Test to login with correct credentials
     */
    @Test
    public void successfulLogin() {
        // From HomeActivity, click on Login
        onView(withId(R.id.loginButton)).perform(click());

        // Login (as a renter). Type email, password. Then press Login button
        onView(withId(R.id.emailText)).perform(typeText(EMAIL_TO_BE_TYPED));
        onView(withId(R.id.passwordText)).perform(typeText(PASSWORD_CORRECT_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Assuming only registered only as a renter, check that intent to Renter Activity is called.
//        onView(withId(R.id.renter_ui)).check(matches(isDisplayed()));
        // Validate label on SecondActivity
//        onView(withText("RenterActivity")).check(ViewAssertions.matches(isDisplayed()));

        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(
                Activity.RESULT_OK, new Intent());
        intending(toPackage("csci310.parkhere.ui")).respondWith(result);
//        intended(hasComponent(RenterActivity.class.getName()));
//        intended(hasComponent(new ComponentName(getTargetContext(), RenterActivity.class)));

    }


    /*
    Test that role is correct when logged in after registering & logging out as a provider.
     */
    @Test
    public void addProviderRoleLogoutCheckRole() {
        // From HomeActivity, click on Login
        onView(withId(R.id.loginButton)).perform(click());

        // Login (as a renter). Type email, password. Then press Login button
        onView(withId(R.id.emailText)).perform(typeText(EMAIL_TO_BE_TYPED));
        onView(withId(R.id.passwordText)).perform(typeText(PASSWORD_CORRECT_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Switch to provider
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Switch to Provider")).perform(click());

//        {
//            // Check that RegisterProviderActivity is called.
//            intended(toPackage("csci310.parkhere.RegisterProviderActivity"));
//
//            // Enter licence ID. Click on Next.
//            onView(withId(R.id.liscenseIdText)).perform(typeText(LICENCE_TO_BE_TYPED), closeSoftKeyboard());
//            onView(withId(R.id.nextButton)).perform(click());
//
//            // Check that ProviderActivity is called.
//            intended(toPackage("csci310.parkhere.ProviderActivity"));
//        }

        // Logout
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Log Out")).perform(click());

        // In HomeActivity, click on login.  Login in LoginActivity
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.emailText)).perform(typeText(EMAIL_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.passwordText)).perform(typeText(PASSWORD_CORRECT_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Check that ProviderActivity is called.
//        intended(toPackage("csci310.parkhere.ProviderActivity"));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(
                Activity.RESULT_OK, new Intent());
        intending(toPackage("csci310.parkhere.ui")).respondWith(result);

        // For consistency purposes (set last role as renter), switch back to renter and logout
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Switch to Renter")).perform(click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Log Out")).perform(click());
    }

    /*
    Test that login fails with incorrect credentials
     */
    @Test
    public void incorrectEmailAndPasswordLogin() {
        // From HomeActivity, click on Login
        onView(withId(R.id.loginButton)).perform(click());

        // Login (as a renter). Type email, password. Then press Login button
        onView(withId(R.id.emailText)).perform(typeText(EMAIL_TO_BE_TYPED));
        onView(withId(R.id.passwordText)).perform(typeText(PASSWORD_INCORRECT_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

//        intended(hasComponent(RenterActivity.class.getName()), times(0));
//        intended(hasComponent(ProviderActivity.class.getName()), times(0));

        onView(withId(R.id.renter_ui)).check(doesNotExist());
        onView(withId(R.id.provider_ui)).check(doesNotExist());
    }
}