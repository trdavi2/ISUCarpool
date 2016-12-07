package com.it326.isucarpool;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.it326.isucarpool.model.User;

import java.io.ByteArrayOutputStream;

public class UserProfileActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    private User user;
    private AutoCompleteTextView firstName;
    private AutoCompleteTextView lastName;
    private AutoCompleteTextView email;
    private AutoCompleteTextView password;
    private AutoCompleteTextView confirmPassword;
    private AutoCompleteTextView emergencyContact;
    private RadioButton male;
    private RadioButton female;
    private AutoCompleteTextView address;
    private AutoCompleteTextView city;
    private AutoCompleteTextView state;
    private ImageView profilePicture;
    private Button getPicture;
    private Button updateProfile;
    private Uri profilePicUri;
    private FirebaseAuth fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fb = FirebaseAuth.getInstance();
        UserProfileFragment fragment = new UserProfileFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment);

        firstName = (AutoCompleteTextView) findViewById(R.id.firstName);
        lastName = (AutoCompleteTextView) findViewById(R.id.lastName);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        address = (AutoCompleteTextView) findViewById(R.id.address);
        city = (AutoCompleteTextView) findViewById(R.id.city);
        state = (AutoCompleteTextView) findViewById(R.id.state);
        emergencyContact = (AutoCompleteTextView) findViewById(R.id.emergency_contact);
        getPicture = (Button) findViewById(R.id.select_picture);
        updateProfile = (Button) findViewById(R.id.update_profile_button);
        profilePicture = (ImageView) findViewById(R.id.profile_picture);
        if(MainActivity.getProfilePic() != null){
            profilePicture.setImageBitmap(MainActivity.getProfilePic());
        }

        loadUserInformation();
        loadProfilePicture();

        getPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImageFromGallery(view);
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(view);
            }
        });
    }

    public void loadUserInformation() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                firstName.setText(user.getFirstName());
                lastName.setText(user.getLastName());
                address.setText(user.getAddress());
                city.setText(user.getCity());
                state.setText(user.getState());
                if(user.getEmergencyContactEmail() != null) emergencyContact.setText(user.getEmergencyContactEmail());
                final String gender = user.getGender();
                if(gender.equals("Male")) {
                    male.setChecked(true);
                    female.setChecked(false);
                }
                else{
                    male.setChecked(false);
                    female.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    public void loadProfilePicture() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profilePic");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String base64Image = (String) dataSnapshot.getValue();
                if(base64Image != null) {
                    byte[] imageAsBytes = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);
                    profilePicture.setImageBitmap(
                            BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
                    );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    public void loadImageFromGallery(View v) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                profilePicUri);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                profilePicture = (ImageView) findViewById(R.id.profile_picture);
                Bitmap b = BitmapFactory.decodeFile(imgDecodableString);
                //b = ThumbnailUtils.extractThumbnail(b, 144, 144);
                profilePicture.setImageBitmap(b);

            }
        } catch (Exception e) {
            Toast.makeText(this, "Error selecting image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile(View view)
    {
        if(fb == null)
        {
            Toast.makeText(UserProfileActivity.this, "Null FBAuth Reference", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView gender = (TextView) findViewById(R.id.gender);
        boolean cancel = false;
        View focusView = null;
        //Set Errors to null
        firstName.setError(null);
        lastName.setError(null);
        gender.setError(null);
        address.setError(null);
        city.setError(null);
        state.setError(null);
        emergencyContact.setError(null);

        final String firstNameText = firstName.getText().toString();
        final String lastNameText = lastName.getText().toString();
        final String addressText = address.getText().toString();
        final String cityText = city.getText().toString();
        final String stateText = state.getText().toString();
        final String emergencyContactText = emergencyContact.getText().toString();
        String genderText = "";

        //Check to make sure a gender is selected
        if(male.isChecked()) genderText = "Male";
        else if(female.isChecked()) genderText = "Female";
        else{ gender.setError("Please select a gender."); cancel = true; focusView = gender; }

        if(firstNameText.isEmpty())
        {
            firstName.setError("All fields must filled out.");
            focusView = firstName;
            cancel = true;
        }
        if(lastNameText.isEmpty())
        {
            lastName.setError("All fields must filled out.");
            focusView = lastName;
            cancel = true;
        }
        if(addressText.isEmpty())
        {
            address.setError("All fields must filled out.");
            focusView = address;
            cancel = true;
        }
        if(cityText.isEmpty())
        {
            city.setError("All fields must filled out.");
            focusView = city;
            cancel = true;
        }
        if(stateText.isEmpty())
        {
            state.setError("All fields must filled out.");
            focusView = state;
            cancel = true;
        }
        if(emergencyContactText.isEmpty())
        {
            emergencyContact.setError("All fields must filled out.");
            focusView = emergencyContact;
            cancel = true;
        }

        if(cancel)
        {
            focusView.requestFocus();
        }
        else
        {
            user.setFirstName(firstNameText);
            user.setLastName(lastNameText);
            user.setGender(genderText);
            user.setAddress(addressText);
            user.setCity(cityText);
            user.setState(stateText);
            user.setEmergencyContactEmail(emergencyContactText);
            if(profilePicture != null) {
                profilePicture.setDrawingCacheEnabled(true);
                profilePicture.buildDrawingCache();
                Bitmap bitmap = profilePicture.getDrawingCache();
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 144, 144);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://isucarpool-a55c8.appspot.com/");
                StorageReference locRef = storageRef.child("profile_pictures/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpeg");
                UploadTask uploadTask = locRef.putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                });
            }

            // we finally have our base64 string version of the image, save it.
            fb = FirebaseAuth.getInstance();
            FirebaseDatabase.getInstance().getReference("users").child(fb.getCurrentUser().getUid()).child("profile").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(UserProfileActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
