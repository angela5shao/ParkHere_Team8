package csci310.parkhere.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.support.test.espresso.intent.Intents;
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
import csci310.parkhere.ui.RegisterMainActivity;
import csci310.parkhere.ui.RegisterRenterActivity;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Checks.checkNotNull;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserRegistrationTest {

    public static final String NAME_TO_BE_TYPED = "Espresso East";
    public static final String EMAIL_TO_BE_TYPED = "espressoeast@usc.edu";
    public static final String NAME2_TO_BE_TYPED = "Espresso West";
    public static final String EMAIL2_TO_BE_TYPED = "espressowest@usc.edu";
    public static final String PASSWORD_SHORT_TO_BE_TYPED = "12345";
    public static final String PASSWORD_LONG_TO_BE_TYPED = "123456789012";
    public static final String PHONE_TO_BE_TYPED = "2132132133";
    public static final String LICENSE_TO_BE_TYPED = "0909090";
    public static final String LICENSE_PLATE_TO_BE_TYPED = "089089089";

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule(HomeActivity.class);
//    public IntentsTestRule<HomeActivity> mActivityRule = new IntentsTestRule(HomeActivity.class);

    @Before
    public void init() {
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    /*
    Tests that registration succeeds with unique email and long enough password.
     */
    @Test
    public void uniqueEmailLongPasswordRegistration() {
        // From HomeActivity, click on Login
        onView(ViewMatchers.withId(R.id.registerButton)).perform(click());

//        intended(toPackage("csci310.parkhere.RegisterMainActivity"));
//        intended(hasComponent(RegisterMainActivity.class.getName()));
//        assertTrue(getTargetContext() instanceof RegisterMainActivity.class);

//        intended(hasComponent(new ComponentName(getTargetContext(), RegisterMainActivity.class)));
        intended(hasComponent(RegisterMainActivity.class.getName()), times(1));

        // Type name, email, password, phone.
        onView(withId(R.id.nameText)).perform(typeText(NAME_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.emailText)).perform(typeText(EMAIL_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.passwordText)).perform(typeText(PASSWORD_LONG_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.phoneText)).perform(typeText(PHONE_TO_BE_TYPED), closeSoftKeyboard());

        // Select "Renter". Then press button.
        onView(withId(R.id.usertypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Renter"))).perform(click());
//        onView(withId(R.id.usertypeSpinner)).check(matches(withSpinnerText(containsString("Renter"))));

        onView(withId(R.id.nextButton)).perform(click());

        // Check that intent to Renter (same as Provider) Registration Activity is called.
//        intended(toPackage("csci310.parkhere.RegisterRenterActivity"));
        intended(hasComponent(RegisterRenterActivity.class.getName()), times(1));

//        // Type license ID
//        onView(withId(R.id.liscenseIdText)).perform(typeText(LICENSE_TO_BE_TYPED), closeSoftKeyboard());
//        onView(withId(R.id.liscensePlateNumText)).perform(typeText(LICENSE_PLATE_TO_BE_TYPED), closeSoftKeyboard());
//        onView(withId(R.id.nextButton)).perform(click());
//
//        // Check that intent to Renter Activity is called.
//        intended(toPackage("csci310.parkhere.RenterActivity"));
    }

    /*
    Tests that registration fails with unique email and password that's too short.
     */
    @Test
    public void uniqueEmailShortPasswordRegistration() {
        // From HomeActivity, click on Login
        onView(ViewMatchers.withId(R.id.registerButton)).perform(click());

        // Type name, email, password, phone.
        onView(withId(R.id.nameText)).perform(typeText(NAME2_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.emailText)).perform(typeText(EMAIL2_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.passwordText)).perform(typeText(PASSWORD_SHORT_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.phoneText)).perform(typeText(PHONE_TO_BE_TYPED), closeSoftKeyboard());

        // Select "Renter". Then press button.
        onView(withId(R.id.usertypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Renter"))).perform(click());
//        onView(withId(R.id.usertypeSpinner)).check(matches(withSpinnerText(containsString("Renter"))));

        onView(withId(R.id.nextButton)).perform(click());

        // Check that intent to Renter (same as Provider) Registration Activity is not called.
        intended(hasComponent(RegisterRenterActivity.class.getName()), times(0));
    }

    /*
    Tests that registration fails with duplicate email.
     */
    @Test
    public void duplicateRegistration() {
        // From HomeActivity, click on Login
        onView(ViewMatchers.withId(R.id.registerButton)).perform(click());

        // Type name, email, password, phone.
        onView(withId(R.id.nameText)).perform(typeText(NAME_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.emailText)).perform(typeText(EMAIL_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.passwordText)).perform(typeText(PASSWORD_LONG_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.phoneText)).perform(typeText(PHONE_TO_BE_TYPED), closeSoftKeyboard());

        // Select "Renter". Then press button.
        onView(withId(R.id.usertypeSpinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Renter"))).perform(click());
        onView(withId(R.id.usertypeSpinner)).check(matches(withSpinnerText(containsString("Renter"))));

        onView(withId(R.id.nextButton)).perform(click());

        // Check that intent to Renter (same as Provider) Registration Activity is not called.
        intended(hasComponent(RegisterRenterActivity.class.getName()), times(0));
    }
}