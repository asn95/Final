package com.example.finall;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    private ListView enrolledCoursesListView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ArrayList<Course> enrolledCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        enrolledCoursesListView = findViewById(R.id.enrolledCoursesListView);
        enrolledCourses = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Fetch the current user
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            // Fetch enrolled courses from the Firestore user's document
            db.collection("users").document(userId)
                    .collection("enrolledCourses")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                Long creditsLong = document.getLong("credits");
                                int credits = creditsLong != null ? creditsLong.intValue() : 0;
                                Course course = new Course(name, credits);
                                enrolledCourses.add(course);
                            }

                            // Display the enrolled courses in ListView
                            if (enrolledCourses.isEmpty()) {
                                Toast.makeText(this, "No courses enrolled", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, enrolledCourses);
                                enrolledCoursesListView.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(this, "Error fetching enrolled courses", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Course model class (same as in EnrollmentActivity)
    public static class Course {
        private String name;
        private int credits;

        public Course(String name, int credits) {
            this.name = name;
            this.credits = credits;
        }

        public String getName() {
            return name;
        }

        public int getCredits() {
            return credits;
        }

        @Override
        public String toString() {
            return name + " (" + credits + " credits)";
        }
    }
}
