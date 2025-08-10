package com.example.canpay_passenger;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.canpay_passenger.config.HiveMqttManager;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private NotificationsAdapter adapter;
    private List<NotificationItem> notifications;
    private HiveMqttManager mqttManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.rv_notifications);
        emptyState = findViewById(R.id.ll_empty_state);

        // Load notifications (replace with real data/backend)
        notifications = loadNotifications();

        if (notifications.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NotificationsAdapter(notifications);
            recyclerView.setAdapter(adapter);
        }

        // Example: get passengerId from shared preferences or intent
        String passengerId = getSharedPreferences("user", MODE_PRIVATE).getString("passengerId", null);
        if (passengerId != null) {
            mqttManager = new HiveMqttManager(passengerId);
            mqttManager.connect(() -> {
                mqttManager.subscribe(this::onPaymentNotificationReceived);
            }, throwable -> Log.e("MQTT", "Failed to connect for notifications", throwable));
        }
    }

    private void onPaymentNotificationReceived(String payload) {
        try {
            JSONObject json = new JSONObject(payload);
            String busNumber = json.optString("busNumber", "Bus");
            double amount = json.optDouble("amount", 0);
            String status = json.optString("status", "");
            String message = json.optString("message", "Payment notification");
            String date = android.text.format.DateFormat.format("MMM dd, yyyy HH:mm", System.currentTimeMillis()).toString();

            String title = "Payment to " + busNumber + " successful: " + amount + " (" + status + ")";
            NotificationItem item = new NotificationItem(title, date, true);

            runOnUiThread(() -> {
                notifications.add(0, item);
                if (adapter == null) {
                    adapter = new NotificationsAdapter(notifications);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter.notifyItemInserted(0);
                }
                recyclerView.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            });
        } catch (Exception e) {
            Log.e("MQTT", "Failed to parse payment notification", e);
        }
    }

    // Replace this with your backend/API call
    private List<NotificationItem> loadNotifications() {
        List<NotificationItem> list = new ArrayList<>();
        // Uncomment to test with data:
        /*
        list.add(new NotificationItem("Payment Received", "Nov 18, 2024", R.drawable.ic_arrow_down, true));
        list.add(new NotificationItem("Login Successful", "Nov 18, 2024", R.drawable.ic_login, false));
        list.add(new NotificationItem("Assigned to ND-1234", "Nov 18, 2024", R.drawable.ic_bus, false));
        */
        return list;
    }
}
