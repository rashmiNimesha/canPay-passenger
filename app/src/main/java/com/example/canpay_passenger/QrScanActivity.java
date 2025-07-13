package com.example.canpay_passenger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class QrScanActivity extends AppCompatActivity {
    private DecoratedBarcodeView barcodeView;
    private ImageButton btnBack, btnFlash, btnGallery;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            initializeScanner();
        }
    }

    private void initializeScanner() {
        barcodeView = findViewById(R.id.barcode_scanner);
        btnBack = findViewById(R.id.btn_back);
        btnFlash = findViewById(R.id.btn_flash);
        btnGallery = findViewById(R.id.btn_gallery);

        barcodeView.decodeContinuous(callback);

        btnBack.setOnClickListener(v -> finish());
        btnFlash.setOnClickListener(v -> toggleFlash());
        btnGallery.setOnClickListener(v -> Toast.makeText(this, "Gallery feature coming soon", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeScanner();
        } else {
            Toast.makeText(this, "Camera permission required to scan QR codes", Toast.LENGTH_LONG).show();
            finish();
        }
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

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                try {
                    JSONObject qrData = new JSONObject(result.getText());
                    String busId = qrData.getString("busId");
                    String operatorId = qrData.getString("operatorId");

                    Log.d("QrScanActivity", "Scanned QR: busId=" + busId + ", operatorId=" + operatorId);

                    Intent intent = new Intent(QrScanActivity.this, EnterAmountActivity.class);
                    intent.putExtra("busId", busId);
                    intent.putExtra("operatorId", operatorId);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    Log.e("QrScanActivity", "Invalid QR code format: " + e.getMessage());
                    Toast.makeText(QrScanActivity.this, "Invalid QR code format", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void possibleResultPoints(List<com.google.zxing.ResultPoint> resultPoints) {
            // Handle possible result points if needed
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeView != null) {
            barcodeView.resume();
        }
    }

    @Override
    protected void onPause() {
        if (barcodeView != null) {
            barcodeView.pause();
        }
        super.onPause();
    }
}