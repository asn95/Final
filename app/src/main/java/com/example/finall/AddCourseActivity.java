package com.example.finall;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCourseActivity extends AppCompatActivity {

    private EditText courseNameEditText, courseCreditsEditText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        courseNameEditText = findViewById(R.id.courseNameEditText);
        courseCreditsEditText = findViewById(R.id.courseCreditsEditText);
        db = FirebaseFirestore.getInstance();
    }

    // Fungsi untuk menambahkan kursus ke Firestore
    public void addCourse(View view) {
        // Mengambil nilai dari EditText
        String courseName = courseNameEditText.getText().toString().trim();
        String courseCreditsString = courseCreditsEditText.getText().toString().trim();

        // Validasi input
        if (courseName.isEmpty() || courseCreditsString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mengonversi kredit kursus ke angka
        int courseCredits = Integer.parseInt(courseCreditsString);

        // Menambahkan data kursus ke Firestore
        Course newCourse = new Course(courseName, courseCredits);
        db.collection("courses")
                .add(newCourse)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddCourseActivity.this, "Course added successfully", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddCourseActivity.this, "Error adding course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Fungsi untuk membersihkan input setelah kursus berhasil ditambahkan
    private void clearFields() {
        courseNameEditText.setText("");
        courseCreditsEditText.setText("");
    }
}
