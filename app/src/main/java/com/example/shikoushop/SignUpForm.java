package com.example.shikoushop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUpForm extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputFirstName,InputSurName,InputPhoneNumber, InputPassword;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);

        CreateAccountButton = (Button) findViewById(R.id.register);
        InputFirstName = (EditText) findViewById(R.id.firstname);
        InputSurName = (EditText) findViewById(R.id.surname);
        InputPassword = (EditText)findViewById(R.id.password_reg);
        InputPhoneNumber = (EditText) findViewById(R.id.mobile_num);
        loadingbar =  new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String firstname = InputFirstName.getText().toString();
        String surname = InputSurName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(firstname)){
            Toast.makeText(this,"Please write your Firstname...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(surname)){
            Toast.makeText(this,"Please write your Surname...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please write your Phone Number...",Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your Password...",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingbar.setTitle("Create Account");
            loadingbar.setMessage("Please wait, While we are checking the credentials.");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidateonphoneNumber(firstname,surname,phone,password);
        }

    }

    private void ValidateonphoneNumber(final String name, final String surname, final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("user").child(phone).exists())){

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);
                    userdataMap.put("surname",surname);

                    RootRef.child("user").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpForm.this,"Your Account is Created",Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent mainactivity = new Intent(SignUpForm.this,MainActivity.class);
                                finish();
                                startActivity(mainactivity);
                            }
                            else {
                                loadingbar.dismiss();
                                Toast.makeText(SignUpForm.this,"Network Error: Please try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(SignUpForm.this,"This" + phone+"already exists",Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                    Toast.makeText(SignUpForm.this,"Please try again using another phone number",Toast.LENGTH_SHORT).show();
                    Intent mainactivity = new Intent(SignUpForm.this,MainActivity.class);
                    finish();
                    startActivity(mainactivity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
