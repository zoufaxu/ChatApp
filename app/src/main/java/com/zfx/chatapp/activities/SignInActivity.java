package com.zfx.chatapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zfx.chatapp.databinding.ActivitySignInBinding;
import com.zfx.chatapp.utilities.Constants;
import com.zfx.chatapp.utilities.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SetListeners();
    }

    private void SetListeners(){
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext() , SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(v-> {
            if (isValidSignUpDetails()){
                SignIn();
            }
        });
    }

    private void SignIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task ->{
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID ,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME ,documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_IMAGE ,documentSnapshot.getString(Constants.KEY_IMAGE));

//                        Intent是用于在不同组件（如Activity、Service、BroadcastReceiver等）之间传递数据的基本机制。
//                        Intent可以被用于启动另外一个Activity或Service，或者发送广播。
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        loading(false);
                        showToast("无法登录");
                    }
                });


    }

    private void loading(Boolean isLoading){
        if(isLoading)
        {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private Boolean isValidSignUpDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("输入电子邮件");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("输入有效电子邮件");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("输入密码");
            return false;
        }
        return true;
    }

//    private void addDataToFirestore(){
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String , Object> data = new HashMap<>();
//        data.put("first_name" , "Chirag");
//        data.put("last_name" , "Kachhadiya");
//        database.collection("users")
//                .add(data)
//                .addOnSuccessListener(documentReference ->{
//                    Toast.makeText(getApplicationContext() , "Data Inserted", Toast.LENGTH_SHORT).show();
//                    })
//                .addOnFailureListener(exception ->{
//                    Toast.makeText(getApplicationContext() , exception.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
}




































