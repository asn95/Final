package com.example.finall;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EnrollmentActivity extends AppCompatActivity {

    private ListView courseListView;
    private Button submitButton;
    private ArrayList<Course> courseList;
    private ArrayList<Course> selectedCourses;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int totalCredits = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        courseListView = findViewById(R.id.courseListView);
        submitButton = findViewById(R.id.submitButton);
        courseList = new ArrayList<>();
        selectedCourses = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Fetch courses from Firestore
        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            Long creditsLong = document.getLong("credits"); // Use getLong to retrieve integer values
                            int credits = creditsLong != null ? creditsLong.intValue() : 0;
                            Course course = new Course(name, credits);
                            courseList.add(course);
                        }
                        // Populate ListView
                        ArrayAdapter<Course> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, courseList);
                        courseListView.setAdapter(adapter);
                        courseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    } else {
                        Toast.makeText(this, "Error getting courses", Toast.LENGTH_SHORT).show();
                    }
                });

        submitButton.setOnClickListener(v -> {
            totalCredits = 0;
            selectedCourses.clear();

            // Get selected courses
            for (int i = 0; i < courseListView.getCount(); i++) {
                if (courseListView.isItemChecked(i)) {
                    Course selectedCourse = courseList.get(i);
                    totalCredits += selectedCourse.getCredits();
                    selectedCourses.add(selectedCourse);
                }
            }

            // Check credit limit
            if (totalCredits <= 24) {
                // Save selected courses to Firestore under the user's document
                saveSelectedCourses();
                Toast.makeText(this, "Enrollment successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Total credits cannot exceed 24", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSelectedCourses() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            // Add selected courses to user's enrolledCourses subcollection
            for (Course course : selectedCourses) {
                userRef.collection("enrolledCourses").add(course);
            }
        }
    }

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
