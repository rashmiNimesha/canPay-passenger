package com.example.canpay_passenger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProfileActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int GALLERY_REQUEST_CODE = 201;
    private static final int CAMERA_PERMISSION_CODE = 202;
    private static final int EDIT_NAME_REQUEST = 300;
    private static final int EDIT_PHONE_REQUEST = 301;

    private ImageView ivProfileImage, ivCameraIcon;
    private TextView tvUserName, tvUserPhone, tvUserNic;
    private LinearLayout llBankAccounts, llName, llPhone, llChangePin, llLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadUserData();
        setClickListeners();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        ivProfileImage = findViewById(R.id.iv_profile_image);
        ivCameraIcon = findViewById(R.id.iv_camera_icon);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserPhone = findViewById(R.id.tv_user_phone);
        tvUserNic = findViewById(R.id.tv_user_nic);
        llBankAccounts = findViewById(R.id.ll_bank_accounts);
        llName = findViewById(R.id.ll_name);
        llPhone = findViewById(R.id.ll_phone);
        llChangePin = findViewById(R.id.ll_change_pin);
        llLogout = findViewById(R.id.ll_logout);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Sehan Weerasekara");
        String userPhone = prefs.getString("user_phone", "+94 71 12 12 123");
        String userNic = prefs.getString("user_nic", "2000XXXXXXXX");

        tvUserName.setText(userName);
        tvUserPhone.setText(userPhone);
        tvUserNic.setText(userNic);
    }

    private void setClickListeners() {
        ivCameraIcon.setOnClickListener(v -> showImagePickerDialog());

        llBankAccounts.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, BankAccountListActivity.class);
            startActivity(intent);
        });

        llName.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditNameActivity.class);
            intent.putExtra("current_name", tvUserName.getText().toString());
            startActivityForResult(intent, EDIT_NAME_REQUEST);
        });

        llPhone.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditPhoneActivity.class);
            intent.putExtra("current_phone", tvUserPhone.getText().toString());
            startActivityForResult(intent, EDIT_PHONE_REQUEST);
        });

        llChangePin.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CurrentPinActivity.class);
            startActivity(intent);
        });

        llLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkCameraPermission();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    // NEW: Modern Bottom Sheet Logout Dialog
    private void showLogoutDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_logout, null);

        Button btnLogout = view.findViewById(R.id.btnLogout);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnLogout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            logout();
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ivProfileImage.setImageBitmap(bitmap);
                    break;

                case GALLERY_REQUEST_CODE:
                    Uri imageUri = data.getData();
                    ivProfileImage.setImageURI(imageUri);
                    break;

                case EDIT_NAME_REQUEST:
                    String newName = data.getStringExtra("new_name");
                    if (newName != null) {
                        tvUserName.setText(newName);
                        SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
                        prefs.edit().putString("user_name", newName).apply();
                    }
                    break;

                case EDIT_PHONE_REQUEST:
                    String newPhone = data.getStringExtra("new_phone");
                    if (newPhone != null) {
                        tvUserPhone.setText(newPhone);
                        SharedPreferences prefs2 = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
                        prefs2.edit().putString("user_phone", newPhone).apply();
                    }
                    break;
            }
        }
    }
}
