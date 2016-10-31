package csci310.parkhere.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import csci310.parkhere.R;
import csci310.parkhere.controller.ClientController;

/**
 * Created by ivylinlaw on 10/29/16.
 */
public class RegisterProviderActivity extends Activity {
    Button _nextButton;
    EditText _liscenseIdText;
    String name, email, password, phonenum;
    ClientController clientController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_provider_ui);

        _nextButton=(Button)findViewById(R.id.nextButton);
        _liscenseIdText=(EditText)findViewById(R.id.liscenseIdText); // license ID

        Intent intent = getIntent();
        name = intent.getStringExtra("NAME");
        email = intent.getStringExtra("EMAIL");
        password = intent.getStringExtra("PASSWORD");
        phonenum = intent.getStringExtra("PHONE_NUM");

        clientController = ClientController.getInstance();

        _nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });
    }

    private void register(View v) {
        String licenseID = _liscenseIdText.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(RegisterProviderActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Registering...");
        progressDialog.show();

        // TODO: Implement your own authentication logic here.
        try {
            clientController.register(email, password, phonenum, licenseID, null, "provider", name);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final View curr_v = v;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    //                    private View v;
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onRegisterSuccess(curr_v);
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void onRegisterSuccess(View v) {
        Intent intent = new Intent(v.getContext(), RenterActivity.class);
        startActivityForResult(intent, 0);
    }
}
