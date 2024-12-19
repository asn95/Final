package com.example.finall;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomepageActivity extends AppCompatActivity {

    private Button enrollmentMenuButton, enrollmentSummaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        enrollmentMenuButton = findViewById(R.id.enrollmentMenuButton);
        enrollmentSummaryButton = findViewById(R.id.enrollmentSummaryButton);

        enrollmentMenuButton.setOnClickListener(v -> {
            startActivity(new Intent(HomepageActivity.this, EnrollmentActivity.class));
        });

        enrollmentSummaryButton.setOnClickListener(v -> {
            startActivity(new Intent(HomepageActivity.this, SummaryActivity.class));
        });
    }
}
