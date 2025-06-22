package com.example.canpay_passenger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import java.util.List;

public class QrScanActivity extends AppCompatActivity {

    private DecoratedBarcodeView barcodeView;
    private ImageButton btnBack, btnFlash, btnGallery;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        // Initialize views
        barcodeView = findViewById(R.id.barcode_scanner);
        btnBack = findViewById(R.id.btn_back);
        btnFlash = findViewById(R.id.btn_flash);
        btnGallery = findViewById(R.id.btn_gallery);

        // Set up barcode scanner
        barcodeView.decodeContinuous(callback);

        // Set up button click listeners
        btnBack.setOnClickListener(v -> finish());

        btnFlash.setOnClickListener(v -> toggleFlash());

        btnGallery.setOnClickListener(v -> {
            // Handle gallery button click
            Toast.makeText(this, "Gallery feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void toggleFlash() {
        if (isFlashOn) {
            barcodeView.setTorchOff();
            btnFlash.setImageResource(R.drawable.ic_flash_off);
            isFlashOn = false;
        } else {
            barcodeView.setTorchOn();
            btnFlash.setImageResource(R.drawable.ic_flash_on);
            isFlashOn = true;
        }
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                // QR Code scanned successfully - Navigate to EnterAmountActivity
                Intent intent = new Intent(QrScanActivity.this, EnterAmountActivity.class);
                intent.putExtra("QR_RESULT", result.getText());
                startActivity(intent);
                finish(); // Close the scanner screen
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Handle possible result points if needed
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
